/**
 * 
 */
package it.unical.logic_santos.traffic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import com.jme3.math.Vector3f;

import it.unical.logic_santos.collision.ICollidable;
import it.unical.logic_santos.collision.ICollisionDetectionEngine;
import it.unical.logic_santos.collision.ICollisionResults;
import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.environment.sound.HumanSoundManager;
import it.unical.logic_santos.gameplay.IObserver;
import it.unical.logic_santos.gameplay.ISubject;
import it.unical.logic_santos.gameplay.IdentifierGenerator;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.net.IRemoteCommunicator;
import it.unical.logic_santos.physics.activity.HumanAnimation;
import it.unical.logic_santos.physics.activity.HumanPhysicalActivity;
import it.unical.logic_santos.physics.activity.PolicemanAnimation;
import it.unical.logic_santos.physics.activity.PolicemanCookie;
import it.unical.logic_santos.physics.activity.PolicemanPhysicalActivity;
import it.unical.logic_santos.physics.activity.WalkerPhysicalActivity;
import it.unical.logic_santos.roads_network.PathsNetwork;
import it.unical.logic_santos.roads_network.RoadNode;
import it.unical.logic_santos.spatial_entity.AbstractHuman;
import it.unical.logic_santos.spatial_entity.Player;
import it.unical.logic_santos.spatial_entity.Policeman;
import it.unical.logic_santos.spatial_entity.Walker;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class PolicemanTrafficManager implements IObserver, ISubject {

	private List<AbstractHuman> activePolicemans=null;
	private List<AbstractHuman> idlePolicemans=null;
	private PathsNetwork pathsNetwork=null;
	private ICollisionDetectionEngine collisionEngine=null;
	private LogicSantosApplication application=null;
	private Random rn = new Random(System.currentTimeMillis());
	
	private float trafficIntensity=1.0f;
	
	private float updateSleepTime=0.0f;
	
	private List< IObserver > observers=null;
	
	private static final float MIN_DISTANCE_POLICEMAN_ASSIGNMENT = 100.0f;
	private static final float MIN_TIME_FOR_UPDATE = 0.0f; //1.3f;
	private static final int   MAX_NUMBER_POLICEMANS = 5;
	private static final float MAX_NEAR_PLAYER_DISTANCE = 300.0f;
	private static final float PADDING_NEAR_PLAYER_DISTANCE = 100.0f;
	private static final float MARGIN_NEAR_PLAYER_DISTANCE  = 5.0f;
	
	private static final float Y_OFFSET_INIT = 3.8f + 0.2f;
	
	public PolicemanTrafficManager(PathsNetwork pathsNetwork, ICollisionDetectionEngine collisionEngine, LogicSantosApplication application) {
		this.activePolicemans = new LinkedList<AbstractHuman>();
		this.idlePolicemans = new LinkedList<AbstractHuman>();
		this.pathsNetwork = pathsNetwork;
		this.collisionEngine = collisionEngine;
		this.application = application;
		this.observers = new ArrayList< IObserver >();
	}
	
	public void initComponents() {
		removePolicemans();
		
		final int numPolicemans = (int) (MAX_NUMBER_POLICEMANS * trafficIntensity);
		for(int i=0; (i<numPolicemans); ++i) {
			
			Policeman newPoliceman=null;
			try {
				newPoliceman = getRandomPolicemanClass().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				newPoliceman=null;
				e.printStackTrace();
			}
			
			newPoliceman.getPolicemanPhysicalActivity().setObserver( this );
			newPoliceman.getPolicemanPhysicalActivity().getPolicemanAgent().setApplication( application );
			newPoliceman.getPolicemanPhysicalActivity().
				setId( IdentifierGenerator.getInstance().getNextIdentifier() );
			initPolicemanComponent( newPoliceman );
			newPoliceman.setApplication( application );
			idlePolicemans.add( newPoliceman );
		}
	}
	
	public void finalizeComponents() {
		for(AbstractHuman p: activePolicemans)
			finalizePolicemanComponent(p);
	}
	
	public float getTrafficIntensity() {
		return trafficIntensity;
	}
	
	public void setTrafficIntensity(float trafficIntensity) {
		if (trafficIntensity > 1.0f)
			trafficIntensity = 1.0f;
		else if (trafficIntensity < 0.0f)
			trafficIntensity = 0.0f;
		this.trafficIntensity = trafficIntensity;
	}
	
	public void update(final float tpf) {
		updateSleepTime+=tpf;
		if (updateSleepTime>=MIN_TIME_FOR_UPDATE) {
			
			List< Player > players=application.getPlayers();
			if ( players.isEmpty() )
				return;
			
			final int maxWantedStars = findMaxWantedStars( players );
			final int numIdlePoliceman = idlePolicemans.size();
			int numPolicemansToActivate = computeNeededPolicemans( maxWantedStars )-activePolicemans.size();
			boolean excessivePolicemans = false;
			
			if ( numPolicemansToActivate>0 ) {
				if ( numPolicemansToActivate>numIdlePoliceman )
					numPolicemansToActivate = numIdlePoliceman;
				
				if ( canUpdatePolicemans() )
					activateNeededPolicemans( numPolicemansToActivate );
			} else if ( numPolicemansToActivate<0 )
				excessivePolicemans = true;
			
			
			List< RoadNode > nearestNodeToPlayers = findNearestNodesToPlayers();
			
			boolean iterate=true;
			for (Iterator< AbstractHuman > it = activePolicemans.iterator(); ( iterate && it.hasNext() );) {
				AbstractHuman p = it.next();
				
				p.getPolicemanPhysicalActivity().update(updateSleepTime);
				if ( canUpdatePolicemans() )
					iterate = (!updatePolicemanDistanceFromPlayer( p, nearestNodeToPlayers, excessivePolicemans ));
			}
			
			updateSleepTime=0.0f;
		}
	}
	
	public List< AbstractHuman > getHumans() {
		return activePolicemans;
	}
	
	@Override
	public void onStateShanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStateShanged(ISubject subject) {
		if ( subject instanceof PolicemanPhysicalActivity ) {
			PolicemanPhysicalActivity policemanPhysicalActivity = (PolicemanPhysicalActivity) subject;
			AbstractHuman policeman = policemanPhysicalActivity.getAbstractHumanOwner();
			if ( activePolicemans.contains( policeman ) ) {
				
				if ( policemanPhysicalActivity.getLifeBar().isEmpty() ) {
					
					if ( !policemanPhysicalActivity.isHitReactionTimeCounting() ) {
						policemanPhysicalActivity.setIdle( true );
						policemanPhysicalActivity.setIsHitReactionTimeCounting( true );
						policemanPhysicalActivity.getAnimation().setAnimation( PolicemanAnimation.HIT_REACTION );
					}
					if ( !policemanPhysicalActivity.isHitReactionTimeDone() )
						return;
				}
				policemanPhysicalActivity.getAnimation().setAnimation( PolicemanAnimation.IDLE_AIMING );
				//finalizePolicemanComponent(policeman);
				notifyObservers();
			}
		}
	}
	
	public void addObserver( IObserver obs ) {
		if ( !observers.contains(obs) )
			observers.add(obs);
	}
	
	public void removeObserver( IObserver obs ) {
		observers.remove(obs);
	}
	
	public int getNumActivePolicemans() {
		return (activePolicemans.size());
	}
	
	public List< AbstractHuman > getActivePolicemans() {
		return activePolicemans;
	}
	
	public PolicemanTrafficManagerCookie getCookie() {
		PolicemanTrafficManagerCookie cookie = new PolicemanTrafficManagerCookie();
		
		setPolicemansCookiesFromPolicemans(this.activePolicemans, cookie.activePolicemansCookies);
		setPolicemansCookiesFromPolicemans(this.idlePolicemans, cookie.idlePolicemansCookies);
		
		cookie.trafficIntensity = this.trafficIntensity;
		cookie.updateSleepTime = this.updateSleepTime;
		
		return cookie;
	}
	
	public void setCookie( final PolicemanTrafficManagerCookie cookie ) {
		setActivePolicemansFromCookies(cookie.activePolicemansCookies );
		setIdlePolicemansFromCookies(cookie.idlePolicemansCookies );
		
		this.trafficIntensity = cookie.trafficIntensity;
		this.updateSleepTime = cookie.updateSleepTime;
	}
	
	public List< AbstractHuman > getAllPolicemans() {
		List< AbstractHuman > allPolicemans = new ArrayList< AbstractHuman >();
		allPolicemans.addAll( activePolicemans );
		allPolicemans.addAll( idlePolicemans );
		return allPolicemans;
	}
	
	public boolean isActivePoliceman( final AbstractHuman policeman  ) {
		return ( activePolicemans.contains( policeman ) );
	}
	
	public void clear() {
		while ( !activePolicemans.isEmpty() ) {
			AbstractHuman policeman = activePolicemans.get( 0 );
			HumanSoundManager.getInstance().removePolicemanSound( policeman );
			finalizePolicemanComponent(policeman);
			notifyObservers();
		}
		
		List< Player > players=application.getPlayers();
		for( Player player: players )
			player.getWantedStars().clear();
	}
	
	private void removePolicemans() {
		for(AbstractHuman p: activePolicemans) {
			removePoliceman(p);
		}
		activePolicemans.clear();
		idlePolicemans.clear();
	}
	
	private void addPoliceman(AbstractHuman p, final Vector3D position, final boolean addInCollisionEngine) {
		
		((HumanPhysicalActivity) p.getPhysicalActivity()).getControl().setPhysicsLocation(position.toVector3f());
		if (addInCollisionEngine)
			collisionEngine.addCollidable(p.getAbstractPhysicalExtension().getBoundingVolume());
		activePolicemans.add(p);
		idlePolicemans.remove(p);
		p.getHumanPhysicalActivity().getLifeBar().setFull();
		p.getHumanPhysicalActivity().getAnimation().setAnimation( PolicemanAnimation.IDLE_AIMING );
		p.getWalkerPhysicalActivity().clearHitReactionTime();
		application.getRootNode().attachChild(((ModelBasedPhysicalExtension) p.getAbstractPhysicalExtension()).getModelSpatial());
		application.getGameplayScreen().getMinimapScreen().addVisibleEntity( p );
	}
	
	private void removePoliceman(AbstractHuman p) {
		collisionEngine.removeCollidable(p.getAbstractPhysicalExtension().getBoundingVolume());
		activePolicemans.remove(p);
		idlePolicemans.add(p);
		p.getHumanPhysicalActivity().stopWalking();
		p.getHumanPhysicalActivity().getLifeBar().setFull();
		p.getHumanPhysicalActivity().getAnimation().setAnimation( PolicemanAnimation.IDLE_AIMING );
		p.getWalkerPhysicalActivity().clearHitReactionTime();
		p.getPolicemanPhysicalActivity().setIsHitReactionTimeCounting( false );
		application.getRootNode().detachChild(((ModelBasedPhysicalExtension) p.getAbstractPhysicalExtension()).getModelSpatial());
		application.getGameplayScreen().getMinimapScreen().removeVisibleEntity( p );
	}
	
	private void initPolicemanComponent( AbstractHuman policeman ) {
		policeman.getPhysicalActivity().setInPathsNetwork(pathsNetwork); policeman.getPolicemanPhysicalActivity().rootNode = application.getRootNode();
		policeman.getPolicemanPhysicalActivity().setWorld(application.getLogicSantosWorld());
		policeman.getPhysicalActivity().initActivity();
	}
	
	private void finalizePolicemanComponent( AbstractHuman policeman ) {
		policeman.getPhysicalActivity().finalizeActivity();
		removePoliceman(policeman);
	}
	
	private Class<? extends Policeman> getRandomPolicemanClass() {
		return Policeman.class;
	}
	
	private List< RoadNode > findNearestNodesToPlayers() {
		List< RoadNode > nearestNode = new LinkedList< RoadNode >();
		final List< Player > players = application.getPlayers();
		
		for( Player p: players ) {
			final Vector3f playerPosition = p.getHumanPhysicalActivity().getControl().getPhysicsLocation();
			
			/* find nearest nodes ... */
			final int n=pathsNetwork.nodesCount();
			RoadNode node;
			float distance;
			for( int i=1; i<=n; ++i) {
				
				node = pathsNetwork.getNode( i );
				distance = playerPosition.distance( node.getPosition().toVector3f() );
				
				if ( ( distance<=(MAX_NEAR_PLAYER_DISTANCE-MARGIN_NEAR_PLAYER_DISTANCE) ) &&
					 ( distance>=(PADDING_NEAR_PLAYER_DISTANCE) ) )
					nearestNode.add(node);
			}
		}
		return nearestNode;
	}
	
	private boolean updatePolicemanDistanceFromPlayer( AbstractHuman policeman, List< RoadNode > nearestNodesToPlayers, final boolean excessivePolicemans ) {
		if ( nearestNodesToPlayers.isEmpty() ) 
			return false;
		
		boolean modification = false;
		
		PolicemanPhysicalActivity policemanActivity = policeman.getPolicemanPhysicalActivity();
		final Vector3f playerPosition = application.getPlayerManager().getPlayerPosition();
		final Vector3f policemanPosition = policemanActivity.getControl().getPhysicsLocation();
		
		final float distance = playerPosition.distance(policemanPosition);
		if ( (distance>MAX_NEAR_PLAYER_DISTANCE) || (policemanActivity.getLifeBar().isEmpty()) ) {
			
			if ( excessivePolicemans || true ) {
				
				if ( policemanActivity.getLifeBar().isEmpty() ) {
					policemanActivity.stopWalking();
					if ( policemanActivity.isHitReactionTimeCounting() && 
							(!policemanActivity.isHitReactionTimeDone()) )
						return modification;
				}
				HumanSoundManager.getInstance().removePolicemanSound( policeman );
				finalizePolicemanComponent(policeman);
				notifyObservers();
				policemanActivity.clearHitReactionTime();
				policemanActivity.setIsHitReactionTimeCounting( false );
				modification = true;
				System.out.println("POSITION: " + policemanPosition);
			} else {
				
				/* assign new RoadNode to Policeman ... */
				final int k = rn.nextInt( nearestNodesToPlayers.size() );
				RoadNode node = nearestNodesToPlayers.get( k );
				final int nodeId = node.getId();
				Vector3D position = node.getPosition().clone();
				position.setY(position.getY() + Y_OFFSET_INIT);
				
				policemanActivity.getControl().setPhysicsLocation(position.toVector3f());
				((Policeman) policemanActivity.getSpatialEntityOwner()).setStartNodeId(nodeId);
				policemanActivity.setStartPathsNetworkNode(pathsNetwork.getNode(nodeId));
				policemanActivity.clearTergetNode();
				
				if ( policemanActivity.getLifeBar().isEmpty() )
					policemanActivity.getLifeBar().setFull();
				
				nearestNodesToPlayers.remove(k);
			
			}
		}
		return modification;
	}
	
	private void activateNeededPolicemans( final int numPolicemansToActivate ) {
		if ( idlePolicemans.isEmpty() )
			return;
		
		boolean addPolicemanFlag=true;
		int numberOfSteps=0;
		boolean continueAssignment=true;
		
		for( int i=0; i<numPolicemansToActivate; ++i ) {
			
			AbstractHuman policemanToActivate = idlePolicemans.get(0);
			initPolicemanComponent( policemanToActivate );
			
			ICollidable newPolicemanBoundingVolume = policemanToActivate.getAbstractPhysicalExtension().getBoundingVolume();
			List<ICollisionResults> collisionResults = new ArrayList<ICollisionResults>();
			Vector3D position=null;
			
			numberOfSteps=0;
			addPolicemanFlag=true;
			continueAssignment=true;
			
			do {
				
				final int randomNodeId = getRandomNearestPlayersNodeId();
				if ( randomNodeId == -1 )
					return;
				position = pathsNetwork.getNode(randomNodeId).getPosition().clone();
				position.setY(position.getY() + Y_OFFSET_INIT);
				
				policemanToActivate.getPolicemanPhysicalActivity().getControl().setPhysicsLocation(position.toVector3f());
				policemanToActivate.setStartNodeId(randomNodeId);
				policemanToActivate.getPhysicalActivity().setStartPathsNetworkNode(pathsNetwork.getNode(randomNodeId));
				policemanToActivate.getPolicemanPhysicalActivity().initOrientation(pathsNetwork);
				policemanToActivate.getPolicemanPhysicalActivity().clearTergetNode();
				policemanToActivate.getPolicemanPhysicalActivity().stopWalking();
				
				collisionEngine.addCollidable(newPolicemanBoundingVolume);
				
				if (/*collisionEngine.checkCollisions(newWalkerBoundingVolume, collisionResults).isEmpty() && */
						checkDistanceNewPolicemanToEachOther(policemanToActivate))
					continueAssignment = false;
				else
					collisionEngine.removeCollidable(newPolicemanBoundingVolume);
				
				++numberOfSteps;
				if ( (numberOfSteps>pathsNetwork.nodesCount()) && continueAssignment ) {
					collisionEngine.removeCollidable(newPolicemanBoundingVolume);
					addPolicemanFlag=false;
					continueAssignment=false;
				}
			} while(continueAssignment);
		
			/* add new walker */
			if (addPolicemanFlag) {
				addPoliceman(policemanToActivate, position, false);
				HumanSoundManager.getInstance().addPolicemanSound( policemanToActivate );
			} else {
				finalizePolicemanComponent(policemanToActivate);
			}
		}
		
	}
	
	private void disactivateExcessivePolicemans( final int numPolicemanToDisactivate ) {
		if ( activePolicemans.isEmpty() )
			return;
		for( int i=0; i<numPolicemanToDisactivate; ++i ) {
			
			AbstractHuman policemanToDisactivate = findFurtherPoliceman();
			if ( policemanToDisactivate==null )
				return;
			finalizePolicemanComponent(policemanToDisactivate);
		}
	}
	
	private int findMaxWantedStars( final List< Player > players ) {
		int maxWantedStars = 0;
		int currentStars;
		for( Player p: players ) {
			currentStars = p.getWantedStars().getCurrentStars();
			if ( currentStars>maxWantedStars )
				maxWantedStars = currentStars;
		}
		return maxWantedStars;
	}
	
	private static int computeNeededPolicemans( final int wantedStars ) {
		return wantedStars;
	}
	
	private int getRandomNodeId() {
		return ( (rn.nextInt(pathsNetwork.nodesCount())) + 1 );
	}
	
	private int getRandomNearestPlayersNodeId() {
		List< RoadNode > nearestNodes = findNearestNodesToPlayers();
		if ( nearestNodes.isEmpty() )
			return -1;
		final int randomIndex = rn.nextInt( nearestNodes.size() );
		return ( nearestNodes.get( randomIndex ).getId() );
	}
	
	private boolean isPositionNearToPlayers( final Vector3f position ) {
		final List< Player > players = application.getPlayers();
		float distance;
		for( Player p: players ) {
			
			distance = position.distance( p.getHumanPhysicalActivity().getControl().getPhysicsLocation() );
			if ( ( distance<=(MAX_NEAR_PLAYER_DISTANCE-MARGIN_NEAR_PLAYER_DISTANCE) ) &&
					 ( distance>=(PADDING_NEAR_PLAYER_DISTANCE) ) )
				return true;
		}
		return false;
	}
	
	private boolean checkDistanceNewPolicemanToEachOther(final AbstractHuman policeman) {
		for(AbstractHuman p: activePolicemans)
			if (policeman.getSpatialTranslation().distanceTo(p.getSpatialTranslation()) < MIN_DISTANCE_POLICEMAN_ASSIGNMENT)
				return false;
		return true;
	}
	
	private AbstractHuman findFurtherPoliceman() {
		AbstractHuman furtherPoliceman=null;
		float furtherDistance=0.0f;
		float currentDistance;
		for( AbstractHuman p: activePolicemans ) {
			
			currentDistance = computeDistanceAverageFromPlayers( p );
			if ( furtherPoliceman==null || ( (furtherPoliceman!=null) && (currentDistance>furtherDistance) ) ) {
				furtherPoliceman = p;
				furtherDistance = currentDistance;
			}
		}
		return furtherPoliceman;
	}
	
	private float computeDistanceAverageFromPlayers( final AbstractHuman policeman ) {
		final List< Player > players = application.getPlayers();
		if ( players.isEmpty() )
			return 0.0f;
		
		float distanceSum=0.0f;
		for( Player p: players ) {
			distanceSum += policeman.getHumanPhysicalActivity().getControl().getPhysicsLocation().distance(
					p.getHumanPhysicalActivity().getControl().getPhysicsLocation());
		}
		
		return ( distanceSum/((float) players.size()) );
	}
	
	private void notifyObservers() {
		for( IObserver obs: observers ) {
			obs.onStateShanged();
			obs.onStateShanged( this );
		}
	}
	
	private static void setPolicemansCookiesFromPolicemans( final List< AbstractHuman > policemans, List< PolicemanCookie > policemansCookie ) {
		int i, size;
		
		policemansCookie.clear();
		size = policemans.size();
		for( i=0; i<size; ++i )
			policemansCookie.add( 
					policemans.get(i).getPolicemanPhysicalActivity().getPolicemanCookie() );
	}
	
	private void setActivePolicemansFromCookies( final List< PolicemanCookie > activePolicemansCookies ) {
		boolean found;
		
		for( PolicemanCookie policemanCookie: activePolicemansCookies ) {
			
			found=false;
			for (Iterator< AbstractHuman > it = activePolicemans.iterator(); ( (it.hasNext()) && (!found) );) {
				AbstractHuman policeman = it.next();
				PolicemanPhysicalActivity policemanPhysicalActivity = policeman.getPolicemanPhysicalActivity();
				
				if ( policemanPhysicalActivity.getId()==policemanCookie.walkerCookie.id ) {
					policemanPhysicalActivity.setPolicemanCookie(policemanCookie);
					found=true;
				}	
			}
			
			if ( !found ) {
				
				for (Iterator< AbstractHuman > it = idlePolicemans.iterator(); ( (it.hasNext()) && (!found) );) {
					AbstractHuman policeman = it.next();
					PolicemanPhysicalActivity policemanPhysicalActivity = policeman.getPolicemanPhysicalActivity();
					
					if ( policemanPhysicalActivity.getId()==policemanCookie.walkerCookie.id ) {
						initPolicemanComponent(policeman);
						addPoliceman( policeman,
								new Vector3D( policemanPhysicalActivity.getControl().getPhysicsLocation() ), 
								false );
						policemanPhysicalActivity.setPolicemanCookie(policemanCookie);
						found=true;
					}
				}
			}
			
		}
		
	}
	
	private void setIdlePolicemansFromCookies( final List< PolicemanCookie > idlePolicemansCookies ) {
		boolean found;
		
		for( PolicemanCookie policemanCookie: idlePolicemansCookies ) {
			
			found=false;
			for (Iterator< AbstractHuman > it = idlePolicemans.iterator(); ( (it.hasNext()) && (!found) );) {
				AbstractHuman policeman = it.next();
				PolicemanPhysicalActivity policemanPhysicalActivity = policeman.getPolicemanPhysicalActivity();
				
				if ( policemanPhysicalActivity.getId()==policemanCookie.walkerCookie.id ) {
					policemanPhysicalActivity.setPolicemanCookie(policemanCookie);
					found=true;
				}	
			}
			
			if ( !found ) {
				
				for (Iterator< AbstractHuman > it = activePolicemans.iterator(); ( (it.hasNext()) && (!found) );) {
					AbstractHuman policeman = it.next();
					PolicemanPhysicalActivity policemanPhysicalActivity = policeman.getPolicemanPhysicalActivity();
					
					if ( policemanPhysicalActivity.getId()==policemanCookie.walkerCookie.id ) {
						finalizePolicemanComponent(policeman);
						notifyObservers();
						//idlePolicemans.add(policeman);
						policemanPhysicalActivity.setPolicemanCookie(policemanCookie);
						found=true;
					}
				}
			}
			
		}
		
	}
	
	private boolean canUpdatePolicemans() {
		IRemoteCommunicator remoteCommunicator = application.getRemoteCommunicator();
		if ( (remoteCommunicator!=null) && (remoteCommunicator.isConnected()) && (!remoteCommunicator.isMaster()) )
			return false;
		return true;
	}

}
