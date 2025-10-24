/**
 * 
 */
package it.unical.logic_santos.physics.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.terrain.geomipmap.TerrainQuad;

import it.unical.logic_santos.controls.SpatialEntityPicker;
import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.environment.sound.HumanSoundManager;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.logging.ILogger;
import it.unical.logic_santos.logging.ILoggingEvent;
import it.unical.logic_santos.logging.LoggingManager;
import it.unical.logic_santos.logging.VehicleLoggingEvent;
import it.unical.logic_santos.net.Vector3fCookie;
import it.unical.logic_santos.roads_network.PathsNetwork;
import it.unical.logic_santos.roads_network.RoadNode;
import it.unical.logic_santos.roads_network.RoadsNetwork;
import it.unical.logic_santos.spatial_entity.AbstractAircraft;
import it.unical.logic_santos.spatial_entity.AbstractHuman;
import it.unical.logic_santos.spatial_entity.AbstractVehicle;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.spatial_entity.Player;
import it.unical.logic_santos.toolkit.data_structure.Arc;
import it.unical.logic_santos.toolkit.math.MathGameToolkit;
import it.unical.logic_santos.toolkit.math.Vector2D;
import it.unical.logic_santos.toolkit.math.Vector3D;
import it.unical.logic_santos.traffic.WalkerTrafficManager;
import it.unical.logic_santos.universe.AbstractWorld;

/**
 * @author Agostino
 *
 */
public class WalkerPhysicalActivity extends HumanPhysicalActivity {

	private PathsNetwork pathsNetwork=null;
	private RoadNode startNode=null;
	private RoadNode targetNode=null; 
	
	protected AbstractWorld world=null;
	
	private float currentTimeForRunningOnShoot=0.0f; 
	private boolean runningOnShoot=false;
	private Vector3f previousPosition=null;
	private float idleTime=0.0f;
	private float hitReactionTime=0.0f;
	
	private List< RoadNode > lastTargetNodes=new LinkedList< RoadNode >();
	private AbstractVehicle followedVehicle=null;
	
	private WalkerTrafficManager trafficManager=null;
	
	private List< AnimControl > animationControls=new ArrayList< AnimControl >();
	private List< AnimChannel > animationChannels=new ArrayList< AnimChannel >();
	
	private int id;
	
	private boolean followVehicleLoggingEvent=false;
	private float nextRunningTimeFollowingVehicle=0.0f;
	
	private static final float   MIN_DISTANCE_OBJECT_DETECTION = 0.0f;
	private static final float   MAX_DISTANCE_OBJECT_DETECTION = 30.0f;
	private static final float[] ANGLES_RAYS_OBJECT_DETECTION = 
									{ 0.0f, MathGameToolkit.toRadiants(5.0f), MathGameToolkit.toRadiants(15.0f) };
	private static final float   Y_OFFSET_ORIGIN_RAYS_OBJECT_DETECTION = 1.0f;
	private static final float   TIME_MILLIS_FOR_RUNNING_ON_SHOOT = 5.0f;
	private static final float   MAX_DISTANCE_FOLLOWING_VEHICLE = 100.0f;
	private static final float   MIN_DISTANCE_FOLLOWING_VEHICLE = 30.0f;
	
	private static final float   MAX_IDLE_TIME = 5.0f; // expressed in seconds
	private static final float   MAX_HIT_REACTION_TIME = 5.0f; // expressed in seconds
	
	private static final int     MAX_SIZE_LAST_TARGET_NODES = 3;
	
	
	public WalkerPhysicalActivity(AbstractHuman owner) {
		super(owner);
	}
	
	public WalkerPhysicalActivity(AbstractHuman owner, AbstractWorld world) {
		super(owner);
		this.world = world;
	}
	
