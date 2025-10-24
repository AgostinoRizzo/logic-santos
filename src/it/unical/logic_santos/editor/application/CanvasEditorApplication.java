/**
 * 
 */
package it.unical.logic_santos.editor.application;

import java.awt.Dimension;

import javax.swing.JPanel;

import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import it.unical.logic_santos.controls.SpatialEntityPicker;
import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.editor.gui.components.EditorStatus;
import it.unical.logic_santos.gui.application.LogicSantosEditableApplication;
import it.unical.logic_santos.gui.roads_network.GraphicalRoadNode;
import it.unical.logic_santos.roads_network.PathsNetwork;
import it.unical.logic_santos.roads_network.RoadArc;
import it.unical.logic_santos.roads_network.RoadNode;
import it.unical.logic_santos.roads_network.RoadsNetwork;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.toolkit.math.Vector2D;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class CanvasEditorApplication extends LogicSantosEditableApplication {

	private JmeCanvasContext ctx = null;
	private LogicSantosEditorApplication editorApplication = null;
	
	
	public CanvasEditorApplication(LogicSantosEditorApplication editorApplication) {
		super();
		this.editorApplication = editorApplication;
	}
	
	public void loadComponents() {
		AppSettings settings = new AppSettings(true);
		settings.setWidth(EditorApplicationConfiguration.EDITABLE_APP_WIDTH);
		settings.setHeight(EditorApplicationConfiguration.EDITABLE_APP_HEIGHT);
				
		setSettings(settings);
		createCanvas();
		ctx = (JmeCanvasContext) getContext(); 
		ctx.setSystemListener(this);
		Dimension dim = new Dimension(EditorApplicationConfiguration.EDITABLE_APP_WIDTH, EditorApplicationConfiguration.EDITABLE_APP_HEIGHT);
		ctx.getCanvas().setPreferredSize(dim);
	}
	
	public void addCanvasContextToPanel(JPanel convasPanel) {
		convasPanel.add(ctx.getCanvas());
	}
	
	public void deselectFirstNode() {
		firstSelectedNode = null;
	}
	
	public void start() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			
			public void run() {
				
				loadComponents();
				startCanvas();
				
			}
			
		});
	}
	
	@Override
	protected void manageClickEvent() {
		
		if (editorApplication.getEditorStatus() == EditorStatus.ADD_OBJECT) {
			manageAddObjectClick();
		} else if (editorApplication.getEditorStatus() == EditorStatus.DELETE_OBJECT) {
			manageDeleteObjectClick();
		} else if (editorApplication.getEditorStatus() == EditorStatus.MODIFY_OBJECT) {
			manageModifyObjectClick();
		} else if (editorApplication.getEditorStatus() == EditorStatus.ADD_ROADS_NETWORK_NODE) {
			manageAddRoadNetworkNodeClick(RoadsNetwork.class);
		} else if (editorApplication.getEditorStatus() == EditorStatus.REMOVE_ROADS_NETWORK_NODE) {
			manageRemoveRoadNetworkNodeClick(RoadsNetwork.class);
		} else if (editorApplication.getEditorStatus() == EditorStatus.MODIFY_ROADS_NETWORK_NODE) {
			manageModifyRoadNetworkNodeClick(RoadsNetwork.class);
		} else if (editorApplication.getEditorStatus() == EditorStatus.ADD_ROADS_NETWORK_ARC) {
			manageAddRoadNetworkArcClick(RoadsNetwork.class);
		} else if (editorApplication.getEditorStatus() == EditorStatus.REMOVE_ROADS_NETWORK_ARC) {
			manageRemoveRoadNetworkArcClick(RoadsNetwork.class);
			
		} else if (editorApplication.getEditorStatus() == EditorStatus.ADD_PATHS_NETWORK_NODE) {
			manageAddRoadNetworkNodeClick(PathsNetwork.class);
		} else if (editorApplication.getEditorStatus() == EditorStatus.REMOVE_PATHS_NETWORK_NODE) {
			manageRemoveRoadNetworkNodeClick(PathsNetwork.class);
		} else if (editorApplication.getEditorStatus() == EditorStatus.MODIFY_PATHS_NETWORK_NODE) {
			manageModifyRoadNetworkNodeClick(PathsNetwork.class);
		} else if (editorApplication.getEditorStatus() == EditorStatus.ADD_PATHS_NETWORK_ARC) {
			manageAddRoadNetworkArcClick(PathsNetwork.class);
		} else if (editorApplication.getEditorStatus() == EditorStatus.REMOVE_PATHS_NETWORK_ARC) {
			manageRemoveRoadNetworkArcClick(PathsNetwork.class);
		}
	}

	private void manageAddObjectClick() {
		
		if (editorApplication.editObjectsEventManager.getSelectedEntity() != null) {
			ISpatialEntity newEntity = editorApplication.editObjectsEventManager.getSelectedEntity().cloneSpatialEntity();
			final Vector2D terrainSurfacePosition = cursorShifter.detectTerrainSurfacePosition();
			
			if (terrainSurfacePosition != null) {
				final float terrainSurfaceHeight = cursorShifter.detectTerrainHeight();
				newEntity.setSpatialTranslation(new Vector3D(terrainSurfacePosition.getX(), terrainSurfaceHeight, terrainSurfacePosition.getY()));
				logicSantosWorld.addSpatialEntity(newEntity);
				Spatial newModelSpatial = ((ModelBasedPhysicalExtension) newEntity.getAbstractPhysicalExtension()).getModelSpatial();
				// TODO: attachChild by master thread!
				//GraphicalLoader.getInstance().attachSpatialToNode(newModelSpatial, GraphicalRootNode.getInstance().getRootNode());
				//GraphicalLoader.getInstance().attachSpatialToNode(newModelSpatial, rootNode);
				rootNode.attachChild(newModelSpatial);
			}
		}
	}
	
	private void manageDeleteObjectClick() {
		cursorShifter.pickSpatialEntity();
		if (cursorShifter.isSpatialEntitySelected()) {
			
			ISpatialEntity selectedEntity = cursorShifter.getSelectedSpatialEntity();
			staticSpatialEntityViewer.detachSpatialEntity( selectedEntity );
			logicSantosWorld.removeSpatialEntity(selectedEntity);
			Spatial modelSpatial = ((ModelBasedPhysicalExtension) selectedEntity.getAbstractPhysicalExtension()).getModelSpatial();
			rootNode.detachChild(modelSpatial);
		}
	}
	
	private void manageModifyObjectClick() {
		if (cursorShifter.isSpatialEntitySelected()) {
			System.out.println("Releasing");
			cursorShifter.releaseSpatialEntity();
		} else {
			System.out.println("Picking");
			cursorShifter.pickSpatialEntity();
		}
	}
	
	private void manageAddRoadNetworkNodeClick(final Class<? extends RoadsNetwork> networkClass) {
		final Vector2D terrainSurfacePosition = cursorShifter.detectTerrainSurfacePosition();
		
		RoadsNetwork network = getRoadsNetwork(networkClass);
		
		final boolean decoreToPath = (networkClass == PathsNetwork.class);
		
		if (terrainSurfacePosition != null) {
			final float terrainSurfaceHeight = cursorShifter.detectTerrainHeight();
			Vector3D nodePosition = new Vector3D(terrainSurfacePosition.getX(), terrainSurfaceHeight + GraphicalRoadNode.Y_ADJUSTMENT, terrainSurfacePosition.getY());
			ISpatialEntity node = new RoadNode(nodePosition, decoreToPath);
			rootNode.attachChild(((ModelBasedPhysicalExtension) node.getAbstractPhysicalExtension()).getModelSpatial());
			
			if ( network.usePreviousNode() && network.hasPreviuosNodeSelected()) {
				final int previuosNodeId = network.getPreviousNode().getId();
				final RoadNode previousNode = network.getNode(previuosNodeId);
				
				network.addNode((RoadNode) node);
				
				RoadArc arc = new RoadArc(previousNode, (RoadNode) node, decoreToPath, true);
				rootNode.attachChild( ((ModelBasedPhysicalExtension) arc.getAbstractPhysicalExtension()).getModelSpatial() );
				
				network.addArc(arc);
			} else 
				network.addNode((RoadNode) node);
		}
	}
	
	private void manageRemoveRoadNetworkNodeClick(final Class<? extends RoadsNetwork> networkClass) {
		
		RoadsNetwork network = getRoadsNetwork(networkClass);
		
		SpatialEntityPicker.makeUniquePickable(RoadNode.class);
		network.makeArcsUnCollidable();
		cursorShifter.pickSpatialEntity();
		if (cursorShifter.isSpatialEntitySelected() && 
				(cursorShifter.getSelectedSpatialEntity() instanceof RoadNode) && 
				(network.getRoadsNodes().contains(cursorShifter.getSelectedSpatialEntity()))) {
			
			ISpatialEntity selectedNodeEntity = cursorShifter.getSelectedSpatialEntity();
			network.removeNode((RoadNode) selectedNodeEntity);
			Spatial modelSpatial = ((ModelBasedPhysicalExtension) selectedNodeEntity.getAbstractPhysicalExtension()).getModelSpatial();
			rootNode.detachChild(modelSpatial);
			staticPathEntityViewer.detachSpatialEntity(selectedNodeEntity);
		}
		cursorShifter.releaseSpatialEntity();
		network.makeArcsCollidable();
		SpatialEntityPicker.makeUniqueUnPickable(RoadNode.class);
		/*
		ISpatialEntity newEntity = editorApplication.editObjectsEventManager.getSelectedEntity().cloneSpatialEntity();
		final Vector2D terrainSurfacePosition = cursorShifter.detectTerrainSurfacePosition();
		
		if (terrainSurfacePosition != null) {
			final float terrainSurfaceHeight = cursorShifter.detectTerrainHeight();
			newEntity.setSpatialTranslation(new Vector3D(terrainSurfacePosition.getX(), terrainSurfaceHeight, terrainSurfacePosition.getY()));
			logicSantosWorld.addSpatialEntity(newEntity);
			Spatial newModelSpatial = ((ModelBasedPhysicalExtension) newEntity.getAbstractPhysicalExtension()).getModelSpatial();
			//GraphicalLoader.getInstance().attachSpatialToNode(newModelSpatial, rootNode);
			rootNode.attachChild(newModelSpatial);
		}
		 */
	}
	
	private void manageModifyRoadNetworkNodeClick(final Class<? extends RoadsNetwork> networkClass) {
		// TODO: active modify operation
		/*
		if (cursorShifter.isSpatialEntitySelected()) {
			System.out.println("Releasing");
			cursorShifter.releaseSpatialEntity();
			roadsNetwork.updateRoadArcsPositions();
		} else {
			SpatialEntityPicker.makeUnPickable(RoadArc.class);
			roadsNetwork.makeArcsUnCollidable();
			System.out.println("Picking");
			cursorShifter.pickSpatialEntity();
			roadsNetwork.makeArcsCollidable();
			SpatialEntityPicker.makePickable(RoadArc.class);
		}
		*/
	}
	
	private RoadNode firstSelectedNode=null;
	private void manageAddRoadNetworkArcClick(final Class<? extends RoadsNetwork> networkClass) {

		RoadsNetwork network = getRoadsNetwork(networkClass);
		
		final boolean decoreToPath = (networkClass == PathsNetwork.class);
		
		SpatialEntityPicker.makeUniquePickable(RoadNode.class);
		network.makeArcsUnCollidable();
		System.out.println(" ADD ARC: PICKING");
		if (firstSelectedNode == null) {
			cursorShifter.pickSpatialEntity();
			if (cursorShifter.isSpatialEntitySelected() && 
					(cursorShifter.getSelectedSpatialEntity() instanceof RoadNode) && 
					(network.getRoadsNodes().contains(cursorShifter.getSelectedSpatialEntity()))) {
				
				ISpatialEntity firstSelectedNodeEntity = cursorShifter.getSelectedSpatialEntity();
				firstSelectedNode = (RoadNode) firstSelectedNodeEntity;
				cursorShifter.releaseSpatialEntity();
				
				
			} else { System.out.println(" ADD ARC: DESELECTED");
				cursorShifter.releaseSpatialEntity();
				firstSelectedNode = null;
			}
		} else {
			cursorShifter.pickSpatialEntity();
			if (cursorShifter.isSpatialEntitySelected() && 
					(cursorShifter.getSelectedSpatialEntity() instanceof RoadNode) && 
					(network.getRoadsNodes().contains(cursorShifter.getSelectedSpatialEntity()))) {
					
				ISpatialEntity secondSelectedNodeEntity = cursorShifter.getSelectedSpatialEntity();
				RoadNode secondSelectedNode = (RoadNode) secondSelectedNodeEntity;
					
				if ( !((firstSelectedNode.getId()==secondSelectedNode.getId()) || (network.getArc(firstSelectedNode.getId(), secondSelectedNode.getId()))) ) {
					RoadArc arc = new RoadArc(firstSelectedNode, secondSelectedNode, decoreToPath, true);
					System.out.println("NEW ARC");
					network.addArc(arc);
					Spatial modelSpatial = ((ModelBasedPhysicalExtension) arc.getAbstractPhysicalExtension()).getModelSpatial();
					rootNode.attachChild(modelSpatial);
					cursorShifter.releaseSpatialEntity();
				} else {
					cursorShifter.releaseSpatialEntity();
					firstSelectedNode = null;
				}
					
			} else {
				cursorShifter.releaseSpatialEntity();
				firstSelectedNode = null;
			}
		}
		
		network.makeArcsCollidable();
		SpatialEntityPicker.makeUniqueUnPickable(RoadNode.class);
		
	}
	
	private void manageRemoveRoadNetworkArcClick(final Class<? extends RoadsNetwork> networkClass) {
		
		RoadsNetwork network = getRoadsNetwork(networkClass);
		
		SpatialEntityPicker.makeUniquePickable(RoadArc.class);
		network.makeNodesUnCollidable();
		cursorShifter.pickSpatialEntity();
		if (cursorShifter.isSpatialEntitySelected() && 
				(cursorShifter.getSelectedSpatialEntity() instanceof RoadArc) && 
				(network.getRoadsArcs().contains((ISpatialEntity) cursorShifter.getSelectedSpatialEntity()))) {
			
			ISpatialEntity selectedNodeEntity = cursorShifter.getSelectedSpatialEntity();
			network.removeArc((RoadArc) selectedNodeEntity);
			Spatial modelSpatial = ((ModelBasedPhysicalExtension) selectedNodeEntity.getAbstractPhysicalExtension()).getModelSpatial();
			rootNode.detachChild(modelSpatial);
			staticPathEntityViewer.detachSpatialEntity(selectedNodeEntity);
		} else 
			cursorShifter.releaseSpatialEntity();
		network.makeNodesCollidable();
		SpatialEntityPicker.makeUniqueUnPickable(RoadArc.class);
		
	}

	private RoadsNetwork getRoadsNetwork(final Class<? extends RoadsNetwork> networkClass) {
		if (networkClass == RoadsNetwork.class)
			return roadsNetwork;
		else if (networkClass == PathsNetwork.class)
			return pathsNetwork;
		return null;
	}
}
