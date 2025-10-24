/**
 * 
 */
package it.unical.logic_santos.editor.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import it.unical.logic_santos.editor.application.LogicSantosEditorApplication;
import it.unical.logic_santos.io.universe.FileWorldBuilder;
import it.unical.logic_santos.io.universe.FileWorldReader;
import it.unical.logic_santos.io.universe.FileWorldWriter;

/**
 * @author Agostino
 *
 */
public class FileMenuEventManager implements ActionListener {

	
	private JFrame mainFrame=null;
	private LogicSantosEditorApplication application=null;
	
	
	public FileMenuEventManager(JFrame _mainFrame, LogicSantosEditorApplication _application) {
		this.mainFrame = _mainFrame;
		this.application = _application;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		if (command.equals("New City World")) {
			manageNewCityWorld();
		} else if (command.equals("Load City World")) {
			manageLoadCityWorld();
		} else if (command.equals("Save City World")) {
			if (application.getEditorStatus()!=EditorStatus.UNLOAD)
				manageSaveCityWorld();
			else
				JOptionPane.showMessageDialog(null, "City World not loaded.");
		} else if (command.equals("Exit")) {
			// TODO: exit operation
		}
		
	}
	
	private void manageSaveCityWorld() {
		FileWorldWriter fileWorldWriter = new FileWorldWriter(application.getCanvasEditor());
		fileWorldWriter.writeWorld(application.getCanvasEditor().getLogicSantosWorld());
	}
	
	private void manageLoadCityWorld() {
		application.loadCanvas();
		FileWorldReader fileWorldReader = new FileWorldReader(mainFrame, application.getCanvasEditor());
		if (fileWorldReader.fetchWorldLocation(application.getCanvasEditor().getLogicSantosWorld())) {
			
			
			application.getCanvasEditor().waitSimpleInit();
			fileWorldReader.readWorldFromEditor(application.getCanvasEditor().getLogicSantosWorld());
			application.setEditorStatus(EditorStatus.NDEF);
			application.showCanvasEditor();
			application.getCanvasEditor().configureStaticSpatialEntityViewers();
			//application.getTextAreaSettings().setText(adjustSettingsString(application.getSettingsString()));
		}
	}
	
	private void manageNewCityWorld() {
		CityNameInputDialog input = new CityNameInputDialog();
		if (input.showInputDialog()) {
			System.out.println(input.getChoosenCityName());
			FileWorldBuilder worldBuilder = new FileWorldBuilder(mainFrame);
			application.loadCanvas();
			worldBuilder.buildNewWorld(input.getChoosenCityName());
			application.showCanvasEditor();
		}
		
	}
	
	/*private String adjustSettingsString(final String settings) {
		StringBuilder builder = new StringBuilder();
		return settings;
	}*/

}
