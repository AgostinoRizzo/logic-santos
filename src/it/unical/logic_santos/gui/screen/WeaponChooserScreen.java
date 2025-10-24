/**
 * 
 */
package it.unical.logic_santos.gui.screen;

import java.util.ArrayList;

import com.jme3.scene.Node;
import com.jme3.ui.Picture;

import it.unical.logic_santos.controls.ControlsConfig;
import it.unical.logic_santos.gameplay.Cannon;
import it.unical.logic_santos.gameplay.Gun;
import it.unical.logic_santos.gameplay.IWeapon;
import it.unical.logic_santos.gameplay.Rifle;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.net.IRemoteCommunicator;
import it.unical.logic_santos.net.PlayerAction;
import it.unical.logic_santos.spatial_entity.Player;

/**
 * @author Agostino
 *
 */
public class WeaponChooserScreen implements IScreen {
	
	private Picture gunSelectionWheelPicture=null;
	private Picture rifleSelectionWheelPicture=null;
	private Picture cannonSelectionWheelPicture=null;
	private Picture unarmedSelectionWheelPicture=null;
	
	private Picture gunFeaturesPicture=null;
	private Picture rifleFeaturesPicture=null;
	private Picture cannonFeaturesPicture=null;
	
	private boolean visible=false;
	private ArrayList< Class< ? extends IWeapon > > weaponSet=null; 
	private int iSelectedWeapon=0;
	
	private Node guiNode=null;
	private LogicSantosApplication application=null;
	
	public WeaponChooserScreen( LogicSantosApplication application ) {
		this.application = application;
		this.weaponSet = getWeaponSet();
		this.iSelectedWeapon=0;
	}

	@Override
	public void initComponents() {
		this.guiNode = new Node();
		this.application.getGuiNode().attachChild( this.guiNode );
		
		this.gunSelectionWheelPicture    =initSelectionWheelPicture( Gun.class, this.application, null );
		this.rifleSelectionWheelPicture  =initSelectionWheelPicture( Rifle.class, this.application, null );
		this.cannonSelectionWheelPicture =initSelectionWheelPicture( Cannon.class, this.application, null );
		this.unarmedSelectionWheelPicture=initSelectionWheelPicture( null, this.application, null );
		
		this.gunFeaturesPicture    =initFeaturesPicture( Gun.class, this.application, this.guiNode );
		this.rifleFeaturesPicture  =initFeaturesPicture( Rifle.class, this.application, this.guiNode );
		this.cannonFeaturesPicture =initFeaturesPicture( Cannon.class, this.application, this.guiNode );
		
		IWeapon playerWeapon = this.application.getPlayer().getWeapon();
		if ( playerWeapon!=null )
			updateGraphicalWeaponFeaturesPicture( getWeaponFeaturesPicture( playerWeapon.getClass() ) );
	}

	@Override
	public void finalizeComponents() {
		guiNode.detachAllChildren();
		this.application.getGuiNode().detachChild( this.guiNode );
	}

	@Override
	public void update(float tpf) {
		// TODO Auto-generated method stub
		
	}
	
	public void chooseSelectedWeapon() {
		Player player = application.getPlayer();
		IWeapon currentPlayerWeapon = player.getWeapon();
		
		if ( (currentPlayerWeapon==null) || (currentPlayerWeapon.getClass() != weaponSet.get( iSelectedWeapon )) ) {
			IWeapon newWeapon=null;
			if ( weaponSet.get( iSelectedWeapon )!=null ) {
				
				try {
					
					newWeapon=weaponSet.get( iSelectedWeapon ).newInstance();
					newWeapon.setOwner( player );
					newWeapon.setApplication( application );
					
				} catch (InstantiationException | IllegalAccessException e) {
					newWeapon=null;
					e.printStackTrace();
				}
			}
			
			player.setWeapon( newWeapon );
			
			IRemoteCommunicator remoteCommunicator = application.getRemoteCommunicator();
			if ( (remoteCommunicator!=null) && (!remoteCommunicator.isMaster()) && (remoteCommunicator.isConnected()) ) {
				PlayerAction playerAction = new PlayerAction( PlayerAction.PLAYER_CHOOSE_WEAPON );
				playerAction.weaponType = IWeapon.getWeaponId( newWeapon );
				remoteCommunicator.onPlayerAction( playerAction );
			}
		}
	}
	
