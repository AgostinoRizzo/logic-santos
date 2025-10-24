/**
 * The SupervisedSpaceCollisionArena class represents the space of the ICollisionArena supervised
 * by the ICollidable game object
 */

package it.unical.logic_santos.collision;

import it.unical.logic_santos.toolkit.math.Vector2D;

/**
 * @author agostino
 * @version 1.0
 * @category CollisionDetectionEngine
 */

public class SupervisedSpaceCollisionArena { // SupervisedSpaceCollisionArena class represents the space of the ICollisionArena supervised
	                                         // by the ICollidable game object

	// the coordinates (x and y) of the supervised collision arena space
	private Vector2D translation;
	
	// the dimension of the supervised collision arena space
	private float width;
	private float depth;
	
	// ... CONSTRUCTORS ...
	
	public SupervisedSpaceCollisionArena() {
		this.translation = new Vector2D(Vector2D.ZERO);
		this.width = 0.0f;
		this.depth = 0.0f;
	}
	
	public SupervisedSpaceCollisionArena(final Vector2D _translation) {
		this.translation = _translation.cpy();
		this.width = 0.0f;
		this.depth = 0.0f;
	}
	
	public SupervisedSpaceCollisionArena(final float w, final float d) throws IllegalArgumentException {
	    if ( (w<0.0f) || (d<0.0f) )
	    	throw new IllegalArgumentException();
		this.translation = new Vector2D(Vector2D.ZERO);
		this.width = w;
		this.depth = d;
	}
	
	public SupervisedSpaceCollisionArena(final Vector2D _translation, final float w, final float d) {
		if ( (w<0.0f) || (d<0.0f) )
	    	throw new IllegalArgumentException();
		this.translation = _translation.cpy();
		this.width = w;
		this.depth = d;
	} 
	
    // ... GETTER AND SETTER METHODS ...
	
	public final Vector2D getTranslation() {
		return translation;
	}

	public final void setTranslation(Vector2D translation) {
		this.translation = translation;
	}

	public final float getWidth() {
		return width;
	}

	public final void setWidth(float w) throws IllegalArgumentException {
		if (w < 0.0f)
			throw new IllegalArgumentException();
		this.width = w;
	}

	public final float getDepth() {
		return depth;
	}

	public final void setDepht(float d) {
		if (d < 0.0f)
			throw new IllegalArgumentException();
		this.depth = d;
	}
	
	public final float getXExtension() {
		return (width/2.0f);
	}
	
	public final void setXExtension(final float xExtension) {
		setWidth(xExtension*2.0f);
	}
	
	public final float getZExtension() {
		return (depth/2.0f);
	}
	
	public final void setZExtension(final float zExtension) {
		setDepht(zExtension*2.0f);
	}

}
