/**
 * 
 */
package it.unical.logic_santos.spatial_entity;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.gui.screen.IVisibleEntity;
import it.unical.logic_santos.gui.screen.IndicatorType;
import it.unical.logic_santos.gui.screen.ScreenConfig;
import it.unical.logic_santos.physics.activity.IPhysicalActivity;
import it.unical.logic_santos.physics.activity.VehiclePhysicalActivity;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.roads_network.RoadsNetwork;
import it.unical.logic_santos.toolkit.data_structure.Arc;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public abstract class AbstractVehicle extends AbstractDynamicSpatialEntity implements IVisibleEntity {
	
	private int startNodeId=1;
	
	public AbstractVehicle(IPhysicalExtension _physicalExtension) {
		super(_physicalExtension);
	}

	public AbstractVehicle(final Vector3D _spatialPosition, IPhysicalExtension _physicalExtension) {
		super(_spatialPosition, _physicalExtension);
	}
	
	public int getStartNodeId() {
		return startNodeId;
	}
	
	public void setStartNodeId(final int id) {
		this.startNodeId = id;
	}
	
	public VehiclePhysicalActivity getVehiclePhysicalActivity() {
		return ((VehiclePhysicalActivity) getPhysicalActivity());
	}
	
	@Override
	public Vector3D getSpatialTranslation() {
		return ( new Vector3D(getVehiclePhysicalActivity().getControl().getPhysicsLocation()) );
	}
	
	@Override
	public IndicatorType getIndicatorType() {
		return IndicatorType.VEHICLE;
	}

	@Override
	public Vector2f get2dWorldPosition() {
		final Vector3f position3d = getVehiclePhysicalActivity().getControl().getPhysicsLocation();
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
