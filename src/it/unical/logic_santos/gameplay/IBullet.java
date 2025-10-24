/**
 * 
 */
package it.unical.logic_santos.gameplay;

import java.util.List;

import com.jme3.math.Vector3f;

import it.unical.logic_santos.spatial_entity.AbstractHuman;
import it.unical.logic_santos.spatial_entity.AbstractStaticSpatialEntity;
import it.unical.logic_santos.spatial_entity.AbstractVehicle;
import it.unical.logic_santos.terrain.modeling.ITerrainChunkModel;

/**
 * @author Agostino
 *
 */
public interface IBullet {

	public void addInWorld();
	public void removeFromWorld();
	public void initActivity( final Vector3f startPosition, final Vector3f direction );
	public void finalizeActivity();
	public void update( final float tpf );
	public boolean isActive();
	public void setHumans( List<AbstractHuman> humans );
	public void setVehicles( List<AbstractVehicle> vehicles );
	public void setStaticSpatialEntities(List<AbstractStaticSpatialEntity> staticSpatialEntities);
	public void setTerrainModel(ITerrainChunkModel terrainModel);
	public List< AbstractHuman > checkHumanCollisions();
	public List< AbstractVehicle > checkVehicleCollisions();
	public boolean checkStaticSpatialEntitiesCollisions();
	public boolean checkTerrainCollision();
	
	public Vector3f getDirection();
	public Vector3f getCurrentTranslation();
	public LifeBar getHumanLifeBarReduction( final Class< ? extends AbstractHuman > humanClass );
	public LifeBar getVehicleLifeBarReduction();
	public int getWantedValue();
	public float getGainValue();
	
	public BulletCookie getCookie();
	public void setCookie( final BulletCookie cookie );
	
	public AbstractHuman getOwner();
	public boolean explosionOnCollision();
	
}
