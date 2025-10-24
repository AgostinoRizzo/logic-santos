/**
 * 
 */
package it.unical.logic_santos.toolkit.geometry;

import it.unical.logic_santos.toolkit.math.MathGameToolkit;

/**
 * @author Agostino
 *
 */
public class Sphere extends Shape3D {

	private float radius;
	
	
	public Sphere() {
		this.radius=0.0f;
	}
	
	public Sphere(final float _radius) {
		if (isGoodDimension(_radius))
			this.radius = _radius;
	}
	
	
	@Override
	public float getVolume() {
		return calculateSphereVolume(radius);
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		if (Shape3D.isGoodDimension(radius))
			this.radius = radius;
	}
	
	public float getDiameter() {
		return (radius*2.0f);
	}
	
	public static float calculateSphereVolume(final float radius) {
		return ((float)(4.0f * Math.PI * MathGameToolkit.cube(radius)))/3.0f;
	}

	
}
