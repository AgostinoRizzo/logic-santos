/**
 * 
 */
package it.unical.logic_santos.design.modeling;


import com.jme3.bounding.BoundingVolume;
import com.jme3.bounding.BoundingVolume.Type;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import it.unical.logic_santos.collision.ICollidable;
import it.unical.logic_santos.collision.ICollisionResults;
import it.unical.logic_santos.collision.SupervisedSpaceCollisionArena;
import it.unical.logic_santos.physics.extension.AbstractBoundingVolume;
import it.unical.logic_santos.physics.extension.AbstractPhysicalExtension;
import it.unical.logic_santos.physics.extension.BoundingBox;
import it.unical.logic_santos.physics.extension.BoundingSphere;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.toolkit.math.Vector3D;


/**
 * @author Agostino
 *
 */
public class ModelBasedPhysicalExtension extends AbstractPhysicalExtension {
	
	private Spatial modelSpatial=null;

	public ModelBasedPhysicalExtension(Spatial model, final Vector3D spatialPosition, ISpatialEntity _spatialEntityOwner, final float scaleFactor, final Vector3D translationAdjustment, final boolean isNode) {
		
		super(_spatialEntityOwner);
		
		if (isNode)
			;//((Node) model).setLocalScale(scaleFactor);
		else
			model.scale(scaleFactor);
		Vector3f position = spatialPosition.toVector3f();
		position.addLocal( translationAdjustment.toVector3f() );
		model.setLocalTranslation( position );
		this.modelSpatial = model;
		
		AbstractBoundingVolume boundingVolume=null;
		BoundingVolume modelBoundingVolume = modelSpatial.getWorldBound();
		
		Vector3D centerPosition = new Vector3D(modelBoundingVolume.getCenter().getX(),
												modelBoundingVolume.getCenter().getY(),
												modelBoundingVolume.getCenter().getZ());
		
		if (modelBoundingVolume.getType() == Type.AABB) {
			
			com.jme3.bounding.BoundingBox modelBoundingBox = (com.jme3.bounding.BoundingBox)modelBoundingVolume;
			boundingVolume = new BoundingBox(modelBoundingBox.getXExtent(), modelBoundingBox.getYExtent(), modelBoundingBox.getZExtent(), this);
			
			boundingVolume.setCenterPosition(centerPosition);
			
		} else if (modelBoundingVolume.getType() == Type.Sphere) {
			
			com.jme3.bounding.BoundingSphere modelBoundingSphere = (com.jme3.bounding.BoundingSphere)modelBoundingVolume;
			boundingVolume = new BoundingSphere(modelBoundingSphere.getRadius(), this);
			
			boundingVolume.setCenterPosition(centerPosition);
			
		} else if (modelBoundingVolume.getType() == Type.Capsule) {
			
		}
		
		super.setBoundingVolume(boundingVolume);
		
	}

	@Override
	public boolean collide(ICollidable c, ICollisionResults collisionResults) {
		if (c instanceof ModelBasedPhysicalExtension) {
			return this.boundingVolume.collide(((ModelBasedPhysicalExtension) c).boundingVolume, collisionResults);
		}
		return false;
	}


	@Override
	public boolean nearby(ICollidable c) {
		if (c instanceof ModelBasedPhysicalExtension) {
			return this.boundingVolume.collide(((ModelBasedPhysicalExtension) c).boundingVolume, null);
		}
		return false;
	}


	@Override
	public SupervisedSpaceCollisionArena getSupervisedSpaceCollisionArena() {
		return boundingVolume.getSupervisedSpaceCollisionArena();
	}

	@Override
	public void updateTranslation() {
		boundingVolume.setCenterPosition(new Vector3D(super.spatialEntityOwner.getSpatialTranslation().toVector3f()));
		modelSpatial.setLocalTranslation(super.spatialEntityOwner.getSpatialTranslation().toVector3f());
		//System.out.println(super.spatialEntityOwner.getSpatialTranslation().toVector3f().toString());
		// TODO Auto-generated method stub
		
	}
	
	public Spatial getModelSpatial() {
		return modelSpatial;
	}
	
	public void setModelSpatial(Spatial m) {
		modelSpatial = m;
	}

	@Override
	public void onShootResetEffects() {
		super.onShootResetEffects();
		this.modelSpatial.setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
	}
	
	@Override
	public void onShootApplyEffects() {
		super.onShootApplyEffects();
		/*GraphicalExplosionEffetc explosionEffect = new GraphicalExplosionEffetc( this.modelSpatial.getLocalTranslation() );
		explosionEffect.loadComponents();
		explosionEffect.attachComponentsToGraphicalEngine();*/
		this.modelSpatial.setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.Off);
	}
}
