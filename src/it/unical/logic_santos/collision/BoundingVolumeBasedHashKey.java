/**
 * The VerticesHashKey class represents an hash function that determines an hash value of a ICollidable game object based on the vertices of the object
 */

package it.unical.logic_santos.collision;

import java.util.ArrayList;
import java.util.Collection;

import it.unical.logic_santos.toolkit.math.Vector3D;


/**
 * @author agostino
 * @version 1.0
 * @category CollisionDetectionEngine
 */

public class BoundingVolumeBasedHashKey implements ICollidableHashKey { // VerticesHashKey class represents an hash function that determines an hash value of a ICollidable game object
	                                                                    // based on the vertices of the object
	// Collision detection engine
	SpatialHashingEngine collisionEngine;
	
	
	// ... CONSTRUCTORS ...
	
	public BoundingVolumeBasedHashKey(final SpatialHashingEngine engine) {
		this.collisionEngine = engine;
	}
	
	// ... GETTER AND SETTER METHODS ...
	
	public final SpatialHashingEngine getCollisionEngine() {
		return collisionEngine;
	}

	public final void setCollisionEngine(SpatialHashingEngine engine) {
		this.collisionEngine = engine;
	} 

	// ... GENERAL METHODS ...
	
	@Override
	public Collection<Integer> getHashCodes(ICollidable c) {
		
		/* we assume that a Collidable Supervised space is always is the CollisionArena ! */
		
		final float cellSize = collisionEngine.getCellSize();
		final Vector3D collisionArenaTranslation = collisionEngine.getCollisionArena().getTranslation();
		final SupervisedSpaceCollisionArena supervisedSpace = c.getSupervisedSpaceCollisionArena();
		float supervisedTranslationX = supervisedSpace.getTranslation().getX();
		float supervisedTranslationZ = supervisedSpace.getTranslation().getY();
		//System.out.println("Translation XY: " + collisionArenaTranslation);
		final float supervisedLocalTranslationX = ( -collisionArenaTranslation.getX() + supervisedTranslationX ) + collisionEngine.getCollisionArena().getXExtension();
		final float supervisedLocalTranslationZ = ( -collisionArenaTranslation.getZ() + supervisedTranslationZ ) + collisionEngine.getCollisionArena().getZExtension();
	
		final int x1 = (int) ((supervisedLocalTranslationX - supervisedSpace.getXExtension()) / cellSize);
		final int z1 = (int) ((supervisedLocalTranslationZ - supervisedSpace.getZExtension()) / cellSize);
		final int x2 = (int) ((supervisedLocalTranslationX + supervisedSpace.getXExtension()) / cellSize);
		final int z2 = (int) ((supervisedLocalTranslationZ + supervisedSpace.getZExtension()) / cellSize);
		
		int j;
		final int numColumns = collisionEngine.getNumberOfColumnBuckets();
		Collection<Integer> hashCodes = new ArrayList<Integer>();
		for(int i = z1; i <= z2; ++i)
			for(j = x1; j <= x2; ++j)
				hashCodes.add( (i*numColumns) + j );
		
		/*
		if (c instanceof BoundingBox) {
			for(Integer i: hashCodes)
				System.out.println("HASH CODE T: " + i.toString());
			System.out.println();
		} else if (c instanceof Point) {
			for(Integer i: hashCodes)
				System.out.println("HASH CODE P: " + i.toString());
			System.out.println();
		}
			;//System.out.println(c.getClass().getName());
		*/
		return hashCodes;
	}

}	