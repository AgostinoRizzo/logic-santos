/**
 * 
 */
package it.unical.logic_santos.universe;

import it.unical.logic_santos.spatial_entity.ISpatialEntity;

/**
 * @author Agostino
 *
 */
public interface IWorld {

	public void initComponents();
	public void loadComponents();
	public void update(final float tpf);
	
	public boolean addSpatialEntity(ISpatialEntity spatialEntity);
	public boolean removeSpatialEntity(ISpatialEntity spatialEntity);
}
