/**
 * 
 */
package it.unical.logic_santos.toolkit.data_structure;

/**
 * @author Agostino
 *
 */
public abstract class OrientedGraph implements IGraph {

	protected int nodesCount;
	protected int arcsCount;
	
	public OrientedGraph() {
		this.nodesCount = 0;
		this.arcsCount = 0;
	}
	
	@Override
	public int nodesCount() {
		return nodesCount;
	}

	@Override
	public int arcsCount() {
		return arcsCount;
	}

	@Override
	public void clear() {
		nodesCount=0;
		arcsCount=0;
	}
	
}
