/**
 * 
 */
package it.unical.logic_santos.universe;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;


import it.unical.logic_santos.collision.ICollisionArena;
import it.unical.logic_santos.collision.ICollisionDetectionEngine;
import it.unical.logic_santos.collision.SpatialHashingEngine;
import it.unical.logic_santos.environment.Calendar;
import it.unical.logic_santos.spatial_entity.AbstractDynamicSpatialEntity;
import it.unical.logic_santos.spatial_entity.AbstractStaticSpatialEntity;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.terrain.modeling.ITerrainChunkModel;
import it.unical.logic_santos.terrain.modeling.ImageBasedHeightMapModel;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public abstract class AbstractWorld implements IWorld, IWorldArena, ICollisionArena {
	
	private Collection<AbstractStaticSpatialEntity> staticSpatialEntities=null;
	private Collection<AbstractDynamicSpatialEntity> dynamicSpatialEntities=null;
	
	private ITerrainChunkModel terrainModel=null;
	
	private ICollisionDetectionEngine collisionEngine=null;
	
	private Calendar calendar=null;

	
	public AbstractWorld() {
		initComponents();
	}
	
	@Override
	public void initComponents() {
		
		this.staticSpatialEntities = new TreeSet<AbstractStaticSpatialEntity>();
		this.dynamicSpatialEntities = new TreeSet<AbstractDynamicSpatialEntity>();
		
		this.terrainModel = new ImageBasedHeightMapModel();
		this.collisionEngine = new SpatialHashingEngine(this);
		
		this.calendar = new Calendar();
		this.calendar.getDayClock().setCurrentRealTime();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadComponents() {
		// TODO Auto-generated method stub
		
	}

	public final Collection<AbstractStaticSpatialEntity> getStaticSpatialEntities() {
		return staticSpatialEntities;
	}

	public final Collection<AbstractDynamicSpatialEntity> getDynamicSpatialEntities() {
		return dynamicSpatialEntities;
	}

	public final ITerrainChunkModel getTerrainModel() {
		return terrainModel;
	}

	public final ICollisionDetectionEngine getCollisionEngine() {
		return collisionEngine;
	}

	public final Calendar getCalendar() {
		return calendar;
	}


	@Override
	public void update(final float tpf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addSpatialEntity(ISpatialEntity spatialEntity) {
		if (spatialEntity.isStatic()) {
			if (!staticSpatialEntities.contains(spatialEntity)) {
				staticSpatialEntities.add((AbstractStaticSpatialEntity) spatialEntity);
				collisionEngine.addCollidable(spatialEntity.getAbstractPhysicalExtension().getBoundingVolume());
				return true;
			}
		} else if (spatialEntity.isDynamic()) {
			if (!dynamicSpatialEntities.contains(spatialEntity)) {
				dynamicSpatialEntities.add((AbstractDynamicSpatialEntity) spatialEntity);
				collisionEngine.addCollidable(spatialEntity.getAbstractPhysicalExtension().getBoundingVolume());
				return true;
			}
		}
		return false;
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean removeSpatialEntity(ISpatialEntity spatialEntity) {
		if (spatialEntity.isStatic()) {
			if (staticSpatialEntities.contains(spatialEntity)) {
				staticSpatialEntities.remove((AbstractStaticSpatialEntity) spatialEntity);
				collisionEngine.removeCollidable(spatialEntity.getAbstractPhysicalExtension().getBoundingVolume());
				return true;
			}
		} else if (spatialEntity.isDynamic()) {
			if (dynamicSpatialEntities.contains(spatialEntity)) {
				dynamicSpatialEntities.remove((AbstractDynamicSpatialEntity) spatialEntity);
				collisionEngine.removeCollidable(spatialEntity.getAbstractPhysicalExtension().getBoundingVolume());
				return true;
			}
		}
		return false;
	}

	@Override
	public Vector3D getTranslation() {
		Vector3D terrainTranslation = WorldConfiguration.WORLD_TRANSLATION;
		return new Vector3D(terrainTranslation);
	}

	@Override
	public float getWidthSize() {
		return terrainModel.getWidth();
	}

	@Override
	public float getDepthSize() {
		return terrainModel.getDepth();
	}

	@Override
	public float getXExtension() {
		return terrainModel.getWidth()/2.0f;
	}

	@Override
	public float getZExtension() {
		return terrainModel.getDepth()/2.0f;
	}
	
	
}
