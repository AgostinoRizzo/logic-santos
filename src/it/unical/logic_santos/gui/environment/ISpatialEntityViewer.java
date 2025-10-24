/**
 * 
 */
package it.unical.logic_santos.gui.environment;

import java.util.List;

import com.jme3.math.Vector3f;

import it.unical.logic_santos.spatial_entity.ISpatialEntity;

/**
 * @author Agostino
 *
 */
public interface ISpatialEntityViewer {

	public void attachSpatialEntity( ISpatialEntity entity );
	public void detachSpatialEntity( ISpatialEntity entity );
	public void setFrustumFar( final float far );
	
	public void update( final Vector3f subjectPosition, final float tpf );
	public void update( final List< Vector3f > subjectPositions, final float tpf );
	public void update( final float tpf );
	
}
