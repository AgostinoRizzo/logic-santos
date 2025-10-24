/**
 * 
 */
package it.unical.logic_santos.spatial_entity;

import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.physics.extension.IPhysicalExtension;
import it.unical.logic_santos.toolkit.math.Vector3D;

/**
 * @author Agostino
 *
 */
public class CoffeeShop extends AbstractStaticSpatialEntity {

	private static final String NAME = "CoffeeShop";
	
	public CoffeeShop() {
		super(CoffeeShop.getCoffeeShopPhysicalExtension(Vector3D.ZERO));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	public CoffeeShop(final Vector3D _spatialPosition) {
		super(_spatialPosition, CoffeeShop.getCoffeeShopPhysicalExtension(_spatialPosition));
		this.physicalExtension.setSpatialEntityOwner(this);
	}
	
	@Override
	public String getName() {
		return CoffeeShop.NAME;
	}
	
	@Override
	public ISpatialEntity cloneSpatialEntity() {
		return new CoffeeShop();
	}

	public static IPhysicalExtension getCoffeeShopPhysicalExtension(final Vector3D _spatialPosition) {
		return ModelingConfig.DEFAULT_PHYSICAL_EXTENSION_GENERATOR.generateCoffeeShopPhysicalExtension(_spatialPosition);
	}
}
