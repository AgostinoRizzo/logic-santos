/**
 * 
 */
package it.unical.logic_santos.gui.application;

import com.jme3.math.Vector3f;

/**
 * @author Agostino
 *
 */
public interface IGraphicalEntity {
	
	public void loadComponents() throws Exception;
	public void attachComponentsToGraphicalEngine();
	public void detachComponentsToGraphicalEngine();
	
	public void setTranslation( final Vector3f translation );

}
