/**
 * 
 */
package it.unical.logic_santos.io.controls;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import it.unical.logic_santos.gameplay.IObserver;
import it.unical.logic_santos.gameplay.ISubject;
import wiiremotej.WiiRemote;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRStatusEvent;
import wiiremotej.event.WiiRemoteAdapter;

/**
 * @author Agostino
 *
 */
public class WiiRemoteManager extends WiiRemoteAdapter implements ISubject {

	private WiiRemote wiiRemote=null;
	private String status="";
	
	private Set< IObserver > observers=null;
	
	private static Robot robot=null;
	
	public WiiRemoteManager( WiiRemote wiiRemote ) {
		this.wiiRemote = wiiRemote;
		this.observers = new HashSet<>();
	}
	
	@Override
	public void statusReported( WRStatusEvent evt ) {
		status = "Battery level: " + (double)evt.getBatteryLevel()/2+ "%" + "\n" +
				"Continuous: " + evt.isContinuousEnabled() + "\n" +
				"Remote continuous: " + wiiRemote.isContinuousEnabled();
		notifyObservers();
	}
	
	@Override
	public void buttonInputReceived( WRButtonEvent event ) {
		Robot robot = getRobot();
		if ( robot==null )
			return;
		
		if ( event.isPressed( WRButtonEvent.A ) )
			robot.mousePress( InputEvent.BUTTON1_MASK );	
		
		else if ( event.isPressed( WRButtonEvent.B ) ) 
			robot.mousePress( InputEvent.BUTTON2_MASK );
		else if ( event.wasReleased( WRButtonEvent.B ) ) 
			robot.mouseRelease( InputEvent.BUTTON2_MASK );
		
		else if ( event.isPressed( WRButtonEvent.UP ) ) 
			robot.keyPress( KeyEvent.VK_W );
		else if ( event.wasReleased( WRButtonEvent.UP ) ) 
			robot.keyRelease( KeyEvent.VK_W );
		
		else if ( event.isPressed( WRButtonEvent.DOWN ) ) 
			robot.keyPress( KeyEvent.VK_S );
		else if ( event.wasReleased( WRButtonEvent.DOWN ) ) 
			robot.keyRelease( KeyEvent.VK_S );
		
		else if ( event.isPressed( WRButtonEvent.RIGHT ) ) 
			robot.keyPress( KeyEvent.VK_D );
		else if ( event.wasReleased( WRButtonEvent.RIGHT ) ) 
			robot.keyRelease( KeyEvent.VK_D );
		
		else if ( event.isPressed( WRButtonEvent.LEFT ) ) 
			robot.keyPress( KeyEvent.VK_A );
		else if ( event.wasReleased( WRButtonEvent.LEFT ) ) 
			robot.keyRelease( KeyEvent.VK_A );
	}
	
	@Override
	public void disconnected() {
		notifyObservers();
	}
	
	public String getWiiRemoteStatus() {
		return status;
	}
	
	public WiiRemote getWiiRemote() {
		return wiiRemote;
	}
	
	public boolean isConnected() {
		return wiiRemote.isConnected();
	}
	
	public void addObserver( IObserver obs ) {
		observers.add( obs );
	}
	
	public void removeObserver( IObserver obs ) {
		observers.remove( obs );
	}
	
	private void notifyObservers() {
		for( IObserver obs: observers )
			obs.onStateShanged( this );
	}
	
	private static Robot getRobot() {
		if ( robot==null ) {
			try {
				robot = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
				robot = null;
			}
		}
		return robot;
	}
}