	public void chooseSelectedWeapon( final boolean chooseAndHide ) {
		chooseSelectedWeapon();
		if ( chooseAndHide )
			hideChooser();
	}
	
	public void showChooser() {
		if ( visible )
			return;
		visible=true;
		updateGraphicalChoosePicture();
	}
	
	public void hideChooser() {
		if ( !visible )
			return;
		this.guiNode.detachAllChildren();
		visible=false;
		
		IWeapon playerWeapon = this.application.getPlayer().getWeapon();
		if ( playerWeapon!=null )
			updateGraphicalWeaponFeaturesPicture( getWeaponFeaturesPicture( playerWeapon.getClass() ) );
	}
	
	public void onChooserRequest() {
		if ( !visible )
			showChooser();
		else
			hideChooser();
	}
	
	public void selectNextWeapon() {
		iSelectedWeapon++;
		if ( iSelectedWeapon>=weaponSet.size() )
			iSelectedWeapon=0;
		updateGraphicalChoosePicture();
	}
	
	public void selectPreviousWeapon() {
		iSelectedWeapon--;
		if ( iSelectedWeapon<0 )
			iSelectedWeapon=(weaponSet.size()-1);
		updateGraphicalChoosePicture();
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void onMenuShow() { 
		hideChooser();
		Picture currentWeaponFeaturesPicture = getCurrentWeaponFeaturesPicture();
		if ( currentWeaponFeaturesPicture!=null )
			this.guiNode.detachChild( currentWeaponFeaturesPicture );
	}
	
	public void onMenuHide() {
		Picture currentWeaponFeaturesPicture = getCurrentWeaponFeaturesPicture();
		if ( currentWeaponFeaturesPicture!=null )
			this.guiNode.attachChild( currentWeaponFeaturesPicture );
	}
	
	private void updateGraphicalChoosePicture() {
		if ( !visible )
			return;
		this.guiNode.detachAllChildren();
		this.guiNode.attachChild( getCurrentWeaponSelectionWheelPicture() );
		
		updateGraphicalWeaponFeaturesPicture( getCurrentWeaponFeaturesPicture() );
	}
	
	private void updateGraphicalWeaponFeaturesPicture( Picture currentWeaponFeaturesPic ) {
		this.guiNode.detachChild( this.gunFeaturesPicture );
		this.guiNode.detachChild( this.rifleFeaturesPicture );
		this.guiNode.detachChild( this.cannonFeaturesPicture );
		
		if ( currentWeaponFeaturesPic!=null )
			this.guiNode.attachChild( currentWeaponFeaturesPic );
	}
	
	private Picture getCurrentWeaponSelectionWheelPicture() {
		final Class< ? extends IWeapon > selectedWeaponClass =
				weaponSet.get( iSelectedWeapon );
		
		if ( selectedWeaponClass==null )
			return unarmedSelectionWheelPicture;
		if ( selectedWeaponClass==Gun.class )
			return gunSelectionWheelPicture;
		if ( selectedWeaponClass==Rifle.class )
			return rifleSelectionWheelPicture;
		if ( selectedWeaponClass==Cannon.class )
			return cannonSelectionWheelPicture;
		return null;
	}
	
	private Picture getCurrentWeaponFeaturesPicture() {
		final Class< ? extends IWeapon > selectedWeaponClass =
				weaponSet.get( iSelectedWeapon );
		
		return getWeaponFeaturesPicture( selectedWeaponClass );
	}
	
	private Picture getWeaponFeaturesPicture( final Class< ? extends IWeapon > selectedWeaponClass ) {
		if ( selectedWeaponClass==Gun.class )
			return gunFeaturesPicture;
		if ( selectedWeaponClass==Rifle.class )
			return rifleFeaturesPicture;
		if ( selectedWeaponClass==Cannon.class )
			return cannonFeaturesPicture;
		return null;
	}
	
	private static Picture initSelectionWheelPicture( final Class< ? extends IWeapon > weaponClass, LogicSantosApplication application, Node guiNode ) {
		Picture pic = new Picture( "HUD Picture" );
		pic.setImage( application.getAssetManager(), getSelectionWheelImageNameFromWeaponClass( weaponClass ), true );
		pic.setWidth( ControlsConfig.WEAPON_SELECTION_WHEEL_IMAGE_WIDTH );
		pic.setHeight( ControlsConfig.WEAPON_SELECTION_WHEEL_IMAGE_HEIGTH );
		
		pic.setPosition( 
				(application.getSettings().getWidth()/2) - (ControlsConfig.WEAPON_SELECTION_WHEEL_IMAGE_WIDTH/2), 
				(application.getSettings().getHeight()/2) - (ControlsConfig.WEAPON_SELECTION_WHEEL_IMAGE_HEIGTH/2) 
				);
		
		if ( guiNode!=null )
			guiNode.attachChild( pic );
		
		return pic;
	}
	
	private static Picture initFeaturesPicture( final Class< ? extends IWeapon > weaponClass, LogicSantosApplication application, Node guiNode ) {
		Picture pic = new Picture( "HUD Picture" );
		pic.setImage( application.getAssetManager(), getFeaturesImageNameFromWeaponClass( weaponClass ), true );
		pic.setWidth( ControlsConfig.WEAPON_FEATURES_IMAGE_WIDTH );
		pic.setHeight( ControlsConfig.WEAPON_FEATURES_IMAGE_HEIGTH );
		
		pic.setPosition( 
				ControlsConfig.WEAPON_FEATURES_IMAGE_LEFT_MARGIN, 
				application.getSettings().getHeight() - ControlsConfig.WEAPON_FEATURES_IMAGE_TOP_MARGIN - ControlsConfig.WEAPON_IMAGE_HEIGTH
				);
		
		if ( guiNode!=null )
			guiNode.attachChild( pic );
		
		return pic;
	}
	
	private static String getSelectionWheelImageNameFromWeaponClass( final Class< ? extends IWeapon > weaponClass ) {
		if ( weaponClass==null )
			return ControlsConfig.UNARMED_SELECTION_WHEEL_IMAGE_NAME;
		if ( weaponClass==Gun.class )
			return ControlsConfig.GUN_SELECTION_WHEEL_IMAGE_NAME;
		if ( weaponClass==Rifle.class )
			return ControlsConfig.RIFLE_SELECTION_WHEEL_IMAGE_NAME;
		if ( weaponClass==Cannon.class )
			return ControlsConfig.CANNON_SELECTION_WHEEL_IMAGE_NAME;
		return null;
	}
	
	private static String getFeaturesImageNameFromWeaponClass( final Class< ? extends IWeapon > weaponClass ) {
		if ( weaponClass==Gun.class )
			return ControlsConfig.GUN_FEATURES_IMAGE_NAME;
		if ( weaponClass==Rifle.class )
			return ControlsConfig.RIFLE_FEATURES_IMAGE_NAME;
		if ( weaponClass==Cannon.class )
			return ControlsConfig.CANNON_FEATURES_IMAGE_NAME;
		return null;
	}
	
	private static ArrayList< Class< ? extends IWeapon > > getWeaponSet() {
		ArrayList< Class< ? extends IWeapon > > weaponSet = new ArrayList< Class< ? extends IWeapon > >(4);
		weaponSet.add( Gun.class );
		weaponSet.add( Rifle.class );
		weaponSet.add( null );
		weaponSet.add( Cannon.class );
		return weaponSet;
	}

}
