/**
 * 
 */
package it.unical.logic_santos.mission;

import java.util.List;

/**
 * @author Agostino
 * The IMissionLoader interface represents a Mission Loader 
 * of Mission classes from a generic source ( Asset, Network, etc )
 */
public interface IMissionLoader {

	/** the method loads the available Missions from source.
	 * 
	 * @param source of the Missions
	 */
	public void loadMissions( final IMissionSource source );
	
	/** the method returns the available Missions classes already loaded.
	 * 
	 * @return the available Missions class already loaded.
	 */
	public List< IMission > getAvailableMissionPrototypes();
	
	
	/** the static method returns the Asset Mission Loader ( from Asset Folder Source ).
	 * 
	 * @return the Asset Mission Loader ( from Asset Folder Source ).
	 */
	public static AssetMissionLoader getAssetMissionLoader() {
		return AssetMissionLoader.getInstance();
	}

}
