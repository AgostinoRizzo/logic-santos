/**
 * 
 */
package it.unical.logic_santos.physics.activity;

import it.unical.logic_santos.gameplay.IBullet;
import it.unical.logic_santos.roads_network.PathsNetwork;
import it.unical.logic_santos.roads_network.RoadNode;
import it.unical.logic_santos.roads_network.RoadsNetwork;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;

/**
 * @author Agostino
 *
 */
public interface IPhysicalActivity {

	public void initActivity();
	public void finalizeActivity();
	
	public ISpatialEntity getSpatialEntityOwner();
	public void setSpatialEntityOwner(ISpatialEntity spatialEntity);
	
	public void setInRoadsNetwork(RoadsNetwork roadsNetwork, RoadNode startNode);
	public void setInRoadsNetwork(RoadsNetwork roadsNetwork);
	public void setStartRoadsNetworkNode(RoadNode startNode);
	
	public void setInPathsNetwork(PathsNetwork pathsNetwork, RoadNode startNode);
	public void setInPathsNetwork(PathsNetwork pathsNetwork);
	public void setStartPathsNetworkNode(RoadNode startNode);
	
	public void updateTranslation();
	
	public void update(final float tpf);
	
	public void onShootReaction( final IBullet bullet);
	public void onExplosionReaction( final IBullet bullet );
}
