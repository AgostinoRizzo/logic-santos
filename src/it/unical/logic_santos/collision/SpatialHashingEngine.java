/**
 * The ISpatialHashingEngine represents a collision detection engine based on spatial hashing partitioning technique
 * to detect collisions between ICollidable game objects in the Arena
 */

package it.unical.logic_santos.collision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import it.unical.logic_santos.spatial_entity.ClassicTree;


/**
 * @author agostino
 * @version 1.0
 * @category CollisionDetectionEngine
 */

public class SpatialHashingEngine implements ICollisionDetectionEngine { // represents a collision detection engine based on spatial hashing partitioning technique
	                                                                     // to detect collisions between ICollidable game objects in the Arena 

	// maximum number of buckets used to models the Hash Table (spatial hashing partitioning) ...
	public static final int MAX_BUCKETS = 1000000000;
	
	public static final float DEFAULT_CELL_SIZE = 300.0f;
	
    // Array of LinkedLists of ICosetNearbyCllidable game objects that represents the Hash Table used for the spatial hashing partitioning
	private NearbyCollidablesSet[] buckets; // each bucket is a list of ICollidable objects (a cell that contains nearby ICollidable game objects) ...

	private float cellSize = DEFAULT_CELL_SIZE;
	private int columnBuckets;
	
	private ICollidableHashKey hashKeyFunction; // hash function used to determines the buckets of a ICollidable game object ...
	
	private ICollisionArena collisionArena=null;
	
	// ... CONSTRUCTORS ...
	
	public SpatialHashingEngine(final ICollisionArena arena) throws IllegalArgumentException {
		initHashTable(arena, DEFAULT_CELL_SIZE);
		hashKeyFunction = new BoundingVolumeBasedHashKey(this);
	}
	
	public SpatialHashingEngine(final ICollisionArena arena, final float cellSize) throws IllegalArgumentException {
		initHashTable(arena, cellSize);
		hashKeyFunction = new BoundingVolumeBasedHashKey(this);
	}
	
    // ... GETTER AND SETTER METHODS ...
	
	public int getNumberOfBuckets() {
		return buckets.length;
	}
	
	public float getCellSize() {
		return cellSize;
	}
	
	public int getNumberOfColumnBuckets() {
		return columnBuckets;
	}
	
	public int getNumberOfRowBuckets() {
		return (getNumberOfBuckets() - columnBuckets);
	}
	
	// ... GENERAL METHODS ...
	
	@Override
	public boolean addCollidable(ICollidable c) {
		//System.out.println("Supervised " + c.getSupervisedSpaceCollisionArena().getXExtension());
		boolean ans = false; if (c instanceof ClassicTree) System.out.println("Adding Classic Tree");
		Collection<Integer> keys = hashKeyFunction.getHashCodes(c);
		for(Integer key: keys)
		    if (buckets[key].collidables.add(c))
		    	ans = true;
		return ans;
	}

	@Override
	public boolean removeCollidable(ICollidable c) {
		
		boolean ans = false;
		Collection<Integer> keys = hashKeyFunction.getHashCodes(c);
		for(Integer key: keys)
			if (buckets[key].collidables.remove(c))
				ans = true;
		return ans;
	}
	
	@Override
	public void removeCollidables() {
		for(int i=0; i<buckets.length; ++i)
			buckets[i].collidables.clear();
	}

	@Override
	public boolean updateCollidable(ICollidable c) {
		
		
		return false;
	}

	@Override
	public Set<ICollidable> getNearby(ICollidable c) {
		
		Set<ICollidable> nearby = new TreeSet<ICollidable>();
		Collection<Integer> keys = hashKeyFunction.getHashCodes(c);
		for(Integer key: keys)
			nearby.addAll(buckets[key].collidables);
		return nearby;
	}
    
	@Override
	public Set<ICollidable> checkCollisions(ICollidable c, List<ICollisionResults> collisionResults) {
		
		Set<ICollidable> collidables = new TreeSet<ICollidable>();
		Collection<Integer> keys = hashKeyFunction.getHashCodes(c);
		Set<ICollidable> nearby;
		
		collisionResults = new ArrayList<ICollisionResults>();
		ICollisionResults currentCollisionResults=null;
		for(Integer key: keys) {
			
			nearby = buckets[key].collidables;
			for(ICollidable cObj: nearby) 
				if ( (!c.equals(cObj)) && (c.nearby(cObj)) && (c.collide(cObj, currentCollisionResults)) ) {
					collidables.add(cObj);
					collisionResults.add(currentCollisionResults);
				}
		}
		return collidables;
	}
	
	public void printBucketsLoad() {
		for (int i = 0; i < buckets.length; ++i) {
			System.out.println("Bucket ID: " + i + ", Load: " + buckets[i].collidables.size());
		}
	}
	
	// ... PRIVATE METHODS ...
	
	private void initHashTable(final ICollisionArena arena, final float cellSize) throws IllegalArgumentException {
		
		this.collisionArena = arena;
		
		if ( (arena.getWidthSize() > 0) && (arena.getDepthSize() > 0) && (cellSize > 0) && 
			 (cellSize <= arena.getWidthSize()) && (cellSize <= arena.getDepthSize()) ) {
			
			this.cellSize = cellSize;
			
			this.columnBuckets = (int)((float)Math.ceil((double)(arena.getWidthSize() / cellSize)));
		    final int numberOfBuckets = (int)((float)this.columnBuckets * 
		    		                          (float)Math.ceil((double)(arena.getDepthSize() / cellSize)));
		    
		    if (numberOfBuckets > MAX_BUCKETS) throw new IllegalArgumentException();
		    
		    buckets = new NearbyCollidablesSet[numberOfBuckets];	
		    for (int i = 0; i < buckets.length; ++i) {
				buckets[i] = new NearbyCollidablesSet();
			}
		    
		} else 
			throw new IllegalArgumentException();
	}

	public ICollisionArena getCollisionArena() {
		return collisionArena;
	}

	public void setCollisionArena(ICollisionArena collisionArena) {
		this.collisionArena = collisionArena;
	}

	
	
	
	
}
