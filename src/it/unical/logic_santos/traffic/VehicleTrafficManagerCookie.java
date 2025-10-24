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
import it.unical.logic_santos.physics.activity.VehicleCookie;

/**
 * @author Agostino
 *
 */
public class VehicleTrafficManagerCookie implements ICookie {

	public List< VehicleCookie > vehiclesCookies=new ArrayList< VehicleCookie >();
	
	protected float trafficIntensity;
	protected float updateDistancePlayerSleepTime;
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		final int sizeVehiclesCookies = vehiclesCookies.size();
		out.writeInt(sizeVehiclesCookies);
		for( int i=0; i<sizeVehiclesCookies; ++i )
			vehiclesCookies.get(i).writeToDataOutputStream(out);
		
		out.writeFloat(trafficIntensity);
		out.writeFloat(updateDistancePlayerSleepTime);
	}

	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		vehiclesCookies.clear();
		final int sizeVehiclesCookies = in.readInt();
		for( int i=0; i<sizeVehiclesCookies; ++i ) {
			
			VehicleCookie vehicleCookie = new VehicleCookie();
			vehicleCookie.readFromDataInputStream(in);
			vehiclesCookies.add(vehicleCookie);
		}
		
		trafficIntensity = in.readFloat();
		updateDistancePlayerSleepTime = in.readFloat();
	}

}
