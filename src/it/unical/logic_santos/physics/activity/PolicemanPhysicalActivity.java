/**
 * 
 */
package it.unical.logic_santos.physics.activity;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.animation.Skeleton;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LodControl;
import com.jme3.scene.debug.SkeletonDebugger;

import it.unical.logic_santos.ai.IAgent;
import it.unical.logic_santos.ai.PlayerFollowingData;
import it.unical.logic_santos.ai.PolicemanAgent;
import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.environment.sound.EffectSoundManager;
import it.unical.logic_santos.gameplay.IBullet;
import it.unical.logic_santos.gameplay.IObserver;
import it.unical.logic_santos.gameplay.ISubject;
import it.unical.logic_santos.gameplay.Rifle;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.spatial_entity.AbstractHuman;
import it.unical.logic_santos.spatial_entity.Player;
import it.unical.logic_santos.spatial_entity.Policeman;
import it.unical.logic_santos.universe.AbstractWorld;

/**
 * @author Agostino
 *
 */
public class PolicemanPhysicalActivity extends WalkerPhysicalActivity implements ISubject {

	private IObserver policemanTrafficManager=null;
	private IAgent policemanAgent=null;
	private float maxRandomDistanceToStop=0.0f;
	private float randomTimeNextShoot=0.0f;
	private float currentTimeNextShoot=0.0f;
	private float hitReactionTimeCount=-1.0f;
	private boolean idle=false;
	private AnimControl headAnimationControl=null;
	private AnimChannel headAnimationChannel=null;
	
	private static final float HIT_REACTION_TIME = 5.0f; // expressed in seconds
	
	public PolicemanPhysicalActivity(AbstractHuman owner) {
		super(owner);
		this.policemanAgent = new PolicemanAgent( this );
	}
	
	public PolicemanPhysicalActivity(AbstractHuman owner, AbstractWorld world) {
		super(owner);
		this.world = world;
		this.policemanAgent = new PolicemanAgent( this );
	}
	
	@Override
	public void initActivity() {
		idle=false;
		super.initActivity();
	}
	
	/*@Override
	public void updateAnimation( final Vector3f walkDirection, final float airTime ) {
		if ( walkDirection.lengthSquared()==0 ) {
			if ( !"stands".equals( animationChannel.getAnimationName() ) ) {
				animationChannel.setAnim("stand", 0.0f);
				headAnimationChannel.setAnim("stand", 0.0f);
			}
		} else {
			if (airTime>0.3f) {
				if ( !"stand".equals( animationChannel.getAnimationName() ) ) {
					animationChannel.setAnim("stand", 0.0f);
					headAnimationChannel.setAnim("stand", 0.0f);
				}
			} else if ( !"Walk".equals( animationChannel.getAnimationName() ) ) {
				animationChannel.setAnim("Walk", 1.1f);
				headAnimationChannel.setAnim("Walk", 1.1f);
			}
		}
		
		animationChannel.setLoopMode( LoopMode.Loop );
		headAnimationChannel.setLoopMode( LoopMode.Loop );
		
	}*/
	
	public void setObserver( IObserver policemanTrafficManager ) {
		this.policemanTrafficManager = policemanTrafficManager;
	}
	
	@Override
	public void onShootReaction(IBullet bullet) {
		super.onShootReaction(bullet);
		if ( this.lifeBar.isEmpty() && (this.policemanTrafficManager!=null) ) {
			policemanTrafficManager.onStateShanged( this );
			
			AbstractHuman bulletOwner = bullet.getOwner();
			if ( bulletOwner instanceof Player ) {
				
				Player player = (Player) bulletOwner;
				if ( player.hasManager() )
					player.getManager().getPlayerCareerStatus().earn( bullet.getGainValue() );
			}
		}
	}
	
	@Override
	public void update(float tpf) { System.out.println("TIME UPDATE: " + tpf);
		if ( isHitReactionTimeCounting() && 
				(!isHitReactionTimeDone()) )
			hitReactionTimeCount+=tpf;
		
		if ( idle ) {
			if ( lifeBar.isEmpty() )
				animation.setAnimation( PolicemanAnimation.HIT_REACTION );
			setAnimation( animation.getAnimationName() );
			return;
		}
		
		policemanAgent.think( tpf );
		
	}
	
	public IAgent getAgent() {
		return policemanAgent;
	}
	
	public PolicemanAgent getPolicemanAgent() {
		return ((PolicemanAgent) policemanAgent);
	}
	
