/**
 * 
 */
package it.unical.logic_santos.toolkit.data_structure;

/**
 * @author Agostino
 *
 */
public interface IGraph {

	public void setArc(final int i, final int j, final boolean status);
	public boolean getArc(final int i, final int j);
	
	public int nodesCount();
	public int arcsCount();
	
	public void clear();
	
	public void addNode();
	public boolean removeNode(final int v);
	
	public Arc firstAdjacent(final int i);
	public Arc nextAdjacent(final Arc e);
	
}
