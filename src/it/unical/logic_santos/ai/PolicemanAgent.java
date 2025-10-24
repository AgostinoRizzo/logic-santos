/**
 * 
 */
package it.unical.logic_santos.ai;

import java.util.List;
import java.util.Random;

import com.jme3.math.Vector3f;

import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.physics.activity.HumanAnimation;
import it.unical.logic_santos.physics.activity.PolicemanPhysicalActivity;
import it.unical.logic_santos.roads_network.RoadNode;
import it.unical.logic_santos.spatial_entity.Player;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class PolicemanAgent implements IAgent {
	
	private PolicemanPhysicalActivity policemanPhysicalActivity=null;
	private PlayerFinderReasoning playerFinderReasoning=null;
	private Random random=new Random( System.currentTimeMillis() );
	private LogicSantosApplication application=null;
	
	private float reasoningTimeAmount=0.0f;
	private boolean isInPlayerFinderMode=false;
	
	private static final float   MAX_DISTANCE_FOR_PLAYER_VISIBILITY  = 100.0f;
	private static final float   MIN_FOLLOWING_DISTANCE              =  30.0f;
	private static final float   MAX_STOP_DISTANCE                   =  50.0f;
	private static final float   STOP_DISTANCE_RANGE                 = MAX_STOP_DISTANCE-MIN_FOLLOWING_DISTANCE;
	private static final float   TIME_BETWEEN_REASONINGS             = 1.0f; // expressed in seconds
	private static final float   MAX_TIME_BETWEEN_SHOOTS             = 3.0f; // expressed in seconds
	private static final float   MIN_TIME_BETWEEN_SHOOTS             = 1.0f; // expressed in seconds
	private static final float   TIME_SHOOT_RANGE                    = MAX_TIME_BETWEEN_SHOOTS-MIN_TIME_BETWEEN_SHOOTS; // expressed in seconds
	private static final boolean USE_OPTIMIZED_PATH_FINDER_REASONING = true;
	private static final boolean USE_GREEDY_PATH_FINDER_REASONING    = false;

	public PolicemanAgent( PolicemanPhysicalActivity policemanPhysicalActivity ) {
		this.policemanPhysicalActivity = policemanPhysicalActivity;
	}
	
	public PolicemanAgent( PolicemanPhysicalActivity policemanPhysicalActivity, LogicSantosApplication application ) {
		this.policemanPhysicalActivity = policemanPhysicalActivity;
		this.application = application;
	}
	
	public void setApplication(LogicSantosApplication application) {
		this.application = application;
	}
	
	@Override
	public void think() {
		think( 0.0f );		
	}

	@Override
	public void think(float tpf) {
		if ( reasoningTimeAmount<TIME_BETWEEN_REASONINGS )
			reasoningTimeAmount+= tpf;
		
		
		
		final PlayerFollowingData playerFollowingData = 
				computeWalkingDirectionToNearestVisiblePlayer( policemanPhysicalActivity.getControl().getPhysicsLocation() );
		if ( !playerFollowingData.isNull() ) {
			
			if ( !policemanPhysicalActivity.hasMaxRandomDistanceToStop() ) {
				final float maxDistanceToStop = computeRandomMaxDistanceToStop( random );
				policemanPhysicalActivity.setMaxRandomDistanceToStop( maxDistanceToStop );
			}
			
			if ( !policemanPhysicalActivity.hasRandomTimeNextShoot() ) {
				final float randomTimeNextShoot = computeRandomTimeForNextShoot( this.random );
				policemanPhysicalActivity.setRandomTimeNextShoot( randomTimeNextShoot );
			}
			policemanPhysicalActivity.followPlayer( playerFollowingData, tpf );
			isInPlayerFinderMode = false;
			return;
			
		} else if ( policemanPhysicalActivity.hasTargetNode() ) {
			followTargetNode( tpf );
			return;
		} else if ( !policemanPhysicalActivity.hasStartNode() ) {
			RoadNode newTargetNode = policemanPhysicalActivity.findNearestTargetNodeToPosition( policemanPhysicalActivity.getControl().getPhysicsLocation() );
			if ( newTargetNode!=null )
				policemanPhysicalActivity.setTargetNode( newTargetNode );
			else
				policemanPhysicalActivity.clearTergetNode();
			return;
		}
		
		/* apply player finder reasoning */
		if ( (!USE_GREEDY_PATH_FINDER_REASONING) && policemanPhysicalActivity.hasTargetNode() && (!policemanPhysicalActivity.targetNodeReached()) )
			return;
		
		if ( (playerFinderReasoning==null) /*&& (reasoningTimeAmount>=TIME_BETWEEN_REASONINGS)*/ ) { System.out.println("START REASONING!!!");
			playerFinderReasoning = createNewPlayerFinderReasoning( this );
			playerFinderReasoning.execute();
			reasoningTimeAmount=0.0f;
			isInPlayerFinderMode = true;
		} else if ( (playerFinderReasoning!=null) && (playerFinderReasoning.poll()) ) {
			System.out.println("SOLUTION AVAILABLE!!!");
			List< RoadNode > path = playerFinderReasoning.getResult();
			if ( (path!=null) && (!(path.size()<=1)) ) {
				RoadNode targetNode = path.get( 1 );
				policemanPhysicalActivity.setTargetNode( targetNode );
				System.out.println("SOLUTION: " + targetNode.getId());
			}
			isInPlayerFinderMode = true;
			playerFinderReasoning = null;
		}
		
	}
	
	public PolicemanPhysicalActivity getPolicemanPhysicalActivity() {
		return policemanPhysicalActivity;
	}
	
	public PlayerFollowingData computeWalkingDirectionToNearestVisiblePlayer( final Vector3f subjectPosition ) {
		List< Player > players = application.getPlayers();
		if ( players.isEmpty() )
			return null;
		
		Vector3f walkingDirection=null;
		Player nearestPlayer=null;
		float nearestDistance=Float.MAX_VALUE;
		float distance;
		boolean canSeePlayer;
		for( Player p: players ) {
			
			distance = subjectPosition.distance( p.getHumanPhysicalActivity().getControl().getPhysicsLocation() );
			canSeePlayer = canSeePlayer(p, distance);
			if ( ( (nearestPlayer==null) && canSeePlayer) || 
					( (nearestPlayer!=null) && canSeePlayer &&(distance<nearestDistance) ) ) {
				nearestPlayer = p;
				nearestDistance = distance;
			}
		}
		
		if ( nearestPlayer!=null ) {
			walkingDirection = Vector3D.calculateDirection( 
					new Vector3D(subjectPosition),
					new Vector3D(nearestPlayer.getHumanPhysicalActivity().getControl().getPhysicsLocation()) ).toVector3f();
			walkingDirection.normalizeLocal();
		}
		
		return (new PlayerFollowingData( nearestPlayer, walkingDirection, nearestDistance ));
	}
	
	public boolean isInPlayerFinderMode() {
		return (this.isInPlayerFinderMode);
	}
	
	private boolean canSeePlayer( final Player player, final float distance ) {
		return ( distance<=MAX_DISTANCE_FOR_PLAYER_VISIBILITY );
	}
	
	private void followTargetNode( final float tpf ) {
		if ( policemanPhysicalActivity.targetNodeReached() ) {
			policemanPhysicalActivity.setStartPathsNetworkNode( policemanPhysicalActivity.getTargetNode() );
			policemanPhysicalActivity.clearTergetNode();
			policemanPhysicalActivity.getAnimation().setAnimation( HumanAnimation.IDLE_AIMING );
			policemanPhysicalActivity.walk(Vector3f.ZERO, tpf, false);
			
		} else {
		
			final Vector3f walkingPosition = policemanPhysicalActivity.getControl().getPhysicsLocation();
			final Vector3f targetPosition  = policemanPhysicalActivity.getTargetNode().getPosition().toVector3f();
			final Vector3f walkingDirection = Vector3D.calculateDirection(new Vector3D(walkingPosition), 
																			new Vector3D(targetPosition)).toVector3f().normalize();
			policemanPhysicalActivity.walk(walkingDirection.mult(0.1f), tpf, true);
			policemanPhysicalActivity.run();
		}
	}
	
	private static float computeRandomTimeForNextShoot( Random random ) {
		return ( (random.nextFloat()*TIME_SHOOT_RANGE)+MIN_TIME_BETWEEN_SHOOTS );
	}
	
	private static float computeRandomMaxDistanceToStop( Random random ) {
		return ((random.nextFloat()*STOP_DISTANCE_RANGE)+MIN_FOLLOWING_DISTANCE);
	}
	
	private static PlayerFinderReasoning createNewPlayerFinderReasoning( PolicemanAgent agent ) {
		if ( PolicemanAgent.USE_OPTIMIZED_PATH_FINDER_REASONING )
			return new OptimizedPlayerFinderReasoning( agent );
		return new PlayerFinderReasoning( agent );
	}
	
}
