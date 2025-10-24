/**
 * 
 */
package it.unical.logic_santos.io.universe;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Agostino
 *
 */
public class ClassEncoder {

	private String encoding=null;
	
	public ClassEncoder(final String className) {
		this.encoding = new String(className);
		this.encoding.trim();
	}
	
	public int getClassNameEncodingLength() {
		return encoding.length();
	}
	
	public String getClassName() {
		return new String(encoding);
	}
	
	public void writeOnDataOutputStream(DataOutputStream out) throws IOException {
		out.writeInt(encoding.length());
		out.writeChars(encoding);
	}
	
	public static String encodeClass(DataInputStream in) throws IOException {
		final int length = in.readInt();
		String className = new String();
		for(int i=0; i<length; ++i)
			className += in.readChar();
		return className;
	}
	
	public static ClassEncoder getNewClassEncoder(DataInputStream in) throws IOException {
		final int length = in.readInt();
		String className = new String();
		for(int i=0; i<length; ++i)
			className += in.readChar();
		ClassEncoder classEncoder = new ClassEncoder(className);
		return classEncoder;
	}
	
}
