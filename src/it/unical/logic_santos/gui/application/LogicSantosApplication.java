/**
 * 
 */
package it.unical.logic_santos.gui.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import com.jme3.util.BufferUtils;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import it.unical.logic_santos.collision.ICollisionArena;
import it.unical.logic_santos.controls.ControlsConfig;
import it.unical.logic_santos.controls.CursorShifter;
import it.unical.logic_santos.controls.IPickerCamera;
import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.editor.application.CanvasEditorApplication;
import it.unical.logic_santos.environment.sound.AmbientSoundManager;
import it.unical.logic_santos.environment.sound.EffectSoundManager;
import it.unical.logic_santos.environment.sound.HumanSoundManager;
import it.unical.logic_santos.environment.sound.ISoundManager;
import it.unical.logic_santos.environment.sound.VehicleSoundManager;
import it.unical.logic_santos.gameplay.BulletManager;
import it.unical.logic_santos.gameplay.GameplayConfig;
import it.unical.logic_santos.gameplay.Gun;
import it.unical.logic_santos.gameplay.PlayerManager;
import it.unical.logic_santos.gameplay.TelescopeCamera;
import it.unical.logic_santos.gui.environment.GraphicalExplosionEffectManager;
import it.unical.logic_santos.gui.environment.GraphicalLightEnvironment;
import it.unical.logic_santos.gui.environment.GraphicalSky;
import it.unical.logic_santos.gui.environment.GraphicalWater;
import it.unical.logic_santos.gui.environment.StaticSpatialEntityEffectViewer;
import it.unical.logic_santos.gui.environment.StaticSpatialEntityViewer;
import it.unical.logic_santos.gui.roads_network.RoadsNetworkPlatform;
import it.unical.logic_santos.gui.screen.GameplayScreen;
import it.unical.logic_santos.gui.screen.LoadingScreen;
import it.unical.logic_santos.gui.screen.MainMenuScreen;
import it.unical.logic_santos.gui.terrain.GraphicalRoadsNetworkTerrain;
import it.unical.logic_santos.gui.terrain.GraphicalTerrain;
import it.unical.logic_santos.io.controls.WiiAreaNetwork;
import it.unical.logic_santos.io.universe.FileWorldReader;
import it.unical.logic_santos.net.IRemoteCommunicator;
import it.unical.logic_santos.net.PlayerAction;
import it.unical.logic_santos.net.PlayerCookie;
import it.unical.logic_santos.net.TCPClientCommunicator;
import it.unical.logic_santos.net.TCPServerCommunicator;
import it.unical.logic_santos.net.Vector3fCookie;
import it.unical.logic_santos.net.WorldCookie;
import it.unical.logic_santos.physics.activity.AircraftPhysicalActivity;
import it.unical.logic_santos.roads_network.PathsNetwork;
import it.unical.logic_santos.roads_network.RoadsNetwork;
import it.unical.logic_santos.spatial_entity.AbstractAircraft;
import it.unical.logic_santos.spatial_entity.AbstractBuilding;
import it.unical.logic_santos.spatial_entity.AbstractHuman;
import it.unical.logic_santos.spatial_entity.AbstractStaticSpatialEntity;
import it.unical.logic_santos.spatial_entity.AbstractTree;
import it.unical.logic_santos.spatial_entity.AbstractVehicle;
import it.unical.logic_santos.spatial_entity.Helicopter;
import it.unical.logic_santos.spatial_entity.Player;
import it.unical.logic_santos.terrain.modeling.AbstractHeightMapModel;
import it.unical.logic_santos.toolkit.math.MathGameToolkit;
import it.unical.logic_santos.toolkit.math.Vector2D;
import it.unical.logic_santos.toolkit.math.Vector3D;
import it.unical.logic_santos.traffic.PolicemanTrafficManager;
import it.unical.logic_santos.traffic.VehicleTrafficManager;
import it.unical.logic_santos.traffic.WalkerTrafficManager;
import it.unical.logic_santos.universe.AbstractWorld;
import it.unical.logic_santos.universe.LogicSantosWorld;
import it.unical.logic_santos.universe.SpatialEntityGenerator;

/**
 * @author Agostino
 *
 */
public class LogicSantosApplication extends SimpleApplication implements ICollisionArena, IPickerCamera, ScreenController {
	
	
	public static AssetManager ASSET_MANAGER_APPLICATION=null;
	
	protected AbstractWorld logicSantosWorld=null;
	protected RoadsNetwork roadsNetwork=null;
	protected PathsNetwork pathsNetwork=null;
	public SpatialEntityGenerator spatialEntityGenerator=null;
	
	protected VehicleTrafficManager   vehicleTrafficManager=null;
	protected WalkerTrafficManager    walkerTrafficManager=null;
	protected PolicemanTrafficManager policemanTrafficManager=null;
	
	protected BulletManager bulletManager=null;
	
	GraphicalTerrain graphicalTerrain=null;
	RoadsNetworkPlatform roadsNetwokrPlatform=null;
	GraphicalRoadsNetworkTerrain graphicalRoadsNetworkTerrain=null;
	GraphicalSky graphicalSky=null;
	GraphicalLightEnvironment graphicalEnvironmentLight=null;
	GraphicalWater graphicalWater=null;
	GraphicalExplosionEffectManager graphicalExplosionEffectManager=null;
	GameplayScreen gameplayScreen=null;
	MainMenuScreen mainMenuScreen=null;
	LoadingScreen loadingScreen=null;
	
	private TelescopeCamera telescopeCamera=null;
	private Picture targetPointerPicture=null;
	private Picture targetAimingPointerPicture=null;
	
	protected StaticSpatialEntityViewer staticSpatialEntityViewer=null;
	private   StaticSpatialEntityEffectViewer staticSpatialEntityEffectViewer=null;
	
	private Player player=null;
	private ChaseCamera chaseCam=null;
	private PlayerManager playerManager=null;
	
	private Helicopter helicopter=null;
	
	
	protected CursorShifter cursorShifter=null;
	
	Vector3f precCamPosition=null;
	float camTerrainDistance = 3.0f;
	
	private Lock lock = new ReentrantLock();
	private Condition simpleInitCondition = lock.newCondition();
	private boolean simpleInitFlag = false;
	private boolean isInPause=false;
	private boolean loadingComplete=false;
	private boolean assetsLoadingScreenRefresh=false;
	private boolean aimingPointer=false;
	private LogicSantosApplication thisApplication=null;
	
	private IRemoteCommunicator remoteCommunicator=null;
	private Player remotePlayer=null;
	private PlayerManager remotePlayerManager=null;
	
	private ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
	private Future<Void> loadFuture=null;
	
	
	public LogicSantosApplication() {
		logicSantosWorld = new LogicSantosWorld();
		roadsNetwork = new RoadsNetwork(logicSantosWorld.getCollisionEngine(), rootNode, this.isInEditingStatus());
		pathsNetwork = new PathsNetwork(logicSantosWorld.getCollisionEngine(), rootNode, this.isInEditingStatus());
		this.thisApplication = this;
	}

	@Override
	public void simpleInitApp() {
		loadingScreen = new LoadingScreen( this );
		loadingScreen.initComponents();
		loadingScreen.showLoadingScreen();
		
		GraphicalRootNode.getInstance().init(rootNode);
		LogicSantosApplication.ASSET_MANAGER_APPLICATION = assetManager;
		
		if ( isInEditingStatus() )
			initApp();
		else {
			/* hide running information */
			setDisplayStatView( false );
			setDisplayFps( false );
		}
		
		ISoundManager.initSoundManagers( this );
		if ( isInEditingStatus() )
			ISoundManager.setVolumeSoundManagers( 0.0f );
		else
			AmbientSoundManager.getInstance().playMenuSound();
		BufferUtils.setTrackDirectMemoryEnabled(true);
	}
	
	Callable< Void > loadingCallable = new Callable< Void >() {
		@Override
		public Void call() throws Exception {
			final boolean editingStatus = (thisApplication instanceof CanvasEditorApplication);
			
			/* loading City World from City File... */
			if ( !editingStatus ) {
				setLoadingProgress( 0.0f, "" );
				FileWorldReader fileWorldReader = new FileWorldReader( thisApplication );
				fileWorldReader.readWorld(thisApplication.getLogicSantosWorld());
			}
			setLoadingProgress( 1.0f, "City World creation" );
			return null;
		}
	};
	
