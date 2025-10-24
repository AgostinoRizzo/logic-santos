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
import it.unical.logic_santos.physics.activity.PolicemanCookie;

/**
 * @author Agostino
 *
 */
public class PolicemanTrafficManagerCookie implements ICookie {

	protected List< PolicemanCookie > activePolicemansCookies=new ArrayList< PolicemanCookie >();
	protected List< PolicemanCookie > idlePolicemansCookies=new ArrayList< PolicemanCookie>();
	
	protected float trafficIntensity;
	protected float updateSleepTime;
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		
		writePolicemansCookiesToDataOutputStream(activePolicemansCookies, out);
		writePolicemansCookiesToDataOutputStream(idlePolicemansCookies, out);
		
		out.writeFloat(trafficIntensity);
		out.writeFloat(updateSleepTime);
	}

	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		
		readPolicemansCookiesFromDataInputStream(activePolicemansCookies, in);
		readPolicemansCookiesFromDataInputStream(idlePolicemansCookies, in);
		
		trafficIntensity = in.readFloat();
		updateSleepTime = in.readFloat();
	}
	
	private static void writePolicemansCookiesToDataOutputStream( final List< PolicemanCookie > policemansCookies, DataOutputStream out ) throws IOException {
		final int policemansCookiesSize = policemansCookies.size();
		out.writeInt(policemansCookiesSize);
		
		for( int i=0; i<policemansCookiesSize; ++i )
			policemansCookies.get(i).writeToDataOutputStream(out);
	}
	
	private static void readPolicemansCookiesFromDataInputStream( List< PolicemanCookie > policemansCookies, DataInputStream in ) throws IOException {
		policemansCookies.clear();
		final int policemansCookiesSize = in.readInt();
		
		for( int i=0; i<policemansCookiesSize; ++i ) {
			
			PolicemanCookie policemanCookie = new PolicemanCookie();
			policemanCookie.readFromDataInputStream(in);
			policemansCookies.add(policemanCookie);
		}
	}

}
