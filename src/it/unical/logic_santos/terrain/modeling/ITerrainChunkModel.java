/**
 * The ITerrainModel interface represents a terrain model based on an ITerrainArena in a 3D space
 * It represents a partition of the 3D game land which model its heights 
 */

package it.unical.logic_santos.terrain.modeling;

import java.io.IOException;

import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author agostino
 * @version 1.0
 * @category TerrainModeling
 */

public interface ITerrainChunkModel {

	/** returns the size of the square terrain */
	public float getSize(); // abstract method
	
	/** returns the width size of the terrain */
	public float getWidth(); // abstract method
	
	/** returns the depth size of the terrain */
	public float getDepth(); // abstract method
	
	/** returns the height of the terrain in the point identified by the x and z coordinates */
	public float getHeight(float x, float z); // abstract method
	
	public Vector3D getScale();
	public void setScale(final Vector3D scaleFactors);
	public void setScale(final float xScale, final float yScale, final float zScale);
	
	public Vector3D getTranslation();
	public void setTranslation(final Vector3D translationFactors);
	public void setTranslation(final float xTranslation, final float yTranslation, final float zTranslation);
	
	/** generate a flat terrain */
	public void generateFlatTerrain(); // abstract method
	
	public boolean isInside(final int x, final int z);
	public boolean isInside(final float x, final float z);
	
	/** save the terrain to a file 
	 * @throws IOException */
	public void saveTerrainToFile(final String fileName) throws IOException, IllegalArgumentException; // abstract method
	
	/** load the terrain from a file */
	public void loadTerrainFromFile(final String fileName) throws IOException, IllegalArgumentException; // abstract method
	
	public void addMixedTerrainModel(ITerrainChunkModel model);
	public void removeMixedTerrainModel(ITerrainChunkModel model);
}
