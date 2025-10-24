/**
 * 
 */
package it.unical.logic_santos.logging;

/**
 * @author Agostino
 *
 */
public class AircraftLoggingEvent implements ILoggingEvent {
	
	public static final int STOLE_AIRCRAFT_EVENT = 0;
	public static final int LEAVE_AIRCRAFT_EVENT = 1;
	
	private int eventType;
	
	public AircraftLoggingEvent( final int eventType ) {
		this.eventType = eventType;
	}

	@Override
	public int getEventType() {
		return eventType;
	}

	@Override
	public int getData() {
		return 0;
	}

}
