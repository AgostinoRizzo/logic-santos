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
public class ParkTower extends AbstractBuilding {

	private static final String NAME = "ParkTower";
	
	public ParkTower() {
		super(ParkTower.getParkTowerPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public ParkTower(final Vector3D _spatialPosition) {
		super(_spatialPosition, ParkTower.getParkTowerPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return ParkTower.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new ParkTower();
	}
	
	@Override
	public Vector3D getSpatialEntityTranslationAdjustment() {
		return ModelingConfig.PARK_TOWER_TRANSLATION_ADJUSTMENT;
	}

	public static IPhysicalExtension getParkTowerPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateParkTowerPhysicalExtension(_spatialPosition);
	}
}
