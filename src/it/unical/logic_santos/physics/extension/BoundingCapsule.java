/**
 * 
 */
package it.unical.logic_santos.physics.extension;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import it.unical.logic_santos.collision.ICollidable;
import it.unical.logic_santos.collision.ICollisionResults;
import it.unical.logic_santos.collision.SupervisedSpaceCollisionArena;
import it.unical.logic_santos.toolkit.geometry.Capsule;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class BoundingCapsule extends AbstractBoundingVolume {

	private Capsule capsule=null;

	
	
	public BoundingCapsule(IPhysicalExtension _owner) {
		super(_owner);
		this.capsule = new Capsule();
	}
	
	public BoundingCapsule(final float radius, final float segmentHeight, IPhysicalExtension _owner) {
		super(_owner);
		this.capsule = new Capsule(radius, segmentHeight);
	}
	
	public BoundingCapsule(final Vector3D centerPosition, final float radius, final float segmentHeight, IPhysicalExtension _owner) {
		super(centerPosition, _owner);
		this.capsule = new Capsule(radius, segmentHeight);
	}
	
	@Override
	public BoundingVolumeType getType() {
		return BoundingVolumeType.Capsule;
	}

	@Override
	public float getVolume() {
		return capsule.getVolume();
	}

	@Override
	public Capsule getShape() {
		return capsule;
	}

	@Override
	public boolean collide(ICollidable c, ICollisionResults collisionResults) {
		
		if (c instanceof BoundingCapsule) {
			
			BoundingCapsule other = (BoundingCapsule)c;
			
			/* we assume that the BoundingCapsule is axes oriented */
			final Vector3f thisPosition  = this.owner.getSpatialEntityOwner().getSpatialTranslation().toVector3f();
			final Vector3f otherPosition = other.owner.getSpatialEntityOwner().getSpatialTranslation().toVector3f();
			
			final Vector2f this2dPosition  = new Vector2f( thisPosition.getX(), thisPosition.getZ() );
			final Vector2f other2dPosition = new Vector2f( otherPosition.getX(), otherPosition.getZ() );
			
			if ( this2dPosition.distance( other2dPosition )>( this.capsule.getRadius()+other.capsule.getRadius() ) )
				return false;
			
			final float thisY  = thisPosition.getY();
			final float otherY = otherPosition.getY();
			
			if ( ( ( thisY+this.capsule.getHeight() )>=( otherY-other.capsule.getHeight() ) ) ||
					( ( thisY-this.capsule.getHeight() )<=( otherY+other.capsule.getHeight() ) ) )
				return true;
			
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean nearby(ICollidable c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SupervisedSpaceCollisionArena getSupervisedSpaceCollisionArena() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getXExtension() {
		return capsule.getRadius();
	}

	@Override
	public float getYExtension() {
		return (capsule.getSegmentHeight()/2.0f);
	}

	@Override
	public float getZExtension() {
		return capsule.getRadius();
	}
}
