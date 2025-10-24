/**
 * 
 */
package it.unical.logic_santos.universe;

import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public interface IWorldArena {

	public Vector3D getTranslation(); // center
	
	/** returns the width size of the arena */
	public float getWidthSize(); // abstract method
	
	/** returns the depth size of the arena */
	public float getDepthSize(); // abstract method
	
	public float getXExtension();
	public float getZExtension();
	
}
