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
public class SeaFrontTrafficLight extends AbstractStaticSpatialEntity {

	private static final String NAME = "SeaFrontTrafficLight";
	
	public SeaFrontTrafficLight() {
		super(SeaFrontTrafficLight.getSeaFrontTrafficLightPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public SeaFrontTrafficLight(final Vector3D _spatialPosition) {
		super(_spatialPosition, SeaFrontTrafficLight.getSeaFrontTrafficLightPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return SeaFrontTrafficLight.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new SeaFrontTrafficLight();
	}

	public static IPhysicalExtension getSeaFrontTrafficLightPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateSeaFrontTrafficLightPhysicalExtension(_spatialPosition);
	}
}
