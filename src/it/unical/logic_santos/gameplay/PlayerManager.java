/**
 * 
 */
package it.unical.logic_santos.gameplay;


import java.util.Collection;
import java.util.List;


import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.environment.sound.EffectSoundManager;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.logging.AircraftLoggingEvent;
import it.unical.logic_santos.logging.ILogger;
import it.unical.logic_santos.logging.ILoggingEvent;
import it.unical.logic_santos.logging.LoggingManager;
import it.unical.logic_santos.logging.VehicleLoggingEvent;
import it.unical.logic_santos.mission.IMission;
import it.unical.logic_santos.mission.MissionManager;
import it.unical.logic_santos.net.AnimationCookie;
import it.unical.logic_santos.net.IRemoteCommunicator;
import it.unical.logic_santos.net.PlayerAction;
import it.unical.logic_santos.net.PlayerCookie;
import it.unical.logic_santos.net.Vector3fCookie;
import it.unical.logic_santos.net.WeaponCookie;
import it.unical.logic_santos.physics.activity.HumanAnimation;
import it.unical.logic_santos.physics.activity.HumanPhysicalActivity;
import it.unical.logic_santos.physics.activity.PhysicsSpace;
import it.unical.logic_santos.spatial_entity.AbstractAircraft;
import it.unical.logic_santos.spatial_entity.AbstractDynamicSpatialEntity;
import it.unical.logic_santos.spatial_entity.AbstractHuman;
import it.unical.logic_santos.spatial_entity.AbstractStaticSpatialEntity;
import it.unical.logic_santos.spatial_entity.AbstractVehicle;
import it.unical.logic_santos.spatial_entity.Player;

/**
 * @author Agostino
 *
 */
public class PlayerManager implements ActionListener {

	private Player player=null;
	private MissionManager missionManager=null;
	private LogicSantosApplication application=null;
	
	private boolean[] movements = new boolean[5];
	private float airTime=0.0f;
	private boolean isRunning=false;
	private boolean isAiming=false;
	private boolean isJumping=false;
	private boolean jumpActionTaken=false;
	private boolean isRemote=false;
	private float   airJumpingTime=0.0f;
	private Vector3f previousPosition=null;
	//private Vector3f previousViewDirection=null;
	private Float romaingChaseCamMaxDistance=null;
	private float hitReactionTime=0.0f;
	
	//private HashMap< AbstractHuman, Float > previousCollisionDistances=null;
	
	private PlayerCareerStatus playerCareerStatus=null;
	
	private static final int LEFT =     0;
	private static final int RIGHT =    1;
	private static final int FORWARD =  2;
	private static final int BACKWARD = 3;
	private static final int JUMP =     4;
	
	private static final float MAX_DISTANCE_VEHICLE_DRIVE_ACTION = 30.0f;
	//private static final float MAX_DISTANCE_AIRCRAFT_DRIVE_ACTION = 30.0f;
	private static final float Y_OFFSET_INIT = 5.8f + 0.0f;
	
	public  static final float MIN_HUMAN_DISTANCE_OUT_VEHICLE_POSITION = 12.0f; //30.0f;
	public  static final float MIN_VEHICLE_DISTANCE_OUT_VEHICLE_POSITION = 12.0f; //30.0f;
	public  static final float STATIC_OBJECT_MIN_DISTANCE_OUT_VEHICLE_POSITION = 12.0f; //50.0f
	public  static final float SCALE_OFFSET_POSITION_OUT_VEHICLE = 12.0f;
	public  static final float SCALE_OFFSET_POSITION_OUT_AIRCRAFT = 20.0f;
	
	private static final float MAX_TIME_HIT_REACTION = 5.0f;
	
	public PlayerManager(final Player player, final LogicSantosApplication application) {
		this.player = player;
		this.application = application;
		this.missionManager = new MissionManager( this );
		//this.previousCollisionDistances = new HashMap< AbstractHuman, Float >();
		this.playerCareerStatus = new PlayerCareerStatus();
		this.previousPosition = compute3dPositionFromMapPosition( GameplayConfig.INITIAL_MAP_PLAYER_POSITION, this.application );
		this.player.getHumanPhysicalActivity().getControl().setPhysicsLocation( 
				this.previousPosition );
		this.player.getHumanPhysicalActivity().getControl().setViewDirection( 
				this.application.getCamera().getDirection() );
		this.player.getHumanPhysicalActivity().getControl().setJumpSpeed( 0.8f );
		//this.previousViewDirection = this.player.getHumanPhysicalActivity().getControl().getViewDirection().clone();
		initMovements();
	}
	