	public void initOrientation(final PathsNetwork pathsNetwork) {
		final int startNodeId = owner.getStartNodeId();
		final List<RoadNode> adjacentsNodes = new ArrayList<RoadNode>();
		Arc e = pathsNetwork.firstAdjacent(startNodeId);
		while (!e.isNull()) {
			adjacentsNodes.add(pathsNetwork.getNode(e.fin()));
			e = pathsNetwork.nextAdjacent(e);
		}
		
		final Vector3D walkerDirection = Vector3D.calculateDirection(new Vector3D(1.0f, 0.0f, 0.0f), pathsNetwork.getNode(startNodeId).getPosition());
		final RoadNode target = VehiclePhysicalActivity.findRightestTargetNode(adjacentsNodes, pathsNetwork.getNode(startNodeId), walkerDirection);
		
		if (target != null) {
			this.control.setWalkDirection(walkerDirection.toVector3f());
		}
		this.targetNode = target;
	
	}
	
	@Override
	public void setInPathsNetwork(PathsNetwork pathsNetwork, RoadNode startNode) {
		this.pathsNetwork = pathsNetwork;
		this.startNode = startNode;		
		
	}

	@Override
	public void setInPathsNetwork(PathsNetwork pathsNetwork) {
		this.pathsNetwork = pathsNetwork;
		
	}

	@Override
	public void setStartPathsNetworkNode(RoadNode startNode) {
		this.startNode = startNode;
		
	}
	
	@Override
	public void update(float tpf) {
		super.update(tpf);
		
		if ( (!isInPathsNetwork()) || (getLifeBar().isEmpty()) ) {
			this.owner.getWalkerPhysicalActivity().stopWalking();
			return;
		}
		
		if ( runningOnShoot ) {
			currentTimeForRunningOnShoot += tpf;
			if ( currentTimeForRunningOnShoot>=TIME_MILLIS_FOR_RUNNING_ON_SHOOT ) {
				currentTimeForRunningOnShoot=0.0f;
				runningOnShoot=false;
				currentRunningScale=1.0f;
				
				//if ( !this.hashFollowedVehicle() )
				//	HumanSoundManager.getInstance().changeToRoamingWalkerSoundState( owner );
			}
		}
		
		final boolean collision = ( checkCollisionWithHumans() || checkCollisionWithAircrafts() );
		
		if ( !collision ) {
			boolean onMaxIdleTime=false;
			if ( idleTime>(MAX_IDLE_TIME*Math.random()) ) {
				final RoadNode tmpTargetNode = targetNode;
				this.targetNode = this.startNode;
				this.startNode = tmpTargetNode;
				idleTime=0.0f;
				onMaxIdleTime=true;
				lastTargetNodes.clear();
			}
			
			if ( (this.hasTargetNode() /*|| this.hashFollowedVehicle()*/) && detectSpatialEntitytInFront(this.control.getPhysicsLocation(), this.control.getViewDirection()) && (!onMaxIdleTime) ) {
				this.owner.getWalkerPhysicalActivity().stopWalking();
				idleTime+=tpf;
				animation.setAnimation( HumanAnimation.IDLE );
				
			} else if ( this.hashFollowedVehicle() ) {
				followVehicle( tpf );
				idleTime=0.0f;
			} else if (this.hasTargetNode()) {
				
				if (targetNodeReached()) {
					addNewLastTargetNode( targetNode );
					this.startNode = targetNode;
					targetNode=null;
					this.owner.getWalkerPhysicalActivity().walk(Vector3f.ZERO, tpf, false);
					animation.setAnimation( HumanAnimation.IDLE );
				} else {
				
					final Vector3f walkingPosition = this.control.getPhysicsLocation();
					final Vector3f targetPosition  = this.targetNode.getPosition().toVector3f();
					final Vector3f walkingDirection = Vector3D.calculateDirection(new Vector3D(walkingPosition), 
																					new Vector3D(targetPosition)).toVector3f().normalize();
					this.owner.getWalkerPhysicalActivity().walk(walkingDirection.mult(0.1f), tpf, true);
					if ( isRunning())
						animation.setAnimation( HumanAnimation.RUNNING );
					else
						animation.setAnimation( HumanAnimation.WALKING );
				}
				idleTime=0.0f;
			} else {
				
				final List<RoadNode> adjacentsNodes = new ArrayList<RoadNode>();
				Arc e = pathsNetwork.firstAdjacent(startNode.getId());
				while (!e.isNull()) {
					adjacentsNodes.add(pathsNetwork.getNode(e.fin()));
					e = pathsNetwork.nextAdjacent(e);
				}
				targetNode = findRandomTargetNode(adjacentsNodes, lastTargetNodes, startNode, new Vector3D(this.control.getWalkDirection()));// VehiclePhysicalActivity.findRightestTargetNode(adjacentsNodes, startNode, new Vector3D(this.control.getWalkDirection()));
				idleTime=0.0f;
				this.owner.getWalkerPhysicalActivity().stopWalking();
				animation.setAnimation( HumanAnimation.IDLE );
			}
		} 
		
		if ( collision ) {
			this.control.setWalkDirection( Vector3f.ZERO.clone() );
			if ( hashFollowedVehicle() )
				animation.setAnimation( HumanAnimation.FIGHT_IDLE );
			else
				animation.setAnimation( HumanAnimation.IDLE );
		}
		
		if ( (!hashFollowedVehicle()) && (followVehicleLoggingEvent) ) {
			ILogger logger = LoggingManager.getDefaultLogger();
			ILoggingEvent event = new VehicleLoggingEvent( 
					VehicleLoggingEvent.LEAVE_VEHICLE_DRIVER_BEHIND_EVENT
					);
			logger.log( event );
			followVehicleLoggingEvent=false;
		}

		if ( ( this.trafficManager!=null ) && 
				( this.trafficManager.getApplication().getGraphicalTerrain().checkOutOfBound( this.control.getPhysicsLocation() ) ) &&
				( this.previousPosition!=null ) )
			this.control.setPhysicsLocation( this.previousPosition );
		else if ( !collision )
			this.previousPosition = this.control.getPhysicsLocation().clone();

		
	}
	
