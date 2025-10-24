/**
 * Vector2d is a class that models a 2D euclidean vector and implements common
 * operations on vectors (scale, add, minus, length etc)
 */

package it.unical.logic_santos.toolkit.math;

import com.jme3.math.Vector2f;

/**
 * @author agostino
 * @version 1.0
 * @category CollisionDetectionEngine
 */

public class Vector2D { // Vector2d models a 2D euclidean vector and implements common
	                    // operations on vectors (scale, add, minus, length etc)
     
	// represents a Vector2d in the origin of the axes (0, 0)
	public static final Vector2D ZERO = new Vector2D(0, 0);
	
	// x, y coordinate (2D space)
	protected float x; 
	protected float y;
	
	
	// ... CONSTRUCTORS ...
	
	public Vector2D() {
		this.x = 0.0f;
		this.y = 0.0f;
	}
	
	public Vector2D(final float xCoordinate, final float yCoordinate) {
		this.x = xCoordinate;
		this.y = yCoordinate;
	}
	
	public Vector2D(final Vector2D v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	public Vector2D(final Vector2f v) {
		this.x = v.getX();
		this.y = v.getY();
	}
	
	// ... GETTERS AND SETTERS ...
	
	public final float getX() {
		return x;
	}
	
	public final float getY() {
		return y;
	}
	
	public final void setX(final float xCoordinate) {
		this.x = xCoordinate;
	}
	
	public final void setY(final float yCoordinate) {
		this.y = yCoordinate;
	}

	// ... GENERAL METHODS ...
	
	public Vector2D cpy() {
		return new Vector2D(this);
	}
	
	public Vector2D scale(final float scaleFactor) {
		x = x * scaleFactor;
		y = y * scaleFactor;
		return this;
	}
	
	public float dot(final Vector2D v) {
		return ((x * v.x) + (y * v.y));
	}
	
	public float abs() {
		return (float)Math.sqrt((double)((x * x) + (y * y)));
	}
	
	public Vector2D add(final Vector2D v) {
		x += v.x;
		y += v.y;
		return this;
	}
	
	public Vector2D minus(final Vector2D v) {
		x -= v.x;
		y -= v.y;
		return this;
	}
	
	public float distanceTo(final Vector2D point) {
		return (float)Math.sqrt( MathGameToolkit.square(point.x-this.x) + MathGameToolkit.square(point.y-this.y));
	}
	
	public Vector2D clone() {
		return new Vector2D(this);
	}
	
	public Vector2D normalize() {
		final float length = abs();
		x = x / length;
		y = y / length;
		return this;
	}
	
	@Override
	public String toString() {
		return "[" + x + "," + y + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vector2D) {
			Vector2D other = (Vector2D) obj;
			return ( (x==other.x) && (y==other.y) );
		}
		return false;
	}
	
	public Vector2f toVector2f() {
		return new Vector2f(x, y);
	}
	
	/**
	 * if the other vector is in the right side of the subject vector the method returns +1, otherwise -1.
	 * if the other vector is parallel to the subject vector then the method returns 0.
	 * @param subject
	 * @param other
	 * @return
	 */
	public static int getSide(final Vector2D subject, final Vector2D other) {
		final float dot = ( subject.x*(-other.y) ) + ( subject.y*other.x );
		if (dot>0.0f)
			return 1;
		else if (dot<0.0f)
			return -1;
		return 0;
	}
	
}
