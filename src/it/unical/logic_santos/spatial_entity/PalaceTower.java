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
public class PalaceTower extends AbstractBuilding {

	private static final String NAME = "PalaceTower";
	
	public PalaceTower() {
		super(PalaceTower.getPalaceTowerPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public PalaceTower(final Vector3D _spatialPosition) {
		super(_spatialPosition, PalaceTower.getPalaceTowerPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return PalaceTower.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new PalaceTower();
	}

	public static IPhysicalExtension getPalaceTowerPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generatePalaceTowerPhysicalExtension(_spatialPosition);
	}
}
