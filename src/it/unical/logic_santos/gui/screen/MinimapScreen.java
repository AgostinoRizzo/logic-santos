/**
 * 
 */
package it.unical.logic_santos.gui.screen;

import java.util.HashMap;
import java.util.List;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.nifty.tools.SizeValueType;
import it.unical.logic_santos.gui.application.LogicSantosApplication;

/**
 * @author Agostino
 *
 */
public class MinimapScreen implements IScreen {
	
	private LogicSantosApplication application=null;
	//private Node guiNode=null;
	private Node miniMapNode=null;
	private NiftyJmeDisplay niftyDisplay=null;
	private Nifty nifty=null;
	
	private Screen screen=null;
	private Element indicatorsLayer=null;
	private Element imageMapElement=null;
	private ImageRenderer imageMapRenderer=null;
	private Picture mainPlayerPicture=null;
	
	private Integer[] subImageStartPoint=null;
	
	private HashMap< IVisibleEntity, Element > visibleEntitiesImagesMap=null;
	private HashMap< Element, IVisibleEntity > elementsVisibleEntityImagesMap=null;
	
	private static final String MINIMAP_IMAGE_PATH = "Screen/map.jpg"; // TODO: move to config class
	private static final String LAYER_ID = "layer";
	private static final String INDICATORS_LAYER_ID = "indicatorsLayer";
	private static final String MINIMAP_IMAGE_ID = "map";
	
	private static final Integer X_MINIMAP_MARGIN = 30; // expressed in pixels
	private static final Integer Y_MINIMAP_MARGIN = 100; // expressed in pixels
	
	private static final Integer WIDTH_MINIMAP  = 300; // expressed in pixels
	private static final Integer HEIGHT_MINIMAP = 200; // expressed in pixels
	private static final Integer HALF_WIDTH_MINIMAP  = WIDTH_MINIMAP/2;  // expressed in pixels
	private static final Integer HALF_HEIGHT_MINIMAP = HEIGHT_MINIMAP/2; // expressed in pixels
	
	private static final Integer WIDTH_MINIMAP_IMAGE  = 2526; // expressed in pixels
	private static final Integer HEIGHT_MINIMAP_IMAGE = 3284; // expressed in pixels
	
	private static final int X_COORD = 0;
	private static final int Y_COORD = 1;
	
	public MinimapScreen( NiftyJmeDisplay niftyDisplay, LogicSantosApplication application ) {
		this.application = application;
		this.niftyDisplay = niftyDisplay;
		this.nifty = this.niftyDisplay.getNifty();
		//this.guiNode = this.application.getGuiNode();
		this.miniMapNode = new Node();
		this.visibleEntitiesImagesMap = new HashMap< IVisibleEntity, Element >();
		this.elementsVisibleEntityImagesMap = new HashMap< Element, IVisibleEntity >();
		this.subImageStartPoint = createNewInitialMinimapImageStartPoint();
		this.mainPlayerPicture = createNewMainPlayerPicture( this.application.getPlayer(), this.application );
	}

	@Override
	public void initComponents() {
		screen = nifty.getScreen( GameplayScreen.SCREEN_ID );
		
		indicatorsLayer = screen.findElementById( INDICATORS_LAYER_ID );
		imageMapElement = initMapImagePosition( nifty, application );
		imageMapRenderer = imageMapElement.getRenderer( ImageRenderer.class ); 
		nifty.gotoScreen( GameplayScreen.SCREEN_ID );
		
	}

	@Override
	public void finalizeComponents() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(float tpf) {
		final Vector3f player3dPosition = application.getPlayer().getHumanPhysicalActivity().getControl().getPhysicsLocation();
		final Vector2f playerPosition = new Vector2f( player3dPosition.getX(), player3dPosition.getZ() );
		
		/* centralize the minimap image w.r.t. player position */
		final Integer[] playerCoords = convertFromWorldCoordsToPixelsCoords( playerPosition );
		centralizeMinimapImage( playerCoords );
		
		/* show spatial entities indicator */
		updateVisibleEntityIndicators();
		updateMainPlayerIndicator();
		
		nifty.gotoScreen( GameplayScreen.SCREEN_ID );
	}
	
