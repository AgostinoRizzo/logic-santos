/**
 * 
 */
package it.unical.logic_santos.traffic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unical.logic_santos.net.ICookie;
import it.unical.logic_santos.physics.activity.WalkerCookie;

/**
 * @author Agostino
 *
 */
public class WalkerTrafficManagerCookie implements ICookie {

	public List< WalkerCookie > walkersCookies=new ArrayList< WalkerCookie >();
	public float trafficIntensity;
	public float updateSleepTime;
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		final int sizeWalkersCookies = walkersCookies.size();
		out.writeInt(sizeWalkersCookies);
		for( int i=0; i<sizeWalkersCookies; ++i )
			walkersCookies.get(i).writeToDataOutputStream(out);
		
		out.writeFloat(trafficIntensity);
		out.writeFloat(updateSleepTime);
		
	}

	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		walkersCookies.clear();
		final int sizeWalkersCookies = in.readInt();
		for( int i=0; i<sizeWalkersCookies; ++i ) {
			
			WalkerCookie walkerCookie = new WalkerCookie();
			walkerCookie.readFromDataInputStream(in);
			walkersCookies.add(walkerCookie);
		}
		
		trafficIntensity = in.readFloat();
		updateSleepTime = in.readFloat();
		
	}

}
