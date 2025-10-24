/**
 * 
 */
package it.unical.logic_santos.controls;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unical.logic_santos.collision.ICollidable;
import it.unical.logic_santos.collision.ICollisionDetectionEngine;
import it.unical.logic_santos.collision.TerrainCollisionResults;
import it.unical.logic_santos.physics.extension.AbstractBoundingVolume;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.terrain.modeling.ITerrainChunkModel;
import it.unical.logic_santos.toolkit.math.Point;
import it.unical.logic_santos.toolkit.math.Ray;
import it.unical.logic_santos.toolkit.math.Vector2D;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class SpatialEntityPicker {

	protected Ray ray=null;
	protected ICollisionDetectionEngine collisionEngine=null;
	protected ITerrainChunkModel terrainModel=null;
	
	private static Set< Class<?> > uniqueVisibleObjects=new HashSet< Class<?> >();
	
	public SpatialEntityPicker(ICollisionDetectionEngine _collisionEngine, ITerrainChunkModel _terrainModel) {
		this.ray = new Ray();
		this.collisionEngine = _collisionEngine;
		this.terrainModel = _terrainModel;
	}
	
	public SpatialEntityPicker(final Vector3D _origin, final Vector3D _direction, ICollisionDetectionEngine _collisionEngine, ITerrainChunkModel _terrainModel) {
		this.ray = new Ray(_origin, _direction);
		this.collisionEngine = _collisionEngine;
		this.terrainModel = _terrainModel;
	}

	public Ray getRay() {
		return ray;
	}

	public void setRay(Ray ray) {
		this.ray = ray;
	}
	
	public ICollisionDetectionEngine getCollisionEngine() {
		return collisionEngine;
	}

	public void setCollisionEngine(ICollisionDetectionEngine collisionEngine) {
		this.collisionEngine = collisionEngine;
	}

	public ISpatialEntity pickSpatialEntity() {
		
		Point currentPoint = new Point(ray.getOrigin());
		final Vector3D rayDirection = ray.getDirection();
		Set<ICollidable> currentPickedCollidables=null;
		List<ICollidable> pickedCollidables=new ArrayList< ICollidable >();
		int currentStep = 0;
		boolean collisionDetected = false;
		while( (!collisionDetected) && (currentStep < Ray.NUM_STEPS) ) {
			
			currentPoint.move(Ray.STEP, rayDirection);
			if (!currentPoint.collide((ICollidable)terrainModel, null)) {
				currentPickedCollidables = collisionEngine.checkCollisions(currentPoint, null);
				collisionDetected = (currentPickedCollidables.size() > 0);
				
				if (collisionDetected) {
					
					for(ICollidable c: currentPickedCollidables) {
						
						if ( (!uniqueVisibleObjects.isEmpty()) && 
								(c instanceof AbstractBoundingVolume) && 
								(!uniqueVisibleObjects.contains(((AbstractBoundingVolume) c).getOwner().getSpatialEntityOwner().getClass())) ) {
							collisionDetected = false;
						} else 
							pickedCollidables.add(c);
					}
				}
			}
				
			++currentStep;
		}
		
		if (pickedCollidables != null) {
			
			for(ICollidable pickedCollidable: pickedCollidables) {
				System.out.println(pickedCollidable.getClass().getName());
				if (pickedCollidable instanceof AbstractBoundingVolume)
					return ((AbstractBoundingVolume) pickedCollidable).getOwner().getSpatialEntityOwner();
			
			}
		}
		return null;
	}
	
	public float detectTerrainHeight() {
		
		Point currentPoint = new Point(ray.getOrigin());
		final Vector3D rayDirection = ray.getDirection();
		TerrainCollisionResults collisionResults = new TerrainCollisionResults();
		int currentStep = 0;
		boolean collisionDetected = false;
		while( (!collisionDetected) && (currentStep < Ray.NUM_STEPS) ) {
			
			currentPoint.move(Ray.STEP, rayDirection);
			if (currentPoint.collide((ICollidable)terrainModel, collisionResults)) {
				return collisionResults.getTerrainHeight();
			}
			++currentStep;
		}
		return 0.0f;
	}
	
	public Vector2D detectTerrainSurfacePosition() {
		
		Point currentPoint = new Point(ray.getOrigin());
		final Vector3D rayDirection = ray.getDirection();
		TerrainCollisionResults collisionResults = new TerrainCollisionResults();
		int currentStep = 0;
		boolean collisionDetected = false;
		while( (!collisionDetected) && (currentStep < Ray.NUM_STEPS) ) {
			
			currentPoint.move(Ray.STEP, rayDirection);
			if (currentPoint.collide((ICollidable)terrainModel, collisionResults)) {
				return collisionResults.getTerrainCollisionCoordinates();
			}
			++currentStep;
		}
		return null;
	}

	public static void makeUniqueUnPickable(final Class<?> objectClass) {
		uniqueVisibleObjects.remove(objectClass);
	}
	
	public static void makeUniquePickable(final Class<?> objectClass) {
		uniqueVisibleObjects.add(objectClass);
	}
}
