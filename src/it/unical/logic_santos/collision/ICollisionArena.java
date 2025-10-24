/**
 * The ICollisionArena interface represents a collision arena within ICollidable objects
 * It represents a flat space of the game world which contains all the objects involved in the game 
 */

package it.unical.logic_santos.collision;

import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author agostino
 * @version 1.0
 * @category CollisionDetectionEngine
 */

public interface ICollisionArena { // Collision arena that contains collidable game objects

	public Vector3D getTranslation(); // center
	
	/** returns the width size of the arena */
	public float getWidthSize(); // abstract method
	
	/** returns the depth size of the arena */
	public float getDepthSize(); // abstract method
	
	public float getXExtension();
	public float getZExtension();
	
}
