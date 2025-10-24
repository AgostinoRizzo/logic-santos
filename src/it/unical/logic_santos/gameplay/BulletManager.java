/**
 * 
 */
package it.unical.logic_santos.gameplay;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import it.unical.logic_santos.environment.sound.EffectSoundManager;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.spatial_entity.AbstractHuman;
import it.unical.logic_santos.spatial_entity.AbstractStaticSpatialEntity;
import it.unical.logic_santos.spatial_entity.AbstractVehicle;
import it.unical.logic_santos.spatial_entity.Walker;

/**
 * @author Agostino
 *
 */
public class BulletManager {

	private List< IBullet > bullets = new LinkedList< IBullet >();
	private List< AbstractStaticSpatialEntity > bulletVisibleStaticSpatialEntities=null;
	private LogicSantosApplication application=null;
	
	public BulletManager( LogicSantosApplication application ) {
		this.application = application;
		bulletVisibleStaticSpatialEntities = application.getBulletVisibleStaticSpatialEntities();
	}
	
	public void addBullet( IBullet newBullet ) {
		newBullet.setHumans( application.getHumans() );
		newBullet.setVehicles( application.getVehicles() );
		newBullet.setTerrainModel( application.getGraphicalTerrain().getTerrainModel() );
		bullets.add(newBullet);
		
		for(AbstractHuman h: application.getWalkerTrafficManager().getHumans()) {
			h.getHumanPhysicalActivity().run();
		}
	}
	
	public void update( final float tpf ) {
		for (Iterator< IBullet > it = bullets.iterator(); it.hasNext();) {
			IBullet b = it.next();
			if ( b.isActive() ) {
				
				b.update(tpf);
				b.setStaticSpatialEntities( bulletVisibleStaticSpatialEntities );
				
				final List< AbstractVehicle > vehicleCollisions = checkVehicleCollision(b);
				if ( b.checkTerrainCollision() || checkHumanCollision(b) || (vehicleCollisions!=null) || ( b.explosionOnCollision() && b.checkStaticSpatialEntitiesCollisions() ) ) {
					it.remove();
					if ( b.explosionOnCollision() ) {
						application.onExplosion( b.getCurrentTranslation() );
						if ( vehicleCollisions!=null )
							for( AbstractVehicle vehicle: vehicleCollisions )
								application.onExplosion( vehicle );
						
						EffectSoundManager.getInstance().onExplosion( b.getCurrentTranslation() );
					}
					b.finalizeActivity();
				}
				
			} else {
				it.remove();
				b.finalizeActivity();
			}
		}
	}
	
	public BulletManagerCookie getCookie() {
		BulletManagerCookie cookie = new BulletManagerCookie();
		
		cookie.bulletsCookies.clear();
		final int bulletsSize = bullets.size();
		for( int i=0; i<bulletsSize; ++i )
			cookie.bulletsCookies.add(bullets.get(i).getCookie());
		
		return cookie;
	}
	
	public void setCookie( final BulletManagerCookie cookie ) {
		for( IBullet b: bullets )
			b.finalizeActivity();
		bullets.clear();
		
		final int bulletsCookiesSize = cookie.bulletsCookies.size();
		for( int i=0; i<bulletsCookiesSize; ++i ) {
			final BulletCookie bulletCookie = cookie.bulletsCookies.get(i);
			IBullet newBullet = createNewBulletFromCookie( bulletCookie, this.application );
			newBullet.initActivity( bulletCookie.startPositionCookie.vector.clone(), 
									bulletCookie.directionCookie.vector.clone() );
			newBullet.setHumans( application.getHumans() );
			newBullet.setVehicles( application.getVehicles() );
			newBullet.setTerrainModel( application.getGraphicalTerrain().getTerrainModel() );
			bullets.add( newBullet );
		}
	}
	
	private boolean checkHumanCollision( IBullet b ) {
		List< AbstractHuman > humanCollisionResults = b.checkHumanCollisions();
		if ( humanCollisionResults.isEmpty() )
			return false;
		
		for( AbstractHuman h: humanCollisionResults ) {
			/* apply human reaction */
			h.getPhysicalActivity().onShootReaction( b );
			if ( ( b.getOwner()==application.getPlayer() ) && (h instanceof Walker) )
				application.getPlayer().getWantedStars().onShootHit(b);
			//h.getHumanPhysicalActivity().getControl().jump(); // TODO: apply human reaction ...
		}
		return true;
	}
	
	private List< AbstractVehicle > checkVehicleCollision( IBullet b ) {
		List< AbstractVehicle > vehicleCollisionResults = b.checkVehicleCollisions();
		if ( vehicleCollisionResults.isEmpty() )
			return null;
		
		for( AbstractVehicle v: vehicleCollisionResults ) {
			/* apply human reaction */
			v.getPhysicalActivity().onShootReaction( b );
			application.getPlayer().getWantedStars().onShootHit(b);
			// TODO: apply vehicle reaction ...
		}
		return vehicleCollisionResults;
	}
	
	private static IBullet createNewBulletFromCookie( final BulletCookie cookie, final LogicSantosApplication application ) {
		AbstractHuman owner=null;
		switch( cookie.ownerTypeId ) {
		case BulletCookie.MASTER_PLAYER_TYPE_OWNER_ID:
			owner= (application.getRemoteCommunicator().isMaster())
						? application.getPlayer()
						: application.getRemotePlayer();
		case BulletCookie.CLIENT_PLAYER_TYPE_OWNER_ID:
			owner= (application.getRemoteCommunicator().isMaster())
						? application.getRemotePlayer()
						: application.getPlayer();
		case BulletCookie.POLICEMAN_TYPE_OWNER_ID:
			owner= application.getPolicemanById(cookie.ownerId);
		}
		
		switch(cookie.type) {
		case BulletCookie.GUN_COOKIE_ID:    return ( new GunBullet(owner, application) );
		case BulletCookie.RIFLE_COOKIE_ID:  return ( new RifleBullet(owner, application) );
		case BulletCookie.CANNON_COOKIE_ID: return ( new CannonBullet(owner, application) );
		default: return null;
		}
	}
	
}
