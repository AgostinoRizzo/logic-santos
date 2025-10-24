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
public class TennisField extends AbstractStaticSpatialEntity {

	private static final String NAME = "TennisField";
	
	public TennisField() {
		super(TennisField.getTennisFieldPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public TennisField(final Vector3D _spatialPosition) {
		super(_spatialPosition, TennisField.getTennisFieldPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return TennisField.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new TennisField();
	}

	public static IPhysicalExtension getTennisFieldPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateTennisFieldPhysicalExtension(_spatialPosition);
	}
}
