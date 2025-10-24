/**
 * 
 */
package it.unical.logic_santos.ai;

import com.jme3.math.Vector3f;

import it.unical.logic_santos.spatial_entity.Player;

/**
 * @author Agostino
 *
 */
public class PlayerFollowingData {

	private Player   player=null;
	private Vector3f direction=null;
	private float    distance=0.0f;
	
	public PlayerFollowingData() {}
	
	public PlayerFollowingData( Player player, Vector3f direction, float distance ) {
		this.player = player;
		this.direction = direction;
		this.distance = distance;
	}
	
	public boolean isNull() {
		return ( (player==null) || (direction==null) || (distance<0.0f) );
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Vector3f getDirection() {
		return direction;
	}
	
	public float getDistance() {
		return distance;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}
	
	public void setDistance(float distance) {
		this.distance = distance;
	}
	
}
