/**
 * Vector3d is a class that models a 3D euclidean vector and implements common
 * operations on vectors (scale, add, minus, length etc)
 */

package it.unical.logic_santos.toolkit.math;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.jme3.math.Vector3f;

/**@author agostino
 * @version 1.0
 * @category CollisionDetectionEngine
 */

public class Vector3D extends Vector2D { // Vector3d is a class that models a 3D euclidean vector and implements common
	                                     // operations on vectors (scale, add, minus, length etc)
    
	// represents a Vector3d in the origin of the axes (0, 0, 0)
	public static final Vector3D ZERO = new Vector3D(0, 0, 0);
	
	// z coordinate (3D space) (coordinate x and y are inherited from Vector2d superclass) ...
	protected float z; 
	 
	
	// ... CONSTRUCTORS ...
	
	public Vector3D() {
		super();
		this.z = 0.0f;
	}
	
	public Vector3D(float xCoordinate, float yCoordinate, float zCoordinate) {
		super(xCoordinate, yCoordinate);
		this.z = zCoordinate;
	}
	
	public Vector3D(final Vector3D v) {
	 	super((Vector2D)v);
	 	this.z = v.z;
	}
	
	public Vector3D(final Vector3f v) {
	 	this.x = v.getX();
	 	this.y = v.getY();
	 	this.z = v.getZ();
	}
	
	// ... GETTERS AND SETTERS ...

	public final float getZ() {
		return z;
	}

	public final void setZ(final float zCoordinate) {
		this.z = zCoordinate;
	}
	
	// ... GENERAL METHODS ...
	
	@Override
	public Vector3D cpy() {
		return new Vector3D(this);
	}
	
	@Override
	public Vector3D scale(final float scaleFactor) {
		super.scale(scaleFactor);
		z = z * scaleFactor;
		return this;
	}

	public float dot(final Vector3D v) {
		return (super.dot((Vector2D)v) + (z * v.z));
	}
	
	@Override
	public float abs() {
		return (float)Math.sqrt((double)((getX() * getX()) + (getY() * getY()) + (z * z)));
	}
	
	public Vector3D add(final Vector3D v) {
		super.add((Vector2D)v);
		z += v.z;
		return this;
	}
	
	public Vector3D minus(final Vector3D v) {
		super.minus((Vector2D)v);
		z -= v.z;
		return this;
	}
	
	public float distanceTo(final Vector3D point) {
		return (float)Math.sqrt( MathGameToolkit.square(point.x-this.x) + MathGameToolkit.square(point.y-this.y) + MathGameToolkit.square(point.z-this.z));
	}
	
	public Vector3D clone() {
		return new Vector3D(this);
	}
	
	@Override
	public Vector3D normalize() {
		final float length = abs();
		x = x / length;
		y = y / length;
		z = z / length;
		return this;
	}
	
	@Override
	public String toString() {
		return "[" + getX() + "," + getY() + "," + z +"]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vector3D) {
			Vector3D other = (Vector3D) obj;
			return ( (x==other.x) && (y==other.y) && (z==other.z) );
		}
		return false;
	}
	
	public Vector3f toVector3f() {
		return new Vector3f(x, y, z);
	}

	public void writeOnDataOutputStream(DataOutputStream out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(z);
	}
	
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		z = in.readFloat();
	}

	public static Vector3D calculateDirection(final Vector3D start, final Vector3D end) {
		Vector3D ans = new Vector3D(Vector3D.ZERO.clone());
		ans.minus(start);
		ans.add(end);
		return ans;
	}
	
}
