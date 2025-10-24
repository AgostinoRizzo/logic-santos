/**
 * The NearbyCollidablesSet class represents an hash table bucket composed by a linked list of ICollidable game objects 
 */

package it.unical.logic_santos.collision;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author agostino
 * @version 1.0
 * @category CollisionDetectionEngine
 */

public class NearbyCollidablesSet { // NearbyCollidablesSet class represents an hash table bucket
	                                //composed by a linked list of ICollidable game objects 

	public Set<ICollidable> collidables = new TreeSet<ICollidable>(); // hash table bucket
	
}