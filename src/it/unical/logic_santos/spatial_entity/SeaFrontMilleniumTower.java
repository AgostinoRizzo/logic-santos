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
public class SeaFrontMilleniumTower extends AbstractBuilding {

	private static final String NAME = "SeaFrontMilleniumTower";
	
	public SeaFrontMilleniumTower() {
		super(SeaFrontMilleniumTower.getSeaFrontMilleniumTowerPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public SeaFrontMilleniumTower(final Vector3D _spatialPosition) {
		super(_spatialPosition, SeaFrontMilleniumTower.getSeaFrontMilleniumTowerPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return SeaFrontMilleniumTower.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new SeaFrontMilleniumTower();
	}
	
	@Override
	public Vector3D getSpatialEntityTranslationAdjustment() {
		return ModelingConfig.MILLENIUM_TOWER_TRANSLATION_ADJUSTMENT;
	}

	public static IPhysicalExtension getSeaFrontMilleniumTowerPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateSeaFrontMilleniumTowerPhysicalExtension(_spatialPosition);
	}
}
