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
public class TrafficLight extends AbstractStaticSpatialEntity {

	private static final String NAME = "TrafficLight";
	
	public TrafficLight() {
		super(TrafficLight.getTrafficLightPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public TrafficLight(final Vector3D _spatialPosition) {
		super(_spatialPosition, TrafficLight.getTrafficLightPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return TrafficLight.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new TrafficLight();
	}

	public static IPhysicalExtension getTrafficLightPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateTrafficLightPhysicalExtension(_spatialPosition);
	}
}
