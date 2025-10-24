/**
 * 
 */
package it.unical.logic_santos.gui.application;



import com.jme3.ui.Picture;

import it.unical.logic_santos.controls.ControlsConfig;
import it.unical.logic_santos.gameplay.IObserver;
import it.unical.logic_santos.gameplay.ISubject;
import it.unical.logic_santos.gameplay.WantedStars;

/**
 * @author Agostino
 *
 */
public class GraphicalWantedStars implements IObserver {

	private WantedStars target=null;
	private LogicSantosApplication application=null;
	
	private Picture[] starPictures=null;
	private boolean[] starPictureState=null;
	
	private static final int SPACE_BETWEEN_STARS = 0;
	private static final int RIGHT_MARGIN = 30;
	private static final int TOP_MARGIN = 150;
	
	
	public GraphicalWantedStars( LogicSantosApplication application, WantedStars target ) {
		this.target = target;
		this.application = application;
		this.starPictures = new Picture[WantedStars.MAX_WANTED_VALUE];
		this.starPictureState = new boolean[WantedStars.MAX_WANTED_VALUE];
		initStarPictures( this.starPictures, this.starPictureState, this.application );
		this.target.addObserver(this);
	}

	@Override
	public void onStateShanged() {
		boolean[] starStates = target.getStarsStates();
		
		for( int i=0; i<starStates.length; ++i) {
			if ( starStates[i] == WantedStars.EMPTY_STAR )
				starPictures[i].setImage(application.getAssetManager(), ControlsConfig.EMPTY_WANTED_STAR_IMAGE_NAME, true);
			else if ( starStates[i] == WantedStars.FULL_STAR )
				starPictures[i].setImage(application.getAssetManager(), ControlsConfig.FULL_WANTED_STAR_IMAGE_NAME, true);
		}
		
	}
	
	@Override
	public void onStateShanged(ISubject subject) {
		onStateShanged();
	}
	
	public void onMenuShow() {
		//for( int i=0; i<starPictures.length; ++i )
		//	application.getGuiNode().detachChild( starPictures[i] );
	}
	
	public void onMenuHide() {
		//for( int i=0; i<starPictures.length; ++i )
		//	application.getGuiNode().attachChild( starPictures[i] );
	}
	
	private static void initStarPictures( Picture[] starPictures, boolean[] starPicturesState, LogicSantosApplication application ) {
		for(int i=0; i<starPictures.length; ++i) {
			
			Picture picStar = new Picture("HUD Picture");
			picStar.setImage(application.getAssetManager(), ControlsConfig.EMPTY_WANTED_STAR_IMAGE_NAME, true);
			picStar.setWidth(ControlsConfig.WANTED_STAR_WIDTH);
			picStar.setHeight(ControlsConfig.WANTED_STAR_HEIGTH);
			picStar.setPosition( application.getSettings().getWidth() - 
					( RIGHT_MARGIN + (ControlsConfig.WANTED_STAR_WIDTH*(starPictures.length-i)) + (SPACE_BETWEEN_STARS*(starPictures.length-i+1)) ),
					application.getSettings().getHeight() - TOP_MARGIN);
			starPictures[i] = picStar;
			starPicturesState[i] = WantedStars.EMPTY_STAR;
			application.getGuiNode().attachChild( picStar );
		}
	}
	
	
}
