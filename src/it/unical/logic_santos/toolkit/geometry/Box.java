/**
 * 
 */
package it.unical.logic_santos.toolkit.geometry;

/**
 * @author Agostino
 *
 */
public class Box extends Shape3D {

	private float width;
	private float height;
	private float depth;
	
	public Box() {
		this.width=0;
		this.height=0;
		this.depth=0;
	}
	
	public Box(final float _width, final float _height, final float _depth) {
		this.width = _width;
		this.height = _height;
		this.depth = _depth;
	}
	
	public Box(final Box b) {
		this.width = b.width;
		this.height = b.height;
		this.depth = b.depth;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		if (Shape3D.isGoodDimension(width))
			this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		if (Shape3D.isGoodDimension(height))
			this.height = height;
	}

	public float getDepth() {
		return depth;
	}

	public void setDepth(float depth) {
		if (Shape3D.isGoodDimension(depth))
			this.depth = depth;
	}
	

	@Override
	public float getVolume() {
		return (width*height*depth);
	}
}
