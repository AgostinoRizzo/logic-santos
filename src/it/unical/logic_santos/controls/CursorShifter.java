/**
 * 
 */
package it.unical.logic_santos.controls;

import it.unical.logic_santos.collision.ICollisionDetectionEngine;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.terrain.modeling.ITerrainChunkModel;
import it.unical.logic_santos.toolkit.math.Vector2D;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class CursorShifter extends CursorPicker {

	private ISpatialEntity selectedSpatialEntity=null;
	private float currentTerrainHeight=0.0f;
	private Vector2D currentTerrainSurfacePosition=null;
	
	public CursorShifter(IPickerCamera _pickerCamera, ICollisionDetectionEngine _collisionEngine, ITerrainChunkModel _terrainModel) {
		super(_pickerCamera, _collisionEngine, _terrainModel);
		this.selectedSpatialEntity=null;
		this.currentTerrainHeight=0.0f;
		this.currentTerrainSurfacePosition=null;
		// TODO Auto-generated constructor stub
	}

	public ISpatialEntity getSelectedSpatialEntity() {
		return selectedSpatialEntity;
	}

	public void setSelectedSpatialEntity(ISpatialEntity selectedSpatialEntity) {
		this.selectedSpatialEntity = selectedSpatialEntity;
	}

	public float getCurrentTerrainHeight() {
		return currentTerrainHeight;
	}
	
	public Vector2D getCurrentTerrainSurfacePosition() {
		if (currentTerrainSurfacePosition == null)
			return null;
		return currentTerrainSurfacePosition.clone();
	}

	@Override
	public ISpatialEntity pickSpatialEntity() {
		selectedSpatialEntity = super.pickSpatialEntity();
		currentTerrainHeight = super.detectTerrainHeight();
		return selectedSpatialEntity;
	}
	
	@Override
	public float detectTerrainHeight() {
		currentTerrainHeight = super.detectTerrainHeight();
		return currentTerrainHeight;
	}
	
	@Override
	public Vector2D detectTerrainSurfacePosition() {
		currentTerrainSurfacePosition = super.detectTerrainSurfacePosition();
		return currentTerrainSurfacePosition;
	}
	
	public boolean releaseSpatialEntity() {
		if (selectedSpatialEntity != null) {
			selectedSpatialEntity=null;
			return true;
		}
		return false;
	}
	
	public boolean isSpatialEntitySelected() {
		return (selectedSpatialEntity != null);
	}
	
	public void moveSelectedSpatialEntity() {
		if (isSpatialEntitySelected()) {
			detectTerrainHeight();
			detectTerrainSurfacePosition();
			if (currentTerrainSurfacePosition != null) {
				selectedSpatialEntity.setSpatialTranslation(new Vector3D(currentTerrainSurfacePosition.getX(), currentTerrainHeight, currentTerrainSurfacePosition.getY()));
				//System.out.println(currentTerrainSurfacePosition.getX());
			}
		}
	}
	
	
	
	

}
