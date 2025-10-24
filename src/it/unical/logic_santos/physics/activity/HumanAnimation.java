/**
 * 
 */
package it.unical.logic_santos.physics.activity;

import it.unical.logic_santos.net.AnimationCookie;
import jogamp.graph.font.typecast.ot.table.ID;

/**
 * @author Agostino
 *
 */
public class HumanAnimation {

	public static final HumanAnimation IDLE           = new HumanAnimation( "Idle" );
	public static final HumanAnimation FIGHT_IDLE     = new HumanAnimation( "FightIdle" );
	public static final HumanAnimation IDLE_AIMING    = new HumanAnimation( "IdleAiming" );
	public static final HumanAnimation WALKING        = new HumanAnimation( "Walking" );
	public static final HumanAnimation WALKING_AIMING = new HumanAnimation( "WalkingAiming" );
	public static final HumanAnimation RUNNING        = new HumanAnimation( "Running" );
	public static final HumanAnimation JUMPING        = new HumanAnimation( "Jumping" );
	public static final HumanAnimation HIT_REACTION   = new HumanAnimation( "HitReaction" );
	
	private String animationName=null;
	
	public static byte getAnimationId( final HumanAnimation animation ) {
		if ( animation.equals( IDLE ) )
			return 0;
		if ( animation.equals( FIGHT_IDLE ) )
			return 1;
		if ( animation.equals( IDLE_AIMING ) )
			return 2;
		if ( animation.equals( WALKING ) )
			return 3;
		if ( animation.equals( WALKING_AIMING ) )
			return 4;
		if ( animation.equals( RUNNING ) )
			return 5;
		if ( animation.equals( JUMPING ) )
			return 6;
		if ( animation.equals( HIT_REACTION ) )
			return 7;
		return 0;
	}
	
	public static HumanAnimation getAnimationFromId( final byte animationId ) {
		switch( animationId ) {
		case 0: return IDLE;
		case 1: return FIGHT_IDLE;
		case 2: return IDLE_AIMING;
		case 3: return WALKING;
		case 4: return WALKING_AIMING;
		case 5: return RUNNING;
		case 6: return JUMPING;
		case 7: return HIT_REACTION;
		default: return IDLE;
		}
	}
	
	public HumanAnimation() { 
		this.animationName = IDLE.getAnimationName();
	}
	
	public HumanAnimation( final String animationName ) {
		this.animationName = animationName;
	}
	
	public String getAnimationName() {
		return animationName;
	}
	
	public void setAnimationName(String animationName) {
		this.animationName = animationName;
	}
	
	public void setAnimation(HumanAnimation animation) {
		this.animationName = animation.animationName;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if ( obj instanceof HumanAnimation ) {
			final HumanAnimation other = (HumanAnimation) obj;
			return ( this.animationName==other.animationName );
		}
		return false;
	}
	
	public void setCookie( final AnimationCookie cookie ) {
		this.animationName = getAnimationFromId(cookie.animationId).getAnimationName();
	}
}
