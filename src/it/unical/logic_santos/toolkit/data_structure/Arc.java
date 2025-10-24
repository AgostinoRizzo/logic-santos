/**
 * 
 */
package it.unical.logic_santos.toolkit.data_structure;

/**
 * @author Agostino
 *
 */
public class Arc {

	private IGraph graph=null;
	private int i;
	private int j;
	
	public Arc() {
		this.graph = null;
		this.i = 1;
		this.j = 1;
	}
	
	public Arc(final IGraph graph) {
		this.graph = graph;
		this.i = 1;
		this.j = 1;
	}
	
	public IGraph getGraph() {
		return graph;
	}
	
	public boolean isNull() {
		return (graph == null);
	}
	
	public int in() {
		return i;
	}
	
	public int fin() {
		return j;
	}
	
	public void setIn(final int i) {
		this.i = i;
	}
	
	public void setFin(final int j) {
		this.j = j;
	}
	
	public void setGraph(final IGraph graph) {
		this.graph = graph;
	}
	
	public static int in(final Arc e) {
		return e.i;
	}
	
	public static int fin(final Arc e) {
		return e.j;
	}
}
