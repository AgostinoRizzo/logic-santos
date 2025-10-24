/**
 * 
 */
package it.unical.logic_santos.spatial_entity;

import it.unical.logic_santos.physics.activity.IPhysicalActivity;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public abstract class AbstractDynamicSpatialEntity extends AbstractStaticSpatialEntity {
	
	private IPhysicalActivity physicalActivity=null;
	
	
	
	public AbstractDynamicSpatialEntity(IPhysicalExtension _physicalExtension) {
		super(_physicalExtension);
	}
	
	public AbstractDynamicSpatialEntity(IPhysicalExtension _physicalExtension, IPhysicalActivity _physicalActivity) {
		super(_physicalExtension);
		this.physicalActivity = _physicalActivity;
	}
	
	public AbstractDynamicSpatialEntity(final Vector3D _spatialPosition, IPhysicalExtension _physicalExtension) {
		super(_spatialPosition, _physicalExtension);
	}

	
	
	
	
	public IPhysicalActivity getPhysicalActivity() {
		return physicalActivity;
	}

	public void setPhysicalActivity(IPhysicalActivity physicalActivity) {
		this.physicalActivity = physicalActivity;
	}
	
	@Override
	public boolean isStatic() {
		return false;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

}
