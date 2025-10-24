/**
 * 
 */
package it.unical.logic_santos.gui.application;

import com.jme3.math.Vector3f;

import it.unical.logic_santos.gui.environment.StaticSpatialEntityViewer;

/**
 * @author Agostino
 *
 */
public class LogicSantosEditableApplication extends LogicSantosApplication {

	protected StaticSpatialEntityViewer staticPathEntityViewer=null;
	
	public LogicSantosEditableApplication() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void simpleInitApp() {
		staticPathEntityViewer = new StaticSpatialEntityViewer( this );
		super.simpleInitApp();
		flyCam.setDragToRotate(true);
	}

	@Override
	public void simpleUpdate(float tpf) {
		//super.simpleUpdate(tpf);
		
		Vector3f currCamPosition = cam.getLocation();
		final float terrainHeight = logicSantosWorld.getTerrainModel().getHeight(currCamPosition.getX(), currCamPosition.getZ());
		if ((currCamPosition.getY()-camTerrainDistance) < terrainHeight) {
			//bapka.
			if ( precCamPosition!=null )
				cam.setLocation(precCamPosition.clone());
		} else
			precCamPosition = currCamPosition.clone();
		

		staticPathEntityViewer.update(cam.getLocation(), tpf);
		if ( super.staticSpatialEntityViewer!=null )
			super.staticSpatialEntityViewer.update( cam.getLocation(), tpf );
		
		//GraphicalLoader.getInstance().attachSpatials();
		
	}
	
	@Override
	public StaticSpatialEntityViewer getStaticPathEntityViewer() {
		return staticPathEntityViewer;
	}

	
}
