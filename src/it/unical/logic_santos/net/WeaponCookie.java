/**
 * 
 */
package it.unical.logic_santos.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import it.unical.logic_santos.gameplay.Cannon;
import it.unical.logic_santos.gameplay.Gun;
import it.unical.logic_santos.gameplay.IWeapon;
import it.unical.logic_santos.gameplay.Rifle;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.spatial_entity.AbstractHuman;

/**
 * @author Agostino
 *
 */
public class WeaponCookie implements ICookie {

	private byte weaponId;
	
	public WeaponCookie() {}
	
	public WeaponCookie( final IWeapon weapon ) {
		if ( weapon==null )
			this.weaponId=0;
		else if ( weapon instanceof Gun )
			this.weaponId=1;
		else if ( weapon instanceof Rifle )
			this.weaponId=2;
		else if ( weapon instanceof Cannon )
			this.weaponId=3;
		else
			this.weaponId=0;
	}
	
	@Override
	public void writeToDataOutputStream(DataOutputStream out) throws IOException {
		out.writeByte( weaponId );
		
	}

	@Override
	public void readFromDataInputStream(DataInputStream in) throws IOException {
		weaponId = in.readByte();
	}
	
	public IWeapon getWeapon(AbstractHuman owner, LogicSantosApplication application) {
		if ( weaponId==0 )
			return null;
		if ( weaponId==1 )
			return ( new Gun(owner, application) );
		if ( weaponId==2 )
			return ( new Rifle(owner, application) );
		if ( weaponId==3 )
			return ( new Cannon(owner, application) );
		return null;
	}

}
