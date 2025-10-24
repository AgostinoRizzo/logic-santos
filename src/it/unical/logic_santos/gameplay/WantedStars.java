/**
 * 
 */
package it.unical.logic_santos.gameplay;

import java.util.ArrayList;
import java.util.List;

import it.unical.logic_santos.logging.ILogger;
import it.unical.logic_santos.logging.ILoggingEvent;
import it.unical.logic_santos.logging.LoggingManager;
import it.unical.logic_santos.logging.WantedStarsLoggingEvent;
import it.unical.logic_santos.traffic.PolicemanTrafficManager;

/**
 * @author Agostino
 *
 */
public class WantedStars implements IObserver {

	public static final int  MAX_WANTED_VALUE = 5; // it is intended 5 stars
	public static final boolean EMPTY_STAR = false;
	public static final boolean FULL_STAR  = true;
	
	private static final int MIN_SHOOT_STARTED_VALUES_FIRST_TRIGGER = 3;
	private static final int MIN_SHOOT_STARTED_VALUES_SUCCESSIVE_TRIGGER = 50;
	private static final int MIN_SHOOT_HIT_VALUES_FIRST_TRIGGER = 1;
	private static final int MIN_SHOOT_HIT_VALUES_SUCCESSIVE_TRIGGER = 3;
	
	private int currentStars=0;
	private int currentShootStartedValues=0;
	private int currentShootHitValues=0;
	
	private List< IObserver > observers=null;
	
	public WantedStars() {
		this.currentStars=0;
		this.currentShootStartedValues=0;
		this.currentShootHitValues=0;
		this.observers = new ArrayList< IObserver >();
	}
	
	public int getCurrentStars() {
		return currentStars;
	}
	
	public void onShootStart( final IBullet bullet ) {
		currentShootStartedValues += bullet.getWantedValue();
		final int minShootStartedValues = getMinShootStartedValuesTrigger();
		if ( currentShootStartedValues>=minShootStartedValues ) {
			currentStars += currentShootStartedValues/minShootStartedValues;
			currentShootStartedValues=0;
			notifyObservers();
			
			ILogger logger = LoggingManager.getDefaultLogger();
			ILoggingEvent event = new WantedStarsLoggingEvent( 
					WantedStarsLoggingEvent.WANTED_STARS_INCREASE_EVENT, 
					currentStars );
			logger.log( event );
		}
	}
	
	public void onShootHit( final IBullet bullet ) {
		currentShootHitValues += bullet.getWantedValue();
		final int minShootHitValues = getMinShootHitValuesTrigger();
		if ( currentShootHitValues>=minShootHitValues ) {
			currentStars += currentShootHitValues/minShootHitValues;
			currentShootHitValues=0;
			notifyObservers();
			
			ILogger logger = LoggingManager.getDefaultLogger();
			ILoggingEvent event = new WantedStarsLoggingEvent( 
					WantedStarsLoggingEvent.WANTED_STARS_INCREASE_EVENT, 
					currentStars );
			logger.log( event );
		}
	}
	
	public void update( final float tpf ) {
		// TODO
	}
	
	public void addObserver( IObserver obs ) {
		if ( !observers.contains(obs) )
			observers.add(obs);
	}
	
	public void removeObserver( IObserver obs ) {
		observers.remove(obs);
	}
	
	public boolean[] getStarsStates() {
		boolean[] starStates = new boolean[MAX_WANTED_VALUE];
		
		final int remaningStars = MAX_WANTED_VALUE-currentStars;
		int i;
		for( i=0; i<remaningStars; ++i)
			starStates[i] = EMPTY_STAR;
		for( ; i<MAX_WANTED_VALUE; ++i)
			starStates[i] = FULL_STAR;
		
		return starStates;
	}
	
	@Override
	public String toString() {
		int i;
		StringBuilder ansBuilder = new StringBuilder();
		
		final int remaningStars = MAX_WANTED_VALUE-currentStars;
		for( i=0; i<remaningStars; ++i)
			ansBuilder.append('_');
		
		for( i=0; i<currentStars; ++i)
			ansBuilder.append('*');
			
		return (ansBuilder.toString());
	}
	
	@Override
	public void onStateShanged() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStateShanged(ISubject subject) {
		if ( subject instanceof PolicemanTrafficManager ) {
			PolicemanTrafficManager policemanTrafficManager = (PolicemanTrafficManager) subject;
			final int numActivePolicemans = policemanTrafficManager.getNumActivePolicemans();
			final int numWantedStarsToAdd = computeNeededWantedStars( numActivePolicemans )-currentStars;
			currentStars += numWantedStarsToAdd;
			if ( currentStars >= MAX_WANTED_VALUE )
				currentStars = MAX_WANTED_VALUE;
			else if ( currentStars < 0 )
				currentStars = 0;
			currentShootStartedValues = 0;
			currentShootHitValues = 0;
			notifyObservers();
			
			if ( numWantedStarsToAdd!=0 ) {
				
				int loggingEventType = ( numWantedStarsToAdd>0 ) ? WantedStarsLoggingEvent.WANTED_STARS_INCREASE_EVENT
																 : WantedStarsLoggingEvent.WANTED_STARS_REDUCTION_EVENT;
				ILogger logger = LoggingManager.getDefaultLogger();
				ILoggingEvent event = new WantedStarsLoggingEvent( 
						loggingEventType, 
						currentStars );
				logger.log( event );
			}
		}
	}
	
	public void clear() {
		this.currentStars=0;
		this.currentShootStartedValues=0;
		this.currentShootHitValues=0;
		notifyObservers();
	}
	
	public WantedStarsCookie getCookie() {
		WantedStarsCookie cookie = new WantedStarsCookie();
		cookie.currentStars = this.currentStars;
		cookie.currentShootStartedValues = this.currentShootStartedValues;
		cookie.currentShootHitValues = this.currentShootHitValues;
		return cookie;
	}
	
	public void setCookie( final WantedStarsCookie cookie ) {
		this.currentStars = cookie.currentStars;
		this.currentShootStartedValues = cookie.currentShootStartedValues;
		this.currentShootHitValues = cookie.currentShootHitValues;
	}
	
	private int getMinShootStartedValuesTrigger() {
		if ( currentStars==0 )
			return MIN_SHOOT_STARTED_VALUES_FIRST_TRIGGER;
		else return MIN_SHOOT_STARTED_VALUES_SUCCESSIVE_TRIGGER;
	}
	
	private int getMinShootHitValuesTrigger() {
		if ( currentStars==0 )
			return MIN_SHOOT_HIT_VALUES_FIRST_TRIGGER;
		else return MIN_SHOOT_HIT_VALUES_SUCCESSIVE_TRIGGER;
	}
	
	private void notifyObservers() {
		for( IObserver obs: observers )
			obs.onStateShanged();
	}

	private static int computeNeededWantedStars( final int numActivePolicemans ) {
		return numActivePolicemans;
	}
	
	
}
