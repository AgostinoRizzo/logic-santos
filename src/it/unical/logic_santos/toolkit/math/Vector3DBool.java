/**
 * 
 */
package it.unical.logic_santos.toolkit.math;

/**
 * @author Agostino
 *
 */
public class Vector3DBool {
	
	public static final Vector3DBool TRUE = new Vector3DBool(true, true, true); 
	public static final Vector3DBool FALSE = new Vector3DBool(false, false, false);
	
	private boolean x;
	private boolean y;
	private boolean z;
	
	public Vector3DBool() {
		this.x=false;
		this.y=false;
		this.z=false;
	}
	
	public Vector3DBool(final boolean _x, final boolean _y, final boolean _z) {
		this.x=_x;
		this.y=_y;
		this.z=_z;
	}
	
	public Vector3DBool(final Vector3DBool v) {
		this.x=v.x;
		this.y=v.y;
		this.z=v.z;
	}

	public boolean isX() {
		return x;
	}

	public void setX(boolean x) {
		this.x = x;
	}

	public boolean isY() {
		return y;
	}

	public void setY(boolean y) {
		this.y = y;
	}

	public boolean isZ() {
		return z;
	}

	public void setZ(boolean z) {
		this.z = z;
	}
	
	

}
