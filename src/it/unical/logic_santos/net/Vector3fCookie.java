/**
 * 
 */
package it.unical.logic_santos.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.jme3.math.Vector3f;

/**
 * @author Agostino
 *
 */
public class Vector3fCookie implements ICookie {

	public Vector3f vector = new Vector3f();
	
	
	public Vector3fCookie() {}
	
	
	public Vector3fCookie( final Vector3f vector ) {
		this.vector.setX( vector.getX() );
		this.vector.setY( vector.getY() );
		this.vector.setZ( vector.getZ() );
	}
	
	public void set( final Vector3f vector ) {
		this.vector.setX( vector.getX() );
		this.vector.setY( vector.getY() );
		this.vector.setZ( vector.getZ() );
	}
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		out.writeFloat(vector.getX());
		out.writeFloat(vector.getY());
		out.writeFloat(vector.getZ());
	}

	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		vector.setX(in.readFloat());
		vector.setY(in.readFloat());
		vector.setZ(in.readFloat());
	}

}
