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
public class Skyscraper extends AbstractBuilding {

	private static final String NAME = "Skyscraper";
	
	public Skyscraper() {
		super(Skyscraper.getSkyscraperPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public Skyscraper(final Vector3D _spatialPosition) {
		super(_spatialPosition, Skyscraper.getSkyscraperPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return Skyscraper.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new Skyscraper();
	}

	public static IPhysicalExtension getSkyscraperPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateSkyscraperPhysicalExtension(_spatialPosition);
	}
}
