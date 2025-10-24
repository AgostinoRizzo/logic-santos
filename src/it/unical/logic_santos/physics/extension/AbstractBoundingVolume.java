/**
 * 
 */
package it.unical.logic_santos.physics.extension;

import it.unical.logic_santos.collision.ICollidable;
import it.unical.logic_santos.toolkit.geometry.Shape3D;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public abstract class AbstractBoundingVolume implements ICollidable, Comparable<AbstractBoundingVolume> {
	
	protected Vector3D centerPosition=null;
	
	protected IPhysicalExtension owner=null;
	
	

	public AbstractBoundingVolume(IPhysicalExtension _owner) {
		this.centerPosition = new Vector3D();
		this.owner = _owner;
	}
	
	public AbstractBoundingVolume(final Vector3D _centerPosition, IPhysicalExtension _owner) {
		this.centerPosition = new Vector3D(_centerPosition);
		this.owner = _owner;
	}
	
	
	public abstract BoundingVolumeType getType();

	
	
	public Vector3D getCenterPosition() {
		return centerPosition;
	}

	public void setCenterPosition(final Vector3D _centerPosition) {
		this.centerPosition = new Vector3D(_centerPosition);
	}

	public float distanceTo(final Vector3D point) {
		return centerPosition.distanceTo(point);
	}
	
	public IPhysicalExtension getOwner() {
		return owner;
	}

	public void setOwner(IPhysicalExtension owner) {
		this.owner = owner;
	}

	public abstract float getVolume();
	
	public abstract Shape3D getShape();
	
	public abstract float getXExtension();
	public abstract float getYExtension();
	public abstract float getZExtension();
	
	@Override
	public int compareTo(AbstractBoundingVolume arg0) {
		return (this.hashCode() - arg0.hashCode());
	}
	
}
