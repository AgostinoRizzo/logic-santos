/**
 * 
 */
package it.unical.logic_santos.physics.extension;

import it.unical.logic_santos.spatial_entity.ISpatialEntity;

/**
 * @author Agostino
 *
 */
public interface IPhysicalExtension {
	
	public AbstractBoundingVolume getBoundingVolume();
	public void setBoundingVolume(AbstractBoundingVolume boundingVolume);
	public ISpatialEntity getSpatialEntityOwner();
	public void setSpatialEntityOwner(ISpatialEntity spatialEntity);
	public void updateTranslation();
	
}