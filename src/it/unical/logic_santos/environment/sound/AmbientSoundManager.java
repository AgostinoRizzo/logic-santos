/**
 * 
 */
package it.unical.logic_santos.environment.sound;

import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.AudioSource.Status;
import com.jme3.math.Vector3f;

import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.gui.terrain.GraphicalTerrainConfig;
import it.unical.logic_santos.spatial_entity.AbstractHuman;

/**
 * @author Agostino
 *
 */
public class AmbientSoundManager implements ISoundManager {

	private static AmbientSoundManager instance = null;
	
	private static final float MAX_OFFSET_POSITION_HEIGHT_TO_HEAR_WAVES_SOUND = 18.0f;
	private static final float MAX_DISTANCE_TO_HEAR_CITY_SOUND = 1000.0f;
	private static final float MAX_DISTANCE_TO_HEAR_POLICE_ALARM_SOUND = 300.0f;
	
	private AudioNode wavesAudioNode=null;
	private AudioNode natureAudioNode=null;
	private AudioNode cityAudioNode=null;
	private AudioNode policeAlarmAudioNode=null;
	private AudioNode menuAudioNode=null;
	
	private float masterVolume=1.0f;
	
	
	public static AmbientSoundManager getInstance() {
		if ( instance==null )
			instance = new AmbientSoundManager();
		return instance;
	}
	
	private AmbientSoundManager() {}

	@Override
	public void init(LogicSantosApplication application) {
		AssetManager assetManager = application.getAssetManager();
		AudioRenderer audioRenderer = application.getAudioRenderer();
		
		wavesAudioNode = new AudioNode( assetManager, 
										SoundConfig.WAVES_SOUND_EFFECT_NAME, 
										DataType.Buffer );
		wavesAudioNode.setPositional( false );
		wavesAudioNode.setLooping( true );
		wavesAudioNode.setReverbEnabled( true );
		wavesAudioNode.setVolume( 0.0f );
		wavesAudioNode.pause();
		
		audioRenderer.playSource( wavesAudioNode );
		
		natureAudioNode = new AudioNode( assetManager, 
				SoundConfig.NATURE_SOUND_EFFECT_NAME, 
				DataType.Buffer );
		natureAudioNode.setPositional( false );
		natureAudioNode.setLooping( true );
		natureAudioNode.setReverbEnabled( true );
		natureAudioNode.setVolume( 0.0f );
		natureAudioNode.pause();

		audioRenderer.playSource( natureAudioNode );
		
		cityAudioNode = new AudioNode( assetManager, 
				SoundConfig.CITY_SOUND_EFFECT_NAME, 
				DataType.Buffer );
		cityAudioNode.setPositional( false );
		cityAudioNode.setLooping( true );
		cityAudioNode.setReverbEnabled( true );
		cityAudioNode.setVolume( 0.0f );
		cityAudioNode.pause();

		audioRenderer.playSource( cityAudioNode );
		
		policeAlarmAudioNode = new AudioNode( assetManager, 
				SoundConfig.POLICE_ALARM_SOUND_EFFECT_NAME, 
				DataType.Buffer );
		policeAlarmAudioNode.setPositional( false );
		policeAlarmAudioNode.setLooping( true );
		policeAlarmAudioNode.setReverbEnabled( true );
		policeAlarmAudioNode.setVolume( 0.0f );
		policeAlarmAudioNode.pause();

		audioRenderer.playSource( policeAlarmAudioNode );
		
		menuAudioNode = new AudioNode( assetManager, 
				SoundConfig.MENU_SOUND_EFFECT_NAME, 
				DataType.Stream );
		menuAudioNode.setPositional( false );
		menuAudioNode.setLooping( false );
		menuAudioNode.setReverbEnabled( false );
		menuAudioNode.setVolume( 1.0f );
		menuAudioNode.pause();
	}
	
	@Override
	public void update(float tpf, LogicSantosApplication application) {
		final Vector3f playerPosition = application.getPlayer()
				.getHumanPhysicalActivity().getControl()
				.getPhysicsLocation();
		
		
		updateWavesSound( playerPosition, application );
		if ( menuAudioNode.getStatus()!=Status.Playing )
			updateNatureSound( playerPosition );
		else
			natureAudioNode.stop();
		updateCitySound( playerPosition );
		updatePoliceAlarmSound( playerPosition, application );	
	}
	
	public void playMenuSound() {
		menuAudioNode.play();
	}
	
	public void stopMenuSound() {
		menuAudioNode.stop();
	}
	
	@Override
	public void setVolume(float volume) {
		if ( (volume>=0.0f) && (volume<=1.0f) )
			this.masterVolume = volume;
	}
	