	public void configureKeyCommandMapping() {
		application.getInputManager().addMapping("PlayerLeft",     new KeyTrigger(KeyInput.KEY_A));
		application.getInputManager().addMapping("PlayerRight",    new KeyTrigger(KeyInput.KEY_D));
		application.getInputManager().addMapping("PlayerForward",  new KeyTrigger(KeyInput.KEY_W));
		application.getInputManager().addMapping("PlayerBackward", new KeyTrigger(KeyInput.KEY_S));
		application.getInputManager().addMapping("PlayerRun",      new KeyTrigger(KeyInput.KEY_Q));
		application.getInputManager().addMapping("PlayerJump",     new KeyTrigger(KeyInput.KEY_SPACE));
		application.getInputManager().addMapping("PlayerSpeedUp",  new KeyTrigger(KeyInput.KEY_LSHIFT));
		
		application.getInputManager().addMapping("PlayerDrive",        new KeyTrigger(KeyInput.KEY_1));
		application.getInputManager().addMapping("PlayerFly",          new KeyTrigger(KeyInput.KEY_2));
		application.getInputManager().addMapping("PlayerPause",        new KeyTrigger(KeyInput.KEY_P));
		application.getInputManager().addMapping("PlayerChooseWeapon", new KeyTrigger(KeyInput.KEY_N));
		application.getInputManager().addMapping("PlayerMenu",         new KeyTrigger(KeyInput.KEY_M));
		application.getInputManager().addMapping("PlayerSubmit",       new KeyTrigger(KeyInput.KEY_RETURN));
		application.getInputManager().addMapping("PlayerWeaponScope",  new KeyTrigger(KeyInput.KEY_B));
		
		application.getInputManager().addMapping("PlayerDriveLeft",     new KeyTrigger(KeyInput.KEY_LEFT));
		application.getInputManager().addMapping("PlayerDriveRight",    new KeyTrigger(KeyInput.KEY_RIGHT));
		application.getInputManager().addMapping("PlayerDriveForward",  new KeyTrigger(KeyInput.KEY_UP));
		application.getInputManager().addMapping("PlayerDriveBackward", new KeyTrigger(KeyInput.KEY_DOWN));
		application.getInputManager().addMapping("PlayerDriveUp",       new KeyTrigger(KeyInput.KEY_W));
		application.getInputManager().addMapping("PlayerDriveDown",     new KeyTrigger(KeyInput.KEY_S));
		
		application.getInputManager().addListener(this, "PlayerLeft", "PlayerRight");
		application.getInputManager().addListener(this, "PlayerForward", "PlayerBackward");
		application.getInputManager().addListener(this, "PlayerRun");
		application.getInputManager().addListener(this, "PlayerJump");
		application.getInputManager().addListener(this, "PlayerSpeedUp");
		
		application.getInputManager().addListener(this, "PlayerDrive");
		application.getInputManager().addListener(this, "PlayerFly");
		application.getInputManager().addListener(this, "PlayerPause");
		application.getInputManager().addListener(this, "PlayerChooseWeapon");
		application.getInputManager().addListener(this, "PlayerMenu");
		application.getInputManager().addListener(this, "PlayerSubmit");
		application.getInputManager().addListener(this, "PlayerWeaponScope");
		
		application.getInputManager().addListener(this, "PlayerDriveLeft", "PlayerDriveRight");
		application.getInputManager().addListener(this, "PlayerDriveForward", "PlayerDriveBackward");
		application.getInputManager().addListener(this, "PlayerDriveUp", "PlayerDriveDown");
	}

	@Override
	public void onAction(String binding, boolean value, float tpf) {
		if ( (!player.isActive()) || 
			 ( (application.getRemoteCommunicator()!=null) && 
					 (application.getRemoteCommunicator().isMaster()) &&
					 ( (application.getRemotePlayer()==null) || 
							 (application.getRemotePlayerManager()==null) ) && 
					 (!binding.equals("PlayerMenu") ) ) )
			return;
		
		if (binding.equals("PlayerLeft"))
			movements[ LEFT ] = value;
		else if (binding.equals("PlayerRight"))
			movements[ RIGHT ] = value;
		else if (binding.equals("PlayerForward"))
			movements[ FORWARD ] = value;
		else if (binding.equals("PlayerBackward"))
			movements[ BACKWARD ] = value;
		else if (binding.equals("PlayerJump"))
			movements[ JUMP ] = value;
		else if (binding.equals("PlayerRun"))
			isRunning = value;
		
		else if ( binding.equals("PlayerDrive") && (value) ) {
			if ( player.isRoaming() )
				managePlayerDriveAction();
			else 
				managePlayerRoamingAction();
		} else if ( binding.equals("PlayerFly") && (value) ) {
			if ( player.isRoaming() )
				managePlayerFlyAction();
			else 
				managePlayerRoamingAction();
		} else if ( binding.equals("PlayerPause") && (!value) ) {
			application.pauseResumeTrigger();
		}
		
		else if ( binding.equals("PlayerMenu") )
			application.toMenu();
		
		else if ( player.isDriving() ) {
			if (binding.equals("PlayerDriveLeft"))
				player.getDrivenVehicle().getVehiclePhysicalActivity().onDriveLeftAction(value);
			else if (binding.equals("PlayerDriveRight"))
				player.getDrivenVehicle().getVehiclePhysicalActivity().onDriveRightAction(value);
			else if (binding.equals("PlayerDriveForward"))
				player.getDrivenVehicle().getVehiclePhysicalActivity().onDriveForwardAction(value);
			else if (binding.equals("PlayerDriveBackward"))
				player.getDrivenVehicle().getVehiclePhysicalActivity().onDriveBackwardAction(value);
		}
		
		else if ( player.isFlying() ) {
			if (binding.equals("PlayerDriveLeft")) {
				if ( !application.getGameplayScreen().getWeaponChooserScreen().isVisible() )
					player.getDrivenAircraft().getAircraftPhysicalActivity().onDriveLeftAction(value);
				else if ( value )
					application.getGameplayScreen().getWeaponChooserScreen().selectPreviousWeapon();
			} else if (binding.equals("PlayerDriveRight")) {
				if ( !application.getGameplayScreen().getWeaponChooserScreen().isVisible() )
					player.getDrivenAircraft().getAircraftPhysicalActivity().onDriveRightAction(value);
				else if ( value ) 
					application.getGameplayScreen().getWeaponChooserScreen().selectNextWeapon();
			} else if (binding.equals("PlayerDriveForward"))
				player.getDrivenAircraft().getAircraftPhysicalActivity().onDriveForwardAction(value);
			else if (binding.equals("PlayerDriveBackward"))
				player.getDrivenAircraft().getAircraftPhysicalActivity().onDriveBackwardAction(value);
			else if (binding.equals("PlayerDriveUp"))
				player.getDrivenAircraft().getAircraftPhysicalActivity().onDriveUpAction(value);
			else if (binding.equals("PlayerDriveDown"))
				player.getDrivenAircraft().getAircraftPhysicalActivity().onDriveDownAction(value);
			else if ( binding.equals("PlayerChooseWeapon") && (!value) && (!application.isWeaponScopeVisible()))
				application.getGameplayScreen().getWeaponChooserScreen().onChooserRequest();
			else if ( application.getGameplayScreen().getWeaponChooserScreen().isVisible() ) { 
				if (binding.equals("PlayerSubmit") && (value) )
					application.getGameplayScreen().getWeaponChooserScreen().chooseSelectedWeapon( true );
			}
			
			else if ( binding.equals("PlayerSpeedUp") ) {
				player.getDrivenAircraft().getAircraftPhysicalActivity().onSpeedUp( value );
			}
		}
		
		else if ( binding.equals("PlayerChooseWeapon") && (!value) && (!application.isWeaponScopeVisible())) {
			application.getGameplayScreen().getWeaponChooserScreen().onChooserRequest();
		}
		
		else if ( binding.equals("PlayerWeaponScope") && (!value) && player.getWeapon().hasWeaponScope() && (!application.getGameplayScreen().getWeaponChooserScreen().isVisible()) ) {
			if ( !application.isWeaponScopeVisible() ) 
				application.onWeaponScopeShow();
			else
				application.onWeaponScopeHide();
		}
		
		else if ( application.getGameplayScreen().getWeaponChooserScreen().isVisible() ) { 
			if ( binding.equals("PlayerDriveLeft") && (value) )
				application.getGameplayScreen().getWeaponChooserScreen().selectPreviousWeapon();
			else if (binding.equals("PlayerDriveRight") && (value) )
				application.getGameplayScreen().getWeaponChooserScreen().selectNextWeapon();
			else if (binding.equals("PlayerSubmit") && (value) )
				application.getGameplayScreen().getWeaponChooserScreen().chooseSelectedWeapon( true );
		}
		
		IRemoteCommunicator remoteCommunicator = application.getRemoteCommunicator();
		if ( (remoteCommunicator!=null) && (!remoteCommunicator.isMaster()) && (remoteCommunicator.isConnected()) ) {
			PlayerAction playerAction = new PlayerAction( getPlayerActionFromName( binding ), value );
			remoteCommunicator.onPlayerAction( playerAction );
		}
	}
	
