/**
 * 
 */
package it.unical.logic_santos.gameplay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import it.unical.logic_santos.net.ICookie;
import it.unical.logic_santos.net.Vector3fCookie;

/**
 * @author Agostino
 *
 */
public class BulletCookie implements ICookie {

	public static final byte GUN_COOKIE_ID    = 0;
	public static final byte RIFLE_COOKIE_ID  = 1;
	public static final byte CANNON_COOKIE_ID = 2;
	
	public static final byte MASTER_PLAYER_TYPE_OWNER_ID = 0;
	public static final byte CLIENT_PLAYER_TYPE_OWNER_ID = 1;
	public static final byte POLICEMAN_TYPE_OWNER_ID     = 2;
	
	protected Vector3fCookie startPositionCookie=new Vector3fCookie();
	protected Vector3fCookie directionCookie=new Vector3fCookie();
	protected Vector3fCookie currentPosition=new Vector3fCookie();
	
	protected byte ownerTypeId; 
	protected int ownerId;
	
	protected byte type;
	
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		startPositionCookie.writeToDataOutputStream(out);
		directionCookie.writeToDataOutputStream(out);
		currentPosition.writeToDataOutputStream(out);
		
		out.writeByte(ownerTypeId);
		out.writeInt(ownerId);
		
		out.writeByte(type);
	}

	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		startPositionCookie.readFromDataInputStream(in);
		directionCookie.readFromDataInputStream(in);
		currentPosition.readFromDataInputStream(in);
		
		ownerTypeId = in.readByte();
		ownerId = in.readInt();
		
		type = in.readByte();
	}

}
