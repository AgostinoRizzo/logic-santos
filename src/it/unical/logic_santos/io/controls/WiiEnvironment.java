package it.unical.logic_santos.io.controls;

import wiiremotej.*;
import wiiremotej.event.WRIREvent;

public class WiiEnvironment {

	public static void initWiiRemoteMasterDevice(WiiRemote wiiRemote) throws Exception, NullPointerException {
		
		if (wiiRemote != null) {
		    wiiRemote.setAccelerometerEnabled(true);
		    wiiRemote.setUseMouse(true);
		    wiiRemote.setSpeakerEnabled(true);
		    wiiRemote.setIRSensorEnabled(true, WRIREvent.BASIC);
		    wiiRemote.setLEDIlluminated(0, true);
		} else 
			throw new NullPointerException();
	}
	
	public static void initWiiSecondaryRemoteDevice(WiiRemote wiiRemote, final int wiiRemotePositionId) throws Exception, NullPointerException, IllegalArgumentException {
		
		if (wiiRemote == null) throw new NullPointerException();
		if (wiiRemotePositionId < 0) throw new IllegalArgumentException(); // cambiare condizione
		
		wiiRemote.setAccelerometerEnabled(true);
		wiiRemote.setUseMouse(true);
		wiiRemote.setSpeakerEnabled(true);
		wiiRemote.setIRSensorEnabled(true, WRIREvent.BASIC);
		wiiRemote.setLEDIlluminated(wiiRemotePositionId, true);
	}
	
}