	public boolean isInPathsNetwork() {
		return ( (pathsNetwork!=null) && (startNode!=null) );
	}
	
	public boolean hasTargetNode() {
		return (targetNode!=null);
	}
	
	public void clearTergetNode() {
		targetNode=null;
	}
	
	public boolean hasStartNode() {
		return (startNode!=null);
	}
	
	public RoadNode getTargetNode() {
		return targetNode;
	}
	
	public void setTargetNode(RoadNode targetNode) {
		this.targetNode = targetNode;
	}
	
	public RoadNode getStartNode() {
		return startNode;
	}
	
	public PathsNetwork getPathsNetwork() {
		return pathsNetwork;
	}
	
	public boolean targetNodeReached() {
		Vector3D myPosition = new Vector3D(this.control.getPhysicsLocation().clone());
		myPosition.setZ(-myPosition.getZ());
		Vector3D targetPosition = targetNode.getPosition().clone();
		targetPosition.setZ(-targetPosition.getZ());
		return (myPosition.distanceTo(targetPosition) <= 10.0f);
	}
	
	public AbstractWorld getWorld() {
		return world;
	}
	
	public void setWorld(AbstractWorld world) {
		this.world = world;
	}
	
	public boolean hashFollowedVehicle() {
		return ( this.followedVehicle!=null );
	}
	
	public void setFollowedVehicle(AbstractVehicle followedVehicle) {
		this.followedVehicle = followedVehicle;
		if ( this.followedVehicle!=null ) {
			this.clearTergetNode();
			
			final Vector3f walkingPosition = this.control.getPhysicsLocation();
			final Vector3f targetPosition  = this.followedVehicle.getVehiclePhysicalActivity().getControl().getPhysicsLocation();
			Vector3f viewDirection = Vector3D.calculateDirection(new Vector3D(walkingPosition), 
					new Vector3D(targetPosition)).toVector3f().normalize();
			viewDirection.setY( 0.0f );
			this.owner.getWalkerPhysicalActivity().getControl().setViewDirection( viewDirection );
			
			followVehicleLoggingEvent=true;
		}
	}
	
	public void clearFollowedVehicle() {
		this.followedVehicle=null;
	}
	
	public void setTrafficManager(WalkerTrafficManager trafficManager) {
		this.trafficManager = trafficManager;
	}
	
