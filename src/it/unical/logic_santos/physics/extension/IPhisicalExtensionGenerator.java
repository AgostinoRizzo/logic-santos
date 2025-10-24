/**
 * 
 */
package it.unical.logic_santos.physics.extension;

import com.jme3.scene.Node;

import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public interface IPhisicalExtensionGenerator {
	
	public IPhysicalExtension generatePhysicalExtension(final Class<?> spatialEntityClass, final Vector3D spatialPosition);

	public IPhysicalExtension generateClassicTreePhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generatePalmTreePhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateCityKernelPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateSkyscraperPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateSkyApartmentPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateCarPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateHelicopterPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generatePlayerPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generatePolicemanPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateWalkerBobPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateWatchTowerPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateStopSignPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateMilleniumTowerPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateSeaFrontMilleniumTowerPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generatePalaceTowerPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateParkTowerPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateCoffeeShopPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateTennisFieldPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateTrafficLightPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateSeaFrontTrafficLightPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateStreetLightPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateSeaFrontStreetLightPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateParkBenchPhysicalExtension(final Vector3D spatialPosition);
	public IPhysicalExtension generateSeaFrontParkBenchPhysicalExtension(final Vector3D spatialPosition);
	
}
