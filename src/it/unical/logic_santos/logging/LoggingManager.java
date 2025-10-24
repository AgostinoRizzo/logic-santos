/**
 * 
 */
package it.unical.logic_santos.logging;

/**
 * @author Agostino
 *
 */
public class LoggingManager {

	public static ILogger getDefaultLogger() {
		return MissionEventsLogger.getInstance();
	}
}