	public boolean detectSpatialEntitytInFront( final Vector3f walkerPosition, final Vector3f walkerViewDirection ) {
		if ( this.trafficManager==null )
			return false;
		
		int ratio;
		Vector3f position = walkerPosition.clone();
		position.setY( position.getY()+Y_OFFSET_ORIGIN_RAYS_OBJECT_DETECTION );
		
		for(int iAngle=0; iAngle<ANGLES_RAYS_OBJECT_DETECTION.length; ++iAngle) {
			
			ratio=-1;
			do {
				
				Vector2f direction2d = new Vector2f( walkerViewDirection.getX(), walkerViewDirection.getZ() );
				direction2d.rotateAroundOrigin( ANGLES_RAYS_OBJECT_DETECTION[iAngle], ratio<0 );
				
				final Vector3f direction = new Vector3f( direction2d.getX(), walkerViewDirection.getY(), direction2d.getY() );
				
				/*if (ANGLES_RAYS_OBJECT_DETECTION[iAngle]==0.0f)
					showDirection(new Vector3D(position), new Vector3D(direction.mult(1.0f)));
				else
					showTargetDirection(new Vector3D(position), new Vector3D(direction.mult(1.0f)));*/
				
				Ray ray = new Ray(position, direction);
				CollisionResults results = new CollisionResults();
				List< ISpatialEntity > entities = this.trafficManager.getDetectableSpatialEntitiesForWalker( getAbstractHumanOwner() );
				
				for(ISpatialEntity e: entities) {
					
					if (e!=this.getSpatialEntityOwner()) {
						Spatial s = ( (ModelBasedPhysicalExtension) e.getAbstractPhysicalExtension() ).getModelSpatial();
						s.collideWith(ray, results);
						
						for(int i=0; i<results.size(); ++i) {
							if ( (results.getCollision(i).getDistance() >= MIN_DISTANCE_OBJECT_DETECTION) && 
								 (results.getCollision(i).getDistance() <= MAX_DISTANCE_OBJECT_DETECTION) )
								return true;
						}
					}
				}
				
				ratio+=2;
			} while( (ANGLES_RAYS_OBJECT_DETECTION[iAngle]!=0.0f) && (ratio<=1) );
		
		}
		return false;
	}
	
	@Override
	public void run() {
		super.run();
		currentTimeForRunningOnShoot = 0.0f;
		runningOnShoot = true;
		//animation.setAnimation( HumanAnimation.RUNNING );
	}
	
	public void onHitReaction( final float tpf ) {
		if ( !animation.equals( HumanAnimation.HIT_REACTION ) )
			animation.setAnimation( HumanAnimation.HIT_REACTION );
		
		if ( hitReactionTime<MAX_HIT_REACTION_TIME )
			hitReactionTime+=tpf;
	}
	
	public boolean isAfterHitReaction() {
		return ( hitReactionTime>=MAX_HIT_REACTION_TIME );
	}
	
	public void clearHitReactionTime() {
		hitReactionTime=0.0f;
	}
	
	private void followVehicle( final float tpf ) {
		if ( !hashFollowedVehicle() )
			return;
		final Vector3f walkingPosition = this.control.getPhysicsLocation();
		final Vector3f targetPosition  = this.followedVehicle.getVehiclePhysicalActivity().getControl().getPhysicsLocation();
		final float distance = walkingPosition.distance( targetPosition );
		if ( distance<MIN_DISTANCE_FOLLOWING_VEHICLE ) {
			this.owner.getWalkerPhysicalActivity().stopWalking();
			Vector3f viewDirection = Vector3D.calculateDirection(new Vector3D(walkingPosition), 
					new Vector3D(targetPosition)).toVector3f().normalize();
			viewDirection.setY( 0.0f );
			this.owner.getWalkerPhysicalActivity().getControl().setViewDirection( viewDirection );
			animation.setAnimation( HumanAnimation.FIGHT_IDLE );
			nextRunningTimeFollowingVehicle=0.0f;
			return;
		} else if ( distance>MAX_DISTANCE_FOLLOWING_VEHICLE ) {
			clearFollowedVehicle();
			RoadNode newTargetNode = findNearestTargetNodeToPosition( this.control.getPhysicsLocation() );
			if ( newTargetNode!=null )
				setTargetNode( newTargetNode );
			else
				clearTergetNode();
			return;
		}
			
		if ( nextRunningTimeFollowingVehicle>=1.0f ) {
			final Vector3f walkingDirection = Vector3D.calculateDirection(new Vector3D(walkingPosition), 
																			new Vector3D(targetPosition)).toVector3f().normalize();
			this.owner.getWalkerPhysicalActivity().walk(walkingDirection.mult(0.1f), tpf, true);
			this.owner.getWalkerPhysicalActivity().run();
			animation.setAnimation( HumanAnimation.RUNNING );
		} else {
			this.owner.getWalkerPhysicalActivity().stopWalking();
			animation.setAnimation( HumanAnimation.FIGHT_IDLE );
			nextRunningTimeFollowingVehicle+=tpf;
		}
	}
	
