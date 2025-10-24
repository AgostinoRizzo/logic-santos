/**
 * 
 */
package it.unical.logic_santos.physics.activity;

/**
 * @author Agostino
 *
 */
public class PolicemanAnimation extends HumanAnimation {

	public PolicemanAnimation() { 
		super( IDLE_AIMING.getAnimationName() );
	}
	
	public PolicemanAnimation( final String animationName ) {
		this.setAnimationName( animationName );
	}
	
	@Override
	public void setAnimationName(String animationName) {
		setAnimation( new HumanAnimation( animationName ) );
	}
	
	@Override
	public void setAnimation(HumanAnimation animation) {
		if ( animation.equals( HumanAnimation.IDLE ) )
			super.setAnimation( HumanAnimation.IDLE_AIMING );
		else if ( animation.equals( HumanAnimation.WALKING ) )
			super.setAnimation( HumanAnimation.WALKING_AIMING );
		else if ( animation.equals( HumanAnimation.IDLE_AIMING ) ||
				  animation.equals( HumanAnimation.WALKING_AIMING ) ||
				  animation.equals( HumanAnimation.RUNNING ) ||
			 	  animation.equals( HumanAnimation.HIT_REACTION ) )
			super.setAnimation( animation );
	}
}
