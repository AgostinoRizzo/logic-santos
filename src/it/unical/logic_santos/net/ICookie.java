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
public interface ICookie {

	public void writeToDataOutputStream( DataOutputStream out ) throws IOException;
	public void readFromDataInputStream( DataInputStream in ) throws IOException;
	
	public static void writeVector3fToDataOutputStream( final Vector3f vector, DataOutputStream out ) throws IOException {
		out.writeFloat( vector.getX() );
		out.writeFloat( vector.getY() );
		out.writeFloat( vector.getZ() );
	}
	
	public static void readVector3fFromDataInputStream( Vector3f vector, DataInputStream in ) throws IOException {
		vector.setX( in.readFloat() );
		vector.setY( in.readFloat() );
		vector.setZ( in.readFloat() );
	}
}
