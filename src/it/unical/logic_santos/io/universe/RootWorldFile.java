/**
 * 
 */
package it.unical.logic_santos.io.universe;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FileChooserUI;

/**
 * @author Agostino
 *
 */
public class RootWorldFile {
			
	private String masterFileName=null;
	
	private static RootWorldFile uniqueInstance = null;
	
	private RootWorldFile() {
		this.masterFileName = null;
	}
	
	public static RootWorldFile getInstance() {
		if (uniqueInstance == null)
			uniqueInstance = new RootWorldFile();
		return uniqueInstance;
	}
	
	public String getMasterFileName() {
		return masterFileName;
	}
	
	public void setMasterFileName(final String fileName) {
		masterFileName = fileName;
	}
	
	public boolean isMasterFileChosen() {
		if (masterFileName == null)
			return false;
		return (masterFileName.length() > 0);
	}
	
	
	public boolean chooseMasterFile(JFrame frame, List<Integer> exceptionFlags) {
		JFileChooser fileChooser = new JFileChooser(IOUniverseConfig.ROOT_ASSET_CITY_WORLD_DIRECTORY);
		FileFilter fileFilter = new FileFilter() {
			
			@Override
			public String getDescription() {
				return "Logic Santos City";
			}
			
			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return false;
				return f.getName().toLowerCase().endsWith(".lsc");
			}
		};
		fileChooser.addChoosableFileFilter(fileFilter);
		final int status = fileChooser.showOpenDialog(frame);
		
		if (status == JFileChooser.APPROVE_OPTION) {
			if (fileFilter.accept(fileChooser.getSelectedFile())) {
				masterFileName = fileChooser.getSelectedFile().getAbsolutePath();
				return true;
			}
		} else if (status == JFileChooser.CANCEL_OPTION) 
			exceptionFlags.add(1);
		return false;
	}
	
}
