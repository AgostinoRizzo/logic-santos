/**
 * 
 */
package it.unical.logic_santos.physics.activity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


import it.unical.logic_santos.gameplay.LifeBarCookie;
import it.unical.logic_santos.net.ICookie;
import it.unical.logic_santos.net.Vector3fCookie;
import it.unical.logic_santos.toolkit.math.QuaternionCookie;

/**
 * @author Agostino
 *
 */
public class VehicleCookie implements ICookie {

	public int id;
	
	protected Vector3fCookie initialVehicleForwardDirectionCookie=new Vector3fCookie();
	
	protected int startNodeId;
	protected int targetNodeId;
	
	protected Vector3fCookie previousPositionCookie=new Vector3fCookie();
	
	protected LifeBarCookie lifeBarCookie=new LifeBarCookie();
	protected boolean isDriven;
	protected boolean hasWalkerDriver;
	
	protected float steeringDriveValue;
	protected float accelerationDriveValue;
	
	protected Vector3fCookie position=new Vector3fCookie();
	protected Vector3fCookie linearVelocity=new Vector3fCookie();
	protected Vector3fCookie angularVelocity=new Vector3fCookie();
	protected QuaternionCookie quaternionRotationCookie=new QuaternionCookie();
	
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		out.writeInt(id);
		
		initialVehicleForwardDirectionCookie.writeToDataOutputStream(out);
		
		out.writeInt(startNodeId);
		out.writeInt(targetNodeId);
		
		previousPositionCookie.writeToDataOutputStream(out);
		
		lifeBarCookie.writeToDataOutputStream(out);
		out.writeBoolean(isDriven);
		out.writeBoolean(hasWalkerDriver);
		
		out.writeFloat(steeringDriveValue);
		out.writeFloat(accelerationDriveValue);
		
		position.writeToDataOutputStream(out);
		linearVelocity.writeToDataOutputStream(out);
		angularVelocity.writeToDataOutputStream(out);
		quaternionRotationCookie.writeToDataOutputStream(out);
	}

	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		id = in.readInt();
		
		initialVehicleForwardDirectionCookie.readFromDataInputStream(in);
		
		startNodeId = in.readInt();
		targetNodeId = in.readInt();
		
		previousPositionCookie.readFromDataInputStream(in);
		
		lifeBarCookie.readFromDataInputStream(in);
		isDriven = in.readBoolean();
		hasWalkerDriver = in.readBoolean();
		
		steeringDriveValue = in.readFloat();
		accelerationDriveValue = in.readFloat();
		
		position.readFromDataInputStream(in);
		linearVelocity.readFromDataInputStream(in);
		angularVelocity.readFromDataInputStream(in);
		quaternionRotationCookie.readFromDataInputStream(in);
	}

}
