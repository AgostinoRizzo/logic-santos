/**
 * 
 */
package it.unical.logic_santos.gui.terrain;

import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class GraphicalTerrainConfig {

	public static final int TERRAIN_TILE_SIZE = 128;
	public static final float SMOOTH_NEIGHBOURS_HEIGHT_INFLUENCE = 0.5f;
	public static final int SMOOTH_RADIUS = 3;
	public static final byte FLATTENING_VALUE = 1;
	
	public static final Vector3D TERRAIN_SCALE = new Vector3D(3.5f, 1.5f, 4.5f); //new Vector3D(3.5f, 1.5f, 3.5f);
	
	
	public static final int ROADS_NETWORK_TERRAIN_TILE_SIZE = 128;
	public static final float ROADS_NETWORK_SMOOTH_NEIGHBOURS_HEIGHT_INFLUENCE = 0.5f;
	public static final int ROADS_NETWORK_SMOOTH_RADIUS = 3;
	public static final byte ROADS_NETWORK_FLATTENING_VALUE = 2;
	
	public static final Vector3D ROADS_NETWORK_TERRAIN_SCALE = new Vector3D(1.0f, 1.0f, 1.0f);
	
	
	public static final Vector3D TERRAIN_TRANSLATION = new Vector3D(-100.0f, 0.0f, -300.0f);
	public static final Vector3D ROADS_NETWORK_TERRAIN_TRANSLATION = new Vector3D(-1000.0f, -36.8f, 1000.0f);
}
