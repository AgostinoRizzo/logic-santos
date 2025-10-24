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
public class ClassicTree extends AbstractTree {
	
	
	private static final String NAME = "ClassicTree";
	
	public ClassicTree() {
		super(ClassicTree.getClassicTreePhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public ClassicTree(final Vector3D _spatialPosition) {
		super(_spatialPosition, ClassicTree.getClassicTreePhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return ClassicTree.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new ClassicTree();
	}

	public static IPhysicalExtension getClassicTreePhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateClassicTreePhysicalExtension( _spatialPosition);
	}


}
