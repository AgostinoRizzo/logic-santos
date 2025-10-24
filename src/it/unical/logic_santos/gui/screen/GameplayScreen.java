/**
 * 
 */
package it.unical.logic_santos.gui.screen;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.ui.Picture;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import it.unical.logic_santos.controls.ControlsConfig;
import it.unical.logic_santos.gameplay.Cannon;
import it.unical.logic_santos.gameplay.Gun;
import it.unical.logic_santos.gameplay.IWeapon;
import it.unical.logic_santos.gameplay.LifeBar;
import it.unical.logic_santos.gameplay.PlayerCareerStatus;
import it.unical.logic_santos.gameplay.Rifle;
import it.unical.logic_santos.gui.application.GraphicalWantedStars;
import it.unical.logic_santos.gui.application.LogicSantosApplication;

/**
 * @author Agostino
 *
 */
public class GameplayScreen implements IScreen, ScreenController {

	private MinimapScreen minimapScreen=null;
	private WorldCalendarScreen worldCalendarScreen=null;
	private GraphicalWantedStars graphicalWantedStars=null;
	private WeaponChooserScreen weaponChooserScreen=null;
	
	private Picture currentWeaponPicture=null;
	private Class< ? extends IWeapon > currentWeaponClass=null;
	
	private Picture currentHealthBarPicture=null;
	private float currentHealthValue=10.0f;
	
	private Picture breathArmorBarsPicture=null;
	
	private Picture missionClearedPicture=null;
	private float currentTimeAmountMissionCleared=0.0f; // expressed in seconds
	
	private Picture wastedTextPicture=null;
	private float currentTimeAmountWastedText=0.0f; // expressed in seconds
	
	//private TextRenderer moneyTextRenderer=null;
	private BitmapText moneyText=null;
	private float currentMoneyValue=0.0f;
	
	private boolean isVisible=true;
	
	
	private NiftyJmeDisplay niftyDisplay=null;
	private Nifty nifty=null;
	private LogicSantosApplication application=null;
	
	static final String SCREEN_ID = "start";
	static final String INFORMATION_LAYER_ID = "informationLayer";
	static final String WEAPON_SCOPE_IMAGE_TAG_ID = "weaponScopeImage";
	
	//private static final String MONEY_ELEMENT_ID = "money";
	
	private static final int WEAPON_PIC_UP_MARGIN    = 30;  // expressed in pixels
	private static final int WEAPON_PIC_RIGHT_MARGIN = 180; // expressed in pixels
	
	private static final int HEALTH_BAR_PIC_UP_MARGIN    = 30+200;  // expressed in pixels
	private static final int HEALTH_BAR_PIC_RIGHT_MARGIN = 30; // expressed in pixels
	
	private static final int MONEY_TEXT_WIDTH        = 135; // expressed in pixels
	private static final int MONEY_TEXT_HEIGHT        = 10; // expressed in pixels
	private static final int MONEY_TEXT_UP_MARGIN    = 50; // expressed in pixels
	private static final int MONEY_TEXT_RIGHT_MARGIN = 30; // expressed in pixels
	
	private static final float MONEY_TEXT_Z_LEVEL = 1.0f;
	private static final float TEXT_SIZE = 32.0f;
	
	private static final float TIME_AMOUNT_MISSION_CLEARED_IMAGE_SHOW = 5.0f;
	
	
	public GameplayScreen( LogicSantosApplication application ) {
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
		nifty.fromXml( ScreenConfig.GAMEPLAY_WIDGET_XML_GUI_NIFTY_NAME, "start", this );
		application.getGuiViewPort().addProcessor( niftyDisplay );

		this.minimapScreen = new MinimapScreen( niftyDisplay, this.application );
		this.worldCalendarScreen = new WorldCalendarScreen( niftyDisplay, this.application);
		this.graphicalWantedStars = new GraphicalWantedStars( this.application, this.application.getPlayer().getWantedStars() );
		this.weaponChooserScreen = new WeaponChooserScreen( this.application );
		this.currentWeaponPicture = createNewWeaponPicture( this.application, true );
		this.currentHealthBarPicture = createNewHealthBarPicture( this.application, true );
		this.breathArmorBarsPicture = createNewBreathArmorBarsPicture( this.application, true );
		this.missionClearedPicture = createNewMissionClearedPicture( this.application, false );
		this.wastedTextPicture = createNewWastedTextPicture( this.application, false );
		this.currentWeaponClass = this.application.getPlayer().getWeapon().getClass();
		//createNewPlayerMoneyTextRenderer();
		createNewPlayerMoneyBitmapText();
		
		minimapScreen.initComponents();		
		worldCalendarScreen.initComponents();
		weaponChooserScreen.initComponents();
		
		nifty.gotoScreen( SCREEN_ID );
	}

