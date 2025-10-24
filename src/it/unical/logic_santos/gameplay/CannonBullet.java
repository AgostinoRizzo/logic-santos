/**
 * 
 */
package it.unical.logic_santos.gameplay;

import com.jme3.math.Vector3f;

import it.unical.logic_santos.gui.application.IGraphicalEntity;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.gui.environment.GraphicalSmoke;
import it.unical.logic_santos.spatial_entity.AbstractHuman;
import it.unical.logic_santos.spatial_entity.Player;
import it.unical.logic_santos.spatial_entity.Policeman;
import it.unical.logic_santos.spatial_entity.Walker;

/**
 * @author Agostino
 *
 */
public class CannonBullet extends AbstractBullet {

	private IGraphicalEntity smokeEffect=null;
	
	public CannonBullet(AbstractHuman owner, LogicSantosApplication application) {
		super(owner, application);
	}

	@Override
	public float getFireRate() {
		return Cannon.FIRE_RATE;
	}

	@Override
	public float getAccuracy() {
		return Cannon.ACCURACY;
	}

	@Override
	public float getRange() {
		return Cannon.RANGE;
	}
	
	@Override
	public LifeBar getHumanLifeBarReduction( final Class< ? extends AbstractHuman > humanClass ) {
		if ( humanClass == Walker.class )
			return ( new LifeBar(1.0f) );
		if ( humanClass == Policeman.class )
			return ( new LifeBar(1.0f) );
		if ( humanClass == Player.class )
			return ( new LifeBar(1.0f) );
		return ( new LifeBar(1.0f) );
	}
	
	@Override
	public LifeBar getVehicleLifeBarReduction() {
		return ( new LifeBar(1.0f) );
	}
	
	@Override
	public float getGainValue() {
		return 100.0f;
	}
	
	@Override
	public void initActivity(Vector3f startPosition, Vector3f direction) {
		super.initActivity(startPosition, direction);
		smokeEffect = new GraphicalSmoke( this.application );
		try {
			smokeEffect.loadComponents();
			smokeEffect.attachComponentsToGraphicalEngine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void finalizeActivity() {
		super.finalizeActivity();
		smokeEffect.detachComponentsToGraphicalEngine();
	}
	
	@Override
	public void update(float tpf) {
		super.update(tpf);
		if ( isActive() )
			smokeEffect.setTranslation( this.control.getPhysicsLocation() );
	}

	@Override
	public boolean explosionOnCollision() {
		return true;
	}
	
	@Override
	public byte getCookieTypeId() {
		return BulletCookie.CANNON_COOKIE_ID;
	}

}
