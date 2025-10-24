/**
 * 
 */
package it.unical.logic_santos.toolkit.data_structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Agostino
 *
 */
public class AdjacencyListOrientedGraph extends OrientedGraph {

	protected List< Set<Integer> > adjacents=null;
	
	
	public AdjacencyListOrientedGraph() {
		this.adjacents = new ArrayList< Set<Integer> >();
	}
	
	public AdjacencyListOrientedGraph(final int nodesCount) {
		this.adjacents = new ArrayList< Set<Integer> >(nodesCount);
		this.nodesCount = nodesCount;
		for(int i=0; i<this.nodesCount; ++i)
			this.adjacents.add(new TreeSet<Integer>());
	}
	
	@Override
	public void setArc(int i, int j, boolean status) {
		final boolean existsArc = adjacents.get(i-1).contains(j-1);
		if ( ((!existsArc) && status) || (existsArc && (!status)) ) {
			
			modifyArc(i, j, status);
			if (status) 
				arcsCount++;
			else
				arcsCount--;
			
		}
	}

	@Override
	public boolean getArc(int i, int j) {
		return adjacents.get(i-1).contains(j-1);
	}

	@Override
	public void clear() {
		super.clear();
		for (Iterator< Set<Integer> > iterator = adjacents.iterator(); iterator.hasNext();) {
			Set<Integer> set = (Set<Integer>) iterator.next();
			set.clear();
		}
		adjacents.clear();
	}

	@Override
	public Arc firstAdjacent(int i) {
		Arc et = new Arc();
		if (!adjacents.get(i-1).isEmpty()) {
			et.setIn(i);
			et.setFin( ((TreeSet<Integer>) adjacents.get(i-1)).first()+1 );
			et.setGraph(this);
		}
		return et;
	}

	@Override
	public Arc nextAdjacent(Arc e) {
		Arc et = new Arc();
		final Integer fin = ((TreeSet<Integer>) adjacents.get(e.in()-1)).higher(e.fin()-1);
		if (fin != null) {
			et.setIn(e.in());
			et.setFin(fin+1);
			et.setGraph(this);
		}
		return et;
	}
	
	private void modifyArc(final int i, final int j, final boolean status) {
		if (status)
			adjacents.get(i-1).add(j-1);
		else
			adjacents.get(i-1).remove(j-1);
	}

	@Override
	public void addNode() {
		adjacents.add(new TreeSet<Integer>());
		nodesCount++;
	}

	@Override
	public boolean removeNode(final int v) {
		if ( (v<1) || (v>nodesCount) )
			return false;
		adjacents.remove(v-1);
		for (Iterator<Set<Integer>> iterator = adjacents.iterator(); iterator.hasNext();) {
			Set<Integer> set = (Set<Integer>) iterator.next();
			set.remove(v-1);
			
			Set<Integer> removedIntegers = new TreeSet<Integer>();
			for (Iterator<Integer> iteratorSet = set.iterator(); iteratorSet.hasNext();) {
				
				Integer integer = (Integer) iteratorSet.next();
				if (integer.intValue() >= v) {
					removedIntegers.add(integer);
					iteratorSet.remove();
				}

			}
			
			for(Integer integer: removedIntegers) {
				set.add(integer.intValue()-1);
			}
		}
		nodesCount--;
		return true;
	}

	@Override
	public String toString() {
		String ans = new String();
		int j;
		for(int i=1; i<=nodesCount; ++i) {
			for(j=1; j<=nodesCount; ++j)
				if (getArc(i, j))
					ans += "1 ";
				else
					ans += "0 ";
			ans += "\n";
		}
		return ans;
	}
	
	public static void main(String[] args) {
		
		IGraph G = new AdjacencyListOrientedGraph(3);
		
		G.setArc(1, 2, true);
		G.setArc(2, 3, true);
		
		G.addNode();
		G.addNode();
		
		G.setArc(3, 4, true);
		G.setArc(4, 2, true);
		G.setArc(4, 5, true);
		
		//G.removeNode(2);
		
		System.out.println(G.toString());
	}

}
