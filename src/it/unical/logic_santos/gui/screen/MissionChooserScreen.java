/**
 * 
 */
package it.unical.logic_santos.gui.screen;

import java.util.ArrayList;
import java.util.List;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.dynamic.CustomControlCreator;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.nifty.tools.SizeValueType;
import it.unical.logic_santos.mission.AssetMissionSource;
import it.unical.logic_santos.mission.IMission;
import it.unical.logic_santos.mission.IMissionLoader;
import it.unical.logic_santos.mission.IMissionSource;
import it.unical.logic_santos.mission.MissionConfig;

/**
 * @author Agostino
 *
 */
public class MissionChooserScreen implements IScreen, ScreenController {

	private Nifty nifty=null;
	private MainMenuScreen mainMenuScreen=null;
	private List< WidgetElementController > takeMissionButtonControllers=null;
	private List< IMission > availableMissionPrototypes=null;
	private int idMissionPrototypeCount=0;
	
	private static final String START_SCREEN_TAG_ID  = "start";
	private static final String ITEMS_PARENT_TAG_ID  = "box-parent";
	private static final String MISSION_PANEL_TAG_ID = "missionsPanel";
	
	private static final String MISSION_NAME_CONTROL_TAG_ID        = "missionNameControl";
	private static final String MISSION_DESCRIPTION_CONTROL_TAG_ID = "missionDescriptionControl";
	private static final String MISSION_GAIN_CONTROL_TAG_ID        = "missionGainControl";
	private static final String MISSION_TAKE_BUTTON_TAG_ID         = "takeMissionButton";
	private static final String SCROLL_PANEL_MISSION_ITEMS_TAG_ID  = "scrollPanel";
	private static final int    MISSION_ITEM_HEIGHT_SIZE           = 330; // expressed in pixels
	
	public MissionChooserScreen( Nifty nifty, MainMenuScreen mainMenuScreen ) {
		this.nifty = nifty;
		this.mainMenuScreen = mainMenuScreen;
		this.takeMissionButtonControllers = new ArrayList< WidgetElementController >();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartScreen() {
		// TODO Auto-generated method stub
		
	}
	
	public void start() {
		nifty.fromXml( ScreenConfig.MISSIONS_XML_GUI_NIFTY_NAME, START_SCREEN_TAG_ID, this );
		addMissionItems();
	}
	
	public void onTakeMission() {
		takeMission( WidgetElementController.widgetElementId );
	}
	
	public void onBackToMainMenu() {
		mainMenuScreen.backToMenu();
	}
	
	public void takeMission( final String takeMissionButtonElementId ) {
		final int intMissionId = Integer.parseInt( takeMissionButtonElementId.substring( MISSION_TAKE_BUTTON_TAG_ID.length() ) );
		IMission newMission = availableMissionPrototypes.get( intMissionId ).cloneMission();
		System.out.println(" TAKING MISSION: "+intMissionId);
		mainMenuScreen.getApplication().getPlayerManager().getMissionManager().setCurrentMission( newMission );
		mainMenuScreen.onRoaming();
	}
	
	private void addMissionItems() {
		Screen screen = nifty.getScreen( START_SCREEN_TAG_ID );
		Element itemsElement = screen.findElementById( ITEMS_PARENT_TAG_ID );
		
		clearElementChildren( itemsElement );
		takeMissionButtonControllers.clear();
		idMissionPrototypeCount=0;
		
		IMissionSource missionSource = new AssetMissionSource( MissionConfig.MISSION_FOLDER_SOURCE_PATH );
		IMissionLoader missionLoader = IMissionLoader.getAssetMissionLoader();
		missionLoader.loadMissions( missionSource );
		availableMissionPrototypes = missionLoader.getAvailableMissionPrototypes();
		
		for( IMission missionPrototype: availableMissionPrototypes ) 
			addMissionItem( missionPrototype );
		
		Element scrollPanelMissionItems = screen.findElementById( SCROLL_PANEL_MISSION_ITEMS_TAG_ID );
		scrollPanelMissionItems.setConstraintHeight( 
				new SizeValue( MISSION_ITEM_HEIGHT_SIZE*availableMissionPrototypes.size(), SizeValueType.Pixel) );
		
	}
	
	private void clearElementChildren( Element element ) {
		List< Element > children = element.getChildren();
		for( Element c: children )
			c.markForRemoval();
	}
	
	private void addMissionItem( final IMission missionPrototype ) {
		Screen screen = nifty.getScreen( START_SCREEN_TAG_ID );
		Element itemsElement = screen.findElementById( ITEMS_PARENT_TAG_ID );
		
		final String missionId = String.valueOf( idMissionPrototypeCount );
		idMissionPrototypeCount++;
		
		CustomControlCreator itemCreator = new CustomControlCreator( missionId, MISSION_PANEL_TAG_ID );
		itemCreator.create(nifty, screen, itemsElement );
		
		Element missionElement = itemsElement.findElementById( missionId );
		Element missionNameElement        = missionElement.findElementById( MISSION_NAME_CONTROL_TAG_ID );
		Element missionDescriptionElement = missionElement.findElementById( MISSION_DESCRIPTION_CONTROL_TAG_ID );
		Element missionGainElement        = missionElement.findElementById( MISSION_GAIN_CONTROL_TAG_ID );
	
		missionNameElement.getRenderer( TextRenderer.class ).setText( missionPrototype.getName() );
		missionDescriptionElement.getRenderer( TextRenderer.class ).setText( missionPrototype.getDescription() );
		missionGainElement.getRenderer( TextRenderer.class ).setText( gainValueToString( missionPrototype.getGain() ) );
		
		Element takeMissionButtonElement = missionElement.findElementById( MISSION_TAKE_BUTTON_TAG_ID );
		takeMissionButtonElement.setId( MISSION_TAKE_BUTTON_TAG_ID+missionId );
		
	}
	
	private static String gainValueToString( final float gainValue ) {
		return ( "$ " + Float.toString(gainValue) );
	}

	

}