	public void followPlayer( final PlayerFollowingData playerFollowingData, final float tpf ) {
		if ( playerFollowingData.isNull() || (!hasMaxRandomDistanceToStop()) || (!hasRandomTimeNextShoot()) )
			return;
		System.out.println("DISTANCE PLAYER POSITION FOLLOWING: " + playerFollowingData.getDistance());
		if ( playerFollowingData.getDistance() > maxRandomDistanceToStop ) {
			this.getAnimation().setAnimation( HumanAnimation.WALKING_AIMING );
			//this.owner.getHumanPhysicalActivity().run();
			this.owner.getHumanPhysicalActivity().walk(playerFollowingData.getDirection().mult(0.1f), tpf, true);
			clearMaxRandomDistanceToStop();
		} else {
			this.getAnimation().setAnimation( HumanAnimation.IDLE_AIMING );
			this.owner.getHumanPhysicalActivity().stopWalking( playerFollowingData.getDirection() ); System.out.println(" STOP WALKING POLICEMAN!!!");
		}
		
		this.setStartPathsNetworkNode(null);
		
		this.currentTimeNextShoot += tpf; System.out.println("TIME: " + tpf);
		if ( this.currentTimeNextShoot>=this.randomTimeNextShoot ) {
			this.getPolicemanOwner().shoot(); System.out.println("SHOOT!!!");
			this.currentTimeNextShoot=0.0f;
			clearRandomTimeNextShoot();
			
			EffectSoundManager.getInstance().onWeaponShoot( new Rifle() );
		}
		
	}
	
	@Override
	public void run() {
		super.run();
		animation.setAnimation( HumanAnimation.RUNNING );
	}
	
	public boolean hasMaxRandomDistanceToStop() {
		return ( this.maxRandomDistanceToStop>0.0f );
	}
	
	public void setMaxRandomDistanceToStop( final float maxRandomDistanceToStop ) {
		this.maxRandomDistanceToStop = maxRandomDistanceToStop;
	}
	
	public void clearMaxRandomDistanceToStop() {
		this.maxRandomDistanceToStop=0.0f;
	}
	
	public boolean hasRandomTimeNextShoot() {
		return ( this.randomTimeNextShoot>0.0f );
	}
	
	public void setRandomTimeNextShoot( final float randomTimeNextShoot ) {
		this.randomTimeNextShoot = randomTimeNextShoot;
		this.currentTimeNextShoot=0.0f;
	}
	
	public void clearRandomTimeNextShoot() {
		this.randomTimeNextShoot=0.0f;
		this.currentTimeNextShoot=0.0f;
	}
	
	public boolean isHitReactionTimeCounting() {
		return ( hitReactionTimeCount>=0.0f );
	}
	
	public void setIsHitReactionTimeCounting( final boolean counting ) {
		hitReactionTimeCount= (counting) ? 0.0f : -1.0f;
	}
	
	public boolean isHitReactionTimeDone() {
		return ( hitReactionTimeCount>=HIT_REACTION_TIME );
	}
	
	public void setIdle(boolean idle) {
		this.idle = idle;
	}
	
	public PolicemanCookie getPolicemanCookie() {
		PolicemanCookie cookie = new PolicemanCookie();
		cookie.setActive( !this.idle );
		cookie.walkerCookie = super.getWalkerCookie();
		
		cookie.maxRandomDistanceToStop = this.maxRandomDistanceToStop;
		cookie.randomTimeNextShoot = this.randomTimeNextShoot;
		cookie.currentTimeNextShoot = this.currentTimeNextShoot;
		cookie.hitReactionTimeCount = this.hitReactionTimeCount;
		
		cookie.idle = this.idle;
		
		return cookie;
	}
	
	public void setPolicemanCookie( final PolicemanCookie cookie ) {
		super.setWalkerCookie(cookie.walkerCookie);
		
		this.maxRandomDistanceToStop = cookie.maxRandomDistanceToStop;
		this.randomTimeNextShoot = cookie.randomTimeNextShoot;
		this.currentTimeNextShoot = cookie.currentTimeNextShoot;
		this.hitReactionTimeCount = cookie.hitReactionTimeCount;
		
		this.idle = cookie.idle;
	}
	
	@Override
	protected HumanAnimation createAnimation() {
		return ( new PolicemanAnimation() );
	}
	
	private Policeman getPolicemanOwner() {
		return ((Policeman) this.owner);
	}
	
	
}
