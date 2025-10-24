/**
 * 
 */
package it.unical.logic_santos.gameplay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.physics.activity.PhysicsSpace;
import it.unical.logic_santos.spatial_entity.AbstractHuman;
import it.unical.logic_santos.spatial_entity.AbstractStaticSpatialEntity;
import it.unical.logic_santos.spatial_entity.AbstractVehicle;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.spatial_entity.Player;
import it.unical.logic_santos.spatial_entity.Policeman;
import it.unical.logic_santos.spatial_entity.Walker;
import it.unical.logic_santos.terrain.modeling.ITerrainChunkModel;

/**
 * @author Agostino
 *
 */
public abstract class AbstractBullet implements IBullet {

	private static final float SIZE = 0.1f;
	private static final float MASS = 0.1f;
	private static final float LINEAR_VELOCITY_SCALE = 300.0f;
	private static final float MAX_DISTANCE_FROM_PLAYERS = 3000.0f;
	private static final Box BULLET_BOX = new Box( SIZE, SIZE, SIZE );
	private static final Material BULLET_MATERIAL = new Material( LogicSantosApplication.ASSET_MANAGER_APPLICATION, 
																  ModelingConfig.BULLET_MATERIAL_PATH );
	private static final float MAX_ACCURACY_DISTORTION_PER_AXIS = 0.01f;
	private static Random random = new Random( System.currentTimeMillis() );
	
	
	private   Geometry bulletGeo=null;
	protected RigidBodyControl control=null;
	private   AbstractHuman owner=null;
	private   Vector3f startPosition=null;
	private   Vector3f direction=null;
	
	protected LogicSantosApplication application=null;
	private   List< AbstractHuman > humans=null;
	private   List< AbstractVehicle > vehicles=null;
	private   List< AbstractStaticSpatialEntity > staticSpatialEntities=null;
	private   ITerrainChunkModel terrainModel=null;
	
	
	public AbstractBullet( AbstractHuman owner, LogicSantosApplication application ) {
		this.owner = owner;
		this.application = application;
		this.bulletGeo = new Geometry( "bullet", BULLET_BOX );
		this.bulletGeo.setMaterial( BULLET_MATERIAL );
	}
	
	public AbstractBullet( AbstractHuman owner, final Vector3f startPosition, final Vector3f direction, LogicSantosApplication application ) {
		this.owner = owner;
		this.startPosition = startPosition.clone();
		this.direction = direction.clone();
		this.application = application;
		this.bulletGeo = new Geometry( "bullet", BULLET_BOX );
		this.bulletGeo.setMaterial( BULLET_MATERIAL );
	}
	
	@Override
	public void addInWorld() {
		if ( application!=null )
			application.getRootNode().attachChild( bulletGeo );
		PhysicsSpace.getInstance().getBulletSpace().add( this.control );
	}
	
	@Override
	public void removeFromWorld() {
		application.getRootNode().detachChild( bulletGeo );
		PhysicsSpace.getInstance().getBulletSpace().remove( this.control );
	}
	
	@Override
	public void initActivity( final Vector3f startPosition, final Vector3f direction ) {
		this.startPosition = startPosition.clone();
		this.direction = calculateDirectionWRTAccuracy( direction );
		this.control = new RigidBodyControl( MASS );
		this.bulletGeo.addControl(this.control);
		
		this.control.setPhysicsLocation( this.startPosition );
		this.control.setLinearVelocity( this.direction.normalize().mult( LINEAR_VELOCITY_SCALE ));
		addInWorld();
		initControl( this.control );
	}
	
	@Override
	public void finalizeActivity() {
		removeFromWorld();
		this.bulletGeo.removeControl(this.control);
		this.control = null;
	}
	
