/**
 * 
 */
package it.unical.logic_santos.spatial_entity;

import it.unical.logic_santos.physics.extension.AbstractPhysicalExtension;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public interface ISpatialEntity {
	
	public Vector3D getSpatialTranslation();
	public void setSpatialTranslation(final Vector3D translation);
	
	public IPhysicalExtension getPhysicalExtension();
	public AbstractPhysicalExtension getAbstractPhysicalExtension();
	public void setPhysicalExtension(IPhysicalExtension phisicalExtension);
	
	public float getMass();
	
	public boolean isStatic();
	public boolean isDynamic();
	
	public Vector3D getTranslationAdjustment();
	public ISpatialEntity cloneSpatialEntity();
	
	public String getName();
	
}
