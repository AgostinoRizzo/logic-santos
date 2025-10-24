/**
 * 
 */
package it.unical.logic_santos.toolkit.math;

import java.util.HashSet;
import java.util.Set;

import it.unical.logic_santos.collision.ICollidable;
import it.unical.logic_santos.collision.ICollisionResults;
import it.unical.logic_santos.collision.SupervisedSpaceCollisionArena;

/**
 * @author Agostino
 *
 */
public class Ray implements ICollidable {

	public final static float LIMIT = 300.0f; //Float.POSITIVE_INFINITY;
	public final static float STEP = 1.0f;
	public final static int NUM_STEPS = (int) (LIMIT / STEP);
	
	private Vector3D origin=null;
	private Vector3D direction=null;
	
	
	public Ray() {
		this.origin = new Vector3D(Vector3D.ZERO);
		this.direction = new Vector3D(0.0f, 0.0f, 1.0f);
	}
	
	public Ray(final Vector3D _origin, final Vector3D _direction) {
		this.origin = new Vector3D(_origin);
		this.direction = new Vector3D(_direction);
		this.direction.normalize();
	}
	
	public Ray(final Ray r) {
		this.origin = new Vector3D(r.origin);
		this.direction = new Vector3D(r.direction);
		this.direction.normalize();
	}

	public Vector3D getOrigin() {
		return origin;
	}

	public void setOrigin(Vector3D origin) {
		this.origin = origin.clone();
	}

	public Vector3D getDirection() {
		return direction;
	}

	public void setDirection(Vector3D direction) {
		this.direction = direction.clone();
		this.direction.normalize();
	}

	@Override
	public boolean collide(ICollidable c, ICollisionResults collisionResults) {
		
		Point currentPoint = new Point(origin);
		int currentStep = 0;
		boolean collisionDetected = false;
		while( (!collisionDetected) && (currentStep < Ray.NUM_STEPS) ) {
			
			currentPoint.move(Ray.STEP, direction);
			collisionDetected = c.collide(currentPoint, collisionResults);
			++currentStep;
		}
		return collisionDetected;
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
	
	

}
