/**
 * 
 */
package it.unical.logic_santos.editor.application;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import it.unical.logic_santos.editor.gui.components.EditObjectsEventManager;
import it.unical.logic_santos.editor.gui.components.EditPathNetworkManager;
import it.unical.logic_santos.editor.gui.components.EditRoadNetworkEventManager;
import it.unical.logic_santos.editor.gui.components.EditRoadNetworkManager;
import it.unical.logic_santos.editor.gui.components.EditorStatus;
import it.unical.logic_santos.editor.gui.components.FileMenuEventManager;

/**
 * @author Agostino
 *
 */
public class LogicSantosEditorApplication implements ActionListener {

	//private LogicSantosEditableApplication editableApplication=null;
	private JFrame mainWindow=null;
	protected JPanel toolsPanel=null;
	//private JPanel terrainSurfaceToolsPanel=null;
	private JPanel convasPanel = null;
	
	private JLabel txtSettings=null;
	
	private CanvasEditorApplication canvasEditor = null;
	
	protected EditRoadNetworkEventManager editRoadNetworkEventManager=null;
	protected EditObjectsEventManager editObjectsEventManager=null;
	protected EditRoadNetworkManager editRoadNetworkManager=null;
	protected EditPathNetworkManager editPathNetworkManager=null;
	protected FileMenuEventManager fileMenuEventManager=null;
	
	private EditorStatus editorStatus=EditorStatus.UNLOAD;
	
	//private boolean canvasLoaded = false;
	private boolean canvasShown  = false;
	
	
	public LogicSantosEditorApplication() {
		super();//editableApplication = new LogicSantosEditableApplication();
	}
	
	
	public void loadComponents() {
				
		mainWindow = new JFrame(EditorApplicationConfiguration.WINDOW_TITLE);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.repaint();
		mainWindow.setContentPane(mainPanel);
		
		convasPanel = new JPanel(new FlowLayout());
		convasPanel.setBackground(new Color(200, 200, 200));
		
		toolsPanel = new JPanel();
		toolsPanel.setLayout(new BorderLayout());
		
		editRoadNetworkEventManager = new EditRoadNetworkEventManager(mainWindow, toolsPanel);
		editObjectsEventManager = new EditObjectsEventManager(this, mainWindow, toolsPanel);
		editRoadNetworkManager = new EditRoadNetworkManager(this, mainWindow, toolsPanel);
		editPathNetworkManager = new EditPathNetworkManager(this, mainWindow, toolsPanel);
		fileMenuEventManager = new FileMenuEventManager(mainWindow, this);
		
		txtSettings = new JLabel();
		
		JMenuBar menuBar = loadJMenuBarComponents();
		mainWindow.setJMenuBar(menuBar);
		mainPanel.add(toolsPanel, BorderLayout.CENTER);
		mainWindow.pack();
		
	}
	
	public void loadCanvas() {
		if (canvasEditor == null) {
			canvasEditor = new CanvasEditorApplication(this);
			canvasEditor.start();
			//canvasLoaded = true;
		}
	}
	
	public void showCanvasEditor() {
		if (!canvasShown) {
			canvasEditor.addCanvasContextToPanel(convasPanel);
			//JPanel panel = new JPanel();
			
			Box b = Box.createVerticalBox();
			b.add(convasPanel);
			//b.add(txtSettings);
			mainWindow.getContentPane().add(b, BorderLayout.LINE_START);
			mainWindow.pack();
			canvasShown = true;
		}
	}
	
