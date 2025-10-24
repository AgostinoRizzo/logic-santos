/**
 * 
 */
package it.unical.logic_santos.spatial_entity;

import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.physics.activity.VehiclePhysicalActivity;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class Car extends AbstractVehicle {

	private static final String NAME = "Car";
	private static final float MASS = 400.0f;
	
	public Car() {
		super(Car.getCarPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
		this.setPhysicalActivity(new VehiclePhysicalActivity(this));
	}
	
	public Car(final Vector3D _spatialPosition) {
		super(_spatialPosition, Car.getCarPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
		this.setPhysicalActivity(new VehiclePhysicalActivity(this));
	}
	
	@Override
	public String getName() {
		return Car.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new Car();
	}
	
	@Override
	public float getMass() {
		return MASS;
	}

	public static IPhysicalExtension getCarPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateCarPhysicalExtension(_spatialPosition);
	}
	
}
