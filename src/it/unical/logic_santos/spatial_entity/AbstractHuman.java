/**
 * 
 */
package it.unical.logic_santos.spatial_entity;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.math.Vector3f;

import it.unical.logic_santos.gui.screen.IVisibleEntity;
import it.unical.logic_santos.physics.activity.HumanPhysicalActivity;
import it.unical.logic_santos.physics.activity.PolicemanPhysicalActivity;
import it.unical.logic_santos.physics.activity.VehiclePhysicalActivity;
import it.unical.logic_santos.physics.activity.WalkerPhysicalActivity;
import it.unical.logic_santos.physics.extension.BoundingCapsule;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public abstract class AbstractHuman extends AbstractDynamicSpatialEntity implements IVisibleEntity {
	
	private int startNodeId=1;
	
	public AbstractHuman(IPhysicalExtension _physicalExtension) {
		super(_physicalExtension);
	}

	public AbstractHuman(final Vector3D _spatialPosition, IPhysicalExtension _physicalExtension) {
		super(_spatialPosition, _physicalExtension);
	}
	
	public VehiclePhysicalActivity getVehiclePhysicalActivity() {
		return ((VehiclePhysicalActivity) getPhysicalActivity());
	}
	
	public WalkerPhysicalActivity getWalkerPhysicalActivity() {
		return ((WalkerPhysicalActivity) getPhysicalActivity());
	}
	
	public PolicemanPhysicalActivity getPolicemanPhysicalActivity() {
		return ((PolicemanPhysicalActivity) getPhysicalActivity());
	}
	
	public HumanPhysicalActivity getHumanPhysicalActivity() {
		return ((HumanPhysicalActivity) getPhysicalActivity());
	}
	
	public int getStartNodeId() {
		return startNodeId;
	}
	
	public void setStartNodeId(final int id) {
		this.startNodeId = id;
	}
	
	@Override
	public Vector3D getSpatialTranslation() {
		return ( new Vector3D(getHumanPhysicalActivity().getControl().getPhysicsLocation()) );
	}
	
	public void updateDriverPosition( final AbstractVehicle drivenVehicle ) {
		
	}
	
	public void updateDriverPosition( final AbstractAircraft drivenAircraft ) {
		
	}
	
	public static boolean collisionHumans( AbstractHuman right, AbstractHuman left ) {
		final CapsuleCollisionShape rightShape = (CapsuleCollisionShape) right.getHumanPhysicalActivity().getControl().getCollisionShape();
		final CapsuleCollisionShape leftShape  = (CapsuleCollisionShape) left.getHumanPhysicalActivity().getControl().getCollisionShape();
		
		BoundingCapsule boundingCapsuleRight = new BoundingCapsule( rightShape.getRadius(), rightShape.getHeight(), right.getPhysicalExtension() );
		BoundingCapsule boundingCapsuleLeft  = new BoundingCapsule( leftShape.getRadius(), leftShape.getHeight(), left.getPhysicalExtension() );
		
		return ( boundingCapsuleRight.collide( boundingCapsuleLeft, null ) );
	}
	
	public static boolean largeCollisionHumans( AbstractHuman right, AbstractHuman left ) {
		final CapsuleCollisionShape rightShape = (CapsuleCollisionShape) right.getHumanPhysicalActivity().getControl().getCollisionShape();
		final CapsuleCollisionShape leftShape  = (CapsuleCollisionShape) left.getHumanPhysicalActivity().getControl().getCollisionShape();
		
		BoundingCapsule boundingCapsuleRight = new BoundingCapsule( rightShape.getRadius(), rightShape.getHeight(), right.getPhysicalExtension() );
		BoundingCapsule boundingCapsuleLeft  = new BoundingCapsule( leftShape.getRadius(), leftShape.getHeight(), left.getPhysicalExtension() );
		boundingCapsuleRight.getShape().setRadius( boundingCapsuleRight.getShape().getRadius()*2.0f );
		boundingCapsuleLeft.getShape().setRadius( boundingCapsuleLeft.getShape().getRadius()*2.0f );
		
		return ( boundingCapsuleRight.collide( boundingCapsuleLeft, null ) );
	}

}
