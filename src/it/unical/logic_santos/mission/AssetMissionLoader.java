/**
 * 
 */
package it.unical.logic_santos.mission;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Agostino
 * The MissionLoader class is a singleton class that represents a Mission Loader 
 * of .class file Mission from Asset folders using Java Reflection
 */
public class AssetMissionLoader implements IMissionLoader {

	private static AssetMissionLoader instance = null;
	
	private List< IMission > availableMissionPrototypes=null;
	
	public static AssetMissionLoader getInstance() {
		if ( instance==null )
			instance = new AssetMissionLoader();
		return instance;
	}
	
	private AssetMissionLoader() {
		this.availableMissionPrototypes = new ArrayList< IMission >();
	}
	
	@Override
	public void loadMissions( final IMissionSource source ) {
		if ( !( source instanceof AssetMissionSource ) )
			return;
		
		AssetMissionSource assetMissionSource = (AssetMissionSource) source;
		
		/* define the class names of the Mission class to be loaded */
		List< Class< IMission > > availableMissionClasses = assetMissionSource.getAvailableMissionClasses();
		availableMissionPrototypes.clear();
		
		for( Class< IMission > missionClass: availableMissionClasses ) {
			
			try {
				
				/* create a new instance of that class ( prototype ) */
				IMission missionPrototype = missionClass.newInstance();
				availableMissionPrototypes.add( missionPrototype );
				
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	@Override
	public List< IMission > getAvailableMissionPrototypes() {
		return availableMissionPrototypes;
	}
}