	@Override
	public void finalizeComponents() {
		minimapScreen.finalizeComponents();
		worldCalendarScreen.finalizeComponents();
		weaponChooserScreen.finalizeComponents();
		application.getGuiNode().detachChild( moneyText );
		application.getGuiNode().detachChild( missionClearedPicture );
		application.getGuiNode().detachChild( wastedTextPicture );
	}

	@Override
	public void update(float tpf) {
		if ( isVisible ) {
			minimapScreen.update(tpf);
			worldCalendarScreen.update(tpf);
			updateWeaponPicture();
			updateHealthBarPicture();
			weaponChooserScreen.update(tpf);
			updateMoneyText();
			updateMissionClearedImageVisualization(tpf);
			updateWastedTextImageVisualization(tpf);
		}
	}
	
	public MinimapScreen getMinimapScreen() {
		return minimapScreen;
	}
	
	public GraphicalWantedStars getGraphicalWantedStars() {
		return graphicalWantedStars;
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
	
	public WeaponChooserScreen getWeaponChooserScreen() {
		return weaponChooserScreen;
	}
	
	public void onMenuShow() {
		minimapScreen.onMenuShow();
		graphicalWantedStars.onMenuShow();
		weaponChooserScreen.onMenuShow();
		application.getGuiNode().detachChild( currentWeaponPicture );
		application.getGuiNode().detachChild( currentHealthBarPicture );
		application.getGuiNode().detachChild( breathArmorBarsPicture );
		application.getGuiNode().detachChild( missionClearedPicture );
		application.getGuiNode().detachChild( wastedTextPicture );
		application.getGuiViewPort().removeProcessor( niftyDisplay );
		isVisible = false;
	}
	
	public void onMenuHide() {
		application.getGuiNode().attachChild( currentWeaponPicture );
		application.getGuiNode().attachChild( currentHealthBarPicture );
		application.getGuiNode().attachChild( breathArmorBarsPicture );
		minimapScreen.onMenuHide();
		graphicalWantedStars.onMenuHide();
		weaponChooserScreen.onMenuHide();
		application.getGuiViewPort().addProcessor( niftyDisplay );
		isVisible = true;
	}
	
	public void onMissionCleared() {
		application.getGuiNode().attachChild( missionClearedPicture );
		currentTimeAmountMissionCleared=0.0f;
	}
	
	public void onWastedText() {
		application.getGuiNode().attachChild( wastedTextPicture );
		currentTimeAmountWastedText=0.0f;
	}
	
	public void onWeaponScopeShow() {
		Element weaponScopeImageElement = nifty.getScreen( SCREEN_ID ).findElementById( WEAPON_SCOPE_IMAGE_TAG_ID );
		weaponScopeImageElement.setVisible( true );
	}
	
	public void onWeaponScopeHide() {
		Element weaponScopeImageElement = nifty.getScreen( SCREEN_ID ).findElementById( WEAPON_SCOPE_IMAGE_TAG_ID );
		weaponScopeImageElement.setVisible( false );
	}
	
	private void updateWeaponPicture() {
		final IWeapon actualPlayerWeapon = application.getPlayer().getWeapon();
		Class< ? extends IWeapon > actualPlayerWeaponClass = null;
		if ( actualPlayerWeapon!=null )
			actualPlayerWeaponClass = actualPlayerWeapon.getClass();
		if ( this.currentWeaponClass!=actualPlayerWeaponClass ) {
			this.currentWeaponPicture.setImage( application.getAssetManager(), getWeaponImageNameFromClass( actualPlayerWeaponClass ), true );
			this.currentWeaponClass = actualPlayerWeaponClass;
		}
	}
	
	private void updateHealthBarPicture() {
		final float actualHealthBarValue = application.getPlayer().getHumanPhysicalActivity().getLifeBar().getValue();
		if ( this.currentHealthValue!=actualHealthBarValue ) {
			this.currentHealthBarPicture.setImage( application.getAssetManager(), 
												   getHealthBarImageNameFromBar( application.getPlayer().getHumanPhysicalActivity().getLifeBar() ), 
												   true );
			this.currentHealthValue = actualHealthBarValue;
		}
	}
	
	private void updateMoneyText() {
		final float actualMoneyValue = application.getPlayerManager().getPlayerCareerStatus().getMoney();
		if ( this.currentMoneyValue!=actualMoneyValue ) {
			//this.moneyTextRenderer.setText( PlayerCareerStatus.toStringMoney( actualMoneyValue ) );
			this.moneyText.setText( PlayerCareerStatus.toStringMoney( actualMoneyValue ) );
			this.currentMoneyValue = actualMoneyValue;
		}
	}
	
	private void updateMissionClearedImageVisualization( final float tpf ) {
		if ( currentTimeAmountMissionCleared<0.0f )
			return;
		currentTimeAmountMissionCleared+=tpf;
		if ( currentTimeAmountMissionCleared>=TIME_AMOUNT_MISSION_CLEARED_IMAGE_SHOW ) {
			application.getGuiNode().detachChild( missionClearedPicture );
			currentTimeAmountMissionCleared=(-1.0f);
		}
	}
	
	private void updateWastedTextImageVisualization( final float tpf ) {
		if ( currentTimeAmountWastedText<0.0f )
			return;
		currentTimeAmountWastedText+=tpf;
		if ( currentTimeAmountWastedText>=TIME_AMOUNT_MISSION_CLEARED_IMAGE_SHOW ) {
			application.getGuiNode().detachChild( wastedTextPicture );
			currentTimeAmountWastedText=(-1.0f);
		}
	}
	
	/*private void createNewPlayerMoneyTextRenderer() {
		Element moneyTextElement = this.nifty.getScreen( SCREEN_ID ).findElementById( INFORMATION_LAYER_ID ).findElementById( MONEY_ELEMENT_ID );
		this.moneyTextRenderer = moneyTextElement.getRenderer( TextRenderer.class ); 
		
		final float actualMoneyValue = application.getPlayerManager().getPlayerCareerStatus().getMoney();
		this.moneyTextRenderer.setText( PlayerCareerStatus.toStringMoney( actualMoneyValue ) );
		this.currentMoneyValue = actualMoneyValue;
		
		moneyTextElement.setConstraintX( new SizeValue( this.application.getSettings().getWidth()-MONEY_TEXT_RIGHT_MARGIN-MONEY_TEXT_WIDTH, SizeValueType.Pixel ) );
		moneyTextElement.setConstraintY( new SizeValue( MONEY_TEXT_UP_MARGIN, SizeValueType.Pixel ) );
	}*/
	
	private void createNewPlayerMoneyBitmapText() {
		/* initialization Picture */
		BitmapFont font = application.getAssetManager().loadFont( ScreenConfig.TEXT_FONT_NAME );
		this.moneyText = new BitmapText( font, true );
		this.moneyText.setSize( TEXT_SIZE );
		
		this.moneyText.setLocalTranslation(
				application.getSettings().getWidth()-MONEY_TEXT_RIGHT_MARGIN-MONEY_TEXT_WIDTH, 
				application.getSettings().getHeight()-MONEY_TEXT_UP_MARGIN-MONEY_TEXT_HEIGHT, 
				MONEY_TEXT_Z_LEVEL 
				);
		
		final float actualMoneyValue = application.getPlayerManager().getPlayerCareerStatus().getMoney();
		this.moneyText.setText( PlayerCareerStatus.toStringMoney( actualMoneyValue ) );
		this.currentMoneyValue = actualMoneyValue;
		
		application.getGuiNode().attachChild( moneyText );
	}
	
	private static Picture createNewWeaponPicture( LogicSantosApplication application, final boolean attachToGraphicNode ) {
		Picture pic = new Picture( "HUD Picture" );
		pic.setImage( application.getAssetManager(), getWeaponImageNameFromClass( application.getPlayer().getWeapon().getClass() ), true );
		pic.setWidth( ControlsConfig.WEAPON_IMAGE_WIDTH );
		pic.setHeight( ControlsConfig.WEAPON_IMAGE_HEIGTH );
		
		pic.setPosition( 
				application.getSettings().getWidth()-WEAPON_PIC_RIGHT_MARGIN-ControlsConfig.WEAPON_IMAGE_WIDTH, 
				application.getSettings().getHeight()-WEAPON_PIC_UP_MARGIN-ControlsConfig.WEAPON_IMAGE_HEIGTH 
				);
		
		if ( attachToGraphicNode )
			application.getGuiNode().attachChild( pic );
		
		return pic;
	}
	
	private static Picture createNewHealthBarPicture( LogicSantosApplication application, final boolean attachToGraphicNode ) {
		Picture pic = new Picture( "HUD Picture" );
		pic.setImage( application.getAssetManager(), 
					  getHealthBarImageNameFromBar( application.getPlayer().getHumanPhysicalActivity().getLifeBar() ),
					  true );
		pic.setWidth( ControlsConfig.HEALTH_BAR_WIDTH );
		pic.setHeight( ControlsConfig.HEALTH_BAR_HEIGTH );
		
		pic.setPosition( 
				application.getSettings().getWidth()-HEALTH_BAR_PIC_RIGHT_MARGIN-ControlsConfig.HEALTH_BAR_WIDTH, 
				application.getSettings().getHeight()-HEALTH_BAR_PIC_UP_MARGIN-ControlsConfig.HEALTH_BAR_HEIGTH 
				);
		
		if ( attachToGraphicNode )
			application.getGuiNode().attachChild( pic );
		
		return pic;
	}
	
	private static Picture createNewBreathArmorBarsPicture( LogicSantosApplication application, final boolean attachToGraphicNode ) {
		Picture pic = new Picture( "HUD Picture" );
		pic.setImage( application.getAssetManager(), 
					  ControlsConfig.BREATH_ARMOR_BARS_IMAGE_NAME,
					  true );
		pic.setWidth( ControlsConfig.BREATH_ARMOR_BARS_WIDTH );
		pic.setHeight( ControlsConfig.BREATH_ARMOR_BARS_HEIGTH );
		
		pic.setPosition( 
				application.getSettings().getWidth()-HEALTH_BAR_PIC_RIGHT_MARGIN-ControlsConfig.BREATH_ARMOR_BARS_WIDTH, 
				application.getSettings().getHeight()-HEALTH_BAR_PIC_UP_MARGIN-ControlsConfig.HEALTH_BAR_HEIGTH-ControlsConfig.BREATH_ARMOR_BARS_HEIGTH 
				);
		
		if ( attachToGraphicNode )
			application.getGuiNode().attachChild( pic );
		
		return pic;
	}
	
	private static Picture createNewMissionClearedPicture( LogicSantosApplication application, final boolean attachToGraphicNode ) {
		Picture pic = new Picture( "HUD Picture" );
		pic.setImage( application.getAssetManager(), 
					  ControlsConfig.MISSION_CLEARED_IMAGE_NAME,
					  true );
		pic.setWidth( ControlsConfig.MISSION_CLEARED_WIDTH );
		pic.setHeight( ControlsConfig.MISSION_CLEARED_HEIGTH );
		
		pic.setPosition( 
				(application.getSettings().getWidth()/2)-(ControlsConfig.MISSION_CLEARED_WIDTH/2), 
				(application.getSettings().getHeight()/2)-(ControlsConfig.MISSION_CLEARED_HEIGTH/2)
				);
		
		if ( attachToGraphicNode )
			application.getGuiNode().attachChild( pic );
		
		return pic;
	}
	
	private static Picture createNewWastedTextPicture( LogicSantosApplication application, final boolean attachToGraphicNode ) {
		Picture pic = new Picture( "HUD Picture" );
		pic.setImage( application.getAssetManager(), 
					  ControlsConfig.WASTED_TEXT_IMAGE_NAME,
					  true );
		pic.setWidth( ControlsConfig.WASTED_TEXT_WIDTH );
		pic.setHeight( ControlsConfig.WASTED_TEXT_HEIGTH );
		
		pic.setPosition( 
				(application.getSettings().getWidth()/2)-(ControlsConfig.WASTED_TEXT_WIDTH/2), 
				(application.getSettings().getHeight()/2)-(ControlsConfig.WASTED_TEXT_HEIGTH/2)
				);
		
		if ( attachToGraphicNode )
			application.getGuiNode().attachChild( pic );
		
		return pic;
	}
	
	private static String getWeaponImageNameFromClass( final Class< ? extends IWeapon > weaponClass ) {
		if ( weaponClass==null )
			return ControlsConfig.UNARMED_IMAGE_NAME;
		if ( weaponClass==Gun.class )
			return ControlsConfig.GUN_IMAGE_NAME;
		if ( weaponClass==Rifle.class )
			return ControlsConfig.RIFLE_IMAGE_NAME;
		if ( weaponClass==Cannon.class )
			return ControlsConfig.CANNON_IMAGE_NAME;
		
		return ControlsConfig.UNARMED_IMAGE_NAME;
	}
	
	private static String getHealthBarImageNameFromBar( final LifeBar bar ) {
		if ( bar.isFull() )
			return ControlsConfig.HEALTH_BAR_FULL_IMAGE_NAME;
		else if ( bar.isEmpty() )
			return ControlsConfig.HEALTH_BAR_EMPTY_IMAGE_NAME;
		
		final float value = bar.getValue();
		if ( value>0.5 )
			return ControlsConfig.HEALTH_BAR_75_IMAGE_NAME;
		else if ( value>0.25 )
			return ControlsConfig.HEALTH_BAR_50_IMAGE_NAME;
		else if ( value>0.1 )
			return ControlsConfig.HEALTH_BAR_25_IMAGE_NAME;
		else
			return ControlsConfig.HEALTH_BAR_10_IMAGE_NAME;
	}

	
}
