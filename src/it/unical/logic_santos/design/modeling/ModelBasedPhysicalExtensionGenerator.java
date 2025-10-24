/**
 * 
 */
package it.unical.logic_santos.design.modeling;



import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Node;

import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.physics.extension.IPhisicalExtensionGenerator;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.toolkit.math.MathGameToolkit;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class ModelBasedPhysicalExtensionGenerator implements IPhisicalExtensionGenerator {
	
	private AssetManager assetManager=null;

	
	public ModelBasedPhysicalExtensionGenerator() {
		this.assetManager = LogicSantosApplication.ASSET_MANAGER_APPLICATION;
	}
	
	@Override
	public IPhysicalExtension generatePhysicalExtension(Class<?> spatialEntityClass, final Vector3D spatialPosition) {
		return null;
	}
	
	@Override
	public IPhysicalExtension generateClassicTreePhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.CLASSIC_TREE_MODEL_NAME,
											ModelingConfig.DEFAULT_MODEL_SCALE_FACTOR,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generatePalmTreePhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = new ModelBasedPhysicalExtension(assetManager.loadModel( ModelingConfig.PALM_TREE_MODEL_NAME ), spatialPosition, null, ModelingConfig.DEFAULT_MODEL_SCALE_FACTOR,  ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT, false);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setTexture("ColorMap", assetManager.loadTexture("Models/PalmTree/Leaf_CoconutGree.png"));
		mat.setFloat("AlphaDiscardThreshold", 0.8f);
		physicalExtension.getModelSpatial().setMaterial(mat);
		physicalExtension.getModelSpatial().setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
		return physicalExtension;
	}
		
	@Override
	public IPhysicalExtension generateCityKernelPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.CITY_KERNEL_MODEL_NAME,
											0.1f,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generateSkyscraperPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.SKYSCRAPER_MODEL_NAME,
											ModelingConfig.SKYSCRAPER_MODEL_SCALE_FACTOR,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}

	@Override
	public IPhysicalExtension generateSkyApartmentPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.SKY_APARTMENT_MODEL_NAME,
											ModelingConfig.SKYAPARTMENT_MODEL_SCALE_FACTOR,  
											ModelingConfig.SKYAPARTMENT_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}
	
	/*else if (spatialEntityClass == SkyApartment.class) {
			System.out.println("Creating SKYAPARTMENT"); spatialPosition.setY(0.0f);
			ModelBasedPhysicalExtension physicalExtension = new ModelBasedPhysicalExtension(assetManager.loadModel("Models/SkyApartment/3d-model.j3o"), spatialPosition, null, ModelingConfig.SKYAPARTMENT_MODEL_SCALE_FACTOR,  ModelingConfig.SKYAPARTMENT_TRANSLATION_ADJUSTMENT, false);
			//Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			//mat.setTexture("ColorMap", assetManager.loadTexture("Models/SkyApartment/texture1.jpg"));
			physicalExtension.getModelSpatial().setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
			physicalExtension.getModelSpatial().getWorldBound().setCenter(Vector3f.ZERO);
			physicalExtension.getModelSpatial().setLocalTranslation(Vector3f.ZERO);
			return physicalExtension;
			
	} */
	
	@Override
	public IPhysicalExtension generateCarPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = new ModelBasedPhysicalExtension((Node) assetManager.loadModel( ModelingConfig.CAR_MODEL_NAME ), spatialPosition, null, ModelingConfig.VEHICLE_MODEL_SCALE_FACTOR,  ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT, true);
		physicalExtension.getModelSpatial().setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
		return physicalExtension;

	}
	
	@Override
	public IPhysicalExtension generateHelicopterPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = new ModelBasedPhysicalExtension( assetManager.loadModel( ModelingConfig.HELICOPTER_MODEL_NAME ), spatialPosition, null, ModelingConfig.HELICOPTER_MODEL_SCALE_FACTOR,  ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT, false);
		physicalExtension.getModelSpatial().setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
		return physicalExtension;

	}

	@Override
	public IPhysicalExtension generatePlayerPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.PLAYER_MODEL_NAME,
											ModelingConfig.PLAYER_CHARACTER_MODEL_SCALE_FACTOR,  
											ModelingConfig.PLAYER_CHARACTER_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}

	@Override
	public IPhysicalExtension generatePolicemanPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.POLICEMAN_MODEL_NAME,
											ModelingConfig.POLICEMAN_MODEL_SCALE_FACTOR,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generateWalkerBobPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.PLAYER_MODEL_NAME,
											ModelingConfig.PLAYER_CHARACTER_MODEL_SCALE_FACTOR,  
											ModelingConfig.PLAYER_CHARACTER_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generateWatchTowerPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.WATCH_TOWER_MODEL_NAME,
											ModelingConfig.WATCH_TOWER_MODEL_SCALE_FACTOR,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generateStopSignPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.STOP_SIGN_MODEL_NAME,
											ModelingConfig.STOP_SIGN_MODEL_SCALE_FACTOR,  
											ModelingConfig.STOP_SIGN_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generateMilleniumTowerPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.MILLENIUM_TOWER_MODEL_NAME,
											ModelingConfig.MILLENIUM_TOWER_MODEL_SCALE_FACTOR,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generateSeaFrontMilleniumTowerPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.MILLENIUM_TOWER_MODEL_NAME,
											ModelingConfig.MILLENIUM_TOWER_MODEL_SCALE_FACTOR,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		physicalExtension.getModelSpatial().rotate( 0.0f, MathGameToolkit.toRadiants( 270 ), 0.0f );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generatePalaceTowerPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.PALACE_TOWER_MODEL_NAME,
											ModelingConfig.PALACE_TOWER_MODEL_SCALE_FACTOR,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generateParkTowerPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.PARK_TOWER_MODEL_NAME,
											ModelingConfig.PARK_TOWER_MODEL_SCALE_FACTOR,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generateCoffeeShopPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.COFFEE_SHOP_MODEL_NAME,
											ModelingConfig.COFFEE_SHOP_MODEL_SCALE_FACTOR,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generateTennisFieldPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.TENNIS_FIELD_MODEL_NAME,
											ModelingConfig.TENNIS_FIELD_MODEL_SCALE_FACTOR,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generateTrafficLightPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.TRAFFIC_LIGHT_MODEL_NAME,
											ModelingConfig.TRAFFIC_LIGHT_MODEL_SCALE_FACTOR,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generateSeaFrontTrafficLightPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.TRAFFIC_LIGHT_MODEL_NAME,
											ModelingConfig.TRAFFIC_LIGHT_MODEL_SCALE_FACTOR,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		physicalExtension.getModelSpatial().rotate( 0.0f, MathGameToolkit.toRadiants( 90 ), 0.0f );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generateStreetLightPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.STREET_LIGHT_MODEL_NAME,
											ModelingConfig.STREET_LIGHT_MODEL_SCALE_FACTOR,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generateSeaFrontStreetLightPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.STREET_LIGHT_MODEL_NAME,
											ModelingConfig.STREET_LIGHT_MODEL_SCALE_FACTOR,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		
		physicalExtension.getModelSpatial().rotate( 0.0f, MathGameToolkit.toRadiants( 90 ), 0.0f );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generateParkBenchPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.PARK_BENCH_MODEL_NAME,
											ModelingConfig.PARK_BENCH_MODEL_SCALE_FACTOR,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		return physicalExtension;
	}
	
	@Override
	public IPhysicalExtension generateSeaFrontParkBenchPhysicalExtension(Vector3D spatialPosition) {
		ModelBasedPhysicalExtension physicalExtension = 
				generatePhysicalExtension( spatialPosition, ModelingConfig.PARK_BENCH_MODEL_NAME,
											ModelingConfig.PARK_BENCH_MODEL_SCALE_FACTOR,  
											ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT );
		
		physicalExtension.getModelSpatial().rotate( 0.0f, MathGameToolkit.toRadiants( 90 ), 0.0f );
		return physicalExtension;
	}
	
	private ModelBasedPhysicalExtension generatePhysicalExtension( final Vector3D spatialPosition, final String modelName, final float modelScaleFactor, final Vector3D translationAdjustment ) {
		ModelBasedPhysicalExtension physicalExtension = new ModelBasedPhysicalExtension(
				assetManager.loadModel( modelName ), 
				spatialPosition, null, modelScaleFactor,  
				translationAdjustment, false);
		physicalExtension.getModelSpatial().setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
		return physicalExtension;
	}

}
