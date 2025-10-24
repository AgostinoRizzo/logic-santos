/**
 * 
 */
package it.unical.logic_santos.collision;

import it.unical.logic_santos.toolkit.math.Vector2D;

/**
 * @author Agostino
 *
 */
public class TerrainCollisionResults implements ICollisionResults {

	private Vector2D terrainCollisionCoordinates=null;
	private float terrainHeight;
	
	
	public TerrainCollisionResults() {
		this.terrainCollisionCoordinates = new Vector2D(Vector2D.ZERO);
		this.terrainHeight=0.0f;
	}
	
	public TerrainCollisionResults(final Vector2D _terrainCollisionCoordinates, final float _terrainHeight) {
		this.terrainCollisionCoordinates = _terrainCollisionCoordinates.clone();
		this.terrainHeight = _terrainHeight;
	}

	
	public Vector2D getTerrainCollisionCoordinates() {
		return terrainCollisionCoordinates;
	}

	public void setTerrainCollisionCoordinates(Vector2D terrainCollisionCoordinates) {
		this.terrainCollisionCoordinates = terrainCollisionCoordinates.clone();
	}

	public float getTerrainHeight() {
		return terrainHeight;
	}

	public void setTerrainHeight(float terrainHeight) {
		this.terrainHeight = terrainHeight;
	}
	
	
}
