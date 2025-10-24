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
public class SkyApartment extends AbstractBuilding {

	private static final String NAME = "SkyApartment";
	
	public SkyApartment() {
		super(SkyApartment.getSkyApartmentPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public SkyApartment(final Vector3D _spatialPosition) {
		super(_spatialPosition, SkyApartment.getSkyApartmentPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return SkyApartment.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new SkyApartment();
	}

	@Override
	public Vector3D getTranslationAdjustment() {
		return ModelingConfig.SKYAPARTMENT_TRANSLATION_ADJUSTMENT;
	}

	public static IPhysicalExtension getSkyApartmentPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateSkyApartmentPhysicalExtension(_spatialPosition);
	}
}
