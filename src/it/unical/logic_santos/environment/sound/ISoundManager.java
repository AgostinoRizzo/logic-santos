/**
 * 
 */
package it.unical.logic_santos.environment.sound;


import it.unical.logic_santos.gui.application.LogicSantosApplication;

/**
 * @author Agostino
 *
 */
public interface ISoundManager {

	public void init( LogicSantosApplication application );
	public void update( final float tpf, LogicSantosApplication application );
	
	public void setVolume( final float volume );
	
	public static void initSoundManagers( LogicSantosApplication application ) {
		AmbientSoundManager.getInstance().init( application );
		EffectSoundManager.getInstance().init( application );
		VehicleSoundManager.getInstance().init( application );
		HumanSoundManager.getInstance().init( application );
	}
	
	public static void setVolumeSoundManagers( final float volume ) {
		if ( (volume>=0.0f) && (volume<=1.0f) ) {
			AmbientSoundManager.getInstance().setVolume(volume);
			EffectSoundManager.getInstance().setVolume(volume);
			VehicleSoundManager.getInstance().setVolume(volume);
			HumanSoundManager.getInstance().setVolume(volume);
		}
	}

}
