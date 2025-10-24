/**
 * 
 */
package it.unical.logic_santos.toolkit.math;

import it.unical.logic_santos.collision.ICollidable;
import it.unical.logic_santos.collision.ICollisionResults;
import it.unical.logic_santos.collision.SupervisedSpaceCollisionArena;

/**
 * @author Agostino
 *
 */
public class Point extends Vector3D implements ICollidable {

	
	public Point() {
		super();
	}
	
	public Point(final Point p) {
		super((Vector3D)p);
	}
	
	public Point(final Vector3D p) {
		super(p);
	}
	
	public void move(final float step, final Vector3D direction) {
		this.x += (step * direction.x);
		this.y += (step * direction.y);
		this.z += (step * direction.z);
	}
	
	@Override
	public boolean collide(ICollidable c, ICollisionResults collisionResults) {
		return c.collide(this, collisionResults);
	}

	@Override
	public boolean nearby(ICollidable c) {
		return c.nearby(this);
	}

	@Override
	public SupervisedSpaceCollisionArena getSupervisedSpaceCollisionArena() {
		return new SupervisedSpaceCollisionArena(new Vector2D(x, z), 0.0f, 0.0f);
	}
	
	

}
