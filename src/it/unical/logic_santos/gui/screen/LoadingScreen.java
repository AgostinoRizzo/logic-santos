/**
 * 
 */
package it.unical.logic_santos.gui.screen;

import com.jme3.niftygui.NiftyJmeDisplay;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import it.unical.logic_santos.gui.application.LogicSantosApplication;

/**
 * @author Agostino
 *
 */
public class LoadingScreen implements IScreen, ScreenController, Controller {

	private NiftyJmeDisplay display=null;
	private Nifty nifty=null;
	private Element progressBarElement=null;
	private TextRenderer textRenderer=null;
	private boolean isLoading=true;
	private boolean assetsLoadingComplete=false;
	private boolean onStart=false;
	private LogicSantosApplication application=null;
	
	private static final String SCREEN_ID           = "loadlevel";
	private static final String LOAD_LEVEL_ID       = "loadlevel";
	private static final String PROGRESS_BAR_ID     = "progressbar";
	private static final String LOADING_TEXT_ID     = "loadingtext";
	private static final String LOADING_TEXT        = "Loading...";
	private static final String PREFIX_LOADING_TEXT = "Loading";
	
	
	public LoadingScreen( LogicSantosApplication application ) {
		this.application = application;
	}
	
	@Override
	public void initComponents() {
		display = new NiftyJmeDisplay( application.getAssetManager(), 
									   application.getInputManager(), 
									   application.getAudioRenderer(), 
									   application.getGuiViewPort() );
		nifty = display.getNifty();
		nifty.fromXml( ScreenConfig.LOADING_XML_GUI_NIFTY_NAME, SCREEN_ID, this );
		application.getGuiViewPort().addProcessor( display );
	}

	@Override
	public void finalizeComponents() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(float tpf) {
		// TODO Auto-generated method stub
		
	}
	
	public void setLoadingProgress( final float progress ) {
		final int MIN_WIDTH = 32;
		int pixelWidth = (int) ( MIN_WIDTH + ( progressBarElement.getParent(). 
								 			   getWidth() - MIN_WIDTH ) * progress );
		progressBarElement.setConstraintWidth( new SizeValue( pixelWidth + "px" ) );
		progressBarElement.getParent().layoutElements();
		
		isLoading = ( progress<1.0f );
	}
	
	public void setLoadingProgress( final float progress, final String loadingText ) {
		setLoadingProgress( progress );
		textRenderer.setText( loadingText );
	}
	
	public void showLoadingScreen() {
		nifty.gotoScreen( LOAD_LEVEL_ID );
	}
	
	public void hideLoadingScreen() {
		nifty.gotoScreen( "end" );
		nifty.exit();
		application.getGuiViewPort().removeProcessor( display );
	}
	
	public void onStart() {
		onStart = true;
		hideLoadingScreen();
	}
	
	public boolean isLoading() {
		return isLoading;
	}
	
	public boolean isAssetsLoadingComplete() {
		return assetsLoadingComplete;
	}
	
	public boolean isOnStart() {
		return onStart;
	}
	
	public void setAssetsLoadingComplete(boolean assetsLoadingComplete) {
		this.assetsLoadingComplete = assetsLoadingComplete;
	}
	
	public void setLoadingText( final String loadingText ) {
		textRenderer.setText( PREFIX_LOADING_TEXT + " : " + loadingText );
	}
	
	@Override
	public void bind(Nifty nifty, Screen screen) {
		progressBarElement = nifty.getScreen( LOAD_LEVEL_ID ).
							 findElementById( PROGRESS_BAR_ID );
		textRenderer = nifty.getScreen( LOAD_LEVEL_ID ).
					   findElementById( LOADING_TEXT_ID ).
					   getRenderer( TextRenderer.class );
		textRenderer.setText( LOADING_TEXT );
	}

	@Override
	public void onEndScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartScreen() {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void bind(Nifty nifty, Screen screen, Element element, Parameters prmtrs) {
		progressBarElement = element.findElementById( PROGRESS_BAR_ID );
		
	}

	@Override
	public void init(Parameters arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean inputEvent(NiftyInputEvent arg0) {
		return false;
	}

	@Override
	public void onFocus(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

}
