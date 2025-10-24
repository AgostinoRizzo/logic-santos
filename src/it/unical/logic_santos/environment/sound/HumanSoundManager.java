/**
 * 
 */
package it.unical.logic_santos.environment.sound;



import java.util.HashMap;


import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData.DataType;
import com.jme3.math.Vector3f;

import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.spatial_entity.AbstractHuman;

/**
 * @author Agostino
 *
 */
public class HumanSoundManager implements ISoundManager {

	private static HumanSoundManager instance = null;
	
	private HashMap< AbstractHuman, AudioNode > walkersAudioNodeMapping=null;
	private HashMap< AbstractHuman, AudioNode > policemansAudioNodeMapping=null;
	private LogicSantosApplication application=null;
	
	private float masterVolume=1.0f;
	
	private static final float MAX_DISTANCE_SOUND = 100.0f;
	//private static final float MAX_TIME_HUMAN_SOUND_OFFSET_PLAY = 12.0f;
	
	private static final int MAX_WALKER_SOUND_COUNT    = 3;
	private static final int MAX_POLICEMAN_SOUND_COUNT = 3;
	
	private static float currentWalkerPlayOffset    = 0.0f;
	private static float currentPolicemanPlayOffset = 0.0f;
	
	//private static Random random = new Random( System.currentTimeMillis() );
	
	
	public static HumanSoundManager getInstance() {
		if ( instance==null )
			instance = new HumanSoundManager();
		return instance;
	}
	
	private HumanSoundManager() {
		this.walkersAudioNodeMapping = new HashMap< AbstractHuman, AudioNode >();
		this.policemansAudioNodeMapping = new HashMap< AbstractHuman, AudioNode >();
	}
	
	@Override
	public void init(LogicSantosApplication application) {
		this.application = application;
		this.walkersAudioNodeMapping.clear();
		this.policemansAudioNodeMapping.clear();
		
	}

	@Override
	public void update(float tpf, LogicSantosApplication application) {
		final Vector3f playerPosition = application.getPlayer()
										.getHumanPhysicalActivity()
										.getControl()
										.getPhysicsLocation();
		float distance;
		float volume;
		HashMap< AbstractHuman, AudioNode > mapping;
		
		for( int i=0; i<2; ++i ) {
			mapping= (i==0) ? walkersAudioNodeMapping : policemansAudioNodeMapping;
			
			for( AbstractHuman human: mapping.keySet() ) {
				
				distance = playerPosition.distance( human.getHumanPhysicalActivity()
															.getControl().getPhysicsLocation() );
				AudioNode humanAudioNode = mapping.get( human );
				if ( distance> MAX_DISTANCE_SOUND ) {
					volume = 0.0f;
				} else 
					volume = 1.0f-(distance/MAX_DISTANCE_SOUND);
				volume*=masterVolume;
				if ( i==0 )
					volume*=0.5f;
				else
					volume*=0.8f;
				humanAudioNode.setVolume( volume );
				
			}
		}
	}
	
	public void addWalkerSound( AbstractHuman walker ) { 
		if ( walkersAudioNodeMapping.keySet().size()>=MAX_WALKER_SOUND_COUNT )
			return;
		AudioNode walkerAudioNode = createNewWalkerSoundNode( application, SoundConfig.WALKER_ROAMING_SOUND_EFFECT_NAME );
		walkerAudioNode.play();
		walkersAudioNodeMapping.put( walker, walkerAudioNode ); 
	}
	
	public void removeWalkerSound( AbstractHuman walker ) {
		AudioNode walkerAudioNode = walkersAudioNodeMapping.get( walker );
		if ( walkerAudioNode!=null ) {
			walkerAudioNode.stop();
			walkersAudioNodeMapping.remove( walker );
		}
	}
	
