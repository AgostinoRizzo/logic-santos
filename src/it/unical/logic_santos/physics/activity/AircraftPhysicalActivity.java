/**
 * 
 */
package it.unical.logic_santos.physics.activity;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.environment.sound.EffectSoundManager;
import it.unical.logic_santos.gameplay.IBullet;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.roads_network.PathsNetwork;
import it.unical.logic_santos.roads_network.RoadNode;
import it.unical.logic_santos.roads_network.RoadsNetwork;
import it.unical.logic_santos.spatial_entity.AbstractAircraft;
import it.unical.logic_santos.spatial_entity.AbstractHuman;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.toolkit.math.MathGameToolkit;

/**
 * @author Agostino
 *
 */
public class AircraftPhysicalActivity implements IPhysicalActivity, AnimEventListener {

	protected AbstractAircraft owner=null;
	protected CharacterControl control=null;
	
	protected AnimChannel animationChannel=null;
	protected AnimControl animationControl=null;
	
	private List< AnimControl > animationControls=new ArrayList< AnimControl >();
	private List< AnimChannel > animationChannels=new ArrayList< AnimChannel >();
	
	protected float currentRotationSideScale=0.0f;
	private boolean isDriven=false;
	private boolean hasWalkerDriver=true;
	private boolean engineOn=false;
	private float currentPropellerRotation=0.0f;
	
	private Vector3f previousPosition=null;
	
	private AbstractHuman driver=null;
	
	protected LogicSantosApplication application=null;
	
	public static final float Y_OFFSET_INIT = 30.8f + 0.2f;
	
	private static final float STILL_GRAVITY_VALUE = 0.0f;
	private static final float UP_GRAVITY_VALUE    = -10.0f;
	private static final float DOWN_GRAVITY_VALUE  = 10.0f;
	
	private static final float DRIVE_FORWARD_SCALE     = 0.5f;
	private static final float DRIVE_BACKFORWARD_SCALE = -0.5f;
	
	private static final float DEGREE_ROTATION_PER_SEC = 30.0f; 
	
	private static final float MAX_DISTANCE_NEAR_TERRAIN = 30.0f;
	
	private static final String FLYING_ANIMATION_NAME = "Flying";
	private static final float  FLYING_ROTATION_PER_SEC = 3.0f;
	private static final float  FLYING_ROTATION_ACCELLERATION_PER_SEC = 0.5f;
	
	public AircraftPhysicalActivity( AbstractAircraft owner ) {
		this.owner = owner;
		
		/** init Control */
		CapsuleCollisionShape capsule = new CapsuleCollisionShape( ModelingConfig.AIRCRAFT_CAPSULE_COLLISION_SHAPE_RADIUS, 
																	ModelingConfig.AIRCRAFT_CAPSULE_COLLISION_SHAPE_HEIGHT );
		this.control = new CharacterControl(capsule, 0.05f);
		
		/** init AnimationChannel and AnimationControl */
		if ( animationControl==null ) {
			
			Node aircraftNode = (Node) ( (ModelBasedPhysicalExtension) owner.getAbstractPhysicalExtension() ).getModelSpatial();
			animationControl = findAnimControl( aircraftNode, null );
			
			animationControls.clear();
			animationChannels.clear();
			
			AnimControl newAnimationControl=null;
			do {
				newAnimationControl = findNextAnimControl( aircraftNode, animationControls );
				if ( newAnimationControl!=null )
					animationControls.add( newAnimationControl );
			} while( newAnimationControl!=null );
			
			
			for( AnimControl ac: animationControls ) {
				ac.addListener( this );
				animationChannels.add( ac.createChannel() );
			}
			
		}
	}
	
	@Override
	public void initActivity() {

		getOwnerSpatial().addControl( this.control );
		
		setLocalTranslationComponents( getOwnerSpatial() );
		
		PhysicsSpace.getInstance().getSpace().add( this.control );
		PhysicsSpace.getInstance().getCollisionEngine().addCollidable(this.owner.getAbstractPhysicalExtension().getBoundingVolume());
	}

