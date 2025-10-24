/**
 * 
 */
package it.unical.logic_santos.io.universe;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * @author Agostino
 *
 */
public class FileWorldBuilder implements IWorldBuilder {

	private JFrame frame=null;
	
	public FileWorldBuilder(JFrame _frame) {
		this.frame = _frame;
	}
	
	@Override
	public boolean buildNewWorld(String cityName) {
		final String directory = chooseDirectory();
		System.out.println("CHOOSE DIR: " + directory);
		if (directory == null)
			return false;
		Path masterFilePath = FileSystems.getDefault().getPath(directory+IOUniverseConfig.FOLDER_SEPARATOR+cityName+"."+IOUniverseConfig.MASTER_FILE_EXTENSION, ""); 
		try {
			
			Path objectsFilePath = FileSystems.getDefault().getPath(directory+IOUniverseConfig.FOLDER_SEPARATOR+IOUniverseConfig.OBJECTS_FILE_NAME+"."+IOUniverseConfig.OBJECTS_FILE_EXTENSION, "");
			
			PrintWriter outObjects = new PrintWriter(Files.newOutputStream(masterFilePath));
			outObjects.println(objectsFilePath.toString()); /* printing objects file */
			outObjects.close();	
			
			/* roads network */
			Path roadsNetworkFilePath = FileSystems.getDefault().getPath(directory+IOUniverseConfig.FOLDER_SEPARATOR+IOUniverseConfig.ROADS_NETWORK_FILE_NAME+"."+IOUniverseConfig.ROADS_NETWORK_FILE_EXTENSION, "");
			
			PrintWriter outRoadsNetwork = new PrintWriter(Files.newOutputStream(masterFilePath));
			outRoadsNetwork.println(roadsNetworkFilePath.toString()); /* printing roads network file */
			outRoadsNetwork.close();	
			
			DataOutputStream roadsNetworkOut = new DataOutputStream(Files.newOutputStream(roadsNetworkFilePath));
			roadsNetworkOut.writeInt(0);
			roadsNetworkOut.writeInt(0);
			roadsNetworkOut.close();
			
			/* paths network */
			Path pathsNetworkFilePath = FileSystems.getDefault().getPath(directory+IOUniverseConfig.FOLDER_SEPARATOR+IOUniverseConfig.PATHS_NETWORK_FILE_NAME+"."+IOUniverseConfig.PATHS_NETWORK_FILE_EXTENSION, "");
			
			PrintWriter ouPathsNetwork = new PrintWriter(Files.newOutputStream(masterFilePath));
			outRoadsNetwork.println(pathsNetworkFilePath.toString()); /* printing roads network file */
			ouPathsNetwork.close();	
			
			DataOutputStream pathsNetworkOut = new DataOutputStream(Files.newOutputStream(pathsNetworkFilePath));
			pathsNetworkOut.writeInt(0);
			pathsNetworkOut.writeInt(0);
			pathsNetworkOut.close();
			
			RootWorldFile.getInstance().setMasterFileName(masterFilePath.toString());
			JOptionPane.showMessageDialog(null, "You city is successfully created! " + cityName + " is now a new City");
			System.out.println(masterFilePath.toString());
			return true;
			
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	public String chooseDirectory() {
		JFileChooser directoryChooser = new JFileChooser();
		directoryChooser.setDialogTitle("Select Folder");
		directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		directoryChooser.setAcceptAllFileFilterUsed(false);
		final int status = directoryChooser.showOpenDialog(frame);
		
		if (status == JFileChooser.APPROVE_OPTION) {
			return directoryChooser.getSelectedFile().getAbsolutePath();
		}
		return null;
	}

}
