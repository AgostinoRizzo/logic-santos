/**
 * The ICollidableHashKey interface represents a generic hash function that determines an hash value of a ICollidable game object
 */

package it.unical.logic_santos.collision;

import java.util.Collection;

/**
 * @author agostino
 * @version 1.0
 * @category CollisionDetectionEngine
 */

public interface ICollidableHashKey { // ICollidableHashKey represents a generic hash function that determines an hash value of a ICollidable game object

	/** returns the hash value of a ICollidable game object */
	public Collection<Integer> getHashCodes(final ICollidable c); // abstract method
	
}