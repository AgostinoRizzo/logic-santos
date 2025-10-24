/**
 * 
 */
package it.unical.logic_santos.editor.gui.components;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import it.unical.logic_santos.editor.application.LogicSantosEditorApplication;

/**
 * @author Agostino
 *
 */
public class EditRoadNetworkManager implements ActionListener {

	//private JFrame mainWindow=null;
	private JPanel toolsPanel=null;
	
	private JPanel toolsWidgetPanel=new JPanel();
	private JPanel descriptionWidgetPanel=new JPanel(new BorderLayout());
	private JPanel categoriesPanel = new JPanel();
	//private JPanel prototypesPanel = new JPanel(new BorderLayout());
	//private JPanel listPrototypesPanel = new JPanel(new BorderLayout());
	
	private JLabel lblOperation = new JLabel("Roads Operation: ");
	//private JLabel lblObjectPrototypes = new JLabel("Object Prototypes: ");
	private JComboBox<String> cmbOperations = new JComboBox<String>();
	private JButton btnConnectPreviousNode = new JButton("Deselect Previous Node");
	private JCheckBox cbUsePreviousNodeSelection = new JCheckBox("Connect Previous Node");
	//private JList<String> lstObjects = new JList<String>();
	//private List<String> objectsDataList = new LinkedList<String>();
	
	//private ISpatialEntity selectedEntity=null;
	
	private LogicSantosEditorApplication application=null;
	
	
	public EditRoadNetworkManager(LogicSantosEditorApplication app, JFrame _mainWindow, JPanel _toolsPanel) {
		this.application = app;
		//this.mainWindow = _mainWindow;
		this.toolsPanel = _toolsPanel;
		
		this.toolsWidgetPanel.setLayout(new BoxLayout(this.toolsWidgetPanel,BoxLayout.Y_AXIS));
		//this.toolsPanel.add(this.toolsWidgetPanel, BorderLayout.NORTH);
		//this.toolsPanel.add(this.descriptionWidgetPanel, BorderLayout.CENTER);
		
		setItalicLabel(lblOperation);
		
		cmbOperations.addItem("Add Node");
		cmbOperations.addItem("Remove Node");
		cmbOperations.addItem("Modify Node");
		cmbOperations.addItem("Add Arc");
		cmbOperations.addItem("Remove Arc");
		
		cbUsePreviousNodeSelection.setSelected(true);
		
		cmbOperations.addActionListener(this);
		btnConnectPreviousNode.addActionListener(this);
		cbUsePreviousNodeSelection.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (application.getEditorStatus() == EditorStatus.UNLOAD) {
			JOptionPane.showMessageDialog(null, "City World not loaded.");
			return;
		}
		
		if (e.getActionCommand().compareTo("Edit Roads Network") == 0) {
			loadToolsComponents();
		} else if (e.getActionCommand().compareTo("comboBoxChanged") == 0) {
			if (cmbOperations.getSelectedIndex() == 0) {
				addRoadNetworkNode();
			} else if (cmbOperations.getSelectedIndex() == 1) {
				removeRoadNetworkNode();
			} else if (cmbOperations.getSelectedIndex() == 2) {
				modifyRoadNetworkNode();
			} else if (cmbOperations.getSelectedIndex() == 3) {
				addRoadNetworkArc();
			} else if (cmbOperations.getSelectedIndex() == 4) {
				removeRoadNetworkArc();
			}
		} else if (e.getActionCommand().compareTo("Deselect Previous Node") == 0) {
			application.getCanvasEditor().getRoadsNetwork().deselectPreviousNode();
			application.getCanvasEditor().deselectFirstNode();
		} else if (e.getSource().equals(cbUsePreviousNodeSelection)) {
			application.getCanvasEditor().getRoadsNetwork().setUsePreviousNode(cbUsePreviousNodeSelection.isSelected());
		}
	}
	
	private void loadToolsComponents() {
		this.toolsPanel.removeAll();
		this.toolsPanel.add(this.toolsWidgetPanel, BorderLayout.NORTH);
		this.toolsPanel.add(this.descriptionWidgetPanel, BorderLayout.CENTER);
		
		toolsWidgetPanel.removeAll();
		
		categoriesPanel.add(lblOperation);
		categoriesPanel.add(cmbOperations);
	
		
		//listPrototypesPanel.add(lstObjects);
		
		toolsWidgetPanel.add(categoriesPanel);
		//toolsWidgetPanel.add(prototypesPanel);
		
		toolsWidgetPanel.add(cbUsePreviousNodeSelection);
		toolsWidgetPanel.add(btnConnectPreviousNode);
		//toolsWidgetPanel.add(listPrototypesPanel);
		toolsWidgetPanel.updateUI();
		
		
		application.setEditorStatus(EditorStatus.ADD_ROADS_NETWORK_NODE);
	}
	
	private void addRoadNetworkNode() {
		application.setEditorStatus(EditorStatus.ADD_ROADS_NETWORK_NODE);
	}
	
	private void removeRoadNetworkNode() {
		application.setEditorStatus(EditorStatus.REMOVE_ROADS_NETWORK_NODE);
	}
	
	private void modifyRoadNetworkNode() {
		application.setEditorStatus(EditorStatus.MODIFY_ROADS_NETWORK_NODE);
	}
	
	private void addRoadNetworkArc() {
		application.setEditorStatus(EditorStatus.ADD_ROADS_NETWORK_ARC);
	}

	private void removeRoadNetworkArc() {
		application.setEditorStatus(EditorStatus.REMOVE_ROADS_NETWORK_ARC);
	}
	
	/*private void setBoldLabel(JLabel lbl) {
		lbl.setFont(new Font(lbl.getFont().getName(), Font.BOLD, lbl.getFont().getSize()));
	}*/
	
	private void setItalicLabel(JLabel lbl) {
		lbl.setFont(new Font(lbl.getFont().getName(), Font.ITALIC, lbl.getFont().getSize()));
	}
	
}
