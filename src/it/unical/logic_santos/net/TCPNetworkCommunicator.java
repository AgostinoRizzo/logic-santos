/**
 * 
 */
package it.unical.logic_santos.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import it.unical.logic_santos.gui.application.LogicSantosApplication;

/**
 * @author Agostino
 *
 */
public abstract class TCPNetworkCommunicator extends Thread implements IRemoteCommunicator {

	protected DataInputStream in=null;
	protected DataOutputStream out=null;
	
	protected Queue< PlayerAction > playerActionsQueue=null;
	protected Queue< WorldCookie > worldCookiesQueue=null;
	protected Queue< PlayerCookie > playerCookiesQueue=null;
	protected Queue< Vector3fCookie[] > playerDirectionsQueue=null;
	protected LogicSantosApplication application=null;
	private   float timeWorldCookieSending=0.0f;
	private   long  timePlayerDirectionCookieSending=0;
	
	protected Lock lock = new ReentrantLock();
	protected boolean runningFlag=false;
	
	protected static final byte PLAYER_ACTION_ID    = 0;
	protected static final byte WORLD_COOKIE_ID     = 1;
	protected static final byte PLAYER_COOKIE_ID    = 2;
	protected static final byte PLAYER_DIRECTION_ID = 3;
	
	private static final float TIME_BETWEEN_WORLD_COOKIE_SENDINGS            = 0.1f; // expressed in seconds
	private static final float TIME_BETWEEN_PLAYER_DIRECTION_COOKIE_SENDINGS = 0.1f; // expressed in seconds
	
	public TCPNetworkCommunicator( LogicSantosApplication application ) {
		this.application = application;
		this.playerActionsQueue = new LinkedList< PlayerAction >();
		this.worldCookiesQueue = new LinkedList< WorldCookie >();
		this.playerCookiesQueue = new LinkedList< PlayerCookie >();
		this.playerDirectionsQueue = new LinkedList< Vector3fCookie[] >();
		this.timeWorldCookieSending = 0.0f;
	}
	
