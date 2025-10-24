/**
 * 
 */
package it.unical.logic_santos.logging;

/**
 * @author Agostino
 *
 */
public class VehicleLoggingEvent implements ILoggingEvent {

	public static final int STOLE_VEHICLE_EVENT               = 0;
	public static final int LEAVE_VEHICLE_DRIVER_BEHIND_EVENT = 1;
	public static final int VEHICLE_EXPLOSION_EVENT           = 2;
	
	private int eventType;
	
	public VehicleLoggingEvent( final int eventType ) {
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
