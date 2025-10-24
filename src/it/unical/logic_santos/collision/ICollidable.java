/**
 * This ICollidable interface represents a collidable object used in the CollisionDetectionEngine
 * It represents any kind of collidable object in the game arena
 */

package it.unical.logic_santos.collision;

/**
 * @author agostino
 * @version 1.0
 * @category CollisionDetectionEngine
 */

public interface ICollidable { // Collidable object processed in the CollisionDetectionEngine
	
	/** returns true if the current collidable object collides with the collidable object passed like a parameter, false otherwise */
	public boolean collide(final ICollidable c, ICollisionResults collisionResults); // abstract method
	
	public boolean nearby(final ICollidable c); // abstract method
	
	/** returns the coordinates of the points that locates the ICollidable game object in the ICollisionArena */
    public SupervisedSpaceCollisionArena getSupervisedSpaceCollisionArena(); // abstract method
	
}