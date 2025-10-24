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
public class ParkBench extends AbstractStaticSpatialEntity {

	private static final String NAME = "ParkBench";
	
	public ParkBench() {
		super(ParkBench.getParkBenchPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public ParkBench(final Vector3D _spatialPosition) {
		super(_spatialPosition, ParkBench.getParkBenchPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return ParkBench.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new ParkBench();
	}

	public static IPhysicalExtension getParkBenchPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateParkBenchPhysicalExtension(_spatialPosition);
	}
}
