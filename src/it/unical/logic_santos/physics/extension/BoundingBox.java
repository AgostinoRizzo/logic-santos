/**
 * 
 */
package it.unical.logic_santos.physics.extension;

import it.unical.logic_santos.collision.ICollidable;
import it.unical.logic_santos.collision.ICollisionResults;
import it.unical.logic_santos.collision.SupervisedSpaceCollisionArena;
import it.unical.logic_santos.toolkit.geometry.Box;
import it.unical.logic_santos.toolkit.math.Point;
import it.unical.logic_santos.toolkit.math.Vector2D;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class BoundingBox extends AbstractBoundingVolume {
	
	private Box box=null;
	
	public BoundingBox(IPhysicalExtension _owner) {
		super(_owner);
		box = new Box();
	}
	
	public BoundingBox(final float xExtension, final float yExtension, final float zExtension, IPhysicalExtension _owner) {
		super(_owner);
		box = new Box(xExtension*2.0f, yExtension*2.0f, zExtension*2.0f);
	}
	
	public BoundingBox(final Vector3D centerPosition, final float xExtension, final float yExtension, final float zExtension, IPhysicalExtension _owner) {
		super(centerPosition, _owner);
		box = new Box(xExtension*2.0f, yExtension*2.0f, zExtension*2.0f);
	}

	@Override
	public BoundingVolumeType getType() {
		return BoundingVolumeType.Box;
	}

	@Override
	public float getVolume() {
		return (box.getVolume());
	}
	
	@Override
	public Box getShape() {
		return box;
	}

	@Override
	public boolean collide(ICollidable c, ICollisionResults collisionResults) {
		
		if (c instanceof BoundingBox) {
			
			BoundingBox other = (BoundingBox)c;
			
			final Vector3D thisCenterPosition = this.getCenterPosition();
			final Vector3D otherCenterPosition = other.getCenterPosition();
			
			if ( ((otherCenterPosition.getX() - other.getXExtension()) > (thisCenterPosition.getX() + this.getXExtension())) ||
				 ((thisCenterPosition.getX() - this.getXExtension()) > (otherCenterPosition.getX() + other.getXExtension())) )
				return false;
			
			if ( ((otherCenterPosition.getY() - other.getYExtension()) > (thisCenterPosition.getY() + this.getYExtension())) ||
				 ((thisCenterPosition.getY() - this.getYExtension()) > (otherCenterPosition.getY() + other.getYExtension())) )
					return false;
			
			if ( ((otherCenterPosition.getZ() - other.getYExtension()) > (thisCenterPosition.getZ() + this.getZExtension())) ||
				 ((thisCenterPosition.getZ() - this.getZExtension()) > (otherCenterPosition.getZ() + other.getYExtension())) )
					return false;
			
			return true;
			
		} else if (c instanceof Point) {
			
			Point other = (Point)c;
			return collideWithPoint(other);
			
		}
		return false;
	}
	

	@Override
	public boolean nearby(ICollidable c) {
		return this.collide(c, null);
	}

	@Override
	public SupervisedSpaceCollisionArena getSupervisedSpaceCollisionArena() {
		
		final float width = this.getShape().getWidth();
		final float depth = this.getShape().getDepth();
		System.out.println(width + " " + this.getShape().getHeight() + " " + depth + " " + this.getCenterPosition().toString());
		return new SupervisedSpaceCollisionArena(new Vector2D(getCenterPosition().getX(), getCenterPosition().getZ()), width, depth);
	}

	@Override
	public float getXExtension() {
		return (box.getWidth()/2.0f);
	}

	@Override
	public float getYExtension() {
		return (box.getHeight()/2.0f);
	}

	@Override
	public float getZExtension() {
		return (box.getDepth()/2.0f);
	}
	
	public boolean collideWithPoint(final Point p) {
		//System.out.println("Point Coordinate: " + p.toString());
		final float xExtension = getXExtension();
		final float yExtension = getYExtension();
		final float zExtension = getZExtension();
		
		if ( (p.getX() > (centerPosition.getX() + xExtension)) ||
			 (p.getX() < (centerPosition.getX() - xExtension)))
			return false;
		
		if ( (p.getY() > (centerPosition.getY() + yExtension)) ||
			 (p.getY() < (centerPosition.getY() - yExtension)))
			return false;
		
		if ( (p.getZ() > (centerPosition.getZ() + zExtension)) ||
			 (p.getZ() < (centerPosition.getZ() - zExtension)))
			return false;
		
		return true;
	}


}
