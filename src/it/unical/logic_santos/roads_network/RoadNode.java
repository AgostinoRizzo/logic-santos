/**
 * 
 */
package it.unical.logic_santos.roads_network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import it.unical.logic_santos.design.modeling.SimplePhysicalExtensionGenerator;
import it.unical.logic_santos.physics.extension.AbstractPhysicalExtension;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.spatial_entity.AbstractStaticSpatialEntity;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class RoadNode extends AbstractStaticSpatialEntity {

	protected Vector3D position=null;
	protected int id=1;
	
	public RoadNode() {
		super(SimplePhysicalExtensionGenerator.generateNewRoadNodePhysicalExtension(Vector3D.ZERO, false));
		this.position = Vector3D.ZERO.clone();
		this.getAbstractPhysicalExtension().setSpatialEntityOwner(this);
		this.id = 1;
	}
	
	public RoadNode(final boolean decoreToPathNode) {
		super(SimplePhysicalExtensionGenerator.generateNewRoadNodePhysicalExtension(Vector3D.ZERO, decoreToPathNode));
		this.position = Vector3D.ZERO.clone();
		this.getAbstractPhysicalExtension().setSpatialEntityOwner(this);
		this.id = 1;
	}
	
	public RoadNode(final Vector3D position, final boolean decoreToPathNode) {
		super(position, SimplePhysicalExtensionGenerator.generateNewRoadNodePhysicalExtension(position, decoreToPathNode));
		this.position = position.clone();
		this.getAbstractPhysicalExtension().setSpatialEntityOwner(this);
		this.id = 1;
	}
	
	public RoadNode(final Vector3D position, final int id, final boolean decoreToPathNode) {
		super(position, SimplePhysicalExtensionGenerator.generateNewRoadNodePhysicalExtension(position, decoreToPathNode));
		this.position = position.clone();
		this.getAbstractPhysicalExtension().setSpatialEntityOwner(this);
		this.id = id;
	}
	
	public Vector3D getPosition() {
		return position;
	}
	
	public void setPosition(final Vector3D position) {
		this.position = position.clone();
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(final int id) {
		this.id = id;
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

	public void readFromDataInputStream(DataInputStream in) throws IOException {
		position.setX(in.readFloat());
		position.setY(in.readFloat());
		position.setZ(in.readFloat());
		id = in.readInt();
		AbstractPhysicalExtension physicalExtension = getAbstractPhysicalExtension();
		if ( physicalExtension!=null )
			physicalExtension.getSpatialEntityOwner().setSpatialTranslation(position);
	}

	public void writeOnDataOutputStream(DataOutputStream out) throws IOException {
		out.writeFloat(position.getX());
		out.writeFloat(position.getY());
		out.writeFloat(position.getZ());
		out.writeInt(id);		
	}
	
}
