/**
 * 
 */
package it.unical.logic_santos.gui.environment;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import it.unical.logic_santos.gui.application.IGraphicalEntity;
import it.unical.logic_santos.gui.application.LogicSantosApplication;

/**
 * @author Agostino
 *
 */
public class GraphicalSmoke implements IGraphicalEntity {
	
	private ParticleEmitter emitter=null;
	//private float angle=0.0f;
	private LogicSantosApplication application=null;

	private static final int NUM_PARTICLES = 300;
	private static final float VELOCITY_VARIATION = 1.0f;
	private static final Vector3f INITIAL_VELOCITY = new Vector3f( 0.0f, 0.5f, 0.0f );

	public GraphicalSmoke( LogicSantosApplication application ) {
		this.application = application;
	}
	
	@Override
	public void loadComponents() throws Exception {
		emitter=new ParticleEmitter( "Emitter", Type.Triangle, NUM_PARTICLES );
		emitter.setGravity( Vector3f.ZERO );
		emitter.getParticleInfluencer().setVelocityVariation( VELOCITY_VARIATION );
		emitter.setLowLife( 1.0f );
		emitter.setHighLife( 1.0f );
		emitter.setStartColor( new ColorRGBA( 0.5f, 0.5f, 0.5f, 1.0f ) );
		emitter.setEndColor( new ColorRGBA( 0.3f, 0.3f, 0.3f, 0.0f ) );
		emitter.getParticleInfluencer().setInitialVelocity( INITIAL_VELOCITY );
		emitter.setImagesX( 15 );
		
		Material mat = new Material( application.getAssetManager(), GraphicalEnviromentConfig.PARTICLES_MATERIAL_NAME );
		mat.setTexture( "Texture",  application.getAssetManager().loadTexture( GraphicalEnviromentConfig.SMOKE_PARTICLES_TEXTURE_NAME ) );
		emitter.setMaterial( mat );
	}

	@Override
	public void attachComponentsToGraphicalEngine() {
		application.getRootNode().attachChild( emitter );
	}

	@Override
	public void detachComponentsToGraphicalEngine() {
		application.getRootNode().detachChild( emitter );
	}

	@Override
	public void setTranslation(Vector3f translation) {
		emitter.setLocalTranslation( translation );
	}

}
