/**
 * 
 */
package it.unical.logic_santos.gui.terrain;

import java.io.IOException;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

import it.unical.logic_santos.gui.application.IGraphicalEntity;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.terrain.modeling.AbstractHeightMapModel;
import it.unical.logic_santos.terrain.modeling.ITerrainChunkModel;
import it.unical.logic_santos.terrain.modeling.ImageBasedHeightMapModel;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class GraphicalTerrain implements IGraphicalEntity {
	
	/* ... PRIVATE AND PROTECTED FIELDS ... */
	
	protected LogicSantosApplication logicSantosApp=null;
	protected AssetManager assetManager=null;
	protected Node rootNode=null;
	
	/* terrain fields */
	protected Material terrainMaterial=null;
	protected TerrainQuad terrainQuad=null;
	protected ITerrainChunkModel terrainChunkModel=null;
	
	protected boolean flatTerrain=false;
	
	protected static final float MARGIN_PLATFORM_BOUND = 30.0f;
	protected static final float DOUBLE_MARGIN_PLATFORM_BOUND = MARGIN_PLATFORM_BOUND*2.0f;


	/* ... CONSTRUCTORS ... */
	
	public GraphicalTerrain(LogicSantosApplication _logicSantosApp, AbstractHeightMapModel _terrainChunkModel, boolean _flatTerrain) {
		this.logicSantosApp = _logicSantosApp;
		this.assetManager = this.logicSantosApp.getAssetManager();
		this.rootNode = this.logicSantosApp.getRootNode();
		this.terrainChunkModel = _terrainChunkModel;
		this.flatTerrain = _flatTerrain;
	}
	
	public GraphicalTerrain(LogicSantosApplication _logicSantosApp, boolean _flatTerrain) {
		this.logicSantosApp = _logicSantosApp;
		this.assetManager = this.logicSantosApp.getAssetManager();
		this.rootNode = this.logicSantosApp.getRootNode();
		this.terrainChunkModel = new ImageBasedHeightMapModel();
		this.terrainChunkModel.generateFlatTerrain();
		this.flatTerrain = _flatTerrain;
	}
	
	public GraphicalTerrain(LogicSantosApplication _logicSantosApp) {
		this.logicSantosApp = _logicSantosApp;
		this.assetManager = this.logicSantosApp.getAssetManager();
		this.rootNode = this.logicSantosApp.getRootNode();
		this.terrainChunkModel = new ImageBasedHeightMapModel();
		this.terrainChunkModel.generateFlatTerrain();
		this.flatTerrain = true;
	}
	
	
	/* ... GENERAL METHODS ... */
	
	@Override
	public void loadComponents() throws IOException {
		loadTerrainMaterial();
		loadTerrainModel();
	}
	
	protected void loadTerrainMaterial() {
		
		terrainMaterial = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
		terrainMaterial.setTexture("AlphaMap", assetManager.loadTexture(GraphicalTerrainResources.TERRAIN_ALPHAMAP_FILE_NAME));
				
		Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
		grass.setWrap(WrapMode.Repeat);
		
		Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
		dirt.setWrap(WrapMode.Repeat);
		
		Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
		rock.setWrap(WrapMode.Repeat);
		
		terrainMaterial.setTexture("DiffuseMap", grass);
		terrainMaterial.setFloat("DiffuseMap_0_scale", 64f);
		
		terrainMaterial.setTexture("DiffuseMap_1", dirt);
		terrainMaterial.setFloat("DiffuseMap_1_scale", 32f);
		
		terrainMaterial.setTexture("DiffuseMap_2", rock);
		terrainMaterial.setFloat("DiffuseMap_2_scale", 128f);
		
		Texture grassNormal = assetManager.loadTexture("Textures/Terrain/splat/grass_normal.jpg");
		grassNormal.setWrap(WrapMode.Repeat);
		
		Texture dirtNormal = assetManager.loadTexture("Textures/Terrain/splat/dirt_normal.png");
		dirtNormal.setWrap(WrapMode.Repeat);
		
		Texture rockNormal = assetManager.loadTexture("Textures/Terrain/splat/road_normal.png");
		rockNormal.setWrap(WrapMode.Repeat);
		
		terrainMaterial.setTexture("NormalMap", grassNormal);
		terrainMaterial.setTexture("NormalMap_1", dirtNormal);
		terrainMaterial.setTexture("NormalMap_2", rockNormal);
	}
	
	@Override
	public void attachComponentsToGraphicalEngine() {
		if (terrainChunkModel instanceof ImageBasedHeightMapModel) {
			rootNode.attachChild(terrainQuad);
		}
	}
	
	@Override
	public void detachComponentsToGraphicalEngine() {
		if (terrainChunkModel instanceof ImageBasedHeightMapModel) {
			rootNode.detachChild(terrainQuad);
		}
	}

	@Override
	public void setTranslation(Vector3f translation) {
		// TODO Auto-generated method stub
		
	}
	
	public ITerrainChunkModel getTerrainModel() {
		return terrainChunkModel;
	}
	
	public boolean isPointInside(final float x, final float z) {
		final float newX = (x-terrainQuad.getLocalTranslation().getX()) / terrainQuad.getLocalScale().getX();
		final float newZ = (z-terrainQuad.getLocalTranslation().getZ()) / terrainQuad.getLocalScale().getZ();
		return terrainChunkModel.isInside(newX, newZ);
		
	}
	
	public boolean isPointInside( final Vector3D point ) {
		return isPointInside(point.getX(), point.getZ());
	}
	
	
	public boolean checkOutOfBound( final Vector3f position ) {
		Vector3f tmpPosition = position.clone();
		tmpPosition.subtractLocal( this.terrainQuad.getLocalTranslation() );
		
		final float halfWidth = ( getWidthExtension()-DOUBLE_MARGIN_PLATFORM_BOUND )/2.0f;
		final float halfDepth = ( getDepthExtension()-DOUBLE_MARGIN_PLATFORM_BOUND )/2.0f;
		final float x = tmpPosition.getX();
		final float z = tmpPosition.getZ();
		
		return ( ( x>halfWidth) || ( x<(-halfWidth) ) ||
				 ( z>halfDepth) || ( z<(-halfDepth) ) );
	}
	
	public float getWidthExtension() {
		return ( ( (float) terrainQuad.getTerrainSize() )*terrainQuad.getLocalScale().getX());
	}
	
	public float getDepthExtension() {
		return ( ( (float) terrainQuad.getTerrainSize() )*terrainQuad.getLocalScale().getZ());
	}
	
	private void loadTerrainModel() throws IOException {	
		if (terrainChunkModel instanceof ImageBasedHeightMapModel) {
			loadImageBasedHeightMapTerrainModel();
		}
	}
	
	protected void loadImageBasedHeightMapTerrainModel() throws IOException {
		
		if (!flatTerrain)
			terrainChunkModel.loadTerrainFromFile(GraphicalTerrainResources.TERRAIN_HEIGHTMAP_FILE_NAME);
		else
			terrainChunkModel.generateFlatTerrain();
	
		((AbstractHeightMapModel)terrainChunkModel).smooth(GraphicalTerrainConfig.SMOOTH_NEIGHBOURS_HEIGHT_INFLUENCE, GraphicalTerrainConfig.SMOOTH_RADIUS);
		//((AbstractHeightMapModel)terrainChunkModel).flatten(GraphicalTerrainConfig.FLATTENING_VALUE);
		terrainQuad = new TerrainQuad("MyTerrain", GraphicalTerrainConfig.TERRAIN_TILE_SIZE, ((AbstractHeightMapModel)terrainChunkModel).getVerticesCount()+1,
										((ImageBasedHeightMapModel)(terrainChunkModel)).getHeightMap());
		terrainQuad.setMaterial(terrainMaterial);
		terrainQuad.setLocalTranslation( GraphicalTerrainConfig.TERRAIN_TRANSLATION.toVector3f() );
		terrainQuad.setLocalScale(GraphicalTerrainConfig.TERRAIN_SCALE.getX(), 
									GraphicalTerrainConfig.TERRAIN_SCALE.getY(), 
									GraphicalTerrainConfig.TERRAIN_SCALE.getZ());
		terrainChunkModel.setTranslation( GraphicalTerrainConfig.TERRAIN_TRANSLATION );
		terrainChunkModel.setScale(GraphicalTerrainConfig.TERRAIN_SCALE);
		
		terrainQuad.setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
	}


	public TerrainQuad getTerrainQuad() {
		return terrainQuad;
	}

	public void setTerrainQuad(TerrainQuad terrainQuad) {
		this.terrainQuad = terrainQuad;
	}

	
	
	

}
