/**
 * 
 */
package it.unical.logic_santos.gui.application;

import com.jme3.scene.Node;

/**
 * @author Agostino
 *
 */
public class GraphicalRootNode {

	private static GraphicalRootNode instance = null;
	
	private Node rootNode = null;
	
	private GraphicalRootNode() {
		
	}
	
	public static GraphicalRootNode getInstance() {
		if (instance == null)
			instance = new GraphicalRootNode();
		return instance;
	}
	
	public void init(Node node) {
		getInstance().rootNode = node;
	}
	
	public Node getRootNode() {
		return rootNode;
	}
	
}
