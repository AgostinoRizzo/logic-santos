/**
 * 
 */
package it.unical.logic_santos.physics.extension;

import it.unical.logic_santos.collision.ICollidable;
import it.unical.logic_santos.collision.ICollisionResults;
import it.unical.logic_santos.collision.SupervisedSpaceCollisionArena;
import it.unical.logic_santos.toolkit.geometry.Sphere;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class BoundingSphere extends AbstractBoundingVolume {

	private Sphere sphere=null;
	
	
	
	public BoundingSphere(IPhysicalExtension _owner) {
		super(_owner);
		this.sphere = new Sphere();
	}
	
	public BoundingSphere(final float radius, IPhysicalExtension _owner) {
		super(_owner);
		this.sphere = new Sphere(radius);
	}
	
	public BoundingSphere(final Vector3D centerPosition, final float radius, IPhysicalExtension _owner) {
		super(centerPosition, _owner);
		this.sphere = new Sphere(radius);
	}
	
	
	
	@Override
	public BoundingVolumeType getType() {
		return BoundingVolumeType.Sphere;
	}

	@Override
	public float getVolume() {
		return sphere.getVolume();
	}

	@Override
	public Sphere getShape() {
		return sphere;
	}

	@Override
	public boolean collide(ICollidable c, ICollisionResults collisionResults) {
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
		return sphere.getRadius();
	}

	@Override
	public float getYExtension() {
		return sphere.getRadius();
	}

	@Override
	public float getZExtension() {
		return sphere.getRadius();
	}

	
}
