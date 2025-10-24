/**
 * 
 */
package it.unical.logic_santos.universe;

import it.unical.logic_santos.spatial_entity.CityKernel;
import it.unical.logic_santos.spatial_entity.ClassicTree;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.terrain.modeling.ITerrainChunkModel;
import it.unical.logic_santos.toolkit.math.Vector2D;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class SpatialEntityGenerator {

	private ITerrainChunkModel terrainModel=null;
	
	public SpatialEntityGenerator(ITerrainChunkModel _terrainModel) {
		this.terrainModel = _terrainModel;
	}
	
	public ISpatialEntity generateNewSpatialEntity(final Class<?> spatialEntityClass, final Vector2D worldArenaSpatialPosition) {
		
		final float terrainHeight = terrainModel.getHeight(worldArenaSpatialPosition.getX(), worldArenaSpatialPosition.getY());
		final Vector3D spatialPosition = new Vector3D(worldArenaSpatialPosition.getX(), terrainHeight, worldArenaSpatialPosition.getY());
		
		if (spatialEntityClass == ClassicTree.class) {
			return new ClassicTree(spatialPosition);
		} else if (spatialEntityClass == CityKernel.class) {
			return new CityKernel(spatialPosition);
		}
		return null;
	}
}
