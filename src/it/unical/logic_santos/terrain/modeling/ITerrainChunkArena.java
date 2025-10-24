/**
 * The ITerrainArena interface represents a terrain arena
 * It represents a flat space of the game world which contains all the objects involved in the game 
 */

package it.unical.logic_santos.terrain.modeling;

import it.unical.logic_santos.terrain.modeling.TerrainModelingConfig;

/**
 * @author agostino
 * @version 1.0
 * @category TerrainModeling
 */

public interface ITerrainChunkArena { // Terrain arena 

	public static final float SIZE = TerrainModelingConfig.TERRAIN_CHUNK_SIZE;
	
	/** returns the width size of the arena */
	public float getWidth(); // abstract method
	
	/** returns the depth size of the arena */
	public float getDepth(); // abstract method
	
	/** returns the size of the square arena */
	public float getSize(); // abstract method
	
}