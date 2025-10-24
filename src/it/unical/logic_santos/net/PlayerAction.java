/**
 * 
 */
package it.unical.logic_santos.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Agostino
 *
 */
public class PlayerAction {

	public static final byte PLAYER_LEFT     = 0;
	public static final byte PLAYER_RIGHT    = 1;
	public static final byte PLAYER_FORWARD  = 2;
	public static final byte PLAYER_BACKWARD = 3;
	public static final byte PLAYER_RUN      = 4;
	public static final byte PLAYER_JUMP     = 5;
	public static final byte PLAYER_SHOOT    = 6;
	public static final byte PLAYER_CHOOSE_WEAPON = 7;
	public static final byte NULL_ACTION     = 8;
	
	private byte action;
	private boolean starting;
	
	public Vector3fCookie bulletStartPosition=new Vector3fCookie();
	public Vector3fCookie bulletDirection=new Vector3fCookie();
	
	public byte weaponType;
	
	public static PlayerAction readFromDataInputStream( DataInputStream in ) throws IOException {
		final byte action = in.readByte();
		final boolean starting = in.readBoolean();
		PlayerAction playerAction = new PlayerAction( action, starting );
		if ( action==PLAYER_SHOOT ) {
			playerAction.bulletStartPosition.readFromDataInputStream(in);
			playerAction.bulletDirection.readFromDataInputStream(in);
		} else if ( action==PLAYER_CHOOSE_WEAPON )
			playerAction.weaponType = in.readByte();
		
		return playerAction;
	}
	
	public PlayerAction( final byte action ) {
		this.action = action;
		this.starting = true;
	}
	
	public PlayerAction( final byte action, final boolean starting ) {
		this.action = action;
		this.starting = starting;
	}
	
	public int getAction() {
		return action;
	}
	
	public void setAction( byte action ) {
		this.action = action;
	}
	
	public boolean isStarting() {
		return starting;
	}
	
	public void setStarting(boolean starting) {
		this.starting = starting;
	}
	
	public boolean isNull() {
		return ( action==NULL_ACTION );
	}
	
	public void writeToDataOutputStream( DataOutputStream out ) throws IOException {
		out.writeByte( action );
		out.writeBoolean( starting );
		if ( action==PLAYER_SHOOT ) {
			bulletStartPosition.writeToDataOutputStream(out);
			bulletDirection.writeToDataOutputStream(out);
		} else if ( action==PLAYER_CHOOSE_WEAPON )
			out.writeByte(weaponType);
	}
}