	public void initApp() {
		
		flyCam.setMoveSpeed(flyCam.getMoveSpeed()*100f);
		cam.setLocation(new Vector3f(100.0f, 100.0f, 100.0f));
		inputManager.setCursorVisible(true);
		
		inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addMapping("Aim", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addMapping("Pause", new MouseButtonTrigger(KeyInput.KEY_P));
		inputManager.addMapping("AimingPointer", new KeyTrigger(KeyInput.KEY_V));
							
		inputManager.addListener(actionListener, "Shoot");
		inputManager.addListener(actionListener, "Aim");
		inputManager.addListener(actionListener, "Pause");
		inputManager.addListener(actionListener, "AimingPointer");
		inputManager.addMapping("MoveXT", new MouseAxisTrigger(MouseInput.AXIS_X, true));
		inputManager.addMapping("MoveYT", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		inputManager.addMapping("MoveXF", new MouseAxisTrigger(MouseInput.AXIS_X, false));
		inputManager.addMapping("MoveYF", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		inputManager.addListener(analogListener, "MoveXT");
		inputManager.addListener(analogListener, "MoveYT");
		inputManager.addListener(analogListener, "MoveXF");
		inputManager.addListener(analogListener, "MoveYF");
		
		staticSpatialEntityViewer = new StaticSpatialEntityViewer( thisApplication );
		staticSpatialEntityViewer.setFrustumFar( 1000.0f );
		
		staticSpatialEntityEffectViewer = new StaticSpatialEntityEffectViewer( thisApplication );
		staticSpatialEntityEffectViewer.setFrustumFar( 300.0f );
		
		//logicSantosWorld = new LogicSantosWorld();
		spatialEntityGenerator = new SpatialEntityGenerator(logicSantosWorld.getTerrainModel());
		
		graphicalTerrain = new GraphicalTerrain(thisApplication, (AbstractHeightMapModel)logicSantosWorld.getTerrainModel(), false);
		//roadsNetwokrPlatform = new RoadsNetworkPlatform(this, RoadsNetworkPlatformConfig.POSITION_PLATFORM);
		graphicalRoadsNetworkTerrain = new GraphicalRoadsNetworkTerrain(thisApplication, false);
		graphicalSky = new GraphicalSky(thisApplication);
		graphicalEnvironmentLight = new GraphicalLightEnvironment(thisApplication);
		graphicalWater = new GraphicalWater(thisApplication, graphicalEnvironmentLight);
		graphicalExplosionEffectManager = new GraphicalExplosionEffectManager(thisApplication);
		
		try {
			graphicalTerrain.loadComponents();
			//roadsNetwokrPlatform.loadComponents();
			graphicalRoadsNetworkTerrain.loadComponents();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		graphicalSky.loadComponents();
		graphicalEnvironmentLight.loadComponents();
		graphicalWater.loadComponents();
		
		graphicalTerrain.attachComponentsToGraphicalEngine();
		  //roadsNetwokrPlatform.attachComponentsToGraphicalEngine();
		graphicalRoadsNetworkTerrain.attachComponentsToGraphicalEngine();
		graphicalSky.attachComponentsToGraphicalEngine();
		graphicalEnvironmentLight.attachComponentsToGraphicalEngine();
		graphicalWater.attachComponentsToGraphicalEngine();
		
		
		logicSantosWorld.getTerrainModel().addMixedTerrainModel(graphicalRoadsNetworkTerrain.getTerrainModel());
		
		cursorShifter = new CursorShifter(thisApplication, logicSantosWorld.getCollisionEngine(), logicSantosWorld.getTerrainModel());
		
		//Vector2D treePosition = new Vector2D(-550.0f, 550.0f);
		//ISpatialEntity tree = spatialEntityGenerator.generateNewSpatialEntity(ClassicTree.class, treePosition);
		//logicSantosWorld.addSpatialEntity(tree);
		//logicSantosWorld.addSpatialEntity(cityKernel);
		
		//if (tree.getAbstractPhysicalExtension() instanceof ModelBasedPhysicalExtension) {
		//	Spatial treeModelSpatial = ((ModelBasedPhysicalExtension) tree.getAbstractPhysicalExtension()).getModelSpatial();
		//	rootNode.attachChild(treeModelSpatial);
		//}
		
		/*if (cityKernel.getAbstractPhysicalExtension() instanceof ModelBasedPhysicalExtension) {
			Spatial cityKernelModelSpatial = ((ModelBasedPhysicalExtension) cityKernel.getAbstractPhysicalExtension()).getModelSpatial();
			rootNode.attachChild(cityKernelModelSpatial);
		}*/
		
		final boolean editingStatus = (thisApplication instanceof CanvasEditorApplication);
		
		/* loading City World from City File... */
		/*if ( !editingStatus ) {
			FileWorldReader fileWorldReader = new FileWorldReader( thisApplication );
			fileWorldReader.readWorld(thisApplication.getLogicSantosWorld());
			configureStaticSpatialEntityViewer();
		}*/
		
		if ( isInEditingStatus() ) {
			FileWorldReader fileWorldReader = new FileWorldReader( this );
			fileWorldReader.readWorldFromEditor(this.getLogicSantosWorld());
		}
		attachStaticSpatialEntityToGraphics();
		if ( !isInEditingStatus() )
			configureStaticSpatialEntityViewers();

		
		if ( !editingStatus ) {
			
			graphicalExplosionEffectManager.initComponents();
			
			/* init PHISICS SPACE */
			it.unical.logic_santos.physics.activity.PhysicsSpace.setLogicSantosApplication(thisApplication);
			addStaticSpatialEntityToPhysicsSpace();
			
			/* init PLAYER */
			player = new Player( new Vector3D( 0.0f, logicSantosWorld.getTerrainModel().getHeight(0.0f, 0.0f), 0.0f), thisApplication );
			player.getHumanPhysicalActivity().initActivity();
			rootNode.attachChild( ((ModelBasedPhysicalExtension) player.getAbstractPhysicalExtension()).getModelSpatial() );
			
			/* init PLAYER MANAGER */
			playerManager = new PlayerManager(player, thisApplication);
			playerManager.configureKeyCommandMapping();
			playerManager.loadCareerStatus();
			player.setManager(playerManager);
			
			/* init GAMEPLAY SCREEN */
			gameplayScreen = new GameplayScreen( thisApplication );
			gameplayScreen.initComponents();
			
			/* init WALKER TRAFFIC MANAGER */
			bulletManager = new BulletManager( thisApplication );
			
			/* init VEHICLE TRAFFIC MANAGER */
			vehicleTrafficManager = new VehicleTrafficManager(roadsNetwork, logicSantosWorld.getCollisionEngine(), thisApplication);
			vehicleTrafficManager.initComponents();
			
			/* init WALKER TRAFFIC MANAGER */
			walkerTrafficManager = new WalkerTrafficManager(pathsNetwork, logicSantosWorld.getCollisionEngine(), thisApplication);
			walkerTrafficManager.initComponents();
			
			/* init POLICEMAN TRAFFIC MANAGER */
			policemanTrafficManager = new PolicemanTrafficManager(pathsNetwork, logicSantosWorld.getCollisionEngine(), thisApplication);
			policemanTrafficManager.initComponents();
			policemanTrafficManager.addObserver( player.getWantedStars() );
				
			/* TODO: just to test!!! */
			//Vector3f position = GraphicalTerrainConfig.TERRAIN_TRANSLATION.toVector3f();// RoadsNetworkPlatformConfig.POSITION_PLATFORM.toVector3f();
			//position.setY(100.0f);
			//player.getHumanPhysicalActivity().getControl().setPhysicsLocation(position);
		}
	
		/* init HELICOPTER */
		if ( !editingStatus )
			initHelicopter();
		
		/* init CAM */
		cam.setFrustumFar(3000f);
		precCamPosition = cam.getLocation().clone();
		if ( !editingStatus ) {
			//flyCam.setEnabled(false); 
			
			Spatial playerSpatial = ( (ModelBasedPhysicalExtension) player.getAbstractPhysicalExtension() ).getModelSpatial();
			chaseCam = new ChaseCamera(cam, playerSpatial, inputManager);
			initCamToChasePlayer(null);
			initChaseCamera(chaseCam, player);
			updatePlayerChaseCameraLookAtOffset( chaseCam, player );
			
			telescopeCamera = new TelescopeCamera( cam );
		}
		
		/* init GRAPHICAL INDICATORS */
		initTargetPointer();
		//System.out.println(logicSantosWorld.getCalendar().getHashCalendar());
	
		/* USED TO TEST */
		if ( !editingStatus )
			;//addThingsForTest();
		
		/* SIGNAL INIT CONDITION */
		lock.lock();
		simpleInitFlag = true;
		simpleInitCondition.signalAll();
		lock.unlock();
		
		loadingScreen.hideLoadingScreen();
		
		if ( !editingStatus ) {
			/* init MAIN MENU SCREEN */
			mainMenuScreen = new MainMenuScreen( thisApplication );
			mainMenuScreen.initComponents();
			mainMenuScreen.startMenu();
		}
		
	}
	
	public void setLoadingProgress( final float progress, final String loadingText ) {
		enqueue(() -> {
			loadingScreen.setLoadingProgress( progress, loadingText );
			return null;
		});
	}
	
	Spatial model = null;
	@Override
	public void simpleUpdate(float tpf) {
		
		if ( !isInEditingStatus() ) {
		if ( !loadingScreen.isAssetsLoadingComplete() ) {
			
			if ( loadFuture==null )
				loadFuture = exec.submit( loadingCallable );
			if ( loadFuture.isDone() ) {
				return;
			} else
				return;
			
		} else if ( !assetsLoadingScreenRefresh ) {
			assetsLoadingScreenRefresh=true;
			return;
		} else if ( !loadingComplete ) {
			//loadingScreen.hideLoadingScreen();
			loadingComplete = true;
			initApp();
			setLoadingProgress( 1.0f, "" );
		}
		}
		
		if ( this.isInPause() )
			return;
		super.simpleUpdate(tpf);
		
		Vector3f currCamPosition = cam.getLocation();
		float terrainHeight;
		//if (graphicalRoadsNetworkTerrain.isPointInside(currCamPosition.getX(), currCamPosition.getZ())) {
		//	terrainHeight = graphicalRoadsNetworkTerrain.getTerrainModel().getHeight(currCamPosition.getX(), currCamPosition.getZ());
		//	if (terrainHeight < graphicalRoadsNetworkTerrain.getRoadsHeight())
		//		terrainHeight = logicSantosWorld.getTerrainModel().getHeight(currCamPosition.getX(), currCamPosition.getZ());
		//} else
			terrainHeight = logicSantosWorld.getTerrainModel().getHeight(currCamPosition.getX(), currCamPosition.getZ());
		/*
		if ((currCamPosition.getY()-camTerrainDistance) < terrainHeight)
			cam.setLocation(precCamPosition.clone());
		else
			precCamPosition = currCamPosition.clone();
		*/
		
		if (currCamPosition.getY() < (terrainHeight+15.0f))
			currCamPosition.setY(terrainHeight + 15.0f);
		//cam.setLocation(currCamPosition);
		
		//cam.lookAt(controlVehicle.getPhysicsLocation(), Vector3f.UNIT_Y);
		
		bulletManager.update(tpf);
		
		vehicleTrafficManager.update(tpf);
		walkerTrafficManager.update(tpf);
		policemanTrafficManager.update(tpf);
		
		playerManager.update(tpf);
		updatePlayerChaseCameraLookAtOffset( chaseCam, player ); 
		
		if ( (remotePlayer!=null) && (remotePlayerManager!=null) )
			remotePlayerManager.update(tpf);
		
		helicopter.getAircraftPhysicalActivity().update(tpf);
		
		graphicalExplosionEffectManager.update(tpf);
		staticSpatialEntityViewer.update(tpf);
		staticSpatialEntityEffectViewer.update(tpf);
		
		logicSantosWorld.getCalendar().getDayClock().update(tpf);
		
		gameplayScreen.update(tpf);
		
		remoteCommunicatorUpdate(tpf);
		
		/* UPDATE SOUND EFFECTS */
		listener.setLocation( player.getHumanPhysicalActivity().getControl().getPhysicsLocation().clone() );
		listener.setRotation( cam.getRotation() );
		AmbientSoundManager.getInstance().update(tpf, this);
		HumanSoundManager.getInstance().update(tpf, this);
		VehicleSoundManager.getInstance().update(tpf, this);
		
		if ( aimingPointer ) {
			final Vector2f cursorPosition = inputManager.getCursorPosition();
			targetAimingPointerPicture.setPosition( 
					cursorPosition.getX()-(ControlsConfig.TARGET_AIMING_POINTER_WIDTH/2.0f), 
					cursorPosition.getY()-(ControlsConfig.TARGET_AIMING_POINTER_HEIGTH/2.0f) );
		}
		/*
		Spatial model = assetManager.loadModel("Models/PalmTree/Coconut Tree.obj");
		model.setLocalTranslation(0.0f, 100.0f, 0.0f);
		rootNode.attachChild(model);
		*/
		
		//GraphicalLoader.getInstance().attachSpatials();
		//treeModel.setLocalTranslation(tree.getSpatialTranslation().toVector3f());
		//treeModel.setLocalTranslation(treeModel.getLocalTranslation().getX()+0.03f, treeModel.getLocalTranslation().getY(), treeModel.getLocalTranslation().getZ());
		//System.out.println(treeModel.getLocalTranslation());
	}
	
	@Override
	public void stop() {
		WiiAreaNetwork.getInstance().finalize();
		closeRemoteCommunicator();
		playerManager.storeCereerStatus();
		super.stop();
		System.gc();
	}
	
	@Override
	public void destroy() {
		closeRemoteCommunicator();
		playerManager.storeCereerStatus();
		super.destroy();
		closeAndAwaitLoadingExecutor( exec );
	}
	
	private void closeAndAwaitLoadingExecutor( ExecutorService pool ) {
		pool.shutdown();
		
		try {
			if ( !pool.awaitTermination( 10, TimeUnit.SECONDS ) )
				pool.shutdownNow();
		} catch (InterruptedException e) {
			pool.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}
	
	public static void main(String[] args) {
		LogicSantosApplication logicSantosApp = new LogicSantosApplication();
		logicSantosApp.start();
	}
	
	@Override
	public Vector3D getTranslation() {
		Vector3f terrainTranslation = graphicalTerrain.getTerrainQuad().getLocalTranslation();
		return new Vector3D(terrainTranslation.getX(), terrainTranslation.getY(), terrainTranslation.getZ());
	}
	
	@Override
	public float getWidthSize() {
		return graphicalTerrain.getTerrainQuad().getTerrainSize()*2.0f;
	}

	@Override
	public float getDepthSize() {
		return graphicalTerrain.getTerrainQuad().getTerrainSize()*2.0f;
	}

	@Override
	public float getXExtension() {
		return graphicalTerrain.getTerrainQuad().getTerrainSize();
	}

	@Override
	public float getZExtension() {
		return graphicalTerrain.getTerrainQuad().getTerrainSize();
	}
	
	public GraphicalTerrain getGraphicalTerrain() {
		return graphicalTerrain;
	}
	
	public GraphicalRoadsNetworkTerrain getGraphicalRoadsNetworkTerrain() {
		return graphicalRoadsNetworkTerrain;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public List< Player > getPlayers() {
		List< Player > players = new ArrayList< Player >();
		if ( player!=null )
			players.add( player );
		if ( remotePlayer!=null )
			players.add( remotePlayer );
		return players;
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
	
	public List< Vector3f > getPlayersPositions() {
		List< Vector3f > positions = new ArrayList< Vector3f >();
		positions.add( player.getHumanPhysicalActivity().getControl().getPhysicsLocation() );
		return positions;
	}
	
	public List< AbstractHuman > getHumans() {
		List< AbstractHuman > humans = new ArrayList< AbstractHuman >();
		humans.addAll( walkerTrafficManager.getHumans() );
		humans.addAll( policemanTrafficManager.getHumans() );
		if ( player!=null )
			humans.add( player );
		if ( remotePlayer!=null )
			humans.add( remotePlayer );
		return humans;
	}
	
	public List< AbstractHuman > getVisibleHumans() {
		List< AbstractHuman > humans = new ArrayList< AbstractHuman >();
		humans.addAll( walkerTrafficManager.getHumans() );
		humans.addAll( policemanTrafficManager.getHumans() );
		if ( (player!=null) && player.isRoaming() && player.isActive() )
			humans.add( player );
		if ( (remotePlayer!=null) && remotePlayer.isRoaming() && remotePlayer.isActive() )
			humans.add( remotePlayer );
		return humans;
	}
	
	public List< AbstractVehicle > getVehicles() {
		List< AbstractVehicle > vehicles = new ArrayList< AbstractVehicle >();
		vehicles.addAll( vehicleTrafficManager.getVehicles() );
		return vehicles;
	}
	
	public Helicopter getHelicopter() {
		return helicopter;
	}
	
	public List< AbstractAircraft > getAircrafts() {
		List< AbstractAircraft > aircrafts = new ArrayList< AbstractAircraft >();
		aircrafts.add( helicopter );
		return aircrafts;
	}
	
	public AbstractHuman getPolicemanById( final int id ) {
		if ( policemanTrafficManager== null )
			return null;
		for( AbstractHuman p: policemanTrafficManager.getAllPolicemans() )
			if ( p.getWalkerPhysicalActivity().getId()==id )
				return p;
		return null;
	}
	
	public WalkerTrafficManager getWalkerTrafficManager() {
		return walkerTrafficManager;
	}
	
	public PolicemanTrafficManager getPolicemanTrafficManager() {
		return policemanTrafficManager;
	}
	
	public BulletManager getBulletManager() {
		return bulletManager;
	}
	
	public GameplayScreen getGameplayScreen() {
		return gameplayScreen;
	}
	
	public StaticSpatialEntityViewer getStaticSpatialEntityViewer() {
		return staticSpatialEntityViewer;
	}
	
	public StaticSpatialEntityViewer getStaticPathEntityViewer() {
		return null;
	}
	
	public boolean isInPause() {
		return isInPause;
	}
	
	public void pauseResumeTrigger() {
		isInPause=(!isInPause);
	}
	
	public List< Camera > getActivatedCameras() {
		List< Camera > cameras = new ArrayList< Camera >();
		if ( ((mainMenuScreen!=null) && mainMenuScreen.isVisible()) || getPlayers().isEmpty() )
			cameras.add( this.getCamera() );
		return cameras;
	}
	
	public List< AbstractStaticSpatialEntity > getBulletVisibleStaticSpatialEntities() {
		Collection< AbstractStaticSpatialEntity > staticSpatialEntities = logicSantosWorld.getStaticSpatialEntities();
		List< AbstractStaticSpatialEntity > bulletVisibleStaticSpatialEntities = new ArrayList<>();
		
		for( AbstractStaticSpatialEntity spatialEntity: staticSpatialEntities )
			if ( isBulletVisibleStaticSpatialEntity( spatialEntity ) )
				bulletVisibleStaticSpatialEntities.add( spatialEntity );
		return bulletVisibleStaticSpatialEntities;
	}

	public void clearWorld() {
		for (Iterator<Spatial> iterator = rootNode.getChildren().iterator(); iterator.hasNext();) {
			Spatial s = iterator.next();
			if (!isBaseObject(s))
				iterator.remove();
		}
	}
	
	public void toMenu() {
		mainMenuScreen.startMenu();
	}
	
	public void onMenuShow() {
		rootNode.detachChild( ((ModelBasedPhysicalExtension) player.getAbstractPhysicalExtension()).getModelSpatial() );
		gameplayScreen.onMenuShow();
		chaseCam.setEnabled( false );
		flyCam.setEnabled( false );
		cam.setLocation( compute3dCamPositionFromMapPosition( GameplayConfig.DEFAULT_MAP_MENU_CAM_POSITION, this ) );
		cam.lookAt( new Vector3f(0.0f, 0.0f, 0.0f), Vector3f.UNIT_Y );
		//staticSpatialEntityViewer.setFrustumFar( 1000.0f );
		playerManager.removePlayerFromWorld();
		player.getHumanPhysicalActivity().getLifeBar().setFull();
		player.getWalkerPhysicalActivity().clearHitReactionTime();
		player.getWantedStars().clear();
		staticSpatialEntityEffectViewer.setFrustumFar( StaticSpatialEntityViewer.INFINITE_FAR );
		staticSpatialEntityEffectViewer.forceNextUpdate();
		staticSpatialEntityViewer.forceNextUpdate();
		
		/* init dynamic world objects */
		if ( !policemanTrafficManager.getActivePolicemans().isEmpty() ) {
			policemanTrafficManager.clear();
		}
		
		helicopter.getAircraftPhysicalActivity().onEngineOff();
		helicopter.getAircraftPhysicalActivity().stopAircraft();
		helicopter.getAircraftPhysicalActivity().setDriver(null);
		helicopter.getAircraftPhysicalActivity().setIsDriven( false );
		initHelicopterPosition();
		EffectSoundManager.getInstance().onHelicopterOff();
		
		hideTargetPointer();
		
		AmbientSoundManager.getInstance().playMenuSound();
		
		
		closeRemoteCommunicator();
		if ( remotePlayerManager!=null ) {
			remotePlayerManager.removePlayerFromWorld();
			remotePlayer.getHumanPhysicalActivity().finalizeActivity();
			rootNode.detachChild( ((ModelBasedPhysicalExtension) remotePlayer.getAbstractPhysicalExtension()).getModelSpatial() );
			remotePlayerManager = null;
			remotePlayer = null;
		}
	}
	
	public void onMenuHide() {
		cam.setRotation( new Quaternion() );
		playerManager.insertPlayerIntoWorld();
		flyCam.setEnabled( true );

		Spatial playerSpatial = ( (ModelBasedPhysicalExtension) player.getAbstractPhysicalExtension() ).getModelSpatial();
		chaseCam = new ChaseCamera(cam, playerSpatial, inputManager);
		
		initCamToChasePlayer(null);
		//initChaseCamera(chaseCam, player);
		updatePlayerChaseCameraLookAtOffset(chaseCam, player);
		//staticSpatialEntityViewer.setFrustumFar( StaticSpatialEntityViewer.DEFAULT_FAR );
		gameplayScreen.onMenuHide();
		rootNode.attachChild( ((ModelBasedPhysicalExtension) player.getAbstractPhysicalExtension()).getModelSpatial() );
		staticSpatialEntityEffectViewer.setFrustumFar( 300.0f );
		staticSpatialEntityEffectViewer.forceNextUpdate();
		staticSpatialEntityViewer.forceNextUpdate();
		showTargetPointer();
		
		if ( (remoteCommunicator!=null) && (!remoteCommunicator.isMaster()) )
			player.setWeapon( new Gun( player, playerManager.getApplication() ) );
		
		AmbientSoundManager.getInstance().stopMenuSound();
	}
	
	private boolean isBaseObject(final Spatial s) {
		return ( (s.equals(graphicalTerrain.getTerrainQuad())) || 
				(s.equals(graphicalRoadsNetworkTerrain.getTerrainQuad())) ||
				(s.equals(graphicalSky.getSkySpatial())) );
			
	}
	
	public String getSettingsString() {
		return settings.toString();
	}
	
	public void waitSimpleInit() {
		lock.lock();
		
		while(!this.simpleInitFlag) {
			try {
				simpleInitCondition.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		lock.unlock();
	}
	
	private ActionListener actionListener = new ActionListener() {

		@Override
		public void onAction(String name, boolean keyPressed, float tpf) {
			if (name.equals("Shoot") && (!keyPressed)) {
				//Quaternion q = ((ModelBasedPhysicalExtension) c1.getAbstractPhysicalExtension()).getModelSpatial().getLocalRotation();
				//((VehiclePhysicalActivity) c1.getPhysicalActivity()).getControl().setLinearVelocity(new Vector3f(50, -10.0f, 50));
				//((VehiclePhysicalActivity) c1.getPhysicalActivity()).getControl().setGravity(new Vector3f(0.0f, -10.0f, 0.0f));
				manageClickEvent();
				//controlVehicle.accelerate(10.0f);
				//vehicleTrafficManager.update(tpf);
				/*
				Ray ray = new Ray(new Vector3D(cam.getLocation()), new Vector3D(cam.getDirection()));
				
				System.out.println("CAM ORIGIN: " + cam.getLocation().toString());
				System.out.println("CAM DIRECT: " + cam.getDirection().toString());
				
				if (tree != null) {
					if (ray.collide(tree.getAbstractPhysicalExtension().getBoundingVolume(), null))
						System.out.println("Collision with BoundingBox");
					else
						System.out.println("Not Collisions");
				}
				
				if (terrainChunkModel instanceof AbstractHeightMapModel) {
					
					TerrainCollisionResults collisionResults = new TerrainCollisionResults();
					if (ray.collide((AbstractHeightMapModel)terrainChunkModel, collisionResults))
						System.out.println("Collision with Terrain at height: " + collisionResults.getTerrainHeight());
					else
						System.out.println("Not Collisions");
				}
				*/
				//System.out.println("Cam position: " + cam.getLocation().toString());
				//CursorShifter picker = new CursorShifter(new Vector3D(cam.getLocation()), new Vector3D(cam.getDirection()), collisionEngine);
				//ISpatialEntity entity = picker.pickSpatialEntity();
				
				
				/*
				if (cursorShifter.isSpatialEntitySelected()) {
					System.out.println("Releasing");
					cursorShifter.releaseSpatialEntity();
				} else {
					System.out.println("Picking");
					cursorShifter.pickSpatialEntity();
				}
				*/
				
				/*
				if (entity instanceof ClassicTree)
					System.out.println("Collision with Classic Tree");
				if (entity == null)
					System.out.println("ciao");
				*/
			} else if ( (!isInEditingStatus()) && name.equals("Aim") && (player.isRoaming()) ) {
				// TODO: apply aim effect
				if ( (telescopeCamera!=null) && (playerManager!=null) ) {
					if ( keyPressed ) {
						telescopeCamera.aimIn();
						playerManager.setIsAiming( true );
					} else {
						telescopeCamera.aimOut();
						playerManager.setIsAiming( false );
					}
				}
					
			} else if ( (!isInEditingStatus()) && name.equals("Pause") ) {
				isInPause=true;
			}
			
			else if ( (!isInEditingStatus()) && name.equals("AimingPointer") && (chaseCam!=null) && player.isRoaming() ) {
				//chaseCam.setSmoothMotion( !chaseCam.isSmoothMotion() );
				/*if ( chaseCam.getRotationSpeed()==1.0f )
					chaseCam.setRotationSpeed( 0.1f );
				else
					chaseCam.setRotationSpeed( 1.0f );*/
				
				if ( keyPressed ) {
					chaseCam.setRotationSensitivity( 0.1f );
					chaseCam.setDragToRotate( true );
					chaseCam.setEnabled( false );
					flyCam.setDragToRotate( true );
					flyCam.setEnabled( false );
					guiNode.attachChild( targetAimingPointerPicture );
					inputManager.setCursorVisible( false );
					aimingPointer=true;
				} else {
					chaseCam.setRotationSensitivity( 5.0f );
					chaseCam.setDragToRotate( false );
					chaseCam.setEnabled( true );
					flyCam.setDragToRotate( false );
					flyCam.setEnabled( true );
					guiNode.detachChild( targetAimingPointerPicture );
					inputManager.setCursorVisible( false );
					aimingPointer=false;
				}
				
				if ( (telescopeCamera!=null) && (playerManager!=null) ) {
					if ( keyPressed ) {
						telescopeCamera.aimIn();
						playerManager.setIsAiming( true );
					} else {
						telescopeCamera.aimOut();
						playerManager.setIsAiming( false );
					}
				}
			}
			
		}
		
	};
	
	protected void manageClickEvent() {
		
		if ( !isInEditingStatus() ) {
			if ( player.isRoaming() || player.isFlying() ) {
				if ( ( (remoteCommunicator==null) || ( (remoteCommunicator!=null) && remoteCommunicator.isMaster() ) ) && 
						chaseCam.isDragToRotate() && aimingPointer )
					player.shootFromPointer();
				else
					player.shoot();
			}
		} else {
			// TODO: do actions depending on the state of the game: eg. editing, gameplay, etc.
			/*if (cursorShifter.isSpatialEntitySelected()) {
				System.out.println("Releasing");
				cursorShifter.releaseSpatialEntity();
			} else {
				System.out.println("Picking");
				cursorShifter.pickSpatialEntity();
			}*/
		}
	}
	
	private AnalogListener analogListener = new AnalogListener() {
		
		@Override
		public void onAnalog(String name, float intensity, float tpf) {
			if ( !loadingComplete )
				return;
			
			 if (name.equals("MoveXT") || name.equals("MoveYT") || name.equals("MoveXF") || name.equals("MoveYF")) {
				 cursorShifter.moveSelectedSpatialEntity();
			 }
			
		}
	};

	@Override
	public Vector2D getCursorPosition() {
		return new Vector2D(inputManager.getCursorPosition());
	}

	@Override
	public Vector3D getWorldPosition(Vector2D screenPosition, float projectionZPos) {
		return new Vector3D(cam.getWorldCoordinates(screenPosition.toVector2f(), projectionZPos));
	}

	@Override
	public Vector3D getCameraPosition() {
		return new Vector3D(cam.getLocation());
	}

	@Override
	public Vector3D getCameraDirection() {
		return new Vector3D(cam.getDirection());
	}
	
	public AbstractWorld getLogicSantosWorld() {
		return logicSantosWorld;
	}
	
	public RoadsNetwork getRoadsNetwork() {
		return roadsNetwork;
	}
	
	public PathsNetwork getPathsNetwork() {
		return pathsNetwork;
	}
	
	public AppSettings getSettings() {
		return settings;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public LoadingScreen getLoadingScreen() {
		return loadingScreen;
	}
	
	public GraphicalLightEnvironment getGraphicalEnvironmentLight() {
		return graphicalEnvironmentLight;
	}
	
	public GraphicalWater getGraphicalWater() {
		return graphicalWater;
	}
	
	public ChaseCamera getChaseCam() {
		return chaseCam;
	}
	
	public void initCamToChasePlayer( AbstractVehicle previousChaseVehicle ) {
		//chaseCam = new ChaseCamera(cam, ((ModelBasedPhysicalExtension) player.getAbstractPhysicalExtension()).getModelSpatial(), inputManager);
		//chaseCam.setSpatial(((ModelBasedPhysicalExtension) player.getAbstractPhysicalExtension()).getModelSpatial());
		flyCam.setZoomSpeed(0.0f);
		if ( previousChaseVehicle!=null )
			((ModelBasedPhysicalExtension) previousChaseVehicle.getAbstractPhysicalExtension()).getModelSpatial().removeControl(chaseCam);
		((ModelBasedPhysicalExtension) player.getAbstractPhysicalExtension()).getModelSpatial().addControl(chaseCam);
		initChaseCamera(chaseCam, player);
	}
	
	public WorldCookie getWorldCookie() {
		return null; // TODO: create and get World Cookie
	}
	
	public void initCamToChaseVehicle( AbstractVehicle vehicle ) {
		//chaseCam = new ChaseCamera(cam, ((ModelBasedPhysicalExtension) vehicle.getAbstractPhysicalExtension()).getModelSpatial(), inputManager);
		//chaseCam.setSpatial(((ModelBasedPhysicalExtension) vehicle.getAbstractPhysicalExtension()).getModelSpatial());
		flyCam.setZoomSpeed(0.0f);
		((ModelBasedPhysicalExtension) player.getAbstractPhysicalExtension()).getModelSpatial().removeControl(chaseCam);
		((ModelBasedPhysicalExtension) vehicle.getAbstractPhysicalExtension()).getModelSpatial().addControl(chaseCam);
		initChaseCamera(chaseCam);
	}
	
	public void initCamToChaseAircraft( AbstractAircraft aircraft ) {
		//chaseCam = new ChaseCamera(cam, ((ModelBasedPhysicalExtension) vehicle.getAbstractPhysicalExtension()).getModelSpatial(), inputManager);
		//chaseCam.setSpatial(((ModelBasedPhysicalExtension) vehicle.getAbstractPhysicalExtension()).getModelSpatial());
		flyCam.setZoomSpeed(0.0f);
		((ModelBasedPhysicalExtension) player.getAbstractPhysicalExtension()).getModelSpatial().removeControl(chaseCam);
		((ModelBasedPhysicalExtension) aircraft.getAbstractPhysicalExtension()).getModelSpatial().addControl(chaseCam);
		initChaseCamera(chaseCam);
	}
	
	private static void initChaseCamera( ChaseCamera chaseCamera, final Player player ) {
		updatePlayerChaseCameraLookAtOffset( chaseCamera, player );
		initChaseCamera(chaseCamera);
	}
	
	private static void initChaseCamera( ChaseCamera chaseCamera ) {
		chaseCamera.setLookAtOffset( chaseCamera.getLookAtOffset().setY(8.0f) );
		chaseCamera.setDragToRotate(false);
		//chaseCamera.setSmoothMotion(true);
		chaseCamera.setMinVerticalRotation( -MathGameToolkit.toRadiants( 45.0f ) );
		chaseCamera.setMaxDistance( chaseCamera.getMaxDistance()*2.0f );
	}
	
	private static void updatePlayerChaseCameraLookAtOffset( ChaseCamera chaseCamera, final Player player ) {
		if ( player.isRoaming() ) {
			final Vector3f playerViewDirection = player.getHumanPhysicalActivity().getControl().getViewDirection().clone().normalize();
			Vector2f lookAtOffset = new Vector2f( playerViewDirection.getX(), playerViewDirection.getZ() );
			lookAtOffset.rotateAroundOrigin( (float) (Math.PI/2.0f), false );
			lookAtOffset.multLocal( 5.0f );
			
			final Vector3f lookAtOffset3d = new Vector3f( lookAtOffset.getX(), 8.0f, lookAtOffset.getY() );
			chaseCamera.setLookAtOffset( lookAtOffset3d ); 
		} else if ( player.isDriving() ) 
			chaseCamera.setLookAtOffset( new Vector3f( 0.0f, 8.0f, 0.0f) );
		else if ( player.isFlying() ) {
			final Vector3f playerViewDirection = player.getDrivenAircraft().getAircraftPhysicalActivity().getControl().getViewDirection().clone().normalize();
			Vector2f lookAtOffset = new Vector2f( playerViewDirection.getX(), playerViewDirection.getZ() );
			lookAtOffset.rotateAroundOrigin( (float) (Math.PI/2.0f), false );
			lookAtOffset.multLocal( 30.0f );
			
			final Vector3f lookAtOffset3d = new Vector3f( lookAtOffset.getX(), 10.0f, lookAtOffset.getY() );
			chaseCamera.setLookAtOffset( lookAtOffset3d ); 
		}
		//chaseCamera.setDefaultVerticalRotation( 0.0f );
	}

	public void attachStaticSpatialEntityToGraphics() {
		Collection<AbstractStaticSpatialEntity> staticSpatialEntities=logicSantosWorld.getStaticSpatialEntities();
		for(AbstractStaticSpatialEntity spatialEntity: staticSpatialEntities) {
			if ( !isRenderedWithSpatialEntityViewer( spatialEntity ) || isInEditingStatus() ) {
				Spatial entitySpatial = ((ModelBasedPhysicalExtension) spatialEntity.getAbstractPhysicalExtension()).getModelSpatial();
				rootNode.detachChild(entitySpatial);
				rootNode.attachChild(entitySpatial);
			}
		}

	}
	
	public void addStaticSpatialEntityToPhysicsSpace() {
		Collection<AbstractStaticSpatialEntity> staticSpatialEntities=logicSantosWorld.getStaticSpatialEntities();
		for(AbstractStaticSpatialEntity spatialEntity: staticSpatialEntities) {
			
			/* make the staticSpatialEntity physical with mass 0.0f */
			RigidBodyControl control = new RigidBodyControl( 0.0f );
			Spatial spatial = ((ModelBasedPhysicalExtension) spatialEntity.getAbstractPhysicalExtension()).getModelSpatial();
			spatial.addControl( control );
			it.unical.logic_santos.physics.activity.PhysicsSpace.getInstance().getSpace().add( control );
			
		}

	}
	
	public static boolean isRenderedWithSpatialEntityViewer( AbstractStaticSpatialEntity staticEntity ) {
		return ( (staticEntity instanceof AbstractTree) || 
				( (staticEntity instanceof AbstractStaticSpatialEntity) &&
						(!(staticEntity instanceof AbstractBuilding)) ) );
	}
	
	public static boolean isRenderedWithSpatialEntityEffectViewer( AbstractStaticSpatialEntity staticEntity ) {
		return ( staticEntity instanceof AbstractBuilding );
	}
	
	public static boolean isBulletVisibleStaticSpatialEntity( AbstractStaticSpatialEntity staticEntity ) {
		return ( staticEntity instanceof AbstractBuilding );
	}
	
	public void configureStaticSpatialEntityViewers() {
		Collection<AbstractStaticSpatialEntity> staticSpatialEntities=logicSantosWorld.getStaticSpatialEntities();
		//List< Geometry > geoms = new ArrayList< Geometry >();
		for( AbstractStaticSpatialEntity spatialEntity: staticSpatialEntities ) {
			if ( isRenderedWithSpatialEntityViewer( spatialEntity ) )
				staticSpatialEntityViewer.attachSpatialEntity( spatialEntity );
			if ( isRenderedWithSpatialEntityEffectViewer( spatialEntity ) )
				staticSpatialEntityEffectViewer.attachSpatialEntity( spatialEntity );
			
			/*Spatial modelSpatial = ( (ModelBasedPhysicalExtension) spatialEntity.getAbstractPhysicalExtension() ).getModelSpatial();
			
			if ( modelSpatial instanceof Node ) {
				Node modelNode = (Node) modelSpatial;
				for( Spatial spatial: modelNode.getChildren() )
					if ( spatial instanceof Geometry )
						geoms.add( (Geometry) spatial );
			} else if ( modelSpatial instanceof Geometry )
				geoms.add( (Geometry) modelSpatial );*/
		}
		/*
		for( Geometry geo: geoms ) {
			try {
				LodGenerator lodGenerator = new LodGenerator( geo );
				//lodGenerator.bakeLods( LodGenerator.TriangleReductionMethod.PROPORTIONAL, 0.8f );
				//geo.setLodLevel( 1 );
				
				final VertexBuffer[] lods = lodGenerator.computeLods( TriangleReductionMethod.PROPORTIONAL, 0.8f );
				geo.getMesh().setLodLevels( lods );
			} catch ( Exception e ) {}
		}
		*/
	}
	
	private void initHelicopter() {
		helicopter = new Helicopter();
		
		helicopter.getAircraftPhysicalActivity().initActivity();
		initHelicopterPosition();
		logicSantosWorld.addSpatialEntity( helicopter );
		
		
		rootNode.attachChild(((ModelBasedPhysicalExtension) helicopter.getAbstractPhysicalExtension()).getModelSpatial());
		gameplayScreen.getMinimapScreen().addVisibleEntity( helicopter );
		
		helicopter.getAircraftPhysicalActivity().setApplication( this );
	}
	
	private void initHelicopterPosition() {
		if ( helicopter==null )
			return;
		
		final float height = logicSantosWorld.getTerrainModel().
				getHeight( GameplayConfig.INITIAL_MAP_HELICOPTER_POSITION.getX(), 
							GameplayConfig.INITIAL_MAP_HELICOPTER_POSITION.getY() ) + AircraftPhysicalActivity.Y_OFFSET_INIT;
		Vector3f position = new Vector3f( GameplayConfig.INITIAL_MAP_HELICOPTER_POSITION.getX(), 
											height, 
											GameplayConfig.INITIAL_MAP_HELICOPTER_POSITION.getY() );
		position.addLocal( helicopter.getSpatialEntityTranslationAdjustment().toVector3f() );
		helicopter.getAircraftPhysicalActivity().getControl().setPhysicsLocation( position );	
	}
	
	private void initTargetPointer() {
		targetPointerPicture = new Picture("HUD Picture");
		targetPointerPicture.setImage(assetManager, ControlsConfig.TARGET_POINTER_IMAGE_NAME, true);
		targetPointerPicture.setWidth(ControlsConfig.TARGET_POINTER_WIDTH);
		targetPointerPicture.setHeight(ControlsConfig.TARGET_POINTER_HEIGTH);
		targetPointerPicture.setPosition( (settings.getWidth()/2) - (ControlsConfig.TARGET_POINTER_WIDTH/2),
							(settings.getHeight()/2) - (ControlsConfig.TARGET_POINTER_HEIGTH/2) );
		guiNode.attachChild(targetPointerPicture);
		
		targetAimingPointerPicture = new Picture("HUD Picture");
		targetAimingPointerPicture.setImage(assetManager, ControlsConfig.TARGET_AIMING_POINTER_IMAGE_NAME, true);
		targetAimingPointerPicture.setWidth(ControlsConfig.TARGET_AIMING_POINTER_WIDTH);
		targetAimingPointerPicture.setHeight(ControlsConfig.TARGET_AIMING_POINTER_HEIGTH);
	}
	
	private void showTargetPointer() {
		if ( targetPointerPicture!=null )
			guiNode.attachChild(targetPointerPicture);
	}
	
	private void hideTargetPointer() {
		if ( targetPointerPicture!=null )
			guiNode.detachChild(targetPointerPicture);
	}
	
	public static Vector3f compute3dCamPositionFromMapPosition( final Vector2f position2d, final LogicSantosApplication application ) {
		float height = application.getGraphicalTerrain().getTerrainModel()
				.getHeight( position2d.getX(), position2d.getY() );
		height += 50.0f;
		
		return ( new Vector3f( position2d.getX(), height, position2d.getY() ) );
	}
	
	public void onMissionCleared() {
		gameplayScreen.onMissionCleared();
	}
	
	public void onExplosion( final Vector3f translation ) {
		graphicalExplosionEffectManager.onExplosion( translation );
	}
	
	public void onExplosion( AbstractVehicle vehicle ) {
		graphicalExplosionEffectManager.onExplosion( vehicle );
	}
	
	public void onWeaponScopeShow() {
		telescopeCamera.zoomIn();
		gameplayScreen.onWeaponScopeShow();
	}
	
	public void onWeaponScopeHide() {
		telescopeCamera.zoomOut();
		gameplayScreen.onWeaponScopeHide();
	}
	
	public boolean isWeaponScopeVisible() {
		return telescopeCamera.isZooming();
	}
	
	public void onVehicleResetPosition( final AbstractVehicle vehicle ) {
		graphicalExplosionEffectManager.removeVehicleFireEffect( vehicle );
	}
	
	public void onHosts() {
		TCPServerCommunicator serverCommunicator = new TCPServerCommunicator( this );
		if ( serverCommunicator.startServer() ) {
			remoteCommunicator = serverCommunicator;
		}
	}
	
	public void onTakePart() {
		TCPClientCommunicator clientCommunicator = new TCPClientCommunicator( this );
		if ( clientCommunicator.connect() ) {
			remoteCommunicator = clientCommunicator;
			playerManager.insertPlayerIntoWorld( GameplayConfig.INITIAL_MAP_CLIENT_PLAYER_POSITION );
			remoteCommunicator.onPlayerUpdate(playerManager.getCookie());
		}
	}
	
	public boolean isInEditingStatus() {
		return (this instanceof CanvasEditorApplication);
	}
	
	public IRemoteCommunicator getRemoteCommunicator() {
		return remoteCommunicator;
	}
	
	public Player getRemotePlayer() {
		return remotePlayer;
	}
	
	public PlayerManager getRemotePlayerManager() {
		return remotePlayerManager;
	}
	
	public WorldCookie getCookie() {
		WorldCookie cookie = new WorldCookie();
		
		if ( remoteCommunicator.isMaster() ) {
			cookie.playersCookies[WorldCookie.MASTER_PLAYER_INDEX] = this.playerManager.getCookie();
			cookie.playersCookies[WorldCookie.CLIENT_PLAYER_INDEX] = this.remotePlayerManager.getCookie();
		} else {
			cookie.playersCookies[WorldCookie.CLIENT_PLAYER_INDEX] = this.playerManager.getCookie();
			cookie.playersCookies[WorldCookie.MASTER_PLAYER_INDEX] = this.remotePlayerManager.getCookie();
		}
		
		cookie.walkerTrafficManagerCookie = this.walkerTrafficManager.getCookie();
		cookie.policemanTrafficManagerCookie = this.policemanTrafficManager.getCookie();
		cookie.vehicleTrafficManagerCookie = this.vehicleTrafficManager.getCookie();
		cookie.bulletManagerCookie = this.bulletManager.getCookie();
		
		if ( this.helicopter!=null )
			cookie.helicopterCookie = this.helicopter.getAircraftPhysicalActivity().getCookie();
		
		return cookie;
	}
	
	public void setCookie( final WorldCookie cookie ) {
		if ( (remotePlayer==null) || (remotePlayerManager==null) ) {
			int playerIndex = WorldCookie.MASTER_PLAYER_INDEX;
			if ( remoteCommunicator.isMaster() )
				playerIndex = WorldCookie.CLIENT_PLAYER_INDEX;
			createRemotePlayerManagerFromCookie(cookie.playersCookies[playerIndex]);
		}
		
		if ( remoteCommunicator.isMaster() ) {
			this.playerManager.setCookie(cookie.playersCookies[WorldCookie.MASTER_PLAYER_INDEX]);
			this.remotePlayerManager.setCookie(cookie.playersCookies[WorldCookie.CLIENT_PLAYER_INDEX]);
		} else {
			this.playerManager.setCookie(cookie.playersCookies[WorldCookie.CLIENT_PLAYER_INDEX]);
			this.remotePlayerManager.setCookie(cookie.playersCookies[WorldCookie.MASTER_PLAYER_INDEX]);
		}
		
		this.walkerTrafficManager.setCookie(cookie.walkerTrafficManagerCookie);
		this.policemanTrafficManager.setCookie(cookie.policemanTrafficManagerCookie);
		this.vehicleTrafficManager.setCookie(cookie.vehicleTrafficManagerCookie);
		this.bulletManager.setCookie(cookie.bulletManagerCookie);
		
		if ( this.helicopter!=null )
			this.helicopter.getAircraftPhysicalActivity().setCookie(cookie.helicopterCookie);
	}
	
	private void createRemotePlayerManagerFromCookie( final PlayerCookie coookie ) {
		if ( (remotePlayer!=null) && (remotePlayerManager!=null) )
			return;
			
		/* init REMOTE PLAYER */
		remotePlayer = new Player( new Vector3D( 0.0f, logicSantosWorld.getTerrainModel().getHeight(0.0f, 0.0f), 0.0f), this );
		remotePlayer.getHumanPhysicalActivity().initActivity();
		rootNode.attachChild( ((ModelBasedPhysicalExtension) remotePlayer.getAbstractPhysicalExtension()).getModelSpatial() );
		
		/* init REMOTE PLAYER MANAGER */
		remotePlayerManager = new PlayerManager(remotePlayer, this);
		remotePlayerManager.setRemote( true );
		//remotePlayerManager.configureKeyCommandMapping();
		remotePlayer.setManager(remotePlayerManager);
		remotePlayerManager.setCookie(coookie);
	}
	
	private void remoteCommunicatorUpdate( final float tpf ) {
		
		/* waiting for remote Player cookie to initialize remotePlayer and remotePlayerManager objects */
		if ( (remoteCommunicator!=null)      && 
			 (remotePlayer==null)            && 
			 (remotePlayerManager==null)     && 
			 (remoteCommunicator.isConnected()) ) {
			
			PlayerCookie playerCookie = remoteCommunicator.getNextPlayerCookie();
			while( playerCookie!=null ) {
				createRemotePlayerManagerFromCookie(playerCookie);
				playerCookie = remoteCommunicator.getNextPlayerCookie();
			}
		}
		
		/* waiting for remote Player actions, remote Player direction and World cookie send */
		if ( (remoteCommunicator!=null)  && 
		     (remotePlayer!=null)        && 
		     (remotePlayerManager!=null) && 
		     (remoteCommunicator.isConnected()) ) {
		
			/* waiting for remote Player actions */
			PlayerAction remotePlayerAction = remoteCommunicator.getNextPlayerAction();
			while( remotePlayerAction!=null ) {
				remotePlayerManager.onPlayerAction( remotePlayerAction );
				remotePlayerAction = remoteCommunicator.getNextPlayerAction();
			}
			
			/* waiting for remote Player direction */
			Vector3fCookie[] remotePlayerDirection = remoteCommunicator.getNextPlayerDirectionCookie();
			while( remotePlayerDirection!=null ) {
				remotePlayerManager.onPlayerDirectionUpdate( remotePlayerDirection );
				remotePlayerDirection = remoteCommunicator.getNextPlayerDirectionCookie();
			}
			
			/* World cookie send */
			if ( remoteCommunicator.isMaster() )
				remoteCommunicator.onWorldUpdate(tpf);	
		}
		
		/* World cookie receive */
		if ( (remoteCommunicator!=null)       && 
			 (!remoteCommunicator.isMaster()) &&
			 (remoteCommunicator.isConnected()) ) {
			
			WorldCookie worldCookie = remoteCommunicator.getNextWorldCookie();
			while( worldCookie!=null ) {
				setCookie(worldCookie);
				worldCookie = remoteCommunicator.getNextWorldCookie();
			}
		}
		
		
	}
	
	private void closeRemoteCommunicator() {
		if ( remoteCommunicator==null )
			return;
		
		if ( remoteCommunicator instanceof TCPServerCommunicator )
			( (TCPServerCommunicator) remoteCommunicator ).closeServer();
		else if ( remoteCommunicator instanceof TCPClientCommunicator )
			( (TCPClientCommunicator) remoteCommunicator ).closeClient();
		
		remoteCommunicator = null;
	}

	
	//Car c1;
	//VehicleControl controlVehicle;
	//private void addThingsForTest() {
		/*
		Spatial car = assetManager.loadModel("Models/Ferrari/Car.scene");
		car.setLocalScale(10.0f);
		car.setLocalTranslation(new Vector3f(0.0f, 100.0f, 0.0f));
		RigidBodyControl carPhy = new RigidBodyControl(1000f);
		car.addControl(carPhy);
		carPhy.setPhysicsLocation(new Vector3f(0, 100, 0));
		carPhy.setLinearVelocity(Vector3f.UNIT_X.mult(1000.0f));
		carPhy.applyCentralForce(Vector3f.UNIT_X.mult(10.0f));
		//car.getControl(RigidBodyControl.class).getCollisionShape().setScale(new Vector3f(10.0f, 10.0f, 10.0f));
		rootNode.attachChild(car);
		
		BulletAppState bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		PhysicsSpace physicsSpace = bulletAppState.getPhysicsSpace();
		
		physicsSpace.add(carPhy);
		
		Control terrPhy = new RigidBodyControl(0f);
		graphicalTerrain.getTerrainQuad().addControl(terrPhy);
		physicsSpace.add(graphicalTerrain.getTerrainQuad());
		physicsSpace.add(graphicalRoadsNetworkTerrain.getTerrainQuad());
		*/
		
		/*
		c1 = new Car();
		c1.setSpatialTranslation(new Vector3D(-650.0f, 100.0f, 850.0f));
		c1.getPhysicalActivity().init();
		rootNode.attachChild( ((ModelBasedPhysicalExtension) c1.getAbstractPhysicalExtension()).getModelSpatial() );
		*/
		/*
		Car c2 = new Car();
		c2.setSpatialTranslation(new Vector3D(100.0f,100.0f,10.0f));
		c2.getPhysicalActivity().init();
		rootNode.attachChild( ((ModelBasedPhysicalExtension) c2.getAbstractPhysicalExtension()).getModelSpatial() );
		
		Car c3 = new Car();
		c3.setSpatialTranslation(new Vector3D(300.0f,100.0f,10.0f));
		c3.getPhysicalActivity().init();
		rootNode.attachChild( ((ModelBasedPhysicalExtension) c3.getAbstractPhysicalExtension()).getModelSpatial() );
		*/
		
		/*
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        mat.getAdditionalRenderState().setWireframe(true);

        mat.setColor("Color", ColorRGBA.Red);



        //create a compound shape and attach the BoxCollisionShape for the car body at 0,1,0

        //this shifts the effective center of mass of the BoxCollisionShape to 0,-1,0

        CompoundCollisionShape compoundShape = new CompoundCollisionShape();

        BoxCollisionShape box = new BoxCollisionShape(new Vector3f(1.2f, 0.5f, 2.4f));

        compoundShape.addChildShape(box, new Vector3f(0, 1, 0));
		Node vehicleNode = new Node("vehicleNode");
		controlVehicle = new VehicleControl(compoundShape, 400);
		vehicleNode.addControl(controlVehicle);
		
		float stiffness = 60.0f;
		float compValue = .3f;
		float dampValue = .4f;
		controlVehicle.setSuspensionCompression(compValue * 2.0f * FastMath.sqr(stiffness));
		controlVehicle.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
		controlVehicle.setSuspensionStiffness(stiffness);
		controlVehicle.setMaxSuspensionForce(10000.0f);
		
		Vector3f wheelDirection = new Vector3f(0, -1, 0);
		Vector3f wheelAxle = new Vector3f(-1, 0, 0);
		float radius = 0.5f;
		float restLength = 0.3f;
		float yOff = 0.5f;
		float xOff = 1f;
		float zOff = 2f;
		
		Cylinder wheelMesh = new Cylinder(16, 16, radius, radius * 0.6f, true);
		
		Node node1 = new Node("wheel 1 node");

		Geometry wheels1 = new Geometry("wheel 1", wheelMesh);
		node1.attachChild(wheels1);
		wheels1.rotate(0, FastMath.HALF_PI, 0);
		wheels1.setMaterial(mat);
		controlVehicle.addWheel(node1, new Vector3f(-xOff, yOff, zOff),
				
		wheelDirection, wheelAxle, restLength, radius, true);

		Node node2 = new Node("wheel 2 node");
		Geometry wheels2 = new Geometry("wheel 2", wheelMesh);
 		node2.attachChild(wheels2);
 		wheels2.rotate(0, FastMath.HALF_PI, 0);
 		wheels2.setMaterial(mat);
 		controlVehicle.addWheel(node2, new Vector3f(xOff, yOff, zOff),
 		wheelDirection, wheelAxle, restLength, radius, true);

 		Node node3 = new Node("wheel 3 node");
		Geometry wheels3 = new Geometry("wheel 3", wheelMesh);
		node3.attachChild(wheels3);
		wheels3.rotate(0, FastMath.HALF_PI, 0);
		wheels3.setMaterial(mat);
		
		controlVehicle.addWheel(node3, new Vector3f(-xOff, yOff, -zOff),
		wheelDirection, wheelAxle, restLength, radius, false);

		Node node4 = new Node("wheel 4 node");
		Geometry wheels4 = new Geometry("wheel 4", wheelMesh);
		node4.attachChild(wheels4);
		wheels4.rotate(0, FastMath.HALF_PI, 0);
		wheels4.setMaterial(mat);
		controlVehicle.addWheel(node4, new Vector3f(xOff, yOff, -zOff),
		wheelDirection, wheelAxle, restLength, radius, false);


		vehicleNode.attachChild(node1);
		vehicleNode.attachChild(node2);
		vehicleNode.attachChild(node3);
		vehicleNode.attachChild(node4);
		
		controlVehicle.setGravity(new Vector3f(0.0f, -10.0f, 0.0f));
		Vector3f pos = GraphicalTerrainConfig.ROADS_NETWORK_TERRAIN_TRANSLATION.toVector3f(); pos.setY(800.0f);
		controlVehicle.getPhysicsLocation(pos);
		rootNode.attachChild(vehicleNode);
		it.unical.logic_santos.physics.activity.PhysicsSpace.getInstance().getSpace().add(controlVehicle);
		
		
	}*/
	

	@Override
	public void bind(Nifty arg0, Screen arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEndScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartScreen() {
		// TODO Auto-generated method stub
		
	}

	
}
