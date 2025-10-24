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
public class SeaFrontParkBench extends AbstractStaticSpatialEntity {

	private static final String NAME = "SeaFrontParkBench";
	
	public SeaFrontParkBench() {
		super(SeaFrontParkBench.getSeaFrontParkBenchPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public SeaFrontParkBench(final Vector3D _spatialPosition) {
		super(_spatialPosition, SeaFrontParkBench.getSeaFrontParkBenchPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return SeaFrontParkBench.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new SeaFrontParkBench();
	}

	public static IPhysicalExtension getSeaFrontParkBenchPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateSeaFrontParkBenchPhysicalExtension(_spatialPosition);
	}	
}
