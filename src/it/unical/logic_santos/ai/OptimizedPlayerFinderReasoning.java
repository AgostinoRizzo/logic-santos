/**
 * 
 */
package it.unical.logic_santos.ai;


import java.util.List;
import java.util.Stack;

import it.unical.logic_santos.roads_network.RoadNode;
import it.unical.logic_santos.toolkit.data_structure.Arc;

/**
 * @author Agostino
 *
 */
public class OptimizedPlayerFinderReasoning extends PlayerFinderReasoning {

	private float minWeightPath=Float.MAX_VALUE;
	private float currentPathWeight=0.0f;
	private Stack< RoadNode > minPath=null;
	
	public OptimizedPlayerFinderReasoning( PolicemanAgent agent ) {
		super(agent);
	}
	
	@Override
	public void reasoning() {
		minWeightPath=Float.MAX_VALUE;
		currentPathWeight=0.0f;
		minPath=null;
		super.reasoning();
	}
	
	/** finds the path starting from startNode to node N such that:
	 * 		- N is a node a Player P is visible from
	 *  	- N is the NEAREST node to Player P
	 *  this method apply the backtracking and OPTIMIZATION technique
	 * @param startNode: the start node of the path
	 * @return the path starting from startNode which respect the property
	 */
	@Override
	protected boolean findPath( Stack< RoadNode > currentPath, boolean[] visitedNodes, final int max_deep ) {
		if ( currentPath.size()>=max_deep )
			return false;
		
		RoadNode currentNode = currentPath.peek(); float arcWeight;
		for( Arc e=pathsNetwork.firstAdjacent(currentNode.getId()); (!e.isNull()); e=pathsNetwork.nextAdjacent(e) ) {
			
			RoadNode nextNode = pathsNetwork.getNode( e.fin() );
			if ( canAdd( nextNode, currentPath, visitedNodes, max_deep ) ) {
				currentPath.push( nextNode );
				visitedNodes[ nextNode.getId()-1 ] = true;
				arcWeight = evaluateDistance( currentNode, nextNode );
				currentPathWeight += arcWeight;
				
				if ( isComplete( currentPath ) ) {
					
					if ( (minPath==null) || (currentPathWeight<minWeightPath) ) {
						minPath = clonePath( currentPath );
						minWeightPath = currentPathWeight;
					}
					
				} else if ( findPath(currentPath, visitedNodes, max_deep ) )
					return true;
				
				currentPath.pop();
				visitedNodes[ nextNode.getId()-1 ] = false;
				currentPathWeight -= arcWeight;
			}
		}
		return false;
	}
	
	/** returns the solution computed by the Finder
	 *  this method is blocking ( until the solution is available )
	 * @return the solution computed by the Finder
	 */
	@Override
	public List< RoadNode > getResult() {
		lock.lock();
		try {
			while( !isAvailable )
				solutionAvailable.await();
			path = minPath;
			return path;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} finally {
			lock.unlock();
		}
	}
	
	protected float evaluatePath( final Stack< RoadNode > path ) {
		if ( path.empty() || (path.size()==1) )
			return 0.0f;
		
		float weight = 0.0f;
		RoadNode precNode = path.get(0);
		final float n = path.size();
		for( int i=1; i<n; ++i ) {
			
			RoadNode currentNode = path.get(i);
			weight += precNode.getPosition().toVector3f().distance( currentNode.getPosition().toVector3f() );
			precNode = currentNode;
		}
		
		return weight;
	}
	
	protected float evaluateDistance( final RoadNode from, final RoadNode to ) {
		return from.getPosition().toVector3f().distance( to.getPosition().toVector3f() );
	}
	
	private Stack< RoadNode > clonePath( final Stack< RoadNode > path ) {
		Stack< RoadNode > copy = new Stack< RoadNode >();
		final float n = path.size();
		for( int i=0; i<n; ++i )
			copy.push( path.get(i) );
		return copy;
	}

}
