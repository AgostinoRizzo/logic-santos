/**
 * 
 */
package it.unical.logic_santos.environment.sound;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.jme3.audio.AudioData.DataType;

import it.unical.logic_santos.gameplay.Cannon;
import it.unical.logic_santos.gameplay.Gun;
import it.unical.logic_santos.gameplay.IWeapon;
import it.unical.logic_santos.gameplay.Rifle;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.spatial_entity.AbstractVehicle;

/**
 * @author Agostino
 *
 */
public class EffectSoundManager implements ISoundManager {

	private static EffectSoundManager instance = null;
	
	private static final float MAX_DISTANCE_EXPLOSION = 1000.0f;
	private static final float MAX_DISTANCE_VEHICLE_CLACSON = 300.0f;
	
	private AudioNode gunShootAudioNode=null;
	private AudioNode rifleShootAudioNode=null;
	
	private AudioNode steppingAudioNode=null;
	private AudioNode runningAudioNode=null;
	private AudioNode steppingWaterAudioNode=null;
	private AudioNode runningWaterAudioNode=null;
	
	private AudioNode helicopterAudioNode=null;
	private AudioNode vehicleClacsonAudioNode=null;
	
	private AudioNode explosionAudioNode=null;
	
	//private AudioNode wiiAreaNetworkAudioNode=null;
	
	private LogicSantosApplication application=null;
	
	private float masterVolume=1.0f;
	
	
	public static EffectSoundManager getInstance() {
		if ( instance==null )
			instance = new EffectSoundManager();
		return instance;
	}
	
	private EffectSoundManager() {}
	
	@Override
	public void init(LogicSantosApplication application) {
		this.application = application;
		this.gunShootAudioNode = createNewWeapongShootAudioNode( application, SoundConfig.GUN_SHOOT_SOUND_EFFECT_NAME );
		this.rifleShootAudioNode = createNewWeapongShootAudioNode( application, SoundConfig.RIFLE_SHOOT_SOUND_EFFECT_NAME );
		this.steppingAudioNode = createNewSteppingAudioNode( application, SoundConfig.STEPPING_SOUND_EFFECT_NAME );
		this.runningAudioNode = createNewSteppingAudioNode( application, SoundConfig.RUNNING_SOUND_EFFECT_NAME );
		this.helicopterAudioNode = createNewHelicopterAudioNode( application );
		this.steppingWaterAudioNode = createNewSteppingAudioNode( application, SoundConfig.STEPPING_WATER_SOUND_EFFECT_NAME );
		this.runningWaterAudioNode = createNewSteppingAudioNode( application, SoundConfig.RUNNING_WATER_SOUND_EFFECT_NAME );
		
		this.vehicleClacsonAudioNode = createNewVehicleClacsonAudioNode( application );
		this.explosionAudioNode = createNewExplosionAudioNode( application );
		
		//this.wiiAreaNetworkAudioNode = createNewWiiAreaNetworkAudioNode( application );
	}

	@Override
	public void update(float tpf, LogicSantosApplication application) {
		// TODO Auto-generated method stub
		
	}
	
	public void onWeaponShoot( final IWeapon weapon ) {
		if ( weapon instanceof Gun )
			gunShootAudioNode.playInstance();
		else if ( (weapon instanceof Rifle) || (weapon instanceof Cannon) )
			rifleShootAudioNode.playInstance();
	}
	
	public void onExplosion( final Vector3f position ) {
		final Vector3f playerPosition = 
				application.getPlayer().getHumanPhysicalActivity().getControl().getPhysicsLocation();
		final float distance = playerPosition.distance( position );
		float volume=1.0f;
		if ( distance>MAX_DISTANCE_EXPLOSION )
			volume = 0.0f;
		else
			volume = 1.0f-(distance/MAX_DISTANCE_EXPLOSION);
		explosionAudioNode.setVolume( 0.5f*volume );
		explosionAudioNode.playInstance();
	}
	
	public void onSteppingOn( final boolean isInWater ) {
		if ( isInWater ) {
			steppingAudioNode.stop();
			steppingWaterAudioNode.play();
		} else {
			steppingWaterAudioNode.stop();
			steppingAudioNode.play();
		}
	}
	
	public void onRunningOn( final boolean isInWater ) {
		if ( isInWater ) {
			runningAudioNode.stop();
			runningWaterAudioNode.play();
		} else {
			runningWaterAudioNode.stop();
			runningAudioNode.play();
		}
	}
	
	public void onSteppingOff() {
		steppingAudioNode.stop();
		steppingWaterAudioNode.stop();
		runningAudioNode.stop();
		runningWaterAudioNode.stop();
	}
	