	@Override
	public void finalizeActivity() {
		getOwnerSpatial().removeControl(this.control);
		PhysicsSpace.getInstance().getSpace().remove(this.control);
		getOwnerSpatial().removeControl( this.control );
	}

	@Override
	public ISpatialEntity getSpatialEntityOwner() {
		return owner;
	}

	@Override
	public void setSpatialEntityOwner(ISpatialEntity spatialEntity) {
		if (spatialEntity instanceof AbstractAircraft)
			this.owner = (AbstractAircraft) spatialEntity;
	}

	@Override
	public void setInRoadsNetwork(RoadsNetwork roadsNetwork, RoadNode startNode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInRoadsNetwork(RoadsNetwork roadsNetwork) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStartRoadsNetworkNode(RoadNode startNode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInPathsNetwork(PathsNetwork pathsNetwork, RoadNode startNode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInPathsNetwork(PathsNetwork pathsNetwork) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStartPathsNetworkNode(RoadNode startNode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTranslation() {
		control.setPhysicsLocation(owner.getSpatialTranslation().toVector3f());
	}

	@Override
	public void update(float tpf) {
		if ( this.isDriven() ) {
			keepInsideWorldPlatform();
			if ( this.driver!=null )
				this.driver.updateDriverPosition( this.owner );
			
			updateRotation(tpf);
			rotatePropellers(tpf);
			return;
		}
		keepInsideWorldPlatform();
		rotatePropellers(tpf);
	}

	@Override
	public void onShootReaction(IBullet bullet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onExplosionReaction(IBullet bullet) {
		// TODO Auto-generated method stub
		
	}
	
	public CharacterControl getControl() {
		return control;
	}
	
	public boolean isDriven() {
		return isDriven;
	}
	
	public void setIsDriven( final boolean isDriven ) {
		this.isDriven = isDriven;
		if ( this.isDriven ) {
			hasWalkerDriver=false;
		} else {
			driver = null;
			hasWalkerDriver=true;
		}
		stopAircraft();
	}
	
	public boolean hasDriver() {
		return (this.driver!=null);
	}
	
	public void setDriver( AbstractHuman driver ) {
		this.driver = driver;
	}
	
	public void onEngineOn() {
		engineOn=true;
		//setAnimation( FLYING_ANIMATION_NAME );
	}
	
	public void onEngineOff() {
		engineOn=false;
		this.control.setWalkDirection( Vector3f.ZERO.clone() );
		this.control.setGravity( DOWN_GRAVITY_VALUE );
		keepInsideWorldPlatform();
		//for( AnimChannel ac: animationChannels )
		//	ac.setLoopMode( LoopMode.DontLoop );
	}
	
	public void onDriveUpAction( final boolean onPress ) {
		if ( !engineOn )
			return;
		
		if ( onPress )
			this.control.setGravity( UP_GRAVITY_VALUE );
		else
			this.control.setGravity( STILL_GRAVITY_VALUE );
		keepInsideWorldPlatform();
	}
	
	public void onDriveDownAction( final boolean onPress ) {
		if ( !engineOn )
			return;
		
		if ( onPress )
			this.control.setGravity( DOWN_GRAVITY_VALUE );
		else
			this.control.setGravity( STILL_GRAVITY_VALUE );
		keepInsideWorldPlatform();
	}
	
	public void onDriveForwardAction( final boolean onPress ) {
		if ( !engineOn )
			return;
		
		if ( onPress )
			this.control.setWalkDirection( 
					this.control.getViewDirection().mult( DRIVE_FORWARD_SCALE ) );
		else
			this.control.setWalkDirection( Vector3f.ZERO.clone() );
		keepInsideWorldPlatform();
	}
	
	public void onDriveBackwardAction( final boolean onPress ) {
		if ( !engineOn )
			return;
		
		if ( onPress )
			this.control.setWalkDirection( 
					this.control.getViewDirection().mult( DRIVE_BACKFORWARD_SCALE ) );
		else
			this.control.setWalkDirection( Vector3f.ZERO.clone() );
		keepInsideWorldPlatform();
	}
	
	public void onDriveRightAction( final boolean onPress ) {
		if ( !engineOn )
			return;
		
		currentRotationSideScale= ( onPress ) ? -1.0f : 0.0f;
		keepInsideWorldPlatform();
	}
	
	public void onDriveLeftAction( final boolean onPress ) {
		if ( !engineOn )
			return;
		
		currentRotationSideScale= ( onPress ) ? 1.0f : 0.0f;
		keepInsideWorldPlatform();
	}
	
	public void stopAircraft() {
		if ( !engineOn )
			return;
		
		this.control.setWalkDirection( Vector3f.ZERO.clone() );
		keepInsideWorldPlatform();
	}
	
	public void onSpeedUp( final boolean up ) {
		final Vector3f moveDirection=  ( up )
			? this.control.getWalkDirection().mult( 2.0f )
			: this.control.getWalkDirection().mult( 0.5f );
		this.control.setWalkDirection( moveDirection );
	}
	
	public boolean isNearToGround() {
		final Vector3f currentPosition = this.control.getPhysicsLocation();
		final float currentHeight = currentPosition.getY();
		final float terrainHeight = application.getGraphicalTerrain().getTerrainModel().
				getHeight( currentPosition.getX(), currentPosition.getZ() );
		
		return ( currentHeight<=(terrainHeight+MAX_DISTANCE_NEAR_TERRAIN) );
	}
	
	public AbstractAircraft getAbstractHumanOwner() {
		return (this.owner);
	}
	
	public void setApplication(LogicSantosApplication application) {
		this.application = application;
	}
	
	public AircraftCookie getCookie() {
		AircraftCookie cookie = new AircraftCookie();
		
		cookie.currentRotationSideScale = this.currentRotationSideScale;
		cookie.isDriven = this.isDriven;
		cookie.hasWalkerDriver = this.hasWalkerDriver;
		cookie.engineOn = this.engineOn;
		cookie.currentPropellerRotation = this.currentPropellerRotation;
		
		cookie.gravity = this.control.getGravity();
		
		cookie.previousPositionCookie.vector = this.previousPosition.clone();
		cookie.currentPositionCookie.vector = this.control.getPhysicsLocation().clone();
		cookie.viewDirectionCookie.vector = this.control.getViewDirection().clone();
		cookie.moveDirectionCookie.vector = this.control.getWalkDirection().clone();
		
		return cookie;
	}
	
	public void setCookie( final AircraftCookie cookie ) {
		this.currentRotationSideScale = cookie.currentRotationSideScale;
		this.isDriven = cookie.isDriven;
		this.hasWalkerDriver = cookie.hasWalkerDriver;
		this.engineOn = cookie.engineOn;
		if ( this.engineOn )
			EffectSoundManager.getInstance().onHelicopterOn();
		else
			EffectSoundManager.getInstance().onHelicopterOff();
		this.currentPropellerRotation = cookie.currentPropellerRotation;
		
		this.control.setGravity(0.0f);
		
		this.previousPosition = cookie.previousPositionCookie.vector.clone();
		this.control.setPhysicsLocation(cookie.currentPositionCookie.vector.clone());
		this.control.setViewDirection(cookie.viewDirectionCookie.vector.clone());
		this.control.setWalkDirection(cookie.moveDirectionCookie.vector.clone());
	}
	
	protected Node getOwnerSpatialNode() {
		return ((Node) ((ModelBasedPhysicalExtension) this.owner.getAbstractPhysicalExtension()).getModelSpatial());
	}
	
	protected Spatial getOwnerSpatial() {
		return (((ModelBasedPhysicalExtension) this.owner.getAbstractPhysicalExtension()).getModelSpatial());
	}
	
	private void updateRotation( final float tpf ) {
		if ( currentRotationSideScale==0.0f )
			return;
		
		final Vector3f viewDirection = this.control.getViewDirection();
		Vector2f viewDirection2d = new Vector2f( viewDirection.getX(), viewDirection.getZ() );
		
		final float degreeRotation = DEGREE_ROTATION_PER_SEC*tpf;
		viewDirection2d.rotateAroundOrigin( MathGameToolkit.toRadiants( degreeRotation ), ( currentRotationSideScale>=0.0f ) );
		
		final Vector3f newViewDirection = new Vector3f( viewDirection2d.getX(), 0.0f, viewDirection2d.getY() );
		this.control.setViewDirection( newViewDirection );
	}
	
	private void keepInsideWorldPlatform() {
		if ( (this.application!=null) && (this.application.getGraphicalTerrain().checkOutOfBound( this.control.getPhysicsLocation() ) ) &&
				( this.previousPosition!=null ) )
			this.control.setPhysicsLocation( this.previousPosition );
		else 
			this.previousPosition = this.control.getPhysicsLocation().clone();
	}

	@Override
	public void onAnimChange(AnimControl arg0, AnimChannel arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}
	
	protected AnimControl findAnimControl( final Spatial parent, final AnimControl previousAnimControl ) {
		AnimControl animControl = parent.getControl( AnimControl.class );
		if ( animControl!=null )
			return animControl;
		
		if ( parent instanceof Node ) {
			for( Spatial s: ( (Node) parent).getChildren() ) {
				AnimControl tmpAnimControl = findAnimControl( s, previousAnimControl );
				if ( tmpAnimControl!=null ) {
					if ( tmpAnimControl!=previousAnimControl )
						return tmpAnimControl;;
				}
			}
		}
		return null;
	}
	
	protected AnimControl findNextAnimControl( final Spatial parent, final List< AnimControl > previousAnimControls ) {
		AnimControl animControl = parent.getControl( AnimControl.class );
		if ( animControl!=null )
			return animControl;
		
		if ( parent instanceof Node ) {
			for( Spatial s: ( (Node) parent).getChildren() ) {
				AnimControl tmpAnimControl = findNextAnimControl( s, previousAnimControls );
				if ( tmpAnimControl!=null ) {
					if ( !previousAnimControls.contains( tmpAnimControl ) )
						return tmpAnimControl;;
				}
			}
		}
		return null;
	}
	
	private void setAnimation( final String animationName ) {
		for( AnimChannel ac: animationChannels )
			if ( !animationName.equals( ac.getAnimationName()) ) {
					ac.setAnim( animationName );
					ac.setLoopMode( LoopMode.Loop );
			}
	}
	
	private void rotatePropellers( final float tpf ) {
		if ( (!isDriven()) && (currentPropellerRotation<=0.0f) )
			return;
		
		if ( isDriven() && (currentPropellerRotation<FLYING_ROTATION_PER_SEC) )
			currentPropellerRotation+=(tpf*FLYING_ROTATION_ACCELLERATION_PER_SEC);
		else if ( (!isDriven()) && (currentPropellerRotation>0.0f) ) {
			currentPropellerRotation-=(tpf*FLYING_ROTATION_ACCELLERATION_PER_SEC);
			if ( currentPropellerRotation<0.0f )
				currentPropellerRotation=0.0f;
		}
		
		Spatial aircraftSpatial = 
				( (ModelBasedPhysicalExtension) owner.getAbstractPhysicalExtension() )
					.getModelSpatial();
		Geometry geo = findGeom( ((Node) aircraftSpatial).getChild(14), "shader_1" );
		if ( geo!=null )
			geo.rotate( 0.0f, FastMath.TWO_PI*tpf*currentPropellerRotation, 0.0f );
	}
	
	public static Geometry findGeom(Spatial spatial, String name) {
		
		if (spatial instanceof Node) {
			Node node = (Node) spatial;
			for (int i=0; i<node.getQuantity(); ++i) {
				Spatial child = node.getChild(i);
				Geometry result = findGeom(child, name);
				if (result != null)
					return result;
			}
		} else if (spatial instanceof Geometry) {
			if (spatial.getName().startsWith(name)) 
				return (Geometry) spatial;
		}
		return null;
	}
	
	private static void setLocalTranslationComponents( Spatial aircraftModel ) {
		if (aircraftModel instanceof Node) {
			Node node = (Node) aircraftModel;
			for (int i=0; i<node.getQuantity(); ++i) {
				Spatial child = node.getChild(i);
				setLocalTranslationComponents(child);
			}
		} else if (aircraftModel instanceof Geometry) {
			( (Geometry) aircraftModel ).setLocalTranslation( 0.0f, -1.0f, 0.0f );
		}
	}

}
