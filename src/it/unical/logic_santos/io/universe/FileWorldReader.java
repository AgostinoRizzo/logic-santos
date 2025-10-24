/**
 * 
 */
package it.unical.logic_santos.io.universe;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;

import it.unical.logic_santos.collision.ICollisionDetectionEngine;
import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.gui.application.LogicSantosEditableApplication;
import it.unical.logic_santos.gui.screen.LoadingScreen;
import it.unical.logic_santos.physics.activity.PhysicsSpace;
import it.unical.logic_santos.roads_network.PathsNetwork;
import it.unical.logic_santos.roads_network.RoadArc;
import it.unical.logic_santos.roads_network.RoadNode;
import it.unical.logic_santos.roads_network.RoadsNetwork;
import it.unical.logic_santos.spatial_entity.AbstractBuilding;
import it.unical.logic_santos.spatial_entity.AbstractStaticSpatialEntity;
import it.unical.logic_santos.spatial_entity.ClassicTree;
import it.unical.logic_santos.spatial_entity.CoffeeShop;
import it.unical.logic_santos.toolkit.math.Vector3D;
import it.unical.logic_santos.universe.AbstractWorld;

/**
 * @author Agostino
 *
 */
public class FileWorldReader implements IWorldReader {
	
	private JFrame frame=null;
	private LogicSantosApplication application=null;
	
	public FileWorldReader(JFrame _frame, LogicSantosEditableApplication _application) {
		this.frame = _frame;
		this.application = _application;
	}
	
	public FileWorldReader(LogicSantosApplication _application) {
		this.application = _application;
	}
	
	Path filePath=null;
	public boolean fetchWorldLocation(AbstractWorld world) {
		List<Integer> exceptionFlags = new ArrayList<Integer>();
		
		if (!RootWorldFile.getInstance().chooseMasterFile(frame, exceptionFlags)) {
			if (exceptionFlags.isEmpty())
				JOptionPane.showMessageDialog(null, "File format not supported");
			return false;
		}
		if (!RootWorldFile.getInstance().isMasterFileChosen()) {
			JOptionPane.showMessageDialog(null, "Root World File not chosen! Please create a new City World or load an existing one.");
			return false;
		}
		
		filePath = FileSystems.getDefault().getPath(RootWorldFile.getInstance().getMasterFileName(), "");
		return true;
	}
	
	@Override
	public boolean readWorldFromEditor(AbstractWorld world) {
		Path filePath = FileSystems.getDefault().getPath(IOUniverseConfig.CITY_WORLD_PATH, ""); 
		return read(world, filePath, true, true);
	}

	@Override
	public boolean readWorld(AbstractWorld world) {
		Path filePath = FileSystems.getDefault().getPath(IOUniverseConfig.CITY_WORLD_PATH, ""); 
		return read(world, filePath, false, false);
	}
	