	@Override
	public void onWorldUpdate( final float tpf ) {
		timeWorldCookieSending+=tpf;
		if ( timeWorldCookieSending<TIME_BETWEEN_WORLD_COOKIE_SENDINGS )
			return;
		timeWorldCookieSending=0.0f;
		
		final WorldCookie worldCookie = application.getCookie();
		try {
			
			/* print the data type identifier */
			out.writeByte( WORLD_COOKIE_ID );
			
			/* print the data */
			worldCookie.writeToDataOutputStream( out );
			
			out.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void onPlayerAction( final PlayerAction action ) {
		if ( action.isNull() )
			return;
		
		try {
			
			/* print the data type identifier */
			out.writeByte( PLAYER_ACTION_ID );
			
			/* print the data */
			action.writeToDataOutputStream( out );
			
			out.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPlayerUpdate(PlayerCookie cookie) {
		
		try {
			
			/* print the data type identifier */
			out.writeByte( PLAYER_COOKIE_ID );
			
			/* print the data */
			cookie.writeToDataOutputStream( out );
			
			out.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPlayerDirectionUpdate(Vector3fCookie viewDirectionCookie, Vector3fCookie walkDirectionCookie) {
		
		try {
			
			final long currentTime = System.currentTimeMillis();
			if ( ( ((float) (currentTime-timePlayerDirectionCookieSending))*1000.0f ) >= 
					TIME_BETWEEN_PLAYER_DIRECTION_COOKIE_SENDINGS ) {
			
				/* print the data type identifier */
				out.writeByte( PLAYER_DIRECTION_ID );
				
				/* print the data */
				viewDirectionCookie.writeToDataOutputStream( out );
				walkDirectionCookie.writeToDataOutputStream( out );
				
				out.flush();
				
				timePlayerDirectionCookieSending=currentTime;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		byte dataTypeId;
		while( runningFlag ) {
			try {
				
				/* read the data type identifier */
				dataTypeId = in.readByte();
				
				if ( dataTypeId == PLAYER_ACTION_ID ) {
					
					/* read the Player Action */
					PlayerAction playerAction = PlayerAction.readFromDataInputStream( in );
					
					/* add the Player Action to the Queue */
					pushBackPlayerAction( playerAction );
					
				} else if ( dataTypeId == WORLD_COOKIE_ID ) {
					
					/* read the World Cookie */
					WorldCookie worldCookie = new WorldCookie();
					worldCookie.readFromDataInputStream( in );
					
					/* add the World Cookie to the Queue */
					pushBackWorldCookie( worldCookie );
					
				} else if ( dataTypeId == PLAYER_COOKIE_ID ) {
					
					/* read the Player Cookie */
					PlayerCookie playerCookie = new PlayerCookie();
					playerCookie.readFromDataInputStream( in );
					
					/* add the Player Cookie to the Queue */
					pushBackPlyerCookie( playerCookie );
					
				} else if ( dataTypeId == PLAYER_DIRECTION_ID ) {
					
					/* read the Player Direction Cookie */
					Vector3fCookie viewDirectionCookie = new Vector3fCookie();
					Vector3fCookie walkDirectionCookie = new Vector3fCookie();
					viewDirectionCookie.readFromDataInputStream( in );
					walkDirectionCookie.readFromDataInputStream( in );
					
					/* add the Player Direction Cookie to the Queue */
					pushBackPlyerDirectionCookie( viewDirectionCookie, walkDirectionCookie );
					
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
				onReadStreamException();
			}
		}
	}
	
	@Override
	public PlayerAction getNextPlayerAction() {
		lock.lock();
		try {
			if ( playerActionsQueue.isEmpty() )
				return null;
			return playerActionsQueue.remove();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public boolean playerActionsAvailable() {
		lock.lock();
		try {
			return ( !playerActionsQueue.isEmpty() );
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public WorldCookie getNextWorldCookie() {
		lock.lock();
		try {
			if ( worldCookiesQueue.isEmpty() )
				return null;
			return worldCookiesQueue.remove();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public boolean worldCookiesAvailable() {
		lock.lock();
		try {
			return ( !worldCookiesQueue.isEmpty() );
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public PlayerCookie getNextPlayerCookie() {
		lock.lock();
		try {
			if ( playerCookiesQueue.isEmpty() )
				return null;
			return playerCookiesQueue.remove();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public boolean playerCookiesAvailable() {
		lock.lock();
		try {
			return ( !playerCookiesQueue.isEmpty() );
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public Vector3fCookie[] getNextPlayerDirectionCookie() {
		lock.lock();
		try {
			if ( playerDirectionsQueue.isEmpty() )
				return null;
			return playerDirectionsQueue.remove();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public boolean playerDirectionCookiesAvailable() {
		lock.lock();
		try {
			return ( !playerDirectionsQueue.isEmpty() );
		} finally {
			lock.unlock();
		}
	}
	
	protected abstract void onReadStreamException();
	
	private void pushBackPlayerAction( final PlayerAction playerAction ) {
		lock.lock();
		playerActionsQueue.add( playerAction );
		lock.unlock();
	}
	
	private void pushBackWorldCookie( final WorldCookie worldCookie ) {
		lock.lock();
		worldCookiesQueue.add( worldCookie );
		lock.unlock();
	}
	
	private void pushBackPlyerCookie( final PlayerCookie playerCookie ) {
		lock.lock();
		playerCookiesQueue.add( playerCookie );
		lock.unlock();
	}
	
	private void pushBackPlyerDirectionCookie( final Vector3fCookie viewDirectionCookie, final Vector3fCookie walkDirectionCookie ) {
		lock.lock();
		Vector3fCookie[] cookies = { viewDirectionCookie, walkDirectionCookie };
		playerDirectionsQueue.add( cookies );
		lock.unlock();
	}

}
