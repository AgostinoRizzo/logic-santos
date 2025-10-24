/**
 * 
 */
package it.unical.logic_santos.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


import it.unical.logic_santos.physics.activity.HumanAnimation;

/**
 * @author Agostino
 *
 */
public class AnimationCookie implements ICookie {

	public byte animationId;
	
	public AnimationCookie() {}
	
	public AnimationCookie( final HumanAnimation animation ) {
		this.animationId = HumanAnimation.getAnimationId( animation );
	}
	
	public void set( final HumanAnimation animation ) {
		this.animationId = HumanAnimation.getAnimationId( animation );
	}
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		out.writeByte( animationId );
	}

	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		animationId = in.readByte();
	}
	
	public HumanAnimation getAnimation() {
		return HumanAnimation.getAnimationFromId( animationId );
	}

}