	public void onVehicleClacson( final AbstractVehicle vehicle ) {
		final Vector3f playerPosition = 
				application.getPlayer().getHumanPhysicalActivity().getControl().getPhysicsLocation();
		final float distance = playerPosition.distance( vehicle.getVehiclePhysicalActivity().getControl().getPhysicsLocation() );
		float volume=1.0f;
		if ( distance>MAX_DISTANCE_VEHICLE_CLACSON )
			volume = 0.0f;
		else
			volume = 1.0f-(distance/MAX_DISTANCE_VEHICLE_CLACSON);
		vehicleClacsonAudioNode.setVolume( volume );
		vehicleClacsonAudioNode.playInstance();
	}
	
	public void onHelicopterOn() {
		helicopterAudioNode.play();
	}
	
	public void onHelicopterOff() {
		helicopterAudioNode.stop();
	}
	
	public void onWiiAreaNetworkChanged() {
		
	}
	
	@Override
	public void setVolume(float volume) {
		if ( (volume>=0.0f) && (volume<=1.0f) ) {
			this.masterVolume = volume;
			gunShootAudioNode.setVolume( gunShootAudioNode.getVolume()*masterVolume );
			steppingAudioNode.setVolume( steppingAudioNode.getVolume()*masterVolume );
		}
	}
	
	private static AudioNode createNewWeapongShootAudioNode( LogicSantosApplication application, final String soundEffectName ) {
		AssetManager assetManager = application.getAssetManager();
		
		AudioNode gunShootAudioNode = new AudioNode( assetManager, 
											soundEffectName, 
											DataType.Buffer );
		gunShootAudioNode.stop();
		gunShootAudioNode.setPositional( false );
		gunShootAudioNode.setLooping( false );
		gunShootAudioNode.setReverbEnabled( true );
		gunShootAudioNode.setVolume( 0.3f );
		
		return gunShootAudioNode;
	}
	
	private static AudioNode createNewSteppingAudioNode( LogicSantosApplication application, final String soundEffectName ) {
		AssetManager assetManager = application.getAssetManager();
		
		AudioNode steppingAudioNode = new AudioNode( assetManager, 
											soundEffectName, 
											DataType.Buffer );
		steppingAudioNode.stop();
		steppingAudioNode.setPositional( false );
		steppingAudioNode.setLooping( true );
		steppingAudioNode.setReverbEnabled( true );
		steppingAudioNode.setVolume( 0.1f );
		steppingAudioNode.setTimeOffset( 1.0f );
		
		return steppingAudioNode;
	}
	
	private static AudioNode createNewHelicopterAudioNode( LogicSantosApplication application ) {
		AssetManager assetManager = application.getAssetManager();
		
		AudioNode helicopterAudioNode = new AudioNode( assetManager, 
											SoundConfig.HELICOPTER_SOUND_EFFECT_NAME, 
											DataType.Buffer );
		helicopterAudioNode.stop();
		helicopterAudioNode.setPositional( false );
		helicopterAudioNode.setLooping( true );
		helicopterAudioNode.setReverbEnabled( true );
		helicopterAudioNode.setVolume( 1.0f );
		
		return helicopterAudioNode;
	}
	
	
	private static AudioNode createNewExplosionAudioNode( LogicSantosApplication application ) {
		AssetManager assetManager = application.getAssetManager();
		
		AudioNode explosionAudioNode = new AudioNode( assetManager, 
											SoundConfig.EXPLOSION_SOUND_EFFECT_NAME, 
											DataType.Buffer );
		explosionAudioNode.stop();
		explosionAudioNode.setPositional( false );
		explosionAudioNode.setLooping( false );
		explosionAudioNode.setReverbEnabled( true );
		explosionAudioNode.setVolume( 0.5f );
		
		return explosionAudioNode;
	}
	
	private static AudioNode createNewVehicleClacsonAudioNode( LogicSantosApplication application ) {
		AssetManager assetManager = application.getAssetManager();
		
		AudioNode vehicleClacsonAudioNode = new AudioNode( assetManager, 
											SoundConfig.VEHICLE_CLACSON_SOUND_EFFECT_NAME, 
											DataType.Buffer );
		vehicleClacsonAudioNode.stop();
		vehicleClacsonAudioNode.setPositional( false );
		vehicleClacsonAudioNode.setLooping( false );
		vehicleClacsonAudioNode.setReverbEnabled( true );
		vehicleClacsonAudioNode.setVolume( 1.0f );
		
		return vehicleClacsonAudioNode;
	}
	
	/*private static AudioNode createNewWiiAreaNetworkAudioNode( LogicSantosApplication application ) {
		AssetManager assetManager = application.getAssetManager();
		
		AudioNode wiiAreaNetworkAudioNode = new AudioNode( assetManager, 
											SoundConfig.WII_AREA_NETWORK_SOUND_EFFECT_NAME, 
											DataType.Buffer );
		wiiAreaNetworkAudioNode.stop();
		wiiAreaNetworkAudioNode.setPositional( false );
		wiiAreaNetworkAudioNode.setLooping( false );
		wiiAreaNetworkAudioNode.setReverbEnabled( false );
		wiiAreaNetworkAudioNode.setVolume( 0.5f );
		
		return wiiAreaNetworkAudioNode;
	}*/
	

}
