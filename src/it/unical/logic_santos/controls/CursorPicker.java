/**
 * 
 */
package it.unical.logic_santos.controls;

import it.unical.logic_santos.collision.ICollisionDetectionEngine;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.terrain.modeling.ITerrainChunkModel;
import it.unical.logic_santos.toolkit.math.Ray;
import it.unical.logic_santos.toolkit.math.Vector2D;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class CursorPicker extends SpatialEntityPicker {

	protected IPickerCamera pickerCamera=null;
	
	public CursorPicker(IPickerCamera _pickerCamera, ICollisionDetectionEngine _collisionEngine, ITerrainChunkModel _terrainModel) {
		super(_collisionEngine, _terrainModel);
		this.pickerCamera = _pickerCamera;
		this.ray = CursorPicker.calculateRay(this.pickerCamera);
	}
	
	@Override
	public ISpatialEntity pickSpatialEntity() {
		CursorPicker.updateRay(pickerCamera, ray);
		return super.pickSpatialEntity();
	}
	
	@Override
	public float detectTerrainHeight() {
		CursorPicker.updateRay(pickerCamera, ray);
		return super.detectTerrainHeight();
	}
	
	@Override
	public Vector2D detectTerrainSurfacePosition() {
		CursorPicker.updateRay(pickerCamera, ray);
		return super.detectTerrainSurfacePosition();
	}

	public static Ray calculateRay(IPickerCamera pickerCamera) {
		//final Vector2D cursorPosition = pickerCamera.getCursorPosition();
		final Vector3D origin = pickerCamera.getCameraPosition(); //pickerCamera.getWorldPosition(cursorPosition, 0.0f).clone();
		final Vector3D direction = pickerCamera.getCameraDirection(); //pickerCamera.getWorldPosition(cursorPosition, 1.0f).minus(origin).normalize();
		return new Ray(origin, direction);
	}
	
	public static void updateRay(IPickerCamera pickerCamera, Ray ray) {
		//final Vector2D cursorPosition = pickerCamera.getCursorPosition();
		final Vector3D origin = pickerCamera.getCameraPosition(); //pickerCamera.getWorldPosition(cursorPosition, 0.0f).clone();
		final Vector3D direction = pickerCamera.getCameraDirection(); //pickerCamera.getWorldPosition(cursorPosition, 1.0f).minus(origin).normalize();
		ray.setOrigin(origin);
		ray.setDirection(direction);
	}

}