	@Override
	public void update( final float tpf ) {
		if ( !isActive() )
			return;
		
		List< Vector3f > playersPositions = application.getPlayersPositions();
		
		float distance;
		Vector3f bulletPosition = this.control.getPhysicsLocation();
		boolean farToAllPlayers = true;
		final float max_distance = ( this.getRange()*MAX_DISTANCE_FROM_PLAYERS );
		
		for (Iterator< Vector3f > it = playersPositions.iterator(); ( (it.hasNext()) && (farToAllPlayers) );) {
			Vector3f playerPosition = it.next();
			distance = playerPosition.distance(bulletPosition);
			if ( distance<=max_distance  )
				farToAllPlayers = false;
		}
		
		if ( farToAllPlayers )
			finalizeActivity();
	}
	
	@Override
	public boolean isActive() {
		return ( this.control!=null );
	}

	@Override
	public void setHumans( List<AbstractHuman> humans ) {
		this.humans = humans;
	}
	
	@Override
	public void setVehicles( List<AbstractVehicle> vehicles ) {
		this.vehicles = vehicles;
	}
	
	@Override
	public void setStaticSpatialEntities(List<AbstractStaticSpatialEntity> staticSpatialEntities) {
		this.staticSpatialEntities = staticSpatialEntities;
	}
	
	public void setTerrainModel(ITerrainChunkModel terrainModel) {
		this.terrainModel = terrainModel;
	}
	
	@Override
	public List< AbstractHuman > checkHumanCollisions() {
		List< AbstractHuman > results = new ArrayList< AbstractHuman >();
		
		if ( !isActive() )
			return results;
		
		for( AbstractHuman human: humans ) {
			
			if ( human!=this.owner ) {
				CollisionResults collisionResults = new CollisionResults();
				( (ModelBasedPhysicalExtension) human.getAbstractPhysicalExtension() ).getModelSpatial().getWorldBound().collideWith(bulletGeo.getWorldBound(), collisionResults);
				
				if ( collisionResults.size()>0 )
					results.add( human );
			}
		}
		return results;
	}
	
	@Override
	public List< AbstractVehicle > checkVehicleCollisions() {
		List< AbstractVehicle > results = new ArrayList< AbstractVehicle >();
		
		if ( !isActive() )
			return results;
		
		for( AbstractVehicle vehicle: vehicles ) {
			
			CollisionResults collisionResults = new CollisionResults();
			( (ModelBasedPhysicalExtension) vehicle.getAbstractPhysicalExtension() ).getModelSpatial().getWorldBound().collideWith(bulletGeo.getWorldBound(), collisionResults);
			
			if ( collisionResults.size()>0 )
				results.add( vehicle );
			
		}
		return results;
	}
	
	@Override
	public boolean checkStaticSpatialEntitiesCollisions() {
		if ( !isActive() )
			return false;
		
		for( ISpatialEntity staticSpatialEntity: staticSpatialEntities ) {
			
			CollisionResults collisionResults = new CollisionResults();
			( (ModelBasedPhysicalExtension) staticSpatialEntity.getAbstractPhysicalExtension() ).getModelSpatial().getWorldBound().collideWith(bulletGeo.getWorldBound(), collisionResults);
			
			if ( collisionResults.size()>0 )
				return true;
			
		}
		return false;
	}
	
	@Override
	public boolean checkTerrainCollision() {
		if ( !isActive() )
			return false;
		final Vector3f currentPosition = this.control.getPhysicsLocation();
		final float terrainHeight = terrainModel.getHeight( currentPosition.getX(), currentPosition.getZ() );
		return ( currentPosition.getY()<=terrainHeight );
	}
	
	public AbstractHuman getOwner() {
		return owner;
	}
	
	@Override
	public Vector3f getDirection() {
		return direction.clone();
	}
	
	@Override
	public Vector3f getCurrentTranslation() {
		return ( this.control.getPhysicsLocation() );
	}
	
	@Override
	public LifeBar getHumanLifeBarReduction( final Class< ? extends AbstractHuman > humanClass ) {
		if ( humanClass == Walker.class )
			return ( new LifeBar(0.5f) );
		if ( humanClass == Policeman.class )
			return ( new LifeBar(0.2f) );
		if ( humanClass == Player.class )
			return ( new LifeBar(0.2f) );
		return ( new LifeBar(0.5f) );
	}
	
