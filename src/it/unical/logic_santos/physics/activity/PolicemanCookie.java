/**
 * 
 */
package it.unical.logic_santos.physics.activity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import it.unical.logic_santos.net.ICookie;

/**
 * @author Agostino
 *
 */
public class PolicemanCookie implements ICookie  {

	public WalkerCookie walkerCookie=new WalkerCookie();
	
	protected float maxRandomDistanceToStop;
	protected float randomTimeNextShoot;
	protected float currentTimeNextShoot;
	protected float hitReactionTimeCount;
	protected boolean idle;
	
	private boolean isActive=true;
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		walkerCookie.writeToDataOutputStream(out);
		
		out.writeFloat(maxRandomDistanceToStop);
		out.writeFloat(randomTimeNextShoot);
		out.writeFloat(currentTimeNextShoot);
		out.writeFloat(hitReactionTimeCount);
		
		out.writeBoolean(idle);
	}

	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		walkerCookie.readFromDataInputStream(in);
		
		maxRandomDistanceToStop = in.readFloat();
		randomTimeNextShoot = in.readFloat();
		currentTimeNextShoot = in.readFloat();
		hitReactionTimeCount = in.readFloat();
		
		idle = in.readBoolean();
	}
	
	public void setActive(boolean isActive) {
		this.walkerCookie.setActive( isActive );
		this.isActive = isActive;
	}

}
