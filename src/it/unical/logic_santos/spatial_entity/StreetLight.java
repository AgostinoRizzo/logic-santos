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
public class StreetLight extends AbstractStaticSpatialEntity {

	private static final String NAME = "StreetLight";
	
	public StreetLight() {
		super(StreetLight.getStreetLightPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public StreetLight(final Vector3D _spatialPosition) {
		super(_spatialPosition, StreetLight.getStreetLightPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return StreetLight.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new StreetLight();
	}

	public static IPhysicalExtension getStreetLightPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateStreetLightPhysicalExtension(_spatialPosition);
	}
}
