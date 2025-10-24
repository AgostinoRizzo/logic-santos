/**
 * 
 */
package it.unical.logic_santos.spatial_entity;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.gui.screen.IVisibleEntity;
import it.unical.logic_santos.gui.screen.IndicatorType;
import it.unical.logic_santos.gui.screen.ScreenConfig;
import it.unical.logic_santos.physics.activity.HumanPhysicalActivity;
import it.unical.logic_santos.physics.activity.VehiclePhysicalActivity;
import it.unical.logic_santos.physics.activity.WalkerPhysicalActivity;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class Walker extends AbstractHuman {

	private static final String NAME = "Walker";
	private static final float  MASS = 50.0f;
	
	public Walker() {
		super(Walker.getPlayerPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
		this.setPhysicalActivity(new WalkerPhysicalActivity(this));
	}
	
	public Walker(final Vector3D _spatialPosition) {
		super(_spatialPosition, Walker.getPlayerPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
		this.setPhysicalActivity(new WalkerPhysicalActivity(this));
	}
	
	@Override
	public String getName() {
		return Walker.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new Walker();
	}
	
	@Override
	public float getMass() {
		return MASS;
	}
	
	public WalkerPhysicalActivity getPWalkerPhysicalActivity() {
		return ((WalkerPhysicalActivity) getPhysicalActivity());
	}

	public static IPhysicalExtension getPlayerPhysicalExtension(final Vector3D _spatialPosition) { //TODO: Walker.class!!!
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateWalkerBobPhysicalExtension(_spatialPosition);
	}

	@Override
	public IndicatorType getIndicatorType() {
		return IndicatorType.PLAYER;
	}

	@Override
	public Vector2f get2dWorldPosition() {
		final Vector3f position3d = getHumanPhysicalActivity().getControl().getPhysicsLocation();
		return ( new Vector2f( position3d.getX(), position3d.getZ() ) );
	}

	@Override
	public String getIndicatorImageName() {
		return ScreenConfig.WALKER_INDICATOR_IMAGE_NAME;
	}

	@Override
	public Integer[] getIndicatorImageExtension() {
		Integer[] size = new Integer[2];
		size[ 0 ] = ScreenConfig.WALKER_INDICATOR_IMAGE_SIZE;
		size[ 1 ] = ScreenConfig.WALKER_INDICATOR_IMAGE_SIZE;
		return size;
	}

	@Override
	public float getAngleRotation() {
		return 0.0f;
	}

}