	private void updateWavesSound( final Vector3f playerPosition, LogicSantosApplication application ) {
		final float playerPositionHeight = playerPosition.getY();
		final float waterHeight = application.getGraphicalWater()
												.getWaterHeight();
		
		final float waterLevelDist = playerPositionHeight-waterHeight;
		float wavesSoundVolume = 1.0f;
		
		if ( waterLevelDist>MAX_OFFSET_POSITION_HEIGHT_TO_HEAR_WAVES_SOUND )
			wavesSoundVolume = 0.0f;
		else if ( playerPositionHeight>waterHeight )
			wavesSoundVolume = 
				1.0f-((waterLevelDist)/MAX_OFFSET_POSITION_HEIGHT_TO_HEAR_WAVES_SOUND);
		
		wavesSoundVolume*=masterVolume;
		if ( wavesAudioNode.getVolume()!=wavesSoundVolume )
			wavesAudioNode.setVolume( wavesSoundVolume );
		if ( wavesAudioNode.getVolume()==0.0f )
			wavesAudioNode.pause();
		else
			wavesAudioNode.play();
	}
	
	private void updateNatureSound( final Vector3f playerPosition ) {
		final float distance = playerPosition.distance( 
				GraphicalTerrainConfig.ROADS_NETWORK_TERRAIN_TRANSLATION.toVector3f() );
		if ( distance<=(MAX_DISTANCE_TO_HEAR_CITY_SOUND/2.0f) )
			return;
			
		float natureSoundVolume = 1.0f;
		
		/*if ( waterLevelDist>MAX_OFFSET_POSITION_HEIGHT_TO_HEAR_WAVES_SOUND )
			wavesSoundVolume = 0.0f;
		else if ( playerPositionHeight>waterHeight )
			wavesSoundVolume = 
				1.0f-((waterLevelDist)/MAX_OFFSET_POSITION_HEIGHT_TO_HEAR_WAVES_SOUND);*/
		natureSoundVolume*=masterVolume;
		if ( natureAudioNode.getVolume()!=natureSoundVolume )
			natureAudioNode.setVolume( natureSoundVolume );
		if ( natureAudioNode.getVolume()==0.0f )
			natureAudioNode.stop();
		else
			natureAudioNode.play();
	}
	
	private void updateCitySound( final Vector3f playerPosition ) {
		final float distance = playerPosition.distance( 
			GraphicalTerrainConfig.ROADS_NETWORK_TERRAIN_TRANSLATION.toVector3f() );
		
		float citySoundVolume = 1.0f;
		
		if ( distance>MAX_DISTANCE_TO_HEAR_CITY_SOUND )
			citySoundVolume = 0.0f;
		else
			citySoundVolume = 1.0f-((distance)/MAX_DISTANCE_TO_HEAR_CITY_SOUND);
		
		citySoundVolume*=masterVolume;
		if ( cityAudioNode.getVolume()!=citySoundVolume )
			cityAudioNode.setVolume( citySoundVolume );
		if ( cityAudioNode.getVolume()==0.0f )
			cityAudioNode.stop();
		else
			cityAudioNode.play();
	}
	
	private void updatePoliceAlarmSound( final Vector3f playerPosition, LogicSantosApplication application ) {
		List< AbstractHuman > policemans = application.getPolicemanTrafficManager().getActivePolicemans();
		if ( policemans.isEmpty() ) {
			policeAlarmAudioNode.stop();
			return;
		}
			
		float minDistance = Float.MAX_VALUE;
		float distance;
		for( AbstractHuman p: policemans ) {
			distance = playerPosition.distance( 
					p.getHumanPhysicalActivity().getControl().getPhysicsLocation() );
			if ( distance<minDistance )
				minDistance=distance;
		}
		
		float policeAlarmSoundVolume = 1.0f;
		
		if ( minDistance>MAX_DISTANCE_TO_HEAR_POLICE_ALARM_SOUND )
			policeAlarmSoundVolume = 0.0f;
		else
			policeAlarmSoundVolume = 1.0f-((minDistance)/MAX_DISTANCE_TO_HEAR_POLICE_ALARM_SOUND);
		
		policeAlarmSoundVolume*=masterVolume;
		if ( policeAlarmAudioNode.getVolume()!=policeAlarmSoundVolume )
			policeAlarmAudioNode.setVolume( 0.5f*policeAlarmSoundVolume );
		if ( policeAlarmAudioNode.getVolume()==0.0f )
			policeAlarmAudioNode.stop();
		else
			policeAlarmAudioNode.play();
	}
	
	
}
