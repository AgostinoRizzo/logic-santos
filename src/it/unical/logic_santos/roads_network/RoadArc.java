/**
 * 
 */
package it.unical.logic_santos.roads_network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import it.unical.logic_santos.design.modeling.SimplePhysicalExtensionGenerator;
import it.unical.logic_santos.spatial_entity.AbstractStaticSpatialEntity;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class RoadArc extends AbstractStaticSpatialEntity {

	protected RoadNode startNode=null;
	protected RoadNode endNode=null;
	protected boolean loadPhysicalExtension=true;
	
	public RoadArc() {
		super(SimplePhysicalExtensionGenerator.generateNewRoadArcPhysicalExtension(Vector3D.ZERO, Vector3D.ZERO, false, true));
		this.getAbstractPhysicalExtension().setSpatialEntityOwner(this);
	}
	
	public RoadArc(final RoadNode startNode, final RoadNode endNode, final boolean decoreToPathNode, final boolean loadPhysicalExtension) {
		super(startNode.getPosition(), SimplePhysicalExtensionGenerator.generateNewRoadArcPhysicalExtension(startNode.getPosition(), endNode.getPosition(), decoreToPathNode, loadPhysicalExtension));
		this.startNode = startNode;
		this.endNode = endNode;
		this.loadPhysicalExtension = loadPhysicalExtension;
		if ( this.loadPhysicalExtension )
			this.getAbstractPhysicalExtension().setSpatialEntityOwner(this);
	}
	
	public RoadNode getStartNode() {
		return startNode;
	}
	
	public RoadNode getEndNode() {
		return endNode;
	}
	
	public void setStartPosition(final RoadNode startNode) {
		this.startNode = startNode;
	}
	
	public void setEndPosition(final RoadNode endNode) {
		this.endNode = endNode;
	}


	@Override
	public ISpatialEntity cloneSpatialEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void readFromDataInputStream(DataInputStream in, RoadsNetwork roadsNetwork) throws IOException {
		final int i = in.readInt();
		final int j = in.readInt();
		startNode = roadsNetwork.getNode(i);
		endNode = roadsNetwork.getNode(j);
		if ( this.loadPhysicalExtension )
			getAbstractPhysicalExtension().getSpatialEntityOwner().setSpatialTranslation(startNode.getPosition());
	}
	
	public static RoadArc newFromDataInputStream(DataInputStream in, RoadsNetwork roadsNetwork, final boolean decoreToPathArc, final boolean loadPhysicalExtension) throws IOException {
		final int i = in.readInt();
		final int j = in.readInt();
		RoadArc arc = new RoadArc(roadsNetwork.getNode(i), roadsNetwork.getNode(j), decoreToPathArc, loadPhysicalExtension);
		return arc;
	}

	public void writeOnDataOutputStream(DataOutputStream out) throws IOException {
		out.writeInt(startNode.getId());
		out.writeInt(endNode.getId());
	}
	
}
