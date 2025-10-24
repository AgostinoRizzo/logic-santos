/**
 * 
 */
package it.unical.logic_santos.net;

/**
 * @author Agostino
 *
 */
public interface IRemoteCommunicator {

	public boolean isConnected();
	public boolean isMaster();
	
	public void onWorldUpdate( final float tpf );
	public void onPlayerAction( final PlayerAction action );
	public void onPlayerUpdate( final PlayerCookie cookie );
	public void onPlayerDirectionUpdate( final Vector3fCookie viewDirectionCookie, final Vector3fCookie walkDirectionCookie );
	
	public PlayerAction getNextPlayerAction();
	public boolean playerActionsAvailable();
	
	public WorldCookie getNextWorldCookie();
	public boolean worldCookiesAvailable();
	
	public PlayerCookie getNextPlayerCookie();
	public boolean playerCookiesAvailable();
	
	public Vector3fCookie[] getNextPlayerDirectionCookie();
	public boolean playerDirectionCookiesAvailable();
}
