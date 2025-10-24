/**
 * 
 */
package it.unical.logic_santos.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.jme3.math.Vector3f;

import it.unical.logic_santos.gameplay.LifeBarCookie;
import it.unical.logic_santos.gameplay.WantedStarsCookie;

/**
 * @author Agostino
 *
 */
public class PlayerCookie implements ICookie {

	public int id;
	
	public Vector3f position=new Vector3f();
	public Vector3f viewDirection=new Vector3f();
	public Vector3f walkDirection=new Vector3f();
	public LifeBarCookie lifeBarCookie=new LifeBarCookie();
	public AnimationCookie animationCookie=new AnimationCookie();
	
	public WeaponCookie weaponCookie=new WeaponCookie();
	public boolean isActive;
	public boolean isRunning;
	public boolean isVisible;
	public WantedStarsCookie wantedStarsCookie=new WantedStarsCookie();
	
	public Vector3f previousPosition=new Vector3f();
	
	public boolean[] movements=new boolean[5];
	
	
	public PlayerCookie() {}
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		out.writeInt(id);
		
		ICookie.writeVector3fToDataOutputStream(position, out);
		ICookie.writeVector3fToDataOutputStream(viewDirection, out);
		ICookie.writeVector3fToDataOutputStream(walkDirection, out);
		lifeBarCookie.writeToDataOutputStream(out);
		animationCookie.writeToDataOutputStream(out);
		
		weaponCookie.writeToDataOutputStream(out);
		out.writeBoolean(isActive);
		out.writeBoolean(isRunning);
		out.writeBoolean(isVisible);
		wantedStarsCookie.writeToDataOutputStream(out);
		
		for( int i=0; i<5; ++i )
			out.writeBoolean(movements[i]);
		
		ICookie.writeVector3fToDataOutputStream(previousPosition, out);
		
	}

	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		id = in.readInt();
		
		ICookie.readVector3fFromDataInputStream(position, in);
		ICookie.readVector3fFromDataInputStream(viewDirection, in);
		ICookie.readVector3fFromDataInputStream(walkDirection, in);
		lifeBarCookie.readFromDataInputStream(in);
		animationCookie.readFromDataInputStream(in);
		
		weaponCookie.readFromDataInputStream(in);
		isActive = in.readBoolean();
		isRunning = in.readBoolean();
		isVisible = in.readBoolean();
		wantedStarsCookie.readFromDataInputStream(in);
		
		for( int i=0; i<5; ++i )
			movements[i] = in.readBoolean();
		
		ICookie.readVector3fFromDataInputStream(previousPosition, in);
		
	}

}
