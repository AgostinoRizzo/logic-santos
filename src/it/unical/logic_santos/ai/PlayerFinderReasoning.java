/**
 * 
 */
package it.unical.logic_santos.ai;

import java.util.List;
import java.util.Stack;

import it.unical.logic_santos.physics.activity.PolicemanPhysicalActivity;
import it.unical.logic_santos.roads_network.PathsNetwork;
import it.unical.logic_santos.roads_network.RoadNode;
import it.unical.logic_santos.toolkit.data_structure.Arc;

/**
 * @author Agostino
 *
 */
public class PlayerFinderReasoning extends ThreadBasedReasoning {

	protected PolicemanAgent agent=null;
	protected PathsNetwork pathsNetwork=null;
	protected List< RoadNode > path=null;
	
	protected static final int DEEP_PATH_SEARCH = 30;
	
	public PlayerFinderReasoning( PolicemanAgent agent ) {
		this.agent = agent;
		this.pathsNetwork = agent.getPolicemanPhysicalActivity().getPathsNetwork();
	}
	
	@Override
	public void reasoning() {
		PolicemanPhysicalActivity policemanPhysicalActivity = agent.getPolicemanPhysicalActivity();
		if ( !policemanPhysicalActivity.hasStartNode() )
			return;
		
		Stack< RoadNode > currentPath = new Stack< RoadNode >();
		currentPath.push( policemanPhysicalActivity.getStartNode() );
		boolean[] visitedNodes = new boolean[ pathsNetwork.nodesCount() ];
		for( int i=0; i<visitedNodes.length; ++i )
			visitedNodes[i]=false;
		visitedNodes[ currentPath.peek().getId()-1 ] = true;
		if ( this.findPath( currentPath, visitedNodes, DEEP_PATH_SEARCH ) ) {
			path = currentPath; System.out.println("SOLUTION COMPUTED!!!");
		} else
			path = null;
	}
	
	/** returns the solution computed by the Finder
	 *  this method is blocking ( until the solution is available )
	 * @return the solution computed by the Finder
	 */
	public List< RoadNode > getResult() {
		lock.lock();
		try {
			while( !isAvailable )
				solutionAvailable.await();
			return path;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} finally {
			lock.unlock();
		}
	}
	
	/** finds the path starting from startNode to node N such that:
	 * 		- N is a node a Player P is visible from
	 *  this method apply the backtracking technique
	 * @param startNode: the start node of the path
	 * @return the path starting from startNode which respect the property
	 */
	protected boolean findPath( Stack< RoadNode > currentPath, boolean[] visitedNodes, final int max_deep ) {
		if ( currentPath.size()>=max_deep )
			return false;
		
		RoadNode currentNode = currentPath.peek();
		for( Arc e=pathsNetwork.firstAdjacent(currentNode.getId()); (!e.isNull()); e=pathsNetwork.nextAdjacent(e) ) {
			
			RoadNode nextNode = pathsNetwork.getNode( e.fin() );
			if ( canAdd( nextNode, currentPath, visitedNodes, max_deep ) ) {
				currentPath.push( nextNode );
				visitedNodes[ nextNode.getId()-1 ] = true;
				if ( isComplete( currentPath ) )
					return true;
				else if ( findPath(currentPath, visitedNodes, max_deep ) )
					return true;
				currentPath.pop();
				visitedNodes[ nextNode.getId()-1 ] = false;
			}
		}
		return false;
	}
	
	protected boolean canAdd( final RoadNode nextNode, final Stack< RoadNode > currentPath, final boolean[] visitedNodes, final int max_deep ) {
		if ( (currentPath.size()+1)>=max_deep )
			return false;
		return (!visitedNodes[ nextNode.getId()-1 ]);
	}
	
	protected boolean isComplete( final Stack< RoadNode > currentPath ) {
		if ( currentPath.empty() )
			return false;
		return checkVisibility( currentPath.peek() );
	}
	
	protected boolean checkVisibility( RoadNode node ) {
		return ( !agent.computeWalkingDirectionToNearestVisiblePlayer( node.getPosition().toVector3f() ).isNull() );
	}

}
