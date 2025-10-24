/**
 * 
 */
package it.unical.logic_santos.roads_network;

import com.jme3.scene.Node;

import it.unical.logic_santos.collision.ICollisionDetectionEngine;
import it.unical.logic_santos.toolkit.data_structure.Arc;

/**
 * @author Agostino
 *
 */
public class PathsNetwork extends RoadsNetwork {

	public PathsNetwork(ICollisionDetectionEngine collisionEngine, Node graphicNode, final boolean onEditorMode) {
		super(collisionEngine, graphicNode, onEditorMode);
	}

	@Override
	public void addArc(RoadArc newArc) {
		super.addArc(newArc);
		super.setArc( newArc.getEndNode().getId(), newArc.getStartNode().getId(), true );
	}
}