	public RoadNode findNearestTargetNodeToPosition( final Vector3f position ) {
		final int n=pathsNetwork.nodesCount();
		RoadNode nearestNode=null;
		float minDistance=Float.MAX_VALUE;
		float distance;
		
		for( int i=1; i<=n; ++i ) {
			
			RoadNode currentNode = pathsNetwork.getNode( i );
			distance = currentNode.getPosition().toVector3f().distance( position );
			
			if ( (nearestNode==null) || ( (nearestNode!=null) && (distance<minDistance) ) ) {
				nearestNode = currentNode;
				minDistance = distance;
			}
		}
		
		return nearestNode;
	}
	
	@Override
	public void onCollisionWithAbstractHuman( AbstractHuman other ) {
		if ( this.previousPosition!=null ) {
			//this.owner.getWalkerPhysicalActivity().walk(Vector3f.ZERO, 0.0f, false);
			//this.control.setWalkDirection( Vector3f.ZERO.clone() );
			this.control.setPhysicsLocation( this.previousPosition );
		}
	}
	
	@Override
	public void onCollisionWithAbstractAircraft( AbstractAircraft other ) {
		if ( this.previousPosition!=null ) {
			//this.owner.getWalkerPhysicalActivity().walk(Vector3f.ZERO, 0.0f, false);
			//this.control.setWalkDirection( Vector3f.ZERO.clone() );
			this.control.setPhysicsLocation( this.previousPosition );
		}
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public WalkerCookie getWalkerCookie() {
		WalkerCookie cookie = new WalkerCookie();
		cookie.id = this.id;
		
		cookie.positionCookie.
			set(this.control.getPhysicsLocation().clone());
		cookie.viewDirectionCookie.set(this.control.getViewDirection().clone());
		cookie.walkDirectionCookie.set(this.control.getWalkDirection().clone());
		
		cookie.lifeBarCookie.set(this.lifeBar);
		cookie.animationCookie.set(this.animation);
		
		cookie.startNodeId = getNodeId(this.startNode);
		cookie.targetNodeId = getNodeId(this.targetNode);
		
		cookie.currentTimeForRunningOnShoot = this.currentTimeForRunningOnShoot;
		cookie.currentRunningScale = this.currentRunningScale;
		cookie.runningOnShoot = this.runningOnShoot;
		
		cookie.idleTime = this.idleTime;
		cookie.hitReactionTime = this.hitReactionTime;
		
		if ( followedVehicle==null )
			cookie.followedVehicleId = -1;
		else
			cookie.followedVehicleId = followedVehicle.getVehiclePhysicalActivity().getId();
		
		cookie.lastTargetNodesIds.clear();
		final float size = lastTargetNodes.size();
		for( int i=0; i<size; ++i )
			cookie.lastTargetNodesIds.add(getNodeId(this.lastTargetNodes.get(i)));
		
		return cookie;
	}
	
	public void setWalkerCookie( final WalkerCookie cookie ) {
		this.id = cookie.id;
		
		if (!cookie.isActive())
			return;
		
		this.control.setPhysicsLocation(cookie.positionCookie.vector.clone());
		this.control.setViewDirection(cookie.viewDirectionCookie.vector.clone());
		this.control.setWalkDirection(cookie.walkDirectionCookie.vector.clone());
		//this.walk(cookie.walkDirectionCookie.vector.clone().mult(0.1f), 0.0f, true);
		
		this.lifeBar.setCookie(cookie.lifeBarCookie);
		this.animation.setCookie(cookie.animationCookie);
		this.setAnimation( this.animation.getAnimationName() );
		
		this.startNode = getNodeFromId(cookie.startNodeId, pathsNetwork);
		this.targetNode = getNodeFromId(cookie.targetNodeId, pathsNetwork);
		
		// TODO: verify necessity of this data!
		this.currentTimeForRunningOnShoot = cookie.currentTimeForRunningOnShoot;
		this.currentRunningScale = cookie.currentRunningScale;
		this.runningOnShoot = cookie.runningOnShoot;
		
		this.idleTime = cookie.idleTime;
		this.hitReactionTime = cookie.hitReactionTime;
		
		if ( (cookie.followedVehicleId!=-1) && (trafficManager!=null) ) {
			clearTergetNode();
			List< AbstractVehicle > vehicles = trafficManager.getApplication().getVehicles();
			boolean found=false;
			
			for (Iterator< AbstractVehicle > it = vehicles.iterator(); ( it.hasNext() && (!found) );) {
				AbstractVehicle vehicle = it.next();
				
				if ( vehicle.getVehiclePhysicalActivity().getId() == cookie.followedVehicleId ) {
					this.followedVehicle = vehicle;
					found = true;
				}
			}
			
			if ( !found )
				this.followedVehicle=null;
			
		}
		
		this.lastTargetNodes.clear();
		final int size = cookie.lastTargetNodesIds.size();
		for( int i=0; i<size; ++i )
			this.lastTargetNodes.add(getNodeFromId(cookie.lastTargetNodesIds.get(i), pathsNetwork));
		
	}
	
	private boolean checkCollisionWithHumans() {
		if ( this.trafficManager==null )
			return false;
		
		List< AbstractHuman > humans = this.trafficManager.getApplication().getHumans();
		AbstractHuman[] humansArray = new AbstractHuman[ humans.size() ];
		
		int i=0;
		for( AbstractHuman h: humans ) {
			humansArray[i] = h;
			i++;
		}
		
		for( i=0; i<humansArray.length; ++i )
			if ( (humansArray[i]!=this.owner) && AbstractHuman.collisionHumans( this.owner, humansArray[i] ) ) {
				onCollisionWithAbstractHuman( humansArray[i] );
				return true;
			}
		return false;
	}
	
	private boolean checkCollisionWithAircrafts() {
		if ( this.trafficManager==null )
			return false;
		
		List< AbstractAircraft > aircrafts = this.trafficManager.getApplication().getAircrafts();
		AbstractAircraft[] aircraftsArray = new AbstractAircraft[ aircrafts.size() ];
		
		int i=0;
		for( AbstractAircraft a: aircrafts ) {
			aircraftsArray[i] = a;
			i++;
		}
		
		for( i=0; i<aircraftsArray.length; ++i )
			if ( AbstractAircraft.collisionAircraftHuman( aircraftsArray[i], this.owner ) ) {
				onCollisionWithAbstractAircraft( aircraftsArray[i] );
				return true;
			}
		return false;
	}
	
	private void addNewLastTargetNode( RoadNode newTargetNode ) {
		if ( lastTargetNodes.size()>=MAX_SIZE_LAST_TARGET_NODES )
			lastTargetNodes.remove( 0 );
		lastTargetNodes.add( newTargetNode );
	}
	
	private static RoadNode findRandomTargetNode(final List<RoadNode> adjacentNodes, final List<RoadNode> lastTargetNodes, final RoadNode startNode, final Vector3D vehicleDirection) {
		if (adjacentNodes.isEmpty()) { //System.out.println("ADJACENT LIST EMPTY!");
			return null;
		}
		
		List< RoadNode > higherPriorityAdjacentNodes = new ArrayList< RoadNode >();
		for( RoadNode n: adjacentNodes )
			if ( !lastTargetNodes.contains( n ) )
				higherPriorityAdjacentNodes.add( n );
		
		Vector2D vehicleDirection2d = new Vector2D(vehicleDirection.getX(), vehicleDirection.getZ());
		vehicleDirection2d.normalize();
		
		RoadNode target = null;
	
		if ( higherPriorityAdjacentNodes.isEmpty() ) {
			target=VehiclePhysicalActivity.findRightestTargetNode(adjacentNodes, startNode, vehicleDirection);
		} else {
			target=VehiclePhysicalActivity.findRightestTargetNode(higherPriorityAdjacentNodes, startNode, vehicleDirection);
		}
		
		return target;
	
	}
	
	private Spatial directionArrow=null;
	public Node rootNode=null;
	private void showDirection(Vector3D start, Vector3D direction) {
		
		if (directionArrow!=null) {
			rootNode.detachChild(directionArrow);
			//return;
		}
		
		
		//start.setY(start.getY() + 10.0f);
		//System.out.println("START POSITION: " + start);
		Arrow mesh = new Arrow(Vector3D.calculateDirection(start, start.clone().add(direction.scale(10.0f))).toVector3f());
			
		directionArrow = new Geometry("arrow", mesh);
		Material mat = new Material(LogicSantosApplication.ASSET_MANAGER_APPLICATION, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setLineWidth(3.0f);
		mat.setColor("Color", ColorRGBA.Blue);
		directionArrow.setMaterial(mat);
		directionArrow.setLocalTranslation(start.toVector3f());
		directionArrow.setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
		
		rootNode.attachChild(directionArrow);
			
	}
	
	private Spatial targetDirectionArrow=null;
	private void showTargetDirection(Vector3D start, Vector3D direction) {
		
		if (targetDirectionArrow!=null) {
			rootNode.detachChild(targetDirectionArrow);
			//return;
		}
		
		
		//start.setY(start.getY() + 10.0f);
		//System.out.println("START POSITION: " + start);
		Arrow mesh = new Arrow(Vector3D.calculateDirection(start, start.clone().add(direction.scale(10.0f))).toVector3f());
			
		targetDirectionArrow = new Geometry("arrow", mesh);
		Material mat = new Material(LogicSantosApplication.ASSET_MANAGER_APPLICATION, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setLineWidth(3.0f);
		mat.setColor("Color", ColorRGBA.Green);
		targetDirectionArrow.setMaterial(mat);
		targetDirectionArrow.setLocalTranslation(start.toVector3f());
		targetDirectionArrow.setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
		
		rootNode.attachChild(targetDirectionArrow);
			
	}
	
	
	/* animation override methods */
	
	@Override
	protected void initAnimation() {
		
		/** init AnimationChannel and AnimationControl */
		if ( animationControl==null ) {
			
			Node walkerNode = (Node) ( (ModelBasedPhysicalExtension) getAbstractHumanOwner().getAbstractPhysicalExtension() ).getModelSpatial();
			animationControl = findAnimControl( walkerNode, null );
			
			animationControls.clear();
			animationChannels.clear();
			
			AnimControl newAnimationControl=null;
			do {
				newAnimationControl = findNextAnimControl( walkerNode, animationControls );
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
	public void updateAnimation(Vector3f walkDirection, float airTime) {
		super.updateAnimation(walkDirection, airTime);
		if ( animationChannels.isEmpty() )
			return;
		setAnimation( animation.getAnimationName() );
	}
	
	@Override
	public void setAnimation( final String animationName ) {
		for( AnimChannel ac: animationChannels )
			if ( !animationName.equals( ac.getAnimationName()) )
					ac.setAnim( animationName );
	}
	
	private void setAnimation( final String animationName, final float blendTime ) {
		for( AnimChannel ac: animationChannels )
			if ( !animationName.equals( ac.getAnimationName()) )
				ac.setAnim( animationName, blendTime );
	}
	
	private static int getNodeId( final RoadNode node ) {
		if ( node!=null )
			return ( node.getId() );
		return -1;
	}
	
	private static RoadNode getNodeFromId( final int id, final PathsNetwork pathNetwork ) {
		if ( id>0 )
			return ( pathNetwork.getNode(id) );
		return null;
	}

}
