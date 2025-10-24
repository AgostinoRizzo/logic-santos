/**
 * 
 */
package it.unical.logic_santos.spatial_entity;

import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class WatchTower extends AbstractStaticSpatialEntity {

	private static final String NAME = "Watch Tower";
	
	public WatchTower() {
		super(WatchTower.getWatchTowerPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public WatchTower(final Vector3D _spatialPosition) {
		super(_spatialPosition, WatchTower.getWatchTowerPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return WatchTower.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new WatchTower();
	}

	public static IPhysicalExtension getWatchTowerPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateWatchTowerPhysicalExtension(_spatialPosition);
	}
}