	public void addPolicemanSound( AbstractHuman policeman ) { 
		if ( policemansAudioNodeMapping.keySet().size()>=MAX_POLICEMAN_SOUND_COUNT )
			return;
		AudioNode policemanAudioNode = createNewPolicemanSoundNode( application );
		policemanAudioNode.play();
		policemansAudioNodeMapping.put( policeman, policemanAudioNode ); 
	}
	
	public void removePolicemanSound( AbstractHuman policeman ) {
		AudioNode policemanAudioNode = policemansAudioNodeMapping.get( policeman );
		if ( policemanAudioNode!=null ) {
			policemanAudioNode.stop();
			policemansAudioNodeMapping.remove( policeman );
		}
	}
	
	public void changeToRoamingWalkerSoundState( AbstractHuman walker ) {
		AudioNode walkerAudioNode = walkersAudioNodeMapping.get( walker );
		if ( walkerAudioNode!=null ) {
			walkerAudioNode.stop();
			walkersAudioNodeMapping.remove( walker );
			walkerAudioNode = createNewWalkerSoundNode( application, SoundConfig.WALKER_ROAMING_SOUND_EFFECT_NAME );
			walkerAudioNode.play();
			walkersAudioNodeMapping.put( walker, walkerAudioNode ); 
		}
	}
	
	public void changeToFightWalkerSoundState( AbstractHuman walker ) {
		AudioNode walkerAudioNode = walkersAudioNodeMapping.get( walker );
		if ( walkerAudioNode!=null ) {
			walkerAudioNode.stop();
			walkersAudioNodeMapping.remove( walker );
			walkerAudioNode = createNewWalkerSoundNode( application, SoundConfig.WALKER_FIGHT_SOUND_EFFECT_NAME );
			walkerAudioNode.play();
			walkersAudioNodeMapping.put( walker, walkerAudioNode ); 
		}
	}
	
	@Override
	public void setVolume(float volume) {
		if ( (volume>=0.0f) && (volume<=1.0f) )
			this.masterVolume = volume;
	}
	
	private static AudioNode createNewWalkerSoundNode( LogicSantosApplication application, final String soundEffectName ) {
		AssetManager assetManager = application.getAssetManager();
		
		AudioNode walkerAudioNode = new AudioNode( assetManager, 
											soundEffectName, 
											DataType.Buffer );
		walkerAudioNode.stop();
		walkerAudioNode.setPositional( false );
		walkerAudioNode.setLooping( true );
		walkerAudioNode.setReverbEnabled( true );
		walkerAudioNode.setVolume( 0.0f );
		walkerAudioNode.setTimeOffset( getNewWalkerRandomTimePlayOffset() );
	
		return walkerAudioNode;
	}
	
	private static AudioNode createNewPolicemanSoundNode( LogicSantosApplication application ) {
		AssetManager assetManager = application.getAssetManager();
		
		AudioNode policemanAudioNode = new AudioNode( assetManager, 
											SoundConfig.POLICEMAN_SOUND_EFFECT_NAME, 
											DataType.Buffer );
		policemanAudioNode.stop();
		policemanAudioNode.setPositional( false );
		policemanAudioNode.setLooping( true );
		policemanAudioNode.setReverbEnabled( true );
		policemanAudioNode.setVolume( 0.0f );
		policemanAudioNode.setTimeOffset( getNewPolicemanRandomTimePlayOffset() );
	
		return policemanAudioNode;
	}
	
	private static final float getNewWalkerRandomTimePlayOffset() {
		float time = currentWalkerPlayOffset; //(float) (random.nextInt((int) MAX_TIME_HUMAN_SOUND_OFFSET_PLAY));
		currentWalkerPlayOffset+=3.0f;
		return time;
	}
	
	private static final float getNewPolicemanRandomTimePlayOffset() {
		float time = currentPolicemanPlayOffset; //(float) (random.nextInt((int) MAX_TIME_HUMAN_SOUND_OFFSET_PLAY));
		currentPolicemanPlayOffset+=3.0f;
		return time;
	}
}
