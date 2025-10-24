/**
 * 
 */
package it.unical.logic_santos.physics.activity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unical.logic_santos.gameplay.LifeBarCookie;
import it.unical.logic_santos.net.AnimationCookie;
import it.unical.logic_santos.net.ICookie;
import it.unical.logic_santos.net.Vector3fCookie;

/**
 * @author Agostino
 *
 */
public class WalkerCookie implements ICookie {

	public int id;
	
	public Vector3fCookie positionCookie=new Vector3fCookie();
	public Vector3fCookie viewDirectionCookie=new Vector3fCookie();
	public Vector3fCookie walkDirectionCookie=new Vector3fCookie();
	public LifeBarCookie lifeBarCookie=new LifeBarCookie();
	public AnimationCookie animationCookie=new AnimationCookie();
	
	public int startNodeId;
	public int targetNodeId;
	
	public float currentTimeForRunningOnShoot;
	public float currentRunningScale;
	public boolean runningOnShoot;
	
	public float idleTime;
	public float hitReactionTime;
	
	public int followedVehicleId=-1;
	
	public List< Integer > lastTargetNodesIds=new ArrayList< Integer >();
	
	private boolean isActive=true;
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		out.writeInt(id);
		
		out.writeBoolean(isActive);
		if ( !isActive )
			return;
		
		positionCookie.writeToDataOutputStream(out);
		viewDirectionCookie.writeToDataOutputStream(out);
		walkDirectionCookie.writeToDataOutputStream(out);
		lifeBarCookie.writeToDataOutputStream(out);
		animationCookie.writeToDataOutputStream(out);
		
		out.writeInt(startNodeId);
		out.writeInt(targetNodeId);
		
		out.writeFloat(currentTimeForRunningOnShoot);
		out.writeFloat(currentRunningScale);
		out.writeBoolean(runningOnShoot);
		
		out.writeFloat(idleTime);
		out.writeFloat(hitReactionTime);
		
		out.writeInt(followedVehicleId);
		
		final int sizeLastTargetNodesId = lastTargetNodesIds.size();
		out.writeInt(sizeLastTargetNodesId);
		for( int i=0; i<sizeLastTargetNodesId; ++i )
			out.writeInt(lastTargetNodesIds.get(i).intValue());
	}

	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		id = in.readInt();
		
		isActive = in.readBoolean();
		if ( !isActive )
			return;
		
		positionCookie.readFromDataInputStream(in);
		viewDirectionCookie.readFromDataInputStream(in);
		walkDirectionCookie.readFromDataInputStream(in);
		lifeBarCookie.readFromDataInputStream(in);
		animationCookie.readFromDataInputStream(in);
		
		startNodeId = in.readInt();
		targetNodeId = in.readInt();
		
		currentTimeForRunningOnShoot = in.readFloat();
		currentRunningScale = in.readFloat();
		runningOnShoot = in.readBoolean();
		
		idleTime = in.readFloat();
		hitReactionTime = in.readFloat();
		
		followedVehicleId = in.readInt();
		
		lastTargetNodesIds.clear();
		final int sizeLastTargetNodesId = in.readInt();
		for( int i=0; i<sizeLastTargetNodesId; ++i )
			lastTargetNodesIds.add(new Integer(in.readInt()));
		
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