	@Override
	public LifeBar getVehicleLifeBarReduction() {
		return ( new LifeBar(0.1f) );
	}
	
	@Override
	public BulletCookie getCookie() {
		BulletCookie cookie = new BulletCookie();
		
		try {
		cookie.startPositionCookie.set(this.startPosition.clone());
		cookie.directionCookie.set(this.direction.clone());
		cookie.currentPosition.set(this.control.getPhysicsLocation().clone());
		
		if ( this.owner instanceof Player ) {
			
			final Player mainPlayer = application.getPlayer();
			if ( this.owner==mainPlayer )
				cookie.ownerTypeId= (application.getRemoteCommunicator().isMaster()) 
									? BulletCookie.MASTER_PLAYER_TYPE_OWNER_ID 
									: BulletCookie.CLIENT_PLAYER_TYPE_OWNER_ID;
			else 
				cookie.ownerTypeId= (application.getRemoteCommunicator().isMaster()) 
									? BulletCookie.CLIENT_PLAYER_TYPE_OWNER_ID 
									: BulletCookie.MASTER_PLAYER_TYPE_OWNER_ID;
			
		} else {
			cookie.ownerTypeId = BulletCookie.POLICEMAN_TYPE_OWNER_ID;
			cookie.ownerId = this.owner.getWalkerPhysicalActivity().getId();
		}
		
		cookie.type = this.getCookieTypeId();
		} catch ( Exception e ) {
			return cookie;
		}
		return cookie;
	}
	
	@Override
	public void setCookie( final BulletCookie cookie ) {
		this.startPosition = cookie.startPositionCookie.vector.clone();
		this.direction = cookie.directionCookie.vector.clone();
		this.control.setPhysicsLocation(cookie.currentPosition.vector.clone());
		
		switch( cookie.ownerTypeId ) {
		case BulletCookie.MASTER_PLAYER_TYPE_OWNER_ID:
			this.owner= (application.getRemoteCommunicator().isMaster())
						? application.getPlayer()
						: application.getRemotePlayer();
		case BulletCookie.CLIENT_PLAYER_TYPE_OWNER_ID:
			this.owner= (application.getRemoteCommunicator().isMaster())
						? application.getRemotePlayer()
						: application.getPlayer();
		case BulletCookie.POLICEMAN_TYPE_OWNER_ID:
			this.owner= application.getPolicemanById(cookie.ownerId);
		}
	}
	
	public abstract float getFireRate();
	public abstract float getAccuracy();
	public abstract float getRange();
	public abstract byte  getCookieTypeId();
	
	private static void initControl( RigidBodyControl control ) {
		control.setGravity( Vector3f.ZERO );
	}

	@Override
	public int getWantedValue() {
		return 1;
	}
	
	private Vector3f calculateDirectionWRTAccuracy( final Vector3f direction ) {
		Vector3f newDirection = direction.clone();
		float[] axisRandomSigns = computeAxisRandomSigns();
		
		final float distortion = (1.0f-this.getAccuracy())*MAX_ACCURACY_DISTORTION_PER_AXIS;
		
		final Vector3f distortionVector = new Vector3f( 
				distortion*axisRandomSigns[0], 
				distortion*axisRandomSigns[1], 
				distortion*axisRandomSigns[2]
						);
		newDirection.addLocal( distortionVector );
		return newDirection;
	}
	
	private static float[] computeAxisRandomSigns() {
		float[] randomSigns = new float[3];
		randomSigns[0]=fromBoolToSign( random.nextBoolean() );
		randomSigns[1]=fromBoolToSign( random.nextBoolean() );
		randomSigns[2]=fromBoolToSign( random.nextBoolean() );
		return randomSigns;
	}
	
	private static float fromBoolToSign( final boolean b ) {
		if ( b )
			return +1.0f;
		return -1.0f;
	}
	
}
