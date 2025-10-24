/**
 * 
 */
package it.unical.logic_santos.gui.application;

import com.jme3.math.Vector3f;

import it.unical.logic_santos.gui.terrain.GraphicalTerrainConfig;

/**
 * @author Agostino
 *
 */
public class MapScreenshotUtil extends LogicSantosApplication {

	private final float X_STEP = 1195.8333333333f;
	private final float Z_STEP = 1153.125f;
	private final float Z_HALF_STEP = Z_STEP/2.0f;
	
	private final float X[] = { -X_STEP, 0.0f, +X_STEP };
	private final float Z[] = { -Z_STEP-Z_HALF_STEP, 
								-Z_HALF_STEP, 
								Z_HALF_STEP, 
								+Z_STEP+Z_HALF_STEP,  };
	private final float Y = 1550.0f;
	private int i=0;
	private int j=0;
	
	public static void main(String[] args) {
		MapScreenshotUtil app = new MapScreenshotUtil();
		app.start();
	}
	
	@Override
	public void simpleInitApp() {
		super.simpleInitApp();
		setDisplayFps( false );
		setDisplayStatView( false );
	}
	
	@Override
	protected void manageClickEvent() {
		setCamNext();
	}
	
	private void setCamNext() {
		j++;
		if ( j>=3 ) {
			j=0;
			i++;
		}
		if ( i>=4 )
			i=0;
		Vector3f position = new Vector3f( X[j], Y, Z[i] );
		position.addLocal( GraphicalTerrainConfig.TERRAIN_TRANSLATION.toVector3f() );
		
		Vector3f lookPoint = position.clone();
		lookPoint.setY( 0.0f );
		
		cam.setLocation( position );
		cam.lookAt( lookPoint, Vector3f.UNIT_Y );
	}
}
