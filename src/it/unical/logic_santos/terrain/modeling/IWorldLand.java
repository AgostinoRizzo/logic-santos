/**
 * The IWorldLand interface represents a whole land of a world game
 * It is composed by a grid of Terrains (terrain models)
 */

package it.unical.logic_santos.terrain.modeling;

/**
 * @author agostino
 * @version 1.0
 * @category TerrainModeling
 */

public interface IWorldLand { /** IWorldLand represents a whole land of a world game
                                * and is composed by a grid of Terrains (terrain models) */

	/** returns the cell size of the terrains in the grid */
	public float getTerrainsCellSize(); // abstract method
	
	/** returns the width of the whole land */
	public float getLandWidth(); // abstract method
	
	/** returns the depth of the whole land */
	public float getLandDepth(); // abstract method
	
	/** returns the terrain which in the grid is situated in the cell (iX, iZ) */
	public ITerrainChunkModel getTerrain(final int iX, final int iZ); // abstract method
}