	public void update(final float tpf) {
		missionManager.update(tpf);
		
		if ( player.isDriving() || player.isFlying() || (!player.isActive()) || player.getWalkerPhysicalActivity().getLifeBar().isEmpty()  ) {
			
			if ( player.getWalkerPhysicalActivity().getLifeBar().isEmpty() ) {
				player.getWalkerPhysicalActivity().getControl().setWalkDirection( Vector3f.ZERO.clone() );
				player.getWalkerPhysicalActivity().getAnimation().setAnimation( HumanAnimation.HIT_REACTION );
				player.getHumanPhysicalActivity().updateAnimation( Vector3f.ZERO.clone(), 0.0f );
				EffectSoundManager.getInstance().onSteppingOff();
			
				if ( hitReactionTime<MAX_TIME_HIT_REACTION )
					hitReactionTime+= tpf;
				else {
					application.getRootNode().detachChild( ((ModelBasedPhysicalExtension) player.getAbstractPhysicalExtension()).getModelSpatial() );
					application.getFlyByCamera().setEnabled( false );
					application.getGameplayScreen().onWastedText();
					hitReactionTime=0.0f;
				}
			
				//player.setIsActive( false );
			}
			return;
		}
		
		Vector3f camDir  = application.getCamera().getDirection().mult(0.2f);
		Vector3f camLeft = application.getCamera().getLeft().mult(0.2f);
		if ( isRemote )
			camDir = player.getHumanPhysicalActivity().getControl().getViewDirection().clone().normalize().mult(0.2f);
		camDir.setY(0.0f);
		camLeft.setY(0.0f);
		
		Vector3f viewDirection = camDir.clone();
		
		Vector3f walkDirection = Vector3f.ZERO.clone();
		
		if (movements[ LEFT ])
			walkDirection.addLocal(camLeft);
		if (movements[ RIGHT ])
			walkDirection.addLocal(camLeft.negate());
		if (movements[ FORWARD ])
			walkDirection.addLocal(camDir);
		if ( movements[ BACKWARD ])
			walkDirection.addLocal(camDir.negate());
		if (movements[ JUMP ]) {
			//player.getHumanPhysicalActivity().getControl().jump();
			isJumping=true;
			jumpActionTaken=false;
		}
		
		if ( isRunning )
			walkDirection.multLocal( HumanPhysicalActivity.RUNNING_SCALE );
		
		final boolean collision = ( checkPlayerCollisionWithHumans() || checkPlayerCollisionWithAircrafts() );
		if ( collision )
			walkDirection = Vector3f.ZERO.clone();
		
		if ( application.getGraphicalTerrain().checkOutOfBound( player.getHumanPhysicalActivity().getControl().getPhysicsLocation() ) &&
				( this.previousPosition!=null ) )
			player.getHumanPhysicalActivity().getControl().setPhysicsLocation( this.previousPosition );
		else if ( !collision )
			this.previousPosition = player.getHumanPhysicalActivity().getControl().getPhysicsLocation().clone();
		
		player.getHumanPhysicalActivity().getControl().setWalkDirection(walkDirection);
		player.getHumanPhysicalActivity().getControl().setViewDirection(viewDirection);
		updateAnimationSetting(tpf);
		
		if (walkDirection.lengthSquared()!=0)
			player.getHumanPhysicalActivity().getControl().setViewDirection(walkDirection);
		
		player.getHumanPhysicalActivity().getLifeBar().increaseByTime( tpf );
		
		/* updating ANIMATION */
		if ( !player.getHumanPhysicalActivity().getControl().onGround() )
			airTime+=tpf;
		else
			airTime=0.0f;
		if ( !collision )
			player.getHumanPhysicalActivity().updateAnimation( walkDirection, airTime );
		
		final boolean isInWater = ( (player.getHumanPhysicalActivity().getControl().getPhysicsLocation().getY()-Y_OFFSET_INIT)<=
										application.getGraphicalWater().getWaterHeight() );
		if ( walkDirection.length()!=0 ) {
			if ( isRunning )
				EffectSoundManager.getInstance().onRunningOn( isInWater );
			else
				EffectSoundManager.getInstance().onSteppingOn( isInWater );
		} else
			EffectSoundManager.getInstance().onSteppingOff();
		
		IRemoteCommunicator remoteCommunicator = application.getRemoteCommunicator();
		final Vector3f currentPlayerViewDirection = 
				player.getHumanPhysicalActivity().getControl().getViewDirection().clone();
		final Vector3f currentPlayerWalkDirection = 
				player.getHumanPhysicalActivity().getControl().getWalkDirection().clone();
		
		if ( (remoteCommunicator!=null) && 
			 (remoteCommunicator.isConnected()) &&
			 (!remoteCommunicator.isMaster()) &&
			 (!isRemote)
			 /*(!currentPlayerViewDirection.equals(previousViewDirection))*/ ) {
			
			Vector3fCookie playerViewDirectionCookie = new Vector3fCookie();
			Vector3fCookie playerWalkDirectionCookie = new Vector3fCookie();
			playerViewDirectionCookie.vector = currentPlayerViewDirection;
			playerWalkDirectionCookie.vector = currentPlayerWalkDirection;
					
			remoteCommunicator.onPlayerDirectionUpdate( playerViewDirectionCookie, playerWalkDirectionCookie );
		}
		
		//previousViewDirection = currentPlayerViewDirection;
		
	}
	
