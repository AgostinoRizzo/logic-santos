/**
 * 
 */
package it.unical.logic_santos.gui.environment;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 * @author Agostino
 *
 */
public class GraphicalEnviromentConfig {

	/** Sky fields configuration */
	public static final String SKY_TEXTURE_NAME = "Scenes/Beach/FullskiesSunset0068.dds";
	public static final float SKY_LOCAL_SCALE_FACTOR = 350.0f;
	
	/** Light Environment configuration */
	public static final Vector3f SUN_LIGHT_DIRECTION = new Vector3f(-0.8236743f, -1.27054665f, 0.896916f);
	public static final ColorRGBA SUN_COLOR = ColorRGBA.White.clone().multLocal(1f);
	
	/** Ambient Light Environment configuration */
	public static final ColorRGBA AMBIENT_LIGHT_COLOR = new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f);
	
	/** Bloom Filter Environment configuration */
	public static final float BLOOM_EXPOSURE_POWER = 55.0f;
	public static final float BLOOM_INTENSITY = 1.0f;
	
	/** Light Scattering Filter Environment configuration */
	public static final Vector3f LIGHT_POSITION = SUN_LIGHT_DIRECTION.mult(-300);
	public static final float LIGHT_DENSITY = 0.8f;
	
	/** Shadow Renderer Environment configuration */
	public static final int SHADOW_MAP_SIZE = 1024;
	public static final float SHADOW_LAMBDA = 0.55f;
	public static final float SHADOW_INTENSITY = 0.8f;
	
	/** Depth of Field Filter Environment configuration */
	public static final float DEPTH_FIELD_FOCUS_DISTANCE = 0.0f;
	public static final float DEPTH_FIELD_FOCUS_RANGE = 300.0f;
	public static final float DEPTH_FIELD_BLUR_SCALE = 0.8f;
	
	/** Fog Filter Environment rendering */
	public static final ColorRGBA FOG_COLOR = new ColorRGBA(0.9f, 0.8f, 0.8f, 1.0f); //new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f);
	public static final float FOG_DISTANCE = 300.0f;
	public static final float FOG_DENSITY = 0.8f;
	
	/** Water Environment rendering */
	public static final float INITIAL_WATER_HEIGHT = 10.0f;
	
	/** Smoke, Explosion particles configuration */
	public static final String PARTICLES_MATERIAL_NAME           = "Common/MatDefs/Misc/Particle.j3md";
	public static final String SMOKE_PARTICLES_TEXTURE_NAME      = "Effects/Smoke/Smoke.png";
	public static final String FLAME_TEXTURE_NAME                = "Effects/Explosion/flame.png";
	public static final String FLASH_TEXTURE_NAME                = "Effects/Explosion/flash.png";
	public static final String ROUNDSPARK_TEXTURE_NAME           = "Effects/Explosion/roundspark.png";
	public static final String SPARK_TEXTURE_NAME                = "Effects/Explosion/spark.png";
	public static final String SMOKETRAIL_PARTICLES_TEXTURE_NAME = "Effects/Explosion/smoketrail.png";
	public static final String DEBRIS_TEXTURE_NAME               = "Effects/Explosion/Debris.png";
	public static final String SHOKEWAVE_TEXTURE_NAME            = "Effects/Explosion/shockwave.png";
	
}
