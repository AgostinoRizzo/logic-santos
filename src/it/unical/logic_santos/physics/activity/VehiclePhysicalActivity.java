/**
 * 
 */
package it.unical.logic_santos.physics.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.vecmath.Vector3d;

import com.bulletphysics.collision.dispatch.GhostObject;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.objects.PhysicsVehicle;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Cylinder;

import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.design.modeling.SimplePhysicalExtensionGenerator;
import it.unical.logic_santos.environment.sound.EffectSoundManager;
import it.unical.logic_santos.gameplay.IBullet;
import it.unical.logic_santos.gameplay.LifeBar;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.logging.ILogger;
import it.unical.logic_santos.logging.ILoggingEvent;
import it.unical.logic_santos.logging.LoggingManager;
import it.unical.logic_santos.logging.VehicleLoggingEvent;
import it.unical.logic_santos.physics.extension.AbstractBoundingVolume;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.roads_network.PathsNetwork;
import it.unical.logic_santos.roads_network.RoadNode;
import it.unical.logic_santos.roads_network.RoadsNetwork;
import it.unical.logic_santos.spatial_entity.AbstractHuman;
import it.unical.logic_santos.spatial_entity.AbstractVehicle;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.toolkit.data_structure.Arc;
import it.unical.logic_santos.toolkit.math.MathGameToolkit;
import it.unical.logic_santos.toolkit.math.Point;
import it.unical.logic_santos.toolkit.math.Vector2D;
import it.unical.logic_santos.toolkit.math.Vector3D;
import it.unical.logic_santos.traffic.VehicleTrafficManager;

/**
 * @author Agostino
 *
 */
public class VehiclePhysicalActivity implements IPhysicalActivity {
	
	private AbstractVehicle owner=null;
	private VehicleControl control=null;
	private Vector3f initialVehicleForwardDirection=null;
	private Vector3f currentLinearVelocity=null;
	
	private RoadsNetwork roadsNetwork=null;
	private RoadNode startNode=null;
	private RoadNode targetNode=null; 
	
	private Vector3f previousPosition=null;
	private Vector3D previousDirection=null;
	
	private LifeBar lifeBar=null;
	private boolean isDriven=false;
	private boolean hasWalkerDriver=true;
	
	private AbstractHuman driver=null;
	
	private float steeringDriveValue=0.0f;
	private float accelerationDriveValue=0.0f;
	
	private VehicleTrafficManager trafficManager=null;
	private int id;
	
	private Node carNode=null;
	
	private static final float STIFFNESS = 120.0f;
	private static final float COMP_VALUE = 0.02f;
	private static final float DAMP_VELUE = 0.3f;
	private static final float MAX_SUSPENSION_FORCE = 10000.0f;
	
	private static final float MAX_VELOCITY = 10.0f;
	private static final float MAX_ABS_STEERING_VALUE = 1.0f;
	private static final float MAX_STEER_DEGREE = 1.07853f;
	
	private static final float   MIN_DIST_BETWEEN_VEHICLE = 100.0f;
	private static final float   MIN_DIST_BETWEEN_HUMAN   = 100.0f;
	private static final float   MIN_DIST_BETWEEN_OBJECT  = 100.0f;
	private static final float   MIN_DISTANCE_OBJECT_DETECTION = 0.0f;
	private static final float   MAX_DISTANCE_OBJECT_DETECTION = 30.0f;
	private static final float[] ANGLES_RAYS_OBJECT_DETECTION = 
									{ 0.0f, MathGameToolkit.toRadiants(5.0f), MathGameToolkit.toRadiants(15.0f) };
	private static final float   Y_OFFSET_ORIGIN_RAYS_OBJECT_DETECTION = 1.0f;
	
	private static final Vector3f ON_SHOOT_EMPTY_LIFE_FORCE = Vector3f.UNIT_Y.mult(100000.0f);
	private static final Vector3f ON_SHOOT_EMPTY_LIFE_FORCE_LOCATION = new Vector3f( 10.0f, 0.0f, 0.0f );
	
