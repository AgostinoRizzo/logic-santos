/**
 * 
 */
package it.unical.logic_santos.gui.environment;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;

import it.unical.logic_santos.gui.application.IGraphicalEntity;
import it.unical.logic_santos.gui.application.LogicSantosApplication;

/**
 * @author Agostino
 *
 */
public class GraphicalLightEnvironment implements IGraphicalEntity {
	
	private LogicSantosApplication logicSantosApp=null;
	private AssetManager assetManager=null;
	private ViewPort viewPort=null;
	
	private DirectionalLight sun=null;
	private AmbientLight ambientLight=null;
	private BloomFilter bloomFilter=null;
	private LightScatteringFilter lsf=null;
	private FilterPostProcessor fpp=null;
	private DirectionalLightShadowRenderer dlShadowRenderer=null;
	private DirectionalLightShadowFilter dlShadowFilter=null;
	private DepthOfFieldFilter depthFieldFilter=null;
	private FogFilter fogFilter=null;
	
	public GraphicalLightEnvironment(LogicSantosApplication _logicSantosApp) {
		this.logicSantosApp = _logicSantosApp;
		this.assetManager = this.logicSantosApp.getAssetManager();
		this.viewPort = this.logicSantosApp.getViewPort();
	}
	
	@Override
	public void loadComponents() {
		sun = new DirectionalLight();
		sun.setDirection(GraphicalEnviromentConfig.SUN_LIGHT_DIRECTION);
		sun.setColor(GraphicalEnviromentConfig.SUN_COLOR);
		
		ambientLight = new AmbientLight();
		ambientLight.setColor(GraphicalEnviromentConfig.AMBIENT_LIGHT_COLOR);
		
		/*!!!bloomFilter = new BloomFilter();
		bloomFilter.setExposurePower(GraphicalEnviromentConfig.BLOOM_EXPOSURE_POWER);
		bloomFilter.setBloomIntensity(GraphicalEnviromentConfig.BLOOM_INTENSITY);
		
	*/lsf = new LightScatteringFilter(GraphicalEnviromentConfig.LIGHT_POSITION);
		lsf.setLightDensity(GraphicalEnviromentConfig.LIGHT_DENSITY);
		
		fpp = new FilterPostProcessor(assetManager); 
		//fpp.addFilter(bloomFilter);
		fpp.addFilter(lsf);
		//fpp.addFilter(new FXAAFilter());
		
		loadShadowComponents();
		//loadDepthOfFieldFilterComponents();
		loadFogFilterComponents();
	}
	
	@Override
	public void attachComponentsToGraphicalEngine() {
		if (sun != null)
			logicSantosApp.getRootNode().addLight(sun);
		if (ambientLight != null)
			;//logicSantosApp.getRootNode().addLight(ambientLight);
		if (fpp != null)
			viewPort.addProcessor(fpp);
		if (dlShadowRenderer != null) 
			viewPort.addProcessor(dlShadowRenderer);
	}
	
	@Override
	public void detachComponentsToGraphicalEngine() {
		if (sun != null)
			logicSantosApp.getRootNode().removeLight(sun);
		if (ambientLight != null)
			logicSantosApp.getRootNode().removeLight(ambientLight);
		if (fpp != null)
			viewPort.removeProcessor(fpp);
		if (dlShadowRenderer != null) 
			viewPort.removeProcessor(dlShadowRenderer);
	}

	@Override
	public void setTranslation(Vector3f translation) {
		// TODO Auto-generated method stub
		
	}
	
	public void attachAdditionalLightEffects() {
		fpp.addFilter( lsf );
	}
	
	public void detachAdditionalLightEffects() {
		fpp.removeFilter( lsf );
	}
	
	public FilterPostProcessor getFilter() {
		return fpp;
	}
	
	private void loadShadowComponents() {
		
		/*dlShadowRenderer = new DirectionalLightShadowRenderer(assetManager, GraphicalEnviromentConfig.SHADOW_MAP_SIZE, 3);
		dlShadowRenderer.setLight(sun);
		//dlShadowRenderer.setLambda(GraphicalEnviromentConfig.SHADOW_LAMBDA);
		//dlShadowRenderer.setShadowIntensity(GraphicalEnviromentConfig.SHADOW_INTENSITY);
		//dlShadowRenderer.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
		dlShadowRenderer.displayDebug();
		*/
		
		dlShadowFilter = new DirectionalLightShadowFilter(assetManager, GraphicalEnviromentConfig.SHADOW_MAP_SIZE, 3);
		dlShadowFilter.setLight(sun);
		//dlShadowFilter.setLambda(GraphicalEnviromentConfig.SHADOW_LAMBDA);
		//dlShadowFilter.setShadowIntensity(GraphicalEnviromentConfig.SHADOW_INTENSITY);
		//dlShadowFilter.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
		dlShadowFilter.setShadowZExtend( 1000.0f );
		dlShadowFilter.setEnabled(true);
		
		fpp.addFilter(dlShadowFilter);
	}
	
	/*private void loadDepthOfFieldFilterComponents() {
		
		depthFieldFilter = new DepthOfFieldFilter();
		depthFieldFilter.setFocusDistance(GraphicalEnviromentConfig.DEPTH_FIELD_FOCUS_DISTANCE);
		depthFieldFilter.setFocusRange(GraphicalEnviromentConfig.DEPTH_FIELD_FOCUS_RANGE);
		depthFieldFilter.setBlurScale(GraphicalEnviromentConfig.DEPTH_FIELD_BLUR_SCALE);
		
		fpp.addFilter(depthFieldFilter);
	}*/
	
	private void loadFogFilterComponents() {
		
		fogFilter = new FogFilter();
		fogFilter.setFogColor(GraphicalEnviromentConfig.FOG_COLOR);
		fogFilter.setFogDistance(GraphicalEnviromentConfig.FOG_DISTANCE);
		fogFilter.setFogDensity(GraphicalEnviromentConfig.FOG_DENSITY);
		
		fpp.addFilter(fogFilter);
	}

	

}
