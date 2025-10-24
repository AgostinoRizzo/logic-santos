/**
 * 
 */
package it.unical.logic_santos.util.concurrence;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * @author Agostino
 *
 */
public class GraphicalLoader {

	private static GraphicalLoader instance = null;
	
	private static final int MAX_PENDING_LOADING = 10;
	private BlockingQueue<SpatialLoadingBean> pendindLoading = new ArrayBlockingQueue<SpatialLoadingBean>(MAX_PENDING_LOADING); 
	
	
	private GraphicalLoader() {
		
	}
	
	public static GraphicalLoader getInstance() {
		if (instance == null)
			instance = new GraphicalLoader();
		return instance;
	}
	
	public void attachSpatials() {
		try {
			while(!pendindLoading.isEmpty()) {
				SpatialLoadingBean loading = pendindLoading.take();
				loading.attachToNode();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void attachSpatialsBlocking() {
		try {
			SpatialLoadingBean loading = pendindLoading.take();
			loading.attachToNode();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void attachSpatialToNode(Spatial spatial, Node node) {
		SpatialLoadingBean loading = new SpatialLoadingBean(spatial, node);
		try {
			pendindLoading.put(loading);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
