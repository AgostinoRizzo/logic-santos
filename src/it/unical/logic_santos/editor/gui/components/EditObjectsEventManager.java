/**
 * 
 */
package it.unical.logic_santos.editor.gui.components;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import it.unical.logic_santos.editor.application.LogicSantosEditorApplication;
import it.unical.logic_santos.spatial_entity.ClassicTree;
import it.unical.logic_santos.spatial_entity.CoffeeShop;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.spatial_entity.MilleniumTower;
import it.unical.logic_santos.spatial_entity.PalaceTower;
import it.unical.logic_santos.spatial_entity.PalmTree;
import it.unical.logic_santos.spatial_entity.ParkBench;
import it.unical.logic_santos.spatial_entity.ParkTower;
import it.unical.logic_santos.spatial_entity.SeaFrontMilleniumTower;
import it.unical.logic_santos.spatial_entity.SeaFrontParkBench;
import it.unical.logic_santos.spatial_entity.SeaFrontStreetLight;
import it.unical.logic_santos.spatial_entity.SeaFrontTrafficLight;
import it.unical.logic_santos.spatial_entity.SkyApartment;
import it.unical.logic_santos.spatial_entity.Skyscraper;
import it.unical.logic_santos.spatial_entity.StopSign;
import it.unical.logic_santos.spatial_entity.StreetLight;
import it.unical.logic_santos.spatial_entity.TennisField;
import it.unical.logic_santos.spatial_entity.TrafficLight;
import it.unical.logic_santos.spatial_entity.WatchTower;

/**
 * @author Agostino
 *
 */
public class EditObjectsEventManager implements ActionListener, ListSelectionListener, ItemListener {

	public static final String PREVIEW_IMAGE_EXTENSION = "jpg";
	
	//private JFrame mainWindow=null;
	private JPanel toolsPanel=null;
	
	private JPanel toolsWidgetPanel=new JPanel();
	private JPanel descriptionWidgetPanel=new JPanel(new BorderLayout());
	private JPanel categoriesPanel = new JPanel();
	private JPanel prototypesPanel = new JPanel(new BorderLayout());
	private JPanel listPrototypesPanel = new JPanel(new BorderLayout());
	
	private JLabel lblObjectCategory = new JLabel("Object Category: ");
	private JLabel lblObjectPrototypes = new JLabel("Object Prototypes: ");
	
	private DefaultComboBoxModel<String> categoriesDataList = new DefaultComboBoxModel<String>();
	private JComboBox<String> cmbCategories = new JComboBox<String>(categoriesDataList);
	
	private DefaultListModel<String> objectsDataList = new DefaultListModel<String>();
	private JList<String> lstObjects = new JList<String>(objectsDataList);
	
	private JButton searchButton = new JButton( "Search" );
	
	
	private ISpatialEntity selectedEntity=null;
	
	private boolean updatingObjectsListSelection = false;
	
	private String allObjectData[] = { "ClassicTree", "PalmTree", "StopSign", "Skyscraper", "SkyApartment", "WatchTower", "MilleniumTower", "SeaFrontMilleniumTower", "PalaceTower", "ParkTower", "CoffeeShop", "TennisField", "TrafficLight", "SeaFrontTrafficLight", "StreetLight", "SeaFrontStreetLight", "ParkBench", "SeaFrontParkBench" };
	private String plantsObjectData[] = { "ClassicTree", "PalmTree" };
	private String bildingsObjectData[] = { "Skyscraper", "SkyApartment", "WatchTower", "MilleniumTower", "SeaFrontMilleniumTower", "PalaceTower", "ParkTower" };
	
