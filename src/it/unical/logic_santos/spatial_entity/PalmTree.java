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
public class PalmTree extends AbstractTree {

	private static final String NAME = "ClassicTree";
	
	public PalmTree() {
		super(PalmTree.getPalmTreePhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public PalmTree(final Vector3D _spatialPosition) {
		super(_spatialPosition, PalmTree.getPalmTreePhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return PalmTree.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new PalmTree();
	}

	public static IPhysicalExtension getPalmTreePhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generatePalmTreePhysicalExtension( _spatialPosition);
	}
}
