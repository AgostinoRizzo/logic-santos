/**
 * 
 */
package it.unical.logic_santos.design.modeling;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;

import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.physics.extension.IPhisicalExtensionGenerator;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class SimplePhysicalExtensionGenerator implements IPhisicalExtensionGenerator {
	
	public SimplePhysicalExtensionGenerator() {
		
	}
	
	@Override
	public IPhysicalExtension generatePhysicalExtension(Class<?> spatialEntityClass, final Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	public static IPhysicalExtension generateNewRoadNodePhysicalExtension(final Vector3D position, final boolean decoreToPathNode) {
		Sphere mesh = new Sphere(32, 32, 2.0f);
		Spatial spatialNode = new Geometry("A shape", mesh);
		Material mat = new Material(LogicSantosApplication.ASSET_MANAGER_APPLICATION, "Common/MatDefs/Misc/Unshaded.j3md");
		if (decoreToPathNode)
			mat.setColor("Color", ColorRGBA.Yellow);
		spatialNode.setMaterial(mat);
		spatialNode.setLocalTranslation(position.toVector3f());
		//spatialNode.setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
		//rootNode.attachChild(spatialNode);
		ModelBasedPhysicalExtension physicalExtension = new ModelBasedPhysicalExtension(spatialNode, position, null, 1.0f, Vector3D.ZERO.clone(), false);
		return physicalExtension;
	}

	public static IPhysicalExtension generateNewRoadArcPhysicalExtension(final Vector3D startPosition, final Vector3D endPosition, final boolean decoreToPathNode, final boolean loadPhysicalExtension) {
		if ( !loadPhysicalExtension )
			return null;
		
		Arrow mesh = new Arrow(Vector3D.calculateDirection(startPosition, endPosition).toVector3f());
		//Quaternion q = new Quaternion();
		//q.lookAt(Vector3D.calculateDirection(startPosition, endPosition).toVector3f(), Vector3f.UNIT_Y);
		
		Spatial spatialNode = new Geometry("arrow", mesh);
		Material mat = new Material(LogicSantosApplication.ASSET_MANAGER_APPLICATION, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setLineWidth(3.0f);
		if (decoreToPathNode)
			mat.setColor("Color", ColorRGBA.Blue);
		else
			mat.setColor("Color", ColorRGBA.Green);
		spatialNode.setMaterial(mat);
		spatialNode.setLocalTranslation(startPosition.toVector3f());
		//spatialNode.setLocalRotation(q);
		//spatialNode.setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
		//rootNode.attachChild(spatialNode);
		ModelBasedPhysicalExtension physicalExtension = new ModelBasedPhysicalExtension(spatialNode, startPosition, null, 1.0f, Vector3D.ZERO.clone(), false);
		return physicalExtension;
	}

	@Override
	public IPhysicalExtension generatePlayerPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generatePolicemanPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateWalkerBobPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateWatchTowerPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateStopSignPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateMilleniumTowerPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateSeaFrontMilleniumTowerPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IPhysicalExtension generatePalaceTowerPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateParkTowerPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateCoffeeShopPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateTennisFieldPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateTrafficLightPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IPhysicalExtension generateSeaFrontTrafficLightPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateStreetLightPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateSeaFrontStreetLightPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateParkBenchPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateSeaFrontParkBenchPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateClassicTreePhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generatePalmTreePhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateCityKernelPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateSkyscraperPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateSkyApartmentPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateCarPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicalExtension generateHelicopterPhysicalExtension(Vector3D spatialPosition) {
		// TODO Auto-generated method stub
		return null;
	}
}
