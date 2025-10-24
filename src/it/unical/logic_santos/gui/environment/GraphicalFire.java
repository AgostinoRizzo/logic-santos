/**
 * 
 */
package it.unical.logic_santos.gui.environment;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import it.unical.logic_santos.gui.application.IGraphicalEntity;
import it.unical.logic_santos.gui.application.LogicSantosApplication;

/**
 * @author Agostino
 *
 */
public class GraphicalFire implements IGraphicalEntity {

	private ParticleEmitter fireEmitter=null;
	private LogicSantosApplication application=null;
	private float time=0.0f;
	private float durationTime;
	private float fullSizeDurationTime;
	private float reductionSizeDurationTime;
	private boolean isDone=false;
	
	private static final int NUM_PARTICLES = 300;
	
	private static final float FIRE_EMITTER_SPHERE_SHAPE_RADIUS = 0.1f;
	private static final float PERCENTAGE_TIME_TO_CLOSE_FIRE = 0.5f;
	private static final float FIRE_START_SIZE = 30.8f;
	private static final float FIRE_END_SIZE = 0.8f;
	
	public GraphicalFire( final float durationTime, LogicSantosApplication application ) {
		this.durationTime = durationTime;
		this.fullSizeDurationTime = this.durationTime*PERCENTAGE_TIME_TO_CLOSE_FIRE;
		this.reductionSizeDurationTime = this.durationTime-this.fullSizeDurationTime;
		this.application = application;
	}
	
	@Override
	public void loadComponents() throws Exception {
		fireEmitter = new ParticleEmitter( "Fire", ParticleMesh.Type.Triangle, NUM_PARTICLES );
		
		Material mat = new Material( application.getAssetManager(), GraphicalEnviromentConfig.PARTICLES_MATERIAL_NAME );
		mat.setTexture( "Texture", application.getAssetManager().loadTexture( GraphicalEnviromentConfig.FLAME_TEXTURE_NAME ) );
		mat.setFloat( "Softness", 3.0f );
	
		fireEmitter.setMaterial( mat );
		fireEmitter.setShape( new EmitterSphereShape( Vector3f.ZERO, FIRE_EMITTER_SPHERE_SHAPE_RADIUS ) );
		fireEmitter.setImagesX( 2 );
		fireEmitter.setImagesY( 2 );
		fireEmitter.setStartColor( new ColorRGBA( 1.0f, 1.0f, 0.0f, 0.5f ) );
		fireEmitter.setEndColor( new ColorRGBA( 1.0f, 0.0f, 0.0f, 1.0f ) );
		fireEmitter.setStartSize( FIRE_START_SIZE );
		fireEmitter.setEndSize( FIRE_END_SIZE );
		fireEmitter.setGravity( 0.0f, -15.3f, 0.0f );
		fireEmitter.setLowLife( 0.5f );
		fireEmitter.setHighLife( 3.0f );
		fireEmitter.setLocalScale( 100.0f );
		isDone=false;
	}

	@Override
	public void attachComponentsToGraphicalEngine() {
		application.getRootNode().attachChild( fireEmitter );
		
	}

	@Override
	public void detachComponentsToGraphicalEngine() {
		application.getRootNode().detachChild( fireEmitter );
	}

	@Override
	public void setTranslation(Vector3f translation) {
		fireEmitter.setLocalTranslation( translation );
	}
	
	public void update( final float tpf ) {
		if ( isDone || (durationTime<0.0f) )
			return;
		
		time+=tpf;
		if ( time>=durationTime )
			isDone=true;
		else if ( time>=fullSizeDurationTime ) {
			final float delta = 1.0f-((time-fullSizeDurationTime)/reductionSizeDurationTime);
			//fireEmitter.setShape( new EmitterSphereShape( Vector3f.ZERO, FIRE_EMITTER_SPHERE_SHAPE_RADIUS*delta ) );
			fireEmitter.setStartSize( FIRE_START_SIZE*delta );
			fireEmitter.setEndSize( FIRE_END_SIZE*delta );
		}
	}
	
	public boolean isDone() {
		return isDone;
	}
	
	public void reset() {
		time=0.0f;
		isDone=false;
		fireEmitter.setStartSize( FIRE_START_SIZE );
		fireEmitter.setEndSize( FIRE_END_SIZE );
	}

}
