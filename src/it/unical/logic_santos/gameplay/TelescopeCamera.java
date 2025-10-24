/**
 * 
 */
package it.unical.logic_santos.gameplay;

import com.jme3.input.FlyByCamera;
import com.jme3.renderer.Camera;

/**
 * @author Agostino
 *
 */
public class TelescopeCamera extends FlyByCamera {

	private boolean isAiming=false;
	private boolean isZooming=false;
	
	private static final float AIM_VALUE  = 130.0f;
	private static final float ZOOM_VALUE = 350.0f;
	
	
	public TelescopeCamera(Camera cam) {
		super(cam);
	}
	
	public void aimIn() {
		zoomCamera( -AIM_VALUE );
		isAiming=true;
	}
	
	public void aimOut() {
		zoomCamera( AIM_VALUE );
		isAiming=false;
	}
	
	public void zoomIn() {
		zoomCamera( -ZOOM_VALUE );
		isZooming=true;
	}
	
	public void zoomOut() {
		zoomCamera( ZOOM_VALUE );
		isZooming=false;
	}
	
	public boolean isAiming() {
		return isAiming;
	}
	
	public boolean isZooming() {
		return isZooming;
	}

}
