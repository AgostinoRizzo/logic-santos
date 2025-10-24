/**
 * 
 */
package it.unical.logic_santos.gui.environment;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import com.jme3.water.WaterFilter;

import it.unical.logic_santos.gui.application.IGraphicalEntity;
import it.unical.logic_santos.gui.application.LogicSantosApplication;

/**
 * @author Agostino
 *
 */
public class GraphicalWater implements IGraphicalEntity {

	AssetManager assetManager=null;
	Node rootNode=null;
	ViewPort viewPort=null;
	GraphicalLightEnvironment lightEnvironment;
	
	WaterFilter waterFilter=null;
	FilterPostProcessor fpp=null;
	
	
	public GraphicalWater(LogicSantosApplication _application, GraphicalLightEnvironment _lightEnvironment) {
		this.assetManager = _application.getAssetManager();
		this.rootNode = _application.getRootNode();
		this.viewPort = _application.getViewPort();
		this.lightEnvironment = _lightEnvironment;
	}
	
	@Override
	public void loadComponents() {
		waterFilter = new WaterFilter(rootNode, GraphicalEnviromentConfig.SUN_LIGHT_DIRECTION);
		waterFilter.setWaterColor(new ColorRGBA().setAsSrgb(0.0078f, 0.3176f, 0.5f, 1.0f));
		waterFilter.setDeepWaterColor(new ColorRGBA().setAsSrgb(0.0039f, 0.00196f, 0.145f, 1.0f));
		waterFilter.setUnderWaterFogDistance(80);
		waterFilter.setWaterTransparency(0.12f);
		waterFilter.setFoamIntensity(0.4f);
		waterFilter.setFoamHardness(0.3f);
		waterFilter.setFoamExistence(new Vector3f(0.8f, 8f, 1f));
		waterFilter.setReflectionDisplace(50);
		waterFilter.setRefractionConstant(0.25f);
		waterFilter.setColorExtinction(new Vector3f(30, 50, 70));
		waterFilter.setCausticsIntensity(0.4f);
		waterFilter.setWaveScale(0.003f);
		waterFilter.setMaxAmplitude(2f);
		waterFilter.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
		waterFilter.setRefractionStrength(0.2f);
		waterFilter.setWaterHeight(GraphicalEnviromentConfig.INITIAL_WATER_HEIGHT);

		BloomFilter bloomFilter = new BloomFilter();
		bloomFilter.setExposurePower(55);
		bloomFilter.setBloomIntensity(1.0f);
		
		LightScatteringFilter lsf = new LightScatteringFilter(GraphicalEnviromentConfig.SUN_LIGHT_DIRECTION.mult(-300));
		lsf.setLightDensity(0.5f);
		
		DepthOfFieldFilter dof = new DepthOfFieldFilter();
		dof.setFocusDistance(0);
		dof.setFocusRange(100);
		
		fpp = lightEnvironment.getFilter();
		fpp.addFilter(waterFilter);
		//!!!fpp.addFilter(bloomFilter);
		//!!!fpp.addFilter(dof);
		//!!!fpp.addFilter(lsf);
		fpp.addFilter(new FXAAFilter());
	}

	@Override
	public void attachComponentsToGraphicalEngine() {
		//viewPort.addProcessor(fpp);
	}

	@Override
	public void detachComponentsToGraphicalEngine() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTranslation(Vector3f translation) {
		// TODO Auto-generated method stub
		
	}
	
	public float getWaterHeight() {
		return waterFilter.getWaterHeight();
	}

}
