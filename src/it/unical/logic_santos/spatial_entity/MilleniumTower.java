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
public class MilleniumTower extends AbstractBuilding {

	private static final String NAME = "MilleniumTower";
	
	public MilleniumTower() {
		super(MilleniumTower.getMilleniumTowerPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public MilleniumTower(final Vector3D _spatialPosition) {
		super(_spatialPosition, MilleniumTower.getMilleniumTowerPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return MilleniumTower.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new MilleniumTower();
	}
	
	@Override
	public Vector3D getSpatialEntityTranslationAdjustment() {
		return ModelingConfig.MILLENIUM_TOWER_TRANSLATION_ADJUSTMENT;
	}

	public static IPhysicalExtension getMilleniumTowerPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateMilleniumTowerPhysicalExtension(_spatialPosition);
	}
}
