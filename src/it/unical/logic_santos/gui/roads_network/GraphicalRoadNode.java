/**
 * 
 */
package it.unical.logic_santos.gui.roads_network;

import com.jme3.scene.Spatial;

import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class GraphicalRoadNode {

	public static final float Y_ADJUSTMENT = 3.0f;
	
	private Spatial spatialNode=null;
	
	
	public GraphicalRoadNode(final Vector3D nodePosition) {
		
	}
	
	public Spatial getSpatialNode() {
		return spatialNode;
	}
}