	public Vector3f getPlayerPosition() {
		return player.getHumanPhysicalActivity().getControl().getPhysicsLocation();
	}
	
	public PlayerCareerStatus getPlayerCareerStatus() {
		return playerCareerStatus;
	}
	
	public void loadCareerStatus() {
		this.playerCareerStatus.loadStatus();
	}
	
	public void storeCereerStatus() {
		this.playerCareerStatus.storeStatus();
	}
	
	public void onCollisionWithAbstractHuman( AbstractHuman other ) {
		if ( this.previousPosition!=null ) 
			player.getHumanPhysicalActivity().getControl().setPhysicsLocation( this.previousPosition );
	}
	
	public void onCollisionWithAbstractAircraft( AbstractAircraft other ) {
		if ( this.previousPosition!=null ) 
			player.getHumanPhysicalActivity().getControl().setPhysicsLocation( this.previousPosition );
	}
	
	public void insertPlayerIntoWorld() {
		player.setIsActive( true );
		player.setIsRoaming();
		player.getHumanPhysicalActivity().getControl()
			.setPhysicsLocation( compute3dPositionFromMapPosition( GameplayConfig.INITIAL_MAP_PLAYER_POSITION, this.application ) );
		PhysicsSpace.getInstance().getSpace().add( player.getHumanPhysicalActivity().getControl() );
	}
	
	public void insertPlayerIntoWorld( final Vector2f playerPosition ) {
		player.getHumanPhysicalActivity().getLifeBar().setFull();
		hitReactionTime=0.0f;
		player.setIsActive( true );
		player.setIsRoaming();
		player.getHumanPhysicalActivity().getControl()
			.setPhysicsLocation( compute3dPositionFromMapPosition( playerPosition, this.application ) );
		PhysicsSpace.getInstance().getSpace().add( player.getHumanPhysicalActivity().getControl() );
	}
	
	public void removePlayerFromWorld() {
		player.setIsActive( false );
		if ( player.isDriving() ) {

			player.getDrivenVehicle().getVehiclePhysicalActivity().stopVehicle();
			player.getDrivenVehicle().getVehiclePhysicalActivity().setDriver(null);
			player.setIsRoaming();
			
		} else if ( player.isFlying() ) {

			player.getDrivenAircraft().getAircraftPhysicalActivity().stopAircraft();
			player.getDrivenAircraft().getAircraftPhysicalActivity().setDriver(null);
			player.setIsRoaming();
			
		}
		player.getHumanPhysicalActivity().getControl()
			.setPhysicsLocation( getFartherPosition() );
		PhysicsSpace.getInstance().getSpace().remove( player.getHumanPhysicalActivity().getControl() );
		
	}
	
	public MissionManager getMissionManager() {
		return missionManager;
	}
	
	public void onMissionComplete( final IMission mission ) {
		if ( !mission.isComplete() )
			return;
		playerCareerStatus.earn( mission.getGain() );
		application.onMissionCleared();
	}
	
	public void setIsAiming( final boolean isAiming ) {
		this.isAiming = isAiming;
	}
	
