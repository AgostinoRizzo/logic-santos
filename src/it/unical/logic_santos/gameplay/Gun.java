/**
 * 
 */
package it.unical.logic_santos.gameplay;

import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.spatial_entity.AbstractHuman;

/**
 * @author Agostino
 *
 */
public class Gun implements IWeapon {
	
	private AbstractHuman owner=null;
	private LogicSantosApplication application=null;
	
	public static final float FIRE_RATE = 0.1f;
	public static final float ACCURACY  = 0.3f;
	public static final float RANGE     = 0.3f;
	
	public Gun() {}
	
	public Gun( AbstractHuman owner, LogicSantosApplication application ) {
		this.owner = owner;
		this.application = application;
	}
	
	@Override
	public IBullet shoot() {
		return new GunBullet( owner, application );
	}

	@Override
	public boolean hasBullets() {
		return true;
	}
	
	@Override
	public void setOwner(AbstractHuman owner) {
		this.owner = owner;
	}
	
	@Override
	public void setApplication(LogicSantosApplication application) {
		this.application = application;
	}
	
	@Override
	public boolean hasWeaponScope() {
		return false;
	}
	
	public static float getFireRate() {
		return FIRE_RATE;
	}
	
	public static float getAccuracy() {
		return ACCURACY;
	}
	
	public static float getRange() {
		return RANGE;
	}

	

}
