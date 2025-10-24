/**
 * 
 */
package it.unical.logic_santos.mission;

import it.unical.logic_santos.gameplay.PlayerManager;
import it.unical.logic_santos.logging.ILogger;
import it.unical.logic_santos.logging.LoggingManager;

/**
 * @author Agostino
 *
 */
public class MissionManager {

	private IMission currentMission=null;
	private PlayerManager playerManager=null;
	
	public MissionManager( PlayerManager playerManager) {
		this.playerManager = playerManager;
	}
	
	public MissionManager( IMission currentMission, PlayerManager playerManager ) {
		this.currentMission = currentMission;
		this.playerManager = playerManager;
	}
	
	public void update( final float tpf ) {
		if ( currentMission==null )
			return;
		
		if ( currentMission.check( tpf ) ) {
			playerManager.onMissionComplete( currentMission );
			LoggingManager.getDefaultLogger().removeLoggingObserver( this.currentMission );
			this.currentMission=null;
		}
	}
	
	public IMission getCurrentMission() {
		return currentMission;
	}
	
	public void setCurrentMission(IMission currentMission) {
		if ( this.currentMission!=null )
			LoggingManager.getDefaultLogger().removeLoggingObserver( this.currentMission );
		this.currentMission = currentMission;
		LoggingManager.getDefaultLogger().addLoggingObserver( this.currentMission );
	}
	
	
	
	
}
