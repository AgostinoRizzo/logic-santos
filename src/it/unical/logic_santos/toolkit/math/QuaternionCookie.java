/**
 * 
 */
package it.unical.logic_santos.toolkit.math;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.jme3.math.Quaternion;

import it.unical.logic_santos.net.ICookie;

/**
 * @author Agostino
 *
 */
public class QuaternionCookie implements ICookie {

	protected Quaternion quaternion=new Quaternion();
	
	public Quaternion get() {
		return quaternion;
	}
	
	public void set( final Quaternion quaternion ) {
		this.quaternion = quaternion.clone();
	}
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		out.writeFloat(quaternion.getX());
		out.writeFloat(quaternion.getY());
		out.writeFloat(quaternion.getZ());
		out.writeFloat(quaternion.getW());
	}

	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		quaternion.set(in.readFloat(),
					   in.readFloat(),
					   in.readFloat(),
					   in.readFloat());
	}

}
