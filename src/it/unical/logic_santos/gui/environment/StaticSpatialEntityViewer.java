/**
 * 
 */
package it.unical.logic_santos.gui.environment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.spatial_entity.Player;

/**
 * @author Agostino
 *
 */
public class StaticSpatialEntityViewer implements ISpatialEntityViewer {

	private LogicSantosApplication application=null;
	private Set< ISpatialEntity > hiddenEntities=null;
	private Set< ISpatialEntity > visibleEntities=null;
	private float far=DEFAULT_FAR;
	private float timeCountUpdate=0.0f;
	
	private Lock lock = new ReentrantLock();
	
	public static final float DEFAULT_FAR = 3000.0f;
	public static final float INFINITE_FAR = Float.MAX_VALUE;
	public static final float TIME_TO_UPDATE = 1.0f; // expressed in seconds
	
	public StaticSpatialEntityViewer( LogicSantosApplication application ) {
		lock.lock();
		this.application = application;
		this.hiddenEntities = new HashSet< ISpatialEntity >();
		this.visibleEntities = new HashSet< ISpatialEntity >();
		lock.unlock();
	}
	
	@Override
	public void attachSpatialEntity(ISpatialEntity entity) {
		lock.lock();
		hiddenEntities.add( entity );
		lock.unlock();
	}

	@Override
	public void detachSpatialEntity(ISpatialEntity entity) {
		lock.lock();
		if ( !removeFromVisible( entity ) )
			hiddenEntities.remove( entity );
		lock.unlock();
	}

	@Override
	public void setFrustumFar(float far) {
		lock.lock();
		if ( far>0.0f )
			this.far = far;
		lock.unlock();
	}

	@Override
	public void update(Vector3f subjectPosition, float tpf) {
		lock.lock();
		if ( !itsTimeToUpdate(tpf) ) {
			lock.unlock();
			return;
		}
		List< Vector3f > subjects = new ArrayList< Vector3f >();
		subjects.add( subjectPosition );
		hideUnvisibleEntities( subjects );
		showUnhiddenEntities( subjects );	
		lock.unlock();
	}
	
	@Override
	public void update(List<Vector3f> subjectPositions, float tpf) {
		lock.lock();
		if ( !itsTimeToUpdate(tpf) ) {
			lock.unlock();
			return;
		}
		hideUnvisibleEntities( subjectPositions );
		showUnhiddenEntities( subjectPositions );		
		lock.unlock();
	}

	@Override
	public void update(float tpf) {
		lock.lock();
		if ( !itsTimeToUpdate(tpf) ) {
			lock.unlock();
			return;
		}
		
		final List< Player > players = application.getPlayers();
		final List< Camera > cameras = application.getActivatedCameras();
		List< Vector3f > subjectPositions = new ArrayList< Vector3f >();
		
		for( Player p: players)
			if ( p!=null )
				subjectPositions.add( p.getHumanPhysicalActivity().getControl().getPhysicsLocation() );
		for( Camera c: cameras)
			if ( c!=null )
				subjectPositions.add( c.getLocation() );
		
		hideUnvisibleEntities( subjectPositions );
		showUnhiddenEntities( subjectPositions );		
		lock.unlock();
	}
	
	public void forceNextUpdate() {
		timeCountUpdate=TIME_TO_UPDATE;
	}
	
	public Set< ISpatialEntity > getVisibleStaticSpatialEntities() {
		return visibleEntities;
	}
	
	private boolean removeFromVisible( ISpatialEntity entity ) {
		if ( visibleEntities.contains(entity) ) {
			hideEntity( entity );
			visibleEntities.remove( entity );
			return true;
		}
		return false;
	}
	
	private void hideUnvisibleEntities( final List< Vector3f > subjectPositions ) {
		
		for (Iterator< ISpatialEntity > it = visibleEntities.iterator(); it.hasNext();) {
			ISpatialEntity entity = it.next();
			
			if ( !isVisible( entity.getSpatialTranslation().toVector3f(), subjectPositions ) ) {
				
				hideEntity( entity );
				hiddenEntities.add( entity );
				it.remove();
				
			}
		}
	}
	
	private void showUnhiddenEntities( final List< Vector3f > subjectPositions ) {
		
		for (Iterator< ISpatialEntity > it = hiddenEntities.iterator(); it.hasNext();)  {
			ISpatialEntity entity = it.next();
			
			if ( isVisible( entity.getSpatialTranslation().toVector3f(), subjectPositions ) ) {
				
				showEntity( entity );
				visibleEntities.add( entity );
				it.remove();
				
			}
		}
	}
	
	private boolean isVisible( final Vector3f position, final List< Vector3f > subjectPositions ) {
		for( Vector3f sbj: subjectPositions ) 
			if ( sbj.distance( position )<=far )
				return true;
		return false;
	}
	
	private boolean itsTimeToUpdate( final float tpf ) {
		timeCountUpdate+=tpf;
		if ( timeCountUpdate>=TIME_TO_UPDATE ) {
			timeCountUpdate=0.0f;
			return true;
		}
		return false;
	}
	
	protected void showEntity( ISpatialEntity entity ) {
		application.getRootNode()
		.attachChild( ((ModelBasedPhysicalExtension) entity.getAbstractPhysicalExtension())
					  .getModelSpatial() );
	}
	
	protected void hideEntity( ISpatialEntity entity ) {
		application.getRootNode()
			.detachChild( ((ModelBasedPhysicalExtension) entity.getAbstractPhysicalExtension())
						  .getModelSpatial() );
	}

}
