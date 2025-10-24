/**
 * The ICollisionDetectionEngin interface represents a collision detection engine that models a collision arena
 * of collidable objects to aim collision detection between them using various collision detection techniques
 */

package it.unical.logic_santos.collision;

import java.util.List;
import java.util.Set;

/**
 * @author agostino
 * @version 1.0
 * @category CollisionDetectionEngine
 */

public interface ICollisionDetectionEngine { // Collision detection engine that models a collision arena
	                                         // of collidable objects to aim collision detection between them
	                                         // using various collision detection techniques
    /** adds a collidable game object in the arena of the engine */
	public boolean addCollidable(final ICollidable c); // abstract method
	
	/** removes a collidable game object from the arena of the engine */
	public boolean removeCollidable(final ICollidable c); // abstract method
	
	/** update the position of the collidable object to the arena of the engine */
	public boolean updateCollidable(final ICollidable c); // abstract method
	
	/** get a set of nearby ICollidable game objects of the ICollidable game object passed like a parameter */
	public Set<ICollidable> getNearby(final ICollidable c); // abstract method
	
	/** returns a set of ICollidable game objects which collide with the ICollidable game object passed like a parameter */
	public Set<ICollidable> checkCollisions(final ICollidable c, List<ICollisionResults> collisionResults); // abstract method

	public void removeCollidables();
}
