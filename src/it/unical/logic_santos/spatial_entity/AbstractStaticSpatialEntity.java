/**
 * 
 */
package it.unical.logic_santos.spatial_entity;

import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.physics.extension.AbstractPhysicalExtension;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public abstract class AbstractStaticSpatialEntity implements ISpatialEntity, Comparable<AbstractStaticSpatialEntity> {
	
	protected Vector3D spatialTranslation=null;
	protected IPhysicalExtension physicalExtension=null;
	
	
	public AbstractStaticSpatialEntity() {
		this.spatialTranslation = new Vector3D(Vector3D.ZERO);
	}
	
	public AbstractStaticSpatialEntity(IPhysicalExtension _physicalExtension) {
		this.spatialTranslation = new Vector3D(Vector3D.ZERO);
		this.physicalExtension = _physicalExtension;
	}
	
	public AbstractStaticSpatialEntity(final Vector3D _spatialTranslation, IPhysicalExtension _physicalExtension) {
		this.spatialTranslation = new Vector3D(_spatialTranslation);
		this.physicalExtension = _physicalExtension;
	}
	
	
	@Override
	public Vector3D getSpatialTranslation() {
		return spatialTranslation;
	}

	@Override
	public void setSpatialTranslation(final Vector3D spatialTranslation) {
		this.spatialTranslation = new Vector3D(spatialTranslation);
		//this.spatialTranslation = this.spatialTranslation.clone().add(this.getTranslationAdjustment());
		physicalExtension.updateTranslation();
	}

	@Override
	public IPhysicalExtension getPhysicalExtension() {
		return physicalExtension;
	}
	
	@Override
	public AbstractPhysicalExtension getAbstractPhysicalExtension() {
		return ((AbstractPhysicalExtension)physicalExtension);
	}

	@Override
	public void setPhysicalExtension(IPhysicalExtension physicalExtension) {
		this.physicalExtension = physicalExtension;
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public boolean isDynamic() {
		return false;
	}

	@Override
	public int compareTo(AbstractStaticSpatialEntity arg0) {
		return (this.hashCode() - arg0.hashCode());
	}

	@Override
	public Vector3D getTranslationAdjustment() {
		return ModelingConfig.STOP_SIGN_TRANSLATION_ADJUSTMENT;
	}
	
	
	@Override
	public float getMass() {
		return 0.0f;
	}
	
	public Vector3D getSpatialEntityTranslationAdjustment() {
		return ModelingConfig.DEFAULT_TRANSLATION_ADJUSTMENT;
	}

}
