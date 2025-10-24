/**
 * 
 */
package it.unical.logic_santos.editor.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Agostino
 *
 */
public class EditRoadNetworkEventManager implements ActionListener {

	//private JFrame mainWindow=null;
	//private JPanel toolsPanel=null;
	
	JLabel lblRoadChunk = new JLabel("Road Chunk: ");
	JComboBox<String> cmbRoadChunk = new JComboBox<String>();
	
	
	public EditRoadNetworkEventManager(JFrame _mainWindow, JPanel _toolsPanel) {
		//this.mainWindow = _mainWindow;
		//this.toolsPanel = _toolsPanel;
		
		cmbRoadChunk.addItem("Road1");
		cmbRoadChunk.addItem("Road2");
		cmbRoadChunk.addItem("Road3");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//if (application.getEditorStatus() == EditorStatus.UNLOAD) {
		//	JOptionPane.showMessageDialog(null, "City World not loaded.");
		//	return;
		//}
		/*if (e.getActionCommand().compareTo("Edit Road Network") == 0) {
			loadToolsComponents();
		}*/
	}
	
	
	/*private void loadToolsComponents() {
		
		toolsPanel.removeAll();
		
		toolsPanel.add(lblRoadChunk);
		toolsPanel.add(cmbRoadChunk);
		
		toolsPanel.updateUI();
	}*/

}
