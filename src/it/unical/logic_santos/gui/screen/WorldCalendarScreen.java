/**
 * 
 */
package it.unical.logic_santos.gui.screen;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.niftygui.NiftyJmeDisplay;

import it.unical.logic_santos.environment.Calendar;
import it.unical.logic_santos.gui.application.LogicSantosApplication;

/**
 * @author Agostino
 *
 */
public class WorldCalendarScreen implements IScreen {

	private Calendar calendar=null;
	//private NiftyJmeDisplay niftyDisplay=null;
	//private Nifty nifty=null;
	private LogicSantosApplication application=null;
	
	//private Screen screen=null;
	//private Element informationLayer=null;
	//private Element calendarnElement=null;
	//private TextRenderer calendarTextRenderer=null;
	private BitmapText clockText=null;
	//private static final String CALENDAR_ELEMENT_ID = "calendar";
	
	private static final int CALENDAR_TEXT_WIDTH        = 100; // expressed in pixels
	private static final int CALENDAR_TEXT_HEIGHT       = 10; // expressed in pixels
	private static final int CALENDAR_TEXT_UP_MARGIN    = 10; // expressed in pixels
	private static final int CALENDAR_TEXT_RIGHT_MARGIN = 30; // expressed in pixels
	
	private static final float CALENDAR_TEXT_Z_LEVEL = 1.0f;
	private static final float TEXT_SIZE = 32.0f;
	
	public WorldCalendarScreen( NiftyJmeDisplay niftyDisplay, LogicSantosApplication application ) {
		this.application = application;
		this.calendar = application.getLogicSantosWorld().getCalendar();
		//this.niftyDisplay = niftyDisplay;
		//this.nifty = this.niftyDisplay.getNifty();
	}
	
	@Override
	public void initComponents() {
		/*screen = nifty.getScreen( GameplayScreen.SCREEN_ID );
		
		informationLayer = screen.findElementById( GameplayScreen.INFORMATION_LAYER_ID );
		
		calendarnElement = informationLayer.findElementById( CALENDAR_ELEMENT_ID ); 
		calendarTextRenderer = calendarnElement.getRenderer( TextRenderer.class );
		//initCalendarElement(); 
		
		nifty.gotoScreen( GameplayScreen.SCREEN_ID );*/
		
		/* initialization Picture */
		BitmapFont font = application.getAssetManager().loadFont( ScreenConfig.TEXT_FONT_NAME );
		this.clockText = new BitmapText( font, true );
		this.clockText.setSize( TEXT_SIZE );
		this.clockText.setLocalTranslation(
				application.getSettings().getWidth()-CALENDAR_TEXT_RIGHT_MARGIN-CALENDAR_TEXT_WIDTH, 
				application.getSettings().getHeight()-CALENDAR_TEXT_UP_MARGIN-CALENDAR_TEXT_HEIGHT, 
				CALENDAR_TEXT_Z_LEVEL 
				);
		updateCalendarElement();
		application.getGuiNode().attachChild( clockText );
		
	}

	@Override
	public void finalizeComponents() {
		application.getGuiNode().detachChild( clockText );
	}

	@Override
	public void update(float tpf) {
		updateCalendarElement();
	}
	
	/*private void initCalendarElement() {
		calendarnElement.setConstraintX( 
				new SizeValue( application.getSettings().getWidth()-CALENDAR_TEXT_RIGHT_MARGIN-CALENDAR_TEXT_WIDTH, SizeValueType.Pixel )
				);
		calendarnElement.setConstraintY( 
				new SizeValue( CALENDAR_TEXT_UP_MARGIN, SizeValueType.Pixel )
				);
		updateCalendarElement();
	}*/
	
	private void updateCalendarElement() {
		//calendarTextRenderer.setText( calendar.getHashClock() );
		final String actualText = calendar.stringifyClock();
		if ( clockText.getText()!=actualText )
			clockText.setText( actualText );
	}

}
