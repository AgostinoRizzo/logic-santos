package it.unical.logic_santos.io.controls;

import com.intel.bluetooth.BlueCoveConfigProperties;
import com.intel.bluetooth.BlueCoveImpl;

import it.unical.logic_santos.gui.screen.ControlsScreen;
import wiiremotej.*;
import wiiremotej.event.*;

public class WiiAreaNetwork implements WiiDeviceDiscoveryListener {
	
	// maximum number of Wii Devices that are managed by the WiiAreaNetwork
	public static final int MAX_NUMBER_WII_DEVICES = 4;
	
	// Master Wii Remote: the main Wii Remote used to manage the enviroment
	private WiiRemote wiiRemoteMasterDevice;
	private WiiDevice[] wiiSecondaryDevices; // secondary Wii Devices: WiiRemote, WiiBalanceBoard, ...
	
	private WiiRemoteManager wiiRemoteMasterManager=null;
	
	private ControlsScreen controlsScreenObserver=null;
	
	private static WiiAreaNetwork instance = null;
	
	public static WiiAreaNetwork getInstance() {
		if ( instance==null ) {
			initWiiAreaNetworkEnvironment();
			instance = new WiiAreaNetwork();
		}
		return instance;
	}
	
	// ... CONSTRUCTORS ...
	
	private WiiAreaNetwork() {
		wiiRemoteMasterDevice = null;                                  /* initialize MASTER WII REMOTE with null */
		wiiSecondaryDevices = new WiiDevice[MAX_NUMBER_WII_DEVICES-1]; /* WII SECONDARY DEVICES */
		initNewWiiDeviceArray(wiiSecondaryDevices);                    /* initialize new WII DEVICES array with null value */
	}
	
	private WiiAreaNetwork(WiiRemote masterWiiRemote) {
		wiiRemoteMasterDevice = masterWiiRemote;                       /* initialize MASTER WII REMOTE with given value */
		wiiSecondaryDevices = new WiiDevice[MAX_NUMBER_WII_DEVICES-1]; /* WII SECONDARY DEVICES */
		initNewWiiDeviceArray(wiiSecondaryDevices);                    /* initialize new WII DEVICES array with null value */
	}
	
	
	// ... STATIC METHODS ...
	
	public static void initWiiAreaNetworkEnvironment() { /* this static method has to be invoked at the beginning of the program */
		BlueCoveImpl.setConfigProperty(BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true");
	}
	
	// ... GETTER AND SETTER METHODS ...
	
    public WiiRemote getRemoteMasterDevice() {
    	return wiiRemoteMasterDevice;
    }
    
    public WiiDevice getWiiSecondaryDevice(final int i) throws ArrayIndexOutOfBoundsException {
    	if (i < (MAX_NUMBER_WII_DEVICES-1))
    		return wiiSecondaryDevices[i];
    	else 
    		throw new ArrayIndexOutOfBoundsException();
    }
    
    public int getNumberDevices() {
    	
    	int n = 0;
    	
    	if (wiiRemoteMasterDevice != null)
    		n++;
    	
    	for(int i = 0; i < wiiSecondaryDevices.length; ++i)
    		if (wiiSecondaryDevices[i] != null)
    			n++;
    	return n;
    }

    public int getNumberMissingDevices() {
    	
    	int n = 0;
    	
    	if (wiiRemoteMasterDevice == null)
    		n++;
    	
    	for(int i = 0; i < wiiSecondaryDevices.length; ++i)
    		if (wiiSecondaryDevices[i] == null)
    			n++;
    	return n;
    }
    
    // ... PUBLIC METHODS ...
    
    public void findWiiRemotes() throws Exception { // Exception propagation ...
    	clearBlankDevices();
    	WiiRemoteJ.findRemotes(this, this.getNumberMissingDevices());
    }
    
    public void stopFind() throws Exception {
    	WiiRemoteJ.stopFind();
    }
    
    public boolean isFindingInProgress() {
    	return WiiRemoteJ.isFindInProgress();
    }
    
    public boolean hasWiiRemoteMasterDevice() {
    	return (wiiRemoteMasterDevice != null);
    }
    
    public void clearBlankDevices() {
    	
    	if (wiiRemoteMasterDevice != null)
    		if (!wiiRemoteMasterDevice.isConnected()) {
    			wiiRemoteMasterDevice = null;
    			wiiRemoteMasterManager = null;
    		}
    	
    	for(int i = 0; i < wiiSecondaryDevices.length; ++i)
    		if (wiiSecondaryDevices[i] != null)
    			if (!wiiSecondaryDevices[i].isConnected())
    				wiiSecondaryDevices[i] = null;
    }
    
    public void finalize() {
    	
    	if (wiiRemoteMasterDevice != null) {
    	    if (wiiRemoteMasterDevice.isConnected())
    	    	wiiRemoteMasterDevice.disconnect();
    	    wiiRemoteMasterDevice = null;
    	    wiiRemoteMasterManager = null;
    	}
    	
    	for(int i = 0; i < wiiSecondaryDevices.length; ++i)
    		if (wiiSecondaryDevices[i] != null) {
    			if (wiiSecondaryDevices[i].isConnected())
    				wiiSecondaryDevices[i].disconnect();
    			wiiSecondaryDevices[i] = null;
    		}
    }

    
	@Override
	public void findFinished(int arg0) {
		
		
	}

	@Override
	public void wiiDeviceDiscovered(WiiDeviceDiscoveredEvent evt) {
		
		WiiDevice newWiiDevice = evt.getWiiDevice();
		boolean inserted = false;
		
		if ((newWiiDevice instanceof WiiRemote) && (wiiRemoteMasterDevice == null)) {
			wiiRemoteMasterDevice = (WiiRemote)newWiiDevice;
			wiiRemoteMasterManager = new WiiRemoteManager( wiiRemoteMasterDevice );
			inserted = true;
			try {
				WiiEnvironment.initWiiRemoteMasterDevice( wiiRemoteMasterDevice );
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			for(int i = 0; ((i < wiiSecondaryDevices.length) && (!inserted)); ++i)
				if (wiiSecondaryDevices[i] == null) {
					wiiSecondaryDevices[i] = newWiiDevice;
					inserted = true;
					//WiiEnvironment.initWiiSecondaryRemoteDevice( wiiSecondaryDevices[i] );
				}
		
		if (!inserted)
			newWiiDevice.disconnect();
		else if ( controlsScreenObserver!=null )
			controlsScreenObserver.onWiiAreaNetworkChanged();
			
	}
	
	public void addObserver( ControlsScreen observer ) {
		this.controlsScreenObserver = observer;
	}
	
	public boolean hasWiiRemoteMasterManager() {
		return ( wiiRemoteMasterManager!=null );
	}
	
	public WiiRemoteManager getWiiRemoteMasterManager() {
		return wiiRemoteMasterManager;
	}
	
	// ... PRIVATE METHODS ...
	
	private void initNewWiiDeviceArray(WiiDevice[] devices) {
	for(int i = 0; i < devices.length; ++i)
		devices[i] = null;
	}

}
