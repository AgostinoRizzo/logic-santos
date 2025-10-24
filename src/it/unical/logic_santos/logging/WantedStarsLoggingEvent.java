/**
 * 
 */
package it.unical.logic_santos.logging;

/**
 * @author Agostino
 *
 */
public class WantedStarsLoggingEvent implements ILoggingEvent {

	public static final int WANTED_STARS_INCREASE_EVENT  = 0;
	public static final int WANTED_STARS_REDUCTION_EVENT = 1;
	
	private int eventType;
	private int wantedStarsCount;
	
	public WantedStarsLoggingEvent( final int eventType, final int wantedStarsCount ) {
		this.eventType = eventType;
		this.wantedStarsCount = wantedStarsCount;
	}

	@Override
	public int getEventType() {
		return eventType;
	}

	@Override
	public int getData() {
		return wantedStarsCount;
	}
	
	public int getWantedStarsCount() {
		return wantedStarsCount;
	}
	
}
