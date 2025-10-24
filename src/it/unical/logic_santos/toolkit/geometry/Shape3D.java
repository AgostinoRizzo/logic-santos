/**
 * 
 */
package it.unical.logic_santos.toolkit.geometry;

/**
 * @author Agostino
 *
 */
public abstract class Shape3D implements IShape {

	public static boolean isGoodDimension(final float d) {
		return (d>=0.0f);
	}
}
