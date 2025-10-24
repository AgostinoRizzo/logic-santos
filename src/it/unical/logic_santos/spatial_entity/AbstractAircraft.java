/**
 * 
 */
package it.unical.logic_santos.spatial_entity;


import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;

import it.unical.logic_santos.gui.screen.IVisibleEntity;
import it.unical.logic_santos.physics.activity.AircraftPhysicalActivity;
import it.unical.logic_santos.physics.extension.BoundingCapsule;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public abstract class AbstractAircraft extends AbstractDynamicSpatialEntity implements IVisibleEntity  {

	public AbstractAircraft(IPhysicalExtension _physicalExtension) {
		super(_physicalExtension);
	}
	
	public AbstractAircraft(final Vector3D _spatialPosition, IPhysicalExtension _physicalExtension) {
		super(_spatialPosition, _physicalExtension);
	}
	
	public AircraftPhysicalActivity getAircraftPhysicalActivity() {
		return ((AircraftPhysicalActivity) getPhysicalActivity());
	}
	
	@Override
	public Vector3D getSpatialTranslation() {
		return ( new Vector3D(getAircraftPhysicalActivity().getControl().getPhysicsLocation()) );
	}
	
	public static boolean collisionAircraftHuman( AbstractAircraft right, AbstractHuman left ) {
		final CapsuleCollisionShape rightShape = (CapsuleCollisionShape) right.getAircraftPhysicalActivity().getControl().getCollisionShape();
		final CapsuleCollisionShape leftShape  = (CapsuleCollisionShape) left.getHumanPhysicalActivity().getControl().getCollisionShape();
		
		BoundingCapsule boundingCapsuleRight = new BoundingCapsule( rightShape.getRadius(), rightShape.getHeight(), right.getPhysicalExtension() );
		BoundingCapsule boundingCapsuleLeft  = new BoundingCapsule( leftShape.getRadius(), leftShape.getHeight(), left.getPhysicalExtension() );
		
		return ( boundingCapsuleRight.collide( boundingCapsuleLeft, null ) );
	}

}
