/**
 * 
 */
package it.unical.logic_santos.io.universe;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.roads_network.RoadArc;
import it.unical.logic_santos.roads_network.RoadNode;
import it.unical.logic_santos.roads_network.RoadsNetwork;
import it.unical.logic_santos.spatial_entity.AbstractStaticSpatialEntity;
import it.unical.logic_santos.toolkit.math.Vector3D;
import it.unical.logic_santos.universe.AbstractWorld;

/**
 * @author Agostino
 *
 */
public class FileWorldWriter implements IWorldWriter {
	
	private LogicSantosApplication application=null;
	
	public FileWorldWriter(LogicSantosApplication app) {
		this.application = app;
	}
	


	@Override
	public boolean writeWorld(AbstractWorld world) {
		
		if (!RootWorldFile.getInstance().isMasterFileChosen()) {
			JOptionPane.showMessageDialog(null, "Root World File not chosen! Please create a new City World or load an existing one.");
			return false;
		}
		
		Path filePath = FileSystems.getDefault().getPath(RootWorldFile.getInstance().getMasterFileName(), ""); 
		try {
			
			/* open master file */
			BufferedReader masterIn = new BufferedReader(new FileReader(filePath.toString()));
			final String objectsFileName = masterIn.readLine();
			final Path objectsFilePath = FileSystems.getDefault().getPath(objectsFileName, ""); 
			
			final String roadsNetworkFileName = masterIn.readLine();
			final Path roadsNetworkFilePath = FileSystems.getDefault().getPath(roadsNetworkFileName, ""); 
			
			final String pathsNetworkFileName = masterIn.readLine();
			final Path pathsNetworkFilePath = FileSystems.getDefault().getPath(pathsNetworkFileName, ""); 
			
			/* open objects file */
			DataOutputStream objectsOut = new DataOutputStream(Files.newOutputStream(objectsFilePath));
			final Collection<AbstractStaticSpatialEntity> staticSpatialEntities = world.getStaticSpatialEntities();
			for(AbstractStaticSpatialEntity spatialEntity: staticSpatialEntities) {
			
				Vector3D position = spatialEntity.getSpatialTranslation();
				position.writeOnDataOutputStream(objectsOut);
				
				ClassEncoder classEncoder = new ClassEncoder(spatialEntity.getClass().getName());
				classEncoder.writeOnDataOutputStream(objectsOut);
	
			}
			objectsOut.close();		
			
			/* open roads network file */
			DataOutputStream roadsNetworkOut = new DataOutputStream(Files.newOutputStream(roadsNetworkFilePath));
			RoadsNetwork roadsNetwork = application.getRoadsNetwork();
			
			final int nodesCount = roadsNetwork.getRoadsNodes().size();
			roadsNetworkOut.writeInt(nodesCount);
			
			for(RoadNode v: roadsNetwork.getRoadsNodes())
				v.writeOnDataOutputStream(roadsNetworkOut);
			
			final int arcsCount = roadsNetwork.getRoadsArcs().size();
			roadsNetworkOut.writeInt(arcsCount);
			
			for(RoadArc e: roadsNetwork.getRoadsArcs())
				e.writeOnDataOutputStream(roadsNetworkOut);
			roadsNetworkOut.close();
			
			/* open paths network file */
			DataOutputStream pathsNetworkOut = new DataOutputStream(Files.newOutputStream(pathsNetworkFilePath));
			RoadsNetwork pathsNetwork = application.getPathsNetwork();
			
			final int pathNodesCount = pathsNetwork.getRoadsNodes().size();
			pathsNetworkOut.writeInt(pathNodesCount);
			
			for(RoadNode v: pathsNetwork.getRoadsNodes())
				v.writeOnDataOutputStream(pathsNetworkOut);
			
			final int pathArcsCount = pathsNetwork.getRoadsArcs().size();
			pathsNetworkOut.writeInt(pathArcsCount);
			
			for(RoadArc e: pathsNetwork.getRoadsArcs())
				e.writeOnDataOutputStream(pathsNetworkOut);
			pathsNetworkOut.close();
			
			objectsOut.close();
			masterIn.close();
			
			JOptionPane.showMessageDialog(null, "City world saved saccessfully!");
			return true;
			
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	public static String chooseFile(JFrame frame) {
		JFileChooser fileChooser = new JFileChooser();
		final int status = fileChooser.showOpenDialog(frame);
		
		if (status == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile().getAbsolutePath();
		}
		return null;
	}

}
