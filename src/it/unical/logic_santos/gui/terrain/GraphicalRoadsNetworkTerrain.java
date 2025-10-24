/**
 * 
 */
package it.unical.logic_santos.gui.terrain;

import java.io.IOException;

import com.jme3.material.Material;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.terrain.modeling.AbstractHeightMapModel;
import it.unical.logic_santos.terrain.modeling.ImageBasedHeightMapModel;

/**
 * @author Agostino
 *
 */
public class GraphicalRoadsNetworkTerrain extends GraphicalTerrain {
	
	public GraphicalRoadsNetworkTerrain(LogicSantosApplication _logicSantosApp) {
		super(_logicSantosApp);
	}
	
	public GraphicalRoadsNetworkTerrain(LogicSantosApplication _logicSantosApp, boolean _flatTerrain) {
		super(_logicSantosApp, _flatTerrain);
	}
	
	public float getRoadsHeight() {
		return 50.0f;
	}
	
	@Override
	protected void loadTerrainMaterial() { 
	
		terrainMaterial = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
		terrainMaterial.setTexture("Alpha", assetManager.loadTexture("Textures/RoadsAlphaMap.png"));
				
		Texture road = assetManager.loadTexture("Textures/RoadsTexture.jpg");
		road.setWrap(WrapMode.Repeat);
		
		Texture sidewalk = assetManager.loadTexture("Textures/SidewalkTexture.jpg");
		sidewalk.setWrap(WrapMode.Repeat);
		
		Texture strips = assetManager.loadTexture("Textures/StripsTexture.jpg");
		strips.setWrap(WrapMode.Repeat);
		
		terrainMaterial.setTexture("Tex1", strips);
		terrainMaterial.setFloat("Tex1Scale", 64f);
		
		terrainMaterial.setTexture("Tex2", sidewalk);
		terrainMaterial.setFloat("Tex2Scale", 64f);
		
		terrainMaterial.setTexture("Tex3", road);
		terrainMaterial.setFloat("Tex3Scale", 64f);
		
	}


	@Override
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
		terrainQuad.setLocalTranslation(GraphicalTerrainConfig.ROADS_NETWORK_TERRAIN_TRANSLATION.toVector3f());
		terrainQuad.setLocalScale(GraphicalTerrainConfig.ROADS_NETWORK_TERRAIN_SCALE.getX(), 
									GraphicalTerrainConfig.ROADS_NETWORK_TERRAIN_SCALE.getY(), 
									GraphicalTerrainConfig.ROADS_NETWORK_TERRAIN_SCALE.getZ());
		terrainChunkModel.setScale(GraphicalTerrainConfig.ROADS_NETWORK_TERRAIN_SCALE);
		terrainChunkModel.setTranslation(GraphicalTerrainConfig.ROADS_NETWORK_TERRAIN_TRANSLATION);
		//((AbstractHeightMapModel) terrainChunkModel).erodeTerrain(5.0f);
		//terrainQuad.setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
		 
	}

	

}
