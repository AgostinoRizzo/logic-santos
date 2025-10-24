/**
 * 
 */
package it.unical.logic_santos.toolkit.geometry;

import it.unical.logic_santos.toolkit.math.MathGameToolkit;

/**
 * @author Agostino
 *
 */
public class Capsule extends Shape3D {

	private float radius;
	private float segmentHeight;
	
	
	
	
	public Capsule() {
		this.radius=0.0f;
		this.segmentHeight=0.0f;
	}
	
	public Capsule(final float _radius, final float _segmentHeight) {
		if (Shape3D.isGoodDimension(_radius))
			this.radius = _radius;
		if (Shape3D.isGoodDimension(_segmentHeight))
			this.segmentHeight = _segmentHeight;
	}
	
	
	
	
	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		if (Shape3D.isGoodDimension(radius))
			this.radius = radius;
	}

	public float getSegmentHeight() {
		return segmentHeight;
	}

	public void setSegmentHeight(float segmentHeight) {
		if (Shape3D.isGoodDimension(segmentHeight))
			this.segmentHeight = segmentHeight;
	}
	
	public float getDiameter() {
		return (radius*2.0f);
	}
	
	public float getHeight() {
		return (segmentHeight + getDiameter());
	}

	@Override
	public float getVolume() {
		final float sphereVolume = Sphere.calculateSphereVolume(radius);
		final float cylinderVolume = (float)(Math.PI * MathGameToolkit.square(radius) * segmentHeight);
		return (sphereVolume + cylinderVolume);
	}
	

}
