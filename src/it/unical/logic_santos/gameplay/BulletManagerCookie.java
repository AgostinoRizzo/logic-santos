/**
 * 
 */
package it.unical.logic_santos.gameplay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unical.logic_santos.net.ICookie;

/**
 * @author Agostino
 *
 */
public class BulletManagerCookie implements ICookie {

	protected List< BulletCookie > bulletsCookies = new ArrayList< BulletCookie >();
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		final int bulletsCookiesSize = bulletsCookies.size();
		out.writeInt(bulletsCookiesSize);
		for( int i=0; i<bulletsCookiesSize; ++i )
			bulletsCookies.get(i).writeToDataOutputStream(out);
	}

	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		bulletsCookies.clear();
		final int bulletsCookiesSize = in.readInt();
		for( int i=0; i<bulletsCookiesSize; ++i ) {
			
			BulletCookie bulletCookie = new BulletCookie();
			bulletCookie.readFromDataInputStream(in);
			bulletsCookies.add(bulletCookie);
		}
	}

}
