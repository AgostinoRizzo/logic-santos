/**
 * 
 */
package it.unical.logic_santos.design.modeling;

import com.jme3.math.Vector3f;

import it.unical.logic_santos.physics.extension.IPhisicalExtensionGenerator;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class ModelingConfig {

	public static final IPhisicalExtensionGenerator DEFAULT_PHYSICAL_EXTENSION_GENERATOR = new ModelBasedPhysicalExtensionGenerator();
	
	
	
	
	public static final float DEFAULT_MODEL_SCALE_FACTOR = 10.0f;
	public static final Vector3D DEFAULT_TRANSLATION_ADJUSTMENT = Vector3D.ZERO.clone();
	
	public static final float SKYAPARTMENT_MODEL_SCALE_FACTOR = 0.0023f;
	public static final Vector3D SKYAPARTMENT_TRANSLATION_ADJUSTMENT = new Vector3D(0.0f, -36.0f, 0.0f);
	
	public static final float VEHICLE_MODEL_SCALE_FACTOR          = 5.0f;
	public static final float HELICOPTER_MODEL_SCALE_FACTOR       = 7.0f;
	public static final float PLAYER_MODEL_SCALE_FACTOR           = 6.0f;
	public static final float PLAYER_CHARACTER_MODEL_SCALE_FACTOR = 3.0f;
	public static final float POLICEMAN_MODEL_SCALE_FACTOR        = 6.0f;
	public static final float WATCH_TOWER_MODEL_SCALE_FACTOR      = 1.0f;
	public static final float STOP_SIGN_MODEL_SCALE_FACTOR        = 3.0f;
	public static final float MILLENIUM_TOWER_MODEL_SCALE_FACTOR  = 30.0f;
	public static final float PALACE_TOWER_MODEL_SCALE_FACTOR     = 30.0f;
	public static final float PARK_TOWER_MODEL_SCALE_FACTOR       = 30.0f-10.0f;
	public static final float SKYSCRAPER_MODEL_SCALE_FACTOR       = 10.0f;
	public static final float COFFEE_SHOP_MODEL_SCALE_FACTOR      = 0.1f;
	public static final float TENNIS_FIELD_MODEL_SCALE_FACTOR     = 0.1f;
	public static final float TRAFFIC_LIGHT_MODEL_SCALE_FACTOR    = 2.0f;
	public static final float STREET_LIGHT_MODEL_SCALE_FACTOR     = 3.0f;
	public static final float PARK_BENCH_MODEL_SCALE_FACTOR       = 8.0f;
	
	public static final Vector3D PLAYER_CHARACTER_TRANSLATION_ADJUSTMENT = new Vector3D( 0.0f, 0.0f, 0.0f );
	public static final Vector3D STOP_SIGN_TRANSLATION_ADJUSTMENT        = new Vector3D( 0.0f, -3.8f, 0.0f );
	public static final Vector3D MILLENIUM_TOWER_TRANSLATION_ADJUSTMENT  = new Vector3D( 0.0f, 16.0f, 0.0f );
	public static final Vector3D PARK_TOWER_TRANSLATION_ADJUSTMENT       = new Vector3D( 0.0f, 0.0f, 0.0f );
	
	public static final float HUMAN_CAPSULE_COLLISION_SHAPE_RADIUS = 3.0f;
	public static final float HUMAN_CAPSULE_COLLISION_SHAPE_HEIGHT = 5.3f; //4.0f;
	
	public static final float AIRCRAFT_CAPSULE_COLLISION_SHAPE_RADIUS = 12.0f;
	public static final float AIRCRAFT_CAPSULE_COLLISION_SHAPE_HEIGHT = 8.0f; //4.0f;
	
	public static final Vector3f HALF_EXTENTS_VEHICLE_BOX_COLLISION_SHAPE = new Vector3f( 8.0f, 1.0f, 8.0f );
	
	public static final String BULLET_MATERIAL_PATH = "Common/MatDefs/Misc/Unshaded.j3md";
	
	public static final String CLASSIC_TREE_MODEL_NAME    = "Models/Tree/Tree.mesh.j3o";
	public static final String PALM_TREE_MODEL_NAME       = "Models/PalmTree/Coconut Tree.j3o";
	public static final String PLAYER_MODEL_NAME          = "Models/PlayerCharacter/PlayerCharacter.scene";
	public static final String CITY_KERNEL_MODEL_NAME     = "CityKernel.obj";
	public static final String SKYSCRAPER_MODEL_NAME      = "Models/Skyscraper/building_04.j3o";
	public static final String SKY_APARTMENT_MODEL_NAME   = "Models/SkyApartment/3d-model.j3o";
	public static final String CAR_MODEL_NAME             = "Models/Ferrari/Car.scene";
	public static final String HELICOPTER_MODEL_NAME      = "Models/Helicopter/Helicopter.scene";
	public static final String POLICEMAN_MODEL_NAME       = "Models/PolicemanCharacter/PolicemanCharacter.scene";
	public static final String WATCH_TOWER_MODEL_NAME     = "Models/WatchTower/WatchTower.scene";
	public static final String STOP_SIGN_MODEL_NAME       = "Models/StopSign/StopSign.scene";
	public static final String MILLENIUM_TOWER_MODEL_NAME = "Models/MilleniumTower/MilleniumTower.scene";
	public static final String PALACE_TOWER_MODEL_NAME    = "Models/PalaceTower/PalaceTower.scene";
	public static final String PARK_TOWER_MODEL_NAME      = "Models/ParkTower/ParkTower.scene";
	public static final String COFFEE_SHOP_MODEL_NAME     = "Models/CoffeeShop/CoffeeShop.scene";
	public static final String TENNIS_FIELD_MODEL_NAME    = "Models/TennisField/TennisField.scene";
	public static final String TRAFFIC_LIGHT_MODEL_NAME   = "Models/TrafficLight/TrafficLight.scene";
	public static final String STREET_LIGHT_MODEL_NAME    = "Models/StreetLight/StreetLight.scene";
	public static final String PARK_BENCH_MODEL_NAME      = "Models/ParkBench/ParkBench.scene";
}
