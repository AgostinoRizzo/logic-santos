/**
 * 
 */
package it.unical.logic_santos.roads_network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jme3.scene.Node;

import it.unical.logic_santos.collision.ICollisionDetectionEngine;
import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.toolkit.data_structure.AdjacencyListOrientedGraph;

/**
 * @author Agostino
 *
 */
public class RoadsNetwork extends AdjacencyListOrientedGraph {

	protected List< RoadNode > roadsNodes = new ArrayList< RoadNode >();
	protected List< RoadArc > roadsArcs =   new ArrayList< RoadArc >();
	protected ICollisionDetectionEngine collisionEngine=null;
	
	private Node graphicNode=null;
	private RoadNode previousNode;
	private boolean usePreviousNode = true;
	
	private boolean onEditorMode=true;
	
	public RoadsNetwork(ICollisionDetectionEngine collisionEngine, Node graphicNode, final boolean onEditorMode) {
		this.collisionEngine = collisionEngine;
		this.graphicNode = graphicNode;
		this.previousNode = null;
		this.onEditorMode = onEditorMode;
	}

	@Override
	public void clear() {
		super.clear();
		if ( onEditorMode )
			for( RoadNode n: roadsNodes )
				collisionEngine.removeCollidable(n.getPhysicalExtension().getBoundingVolume());
		roadsNodes.clear();
		this.previousNode = null;
		if ( onEditorMode )
			for( RoadArc e: roadsArcs )
				collisionEngine.removeCollidable(e.getPhysicalExtension().getBoundingVolume());
		roadsArcs.clear();
	}

	public void addNode(RoadNode newNode) {
		super.addNode();
		newNode.setId(super.nodesCount);
		if ( onEditorMode )
			collisionEngine.addCollidable(newNode.getAbstractPhysicalExtension().getBoundingVolume());
		roadsNodes.add(newNode);
		previousNode = roadsNodes.get(super.nodesCount-1);
	}
	
	public void addArc(RoadArc newArc) {
		super.setArc(newArc.getStartNode().getId(), newArc.getEndNode().getId(), true);
		previousNode = roadsNodes.get(super.nodesCount-1);
		if ( onEditorMode )
			collisionEngine.addCollidable(newArc.getAbstractPhysicalExtension().getBoundingVolume());
		roadsArcs.add(newArc);
	}

	@Override
	public boolean removeNode(int v) {
		return removeNode(roadsNodes.get(v));
		/*
		if ( super.removeNode(v) ) {
			collisionEngine.removeCollidable(roadsNodes.get(v).getAbstractPhysicalExtension().getBoundingVolume());
			roadsNodes.remove(v);
			previousNode = -1;

			for (Iterator<RoadArc> it = roadsArcs.iterator(); it.hasNext();) {
				RoadArc e = it.next();
				if ( (e.getStartNode().getId() == v) || (e.getEndNode().getId() == v)) {
					graphicNode.detachChild( ((ModelBasedPhysicalExtension) e.getAbstractPhysicalExtension()).getModelSpatial() );
					it.remove();
					return true;
				}
			}
				
			return true;
		}
		return false;
		*/
	}
	
	public boolean removeNode(RoadNode node) {
		final int nodeId = node.getId();
		
		for(int i=0; i<roadsNodes.size(); ++i) {
	
			if ( (roadsNodes.get(i).getId()==nodeId) && super.removeNode( nodeId )) {
				
				if ( onEditorMode )
					collisionEngine.removeCollidable(roadsNodes.get(i).getAbstractPhysicalExtension().getBoundingVolume());
				roadsNodes.remove(i);
				previousNode = null;
				
				for (Iterator<RoadArc> it = roadsArcs.iterator(); it.hasNext();) {
					RoadArc e = it.next();
					if ( (e.getStartNode().getId() == nodeId) || (e.getEndNode().getId() == nodeId)) {
						graphicNode.detachChild( ((ModelBasedPhysicalExtension) e.getAbstractPhysicalExtension()).getModelSpatial() );
						it.remove();
						it = roadsArcs.iterator();
					}
				}
				updateArcsNodeId( nodeId );
				return true;
			}
		}
		return false;
	}
	
	public boolean removeArc(RoadArc arc) {
		for(int i=0; i<roadsArcs.size(); ++i) {
			RoadArc v = roadsArcs.get(i);
			if ( v.equals(arc) ) {
				
				super.setArc(arc.getStartNode().getId(), arc.getEndNode().getId(), false);
				if ( onEditorMode ) {
					collisionEngine.removeCollidable(v.getAbstractPhysicalExtension().getBoundingVolume());
					graphicNode.detachChild( ((ModelBasedPhysicalExtension) v.getAbstractPhysicalExtension()).getModelSpatial() );
				}
				roadsArcs.remove(i);
				previousNode = null;
				
				//updateArcsNodeId();
				return true;
			}
		}
		return false;
	}
	
	public RoadNode getNode(final int v) {
		return roadsNodes.get(v-1);
	}
	
	public RoadNode getPreviousNode() {
		return previousNode;
	}
	
	public boolean hasPreviuosNodeSelected() {
		return (previousNode!=null);
	}
	
	public void deselectPreviousNode() {
		previousNode = null;
	}
	
	public void setUsePreviousNode(final boolean use) {
		usePreviousNode = use;
	}
	
	public boolean usePreviousNode() {
		return usePreviousNode;
	}
	
	public void makeNodesCollidable() {
		if ( onEditorMode )
			for(RoadNode v: roadsNodes)
				collisionEngine.addCollidable(v.getAbstractPhysicalExtension().getBoundingVolume());
	}
	
	public void makeNodesUnCollidable() {
		if ( onEditorMode )
			for(RoadNode v: roadsNodes)
				collisionEngine.removeCollidable(v.getAbstractPhysicalExtension().getBoundingVolume());
	}
	
	public void makeArcsCollidable() {
		if ( onEditorMode )
			for(RoadArc e: roadsArcs)
				collisionEngine.addCollidable(e.getAbstractPhysicalExtension().getBoundingVolume());
	}
	
	public void makeArcsUnCollidable() {
		if ( onEditorMode )
			for(RoadArc e: roadsArcs)
				collisionEngine.removeCollidable(e.getAbstractPhysicalExtension().getBoundingVolume());
	}
	
	private void updateArcsNodeId( final int removedNodeId ) {
		for(int i=0; i<roadsNodes.size(); ++i) {
			RoadNode node = roadsNodes.get(i);
			
			if ( node.getId()>removedNodeId )
				node.setId( node.getId()-1 );
		}	
			
		/*for(RoadArc v: roadsArcs) {
			RoadNode start = v.getStartNode();
			RoadNode end =   v.getEndNode();
			
			for(int i=0; i<roadsNodes.size(); ++i) {
				if (roadsNodes.get(i).equals(start))
					start.setId(roadsNodes.get(i).getId());
				else if (roadsNodes.get(i).equals(end))
					end.setId(roadsNodes.get(i).getId());
			}
		}*/
	}
	
	public void updateRoadArcsPositions() {
		
	}
	
	public RoadArc getRoadArc(final RoadNode v1, final RoadNode v2) {
		for(RoadArc e: roadsArcs)
			if ( e.getStartNode().equals(v1) && e.getEndNode().equals(v2) )
				return e;
		return null;
	}
	
	public List<RoadNode> getRoadsNodes() {
		return roadsNodes;
	}
	
	
	public List<RoadArc> getRoadsArcs() {
		return roadsArcs;
	}
}
