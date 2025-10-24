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
public class StopSign extends AbstractStaticSpatialEntity {

	private static final String NAME = "Stop Sign";
	
	public StopSign() {
		super(StopSign.getStopSignPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public StopSign(final Vector3D _spatialPosition) {
		super(_spatialPosition, StopSign.getStopSignPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return StopSign.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new StopSign();
	}
	
	@Override
	public Vector3D getSpatialEntityTranslationAdjustment() {
		return ModelingConfig.STOP_SIGN_TRANSLATION_ADJUSTMENT;
	}

	public static IPhysicalExtension getStopSignPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateStopSignPhysicalExtension(_spatialPosition);
	}
}
