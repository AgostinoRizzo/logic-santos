/**
 * 
 */
package it.unical.logic_santos.logging;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Agostino
 *
 */
public class MissionEventsLogger extends AbstractLogger {

	private static MissionEventsLogger instance = null;
	
	private Queue< ILoggingEvent > loggingQueue=null;
	private static final int LOGGING_EVENT_CAPACITY = 10;
	
	public static MissionEventsLogger getInstance() {
		if ( instance==null )
			instance=new MissionEventsLogger();
		return instance;
	}
	
	private MissionEventsLogger() {
		super();
		loggingQueue = new LinkedList< ILoggingEvent >();
	}
	
	@Override
	public void log(ILoggingEvent loggingEvent) {
		if ( loggingQueue.size()>=LOGGING_EVENT_CAPACITY )
			loggingQueue.remove();
		loggingQueue.add( loggingEvent );
		super.log( loggingEvent );
	}

	@Override
	public Queue< ILoggingEvent > getLoggingEvents() {
		return loggingQueue;
	}

	

}