	private JMenuBar loadJMenuBarComponents() {
		
		JMenuBar menuBar = new JMenuBar();
		
		/* ==== FILE MENU ==== */
		JMenu mFile = new JMenu("File");
		menuBar.add(mFile);
		JMenuItem miNew = new JMenuItem("New City World");
		miNew.addActionListener(fileMenuEventManager);
		mFile.add(miNew);
		JMenuItem miLoad = new JMenuItem("Load City World");
		miLoad.addActionListener(fileMenuEventManager);
		mFile.add(miLoad);
		JMenuItem miSave = new JMenuItem("Save City World");
		miSave.addActionListener(fileMenuEventManager);
		mFile.add(miSave);
		JMenuItem miExit = new JMenuItem("Exit");
		miExit.addActionListener(fileMenuEventManager);
		mFile.add(miExit);
		
		/* ==== OVERVIEW MENU ==== */
		JMenu mOverview = new JMenu("Overview");
		JMenuItem miExplore = new JMenuItem("Explore");
		miExplore.addActionListener(this);
		mOverview.add(miExplore);
		menuBar.add(mOverview);
		
		/* ==== TERRAIN MENU ==== */
		JMenu mTerrain = new JMenu("Terrain");
		menuBar.add(mTerrain);
		JMenuItem miSurface = new JMenuItem("Surface");
		mTerrain.add(miSurface);
		
		/* ==== OBJECTS MENU ==== */
		JMenu mObjects = new JMenu("Objects");
		menuBar.add(mObjects);
		JMenuItem miAdd = new JMenuItem("Add");
		miAdd.addActionListener(editObjectsEventManager);
		JMenuItem miDelete = new JMenuItem("Delete");
		miDelete.addActionListener(editObjectsEventManager);
		JMenuItem miModify = new JMenuItem("Modify");
		miModify.addActionListener(editObjectsEventManager);
		mObjects.add(miAdd);
		mObjects.add(miDelete);
		mObjects.add(miModify);
		
		/* ==== ROAD NETWORK MENU ==== */
		JMenu mRoadNetwork = new JMenu("Roads Network");
		JMenuItem miRoadEdit = new JMenuItem("Edit Roads Network");
		miRoadEdit.addActionListener(editRoadNetworkManager);
		mRoadNetwork.add(miRoadEdit);
		menuBar.add(mRoadNetwork);
		
		/* ==== WALKING PATH NETWORK MENU ==== */
		JMenu mWalkingPathNetwork = new JMenu("Walking Paths Network");
		JMenuItem miPathEdit = new JMenuItem("Edit Paths Network");
		miPathEdit.addActionListener(editPathNetworkManager);
		mWalkingPathNetwork.add(miPathEdit);
		menuBar.add(mWalkingPathNetwork);
		
		return menuBar;
		
	}
	
	
	/*private JPanel loadTerrainSurfaceToolsPanel() {
		
		
		JLabel surfaceTypeLabel = new JLabel("Surface Type: ");
		toolsPanel.add(surfaceTypeLabel);
		JLabel surfaceBrushSizeLabel = new JLabel("Brush Size: ");
		toolsPanel.add(surfaceBrushSizeLabel);
		JLabel surfaceBrushDensityLabel = new JLabel("Brush Density: ");
		toolsPanel.add(surfaceBrushDensityLabel);
		
		return toolsPanel;
	}*/
	
	
	public void startCanvasApplication() {
		mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mainWindow.setVisible(true);
	}
	
	
	public EditorStatus getEditorStatus() {
		return editorStatus;
	}

	public void setEditorStatus(EditorStatus editorStatus) {
		this.editorStatus = editorStatus;
	}

	public CanvasEditorApplication getCanvasEditor() {
		return canvasEditor;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (getEditorStatus() == EditorStatus.UNLOAD) {
			JOptionPane.showMessageDialog(null, "City World not loaded.");
			return;
		}
		
		String command = e.getActionCommand();
		if (command.equals("Explore")) {
			editorStatus = EditorStatus.NDEF;
			
			
			/*for( Component c: toolsPanel.getComponents() ) {
				if ( c instanceof JPanel ) {
					JPanel p = (JPanel) c;
					p.removeAll();
					p.repaint();
				}
			}*/
			
		}
	}
	
	public String getSettingsString() {
		return this.canvasEditor.getSettingsString();
	}
	
	public JLabel getTextAreaSettings() {
		return txtSettings;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
				
				LogicSantosEditorApplication editorApp = new LogicSantosEditorApplication();
				editorApp.loadComponents();
				editorApp.startCanvasApplication();
		
		
	}

}