	public void addVisibleEntity( IVisibleEntity entity ) {
		if ( !visibleEntitiesImagesMap.containsKey(entity) ) {
			Element indicatorImageElement = createNewIndicatorImageElement( entity );
			visibleEntitiesImagesMap.put( entity, indicatorImageElement );
			elementsVisibleEntityImagesMap.put( indicatorImageElement, entity ); 
		}
	}
	
	public void removeVisibleEntity( IVisibleEntity entity ) {
		if ( visibleEntitiesImagesMap.containsKey(entity) ) {
			Element indicatorImageElement = visibleEntitiesImagesMap.get(entity); 
			removeIndicatorImageElement( indicatorImageElement );
			visibleEntitiesImagesMap.remove(entity);
			elementsVisibleEntityImagesMap.remove( indicatorImageElement );
		}
	}
	
	public void onMenuShow() { 
		application.getGuiNode().detachChild( miniMapNode );
		application.getGuiNode().detachChild( mainPlayerPicture );
	}
	
	public void onMenuHide() {
		application.getGuiNode().attachChild( miniMapNode );
		application.getGuiNode().attachChild( mainPlayerPicture );
	}
	
	
	/*private static Picture createMapPicture( LogicSantosApplication application ) {
		Picture pic = new Picture("HUD Picture");
		pic.setImage(application.getAssetManager(), ControlsConfig.TARGET_POINTER_IMAGE_NAME, true);
		pic.setWidth(ControlsConfig.TARGET_POINTER_WIDTH);
		pic.setHeight(ControlsConfig.TARGET_POINTER_HEIGTH);
		return pic;
	}*/
	
	private static Element initMapImagePosition( Nifty nifty, LogicSantosApplication application ) {
		Screen screen = nifty.getScreen( GameplayScreen.SCREEN_ID );
		Element layer = screen.findElementById( LAYER_ID );
		
		final Integer Y = application.getSettings().getHeight()-HEIGHT_MINIMAP-Y_MINIMAP_MARGIN;
		
		ImageBuilder builder = new ImageBuilder() {{
			
			id( MINIMAP_IMAGE_ID );
			filename( MINIMAP_IMAGE_PATH );
			imageMode( "subImage:0,0,"  + WIDTH_MINIMAP.toString() + "," + HEIGHT_MINIMAP.toString() );
			childLayout( ChildLayoutType.Absolute );
			x( X_MINIMAP_MARGIN.toString() );
			y( Y.toString() );
			width( WIDTH_MINIMAP.toString() );
			height( HEIGHT_MINIMAP.toString() );
			
		}};
		
		builder.build(nifty, screen, layer);
		builder.buildElementType();
		
		Element imageMapElement = layer.findElementById(MINIMAP_IMAGE_ID);
		return imageMapElement;

	}
	
	private Integer[] convertFromWorldCoordsToPixelsCoords( final Vector2f worldCoords ) {
		Integer[] coords = new Integer[2];
		
		final Vector3f terrain3dTranslation = application.getGraphicalTerrain().getTerrainQuad().getLocalTranslation();
		final Vector2f terrainTranslation = new Vector2f( terrain3dTranslation.getX(), terrain3dTranslation.getZ() );
		Vector2f tmpWorldCoords = worldCoords.clone();
		tmpWorldCoords.subtractLocal( terrainTranslation );
		
		final Vector2f terrainExtension = 
				new Vector2f( application.getGraphicalTerrain().getWidthExtension(),
							  application.getGraphicalTerrain().getDepthExtension() );
		
		tmpWorldCoords.addLocal( terrainExtension.divide(2.0f) ); 
		
		coords[ X_COORD ] = (int) (( tmpWorldCoords.getX()*( (float) WIDTH_MINIMAP_IMAGE ) ) / terrainExtension.getX());
		coords[ Y_COORD ] = (int) (( tmpWorldCoords.getY()*( (float) HEIGHT_MINIMAP_IMAGE ) ) / terrainExtension.getY());
		
		return coords;
		
	}
	
	private void centralizeMinimapImage( final Integer[] subjectCoords ) {
		subImageStartPoint = computeSubImageStartPoint( subjectCoords );
		
		imageMapRenderer.getImage().getImageMode().setParameters( "subImage:" + subImageStartPoint[X_COORD].toString() + "," + subImageStartPoint[Y_COORD].toString() + 
																  ","  + WIDTH_MINIMAP.toString() + "," + HEIGHT_MINIMAP.toString() );
		
	}
	
