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
public interface IWeapon {

	public IBullet shoot();
	public boolean hasBullets();
	public void setOwner(AbstractHuman owner);
	public void setApplication(LogicSantosApplication application);
	public boolean hasWeaponScope();
	
	
	public static byte getWeaponId( final IWeapon weapon ) {
		if ( weapon==null )
			return 3;
		if ( weapon.getClass()==Gun.class )
			return 0;
		else if ( weapon.getClass()==Rifle.class )
			return 1;
		else if ( weapon.getClass()==Cannon.class )
			return 2;
		return 0;
	}
	
	public static IWeapon createNewWeaponFromId( final byte id, AbstractHuman owner, LogicSantosApplication application ) {
		switch( id ) {
		case 0: return (new Gun( owner, application ));
		case 1: return (new Rifle( owner, application ));
		case 2: return (new Cannon( owner, application ));
		case 3: return null;
		default:return (new Gun( owner, application ));
		}
	}
	
}