	private static final float DISTANCE_TO_REACH_NODE = 5.0f;
	
	private static final float PI  = (float) Math.PI;
	private static final float PI2 = (float) (Math.PI*2.0f);
	
	private float[] axesAngles=new float[3];
	
	
	public VehiclePhysicalActivity(AbstractVehicle owner){
		this.owner = owner;
		this.initialVehicleForwardDirection = new Vector3f();
		this.currentLinearVelocity = new Vector3f();
		this.lifeBar = new LifeBar( LifeBar.FULL_VALUE );
	}
	
	@Override
	public void initActivity() {
		
		carNode = (Node) ((ModelBasedPhysicalExtension) this.owner.getAbstractPhysicalExtension()).getModelSpatial();
		
		Geometry chasis = findGeom(carNode, "Car");
		chasis.scale(ModelingConfig.VEHICLE_MODEL_SCALE_FACTOR);
		chasis.rotate(0.0f, (float) Math.PI, 0.0f);
		BoundingBox box=(BoundingBox) chasis.getModelBound();
		
		this.control = new VehicleControl(CollisionShapeFactory.createDynamicMeshShape(
				chasis), 
				owner.getMass());
		carNode.addControl(this.control);
		
		this.control.setSuspensionCompression((float) (COMP_VALUE * 2.0f * Math.sqrt(STIFFNESS)));
		this.control.setSuspensionDamping((float) (DAMP_VELUE * 2.0f * Math.sqrt(STIFFNESS)));
		this.control.setSuspensionStiffness(STIFFNESS);
		this.control.setMaxSuspensionForce(MAX_SUSPENSION_FORCE);
		
		final Vector3f wheelDirection = new Vector3f(0.0f, -1.0f, 0.0f);
		final Vector3f wheelAxle = new Vector3f(-1.0f, 0.0f, 0.0f);
		
		
		
		final float scaleFactor = ModelingConfig.VEHICLE_MODEL_SCALE_FACTOR;
		final float frontScaleAdd = 0.3f;
		final float backScaleAdd = 0.3f;
		
		final float frontYOff = -1.8f;
		final float backYOff = -2.0f;
		final float xOff = 4.3f;
		final float frontZOff = 7.0f;
		final float backZOff = 8.5f;
		
		
		
		/* WHEEL FRONT-RIGHT */
		Geometry wheel_fr = findGeom(carNode, "WheelFrontRight");
		wheel_fr.scale(ModelingConfig.VEHICLE_MODEL_SCALE_FACTOR);
		wheel_fr.center();
		box = (BoundingBox) wheel_fr.getModelBound();
		final float wheelRadius = box.getYExtent() * ModelingConfig.VEHICLE_MODEL_SCALE_FACTOR;
		final float back_wheel_h = ((wheelRadius * 1.7f) - 1.0f) - ModelingConfig.VEHICLE_MODEL_SCALE_FACTOR + 1.0f;
		final float front_wheel_h = ((wheelRadius * 1.9f) -1.0f) - ModelingConfig.VEHICLE_MODEL_SCALE_FACTOR + 1.0f;
		this.control.addWheel(wheel_fr.getParent(), box.getCenter().add(xOff, -front_wheel_h, -backZOff), 
				wheelDirection, wheelAxle, 0.2f, wheelRadius, false);
		
		/* WHEEL FRONT-LEFT */
		Geometry wheel_fl = findGeom(carNode, "WheelFrontLeft");
		wheel_fl.scale(ModelingConfig.VEHICLE_MODEL_SCALE_FACTOR);
		wheel_fl.center();
		box = (BoundingBox) wheel_fl.getModelBound();
		this.control.addWheel(wheel_fl.getParent(), box.getCenter().add(-xOff, -front_wheel_h, -backZOff), 
				wheelDirection, wheelAxle, 0.2f, wheelRadius, false);
		
		/* WHEEL BACK-RIGHT */
		Geometry wheel_br = findGeom(carNode, "WheelBackRight");
		wheel_br.scale(ModelingConfig.VEHICLE_MODEL_SCALE_FACTOR);
		wheel_br.center();
		box = (BoundingBox) wheel_br.getModelBound();
		this.control.addWheel(wheel_br.getParent(), box.getCenter().add(xOff, -back_wheel_h, frontZOff), 
				wheelDirection, wheelAxle, 0.2f, wheelRadius, true);
		
		/* WHEEL BACK-LEFT */
		Geometry wheel_bl = findGeom(carNode, "WheelBackLeft");
		wheel_bl.scale(ModelingConfig.VEHICLE_MODEL_SCALE_FACTOR);
		wheel_bl.center();
		box = (BoundingBox) wheel_bl.getModelBound();
		this.control.addWheel(wheel_bl.getParent(), box.getCenter().add(-xOff, -back_wheel_h, frontZOff), 
				wheelDirection, wheelAxle, 0.2f, wheelRadius, true);
		
		this.control.getWheel(2).setFrictionSlip(4);
		this.control.getWheel(3).setFrictionSlip(4);
		
		PhysicsSpace.getInstance().getSpace().add(this.control);
		this.control.setLinearSleepingThreshold(0.5f);
		//((ModelBasedPhysicalExtension) this.owner.getAbstractPhysicalExtension()).setModelSpatial(carNode);
		//this.control.accelerate(-300.0f);
		//this.control.setCollisionShape(CollisionShapeFactory.createDynamicMeshShape(findGeom(carNode, "Car")));
	
	}
	
