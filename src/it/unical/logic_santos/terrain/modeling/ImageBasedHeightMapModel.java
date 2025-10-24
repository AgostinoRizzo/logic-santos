/**
 * VerticesMesh class represents a terrain model based on a vertices mesh
 * The terrain is divided into triangles organized in a vertices mash 
 */

package it.unical.logic_santos.terrain.modeling;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author agostino
 * @version 1.0
 * @category TerrainModeling
 */

/** VerticesMesh represents a terrain model based on a vertices mesh */
public class ImageBasedHeightMapModel extends AbstractHeightMapModel {
	
    // ... CONSTRUCTORS ...
	
	public ImageBasedHeightMapModel() {
    	super();
	}

	public ImageBasedHeightMapModel(ITerrainChunkArena arena) {
    	super(arena);
	}

    public ImageBasedHeightMapModel(ITerrainChunkArena arena, IWorldLand wl, final int _iX, final int _iZ) throws IllegalArgumentException {
    	super(arena, wl, _iX, _iZ);
	}
    
    public ImageBasedHeightMapModel(ITerrainChunkArena arena, IWorldLand wl, final int _iX, final int _iZ, int _verticesCount) throws IllegalArgumentException {
    	super(arena, wl, _iX, _iZ, _verticesCount);
    }
    
    public ImageBasedHeightMapModel(final ImageBasedHeightMapModel m) {
    	super(m);
    }
  
    
    // ... GENERAL METHODS ...

	@Override
	public void saveTerrainToFile(String heightMapFileName) throws IOException, IllegalArgumentException { // implementare !!!
		
		BufferedImage image=null;
		image = ImageIO.read(new File(heightMapFileName));
		
		final int width = image.getWidth();
		final int height = image.getHeight();
		
		if ( !((width == height) && (height == verticesCount)) )
			throw new IllegalArgumentException();
		
		int j;
		for(int i = 0; i < height; ++i) {
			for(j = 0; j < width; ++j) {
				
			}
		}
	
	}

	@Override
	public void loadTerrainFromFile(String heightMapFileName) throws IOException, IllegalArgumentException {
		
		BufferedImage image=null;
		image = ImageIO.read(new File(heightMapFileName));
		
		final int width = image.getWidth();
		final int height = image.getHeight();
		
		if ( !((width == height) && (height == verticesCount)) )
			throw new IllegalArgumentException();
		
		int j, rgbColor;
		Color pixelColor=null;
		for(int i = 0; i < height; ++i)
			for(j = 0; j < width; ++j) {
				
				rgbColor = image.getRGB(j, i);
				pixelColor = new Color(rgbColor);
				verticesHeights[ (i*verticesCount)+j ] = calculateHeight(pixelColor);
			}
		
		smooth(0.3f);
	}
	
	// ... STATIC METHODS ...
	
	private static float calculateHeight(final float red, final float green, final float blue) {
		return (float) ( (0.299f*red) + (0.587*green) + (0.114*blue) );
	}
	
	private static float calculateHeight(final Color c) {
		return calculateHeight(c.getRed(), c.getGreen(), c.getBlue());
	}
	
}