	private static Integer[] computeSubImageStartPoint( final Integer[] subjectCoords ) {
		Integer[] subImageStartPoint = new Integer[2];
		subImageStartPoint[ X_COORD ] = subjectCoords[ X_COORD ]-HALF_WIDTH_MINIMAP;
		subImageStartPoint[ Y_COORD ] = subjectCoords[ Y_COORD ]-HALF_HEIGHT_MINIMAP;
		
		/* check point domain */
		if ( subImageStartPoint[ X_COORD ]<0 )
			subImageStartPoint[ X_COORD ]=0;	
		else if ( (subImageStartPoint[ X_COORD ]+WIDTH_MINIMAP)>WIDTH_MINIMAP_IMAGE )
			subImageStartPoint[ X_COORD ] = WIDTH_MINIMAP_IMAGE-WIDTH_MINIMAP;
		
		if ( subImageStartPoint[ Y_COORD ]<0 )
			subImageStartPoint[ Y_COORD ]=0;
		else if ( (subImageStartPoint[ Y_COORD ]+HEIGHT_MINIMAP)>HEIGHT_MINIMAP_IMAGE )
			subImageStartPoint[ Y_COORD ] = HEIGHT_MINIMAP_IMAGE-HEIGHT_MINIMAP;
		return subImageStartPoint;
	}
	
	private Element createNewIndicatorImageElement( final IVisibleEntity entity ) {
		
		final Integer[] coords = convertFromWorldCoordsToPixelsCoords( entity.get2dWorldPosition() );
		final Integer[] indicatorExtension = entity.getIndicatorImageExtension();
		ImageBuilder builder = new ImageBuilder() {{
			
			filename( entity.getIndicatorImageName() );
			childLayout( ChildLayoutType.Absolute );
			x( coords[ X_COORD ].toString() );
			y( coords[ Y_COORD ].toString() );
			width( indicatorExtension[ X_COORD ].toString() );
			height( indicatorExtension[ Y_COORD ].toString() );
			
		}};
		
		Element indicatorImageElement = builder.build(nifty, screen, indicatorsLayer);
		indicatorImageElement.setVisible( false );
		builder.buildElementType();
		
		return indicatorImageElement;
		
	}
	
	private void removeIndicatorImageElement( Element indicatorImageElement ) {
		indicatorImageElement.markForRemoval();
	}
	
	private void updateVisibleEntityIndicators() {
		List< Element > indicatorImageElements = indicatorsLayer.getChildren();
		for( Element indicatorImageElement: indicatorImageElements ) {
			
			final IVisibleEntity entity = elementsVisibleEntityImagesMap.get( indicatorImageElement );
			if ( entity!=null ) {
				final Integer[] mapCoords = convertFromWorldCoordsToPixelsCoords( entity.get2dWorldPosition() );
				final Integer[] coords = computeMinimapCoords( mapCoords );
				
				if ( coords!=null ) { // if it is visible w.r.t minimap
				
					final Integer[] indicatorImageExtension = entity.getIndicatorImageExtension();
					indicatorImageElement.setConstraintX( new SizeValue( coords[ X_COORD ]-(indicatorImageExtension[ X_COORD ]/2), SizeValueType.Pixel ) );
					indicatorImageElement.setConstraintY( new SizeValue( coords[ Y_COORD ]-(indicatorImageExtension[ Y_COORD ]/2), SizeValueType.Pixel ) );
					indicatorImageElement.setVisible( true );
				} else
					indicatorImageElement.setVisible( false );
			}
		}
	}
	
