/**
 * 
 */
package it.unical.logic_santos.logging;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Agostino
 *
 */
public abstract class AbstractLogger implements ILogger {

	private List< ILoggingObserver > loggingObservers=null;
	
	public AbstractLogger() {
		this.loggingObservers = new LinkedList< ILoggingObserver >();
	}
	
	@Override
	public void log( ILoggingEvent loggingEvent ) {
		notityLoggingObservers( loggingEvent );		
	}
	
	@Override
	public void addLoggingObserver( ILoggingObserver loggingObserver ) {
		loggingObservers.add( loggingObserver );
		
	}
	
	@Override
	public void removeLoggingObserver( ILoggingObserver loggingObserver ) {
		loggingObservers.remove( loggingObserver );
	}
	
	/** the method notifies all Logging Observer when a new Logging Event appends.
	 * 
	 */
	protected void notityLoggingObservers( final ILoggingEvent loggingEvent ) {
		for( ILoggingObserver obs: loggingObservers )
			obs.onLoggingEvent( loggingEvent, this );
	}
	
}