	@Override
	public void finalizeActivity() {
		getOwnerSpatial().removeControl(this.control);
		PhysicsSpace.getInstance().getSpace().remove(this.control);
	}

	@Override
	public void setInRoadsNetwork(RoadsNetwork roadsNetwork, RoadNode startNode) {
		this.roadsNetwork = roadsNetwork;
		this.startNode = startNode;		
	}
	
	@Override
	public void setInRoadsNetwork(RoadsNetwork roadsNetwork) {
		this.roadsNetwork = roadsNetwork;
	}
	
	public void setStartRoadsNetworkNode(RoadNode startNode) {
		this.startNode = startNode;
	}
	
	@Override
	public ISpatialEntity getSpatialEntityOwner() {
		return owner;
	}

	@Override
	public void setSpatialEntityOwner(ISpatialEntity spatialEntity) {
		if (spatialEntity instanceof AbstractVehicle)
			this.owner = (AbstractVehicle) spatialEntity;
	}

	@Override
	public void updateTranslation() {
		control.setPhysicsLocation(owner.getSpatialTranslation().toVector3f());		
	}
	
	public VehicleControl getControl() {
		return control;
	}
	
	public Node getNode() {
		return carNode;
	}
	
	public LifeBar getLifeBar() {
		return lifeBar;
	}
	
	public boolean isDriven() {
		return isDriven;
	}
	
	public void setIsDriven( final boolean isDriven ) {
		this.isDriven = isDriven;
		if ( this.isDriven ) {
			clearTergetNode();
			hasWalkerDriver=false;
		} else {
			driver = null;
			hasWalkerDriver=true;
		}
		stopVehicle();
	}
	
	public boolean hasDriver() {
		return (this.driver!=null);
	}
	
	public void setDriver( AbstractHuman driver ) {
		this.driver = driver;
	}
	
	public void onDriveLeftAction( final boolean onPress ) {
		if ( onPress )
			steeringDriveValue += 0.5f;
		else
			steeringDriveValue -= 0.5f;
		this.control.steer( steeringDriveValue );
		keepInsideWorldPlatform();
	}
	
	public void onDriveRightAction( final boolean onPress ) {
		if ( onPress )
			steeringDriveValue -= 0.5f;
		else
			steeringDriveValue += 0.5f;
		this.control.steer( steeringDriveValue );
		keepInsideWorldPlatform();
	}
	
