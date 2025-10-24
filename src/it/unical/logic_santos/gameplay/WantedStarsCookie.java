/**
 * 
 */
package it.unical.logic_santos.gameplay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import it.unical.logic_santos.net.ICookie;

/**
 * @author Agostino
 *
 */
public class WantedStarsCookie implements ICookie {

	public int currentStars=0;
	public int currentShootStartedValues=0;
	public int currentShootHitValues=0;
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		out.writeInt(currentStars);
		out.writeInt(currentShootStartedValues);
		out.writeInt(currentShootHitValues);
	}
	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		currentStars = in.readInt();
		currentShootStartedValues = in.readInt();
		currentShootHitValues = in.readInt();
	}
	
}
