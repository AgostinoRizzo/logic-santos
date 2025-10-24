/**
 * 
 */
package it.unical.logic_santos.gui.environment;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;

import it.unical.logic_santos.gui.application.IGraphicalEntity;
import it.unical.logic_santos.gui.application.LogicSantosApplication;

/**
 * @author Agostino
 *
 */
public class GraphicalSky implements IGraphicalEntity {
	
	
	private LogicSantosApplication logicSantosApp=null;
	private AssetManager assetManager=null;
	private Spatial sky=null;
	
	
	public GraphicalSky(LogicSantosApplication _logicSantosApp) {
		this.logicSantosApp = _logicSantosApp;
		this.assetManager = this.logicSantosApp.getAssetManager();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void loadComponents() {
		sky = SkyFactory.createSky(assetManager, GraphicalEnviromentConfig.SKY_TEXTURE_NAME, false);
		sky.setLocalScale(GraphicalEnviromentConfig.SKY_LOCAL_SCALE_FACTOR);
		
	}
	
	@Override
	public void attachComponentsToGraphicalEngine() {
		if (sky != null)
			logicSantosApp.getRootNode().attachChild(sky);
	}
	
	@Override
	public void detachComponentsToGraphicalEngine() {
		if (sky != null)
			logicSantosApp.getRootNode().detachChild(sky);
	}

	@Override
	public void setTranslation(Vector3f translation) {
		// TODO Auto-generated method stub
		
	}
	
	public Spatial getSkySpatial() {
		return sky;
	}

	

}