	public void onDriveForwardAction( final boolean onPress ) {
		if ( onPress )
			accelerationDriveValue += 800.0f;
		else
			accelerationDriveValue -= 800.0f;
		this.control.accelerate( accelerationDriveValue );
		keepInsideWorldPlatform();
	}
	
	public void onDriveBackwardAction( final boolean onPress ) {
		if ( onPress )
			this.control.brake(40.0f);
		else
			this.control.brake(0.0f);
		keepInsideWorldPlatform();
	}
	
	public Spatial getVehicleSpatial() {
		Node carNode = (Node) ((ModelBasedPhysicalExtension) this.owner.getAbstractPhysicalExtension()).getModelSpatial();
		return findGeom(carNode, "Car");
	}
	
	
	public void initOrientation(final RoadsNetwork roadsNetwork) {System.out.println("DIRECTION COMPUTED: " + computeVehicleForwardDirection());
		final int startNodeId = owner.getStartNodeId();
		final List<RoadNode> adjacentsNodes = new ArrayList<RoadNode>();
		Arc e = roadsNetwork.firstAdjacent(startNodeId);
		while (!e.isNull()) {
			adjacentsNodes.add(roadsNetwork.getNode(e.fin()));
			e = roadsNetwork.nextAdjacent(e);
		}
		
		final Vector3D vehicleDirection = Vector3D.calculateDirection(new Vector3D(1.0f, 0.0f, 0.0f), roadsNetwork.getNode(startNodeId).getPosition());
		final RoadNode target = findRightestTargetNode(adjacentsNodes, roadsNetwork.getNode(startNodeId), vehicleDirection);
		
		if (target != null) {
			//((VehiclePhysicalActivity) getPhysicalActivity()).getControl().
			
			//((ModelBasedPhysicalExtension) owner.getAbstractPhysicalExtension()).getModelSpatial().lookAt( Vector3f.ZERO /*target.getPosition().toVector3f()*/, Vector3f.UNIT_Y);
			
			Vector3f lookDirection = Vector3D.calculateDirection( new Vector3D(this.control.getPhysicsLocation()), target.getPosition() ).toVector3f().normalize();
			Quaternion q=new Quaternion(); 
			q.lookAt( lookDirection, Vector3f.UNIT_Y );
			this.control.setPhysicsRotation(q);
			
			//this.control.setPhysicsRotation(((ModelBasedPhysicalExtension) owner.getAbstractPhysicalExtension()).getModelSpatial().getLocalRotation());
			this.initialVehicleForwardDirection = computeVehicleForwardDirection().toVector3f(); //Vector3D.calculateDirection(roadsNetwork.getNode(startNodeId).getPosition(), target.getPosition()).toVector3f().normalize();
			this.previousDirection = new Vector3D(this.initialVehicleForwardDirection);
			//((ModelBasedPhysicalExtension) owner.getAbstractPhysicalExtension()).getModelSpatial().rotate(0.0f, (float) Math.PI, 0.0f);
		}
		this.targetNode = target;
		
	}
	
	@Override
	public void update(float tpf) {
		if ( (!isInRoadNetwork()) || (this.lifeBar.isEmpty()) )
			return;
		if ( this.isDriven() || (!this.hasWalkerDriver()) ) {
			keepInsideWorldPlatform();
			if ( this.driver!=null )
				this.driver.updateDriverPosition( this.owner );
			return;
		}
		
		
		final Vector3D dir = computeVehicleForwardDirection();
		//showDirection(new Vector3D(this.control.getPhysicsLocation()), dir); 

		if ( detectSpatialEntitytInFront(this.control.getPhysicsLocation(), dir.toVector3f()) ) {
			
			stopVehicle();
			
		} else if (this.hasTargetNode()) {
			
			if (targetNodeReached()) {
				this.startNode = targetNode;
				targetNode=null;
				stopVehicle();
			} else {
				accellerateWithControl(500.0f); 
				//getOwnerSpatial().lookAt(targetNode.getPosition().toVector3f(), Vector3f.UNIT_Y);
				//this.control.setPhysicsRotation(((ModelBasedPhysicalExtension) owner.getAbstractPhysicalExtension()).getModelSpatial().getLocalRotation());
				final Vector3D direction = computeVehicleForwardDirection();
				if (direction!=null)
					this.control.steer(getSteerValue(this.control.getPhysicsLocation(), direction.toVector3f(), targetNode.getPosition().toVector3f()));
			}
			
		} else {
			
			final List<RoadNode> adjacentsNodes = new ArrayList<RoadNode>();
			Arc e = roadsNetwork.firstAdjacent(startNode.getId());
			while (!e.isNull()) {
				adjacentsNodes.add(roadsNetwork.getNode(e.fin()));
				e = roadsNetwork.nextAdjacent(e);
			}
			targetNode = findRightestTargetNode(adjacentsNodes, startNode, computeVehicleForwardDirection());
		}
		
		keepInsideWorldPlatform();
		
	}
	
