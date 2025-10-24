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
public class LifeBarCookie implements ICookie {

	public float value;
	public float increaseByTimeAmount;
	
	public void set( final LifeBar bar ) {
		this.value = bar.value;
		this.increaseByTimeAmount = bar.increaseByTimeAmount;
	}
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		out.writeFloat(value);
		out.writeFloat(increaseByTimeAmount);
	}

	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		value = in.readFloat();
		increaseByTimeAmount = in.readFloat();
	}

}
