/**
 * 
 */
package it.unical.logic_santos.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import it.unical.logic_santos.gameplay.BulletManagerCookie;
import it.unical.logic_santos.physics.activity.AircraftCookie;
import it.unical.logic_santos.traffic.PolicemanTrafficManagerCookie;
import it.unical.logic_santos.traffic.VehicleTrafficManagerCookie;
import it.unical.logic_santos.traffic.WalkerTrafficManagerCookie;

/**
 * @author Agostino
 *
 */
public class WorldCookie implements ICookie {
	
	public PlayerCookie[] playersCookies = new PlayerCookie[2];
	public WalkerTrafficManagerCookie walkerTrafficManagerCookie = new WalkerTrafficManagerCookie();
	public PolicemanTrafficManagerCookie policemanTrafficManagerCookie = new PolicemanTrafficManagerCookie();
	public VehicleTrafficManagerCookie vehicleTrafficManagerCookie = new VehicleTrafficManagerCookie();
	public BulletManagerCookie bulletManagerCookie = new BulletManagerCookie();
	public AircraftCookie helicopterCookie = new AircraftCookie();

	public static final int MASTER_PLAYER_INDEX = 0;
	public static final int CLIENT_PLAYER_INDEX = 1;
	
	public WorldCookie() {
		for( int i=0; i<playersCookies.length; ++i )
			playersCookies[i] = new PlayerCookie();
	}
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		for( int i=0; i<playersCookies.length; ++i )
			playersCookies[i].writeToDataOutputStream(out);
		
		walkerTrafficManagerCookie.writeToDataOutputStream(out);
		policemanTrafficManagerCookie.writeToDataOutputStream(out);
		vehicleTrafficManagerCookie.writeToDataOutputStream(out);
		bulletManagerCookie.writeToDataOutputStream(out);
		
		helicopterCookie.writeToDataOutputStream(out);
		
	}

	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		for( int i=0; i<playersCookies.length; ++i )
			playersCookies[i].readFromDataInputStream(in);
	
		walkerTrafficManagerCookie.readFromDataInputStream(in);
		policemanTrafficManagerCookie.readFromDataInputStream(in);
		vehicleTrafficManagerCookie.readFromDataInputStream(in);
		bulletManagerCookie.readFromDataInputStream(in);
		
		helicopterCookie.readFromDataInputStream(in);
	}

}
