/**
 * 
 */
package it.unical.logic_santos.spatial_entity;

import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class CityKernel extends AbstractStaticSpatialEntity {

	private static final String NAME = "City Kernel";
	
	public CityKernel(final Vector3D _spatialPosition) {
		super(_spatialPosition, CityKernel.getCityKernelPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
		this.physicalExtension.updateTranslation();
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new CityKernel(this.spatialTranslation);
	}



	public String getName() {
		return CityKernel.NAME;
	}
	
	public static IPhysicalExtension getCityKernelPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateCityKernelPhysicalExtension(_spatialPosition);
	}

}
