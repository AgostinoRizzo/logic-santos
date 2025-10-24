/**
 * 
 */
package it.unical.logic_santos.gui.roads_network;

import java.io.IOException;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class RoadsNetworkPlatform {
	
	private float xWidthPlatform;
	private float zDepthPlatform;
	private Vector3D centerPosition=null;
	private Geometry platform=null;
	
	private AssetManager assetManager=null;
	private Node rootNode=null;
	
	public RoadsNetworkPlatform(LogicSantosApplication _logicSantosApp, final Vector3D _centerPosition) {
		this.xWidthPlatform = RoadsNetworkPlatformConfig.WIDTH_PLATFORM;
		this.zDepthPlatform = RoadsNetworkPlatformConfig.DEPTH_PLATFORM;
		this.centerPosition = _centerPosition;getClass();
		this.assetManager = _logicSantosApp.getAssetManager();
		this.rootNode = _logicSantosApp.getRootNode();
	}
	
	public RoadsNetworkPlatform(LogicSantosApplication _logicSantosApp) {
		this.xWidthPlatform = RoadsNetworkPlatformConfig.WIDTH_PLATFORM;
		this.zDepthPlatform = RoadsNetworkPlatformConfig.DEPTH_PLATFORM;
		this.centerPosition = Vector3D.ZERO.clone();
		this.assetManager = _logicSantosApp.getAssetManager();
		this.rootNode = _logicSantosApp.getRootNode();
	}
	
	public void loadComponents() throws IOException {
		loadPlatformComponent();
		//loadTerrainModel();
	}
	
	
	private void loadPlatformComponent() {
		
		platform = new Geometry("PlatformGeometry", new Quad(xWidthPlatform, zDepthPlatform));
		platform.rotateUpTo(new Vector3f(0.0f, 0.0f, -1.0f));
				
		Material platformMat = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
		platformMat.setTexture("Alpha", assetManager.loadTexture("Textures/RoadsAlphaMap.png"));
		
		Texture road = assetManager.loadTexture("Textures/RoadsTexture.jpg");
		road.setWrap(WrapMode.Repeat);
		
		Texture sidewalk = assetManager.loadTexture("Textures/SidewalkTexture.jpg");
		sidewalk.setWrap(WrapMode.Repeat);
		
		Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
		grass.setWrap(WrapMode.Repeat);
		
		platformMat.setTexture("Tex1", road);
		platformMat.setFloat("Tex1Scale", 64f);
		
		platformMat.setTexture("Tex2", grass);
		platformMat.setFloat("Tex2Scale", 32f);
		
		platformMat.setTexture("Tex3", sidewalk);
		platformMat.setFloat("Tex3Scale", 128f);
		
		platform.setMaterial(platformMat);
		platform.setLocalTranslation(centerPosition.toVector3f());
		
		platform.setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
		
		/*
		platform = new Geometry("PlatformGeometry", new Quad(xWidthPlatform, zDepthPlatform));
		platform.rotateUpTo(new Vector3f(0.0f, 0.0f, -1.0f));
				
		Material platformMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Texture roadsMap = assetManager.loadTexture("Textures/RoadsBlock.jpg");
		roadsMap.setWrap(WrapMode.Repeat);
		
		platformMat.setTexture("ColorMap", roadsMap);
		
		platform.setMaterial(platformMat);
		platform.setLocalTranslation(centerPosition.toVector3f());
		
		platform.setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
		*/
		
	}

	/*
	protected void loadImageBasedHeightMapTerrainModel() throws IOException {
		
		if (!flatTerrain)
			terrainChunkModel.loadTerrainFromFile(GraphicalTerrainResources.ROADS_NETWORK_TERRAIN_HEIGHTMAP_FILE_NAME);
		else
			terrainChunkModel.generateFlatTerrain();
		
		((AbstractHeightMapModel)terrainChunkModel).smooth(GraphicalTerrainConfig.ROADS_NETWORK_SMOOTH_NEIGHBOURS_HEIGHT_INFLUENCE, GraphicalTerrainConfig.ROADS_NETWORK_SMOOTH_RADIUS);
		((AbstractHeightMapModel)terrainChunkModel).flatten(GraphicalTerrainConfig.ROADS_NETWORK_FLATTENING_VALUE);
		terrainQuad = new TerrainQuad("MyTerrain", GraphicalTerrainConfig.ROADS_NETWORK_TERRAIN_TILE_SIZE, ((AbstractHeightMapModel)terrainChunkModel).getVerticesCount()+1,
										((ImageBasedHeightMapModel)(terrainChunkModel)).getHeightMap());
		terrainQuad.setMaterial(terrainMaterial);
		terrainQuad.setLocalTranslation(0.0f, 0.0f, 0.0f);
		terrainQuad.setLocalScale(GraphicalTerrainConfig.ROADS_NETWORK_TERRAIN_SCALE.getX(), 
									GraphicalTerrainConfig.ROADS_NETWORK_TERRAIN_SCALE.getY(), 
									GraphicalTerrainConfig.ROADS_NETWORK_TERRAIN_SCALE.getZ());
		terrainChunkModel.setScale(GraphicalTerrainConfig.ROADS_NETWORK_TERRAIN_SCALE);
		
		terrainQuad.setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
	}
	*/
	
	public void attachComponentsToGraphicalEngine() {
		rootNode.attachChild(platform);
	}
}
