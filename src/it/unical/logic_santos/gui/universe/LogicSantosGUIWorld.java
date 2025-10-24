/**
 * 
 */
package it.unical.logic_santos.gui.universe;

import com.jme3.asset.AssetManager;

import it.unical.logic_santos.controls.CursorShifter;
import it.unical.logic_santos.gui.environment.GraphicalLightEnvironment;
import it.unical.logic_santos.gui.environment.GraphicalSky;
import it.unical.logic_santos.gui.terrain.GraphicalTerrain;
import it.unical.logic_santos.universe.AbstractWorld;
import it.unical.logic_santos.universe.SpatialEntityGenerator;

/**
 * @author Agostino
 *
 */
public class LogicSantosGUIWorld {

public static AssetManager ASSET_MANAGER_APPLICATION=null;
	
	public AbstractWorld logicSantosWorld=null;
	public SpatialEntityGenerator spatialEntityGenerator=null;
	
	GraphicalTerrain graphicalTerrain=null;
	GraphicalSky graphicalSky=null;
	GraphicalLightEnvironment graphicalEnvironmentLight=null;
	
	CursorShifter cursorShifter=null;
	
	
	public LogicSantosGUIWorld() {
		
	}
	
	public void initWorld() {
		
	}
}
