/**
 * 
 */
package it.unical.logic_santos.gui.screen;

import com.jme3.niftygui.NiftyJmeDisplay;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import it.unical.logic_santos.gui.application.LogicSantosApplication;

/**
 * @author Agostino
 *
 */
public class MainMenuScreen implements IScreen, ScreenController {
	
	private LogicSantosApplication application=null;
	private Nifty nifty=null;
	private NiftyJmeDisplay niftyDisplay=null;
	private MissionChooserScreen missionChooserScreen=null;
	private ControlsScreen controlsScreen=null;
	private boolean isVisible=false;
	
	public MainMenuScreen( LogicSantosApplication application ) {
		this.application = application;
	}

	@Override
	public void initComponents() {
		niftyDisplay = NiftyJmeDisplay
				.newNiftyJmeDisplay( application.getAssetManager(), 
									 application.getInputManager(), 
									 application.getAudioRenderer(), 
									 application.getGuiViewPort() );
		nifty = niftyDisplay.getNifty();
		application.getGuiViewPort().addProcessor( niftyDisplay );
		
		missionChooserScreen = new MissionChooserScreen( nifty, this );
		missionChooserScreen.initComponents();
		
		controlsScreen = new ControlsScreen( nifty, this );
		controlsScreen.initComponents();
	}

	@Override
	public void finalizeComponents() {
		missionChooserScreen.finalizeComponents();
		
	}

	@Override
	public void update(float tpf) {
		
		
	}

	@Override
	public void bind(Nifty arg0, Screen arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEndScreen() {
		application.getGraphicalEnvironmentLight().attachAdditionalLightEffects();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartScreen() {
		application.getGraphicalEnvironmentLight().detachAdditionalLightEffects();
		// TODO Auto-generated method stub
		
	}
	
	public boolean isVisible() {
		return isVisible;
	}
	
	public void startMenu() {
		nifty.fromXml( ScreenConfig.MAIN_MENU_XML_GUI_NIFTY_NAME, "menu", this );
		isVisible=true;
		application.onMenuShow();
	}
	
	void backToMenu() {
		nifty.fromXml( ScreenConfig.MAIN_MENU_XML_GUI_NIFTY_NAME, "menu", this );
		isVisible=true;
		application.onMenuShow();
	}
	
	public void onRoaming() {
		nifty.fromXml( ScreenConfig.ROAMING_XML_GUI_NIFTY_NAME, "start", this );
		//onMissions();
	}
	
	public void onMissions() {
		missionChooserScreen.start();
	}
	
	public void onHosts() {
		application.onHosts();
		nifty.fromXml( ScreenConfig.ROAMING_XML_GUI_NIFTY_NAME, "start", this );
	}
	
	public void onTakePart() {
		application.onTakePart();
		nifty.fromXml( ScreenConfig.ROAMING_XML_GUI_NIFTY_NAME, "start", this );
	}
	
	public void onControls() {
		controlsScreen.start();
	}
	
	public void onExit() {
		application.stop();
	}
	
	public void roaming() {
		nifty.exit();
		isVisible=false;
		application.onMenuHide();
	}
	
	public LogicSantosApplication getApplication() {
		return application;
	}
	

}
