/**
 * 
 */
package it.unical.logic_santos.traffic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.bulletphysics.collision.dispatch.GhostObject;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import it.unical.logic_santos.collision.ICollidable;
import it.unical.logic_santos.collision.ICollisionDetectionEngine;
import it.unical.logic_santos.collision.ICollisionResults;
import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.environment.sound.VehicleSoundManager;
import it.unical.logic_santos.gameplay.IdentifierGenerator;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.net.IRemoteCommunicator;
import it.unical.logic_santos.physics.activity.PhysicsSpace;
import it.unical.logic_santos.physics.activity.VehicleCookie;
import it.unical.logic_santos.physics.activity.VehiclePhysicalActivity;
import it.unical.logic_santos.physics.activity.WalkerCookie;
import it.unical.logic_santos.physics.activity.WalkerPhysicalActivity;
import it.unical.logic_santos.physics.extension.AbstractBoundingVolume;
import it.unical.logic_santos.roads_network.RoadNode;
import it.unical.logic_santos.roads_network.RoadsNetwork;
import it.unical.logic_santos.spatial_entity.AbstractHuman;
import it.unical.logic_santos.spatial_entity.AbstractVehicle;
import it.unical.logic_santos.spatial_entity.Car;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.spatial_entity.Player;
import it.unical.logic_santos.spatial_entity.Walker;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class VehicleTrafficManager {

	private List<AbstractVehicle> vehicles=null;
	private RoadsNetwork roadsNetwork=null;
	private ICollisionDetectionEngine collisionEngine=null;
	private LogicSantosApplication application=null;
	private Random rn = new Random(System.currentTimeMillis());
	
	private float trafficIntensity=1.0f;
	
	private float updateSleepTime=0.0f;
	private float updateDistancePlayerSleepTime=0.0f;
	
	private static final float MIN_DISTANCE_VEHICLE_ASSIGNMENT = 100.0f;
	private static final float MIN_DISTANCE_HUMAN_ASSIGNMENT = 12.0f;
	private static final float MIN_TIME_FOR_UPDATE = 1.3f;
	private static final float MIN_TIME_FOR_DISTANCE_PLAYER_UPDATE = 3.3f;
	private static final int   MAX_NUMBER_VEHICLES = 3;
	private static final float MAX_NEAR_PLAYER_DISTANCE = 300.0f;
	private static final float PADDING_NEAR_PLAYER_DISTANCE = 100.0f;
	private static final float MARGIN_NEAR_PLAYER_DISTANCE  = 5.0f;
	
	private static final float Y_OFFSET_INIT = 0.0f;
	
	
	public VehicleTrafficManager(RoadsNetwork roadsNetwork, ICollisionDetectionEngine collisionEngine, LogicSantosApplication application) {
		this.vehicles = new LinkedList<AbstractVehicle>();
		this.roadsNetwork = roadsNetwork;
		this.collisionEngine = collisionEngine;
		this.application = application;
	}
	
	public void initComponents() {System.out.println("INIT COMPONENT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		removeVehicles();
		
		final int numVehicles = (int) (MAX_NUMBER_VEHICLES * trafficIntensity); //(int) (roadsNetwork.nodesCount() * trafficIntensity); System.out.println("NUMBER!!!!!!!!!!: " + numVehicles);
		boolean addVehicleFlag=true;
		
		for(int i=0; ((i<numVehicles) && (addVehicleFlag)); ++i) {
			
			AbstractVehicle newVehicle=null;
			try {
				newVehicle = getRandomVehicleClass().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				newVehicle=null;
				e.printStackTrace();
			}
			newVehicle.getPhysicalActivity().setInRoadsNetwork(roadsNetwork); newVehicle.getVehiclePhysicalActivity().rootNode = application.getRootNode();
			newVehicle.getVehiclePhysicalActivity().setTrafficManager( this );
			newVehicle.getPhysicalActivity().initActivity();
			newVehicle.getVehiclePhysicalActivity().setId( IdentifierGenerator.getInstance().getNextIdentifier() );
				
			List<ICollisionResults> collisionResults = new ArrayList<ICollisionResults>();
			Vector3D position=null;
			int numberOfSteps=0;
			addVehicleFlag=true;
			boolean continueAssignment=true;
			do {
				
				final int randomNodeId = getRandomNodeId();
				position = roadsNetwork.getNode(randomNodeId).getPosition().clone();
				position.setY(position.getY() + Y_OFFSET_INIT);
				
				newVehicle.setSpatialTranslation(position);
				newVehicle.getVehiclePhysicalActivity().getControl().setPhysicsLocation(position.toVector3f());
				newVehicle.setStartNodeId(randomNodeId);
				newVehicle.getPhysicalActivity().setStartRoadsNetworkNode(roadsNetwork.getNode(randomNodeId));
				
				AbstractBoundingVolume newVehicleBoundingVolume = newVehicle.getAbstractPhysicalExtension().getBoundingVolume();
				newVehicleBoundingVolume.setCenterPosition(position);
				collisionEngine.addCollidable(newVehicleBoundingVolume);
				
				if (/*collisionEngine.checkCollisions(newVehicleBoundingVolume, collisionResults).isEmpty() && */
						checkDistanceNewVehicleToEachOther(newVehicle))
					continueAssignment = false;
				else
					collisionEngine.removeCollidable(newVehicleBoundingVolume);
				
				++numberOfSteps;
				if ( (numberOfSteps>roadsNetwork.nodesCount()) && continueAssignment ) {
					collisionEngine.removeCollidable(newVehicleBoundingVolume);
					addVehicleFlag=false;
					continueAssignment=false;
				}
			} while(continueAssignment);
		
			/* add new vehicle */
			
			if (addVehicleFlag) {
				newVehicle.getVehiclePhysicalActivity().initOrientation(roadsNetwork);
				addVehicle(newVehicle, position, false);
				VehicleSoundManager.getInstance().addVehicleSound( newVehicle );
			} else
				newVehicle.getPhysicalActivity().finalizeActivity();
		
		}
	}
	
	public void finalizeComponents() {
		for(AbstractVehicle v: vehicles) {
			VehicleSoundManager.getInstance().removeVehicleSound( v );
			v.getPhysicalActivity().finalizeActivity();
			removeVehicle(v);
		}
	}
	
	public float getTrafficIntensity() {
		return trafficIntensity;
	}
	
	public List<AbstractVehicle> getVehicles() {
		return vehicles;
	}
	
	public void setTrafficIntensity(float trafficIntensity) {
		if (trafficIntensity > 1.0f)
			trafficIntensity = 1.0f;
		else if (trafficIntensity < 0.0f)
			trafficIntensity = 0.0f;
		this.trafficIntensity = trafficIntensity;
	}
	
	public void update(final float tpf) {
		//TODO: sleep time
		for(AbstractVehicle v: vehicles)
			v.getVehiclePhysicalActivity().update(tpf);
		
		updateDistancePlayerSleepTime+=tpf;
		if (updateDistancePlayerSleepTime>=MIN_TIME_FOR_DISTANCE_PLAYER_UPDATE) {
			
			List< RoadNode > nearestNodeToPlayer = findNearestNodesToPlayers();
			
			for(AbstractVehicle v: vehicles) {
				if ( canUpdateVehicles() )
					updateVehicleDistanceFromPlayer( v.getVehiclePhysicalActivity(), nearestNodeToPlayer );
			}
			updateDistancePlayerSleepTime=0.0f;
		}
	}
	
	public List< ISpatialEntity > getDetectableSpatialEntitiesForVehicle(  final AbstractVehicle vehicle ) {
		List< ISpatialEntity > detectables = new ArrayList< ISpatialEntity >();
		detectables.addAll( vehicles );
		detectables.addAll( application.getVisibleHumans() );
		return detectables;
	}
	
	public LogicSantosApplication getApplication() {
		return application;
	}
	
	public VehicleTrafficManagerCookie getCookie() {
		VehicleTrafficManagerCookie cookie = new VehicleTrafficManagerCookie();
		
		cookie.vehiclesCookies.clear();
		
		final int sizeVehicles = vehicles.size();
		for( int i=0; i<sizeVehicles; ++i )
			cookie.vehiclesCookies.add(this.vehicles.get(i).getVehiclePhysicalActivity().getCookie());
		
		cookie.trafficIntensity = this.trafficIntensity;
		cookie.updateDistancePlayerSleepTime = this.updateDistancePlayerSleepTime;
		
		return cookie;
	}
	
	public void setCookie( final VehicleTrafficManagerCookie cookie ) {
		final int sizeVehiclesCookies = cookie.vehiclesCookies.size();
		boolean flag;
		for( int i=0; i<sizeVehiclesCookies; ++i ) {
			
			VehicleCookie vehicleCookie = cookie.vehiclesCookies.get(i);
			flag=true;
			for (Iterator< AbstractVehicle > it = this.vehicles.iterator(); ( (it.hasNext()) && (flag) );) {
				AbstractVehicle vehicle = it.next();
				VehiclePhysicalActivity vehiclePhysicalActivity = vehicle.getVehiclePhysicalActivity();
				
				if ( vehiclePhysicalActivity.getId()==vehicleCookie.id ) {
					vehiclePhysicalActivity.setCookie(vehicleCookie);
					flag = false;
				}
			}
		}
		
		this.trafficIntensity = cookie.trafficIntensity;
		this.updateDistancePlayerSleepTime = cookie.updateDistancePlayerSleepTime;
	}
	
	private void removeVehicles() {
		for(AbstractVehicle v: vehicles) {
			removeVehicle(v);
		}
		vehicles.clear();
	}
	
	private void addVehicle(AbstractVehicle v, final Vector3D position, final boolean addInCollisionEngine) {
		System.out.println("ADDING NEW VEHICLE");
		((VehiclePhysicalActivity) v.getPhysicalActivity()).getControl().setPhysicsLocation(position.toVector3f());
		if (addInCollisionEngine)
			collisionEngine.addCollidable(v.getAbstractPhysicalExtension().getBoundingVolume());
		vehicles.add(v);
		application.getRootNode().attachChild((Node) ((ModelBasedPhysicalExtension) v.getAbstractPhysicalExtension()).getModelSpatial());
		application.getGameplayScreen().getMinimapScreen().addVisibleEntity( v );
	}
	
	private void removeVehicle(AbstractVehicle v) {
		collisionEngine.removeCollidable(v.getAbstractPhysicalExtension().getBoundingVolume());
		vehicles.remove(v);
		application.getGameplayScreen().getMinimapScreen().removeVisibleEntity( v );
	}
	
	private Class<? extends AbstractVehicle> getRandomVehicleClass() {
		return Car.class;
	}
	
	private int getRandomNodeId() {
		return ( (new Random().nextInt(roadsNetwork.nodesCount())) + 1 );
	}
	
	private boolean checkDistanceNewVehicleToEachOther(final AbstractVehicle vehicle) {
		for(AbstractVehicle v: vehicles)
			if (vehicle.getSpatialTranslation().distanceTo(v.getSpatialTranslation()) < MIN_DISTANCE_VEHICLE_ASSIGNMENT)
				return false;
		return true;
	}
	
	private void updateVehicleDistanceFromPlayer( VehiclePhysicalActivity vehicleActivity, List< RoadNode > nearestNodesToPlayer ) {
		if ( nearestNodesToPlayer.isEmpty() || ( vehicleActivity.isDriven() && vehicleActivity.hasDriver() ) ) 
			return;
		
		final Vector3f playerPosition = application.getPlayerManager().getPlayerPosition();
		final Vector3f vehiclePosition = vehicleActivity.getControl().getPhysicsLocation();
		
		final float distance = playerPosition.distance(vehiclePosition);
		if ( distance>MAX_NEAR_PLAYER_DISTANCE ) {
			
			/* assign new RoadNode to Walker ... */
			boolean continueAssignment;
			do {
				
				continueAssignment = false;
				final int k = rn.nextInt( nearestNodesToPlayer.size() );
				RoadNode node = nearestNodesToPlayer.get( k );
				final int nodeId = node.getId();
				Vector3D position = node.getPosition().clone();
				
				if ( checkPositionFreeForNewVehicle(position.toVector3f()) ) {
					
					final float tmpMass = vehicleActivity.getControl().getMass();
					vehicleActivity.getControl().setMass( 0.0f );
					//PhysicsSpace.getInstance().getSpace().remove( vehicleActivity.getControl() );
					position.setY(position.getY() + Y_OFFSET_INIT);
					//vehicleActivity.getSpatialEntityOwner().setSpatialTranslation(position);
					vehicleActivity.getControl().setPhysicsLocation(position.toVector3f());
					((AbstractVehicle) vehicleActivity.getSpatialEntityOwner()).setStartNodeId(nodeId);
					vehicleActivity.setStartRoadsNetworkNode(roadsNetwork.getNode(nodeId));
					vehicleActivity.clearTergetNode();
					vehicleActivity.initOrientation(roadsNetwork);
					vehicleActivity.getSpatialEntityOwner().getAbstractPhysicalExtension().onShootResetEffects();
					//PhysicsSpace.getInstance().getSpace().add( vehicleActivity.getControl() );
					vehicleActivity.getControl().setMass( tmpMass );
					vehicleActivity.getControl().clearForces();
					
					/* reset vehicle */
					vehicleActivity.getControl().setLinearVelocity( Vector3f.ZERO );
					vehicleActivity.getControl().setAngularVelocity( Vector3f.ZERO );
					vehicleActivity.getControl().resetSuspension();
					
					application.onVehicleResetPosition( vehicleActivity.getAbstractVehicleOwner() );
					
					if ( vehicleActivity.getLifeBar().isEmpty() )
						vehicleActivity.getLifeBar().setFull();
					vehicleActivity.setIsDriven(false);
					
				}
				
				nearestNodesToPlayer.remove(k);
			
			} while( continueAssignment );
		}
	}
	
	private List< RoadNode > findNearestNodesToPlayers() {
		List< RoadNode > nearestNode = new LinkedList< RoadNode >();
		final List< Player > players = application.getPlayers();
		
		for( Player p: players ) {
			final Vector3f playerPosition = p.getHumanPhysicalActivity().getControl().getPhysicsLocation();
			
			/* find nearest nodes ... */
			final int n=roadsNetwork.nodesCount();
			RoadNode node;
			float distance;
			for( int i=1; i<=n; ++i) {
				
				node = roadsNetwork.getNode( i );
				distance = playerPosition.distance( node.getPosition().toVector3f() );
				
				if ( ( distance<=(MAX_NEAR_PLAYER_DISTANCE-MARGIN_NEAR_PLAYER_DISTANCE) ) &&
					 ( distance>=(PADDING_NEAR_PLAYER_DISTANCE) ) )
					nearestNode.add(node);
			}
		}
		return nearestNode;
	}
	
	private boolean checkPositionFreeForNewVehicle( final Vector3f position ) {
		for( AbstractVehicle v: vehicles ) {
			final Vector3f vehiclePosition = v.getVehiclePhysicalActivity().getControl().getPhysicsLocation();
			if ( position.distance(vehiclePosition)<MIN_DISTANCE_VEHICLE_ASSIGNMENT )
				return false;
		}
		
		List< AbstractHuman > humans = application.getHumans();
		for( AbstractHuman h: humans ) {
			final Vector3f humanPosition = h.getHumanPhysicalActivity().getControl().getPhysicsLocation();
			if ( position.distance(humanPosition)<MIN_DISTANCE_HUMAN_ASSIGNMENT )
				return false;
		}
		
		return true;
	}
	
	private boolean canUpdateVehicles() {
		IRemoteCommunicator remoteCommunicator = application.getRemoteCommunicator();
		if ( (remoteCommunicator!=null) && (remoteCommunicator.isConnected()) && (!remoteCommunicator.isMaster()) )
			return false;
		return true;
	}
	
}


