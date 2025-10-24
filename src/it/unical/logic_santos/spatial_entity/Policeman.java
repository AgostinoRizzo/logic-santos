/**
 * 
 */
package it.unical.logic_santos.spatial_entity;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.gameplay.IBullet;
import it.unical.logic_santos.gameplay.IWeapon;
import it.unical.logic_santos.gameplay.Rifle;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.gui.screen.IVisibleEntity;
import it.unical.logic_santos.gui.screen.IndicatorType;
import it.unical.logic_santos.gui.screen.ScreenConfig;
import it.unical.logic_santos.physics.activity.PolicemanPhysicalActivity;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class Policeman extends AbstractHuman implements IVisibleEntity {

	private static final String NAME = "Policeman";
	private static final float  MASS = 50.0f;
	
	private IWeapon weapon=null;
	private LogicSantosApplication application=null;
	
	public Policeman() {
		super(Policeman.getPolicemanPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
		this.setPhysicalActivity(new PolicemanPhysicalActivity(this));
		this.initWeapon();
	}
	
	public Policeman(final Vector3D _spatialPosition) {
		super(_spatialPosition, Policeman.getPolicemanPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
		this.setPhysicalActivity(new PolicemanPhysicalActivity(this));
		this.initWeapon();
	}
	
	@Override
	public String getName() {
		return Policeman.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new Policeman();
	}
	
	@Override
	public float getMass() {
		return MASS;
	}
	
	public PolicemanPhysicalActivity getWalkerPhysicalActivity() {
		return ((PolicemanPhysicalActivity) getPhysicalActivity());
	}
	
	public void setApplication(LogicSantosApplication application) {
		this.application = application;
		this.initWeapon();
	}
	
	public boolean shoot() {
		if ( weapon==null || (!weapon.hasBullets()) )
			return false;
		IBullet bullet = weapon.shoot();
		bullet.initActivity( this.getPolicemanPhysicalActivity().getControl().getPhysicsLocation(),
				this.getPolicemanPhysicalActivity().getControl().getViewDirection() );
		application.getBulletManager().addBullet(bullet);
		return true;
	}

	public static IPhysicalExtension getPolicemanPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generatePolicemanPhysicalExtension(_spatialPosition);
	}
	
	@Override
	public IndicatorType getIndicatorType() {
		return IndicatorType.POLICEMAN;
	}

	@Override
	public Vector2f get2dWorldPosition() {
		final Vector3f position3d = getHumanPhysicalActivity().getControl().getPhysicsLocation();
		return ( new Vector2f( position3d.getX(), position3d.getZ() ) );
	}

	@Override
	public String getIndicatorImageName() {
		return ScreenConfig.POLICEMAN_INDICATOR_IMAGE_NAME;
	}

	@Override
	public Integer[] getIndicatorImageExtension() {
		Integer[] size = new Integer[2];
		size[ 0 ] = ScreenConfig.POLICEMAN_INDICATOR_IMAGE_SIZE;
		size[ 1 ] = ScreenConfig.POLICEMAN_INDICATOR_IMAGE_SIZE;
		return size;
	}

	@Override
	public float getAngleRotation() {
		return 0.0f;
	}
	
	private void initWeapon() {
		this.weapon = new Rifle( this, this.application);
	}
}