	private void updateMainPlayerIndicator() {
		final IVisibleEntity player = application.getPlayer();
		final Integer[] mapCoords = convertFromWorldCoordsToPixelsCoords( player.get2dWorldPosition() );
		Integer[] coords = computeMinimapCoords( mapCoords );
		if ( coords==null )
			return;
		
		final int HEIGHT_SCREEN = application.getSettings().getHeight();
		final Integer[] indicatorImageExtension = player.getIndicatorImageExtension();
		coords = computeMainPlayerPictureCoordsFromRotation( coords, player, -1.0f );
		mainPlayerPicture.setPosition( coords[ X_COORD ]-(indicatorImageExtension[ X_COORD ]/2), 
				HEIGHT_SCREEN - (coords[ Y_COORD ]-(indicatorImageExtension[ Y_COORD ]/2)) );
	
		Quaternion q = new Quaternion();
		q.fromAngleAxis( player.getAngleRotation(), Vector3f.UNIT_Z );
		mainPlayerPicture.setLocalRotation( q );
	}
	
	private Integer[] computeMinimapCoords( final Integer[] mapCoords ) {
		if ( !areVisibleCoords( mapCoords ) )
			return null;
		
		Integer[] coords = new Integer[2];
		coords[ X_COORD ]=mapCoords[ X_COORD ]-subImageStartPoint[ X_COORD ];
		coords[ Y_COORD ]=mapCoords[ Y_COORD ]-subImageStartPoint[ Y_COORD ];
		
		coords[ X_COORD ]+=X_MINIMAP_MARGIN;
		coords[ Y_COORD ]+=(application.getSettings().getHeight()-HEIGHT_MINIMAP-Y_MINIMAP_MARGIN);
		
		return coords;
	}
	
	private boolean areVisibleCoords( final Integer[] mapCoords ) {
		return ( ( mapCoords[ X_COORD ]>=subImageStartPoint[ X_COORD ] ) &&
				 ( mapCoords[ X_COORD ]<=(subImageStartPoint[ X_COORD ]+WIDTH_MINIMAP) ) &&
				 ( mapCoords[ Y_COORD ]>=subImageStartPoint[ Y_COORD ] ) &&
				 ( mapCoords[ Y_COORD ]<=(subImageStartPoint[ Y_COORD ]+HEIGHT_MINIMAP) ) );
	}
	
	private static Integer[] createNewInitialMinimapImageStartPoint() {
		Integer[] startPoint = new Integer[2];
		startPoint[ X_COORD ]=0;
		startPoint[ Y_COORD ]=0;
		return startPoint;
	}
	
	private static Picture createNewMainPlayerPicture( final IVisibleEntity playerEntity, final LogicSantosApplication application ) {
		final Integer[] indicatorExtension = playerEntity.getIndicatorImageExtension();
		
		Picture pic = new Picture( "HUD Picture" );
		pic.setImage( application.getAssetManager(), playerEntity.getIndicatorImageName(), true );
		pic.setWidth( indicatorExtension[ X_COORD ] );
		pic.setHeight (indicatorExtension[ Y_COORD ] );
		application.getGuiNode().attachChild(pic);
		
		return pic;
	}
	
	private Integer[] computeMainPlayerPictureCoordsFromRotation( final Integer[] standardCoords, final IVisibleEntity player, final float y_axis_orientation ) {
		Integer[] rotatedCoords = new Integer[2];
		
		Integer[] pictureExtension = player.getIndicatorImageExtension();
		Integer[] centerCoords = new Integer[2];
		centerCoords[ X_COORD ]=standardCoords[ X_COORD ]+(pictureExtension[ X_COORD ]/2);
		centerCoords[ Y_COORD ]=standardCoords[ Y_COORD ]+(pictureExtension[ Y_COORD ]/2);
		
		Vector2f positionVector = new Vector2f( 
				-centerCoords[ X_COORD ]+standardCoords[ X_COORD ], 
				-centerCoords[ Y_COORD ]+standardCoords[ Y_COORD ] );
		
		positionVector.rotateAroundOrigin( player.getAngleRotation(), false );
		
		rotatedCoords[ X_COORD ]=centerCoords[ X_COORD ] + ((int) positionVector.getX());
		rotatedCoords[ Y_COORD ]=centerCoords[ Y_COORD ] + ((int) (positionVector.getY()*y_axis_orientation));
		
		return rotatedCoords;
	}
	
	/*private static String getIndicatorImagePathFromType( final IndicatorType type ) {
		switch( type ) {
		case MAIN_PLAYER: return "";
		case PLAYER:      return "";
		case WALKER:      return "";
		case POLICEMAN:   return "";
		case VEHICLE:     return "";
		default: return null;
		}
	}*/


	

}
