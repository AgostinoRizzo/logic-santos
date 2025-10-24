/**
 * 
 */
package it.unical.logic_santos.gameplay;

import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.spatial_entity.AbstractHuman;
import it.unical.logic_santos.spatial_entity.Player;
import it.unical.logic_santos.spatial_entity.Policeman;
import it.unical.logic_santos.spatial_entity.Walker;

/**
 * @author Agostino
 *
 */
public class GunBullet extends AbstractBullet {

	public GunBullet(AbstractHuman owner, LogicSantosApplication application) {
		super(owner, application);
	}

	@Override
	public float getFireRate() {
		return Gun.FIRE_RATE;
	}

	@Override
	public float getAccuracy() {
		return Gun.ACCURACY;
	}

	@Override
	public float getRange() {
		return Gun.RANGE;
	}
	
	@Override
	public LifeBar getHumanLifeBarReduction( final Class< ? extends AbstractHuman > humanClass ) {
		if ( humanClass == Walker.class )
			return ( new LifeBar(0.5f) );
		if ( humanClass == Policeman.class )
			return ( new LifeBar(0.1f) );
		if ( humanClass == Player.class )
			return ( new LifeBar(0.1f) );
		return ( new LifeBar(0.5f) );
	}
	
	@Override
	public LifeBar getVehicleLifeBarReduction() {
		return ( new LifeBar(0.1f) );
	}
	
	@Override
	public float getGainValue() {
		return 10.0f;
	}

	@Override
	public boolean explosionOnCollision() {
		return false;
	}

	@Override
	public byte getCookieTypeId() {
		return BulletCookie.GUN_COOKIE_ID;
	}

}
