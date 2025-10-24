/**
 * 
 */
package it.unical.logic_santos.gui.screen;

import com.jme3.math.Vector2f;

/**
 * @author Agostino
 *
 */
public interface IVisibleEntity {

	public IndicatorType getIndicatorType();
	public Vector2f get2dWorldPosition();
	public String getIndicatorImageName();
	public Integer[] getIndicatorImageExtension();
	public float getAngleRotation();
}