	public void onPlayerAction( final PlayerAction action ) {
		if ( action.getAction()==PlayerAction.PLAYER_CHOOSE_WEAPON ) {
			player.setWeapon( IWeapon.createNewWeaponFromId( action.weaponType, player, application ) );
		} else if ( action.getAction()!=PlayerAction.PLAYER_SHOOT ) {
			onAction( getPlayerActionName( action ), action.isStarting(), 0.0f );
		} else {
			//player.shoot();
			IBullet bullet = player.getWeapon().shoot();
			bullet.initActivity( action.bulletStartPosition.vector, action.bulletDirection.vector );
			application.getBulletManager().addBullet(bullet);
			application.getPlayerManager().getPlayer().getWantedStars().onShootStart( bullet );
			
			EffectSoundManager.getInstance().onWeaponShoot( player.getWeapon() );
		}
	}
	
	public void onPlayerDirectionUpdate( final Vector3fCookie[] cookies ) {
		player.getHumanPhysicalActivity().getControl().setViewDirection(cookies[0].vector.clone());
		player.getHumanPhysicalActivity().getControl().setWalkDirection(cookies[1].vector.clone());
	}
	
	public LogicSantosApplication getApplication() {
		return application;
	}
	
	public Vector3f getPlayerPreviousPosition() {
		return previousPosition;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public PlayerCookie getCookie() {
		PlayerCookie cookie = new PlayerCookie();
		
		cookie.previousPosition = previousPosition;
		
		cookie.id = player.getId();
		cookie.weaponCookie = new WeaponCookie( player.getWeapon() );
		cookie.isActive = player.isActive();
		cookie.isRunning = isRunning;
		cookie.wantedStarsCookie = player.getWantedStars().getCookie();
		cookie.isVisible = (!( player.isDriving() || player.isFlying() ));
		
		final HumanPhysicalActivity playerPhysicalActivity = player.getHumanPhysicalActivity();
		cookie.position = playerPhysicalActivity.getControl().getPhysicsLocation().clone();
		cookie.viewDirection = playerPhysicalActivity.getControl().getViewDirection().clone();
		cookie.walkDirection = playerPhysicalActivity.getControl().getWalkDirection().clone();
		cookie.lifeBarCookie = playerPhysicalActivity.getLifeBar().getCookie();
		cookie.animationCookie = new AnimationCookie( playerPhysicalActivity.getAnimation() );
		
		for( int i=0; i<this.movements.length; ++i )
			cookie.movements[i] = this.movements[i];
		
		return cookie;
	}
	
	public void setCookie( final PlayerCookie cookie ) {
		
		this.previousPosition = cookie.previousPosition;
		
		player.setId(cookie.id);
		player.setWeapon(cookie.weaponCookie.getWeapon(player, application));
		player.setIsActive(cookie.isActive);
		isRunning = cookie.isRunning;
		player.getWantedStars().setCookie(cookie.wantedStarsCookie);
		if ( cookie.isVisible )
			player.setRoamingVisibility();
		else
			player.setDrivingInvisibility();
		
		final HumanPhysicalActivity playerPhysicalActivity = player.getHumanPhysicalActivity();
		playerPhysicalActivity.getControl().setPhysicsLocation(cookie.position.clone());
		playerPhysicalActivity.getControl().setViewDirection(cookie.viewDirection.clone());
		playerPhysicalActivity.getControl().setWalkDirection(cookie.walkDirection.clone());
		playerPhysicalActivity.getLifeBar().setCookie(cookie.lifeBarCookie);
		playerPhysicalActivity.getAnimation().setAnimation(cookie.animationCookie.getAnimation());
		// TODO: update animation ?!
		for( int i=0; i<this.movements.length; ++i )
			this.movements[i] = cookie.movements[i];
		
	}
	
	public boolean isRemote() {
		return isRemote;
	}
	
	public void setRemote(boolean isRemote) {
		this.isRemote = isRemote;
	}

	private void initMovements() {
		for(int i=0; i<movements.length; ++i)
			movements[i] = false;
	}
	
	private void managePlayerDriveAction() {
		IRemoteCommunicator remoteCommunicator = application.getRemoteCommunicator();
		if ( player.isDriving() || player.isFlying() || ( (remoteCommunicator!=null) && (!remoteCommunicator.isMaster()) ) || player.getHumanPhysicalActivity().getLifeBar().isEmpty() )
			return;
		
		List< AbstractVehicle > vehicles = application.getVehicles();
		float distance;
		float minDistance = Float.MAX_VALUE;
		AbstractVehicle minDistanceVehicle=null;
		for( AbstractVehicle v: vehicles ) {
			
			distance = this.player.getHumanPhysicalActivity().getControl().getPhysicsLocation()
					.distance( v.getVehiclePhysicalActivity().getControl().getPhysicsLocation() );
			
			if ( (!v.getVehiclePhysicalActivity().getLifeBar().isEmpty()) &&
					(distance <= MAX_DISTANCE_VEHICLE_DRIVE_ACTION) &&
					(distance < minDistance) ) {
				minDistance = distance;
				minDistanceVehicle = v;
			}
		}
		
		if ( (minDistanceVehicle != null) && 
				(application.getWalkerTrafficManager().addVehicleDriverOut( minDistanceVehicle, player )) ) {
			player.setDrivingInvisibility();
			player.setIsDriving( minDistanceVehicle );
			application.initCamToChaseVehicle( minDistanceVehicle );
			
			minDistanceVehicle.getVehiclePhysicalActivity().setIsDriven(true);
			minDistanceVehicle.getVehiclePhysicalActivity().setDriver( player );
			
			ILogger logger = LoggingManager.getDefaultLogger();
			ILoggingEvent event = new VehicleLoggingEvent( 
					VehicleLoggingEvent.STOLE_VEHICLE_EVENT
					);
			logger.log( event );
			
			System.out.println("DRIVE VEHICLE!");
		}
	}
	
	private void managePlayerFlyAction() {
		IRemoteCommunicator remoteCommunicator = application.getRemoteCommunicator();
		if ( player.isFlying() || player.isDriving() || ( (remoteCommunicator!=null) && (!remoteCommunicator.isMaster()) ) || player.getHumanPhysicalActivity().getLifeBar().isEmpty() )
			return;
		
		AbstractAircraft aircraft = application.getHelicopter();
		final float distance = this.player.getHumanPhysicalActivity().getControl().getPhysicsLocation()
							.distance( aircraft.getAircraftPhysicalActivity().getControl().getPhysicsLocation() );
		
		if ( distance <= MAX_DISTANCE_VEHICLE_DRIVE_ACTION ) {
			player.setDrivingInvisibility();
			player.setIsFlying( aircraft );
			application.initCamToChaseAircraft( aircraft );
			
			aircraft.getAircraftPhysicalActivity().setIsDriven(true);
			aircraft.getAircraftPhysicalActivity().setDriver( player );
			aircraft.getAircraftPhysicalActivity().onEngineOn();
			
			ChaseCamera chaseCam = application.getChaseCam();
			if ( chaseCam!=null ) {
				romaingChaseCamMaxDistance = chaseCam.getMaxDistance();
				chaseCam.setMaxDistance( romaingChaseCamMaxDistance + 100.0f );
			}
			
			EffectSoundManager.getInstance().onHelicopterOn();
			
			ILogger logger = LoggingManager.getDefaultLogger();
			ILoggingEvent event = new AircraftLoggingEvent( 
					AircraftLoggingEvent.STOLE_AIRCRAFT_EVENT
					);
			logger.log( event );
		}
	}
	
	private void managePlayerRoamingAction() {
		if ( player.isRoaming() || player.getHumanPhysicalActivity().getLifeBar().isEmpty() )
			return;
		
		Vector3f newPlayerPosition = null;
		if ( player.isDriving() )
			newPlayerPosition = computeNewPlayerPositionAfterDriving( player, player.getDrivenVehicle(), application );
		else if ( player.isFlying() )
			newPlayerPosition = computeNewPlayerPositionAfterFlying( player, player.getDrivenAircraft(), application );
		
		if ( newPlayerPosition==null )
			return;
		
		if ( player.isDriving() ) {
			player.getDrivenVehicle().getVehiclePhysicalActivity().stopVehicle();
			//player.getDrivenVehicle().getVehiclePhysicalActivity().setIsDriven( false );
			player.getDrivenVehicle().getVehiclePhysicalActivity().setDriver(null);
		} else if ( player.isFlying() ) {
			if ( !player.getDrivenAircraft().getAircraftPhysicalActivity().isNearToGround() )
				return;
			player.getDrivenAircraft().getAircraftPhysicalActivity().onEngineOff();
			player.getDrivenAircraft().getAircraftPhysicalActivity().stopAircraft();
			player.getDrivenAircraft().getAircraftPhysicalActivity().setIsDriven( false );
			player.getDrivenAircraft().getAircraftPhysicalActivity().setDriver(null);
			
			if ( romaingChaseCamMaxDistance!=null )
				application.getChaseCam().setMaxDistance( romaingChaseCamMaxDistance );
			
			EffectSoundManager.getInstance().onHelicopterOff();
			
			ILogger logger = LoggingManager.getDefaultLogger();
			ILoggingEvent event = new AircraftLoggingEvent( 
					AircraftLoggingEvent.LEAVE_AIRCRAFT_EVENT
					);
			logger.log( event );
		}
		
		application.initCamToChasePlayer( player.getDrivenVehicle() );
		player.setRoamingVisibility();
		player.getHumanPhysicalActivity().getControl().setPhysicsLocation( newPlayerPosition );
		player.setIsRoaming();
	}
	
	private boolean checkPlayerCollisionWithHumans() {
		List< AbstractHuman > humans = application.getHumans();
		AbstractHuman[] humansArray = new AbstractHuman[ humans.size() ];
		
		int i=0;
		//float distance;
		//float previousDistance;
		for( AbstractHuman h: humans ) {
			humansArray[i] = h;
			i++;
		}
		
		for( i=0; i<humansArray.length; ++i ) {
			AbstractHuman currentHuman = humansArray[i];
			
			if ( (currentHuman!=player) && AbstractHuman.collisionHumans( player, currentHuman ) ) {
				
				onCollisionWithAbstractHuman( currentHuman );
				return true;
				/*
				distance = player.getHumanPhysicalActivity().getControl().getPhysicsLocation()
						.distance( currentHuman.getHumanPhysicalActivity().getControl().getPhysicsLocation() );
				
				if ( previousCollisionDistances.containsKey( currentHuman ) ) {
					
					previousDistance = previousCollisionDistances.get( currentHuman );
					if ( distance!=previousDistance )
						previousCollisionDistances.replace( currentHuman, new Float(distance) );
					if ( distance<=previousDistance ) {
						return true;
					}
					
				} else {
					previousCollisionDistances.put( currentHuman , distance );
					return true;
				}
				*/
			} //else 
				//previousCollisionDistances.remove( currentHuman );
		}
		return false;
	}
	
	private boolean checkPlayerCollisionWithAircrafts() {
		List< AbstractAircraft > aircrafts = application.getAircrafts();
		AbstractAircraft[] aircraftsArray = new AbstractAircraft[ aircrafts.size() ];
		
		int i=0;
		for( AbstractAircraft a: aircrafts ) {
			aircraftsArray[i] = a;
			i++;
		}
		
		for( i=0; i<aircraftsArray.length; ++i ) {
			AbstractAircraft currentAircraft = aircraftsArray[i];
		
			if ( AbstractAircraft.collisionAircraftHuman( currentAircraft, player ) ) {
				
				onCollisionWithAbstractAircraft( currentAircraft );
				return true;
			}
		}
		return false;
	}
	
	private void updateAnimationSetting( final float tpf ) {
		
		HumanAnimation animation=null;
		
		/* jumping status */
		if ( isJumping ) {
			
			animation = HumanAnimation.JUMPING;
			airJumpingTime+=tpf;
			
			if ( airJumpingTime>=3.0f ) {
				
				isJumping=false;
				jumpActionTaken=false;
				airJumpingTime=0.0f;
				
			} else {
				
				if ( (airJumpingTime>=1.0f) && (!jumpActionTaken) ) {
					player.getHumanPhysicalActivity().getControl().jump();
					jumpActionTaken=true;
				}
				player.getHumanPhysicalActivity().getAnimation().setAnimation( animation );
				return;
			}
		}
		
		/* idle status */
		else if ( player.getHumanPhysicalActivity().getControl().getWalkDirection().lengthSquared()==0.0f ) {
			if ( (player.getWeapon()!=null) )
				animation = HumanAnimation.IDLE_AIMING;
			else if ( isAiming )
				animation = HumanAnimation.FIGHT_IDLE;
			else
				animation = HumanAnimation.IDLE;
		}
		
		/* running status */
		else if ( isRunning )
			animation = HumanAnimation.RUNNING;
		
		/* walking status */
		else {
			if ( (player.getWeapon()!=null) )
				animation = HumanAnimation.WALKING_AIMING;
			else
				animation = HumanAnimation.WALKING;
		}
		
		if ( animation!=null )
			player.getHumanPhysicalActivity().getAnimation().setAnimation( animation );
	}
	
	private static Vector3f computeNewPlayerPositionAfterDriving( Player player, AbstractDynamicSpatialEntity vehicle, LogicSantosApplication application ) { // TODO: calculate the right position to enable the player
		final Vector3f vehicleDirection = player.getDrivenVehicle().getVehiclePhysicalActivity().computeVehicleForwardDirection().toVector3f();
		Vector2f playerNew2dOffsetPosition = new Vector2f( vehicleDirection.getX(), vehicleDirection.getZ() );
		playerNew2dOffsetPosition.rotateAroundOrigin( (float) (Math.PI/2.0f), true );
		playerNew2dOffsetPosition.multLocal( SCALE_OFFSET_POSITION_OUT_VEHICLE );
		Vector3f newPlayerPosition = player.getDrivenVehicle().getVehiclePhysicalActivity().getControl().getPhysicsLocation().clone();
		newPlayerPosition.setX( newPlayerPosition.getX()+playerNew2dOffsetPosition.getX() );
		newPlayerPosition.setZ( newPlayerPosition.getZ()+playerNew2dOffsetPosition.getY() );
		newPlayerPosition.setY( application.getLogicSantosWorld().getTerrainModel().getHeight( newPlayerPosition.getX(), newPlayerPosition.getZ()) +  Y_OFFSET_INIT );
		
		/* check collisions position out vehicle */
		List< AbstractHuman > humans = application.getVisibleHumans();
		List< AbstractVehicle > vehicles = application.getVehicles();
		Collection< AbstractStaticSpatialEntity > staticObjects = application.getLogicSantosWorld().getStaticSpatialEntities();
		
		for( AbstractHuman h: humans )
			if ( (h!=player) && 
					(newPlayerPosition.distance( h.getHumanPhysicalActivity().getControl().getPhysicsLocation() )<MIN_HUMAN_DISTANCE_OUT_VEHICLE_POSITION) )
				return null;
		
		for( AbstractVehicle v: vehicles )
			if ( (v!=vehicle) && 
					(newPlayerPosition.distance( v.getVehiclePhysicalActivity().getControl().getPhysicsLocation() )<MIN_VEHICLE_DISTANCE_OUT_VEHICLE_POSITION) )
				return null;
		// TODO add check collisions STATIC OBJECTS !!!
		;
		
		BoundingBox box = new BoundingBox( newPlayerPosition, 
				ModelingConfig.HUMAN_CAPSULE_COLLISION_SHAPE_RADIUS, 
				ModelingConfig.HUMAN_CAPSULE_COLLISION_SHAPE_HEIGHT, 
				ModelingConfig.HUMAN_CAPSULE_COLLISION_SHAPE_RADIUS );
		
		//final Vector2f newPosition2dPlayer = new Vector2f( newPlayerPosition.getX(), newPlayerPosition.getZ() );
		for( AbstractStaticSpatialEntity obj: staticObjects ) {
			
			CollisionResults results=new CollisionResults();
			( (ModelBasedPhysicalExtension) obj.getAbstractPhysicalExtension() ).getModelSpatial().getWorldBound().collideWith( box, results );
			
			if ( results.size()>0 )
				return null;
			
			/*final Vector3f positionObject = obj.getSpatialTranslation().toVector3f();
			final Vector2f position2dObject = new Vector2f( positionObject.getX(), positionObject.getZ() );
			
			if ( newPosition2dPlayer.distance( position2dObject )<STATIC_OBJECT_MIN_DISTANCE_OUT_VEHICLE_POSITION ) 
				return null;*/
		}
		
		return newPlayerPosition;
	}
	
	private static Vector3f computeNewPlayerPositionAfterFlying( Player player, AbstractDynamicSpatialEntity aircraft, LogicSantosApplication application ) { // TODO: calculate the right position to enable the player
		final Vector3f aircraftDirection = player.getDrivenAircraft().getAircraftPhysicalActivity().getControl().getViewDirection();
		Vector2f playerNew2dOffsetPosition = new Vector2f( aircraftDirection.getX(), aircraftDirection.getZ() );
		playerNew2dOffsetPosition.rotateAroundOrigin( (float) (Math.PI/2.0f), true );
		playerNew2dOffsetPosition.multLocal( SCALE_OFFSET_POSITION_OUT_AIRCRAFT );
		Vector3f newPlayerPosition = player.getDrivenAircraft().getAircraftPhysicalActivity().getControl().getPhysicsLocation().clone();
		newPlayerPosition.setX( newPlayerPosition.getX()+playerNew2dOffsetPosition.getX() );
		newPlayerPosition.setZ( newPlayerPosition.getZ()+playerNew2dOffsetPosition.getY() );
		newPlayerPosition.setY( application.getLogicSantosWorld().getTerrainModel().getHeight( newPlayerPosition.getX(), newPlayerPosition.getZ()) +  Y_OFFSET_INIT );
		
		/* check collisions position out vehicle */
		List< AbstractHuman > humans = application.getVisibleHumans();
		List< AbstractVehicle > vehicles = application.getVehicles();
		Collection< AbstractStaticSpatialEntity > staticObjects = application.getLogicSantosWorld().getStaticSpatialEntities();
		
		for( AbstractHuman h: humans )
			if ( (h!=player) && 
					(newPlayerPosition.distance( h.getHumanPhysicalActivity().getControl().getPhysicsLocation() )<MIN_HUMAN_DISTANCE_OUT_VEHICLE_POSITION) )
				return null;
		
		for( AbstractVehicle v: vehicles )
			if ( (v!=aircraft) && 
					(newPlayerPosition.distance( v.getVehiclePhysicalActivity().getControl().getPhysicsLocation() )<MIN_VEHICLE_DISTANCE_OUT_VEHICLE_POSITION) )
				return null;
		// TODO add check collisions STATIC OBJECTS !!!
		;
		
		BoundingBox box = new BoundingBox( newPlayerPosition, 
				ModelingConfig.HUMAN_CAPSULE_COLLISION_SHAPE_RADIUS, 
				ModelingConfig.HUMAN_CAPSULE_COLLISION_SHAPE_HEIGHT, 
				ModelingConfig.HUMAN_CAPSULE_COLLISION_SHAPE_RADIUS );
		
		//final Vector2f newPosition2dPlayer = new Vector2f( newPlayerPosition.getX(), newPlayerPosition.getZ() );
		for( AbstractStaticSpatialEntity obj: staticObjects ) {
			
			CollisionResults results=new CollisionResults();
			( (ModelBasedPhysicalExtension) obj.getAbstractPhysicalExtension() ).getModelSpatial().getWorldBound().collideWith( box, results );
			
			if ( results.size()>0 )
				return null;
			
			/*final Vector3f positionObject = obj.getSpatialTranslation().toVector3f();
			final Vector2f position2dObject = new Vector2f( positionObject.getX(), positionObject.getZ() );
			
			if ( newPosition2dPlayer.distance( position2dObject )<STATIC_OBJECT_MIN_DISTANCE_OUT_VEHICLE_POSITION ) 
				return null;*/
		}
		
		return newPlayerPosition;
	}
	
	private static Vector3f compute3dPositionFromMapPosition( final Vector2f position2d, final LogicSantosApplication application ) {
		float height = application.getGraphicalTerrain().getTerrainModel()
				.getHeight( position2d.getX(), position2d.getY() );
		height += Y_OFFSET_INIT;
		
		return ( new Vector3f( position2d.getX(), height, position2d.getY() ) );
	}
	
	private static Vector3f getFartherPosition() {
		return ( new Vector3f( 100000.0f, 100000.0f, 100000.0f ) );
	}
	
	private static byte getPlayerActionFromName( final String actionName ) {
		if (actionName.equals("PlayerLeft"))
			return PlayerAction.PLAYER_LEFT;
		else if (actionName.equals("PlayerRight"))
			return PlayerAction.PLAYER_RIGHT;
		else if (actionName.equals("PlayerForward"))
			return PlayerAction.PLAYER_FORWARD;
		else if (actionName.equals("PlayerBackward"))
			return PlayerAction.PLAYER_BACKWARD;
		else if (actionName.equals("PlayerJump"))
			return PlayerAction.PLAYER_JUMP;
		else if (actionName.equals("PlayerRun"))
			return PlayerAction.PLAYER_RUN;
		else if (actionName.equals("PlayerShoot"))
			return PlayerAction.PLAYER_SHOOT;
		return PlayerAction.NULL_ACTION;
	}
	
	private static String getPlayerActionName( final PlayerAction action ) {
		if ( action.getAction()==PlayerAction.PLAYER_LEFT )
			return "PlayerLeft";
		else if ( action.getAction()==PlayerAction.PLAYER_RIGHT )
			return "PlayerRight";
		else if ( action.getAction()==PlayerAction.PLAYER_FORWARD )
			return "PlayerForward";
		else if ( action.getAction()==PlayerAction.PLAYER_BACKWARD )
			return "PlayerBackward";
		else if ( action.getAction()==PlayerAction.PLAYER_JUMP )
			return "PlayerJump";
		else if ( action.getAction()==PlayerAction.PLAYER_RUN )
			return "PlayerRun";
		else if ( action.getAction()==PlayerAction.PLAYER_SHOOT )
			return "PlayerShoot";
		return "";
	}
}
