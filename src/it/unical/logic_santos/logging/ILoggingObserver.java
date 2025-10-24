/**
 * 
 */
package it.unical.logic_santos.logging;

/**
 * @author Agostino
 *
 */
public interface ILoggingObserver {

	/** the method onLoggingEvent( final ILoggingEvent loggingEvent ) is 
	 *  called when a new Logging event is detected in the Logger ( Subject ).
	 * 
	 * @param loggingEvent detected in the Logger ( Subject ).
	 * @param logger that detected the Logging Event.
	 */
	public void onLoggingEvent( final ILoggingEvent loggingEvent, final ILogger logger );
}
