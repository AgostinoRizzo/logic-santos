/**
 * 
 */
package it.unical.logic_santos.gui.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.jme3.math.Vector3f;

import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.spatial_entity.AbstractVehicle;

/**
 * @author Agostino
 *
 */
public class GraphicalExplosionEffectManager {

	private List< GraphicalExplosionEffect > readyExplosionEffectsPool=null;
	private List< GraphicalExplosionEffect > activeExplosionEffectsPool=null;
	private List< GraphicalFire > activeFireEffectsPool=null;
	private HashMap< GraphicalFire, AbstractVehicle > activeVehicleFireEffectPool=null;
	private LogicSantosApplication application=null;
	
	private static final int   EXPLOSION_EFFECT_POOL_SIZE = 10;
	private static final float EXPLOSION_FIRE_DURATION = 10.0f; // expressed in seconds
	
	public GraphicalExplosionEffectManager( LogicSantosApplication application ) {
		this.application = application;
		this.readyExplosionEffectsPool = new LinkedList< GraphicalExplosionEffect >();
		this.activeExplosionEffectsPool = new LinkedList< GraphicalExplosionEffect >();
		this.activeFireEffectsPool = new LinkedList< GraphicalFire >();
		this.activeVehicleFireEffectPool = new HashMap< GraphicalFire, AbstractVehicle >();
	}
	
	public void initComponents() {
		finalizeComponents();
		loadExplisionEffectsPool();
	}
	
	public void finalizeComponents() {
		for( GraphicalExplosionEffect effect: readyExplosionEffectsPool )
			effect.detachComponentsToGraphicalEngine();
		readyExplosionEffectsPool.clear();
		
		for( GraphicalExplosionEffect effect: activeExplosionEffectsPool )
			effect.detachComponentsToGraphicalEngine();
		activeExplosionEffectsPool.clear();
	}
	
	public boolean onExplosion( final Vector3f translation ) {
		if ( readyExplosionEffectsPool.isEmpty() )
			return false;
		
		GraphicalExplosionEffect explosionEffect =
				readyExplosionEffectsPool.remove( 0 );
		explosionEffect.reset();
		explosionEffect.setTranslation( translation );
		explosionEffect.attachComponentsToGraphicalEngine();
		activeExplosionEffectsPool.add( explosionEffect ); System.out.println("ON EXPLOSION METHOD!!!");
		
		GraphicalFire fireEffect =
				new GraphicalFire( EXPLOSION_FIRE_DURATION, application );
		try {
			fireEffect.loadComponents();
		} catch (Exception e) {
			e.printStackTrace();
		}
		fireEffect.setTranslation( translation );
		fireEffect.attachComponentsToGraphicalEngine();
		activeFireEffectsPool.add( fireEffect );
		return true;
	}
	
	public boolean onExplosion( final AbstractVehicle vehicle ) {
		if ( readyExplosionEffectsPool.isEmpty() )
			return false;
		
		GraphicalExplosionEffect explosionEffect =
				readyExplosionEffectsPool.remove( 0 );
		explosionEffect.reset();
		explosionEffect.setTranslation( vehicle.getVehiclePhysicalActivity().getControl().getPhysicsLocation() );
		explosionEffect.attachComponentsToGraphicalEngine();
		activeExplosionEffectsPool.add( explosionEffect ); System.out.println("ON EXPLOSION METHOD!!!");
		
		GraphicalFire fireEffect =
				new GraphicalFire( EXPLOSION_FIRE_DURATION, application );
		try {
			fireEffect.loadComponents();
		} catch (Exception e) {
			e.printStackTrace();
		}
		fireEffect.setTranslation( vehicle.getVehiclePhysicalActivity().getControl().getPhysicsLocation() );
		fireEffect.attachComponentsToGraphicalEngine();
		activeVehicleFireEffectPool.put( fireEffect, vehicle );
		return true;
	}
	
	public void removeVehicleFireEffect( final AbstractVehicle vehicle ) {
		Set< GraphicalFire > fireEffects = activeVehicleFireEffectPool.keySet();
		List< GraphicalFire > fireEffectToRemove = new ArrayList< GraphicalFire >();
		
		for( GraphicalFire fireEffect: fireEffects )
			if ( activeVehicleFireEffectPool.get( fireEffect )==vehicle )
				fireEffectToRemove.add( fireEffect );
		
		for (GraphicalFire fireEffect : fireEffectToRemove) {
			fireEffect.detachComponentsToGraphicalEngine();
			activeVehicleFireEffectPool.remove( fireEffect );
		}
	}
	
	public void update( final float tpf ) {
		for ( Iterator< GraphicalExplosionEffect > it = activeExplosionEffectsPool.iterator(); it.hasNext(); ) {
			GraphicalExplosionEffect effect = (GraphicalExplosionEffect) it.next();
			
			effect.update(tpf);
			
			if ( effect.isDone() ) {
				effect.detachComponentsToGraphicalEngine();
				effect.reset();
				it.remove();
				readyExplosionEffectsPool.add( effect );
			}
		}
		
		for ( Iterator< GraphicalFire > it = activeFireEffectsPool.iterator(); it.hasNext(); ) {
			GraphicalFire effect = (GraphicalFire) it.next();
			
			effect.update(tpf);
			
			if ( effect.isDone() ) {
				effect.detachComponentsToGraphicalEngine();
				it.remove();
			}
		}
		
		Set< GraphicalFire > vehicleFires = activeVehicleFireEffectPool.keySet();
		List< GraphicalFire > vehicleFiresToRemove = new ArrayList< GraphicalFire >();
		for (Iterator< GraphicalFire > it = vehicleFires.iterator(); it.hasNext();) {
			GraphicalFire vehicleFire = (GraphicalFire) it.next();
			
			vehicleFire.setTranslation( activeVehicleFireEffectPool.get( vehicleFire ).getVehiclePhysicalActivity().getControl().getPhysicsLocation() );
			vehicleFire.update(tpf);
			
			if ( vehicleFire.isDone() ) {
				vehicleFire.detachComponentsToGraphicalEngine();
				vehicleFiresToRemove.add( vehicleFire );
			}
		}
		
		for( GraphicalFire vehicleFire: vehicleFiresToRemove )
			activeVehicleFireEffectPool.remove( vehicleFire );
		
		
	}
	
	private void loadExplisionEffectsPool() {
		readyExplosionEffectsPool.clear();
		for( int i=0; i<EXPLOSION_EFFECT_POOL_SIZE; ++i ) {
			
			GraphicalExplosionEffect explosionEffect = 
					new GraphicalExplosionEffect( application );
			try {
				
				explosionEffect.loadComponents();
				explosionEffect.reset();
				readyExplosionEffectsPool.add( explosionEffect );
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
