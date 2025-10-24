/**
 * 
 */
package it.unical.logic_santos.traffic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import it.unical.logic_santos.collision.ICollidable;
import it.unical.logic_santos.collision.ICollisionDetectionEngine;
import it.unical.logic_santos.collision.ICollisionResults;
import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.environment.sound.HumanSoundManager;
import it.unical.logic_santos.gameplay.IdentifierGenerator;
import it.unical.logic_santos.gameplay.PlayerManager;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.net.IRemoteCommunicator;
import it.unical.logic_santos.physics.activity.HumanAnimation;
import it.unical.logic_santos.physics.activity.HumanPhysicalActivity;
import it.unical.logic_santos.physics.activity.VehiclePhysicalActivity;
import it.unical.logic_santos.physics.activity.WalkerCookie;
import it.unical.logic_santos.physics.activity.WalkerPhysicalActivity;
import it.unical.logic_santos.physics.extension.BoundingCapsule;
import it.unical.logic_santos.roads_network.PathsNetwork;
import it.unical.logic_santos.roads_network.RoadNode;
import it.unical.logic_santos.roads_network.RoadsNetwork;
import it.unical.logic_santos.spatial_entity.AbstractHuman;
import it.unical.logic_santos.spatial_entity.AbstractStaticSpatialEntity;
import it.unical.logic_santos.spatial_entity.AbstractVehicle;
import it.unical.logic_santos.spatial_entity.Car;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.spatial_entity.Player;
import it.unical.logic_santos.spatial_entity.Walker;
import it.unical.logic_santos.toolkit.geometry.Capsule;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class WalkerTrafficManager {

	private List<AbstractHuman> walkers=null;
	private PathsNetwork pathsNetwork=null;
	private ICollisionDetectionEngine collisionEngine=null;
	private LogicSantosApplication application=null;
	public static Random rn = new Random(System.currentTimeMillis());
	
	private float trafficIntensity=1.0f;
	
	private float updateSleepTime=0.0f;
	
	private static final float MIN_DISTANCE_WALKER_ASSIGNMENT = 100.0f;
	private static final float MIN_DISTANCE_VEHICLE_ASSIGNMENT = 12.0f;
	private static final float MIN_TIME_FOR_UPDATE = 0.0f; //1.3f;
	private static final int   MAX_NUMBER_WALKERS = 5;
	private static final float MAX_NEAR_PLAYER_DISTANCE = 300.0f;
	private static final float PADDING_NEAR_PLAYER_DISTANCE = 100.0f;
	private static final float MARGIN_NEAR_PLAYER_DISTANCE  = 5.0f;
	
	private static final float Y_OFFSET_INIT = 3.8f + 0.2f;
	
	public WalkerTrafficManager(PathsNetwork pathsNetwork, ICollisionDetectionEngine collisionEngine, LogicSantosApplication application) {
		this.walkers = new LinkedList<AbstractHuman>();
		this.pathsNetwork = pathsNetwork;
		this.collisionEngine = collisionEngine;
		this.application = application;
	}
	
	public void initComponents() {
		removeWalkers();
		
		final int numWalkers = (int) (MAX_NUMBER_WALKERS * trafficIntensity);
		boolean addWalkerFlag=true;
		
		for(int i=0; ((i<numWalkers) && (addWalkerFlag)); ++i) {
			
			Walker newWalker=null;
			try {
				newWalker = getRandomWalkerClass().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				newWalker=null;
				e.printStackTrace();
			}
			newWalker.getPhysicalActivity().setInPathsNetwork(pathsNetwork); newWalker.getWalkerPhysicalActivity().rootNode = application.getRootNode();
			newWalker.getWalkerPhysicalActivity().setWorld(application.getLogicSantosWorld());
			newWalker.getWalkerPhysicalActivity().setTrafficManager( this );
			newWalker.getPhysicalActivity().initActivity();
			newWalker.getWalkerPhysicalActivity().setId( IdentifierGenerator.getInstance().getNextIdentifier() );
			
			ICollidable newWalkerBoundingVolume = newWalker.getAbstractPhysicalExtension().getBoundingVolume();
			List<ICollisionResults> collisionResults = new ArrayList<ICollisionResults>();
			Vector3D position=null;
			int numberOfSteps=0;
			addWalkerFlag=true;
			boolean continueAssignment=true;
			do {
				
				final int randomNodeId = getRandomNodeId();
				position = pathsNetwork.getNode(randomNodeId).getPosition().clone();
				position.setY(position.getY() + Y_OFFSET_INIT);
				
				//newWalker.setSpatialTranslation(position);
				newWalker.getWalkerPhysicalActivity().getControl().setPhysicsLocation(position.toVector3f());
				newWalker.setStartNodeId(randomNodeId);
				newWalker.getPhysicalActivity().setStartPathsNetworkNode(pathsNetwork.getNode(randomNodeId));
				newWalker.getWalkerPhysicalActivity().initOrientation(pathsNetwork);
				
				collisionEngine.addCollidable(newWalkerBoundingVolume);
				
				if (/*collisionEngine.checkCollisions(newWalkerBoundingVolume, collisionResults).isEmpty() && */
						checkDistanceNewWalkerToEachOther(newWalker))
					continueAssignment = false;
				else
					collisionEngine.removeCollidable(newWalkerBoundingVolume);
				
				++numberOfSteps;
				if ( (numberOfSteps>pathsNetwork.nodesCount()) && continueAssignment ) {
					collisionEngine.removeCollidable(newWalkerBoundingVolume);
					addWalkerFlag=false;
					continueAssignment=false;
				}
			} while(continueAssignment);
		
			/* add new walker */
			if (addWalkerFlag) {
				addWalker(newWalker, position, false);
				HumanSoundManager.getInstance().addWalkerSound( newWalker );
			}
		
		}
	}
	
	public void finalizeComponents() {
		for(AbstractHuman w: walkers) {
			w.getPhysicalActivity().finalizeActivity();
			removeWalker(w);
		}
	}
	
	public float getTrafficIntensity() {
		return trafficIntensity;
	}
	
	public void setTrafficIntensity(float trafficIntensity) {
		if (trafficIntensity > 1.0f)
			trafficIntensity = 1.0f;
		else if (trafficIntensity < 0.0f)
			trafficIntensity = 0.0f;
		this.trafficIntensity = trafficIntensity;
	}
	
	public LogicSantosApplication getApplication() {
		return application;
	}
	
	public void update(final float tpf) {
		updateSleepTime+=tpf;
		if (updateSleepTime>=MIN_TIME_FOR_UPDATE) {
			
			List< RoadNode > nearestNodeToPlayer = findNearestNodesToPlayers();
			
			for(AbstractHuman w: walkers) {
				w.getWalkerPhysicalActivity().update(updateSleepTime);
				if ( canUpdateWalkers() )
					updateWalkerDistanceFromPlayer( w.getWalkerPhysicalActivity(), nearestNodeToPlayer, tpf );
			}
			
			updateSleepTime=0.0f;
		}
	}
	
	public List< AbstractHuman > getHumans() {
		return walkers;
	}
	
	public boolean addVehicleDriverOut( AbstractVehicle vehicle, Player driver ) {
		if ( !vehicle.getVehiclePhysicalActivity().hasWalkerDriver() )
			return true;
		AbstractHuman furtherWalker = findFurtherWalkerFromPlayers();
		if ( furtherWalker==null )
			return true;
		
		final Vector3f newWalkerPosition = computeNewWalkerPositionOutVehicle( vehicle, driver, application );
		if ( newWalkerPosition==null )
			return false;
		
		furtherWalker.getWalkerPhysicalActivity().getControl().setPhysicsLocation( newWalkerPosition );
		furtherWalker.getWalkerPhysicalActivity().clearTergetNode();
		furtherWalker.getWalkerPhysicalActivity().setFollowedVehicle( vehicle );
		return true;
	}
	
	public List< ISpatialEntity > getDetectableSpatialEntitiesForWalker(  final AbstractHuman walker ) {
		List< ISpatialEntity > detectables = new ArrayList< ISpatialEntity >();
		detectables.addAll( application.getVisibleHumans() );
		detectables.addAll( application.getVehicles() );
		return detectables;
	}
	
	private void removeWalkers() {
		for(AbstractHuman w: walkers) {
			removeWalker(w);
		}
		walkers.clear();
	}
	
	private void addWalker(AbstractHuman w, final Vector3D position, final boolean addInCollisionEngine) {
		
		((HumanPhysicalActivity) w.getPhysicalActivity()).getControl().setPhysicsLocation(position.toVector3f());
		if (addInCollisionEngine)
			collisionEngine.addCollidable(w.getAbstractPhysicalExtension().getBoundingVolume());
		walkers.add(w);
		application.getRootNode().attachChild(((ModelBasedPhysicalExtension) w.getAbstractPhysicalExtension()).getModelSpatial());
		application.getGameplayScreen().getMinimapScreen().addVisibleEntity( w );
	}
	
	private void removeWalker(AbstractHuman w) {
		collisionEngine.removeCollidable(w.getAbstractPhysicalExtension().getBoundingVolume());
		walkers.remove(w);
		w.getHumanPhysicalActivity().stopWalking();
		application.getRootNode().detachChild(((ModelBasedPhysicalExtension) w.getAbstractPhysicalExtension()).getModelSpatial());
		application.getGameplayScreen().getMinimapScreen().removeVisibleEntity( w );
	}
	
	private Class<? extends Walker> getRandomWalkerClass() {
		return Walker.class;
	}
	
	private int getRandomNodeId() {
		return ( (new Random().nextInt(pathsNetwork.nodesCount())) + 1 );
	}
	
	private boolean checkDistanceNewWalkerToEachOther(final AbstractHuman walker) {
		for(AbstractHuman w: walkers)
			if (walker.getSpatialTranslation().distanceTo(w.getSpatialTranslation()) < MIN_DISTANCE_WALKER_ASSIGNMENT)
				return false;
		return true;
	}
	
	public WalkerTrafficManagerCookie getCookie() {
		WalkerTrafficManagerCookie cookie = new WalkerTrafficManagerCookie();
		cookie.walkersCookies.clear();
		
		final int sizeWalkers = walkers.size();
		for( int i=0; i<sizeWalkers; ++i )
			cookie.walkersCookies.add(this.walkers.get(i).getWalkerPhysicalActivity().getWalkerCookie());
		
		cookie.trafficIntensity = this.trafficIntensity;
		cookie.updateSleepTime = this.updateSleepTime;
		
		return cookie;
	}
	
	public void setCookie( final WalkerTrafficManagerCookie cookie ) {
		final int sizeWalkersCookies = cookie.walkersCookies.size();
		boolean flag;
		for( int i=0; i<sizeWalkersCookies; ++i ) {
			
			WalkerCookie walkerCookie = cookie.walkersCookies.get(i);
			flag=true;
			for (Iterator< AbstractHuman > it = this.walkers.iterator(); ( (it.hasNext()) && (flag) );) {
				AbstractHuman walker = it.next();
				WalkerPhysicalActivity walkerPhysicalActivity = walker.getWalkerPhysicalActivity();
				
				if ( walkerPhysicalActivity.getId()==walkerCookie.id ) {
					walkerPhysicalActivity.setWalkerCookie(walkerCookie);
					flag = false;
				}
			}
		}
		
		this.trafficIntensity = cookie.trafficIntensity;
		this.updateSleepTime = cookie.updateSleepTime;
	}
	
	private boolean checkDistanceNewWalkerToEachOther(final Vector3D walkerPosition) {
		for(AbstractHuman w: walkers)
			if (walkerPosition.toVector3f().distance( w.getHumanPhysicalActivity().getControl().getPhysicsLocation() ) < MIN_DISTANCE_WALKER_ASSIGNMENT)
				return false;
		
		List< AbstractVehicle > vehicles = application.getVehicles();
		for(AbstractVehicle v: vehicles)
			if (walkerPosition.toVector3f().distance( v.getVehiclePhysicalActivity().getControl().getPhysicsLocation() ) < MIN_DISTANCE_VEHICLE_ASSIGNMENT)
				return false;
		
		return true;
	}
	
	
	private void updateWalkerDistanceFromPlayer( WalkerPhysicalActivity walkerActivity, List< RoadNode > nearestNodesToPlayer, final float tpf ) {
		if ( nearestNodesToPlayer.isEmpty() ) 
			return;
		
		final Vector3f playerPosition = application.getPlayerManager().getPlayerPosition();
		final Vector3f walkerPosition = walkerActivity.getControl().getPhysicsLocation();
		
		final float distance = playerPosition.distance(walkerPosition);
		if ( (distance>MAX_NEAR_PLAYER_DISTANCE) || (walkerActivity.getLifeBar().isEmpty()) ) {
			
			if ( walkerActivity.getLifeBar().isEmpty() && (!walkerActivity.isAfterHitReaction()) ) {
				walkerActivity.onHitReaction(tpf);
			} else {
				/* assign new RoadNode to Walker ... */
				final int k = rn.nextInt( nearestNodesToPlayer.size() );
				RoadNode node = nearestNodesToPlayer.get( k );
				final int nodeId = node.getId();
				Vector3D position = node.getPosition().clone();
				position.setY(position.getY() + Y_OFFSET_INIT);
				
				if ( checkDistanceNewWalkerToEachOther(position) ) {
					walkerActivity.getControl().setPhysicsLocation(position.toVector3f());
					((Walker) walkerActivity.getSpatialEntityOwner()).setStartNodeId(nodeId);
					walkerActivity.setStartPathsNetworkNode(pathsNetwork.getNode(nodeId));
					walkerActivity.clearTergetNode();
					walkerActivity.clearHitReactionTime();
					
					if ( walkerActivity.getLifeBar().isEmpty() )
						walkerActivity.getLifeBar().setFull();
					
				}
				
				nearestNodesToPlayer.remove(k);
			}
		}
	}
	
	private List< RoadNode > findNearestNodesToPlayers() {
		List< RoadNode > nearestNode = new LinkedList< RoadNode >();
		final List< Player > players = application.getPlayers();
		
		for( Player p: players ) {
			final Vector3f playerPosition = p.getHumanPhysicalActivity().getControl().getPhysicsLocation();
			
			/* find nearest nodes ... */
			final int n=pathsNetwork.nodesCount();
			RoadNode node;
			float distance;
			for( int i=1; i<=n; ++i) {
				
				node = pathsNetwork.getNode( i );
				distance = playerPosition.distance( node.getPosition().toVector3f() );
				
				if ( ( distance<=(MAX_NEAR_PLAYER_DISTANCE-MARGIN_NEAR_PLAYER_DISTANCE) ) &&
					 ( distance>=(PADDING_NEAR_PLAYER_DISTANCE) ) )
					nearestNode.add(node);
			}
		}
		return nearestNode;
	}
	
	private List< Integer > createListNodeId( RoadsNetwork net ) {
		List< Integer > ids = new LinkedList< Integer >();
		final int n=net.nodesCount();
		for( int i=1; i<=n; ++i)
			ids.add(i);
		return ids;
	}
	
	private AbstractHuman findFurtherWalkerFromPlayers() {
		final List< Player > players = application.getPlayers();
		AbstractHuman furtherWalker=null;
		float maxDistance=Float.MIN_VALUE;
		float distance;
		
		for( AbstractHuman w: walkers ) {
			
			/* find further walker ... */
			distance = computeAverageDistanceFromPlayers( w, players );
			if ( (furtherWalker==null) || ( (furtherWalker!=null) && (distance>maxDistance) ) ) {
				furtherWalker = w;
				maxDistance = distance;
			}

		}
		return furtherWalker;
	}
	
	private static Vector3f computeNewWalkerPositionOutVehicle( final AbstractVehicle vehicle, final Player driver, final LogicSantosApplication application ) { // TODO: calculate the right position to enable the walker
		
		final Vector3f vehicleDirection = vehicle.getVehiclePhysicalActivity().computeVehicleForwardDirection().toVector3f();
		Vector2f walkerNew2dOffsetPosition = new Vector2f( vehicleDirection.getX(), vehicleDirection.getZ() );
		walkerNew2dOffsetPosition.rotateAroundOrigin( (float) (Math.PI/2.0f), true );
		walkerNew2dOffsetPosition.multLocal( PlayerManager.SCALE_OFFSET_POSITION_OUT_VEHICLE );
		
		Vector3f newWalkerPosition = vehicle.getVehiclePhysicalActivity().getControl().getPhysicsLocation().clone();
		newWalkerPosition.setX( newWalkerPosition.getX()+walkerNew2dOffsetPosition.getX() );
		newWalkerPosition.setZ( newWalkerPosition.getZ()+walkerNew2dOffsetPosition.getY() );
		newWalkerPosition.setY( application.getLogicSantosWorld().getTerrainModel().getHeight( newWalkerPosition.getX(), newWalkerPosition.getZ()) +  Y_OFFSET_INIT );
		
		/* check collisions position out vehicle */
		List< AbstractHuman > humans = application.getVisibleHumans();
		List< AbstractVehicle > vehicles = application.getVehicles();
		Collection< AbstractStaticSpatialEntity > staticObjects = application.getLogicSantosWorld().getStaticSpatialEntities();
		
		for( AbstractHuman h: humans )
			if ( (h!=driver) && 
					(newWalkerPosition.distance( h.getHumanPhysicalActivity().getControl().getPhysicsLocation() )<PlayerManager.MIN_HUMAN_DISTANCE_OUT_VEHICLE_POSITION) )
				return null;
		
		for( AbstractVehicle v: vehicles )
			if ( (v!=vehicle) && 
					(newWalkerPosition.distance( v.getVehiclePhysicalActivity().getControl().getPhysicsLocation() )<PlayerManager.MIN_VEHICLE_DISTANCE_OUT_VEHICLE_POSITION) )
				return null;
		// TODO add check collisions STATIC OBJECTS !!!

		BoundingBox box = new BoundingBox( newWalkerPosition, 
											ModelingConfig.HUMAN_CAPSULE_COLLISION_SHAPE_RADIUS, 
											ModelingConfig.HUMAN_CAPSULE_COLLISION_SHAPE_HEIGHT, 
											ModelingConfig.HUMAN_CAPSULE_COLLISION_SHAPE_RADIUS );
		
		
		final Vector2f newPosition2dWalker = new Vector2f( newWalkerPosition.getX(), newWalkerPosition.getZ() );
		for( AbstractStaticSpatialEntity obj: staticObjects ) {
			
			CollisionResults results=new CollisionResults();
			( (ModelBasedPhysicalExtension) obj.getAbstractPhysicalExtension() ).getModelSpatial().getWorldBound().collideWith( box, results );
			
			if ( results.size()>0 )
				return null;
			
			/*final Vector3f positionObject = obj.getSpatialTranslation().toVector3f();
			final Vector2f position2dObject = new Vector2f( positionObject.getX(), positionObject.getZ() );
			
			if ( newPosition2dWalker.distance( position2dObject )<PlayerManager.STATIC_OBJECT_MIN_DISTANCE_OUT_VEHICLE_POSITION ) 
				return null;*/
		}
		
		return newWalkerPosition;
	}
	
	private void manageWalkersCollisions() {
		
		//for(AbstractHuman w: walkers) {
		//	w.getWalkerPhysicalActivity().update(updateSleepTime);
		//}
		/*
		AbstractHuman[] walkersArray = new AbstractHuman[ walkers.size() ];
		
		int i=0;
		int j=0;
		for( AbstractHuman w: walkers ) {
			walkersArray[i] = w;
			i++;
		}
		
		for( i=0; i<walkersArray.length; ++i )
			for( j=i+1; j<walkersArray.length; ++j ) 	
				if ( AbstractHuman.collisionHumans( walkersArray[i], walkersArray[j] ) ) {
					walkersArray[i].getHumanPhysicalActivity().onCollisionWithAbstractHuman( walkersArray[j] );
					walkersArray[j].getHumanPhysicalActivity().onCollisionWithAbstractHuman( walkersArray[i] );
				}
		*/
	}
	
	private boolean canUpdateWalkers() {
		IRemoteCommunicator remoteCommunicator = application.getRemoteCommunicator();
		if ( (remoteCommunicator!=null) && (remoteCommunicator.isConnected()) && (!remoteCommunicator.isMaster()) )
			return false;
		return true;
	}
	
	private static float computeAverageDistanceFromPlayers( final AbstractHuman human, final List< Player > players ) {
		float distanceSum=0.0f;
		for( Player p: players ) 
			distanceSum += human.getHumanPhysicalActivity().getControl().getPhysicsLocation()
				.distance( p.getHumanPhysicalActivity().getControl().getPhysicsLocation() );
		distanceSum /= ( (float) players.size() );
		return distanceSum;
	}
	
}
