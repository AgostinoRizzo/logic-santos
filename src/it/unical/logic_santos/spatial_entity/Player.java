/**
 * 
 */
package it.unical.logic_santos.spatial_entity;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.environment.sound.EffectSoundManager;
import it.unical.logic_santos.gameplay.Gun;
import it.unical.logic_santos.gameplay.IBullet;
import it.unical.logic_santos.gameplay.IWeapon;
import it.unical.logic_santos.gameplay.PlayerManager;
import it.unical.logic_santos.gameplay.WantedStars;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.gui.screen.IVisibleEntity;
import it.unical.logic_santos.gui.screen.IndicatorType;
import it.unical.logic_santos.gui.screen.ScreenConfig;
import it.unical.logic_santos.net.IRemoteCommunicator;
import it.unical.logic_santos.net.PlayerAction;
import it.unical.logic_santos.net.Vector3fCookie;
import it.unical.logic_santos.physics.activity.HumanPhysicalActivity;
import it.unical.logic_santos.physics.activity.PhysicsSpace;
import it.unical.logic_santos.physics.activity.VehiclePhysicalActivity;
import it.unical.logic_santos.physics.activity.WalkerPhysicalActivity;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class Player extends AbstractHuman implements IVisibleEntity {

	private static final String NAME = "Player";
	private static final float  MASS = 50.0f;
	
	private IWeapon weapon=null;
	private boolean isDriving=false;
	private boolean isFlying=false;
	private boolean isActive=true;
	private AbstractVehicle drivenVehicle=null;
	private AbstractAircraft drivenAircraft=null;
	private WantedStars wantedStars=null;
	private PlayerManager manager=null;
	private LogicSantosApplication application=null;
	
	public Player( LogicSantosApplication apllication ) { 
		super(Player.getPlayerPhysicalExtension(Vector3D.ZERO));
		this.wantedStars = new WantedStars();
		this.application = apllication;
		this.physicalExtension.setSpatialEntityOwner(this);
		this.setPhysicalActivity(new WalkerPhysicalActivity(this));
		this.initWeapon();
	}
	
	public Player(final Vector3D _spatialPosition, LogicSantosApplication application ) {
		super(_spatialPosition, Player.getPlayerPhysicalExtension(_spatialPosition));
		this.wantedStars = new WantedStars();
		this.application = application;
		this.physicalExtension.setSpatialEntityOwner(this);
		this.setPhysicalActivity(new WalkerPhysicalActivity(this));
		this.initWeapon();
	}
	
	@Override
	public String getName() {
		return Player.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new Car();
	}
	
	@Override
	public float getMass() {
		return MASS;
	}
	
	public boolean isDriving() {
		return isDriving;
	}
	
	public boolean isFlying() {
		return isFlying;
	}
	
	public boolean isRoaming() {
		return ( (!isDriving) && (!isFlying) );
	}
	
	public void setIsDriving( final AbstractVehicle drivenVehicle ) {
		this.isDriving = true;
		this.drivenVehicle = drivenVehicle;
	}
	
	public void setIsFlying( final AbstractAircraft drivenAircraft ) {
		this.isFlying = true;
		this.drivenAircraft = drivenAircraft;
	}
	
	public void setIsRoaming() {
		this.isDriving = false;
		this.isFlying = false;
		this.drivenVehicle = null;
		this.drivenAircraft = null;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void setIsActive( final boolean isActive ) {
		this.isActive = isActive;
	}
	
	public AbstractVehicle getDrivenVehicle() {
		return drivenVehicle;
	}
	
	public AbstractAircraft getDrivenAircraft() {
		return drivenAircraft;
	}
	
	public WantedStars getWantedStars() {
		return wantedStars;
	}
	
	public IWeapon getWeapon() {
		return weapon;
	}
	
	public void setWeapon(IWeapon weapon) {
		this.weapon = weapon;
	}
	
	public PlayerManager getManager() {
		return manager;
	}
	
	public void setManager(PlayerManager manager) {
		this.manager = manager;
	}
	
	public boolean hasManager() {
		return ( this.manager!=null );
	}
	
	public boolean shoot() {
		if ( weapon==null || (!weapon.hasBullets()) || (!isActive()) || getHumanPhysicalActivity().getLifeBar().isEmpty() )
			return false;
		IRemoteCommunicator remoteCommunicator = application.getRemoteCommunicator();
		if ( (remoteCommunicator!=null) && (!remoteCommunicator.isMaster()) && (remoteCommunicator.isConnected()) ) {
			PlayerAction playerAction = new PlayerAction( PlayerAction.PLAYER_SHOOT, true );
			playerAction.bulletStartPosition.vector = computeBullerStartPosition( this, application );
			playerAction.bulletDirection.vector = application.getCameraDirection().toVector3f();
			remoteCommunicator.onPlayerAction( playerAction );
		} else {
			IBullet bullet = weapon.shoot();
			bullet.initActivity( computeBullerStartPosition( this, application ), application.getCameraDirection().toVector3f() );
			application.getBulletManager().addBullet(bullet);
			wantedStars.onShootStart( bullet );
		}
		
		EffectSoundManager.getInstance().onWeaponShoot( weapon );
		return true;
	}
	
	public boolean shootFromPointer() {
		Camera cam = application.getCamera();
		final Vector2f click2d = application.getInputManager().getCursorPosition();
		final Vector3f click3d = cam.getWorldCoordinates( new Vector2f( click2d.getX(), click2d.getY() ), 0.0f ).clone();
		final Vector3f direction = cam.getWorldCoordinates( new Vector2f( click2d.getX(), click2d.getY() ), 1.0f ).
										subtractLocal( click3d ).normalizeLocal();
		final float distance = click3d.distance( getHumanPhysicalActivity().getControl().getPhysicsLocation() );
		IBullet bullet = weapon.shoot();
		bullet.initActivity( click3d.add( direction.mult( distance ) ), direction );
		application.getBulletManager().addBullet(bullet);
		wantedStars.onShootStart( bullet );
		
		EffectSoundManager.getInstance().onWeaponShoot( weapon );
		return true;
	}
	
	public void setDrivingInvisibility() {
		PhysicsSpace.getInstance().getSpace().remove( this.getHumanPhysicalActivity().getControl() );
		application.getRootNode().detachChild( ( (ModelBasedPhysicalExtension) this.getAbstractPhysicalExtension() ).getModelSpatial() ); 
	}
	
	public void setRoamingVisibility() {
		PhysicsSpace.getInstance().getSpace().add( this.getHumanPhysicalActivity().getControl() );
		application.getRootNode().attachChild( ( (ModelBasedPhysicalExtension) this.getAbstractPhysicalExtension() ).getModelSpatial() ); 
	}
	
	@Override
	public void updateDriverPosition(AbstractVehicle drivenVehicle) {
		this.getHumanPhysicalActivity().getControl().setPhysicsLocation( drivenVehicle.getVehiclePhysicalActivity().getControl().getPhysicsLocation() );
		//this.getHumanPhysicalActivity().getControl().setViewDirection( drivenVehicle.getVehiclePhysicalActivity().computeVehicleForwardDirection().toVector3f() );
	}
	
	@Override
	public void updateDriverPosition(AbstractAircraft drivenAircraft) {
		this.getHumanPhysicalActivity().getControl().setPhysicsLocation( drivenAircraft.getAircraftPhysicalActivity().getControl().getPhysicsLocation() );
	}

	private void initWeapon() {
		this.weapon = new Gun( this, this.application);
	}
	
	public static IPhysicalExtension getPlayerPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generatePlayerPhysicalExtension(_spatialPosition);
	}

	@Override
	public IndicatorType getIndicatorType() {
		return IndicatorType.MAIN_PLAYER;
	}

	@Override
	public Vector2f get2dWorldPosition() {
		final Vector3f position3d = getHumanPhysicalActivity().getControl().getPhysicsLocation();
		return ( new Vector2f( position3d.getX(), position3d.getZ() ) );
	}

	@Override
	public String getIndicatorImageName() {
		return ScreenConfig.MAIN_PLAYER_INDICATOR_IMAGE_NAME;
	}

	@Override
	public Integer[] getIndicatorImageExtension() {
		Integer[] size = new Integer[2];
		size[ 0 ] = 30;
		size[ 1 ] = 30;
		return size;
	}
	
	@Override
	public float getAngleRotation() {
		final float[] angles = new float[3]; 
		if ( isRoaming() )
			( (ModelBasedPhysicalExtension) getAbstractPhysicalExtension() ).getModelSpatial().getLocalRotation().toAngles(angles);
		else if ( isDriving() ) 
			( (ModelBasedPhysicalExtension) getDrivenVehicle().getAbstractPhysicalExtension() ).getModelSpatial().getLocalRotation().toAngles(angles);
		else if ( isFlying() ) 
			( (ModelBasedPhysicalExtension) getDrivenAircraft().getAbstractPhysicalExtension() ).getModelSpatial().getLocalRotation().toAngles(angles);
		else
			return 0.0f;
		return angles[1];
	}
	
	public int getId() {
		return 0;
	}
	
	public void setId( final int id ) {
		
	}
	
	private static Vector3f computeBullerStartPosition( final Player player, final LogicSantosApplication application ) {
		Vector3f position = application.getCameraPosition().toVector3f().clone();
		Vector3f direction = application.getCameraDirection().toVector3f().clone();
		
		final float distance = position.distance( player.getHumanPhysicalActivity().getControl().getPhysicsLocation() );
		direction.multLocal( distance );
		position.addLocal( direction );
		return position;
	}
	
}