	private LogicSantosEditorApplication application=null;
	
	
	public EditObjectsEventManager(LogicSantosEditorApplication app, JFrame _mainWindow, JPanel _toolsPanel) {
		this.application = app;
		//this.mainWindow = _mainWindow;
		this.toolsPanel = _toolsPanel;
		
		this.toolsWidgetPanel.setLayout(new BoxLayout(this.toolsWidgetPanel,BoxLayout.Y_AXIS));
		//this.toolsPanel.add(this.toolsWidgetPanel, BorderLayout.NORTH);
		//this.toolsPanel.add(this.descriptionWidgetPanel, BorderLayout.CENTER);
		
		setItalicLabel(lblObjectCategory);
		setItalicLabel(lblObjectPrototypes);
		
		categoriesDataList.addElement("All");
		categoriesDataList.addElement("Plants");
		categoriesDataList.addElement("Buildings");

		lstObjects.addListSelectionListener(this);
		
		searchButton.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbCategories.getSelectedIndex() == 0)
					loadAllListObjects();
				else if (cmbCategories.getSelectedIndex() == 1) {
					loadPlantsListObjects();
				} else if (cmbCategories.getSelectedIndex() == 2) {
					loadBuildingsListObjects();
				}
				deselectObject();
				
			}
		});
		
		if (cmbCategories.getSelectedIndex() == 0)
			loadAllListObjects();
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {
		if (application.getEditorStatus() == EditorStatus.UNLOAD) {
			JOptionPane.showMessageDialog(null, "City World not loaded.");
			return;
		}
		//if ( updatingObjectsListSelection && (descriptionWidgetPanel.getComponents().length>0) )
		//	return;
		if (e.getActionCommand().compareTo("Add") == 0)
			loadToolsComponents();
		else if (e.getActionCommand().compareTo("Delete") == 0)
			loadDeleteToolsComponents();
		else if (e.getActionCommand().compareTo("Modify") == 0)
			loadModityToolsComponents();
		else if (e.getActionCommand().compareTo("comboBoxChanged") == 0) {
			if (cmbCategories.getSelectedIndex() == 0)
				loadAllListObjects();
			else if (cmbCategories.getSelectedIndex() == 1) {
				loadPlantsListObjects();
			}
			deselectObject();
		} else if (e.getActionCommand().compareTo("Deselect Object") == 0)
			deselectObject();
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		/*if (application.getEditorStatus() == EditorStatus.UNLOAD) {
			JOptionPane.showMessageDialog(null, "City World not loaded.");
			return;
		}
		if (cmbCategories.getSelectedIndex() == 0)
			loadAllListObjects();
		else if (cmbCategories.getSelectedIndex() == 1) {
			loadPlantsListObjects();
		}
		System.out.println(" ITEM STATE CHANGED ");
		deselectObject();*/
		
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if ( (!e.getValueIsAdjusting()) && 
				(e.getFirstIndex()>=0) &&
				(!updatingObjectsListSelection) ) {
			
			String selectedObjectName = lstObjects.getSelectedValue();
			updateObjectSelection(selectedObjectName);
			System.out.println("INDEX: " + lstObjects.getSelectedIndex());
			System.out.println("VALUE: " + lstObjects.getSelectedValue());
		}
	}
	
	public ISpatialEntity getSelectedEntity() {
		return selectedEntity;
	}
	
	private void loadToolsComponents() {
		this.toolsPanel.removeAll();
		this.toolsPanel.add(this.toolsWidgetPanel, BorderLayout.NORTH);
		this.toolsPanel.add(this.descriptionWidgetPanel, BorderLayout.CENTER);
		
		toolsWidgetPanel.removeAll();
		
		categoriesPanel.add(lblObjectCategory);
		categoriesPanel.add(cmbCategories);
		categoriesPanel.add(searchButton);
		prototypesPanel.add(lblObjectPrototypes);
		
		listPrototypesPanel.add(lstObjects);
		
		toolsWidgetPanel.add(categoriesPanel);
		toolsWidgetPanel.add(prototypesPanel);
		toolsWidgetPanel.add(listPrototypesPanel);
		toolsWidgetPanel.updateUI();
		
		application.setEditorStatus(EditorStatus.ADD_OBJECT);
	}
	
	private void loadDeleteToolsComponents() {
		this.toolsPanel.removeAll();
		this.toolsPanel.add(this.toolsWidgetPanel, BorderLayout.NORTH);
		this.toolsPanel.add(this.descriptionWidgetPanel, BorderLayout.CENTER);
		toolsWidgetPanel.removeAll();
		descriptionWidgetPanel.removeAll();
		
		JLabel lblInfo = new JLabel("Point to the Object in the map you want to delete.");
		toolsWidgetPanel.add(lblInfo);
		
		toolsWidgetPanel.updateUI();
		descriptionWidgetPanel.updateUI();
		
		application.setEditorStatus(EditorStatus.DELETE_OBJECT);
	}
	
	private void loadModityToolsComponents() {
		this.toolsPanel.removeAll();
		this.toolsPanel.add(this.toolsWidgetPanel, BorderLayout.NORTH);
		this.toolsPanel.add(this.descriptionWidgetPanel, BorderLayout.CENTER);
		toolsWidgetPanel.removeAll();
		descriptionWidgetPanel.removeAll();
		
		JLabel lblInfo = new JLabel("<html>Point to the Object in the map you want to move. <br>Then move the cam to move the selected Object and release it.</html>", SwingConstants.CENTER);
		toolsWidgetPanel.add(lblInfo);
		
		toolsWidgetPanel.updateUI();
		descriptionWidgetPanel.updateUI();
		
		application.setEditorStatus(EditorStatus.MODIFY_OBJECT);
	}

	private void loadAllListObjects() {
		objectsDataList.clear();
		for( String obj: allObjectData )
			objectsDataList.addElement( obj );
		lstObjects.setListData(allObjectData);
		lstObjects.repaint();
		toolsWidgetPanel.updateUI();
	}
	
	private void loadPlantsListObjects() {
		lstObjects.setListData(plantsObjectData);
		lstObjects.repaint();
		toolsWidgetPanel.updateUI();
	}
	
	private void loadBuildingsListObjects() {
		lstObjects.setListData(bildingsObjectData);
		lstObjects.repaint();
		toolsWidgetPanel.updateUI();
	}
	
	private void updateObjectSelection(final String selectedObjectName) {
		switch(selectedObjectName) {
		case "ClassicTree": selectedEntity = new ClassicTree(); break;
		case "PalmTree": selectedEntity = new PalmTree(); break;
		case "Skyscraper": selectedEntity = new Skyscraper(); break;
		case "SkyApartment": selectedEntity = new SkyApartment(); break;
		case "WatchTower": selectedEntity = new WatchTower(); break;
		case "StopSign": selectedEntity = new StopSign(); break;
		case "MilleniumTower": selectedEntity = new MilleniumTower(); break;
		case "SeaFrontMilleniumTower": selectedEntity = new SeaFrontMilleniumTower(); break;
		case "PalaceTower": selectedEntity = new PalaceTower(); break;
		case "ParkTower": selectedEntity = new ParkTower(); break;
		case "CoffeeShop": selectedEntity = new CoffeeShop(); break;
		case "TennisField": selectedEntity = new TennisField(); break;
		case "TrafficLight": selectedEntity = new TrafficLight(); break;
		case "SeaFrontTrafficLight": selectedEntity = new SeaFrontTrafficLight(); break;
		case "StreetLight": selectedEntity = new StreetLight(); break;
		case "SeaFrontStreetLight": selectedEntity = new SeaFrontStreetLight(); break;
		case "ParkBench": selectedEntity = new ParkBench(); break;
		case "SeaFrontParkBench": selectedEntity = new SeaFrontParkBench(); break;
		}
		updateDescriptionSelectedObject();
	}
	
	private void updateDescriptionSelectedObject() {
		if (selectedEntity == null)
			return;
		
		descriptionWidgetPanel.removeAll();
		
		JPanel namePanel = new JPanel();
		JLabel lblName = new JLabel("Selected Object: ");
		JLabel lblNameInfo = new JLabel(selectedEntity.getName());
		namePanel.add(lblName);
		namePanel.add(lblNameInfo);
		
		JPanel previewPanel = new ImagePreview("Assets/Previews/" + selectedEntity.getName() + "." + EditObjectsEventManager.PREVIEW_IMAGE_EXTENSION);
		JButton deselectButton = new JButton("Deselect Object");
		deselectButton.addActionListener(this);
		descriptionWidgetPanel.add(namePanel, BorderLayout.NORTH);
		descriptionWidgetPanel.add(previewPanel, BorderLayout.CENTER);
		descriptionWidgetPanel.add(deselectButton, BorderLayout.SOUTH);
		descriptionWidgetPanel.repaint();
		descriptionWidgetPanel.updateUI();
	}
	
	private void deselectObject() {
		selectedEntity = null;
		descriptionWidgetPanel.removeAll();
		//lstObjects.clearSelection();
		//lstObjects.repaint();	
		descriptionWidgetPanel.repaint();
		descriptionWidgetPanel.updateUI();
	}

	/*private void setBoldLabel(JLabel lbl) {
		lbl.setFont(new Font(lbl.getFont().getName(), Font.BOLD, lbl.getFont().getSize()));
	}*/
	
	private void setItalicLabel(JLabel lbl) {
		lbl.setFont(new Font(lbl.getFont().getName(), Font.ITALIC, lbl.getFont().getSize()));
	}
	

}
