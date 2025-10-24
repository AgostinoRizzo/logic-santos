/**
 * 
 */
package it.unical.logic_santos.io.universe;

import java.io.File;

/**
 * @author Agostino
 *
 */
public class IOUniverseConfig {

	public static final String FOLDER_SEPARATOR = File.separator;
	public static final String MASTER_FILE_EXTENSION = "lsc";
	public static final String OBJECTS_FILE_NAME = "city_objects";
	public static final String OBJECTS_FILE_EXTENSION = "objs";
	public static final String ROADS_NETWORK_FILE_NAME = "roads_network";
	public static final String ROADS_NETWORK_FILE_EXTENSION = "net";
	public static final String PATHS_NETWORK_FILE_NAME = "paths_network";
	public static final String PATHS_NETWORK_FILE_EXTENSION = "net";
	
	public static final String ROOT_PROJECT_DIRECTORY = System.getProperty("user.dir");
	
	public static final String PLAYER_CAREER_STATUS_FILE_PATH = ROOT_PROJECT_DIRECTORY + "/Assets/Player/career_status.txt";
	
	public static final int CLASS_NAME_MAX_LENGTH = 50; /* expressed in chars (number of chars) */
	

	public static final String ROOT_ASSET_CITY_WORLD_DIRECTORY = ROOT_PROJECT_DIRECTORY + FOLDER_SEPARATOR + "Assets" + FOLDER_SEPARATOR + "CityWorld";
	public static final String CITY_WORLD_PATH = ROOT_PROJECT_DIRECTORY + "/Assets/CityWorld/Logic Santos.lsc";

}
