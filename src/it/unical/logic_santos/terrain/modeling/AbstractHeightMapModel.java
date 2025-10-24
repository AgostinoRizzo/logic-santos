/**
 * 
 */
package it.unical.logic_santos.terrain.modeling;

import java.util.ArrayList;
import java.util.List;

import it.unical.logic_santos.collision.ICollidable;
import it.unical.logic_santos.collision.ICollisionResults;
import it.unical.logic_santos.collision.SupervisedSpaceCollisionArena;
import it.unical.logic_santos.collision.TerrainCollisionResults;
import it.unical.logic_santos.toolkit.math.Point;
import it.unical.logic_santos.toolkit.math.Vector2D;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public abstract class AbstractHeightMapModel implements ITerrainChunkModel, ICollidable {

	/* square height map model */
	/* SIZE and VERTICES_COUNT are related to a side */
	
	public static final float SIZE = ITerrainChunkArena.SIZE;
	public static final int VERTICES_COUNT = TerrainModelingConfig.HEIGHTMAP_TERRAIN_CHUNK_VERTICES_COUNT; 
	
	/** Vertices matrix that represents a vertices mesh */
	protected float[] verticesHeights=null; // dimension: meshSize * meshSize
	
	protected float size;
	protected int verticesCount;
	
	/** World Land in which the terrain is */
	protected IWorldLand worldLand=null;
	
	/** i, j indices that represents the position of the terrain in the World Land */
	protected int iX;
	protected int iZ;
	
	protected Vector3D scaleFactors;
	protected Vector3D translationFactors;
	
	protected List<ITerrainChunkModel> mixedTerrainModels=new ArrayList<ITerrainChunkModel>();
	
	
	// ... CONSTRUCTORS ...
	
	public AbstractHeightMapModel() {
	    	
	    this.size = SIZE;
		this.verticesCount = VERTICES_COUNT;
    	this.verticesHeights = new float[VERTICES_COUNT*VERTICES_COUNT];
	   	this.worldLand = null;
	   	this.iX = 0;
	   	this.iZ = 0;
	   	this.scaleFactors=new Vector3D();
	   	this.translationFactors=new Vector3D();
		initFlatTerrain(this.verticesHeights);
	}


	public AbstractHeightMapModel(ITerrainChunkArena arena) {
	    	
	   	this.size = arena.getSize();
		this.verticesCount = VERTICES_COUNT;
	   	this.verticesHeights = new float[VERTICES_COUNT*VERTICES_COUNT];
	   	this.worldLand = null;
	   	this.iX = 0;
	   	this.iZ = 0;
	   	this.scaleFactors=new Vector3D();
	   	this.translationFactors=new Vector3D();
		initFlatTerrain(this.verticesHeights);
	}

    public AbstractHeightMapModel(ITerrainChunkArena arena, IWorldLand wl, final int _iX, final int _iZ) throws IllegalArgumentException {
    	
    	if ( (_iX < 0) || (_iZ < 0) || (_iX >= wl.getLandDepth()) || (_iZ >= wl.getLandWidth())  )
    		throw new IllegalArgumentException();
    	this.size = arena.getSize();
		this.verticesCount = VERTICES_COUNT;
    	this.verticesHeights = new float[VERTICES_COUNT*VERTICES_COUNT];
    	this.worldLand = wl;
    	this.iX = _iX;
    	this.iZ = _iZ;
    	this.scaleFactors=new Vector3D();
    	this.translationFactors=new Vector3D();
		initFlatTerrain(this.verticesHeights);
	}
	    
    public AbstractHeightMapModel(ITerrainChunkArena arena, IWorldLand wl, final int _iX, final int _iZ, int _verticesCount) throws IllegalArgumentException {

    	if ( (_iX < 0) || (_iZ < 0) || (_iX >= wl.getLandDepth()) || (_iZ >= wl.getLandWidth()) || (_verticesCount <= 0) )
    		throw new IllegalArgumentException();
    	this.size = arena.getSize();
		this.verticesCount = _verticesCount;
    	this.verticesHeights = new float[this.verticesCount*this.verticesCount];
    	this.worldLand = wl;
    	this.iX = _iX;
    	this.iZ = _iZ;
    	this.scaleFactors=new Vector3D();
    	this.translationFactors=new Vector3D();
		initFlatTerrain(this.verticesHeights);
    }
    
    public AbstractHeightMapModel(final AbstractHeightMapModel m) {
	    	
    	this.size = m.size;
		this.verticesCount = m.verticesCount;
		this.verticesHeights = new float[this.verticesCount*this.verticesCount];
		this.worldLand = m.worldLand;
		this.iX = m.iX;
		this.iZ = m.iZ;
		this.scaleFactors=new Vector3D();
		this.translationFactors=new Vector3D();
    	for(int i = 0; i < this.verticesHeights.length; ++i)
    		this.verticesHeights[i] = m.verticesHeights[i];
    }
    
    
    // ... GETTER AND SETTER METHODS ...
    
    @Override
    public float getSize() {
		return size;
	}
    
    @Override
	public float getWidth() {
		return size;
	}

	@Override
	public float getDepth() {
		return size;
	}

	public int getVerticesCount() {
		return verticesCount;
	}
	
	public final float getX() {
		return (size * (float)iZ);
	}
	
	public final float getZ() {
		return (size * (float)iX);
	}
    
	
	public final float[] getHeightMap() {
		return verticesHeights;
	}
	
	
    @Override
	public Vector3D getScale() {
		return scaleFactors;
	}


	@Override
	public void setScale(Vector3D scaleFactors) {
		if ( (scaleFactors.getX()>=0.0f) && (scaleFactors.getY()>=0.0f) && (scaleFactors.getZ()>=0.0f) )
			this.scaleFactors = new Vector3D(scaleFactors);
	}
	@Override
	public void setScale(final float xScale, final float yScale, final float zScale) {
		if ( (xScale>=0.0f) && (yScale>=0.0f) && (zScale>=0.0f) )
			this.scaleFactors = new Vector3D(xScale, yScale, zScale);
	}
	
	@Override
	public Vector3D getTranslation() {
		return scaleFactors;
	}


	@Override
	public void setTranslation(Vector3D translationFactors) {
			this.translationFactors = new Vector3D(translationFactors);
	}
	@Override
	public void setTranslation(final float xTranslation, final float yTranslation, final float zTranslation) {
			this.translationFactors = new Vector3D(xTranslation, yTranslation, zTranslation);
	}
	
    // ... GENERAL METHODS ...
   

	@Override
	public void generateFlatTerrain() {
		initFlatTerrain(verticesHeights);
	}
    
	@Override
	public float getHeight(float xWorldLandPoint, float zWorldLandPoint) throws IllegalArgumentException {
		
		xWorldLandPoint -= translationFactors.getX();
		zWorldLandPoint -= translationFactors.getZ();
		
		xWorldLandPoint /= scaleFactors.getX();
		zWorldLandPoint /= scaleFactors.getZ();
		
		if (!isInside(xWorldLandPoint, zWorldLandPoint))
			return 0.0f;
		
		int halfVerticesCount = verticesCount/2;
		int x = Math.round(xWorldLandPoint + halfVerticesCount);
		int z = Math.round(zWorldLandPoint + halfVerticesCount);
		
		if (!isInside(x, z))
			return 0.0f;
		
		float ans = ( verticesHeights[ (z*verticesCount)+x ] * scaleFactors.getY() ) + translationFactors.getY();
		
		if (!mixedTerrainModels.isEmpty()) {
			
			final float xPoint = (xWorldLandPoint * scaleFactors.getX()) + translationFactors.getX(); 
			final float zPoint = (zWorldLandPoint * scaleFactors.getZ()) + translationFactors.getZ(); 
			
			for(ITerrainChunkModel model: mixedTerrainModels) {
				final float h = model.getHeight(xPoint, zPoint);
				if (h > ans)
					ans = h;
			}
		}
		
		return ans;
		
		/*final float terrainX = getX();
		final float terrainZ = getZ();
		
		if ( (xWorldLandPoint < terrainX) || (xWorldLandPoint >= (terrainX + size)) || (zWorldLandPoint < terrainZ) || (zWorldLandPoint >= (terrainZ + size)) )
			throw new IllegalArgumentException();
		
		final float terrainPointX = xWorldLandPoint - terrainX;
		final float terrainPointZ = zWorldLandPoint - terrainZ;
		
		final int cellCount = ((int)verticesCount -1);
		final int iXCell = (int)terrainPointZ / cellCount;
		final int iZCell = (int)terrainPointX / cellCount;
		
		final float xCoord = terrainPointX - (float)iZCell;
		final float zCoord = terrainPointZ - (float)iXCell;
		
		float ans;
		if (xCoord <= (1 - zCoord)) {
			ans = MathGameToolkit.barryCentric(new Vector3d(0, verticesHeights[iXCell-1][iZCell-1], 0),
											   new Vector3d(1, verticesHeights[iXCell-1][iZCell], 0),
										       new Vector3d(0, verticesHeights[iXCell][iZCell-1], 1),
					                           new Vector2d(xCoord, zCoord));
		} else {
			ans = MathGameToolkit.barryCentric(new Vector3d(1, verticesHeights[iXCell-1][iZCell], 0),
											   new Vector3d(0, verticesHeights[iXCell][iZCell-1], 1),
											   new Vector3d(1, verticesHeights[iXCell][iZCell], 1),
											   new Vector2d(xCoord, zCoord));
		}
		
		return ans;*/
		//return 0f;
	} 
	
	@Override
	public boolean isInside(final int x, final int z) {
		return ( (x>=0) && (x<verticesCount) && (z>=0) && (z<verticesCount) );
	}
	
	@Override
	public boolean isInside(final float x, final float z) {
		int halfVerticesCount = verticesCount/2;
		int newX = Math.round(x + halfVerticesCount);
		int newZ = Math.round(z + halfVerticesCount);
		
		return ( (newX>=0) && (newX<verticesCount) && (newZ>=0) && (newZ<verticesCount) );
	}
	
	public void smooth(final float neightboursHeightInfluence) {
		smooth(neightboursHeightInfluence, 1);
	}
	
	public void smooth(final float neightboursHeightInfluence, int radius) {
		 
		if ( (neightboursHeightInfluence<0.0f) || (neightboursHeightInfluence>1.0f) )
			return;
		if (radius == 0)
			radius = 1;
		
		int j, iR, jR;
		int neighboursCount=0;
		float neighboursAverage=0.0f;
		for(int i = 0; i < verticesCount; ++i) {
			for(j = 0; j < verticesCount; ++j) {
				
				neighboursCount = 0;
				neighboursAverage = 0.0f;
				
				for(iR = -radius; iR <= radius; ++iR) {
					for(jR = -radius; jR <= radius; ++jR) {
						
						if ( ((i+iR) < 0) || ((i+iR) >= verticesCount) ||
							 ((j+jR) < 0) || ((j+jR) >= verticesCount) ) {
							;
						} else {
							
							neighboursCount++;
							neighboursAverage += verticesHeights[ ((i+iR)*verticesCount) + (j+jR) ];	
						}
					}
				}
				
				neighboursAverage /= (float)neighboursCount;
				float cp = 1.0f - neightboursHeightInfluence;
				verticesHeights[ (i*verticesCount)+j ] = (neighboursAverage * neightboursHeightInfluence) + (verticesHeights[ (i*verticesCount)+j ] * cp);
				
			}
		}
	}
	
	public void erodeTerrain(final float filter) {
		
		// erode left to right ...
		float v, nFilter = 1-filter;
		int i, j;
		
		for(j = 0; j < verticesCount; ++j) {
			v = verticesHeights[j];
			for(i = 0; i < verticesCount; ++i) {
				verticesHeights[ (i*verticesCount)+j ] = filter * v + (nFilter * verticesHeights[ (i*verticesCount)+j ]);
			}
		}
		
		// erode right to left ...
		for(j = verticesCount-1; j >= 0; --j) {
			v = verticesHeights[j];
			for(i = 0; i < verticesCount; ++i) {
				verticesHeights[ (i*verticesCount)+j ] = filter * v + (nFilter * verticesHeights[ (i*verticesCount)+j ]);
			}
		}
		
		// erode from top to bottom
		for(j = 0; j < verticesCount; ++j) {
			v = verticesHeights[j];
			for(i = 0; i < verticesCount; ++i) {
				verticesHeights[ (i*verticesCount)+j ] = filter * v + (nFilter * verticesHeights[ (i*verticesCount)+j ]);
			}
		}
		
		// erode from buttom to top
		for(j = 0; j < verticesCount; ++j) {
			v = verticesHeights[j];
			for(i = verticesCount-1; i >= 0; --i) {
				verticesHeights[ (i*verticesCount)+j ] = filter * v + (nFilter * verticesHeights[ (i*verticesCount)+j ]);
			}
		}
	}
	
	public void flatten(byte flattening) {
		
		if (flattening <= 1)
			return;
		
		float[] minMax = findMinMaxHeights(verticesHeights);
		
		normalizeTerrain(1.0f);
		
		float newHeight, original;
		for(int i = 0; i < verticesHeights.length; ++i) {
			
			newHeight = 1.0f;
			original = verticesHeights[i];
			
			for(int k = 0; k < flattening; ++k) {
				newHeight *= original;
			}
			
			verticesHeights[i] = newHeight;
		}
		
		float height = minMax[1] - minMax[0];
		normalizeTerrain(height);
	}
	
	public void normalizeTerrain(final float value) {
		normalizeTerrain(verticesHeights, value);
	}
	
	
	// ... STATIC METHODS ...
    
    public static void initFlatTerrain(float[] mesh) {
    	for(int i = 0; i < mesh.length; ++i)
    		mesh[i] = 0.0f;
    }
    
    public static float[] findMinMaxHeights(final float[] heights) {
    	
    	float[] minMax = new float[2];
    	final int iMin=0, iMax=1;
    	
    	if (heights.length == 0) {
    		minMax[iMin] = 0.0f;
    		minMax[iMax] = 0.0f;
    		return minMax;
    	}
    	
    	minMax[iMin] = heights[0];
    	minMax[iMax] = heights[0];
    	
    	for(int i = 1; i < heights.length; ++i) {
    		if (heights[i] < minMax[iMin])
    			minMax[iMin] = heights[i];
    		if (heights[i] > minMax[iMax])
    			minMax[iMax] = heights[i];
    	}
    	
    	return minMax; 	
    }
    
    public static void normalizeTerrain(float[] heights, final float value) {
    	
    	if (heights.length == 0)
    		return;
    	
    	float currentMin, currentMax;
    	float height;
    	
    	currentMin = heights[0];
    	currentMax = heights[0];
    	
    	for(int i = 0; i < heights.length; ++i) {
    		if (heights[i] < currentMin) {
    			currentMin = heights[i];
    		} else if (heights[i] > currentMax) {
    			currentMax = heights[i];
    		}
    	}
    	
    	if (currentMax <= currentMin)
    		return;
    	
    	height = currentMax - currentMin;
    	
    	for(int i = 0; i < heights.length; ++i) {
    		heights[i] = ((heights[i]-currentMin) / height) * value;
    	}
    }
    
	@Override
	public boolean collide(ICollidable c, ICollisionResults collisionResults) {
		
		if (c instanceof Point) {
			
			Point other = (Point)c;
			return collideWith(other, collisionResults);
		}
		return false;
	}

	@Override
	public boolean nearby(ICollidable c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SupervisedSpaceCollisionArena getSupervisedSpaceCollisionArena() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean collideWith(final Point p, ICollisionResults collisionResults) {
		
		final float xWorldLandPoint = p.getX() / scaleFactors.getX();
		final float zWorldLandPoint = p.getZ() / scaleFactors.getZ();
		
		int halfVerticesCount = verticesCount/2;
		int x = Math.round(xWorldLandPoint + halfVerticesCount);
		int z = Math.round(zWorldLandPoint + halfVerticesCount);
		
		if (!isInside(x, z))
			return false;
		
		final float terrainHeight = getHeight(p.getX(), p.getZ()); //w( verticesHeights[ (z*verticesCount)+x ] * scaleFactors.getY() );
		
		if ((p.getY() /*/ scaleFactors.getY()*/) <= terrainHeight) {//TODO
			
			if (collisionResults instanceof TerrainCollisionResults) {
				((TerrainCollisionResults)collisionResults).setTerrainCollisionCoordinates(new Vector2D(p.getX(), p.getZ()));
				((TerrainCollisionResults)collisionResults).setTerrainHeight(terrainHeight);
			}
			return true;
			
		} else 
			return false;
	}
	
	@Override
	public void addMixedTerrainModel(ITerrainChunkModel model) {
		if (!model.equals(this))
			this.mixedTerrainModels.add(model);
	}
	
	@Override
	public void removeMixedTerrainModel(ITerrainChunkModel model) {
		this.mixedTerrainModels.remove(model);
	}
	
}
