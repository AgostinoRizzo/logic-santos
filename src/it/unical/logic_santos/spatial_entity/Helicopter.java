/**
 * 
 */
package it.unical.logic_santos.spatial_entity;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.gui.screen.IndicatorType;
import it.unical.logic_santos.gui.screen.ScreenConfig;
import it.unical.logic_santos.physics.activity.AircraftPhysicalActivity;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class Helicopter extends AbstractAircraft {

	private static final String NAME = "Helicopter";
	private static final float  MASS = 1000.0f;
	
	public Helicopter() {
		super(Helicopter.getHelicopterPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
		this.setPhysicalActivity(new AircraftPhysicalActivity(this));
	}
	
	public Helicopter(final Vector3D _spatialPosition) {
		super(_spatialPosition, Helicopter.getHelicopterPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
		this.setPhysicalActivity(new AircraftPhysicalActivity(this));
	}
	
	@Override
	public String getName() {
		return Helicopter.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new Helicopter();
	}
	
	@Override
	public float getMass() {
		return MASS;
	}
	
	public AircraftPhysicalActivity getAircraftPhysicalActivity() {
		return (AircraftPhysicalActivity) getPhysicalActivity();
	}

	public static IPhysicalExtension getHelicopterPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateHelicopterPhysicalExtension(_spatialPosition);
	}

	@Override
	public IndicatorType getIndicatorType() {
		return IndicatorType.PLAYER;
	}

	@Override
	public Vector2f get2dWorldPosition() {
		final Vector3f position3d = getAircraftPhysicalActivity().getControl().getPhysicsLocation();
		return ( new Vector2f( position3d.getX(), position3d.getZ() ) );
	}

	@Override
	public String getIndicatorImageName() {
		return ScreenConfig.VEHICLE_INDICATOR_IMAGE_NAME;
	}

	@Override
	public Integer[] getIndicatorImageExtension() {
		Integer[] size = new Integer[2];
		size[ 0 ] = ScreenConfig.VEHICLE_INDICATOR_IMAGE_SIZE;
		size[ 1 ] = ScreenConfig.VEHICLE_INDICATOR_IMAGE_SIZE;
		return size;
	}

	@Override
	public float getAngleRotation() {
		return 0.0f;
	}
}
