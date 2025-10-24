/**
 * 
 */
package it.unical.logic_santos.physics.activity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


import it.unical.logic_santos.net.ICookie;
import it.unical.logic_santos.net.Vector3fCookie;

/**
 * @author Agostino
 *
 */
public class AircraftCookie implements ICookie {

	public float   currentRotationSideScale=0.0f;
	public boolean isDriven=false;
	public boolean hasWalkerDriver=true;
	public boolean engineOn=false;
	public float   currentPropellerRotation=0.0f;
	
	public float gravity=0.0f;
	
	public Vector3fCookie previousPositionCookie=new Vector3fCookie();
	public Vector3fCookie currentPositionCookie=new Vector3fCookie();
	public Vector3fCookie viewDirectionCookie=new Vector3fCookie();
	public Vector3fCookie moveDirectionCookie=new Vector3fCookie();
	
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		out.writeFloat(currentRotationSideScale);
		out.writeBoolean(isDriven);
		out.writeBoolean(hasWalkerDriver);
		out.writeBoolean(engineOn);
		out.writeFloat(currentPropellerRotation);
		
		out.writeFloat(gravity);
		
		previousPositionCookie.writeToDataOutputStream(out);
		currentPositionCookie.writeToDataOutputStream(out);
		viewDirectionCookie.writeToDataOutputStream(out);
		moveDirectionCookie.writeToDataOutputStream(out);
		
	}

	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		currentRotationSideScale = in.readFloat();
		isDriven = in.readBoolean();
		hasWalkerDriver = in.readBoolean();
		engineOn = in.readBoolean();
		currentPropellerRotation = in.readFloat();
		
		gravity = in.readFloat();
		
		previousPositionCookie.readFromDataInputStream(in);
		currentPositionCookie.readFromDataInputStream(in);
		viewDirectionCookie.readFromDataInputStream(in);
		moveDirectionCookie.readFromDataInputStream(in);
		
	}

}