	public void accellerate(final float tpf) {
		//this.control.accelerate(100.0f);
		//this.control.applyCentralForce(Vector3f.UNIT_X.mult(100000.0f));
		//this.control.applyImpulse(Vector3f.UNIT_X.mult(100.0f), Vector3f.ZERO);
		//this.control.accelerate(100.0f);
		//controlVehicle.accelerate(100.0f);
		//vehicleForwardDirection.setY(0.0f);
		//this.control.applyCentralForce(vehicleForwardDirection.mult(800000.0f));
		//this.control.setLinearVelocity(this.control.getGravity().add(vehicleForwardDirection));
		//this.control.setPhysicsLocation(this.control.getPhysicsLocation().add(vehicleForwardDirection.mult(0.1f)));
	}
	
	public static RoadNode findRightestTargetNode(final List<RoadNode> adjacentNodes, final RoadNode startNode, final Vector3D vehicleDirection) {
		if (adjacentNodes.isEmpty()) { //System.out.println("ADJACENT LIST EMPTY!");
			return null;
		}
		
		Vector2D vehicleDirection2d = new Vector2D(vehicleDirection.getX(), vehicleDirection.getZ());
		vehicleDirection2d.normalize();
		
		Vector2D targetTmp = null;
		RoadNode target = null;
		float angleTarget = 0.0f;
		
		for(RoadNode adjNode: adjacentNodes) {
			
			final Vector3D adjacentDirection = Vector3D.calculateDirection(startNode.getPosition(), adjNode.getPosition());
			Vector2D adjacentDirection2d = new Vector2D(adjacentDirection.getX(), adjacentDirection.getZ());
			adjacentDirection2d.normalize();
			// TODO: modify angle between values
			float angleBetween = vehicleDirection2d.toVector2f().angleBetween(adjacentDirection2d.toVector2f());
			if (angleBetween <= ((float) Math.PI))
				angleBetween += (float) Math.PI;
			else 
				angleBetween -= (float) Math.PI; 
			
			if ( (targetTmp == null) || ((targetTmp != null) && (angleBetween > angleTarget)) ) {
				targetTmp = adjacentDirection2d;
				target = adjNode;
				angleTarget = angleBetween;
			}
			
		}
		
		return target;
	
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
	
	public boolean isInRoadNetwork() {
		return ( (roadsNetwork!=null) && (startNode!=null) );
	}
	
	public boolean hasTargetNode() {
		return (targetNode!=null);
	}
	
	public void clearTergetNode() {
		targetNode=null;
	}
	
	public boolean targetNodeReached() {
		Vector3D myPosition = new Vector3D(this.control.getPhysicsLocation().clone());
		myPosition.setZ(-myPosition.getZ());
		Vector3D targetPosition = targetNode.getPosition().clone();
		targetPosition.setZ(-targetPosition.getZ());
		return (myPosition.distanceTo(targetPosition) <= DISTANCE_TO_REACH_NODE );
	}
	
	public void setTrafficManager(VehicleTrafficManager trafficManager) {
		this.trafficManager = trafficManager;
	}
	
	public Vector3D computeVehicleForwardDirection() {
		
		/*
		final Vector3D currentPosition = new Vector3D(getOwnerSpatial().getLocalTranslation());
		if (previousPosition==null) {
			previousPosition = currentPosition;
			return new Vector3D(this.initialVehicleForwardDirection);
		}
		if (currentPosition.distanceTo(previousPosition) < 3.0f)
			return previousDirection;
		Vector3D direction = Vector3D.calculateDirection(previousPosition, currentPosition);
		previousPosition = currentPosition;
		direction.normalize();
		previousDirection = direction;
		direction = new Vector3D( this.control.getWheel(3).getDirection()).normalize();
		*/
		
		
		this.control.getPhysicsRotation().toAngles(axesAngles);
		float zAngles = axesAngles[1];
		
		if (zAngles >= 0.0f) {
			zAngles = PI2 - zAngles;
		} else {
			zAngles = -zAngles;
		}
		//System.out.println("YAW ANGLES: " + zAngles);
		Vector2D direction2D = new Vector2D(0.0f, -1.0f);
		rotateVector2D(direction2D, zAngles);
		rotateVector2D(direction2D, (float) Math.PI);
		
		Vector3D direction = new Vector3D( direction2D.getX(), 0.0f, direction2D.getY() );
		direction.normalize();
		//System.out.println("COMPUTED VEHICLE FORWARD DIRECTION: " + direction);
		return direction;
	}
	
	public boolean hasWalkerDriver() {
		return hasWalkerDriver;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	private Vector2D rotateVector2D(Vector2D v, final float angle) {
		final double[][] R = { {Math.cos(angle), -Math.sin(angle)}, {Math.sin(angle), Math.cos(angle)} };
		final double[]   V = { v.getX(), v.getY() };
		final double[]   W = { (V[0]*R[0][0]) + (V[1]*R[0][1]), (V[0]*R[1][0]) + (V[1]*R[1][1]) };
		v.setX((float) W[0]);
		v.setY((float) W[1]);
		return v;
	}
	
	private void accellerateWithControl(final float accelleration) {
		final float velocity = this.control.getLinearVelocity().length();
		if (velocity < MAX_VELOCITY)
			this.control.accelerate(accelleration);
		else
			this.control.accelerate(0.0f);
	}
	
	private float getSteerValue(final Vector3f position, final Vector3f direction, final Vector3f targetPosition) {
		
		final Vector3f targetDirection = Vector3D.calculateDirection(new Vector3D(position), new Vector3D(targetPosition)).toVector3f().normalize();
		//showTargetDirection(new Vector3D(position), new Vector3D(targetDirection));
		
		final Vector2f currentDirection2d = new Vector2f(direction.getX(), direction.getZ());
		final Vector2f targetDirection2d  = new Vector2f(targetDirection.getX(), targetDirection.getZ());
		final float smallestAngleBet = currentDirection2d.smallestAngleBetween(targetDirection2d);
		
		final Vector2f tmpCurrentDirection2d = currentDirection2d.clone();
		final Vector2f tmpTargetDirection2d  = targetDirection2d.clone();
		tmpCurrentDirection2d.setY(-tmpCurrentDirection2d.getY());
		tmpTargetDirection2d.setY(-tmpTargetDirection2d.getY());
		final int rightLeftSide = Vector2D.getSide(new Vector2D(tmpCurrentDirection2d), new Vector2D(tmpTargetDirection2d));
		
		Vector2f v = new Vector2f(-1.0f, -1.0f);
		Vector2f w = new Vector2f(-1.0f, 0.0f);
		float a = v.angleBetween(w);
		
		//if (Math.abs(smallestAngleBet) < 0.001f)
		//	return 0.0f;
		
		// TODO adjust proportions
		float steer = (smallestAngleBet*MAX_STEER_DEGREE)/PI;
		steer = (smallestAngleBet/MAX_STEER_DEGREE);
		steer *= ((float) rightLeftSide);
		steer = -steer;
		
		/*if (angleBet >= 0.0f) {
			steer = -;
		} else {
			steer = -(angleBet/3.14f);
		}*/
		//steer = (float) ( angleBet/Math.PI);
		
		/*if (steer >= 0.0f)
			System.out.println("ANGLE BETWEEN:" + smallestAngleBet + " " + currentDirection2d +  " " + targetDirection2d +  "RIGHT" + "STEER: " + steer);
		else 
			System.out.println("ANGLE BETWEEN:" + smallestAngleBet + " " + currentDirection2d +  " " + targetDirection2d +  "LEFT " + "STEER: " + steer);
		*/
		if (steer > MAX_ABS_STEERING_VALUE) {
			steer = MAX_ABS_STEERING_VALUE;
		} else if (steer < (-MAX_ABS_STEERING_VALUE)) {
			steer = -MAX_ABS_STEERING_VALUE;
		}
		
		getOwnerSpatial().getLocalRotation().toAngles(axesAngles);
		//System.out.println("STEER VALUE: " + steer);
		//System.out.println(this.vehicleForwardDirection);
		return steer;
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
				//showDirection(new Vector3D(position), new Vector3D(direction)); 
				Ray ray = new Ray(position, direction);
				CollisionResults results = new CollisionResults();
				List< ISpatialEntity > entities = this.trafficManager.getDetectableSpatialEntitiesForVehicle( getAbstractVehicleOwner() );
				
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
	
	public AbstractVehicle getAbstractVehicleOwner() {
		return (this.owner);
	}
	
	@Override
	public void onShootReaction( final IBullet bullet) {
		this.lifeBar.reduce( bullet.getVehicleLifeBarReduction() );
		if ( this.lifeBar.isEmpty() ) {
			stopVehicle();
			clearTergetNode();
			this.owner.getAbstractPhysicalExtension().onShootApplyEffects();
			this.control.applyForce( ON_SHOOT_EMPTY_LIFE_FORCE, ON_SHOOT_EMPTY_LIFE_FORCE_LOCATION );
			if ( this.isDriven() && (this.driver!=null) )
				this.driver.getHumanPhysicalActivity().onExplosionReaction( bullet );
				
			if ( trafficManager!=null )
				trafficManager.getApplication().onExplosion( this.owner );
			EffectSoundManager.getInstance().onExplosion( this.control.getPhysicsLocation() );
			
			ILogger logger = LoggingManager.getDefaultLogger();
			ILoggingEvent event = new VehicleLoggingEvent( 
					VehicleLoggingEvent.VEHICLE_EXPLOSION_EVENT
					);
			logger.log( event );
		}
	}
	
	@Override
	public void onExplosionReaction(IBullet bullet) {
		this.lifeBar.reduce( LifeBar.FULL_BAR );
		stopVehicle();
		clearTergetNode();
		this.owner.getAbstractPhysicalExtension().onShootApplyEffects();
		this.control.applyForce( ON_SHOOT_EMPTY_LIFE_FORCE, ON_SHOOT_EMPTY_LIFE_FORCE_LOCATION );	
		if ( this.driver!=null )
			this.driver.getHumanPhysicalActivity().onExplosionReaction( bullet );
	}
	
	public VehicleCookie getCookie() {
		VehicleCookie cookie = new VehicleCookie();
		
		cookie.id = this.id;
		
		cookie.initialVehicleForwardDirectionCookie.set(this.initialVehicleForwardDirection.clone());
		
		cookie.startNodeId = getNodeId(this.startNode);
		cookie.targetNodeId = getNodeId(this.targetNode);
		
		cookie.previousPositionCookie.set(this.previousPosition.clone());
		
		cookie.lifeBarCookie.set(this.lifeBar);
		cookie.isDriven = this.isDriven;
		cookie.hasWalkerDriver = this.hasWalkerDriver;
		
		cookie.steeringDriveValue = this.steeringDriveValue;
		cookie.accelerationDriveValue = this.accelerationDriveValue;
		
		cookie.position.set(this.control.getPhysicsLocation().clone());
		cookie.linearVelocity.set(this.control.getLinearVelocity().clone());
		cookie.angularVelocity.set(this.control.getAngularVelocity().clone());
		cookie.quaternionRotationCookie.set(this.control.getPhysicsRotation().clone());
		
		return cookie;
	}
	
	public void setCookie( final VehicleCookie cookie ) {
		this.id = cookie.id;
		
		this.initialVehicleForwardDirection = cookie.initialVehicleForwardDirectionCookie.vector.clone();
		
		this.startNode = getNodeFromId(cookie.startNodeId, roadsNetwork);
		this.targetNode = getNodeFromId(cookie.targetNodeId, roadsNetwork);
		
		this.previousPosition = cookie.previousPositionCookie.vector.clone();
		
		this.lifeBar.setCookie(cookie.lifeBarCookie);
		this.isDriven = cookie.isDriven;
		this.hasWalkerDriver = cookie.hasWalkerDriver;
		if ( this.isDriven || (this.targetNode==null) ) {
			clearTergetNode();
			stopVehicle();
		}
		
		this.steeringDriveValue = cookie.steeringDriveValue;
		this.control.steer(this.steeringDriveValue);
		this.accelerationDriveValue = cookie.accelerationDriveValue;
		
		this.control.setPhysicsLocation(cookie.position.vector.clone());
		this.control.setLinearVelocity(cookie.linearVelocity.vector.clone());
		this.control.setAngularVelocity(cookie.angularVelocity.vector.clone());
		this.control.setPhysicsRotation(cookie.quaternionRotationCookie.get().clone());
		this.control.resetSuspension();
	}
	
	private Node getOwnerSpatialNode() {
		return ( (Node) ((ModelBasedPhysicalExtension) this.owner.getAbstractPhysicalExtension()).getModelSpatial() );
	}
	
	private Spatial getOwnerSpatial() {
		return ((ModelBasedPhysicalExtension) this.owner.getAbstractPhysicalExtension()).getModelSpatial();
	}
	
	private void keepInsideWorldPlatform() {
		if ( ( this.trafficManager!=null ) && 
				( this.trafficManager.getApplication().getGraphicalTerrain().checkOutOfBound( this.control.getPhysicsLocation() ) ) &&
				( this.previousPosition!=null ) )
			this.control.setPhysicsLocation( this.previousPosition );
		else 
			this.previousPosition = this.control.getPhysicsLocation().clone();
	}
	
	public void stopVehicle() {
		this.control.accelerate(0.0f);
		this.control.brake(10.0f);
	}

	private Spatial directionArrow=null;
	public Node rootNode=null;
	private void showDirection(Vector3D start, Vector3D direction) {
		
		if (directionArrow!=null) {
			rootNode.detachChild(directionArrow);
			//return;
		}
		
		
		start.setY(start.getY() + 10.0f);
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
		
		
		start.setY(start.getY() + 10.0f);
		//System.out.println("START POSITION: " + start);
		Arrow mesh = new Arrow(Vector3D.calculateDirection(start, start.clone().add(direction.scale(10.0f))).toVector3f());
			
		targetDirectionArrow = new Geometry("arrow", mesh);
		Material mat = new Material(LogicSantosApplication.ASSET_MANAGER_APPLICATION, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setLineWidth(3.0f);
		mat.setColor("Color", ColorRGBA.Blue);
		targetDirectionArrow.setMaterial(mat);
		targetDirectionArrow.setLocalTranslation(start.toVector3f());
		targetDirectionArrow.setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
		
		rootNode.attachChild(targetDirectionArrow);
			
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
	
	private static int getNodeId( final RoadNode node ) {
		if ( node!=null )
			return ( node.getId() );
		return -1;
	}
	
	private static RoadNode getNodeFromId( final int id, final RoadsNetwork roadsNetwork ) {
		if ( id>0 )
			return ( roadsNetwork.getNode(id) );
		return null;
	}
}
