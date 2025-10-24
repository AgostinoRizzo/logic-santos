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
public class RifleBullet extends AbstractBullet {

	public RifleBullet(AbstractHuman owner, LogicSantosApplication application) {
		super(owner, application);
	}

	@Override
	public float getFireRate() {
		return Rifle.FIRE_RATE;
	}

	@Override
	public float getAccuracy() {
		return Rifle.ACCURACY;
	}

	@Override
	public float getRange() {
		return Rifle.RANGE;
	}
	
	@Override
	public LifeBar getHumanLifeBarReduction( final Class< ? extends AbstractHuman > humanClass ) {
		if ( humanClass == Walker.class )
			return ( new LifeBar(0.8f) );
		if ( humanClass == Policeman.class )
			return ( new LifeBar(0.2f) );
		if ( humanClass == Player.class )
			return ( new LifeBar(0.2f) );
		return ( new LifeBar(0.5f) );
	}
	
	@Override
	public LifeBar getVehicleLifeBarReduction() {
		return ( new LifeBar(0.2f) );
	}
	
	@Override
	public float getGainValue() {
		return 50.0f;
	}

	@Override
	public boolean explosionOnCollision() {
		return false;
	}
	
	@Override
	public byte getCookieTypeId() {
		return BulletCookie.RIFLE_COOKIE_ID;
	}

}
