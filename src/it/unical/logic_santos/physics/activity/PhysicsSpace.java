/**
 * 
 */
package it.unical.logic_santos.physics.activity;

import java.util.ArrayList;
import java.util.List;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.BulletAppState.ThreadingType;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import com.jme3.terrain.geomipmap.TerrainLodControl;

import it.unical.logic_santos.collision.ICollisionDetectionEngine;
import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.gui.roads_network.RoadsNetworkPlatformConfig;
import it.unical.logic_santos.gui.terrain.GraphicalRoadsNetworkTerrain;
import it.unical.logic_santos.gui.terrain.GraphicalTerrain;
import it.unical.logic_santos.gui.terrain.GraphicalTerrainConfig;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class PhysicsSpace {

	private static PhysicsSpace instance = null;
	private static LogicSantosApplication application = null;
	private com.jme3.bullet.PhysicsSpace phySpace = null;
	private com.jme3.bullet.PhysicsSpace bulletPhySpace = null;
	private TerrainLodControl mainTerrainLodControl = null;
	private TerrainLodControl roadsTerrainLodControl = null;
	
	public static final boolean USE_ROADS_NETWORK_TERRAIN_COLLISION = false;
	
	
	private PhysicsSpace() {
		BulletAppState appState = new BulletAppState();
		appState.setThreadingType(ThreadingType.PARALLEL);
		application.getStateManager().attach(appState);
		phySpace = appState.getPhysicsSpace();
		
		BulletAppState bulletAppState = new BulletAppState();
		bulletAppState.setThreadingType(ThreadingType.PARALLEL);
		application.getStateManager().attach(bulletAppState);
		bulletPhySpace = bulletAppState.getPhysicsSpace();
		
		initPhysicsSpace();
		
		/** The LOD (level of detail) depends on where the camera is */
		List< Camera > cameras = new ArrayList< Camera >();
		cameras.add(application.getCamera());
		mainTerrainLodControl  = new TerrainLodControl(application.getGraphicalTerrain().getTerrainQuad(), cameras);
		if ( USE_ROADS_NETWORK_TERRAIN_COLLISION ) roadsTerrainLodControl = new TerrainLodControl(application.getGraphicalRoadsNetworkTerrain().getTerrainQuad(), cameras);
		
		application.getGraphicalTerrain().getTerrainQuad().addControl(mainTerrainLodControl);
		if ( USE_ROADS_NETWORK_TERRAIN_COLLISION ) application.getGraphicalRoadsNetworkTerrain().getTerrainQuad().addControl(roadsTerrainLodControl);
		
		/** Add physics */
		application.getGraphicalTerrain().getTerrainQuad().addControl(new RigidBodyControl(0.0f));
		if ( USE_ROADS_NETWORK_TERRAIN_COLLISION ) application.getGraphicalRoadsNetworkTerrain().getTerrainQuad().addControl(new RigidBodyControl(0.0f));
		
		phySpace.add(application.getGraphicalTerrain().getTerrainQuad());
		if ( USE_ROADS_NETWORK_TERRAIN_COLLISION ) phySpace.add(application.getGraphicalRoadsNetworkTerrain().getTerrainQuad());
		
	}
	
	public static PhysicsSpace getInstance() {
		if (instance == null)
			instance = new PhysicsSpace();
		return instance;
	}
	
	public static void setLogicSantosApplication(LogicSantosApplication application) {
		PhysicsSpace.application = application;
	}
	
	public com.jme3.bullet.PhysicsSpace getSpace() {
		return phySpace;
	}
	
	public com.jme3.bullet.PhysicsSpace getBulletSpace() {
		return bulletPhySpace;
	}
	
	public ICollisionDetectionEngine getCollisionEngine() {
		return PhysicsSpace.application.getLogicSantosWorld().getCollisionEngine();
	}
	
	public Node getRootNode() {
		return PhysicsSpace.application.getRootNode();
	}
	
	private void initPhysicsSpace() {
		
	}
	
	private void finalizePhysicsSpace() {
		
	}
	
	
	
}
