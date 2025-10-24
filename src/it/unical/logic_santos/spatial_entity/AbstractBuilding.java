/**
 * 
 */
package it.unical.logic_santos.spatial_entity;

import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public abstract class AbstractBuilding extends AbstractStaticSpatialEntity {

	public AbstractBuilding(IPhysicalExtension _physicalExtension) {
		super(_physicalExtension);
	}
	
	public AbstractBuilding(final Vector3D _spatialPosition, IPhysicalExtension _physicalExtension) {
		super(_spatialPosition, _physicalExtension);
	}
}
