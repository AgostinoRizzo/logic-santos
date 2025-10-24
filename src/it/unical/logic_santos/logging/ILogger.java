/**
 * 
 */
package it.unical.logic_santos.logging;

import java.util.Queue;

/**
 * @author Agostino
 * The ILogger interface represents a generic Logger used to log messages and events from 
 * the system and application components.
 */
public interface ILogger {

	/** the method logs a Logging Event that appends in 
	 * the system and application components.
	 * 
	 * @param loggingEvent that appends in the system and application components.
	 */
	public void log( final ILoggingEvent loggingEvent );
	
	/** the method returns the queue of the last Logging Events that are still stored.
	 * 
	 * @return the queue of the last Logging Events that are still stored.
	 */
	public Queue< ILoggingEvent > getLoggingEvents();
	
	/** the method make a Logging Observer to be notified when a new Logging Event appends.
	 * 
	 * @param loggingObjerver the Logging Observer to be notified when a new 
	 *        Logging Event appends.
	 */
	public void addLoggingObserver( ILoggingObserver loggingObserver );
	
	/** the method remove a Logging Observer to be notified when a new Logging Event appends.
	 * 
	 * @param loggingObserver the Logging Observer to be removed
	 */
	public void removeLoggingObserver( ILoggingObserver loggingObserver );
	
	/** the method returns the unique MissionEventsLogger instace ( Singleton ).
	 * 
	 * @return the unique MissionEventsLogger instace ( Singleton ).
	 */
	public static MissionEventsLogger getMissionEventsLogger() {
		return MissionEventsLogger.getInstance();
	}
	
}
