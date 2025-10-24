/**
 * 
 */
package it.unical.logic_santos.controls;

import it.unical.logic_santos.toolkit.math.Vector2D;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public interface IPickerCamera {

	public Vector2D getCursorPosition();
	public Vector3D getWorldPosition(final Vector2D screenPosition, final float projectionZPos);
	
	public Vector3D getCameraPosition();
	public Vector3D getCameraDirection();
}
