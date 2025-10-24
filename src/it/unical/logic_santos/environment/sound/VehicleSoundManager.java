/**
 * 
 */
package it.unical.logic_santos.environment.sound;

import java.util.HashMap;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.jme3.audio.AudioData.DataType;

import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.spatial_entity.AbstractVehicle;

/**
 * @author Agostino
 *
 */
public class VehicleSoundManager implements ISoundManager {

	private static VehicleSoundManager instance = null;
	
	private HashMap< AbstractVehicle, AudioNode > vehicleAudioNodeMapping=null;
	private LogicSantosApplication application=null;
	
	private float masterVolume=1.0f;
	private float timeNextPlayClacson=3.0f;
	private float currentTimePlayNextClacson=0.0f;
	
	private static final float MAX_DISTANCE_SOUND = 100.0f;
	private static final float MAX_DISTANCE_PLAYER_CLACSON = 30.0f;
	private static final float MAX_TIME_NEXT_PLAY_CLACSON = 3.0f;
	
	
	public static VehicleSoundManager getInstance() {
		if ( instance==null )
			instance = new VehicleSoundManager();
		return instance;
	}
	
	private VehicleSoundManager() {
		this.vehicleAudioNodeMapping = new HashMap< AbstractVehicle, AudioNode >();
	}
	
	@Override
	public void init(LogicSantosApplication application) {
		this.application = application;
		this.vehicleAudioNodeMapping.clear();
		
	}

	@Override
	public void update(float tpf, LogicSantosApplication application) {
		final Vector3f playerPosition = application.getPlayer()
										.getHumanPhysicalActivity()
										.getControl()
										.getPhysicsLocation();
		float distance;
		float volume;
		if ( currentTimePlayNextClacson<timeNextPlayClacson )
			currentTimePlayNextClacson+=tpf;
		for( AbstractVehicle vehicle: vehicleAudioNodeMapping.keySet() ) {
			
			distance = playerPosition.distance( vehicle.getVehiclePhysicalActivity()
														.getControl().getPhysicsLocation() );
			AudioNode vehicleAudioNode = vehicleAudioNodeMapping.get( vehicle );
			if ( distance> MAX_DISTANCE_SOUND ) {
				volume = 0.0f;
			} else 
				volume = 1.0f-(distance/MAX_DISTANCE_SOUND);
			volume*=masterVolume;
			vehicleAudioNode.setVolume( 0.5f*volume );
			
			if ( (distance<=MAX_DISTANCE_PLAYER_CLACSON) && (currentTimePlayNextClacson>=timeNextPlayClacson) && (vehicle.getVehiclePhysicalActivity().hasWalkerDriver())  ) {
				EffectSoundManager.getInstance().onVehicleClacson( vehicle );
				currentTimePlayNextClacson = 0.0f;
				timeNextPlayClacson = getNewRandomTimeNextPlayClacson();
			}
		}
	}
	
	public void addVehicleSound( AbstractVehicle vehicle ) { 
		AudioNode vehicleAudioNode = createNewVehicleSoundNode( application );
		//vehicleAudioNode.setLocalTranslation( vehicle.getVehiclePhysicalActivity().getControl().getPhysicsLocation().clone() );
		//application.getRootNode().attachChild( vehicleAudioNode );
		//vehicle.getVehiclePhysicalActivity().getNode().attachChild( vehicleAudioNode );
		vehicleAudioNode.play();
		vehicleAudioNodeMapping.put( vehicle, vehicleAudioNode ); 
	}
	
	public void removeVehicleSound( AbstractVehicle vehicle ) {
		AudioNode vehicleAudioNode = vehicleAudioNodeMapping.get( vehicle );
		if ( vehicleAudioNode!=null ) {
			vehicleAudioNode.stop();
			vehicleAudioNodeMapping.remove( vehicle );
		}
	}
	
	@Override
	public void setVolume(float volume) {
		if ( (volume>=0.0f) && (volume<=1.0f) )
			this.masterVolume = volume;
	}
	
	private static AudioNode createNewVehicleSoundNode( LogicSantosApplication application ) {
		AssetManager assetManager = application.getAssetManager();
		
		AudioNode vehicleAudioNode = new AudioNode( assetManager, 
											SoundConfig.VEHICLE_SOUND_EFFECT_NAME, 
											DataType.Buffer );
		vehicleAudioNode.stop();
		vehicleAudioNode.setPositional( false );
		vehicleAudioNode.setLooping( true );
		vehicleAudioNode.setReverbEnabled( true );
		vehicleAudioNode.setVolume( 0.0f );
	
		return vehicleAudioNode;
	}
	
	private static float getNewRandomTimeNextPlayClacson() {
		float time = (float) ( Math.random()*MAX_TIME_NEXT_PLAY_CLACSON );
		if ( time<0.5f )
			time = 0.5f;
		return time;
	}

}
