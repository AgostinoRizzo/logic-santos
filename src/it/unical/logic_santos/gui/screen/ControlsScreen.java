/**
 * 
 */
package it.unical.logic_santos.gui.screen;

import java.util.List;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.dynamic.CustomControlCreator;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.nifty.tools.SizeValueType;
import it.unical.logic_santos.environment.sound.EffectSoundManager;
import it.unical.logic_santos.gameplay.IObserver;
import it.unical.logic_santos.gameplay.ISubject;
import it.unical.logic_santos.io.controls.WiiAreaNetwork;
import it.unical.logic_santos.io.controls.WiiRemoteManager;
import wiiremotej.WiiRemote;

/**
 * @author Agostino
 *
 */
public class ControlsScreen implements IScreen, ScreenController, IObserver {

	private Nifty nifty=null;
	private MainMenuScreen mainMenuScreen=null;
	
	private int wiiRemoteIdCount=0;
	
	private static final String START_SCREEN_TAG_ID  = "start";
	private static final String ITEMS_PARENT_TAG_ID  = "box-parent";
	private static final String CONTROLS_PANEL_TAG_ID = "controlsPanel";
	
	private static final String CONTROLLER_NAME_CONTROL_TAG_ID        = "controllerNameControl";
	private static final String CONTROLLER_DESCRIPTION_CONTROL_TAG_ID = "controllerDescriptionControl";
	private static final String SCROLL_PANEL_CONTROLS_ITEMS_TAG_ID  = "scrollPanel";
	private static final int    CONTROLS_ITEM_HEIGHT_SIZE           = 330; // expressed in pixels
	
	public ControlsScreen( Nifty nifty, MainMenuScreen mainMenuScreen ) {
		this.nifty = nifty;
		this.mainMenuScreen = mainMenuScreen;
		WiiAreaNetwork.getInstance().addObserver( this );
	}
	
	@Override
	public void initComponents() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finalizeComponents() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(float tpf) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void bind(Nifty arg0, Screen arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEndScreen() {
		try {
			WiiAreaNetwork.getInstance().stopFind();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void onStartScreen() {
		try {
			onWiiAreaNetworkChanged();
			WiiAreaNetwork.getInstance().findWiiRemotes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void start() {
		nifty.fromXml( ScreenConfig.CONTROLS_XML_GUI_NIFTY_NAME, START_SCREEN_TAG_ID, this );
	}
	
	public void onBackToMainMenu() {
		mainMenuScreen.backToMenu();
	}
	
	public void onWiiAreaNetworkChanged() {
		Screen screen = nifty.getScreen( START_SCREEN_TAG_ID );
		Element itemsElement = screen.findElementById( ITEMS_PARENT_TAG_ID );
		
		clearElementChildren( itemsElement );
		wiiRemoteIdCount=0;
		
		EffectSoundManager.getInstance().onWiiAreaNetworkChanged();
		
		WiiAreaNetwork wiiArea = WiiAreaNetwork.getInstance();
		if ( wiiArea.getNumberDevices()==0 )
			return;
		
		if ( wiiArea.hasWiiRemoteMasterDevice() ) {
			addWiiRemoteControllerItem( wiiArea.getRemoteMasterDevice() );
			if ( wiiArea.hasWiiRemoteMasterDevice() )
				wiiArea.getWiiRemoteMasterManager().addObserver( this );
		}
		
		//for( IMission missionPrototype: availableMissionPrototypes ) 
		//	addMissionItem(  );
		
		Element scrollPanelControlItems = screen.findElementById( SCROLL_PANEL_CONTROLS_ITEMS_TAG_ID );
		scrollPanelControlItems.setConstraintHeight( 
				new SizeValue( CONTROLS_ITEM_HEIGHT_SIZE /**availableMissionPrototypes.size()*/, SizeValueType.Pixel) );
	}
	
	@Override
	public void onStateShanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStateShanged(ISubject subject) {
		if ( subject instanceof WiiRemoteManager ) {
			WiiRemoteManager wiiManager = (WiiRemoteManager) subject;
			
			Screen screen = nifty.getScreen( START_SCREEN_TAG_ID );
			Element itemsElement = screen.findElementById( ITEMS_PARENT_TAG_ID );
			
			if ( wiiManager.isConnected() ) {
				
				Element wiiRemoteElement          = itemsElement.findElementById( "0" );
				if ( wiiRemoteElement!=null ) {
					Element missionDescriptionElement = wiiRemoteElement.findElementById( CONTROLLER_DESCRIPTION_CONTROL_TAG_ID );
				
					missionDescriptionElement.getRenderer( TextRenderer.class ).setText( 
							getWiiRemoteDescription( wiiManager.getWiiRemote() ) +
													 wiiManager.getWiiRemoteStatus() );
				}
				
			} else {
				wiiManager.removeObserver( this );
				WiiAreaNetwork.getInstance().finalize();
				clearElementChildren( itemsElement );
			}
		}
		
	}
	
	private void clearElementChildren( Element element ) {
		List< Element > children = element.getChildren();
		for( Element c: children )
			c.markForRemoval();
	}
	
	private void addWiiRemoteControllerItem( final WiiRemote wiiRemote ) {
		Screen screen = nifty.getScreen( START_SCREEN_TAG_ID );
		Element itemsElement = screen.findElementById( ITEMS_PARENT_TAG_ID );
		
		final String wiiRemoteId = ( new Integer( wiiRemoteIdCount ) ).toString();
		
		CustomControlCreator itemCreator = new CustomControlCreator( wiiRemoteId, CONTROLS_PANEL_TAG_ID );
		itemCreator.create(nifty, screen, itemsElement );
		
		Element wiiRemoteElement          = itemsElement.findElementById( wiiRemoteId );
		Element wiiRemoteNameElement      = wiiRemoteElement.findElementById( CONTROLLER_NAME_CONTROL_TAG_ID );
		Element missionDescriptionElement = wiiRemoteElement.findElementById( CONTROLLER_DESCRIPTION_CONTROL_TAG_ID );
	
		wiiRemoteNameElement.getRenderer( TextRenderer.class ).setText( wiiRemote.getBluetoothAddress() );
		missionDescriptionElement.getRenderer( TextRenderer.class ).setText( getWiiRemoteDescription( wiiRemote ) );
		
	}
	
	private static String getWiiRemoteDescription( final WiiRemote wiiRemote ) {
		if ( wiiRemote==null )
			return "";
		return wiiRemote.toString() + "\n";
	}
	
}
