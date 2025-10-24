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
public class SeaFrontStreetLight extends AbstractStaticSpatialEntity {

	private static final String NAME = "SeaFrontStreetLight";
	
	public SeaFrontStreetLight() {
		super(SeaFrontStreetLight.getSeaFrontStreetLightPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public SeaFrontStreetLight(final Vector3D _spatialPosition) {
		super(_spatialPosition, SeaFrontStreetLight.getSeaFrontStreetLightPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return SeaFrontStreetLight.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new SeaFrontStreetLight();
	}

	public static IPhysicalExtension getSeaFrontStreetLightPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateSeaFrontStreetLightPhysicalExtension(_spatialPosition);
	}
}