	private boolean read(AbstractWorld world, Path filePath, final boolean onEditor, final boolean showDialog) {
		try {
			
			application.clearWorld();
			
			/* open master file */
			BufferedReader masterIn = new BufferedReader(new FileReader(filePath.toString()));
			final String objectsFileName = masterIn.readLine();
			final Path objectsFilePath = FileSystems.getDefault().getPath(objectsFileName, ""); 
			
			final String roadsNetworkFileName = masterIn.readLine();
			final Path roadsNetworkFilePath = FileSystems.getDefault().getPath(roadsNetworkFileName, ""); 
			
			final String pathsNetworkFileName = masterIn.readLine();
			final Path pathsNetworkFilePath = FileSystems.getDefault().getPath(pathsNetworkFileName, ""); 
			
			/* open objects file */
			DataInputStream objectsIn = new DataInputStream(Files.newInputStream(objectsFilePath));
			Collection<AbstractStaticSpatialEntity> staticSpatialEntities = world.getStaticSpatialEntities();
			ICollisionDetectionEngine collisionEngine = world.getCollisionEngine();
			staticSpatialEntities.clear();
			collisionEngine.removeCollidables();
			
			application.setLoadingProgress(0.0f, "World Objects");
			int totalAvailableBytes = objectsIn.available();
			
			HashMap< Class< ? extends AbstractStaticSpatialEntity >, ArrayList< Vector3f > > doubleMap=new HashMap<>();
			
			while( objectsIn.available() > 0 ) {
				Vector3D position = new Vector3D();
				position.readFromDataInputStream(objectsIn);
				
				AbstractStaticSpatialEntity newSpatialEntity = (AbstractStaticSpatialEntity) Class.forName(ClassEncoder.encodeClass(objectsIn)).newInstance();
				
				boolean insert = true;
				ArrayList< Vector3f > positions = doubleMap.get( newSpatialEntity.getClass() );
				if ( positions!=null ) {
					for( Vector3f pos: positions )
						if ( pos.equals( position.toVector3f() ) )
						insert=false; 
				} else if ( doubleMap.containsKey( newSpatialEntity.getClass() ) )
					doubleMap.get( newSpatialEntity.getClass() ).add( position.toVector3f() );
				else {
					ArrayList< Vector3f > value = new ArrayList<>();
					value.add( position.toVector3f() );
					doubleMap.put( newSpatialEntity.getClass(), value );
				}
				
				if ( insert ) {
					position.add( newSpatialEntity.getSpatialEntityTranslationAdjustment() );
					newSpatialEntity.setSpatialTranslation(position);
					world.addSpatialEntity(newSpatialEntity);
				}
				
				//if ( (!onEditor) && (newSpatialEntity.isStatic()) )
				//	addStaticSpatialEntityInPhysicsSpace( newSpatialEntity );
				
				application.setLoadingProgress( 0.6f*( 1.0f-(((float)objectsIn.available())/((float)totalAvailableBytes))), "World Objects (" + newSpatialEntity.getName() + ")" );
			}
			
			
			/* open roads network file */
			DataInputStream roadsNetworkIn = new DataInputStream(Files.newInputStream(roadsNetworkFilePath));
			RoadsNetwork roadsNetwork = application.getRoadsNetwork();
			roadsNetwork.clear();
			
			totalAvailableBytes = roadsNetworkIn.available();
			final int nodesCount = roadsNetworkIn.readInt();
			
			for(int i=0; i<nodesCount; ++i) {
				RoadNode node = new RoadNode();
				node.readFromDataInputStream(roadsNetworkIn);
				roadsNetwork.addNode(node);
				if (onEditor)
					application.getStaticPathEntityViewer().attachSpatialEntity(node); //application.getRootNode().attachChild(((ModelBasedPhysicalExtension) node.getAbstractPhysicalExtension()).getModelSpatial());
				
				application.setLoadingProgress( 0.5f + (0.1f*( 1.0f-(((float)roadsNetworkIn.available())/((float)totalAvailableBytes)))), "Roads Network" );
			}
			
			final int arcsCount = roadsNetworkIn.readInt();
			
			for(int i=0; i<arcsCount; ++i) {
				RoadArc arc = RoadArc.newFromDataInputStream(roadsNetworkIn, roadsNetwork, false, onEditor);
				roadsNetwork.addArc(arc);
				if (onEditor)
					application.getStaticPathEntityViewer().attachSpatialEntity(arc); //application.getRootNode().attachChild(((ModelBasedPhysicalExtension) arc.getAbstractPhysicalExtension()).getModelSpatial());
			
				application.setLoadingProgress( 0.6f + (0.1f*( 1.0f-(((float)roadsNetworkIn.available())/((float)totalAvailableBytes)))), "Roads Network" );
			}
			roadsNetwork.deselectPreviousNode();
			
			/* open paths network file */
			DataInputStream pathsNetworkIn = new DataInputStream(Files.newInputStream(pathsNetworkFilePath));
			PathsNetwork pathsNetwork = application.getPathsNetwork(); 
			pathsNetwork.clear();
			
			totalAvailableBytes = pathsNetworkIn.available();
			final int pathsNodesCount = pathsNetworkIn.readInt();
			
			for(int i=0; i<pathsNodesCount; ++i) {
				RoadNode node = new RoadNode(true);
				node.readFromDataInputStream(pathsNetworkIn);
				pathsNetwork.addNode(node);
				if (onEditor)
					application.getStaticPathEntityViewer().attachSpatialEntity(node); //application.getRootNode().attachChild(((ModelBasedPhysicalExtension) node.getAbstractPhysicalExtension()).getModelSpatial());
			
				application.setLoadingProgress( 0.7f + (0.1f*( 1.0f-(((float)pathsNetworkIn.available())/((float)totalAvailableBytes)))), "Roads Network" );
			}
			
			final int pathsArcsCount = pathsNetworkIn.readInt();
			
			for(int i=0; i<pathsArcsCount; ++i) {
				RoadArc arc = RoadArc.newFromDataInputStream(pathsNetworkIn, pathsNetwork, true, onEditor);
				//RoadArc simmArc = new RoadArc(arc.getEndNode(), arc.getStartNode(), true, onEditor);
				pathsNetwork.addArc(arc);
				//pathsNetwork.addArc(simmArc);
				if (onEditor)
					application.getStaticPathEntityViewer().attachSpatialEntity(arc); //application.getRootNode().attachChild(((ModelBasedPhysicalExtension) arc.getAbstractPhysicalExtension()).getModelSpatial());
			
				application.setLoadingProgress( 0.8f + (0.1f*( 1.0f-(((float)pathsNetworkIn.available())/((float)totalAvailableBytes)))), "Roads Network" );
			}
			pathsNetwork.deselectPreviousNode();
			
			roadsNetworkIn.close();
			objectsIn.close();		
			masterIn.close();
			
			application.getLoadingScreen().setAssetsLoadingComplete( true );
			application.setLoadingProgress( 1.0f-0.1f, "City World creation" );
			
			if (showDialog)
				;//JOptionPane.showMessageDialog(null, "City world loaded saccessfully!");
			return true;
			
		} catch (IOException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	private void addRoadsNodeToLSApplication() {
		
	}
	
	private void addRoadsArcToLSApplication() {
		
	}
	
	private void addStaticSpatialEntityInPhysicsSpace( AbstractStaticSpatialEntity staticSpatialEntity ) {
		/* make the staticSpatialEntity physical with mass 0.0f */
		RigidBodyControl control = new RigidBodyControl( 0.0f );
		( (ModelBasedPhysicalExtension) staticSpatialEntity.getAbstractPhysicalExtension() ).getModelSpatial().addControl( control );
		PhysicsSpace.getInstance().getSpace().add( control );
	}

}
