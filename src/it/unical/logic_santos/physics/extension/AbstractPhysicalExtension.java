/**
 * 
 */
package it.unical.logic_santos.physics.extension;

import it.unical.logic_santos.collision.ICollidable;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;

/**
 * @author Agostino
 *
 */
public abstract class AbstractPhysicalExtension implements IPhysicalExtension, ICollidable, Comparable<AbstractPhysicalExtension> {

	protected AbstractBoundingVolume boundingVolume=null;
	protected ISpatialEntity spatialEntityOwner=null;
	
	public AbstractPhysicalExtension(ISpatialEntity _spatialEntityOwner) {
		// TODO Auto-generated constructor stub
		this.spatialEntityOwner = _spatialEntityOwner;
	}
	
	public AbstractPhysicalExtension(AbstractBoundingVolume _boundingVolume, ISpatialEntity _spatialEntityOwner) {
		this.boundingVolume = _boundingVolume;
		this.spatialEntityOwner = _spatialEntityOwner;
	}

	@Override
	public AbstractBoundingVolume getBoundingVolume() {
		return boundingVolume;
	}

	@Override
	public void setBoundingVolume(AbstractBoundingVolume boundingVolume) {
		this.boundingVolume = boundingVolume;
	}
	
	@Override
	public int compareTo(AbstractPhysicalExtension arg0) {
		return (this.hashCode() - arg0.hashCode());
	}

	@Override
	public ISpatialEntity getSpatialEntityOwner() {
		return spatialEntityOwner;
	}

	@Override
	public void setSpatialEntityOwner(ISpatialEntity spatialEntity) {
		this.spatialEntityOwner = spatialEntity;
	}
	
	public void onShootResetEffects() {
		
	}
	
	public void onShootApplyEffects() {
		
	}
	

}
