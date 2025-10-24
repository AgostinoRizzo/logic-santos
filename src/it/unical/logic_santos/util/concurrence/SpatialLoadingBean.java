/**
 * 
 */
package it.unical.logic_santos.util.concurrence;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * @author Agostino
 *
 */
public class SpatialLoadingBean {

	public Spatial spatial=null;
	public Node node = null;
	
	public SpatialLoadingBean(Spatial spatial, Node node) {
		this.spatial = spatial;
		this.node = node;
	}

	public void attachToNode() {
		node.attachChild(spatial);
	}
}
