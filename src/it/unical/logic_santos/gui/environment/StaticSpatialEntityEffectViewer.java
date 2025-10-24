/**
 * 
 */
package it.unical.logic_santos.gui.environment;

import com.jme3.renderer.queue.RenderQueue.ShadowMode;

import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;

/**
 * @author Agostino
 *
 */
public class StaticSpatialEntityEffectViewer extends StaticSpatialEntityViewer {

	public StaticSpatialEntityEffectViewer(LogicSantosApplication application) {
		super(application);
	}

	@Override
	public void attachSpatialEntity(ISpatialEntity entity) {
		hideEntity( entity );
		super.attachSpatialEntity( entity );
	}
	@Override
	protected void showEntity(ISpatialEntity entity) {
		( (ModelBasedPhysicalExtension) entity.getAbstractPhysicalExtension() )
			.getModelSpatial().setShadowMode( ShadowMode.CastAndReceive );
	}
	
	@Override
	protected void hideEntity(ISpatialEntity entity) {
		( (ModelBasedPhysicalExtension) entity.getAbstractPhysicalExtension() )
			.getModelSpatial().setShadowMode( ShadowMode.Cast );
	}

}
