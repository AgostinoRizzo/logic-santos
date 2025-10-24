/**
 * 
 */
package it.unical.logic_santos.physics.activity;

import java.util.List;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LodControl;

import it.unical.logic_santos.design.modeling.ModelBasedPhysicalExtension;
import it.unical.logic_santos.design.modeling.ModelingConfig;
import it.unical.logic_santos.environment.sound.HumanSoundManager;
import it.unical.logic_santos.gameplay.IBullet;
import it.unical.logic_santos.gameplay.LifeBar;
import it.unical.logic_santos.roads_network.PathsNetwork;
import it.unical.logic_santos.roads_network.RoadNode;
import it.unical.logic_santos.roads_network.RoadsNetwork;
import it.unical.logic_santos.spatial_entity.AbstractAircraft;
import it.unical.logic_santos.spatial_entity.AbstractHuman;
import it.unical.logic_santos.spatial_entity.ISpatialEntity;
import it.unical.logic_santos.spatial_entity.Player;

/**
 * @author Agostino
 *
 */
public class HumanPhysicalActivity implements IPhysicalActivity, AnimEventListener {

	protected AbstractHuman owner=null;
	protected CharacterControl control=null;
	protected AnimChannel animationChannel=null;
	protected AnimControl animationControl=null;
	protected LifeBar lifeBar=null;
	
	protected float currentRunningScale = 1.0f;
	private float airTime=0.0f;
	protected HumanAnimation animation=createAnimation();
	
	public static final float JUMP_SPEED = 20f;
	public static final float RUNNING_SCALE = 3.0f;
	
	public HumanPhysicalActivity(final AbstractHuman owner) {
		this.owner = owner;
		this.lifeBar = new LifeBar( LifeBar.FULL_VALUE );
		
		/** init CharacterControl */
		CapsuleCollisionShape capsule = new CapsuleCollisionShape( ModelingConfig.HUMAN_CAPSULE_COLLISION_SHAPE_RADIUS, 
																	ModelingConfig.HUMAN_CAPSULE_COLLISION_SHAPE_HEIGHT );
		this.control = new CharacterControl(capsule, 0.05f);
		this.control.setJumpSpeed( JUMP_SPEED );
	}
	
	@Override
	public void initActivity() {

		getOwnerSpatialNode().addControl( this.control );
		
		PhysicsSpace.getInstance().getSpace().add( this.control );
		PhysicsSpace.getInstance().getCollisionEngine().addCollidable(this.owner.getAbstractPhysicalExtension().getBoundingVolume());
		
		initAnimation();
		
	}
	
	@Override
	public void finalizeActivity() {
		getOwnerSpatialNode().removeControl(this.control);
		PhysicsSpace.getInstance().getSpace().remove(this.control);
		//getOwnerSpatialNode().removeControl( this.control );
	}

	@Override
	public ISpatialEntity getSpatialEntityOwner() {
		return owner;
	}

	@Override
	public void setSpatialEntityOwner(ISpatialEntity spatialEntity) {
		if (spatialEntity instanceof AbstractHuman)
			this.owner = (AbstractHuman) spatialEntity;
	}

	@Override
	public void setInRoadsNetwork(RoadsNetwork roadsNetwork, RoadNode startNode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInRoadsNetwork(RoadsNetwork roadsNetwork) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStartRoadsNetworkNode(RoadNode startNode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTranslation() {
		control.setPhysicsLocation(owner.getSpatialTranslation().toVector3f());			
	}

	@Override
	public void update(float tpf) {
		
	}
	
	public void walk( Vector3f walkDirection, final Vector3f viewDirection, final float tpf ) {
		walkDirection.setY(0.0f);
		viewDirection.setY(0.0f);
		this.control.setWalkDirection(walkDirection);
		this.control.setViewDirection(viewDirection);
		
		if (walkDirection.lengthSquared()!=0)
			this.control.setViewDirection(walkDirection);
		
		/* updating ANIMATION */
		if ( !this.control.onGround() )
			airTime+=tpf;
		else
			airTime=0.0f;
		updateAnimation( walkDirection, airTime );
	}
	
	public void walk( Vector3f walkDirection, final float tpf, final boolean modifyViewDirection ) {
		walkDirection.setY(0.0f);
		this.control.setWalkDirection(walkDirection.clone().mult(this.currentRunningScale));
		if (modifyViewDirection)
			this.control.setViewDirection(walkDirection);
		
		/* updating ANIMATION */
		if ( modifyViewDirection && (!this.control.onGround()) )
			airTime+=tpf;
		else
			airTime=0.0f;
		updateAnimation( walkDirection, airTime );
	}

	
	@Override
	public void onAnimChange(AnimControl arg0, AnimChannel arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}
	
	public void onCollisionWithAbstractHuman( AbstractHuman other ) {
		
	}
	
	public void onCollisionWithAbstractAircraft( AbstractAircraft other ) {
		
	}
	
	public CharacterControl getControl() {
		return (this.control);
	}
	
	public void updateAnimation( final Vector3f walkDirection, final float airTime ) {
		if ( walkDirection.lengthSquared()==0 ) {
			if ( !animation.equals( HumanAnimation.IDLE ) )
				;//animation.setAnimation( HumanAnimation.IDLE );
		} else {
			/*if (airTime>0.3f) {
				if ( !animation.equals( HumanAnimation.IDLE ) )
					animation.setAnimation( HumanAnimation.IDLE );
			}*/
		}
		//setAnimation( animation.getAnimationName() );
	}
	
	public void refreshAnimation() {
		setAnimation( animation.getAnimationName() );
	}
	
	public AbstractHuman getAbstractHumanOwner() {
		return (this.owner);
	}
	
	protected Node getOwnerSpatialNode() {
		return ((Node) ((ModelBasedPhysicalExtension) this.owner.getAbstractPhysicalExtension()).getModelSpatial());
	}
	
	protected Spatial getOwnerSpatial() {
		return (((ModelBasedPhysicalExtension) this.owner.getAbstractPhysicalExtension()).getModelSpatial());
	}
	
	public HumanAnimation getAnimation() {
		return animation;
	}

	@Override
	public void setInPathsNetwork(PathsNetwork pathsNetwork, RoadNode startNode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInPathsNetwork(PathsNetwork pathsNetwork) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStartPathsNetworkNode(RoadNode startNode) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onShootReaction( final IBullet bullet) {
		this.lifeBar.reduce( bullet.getHumanLifeBarReduction( this.owner.getClass() ) );
		//if ( this.lifeBar.isEmpty() )
		//	this.control.jump();
	}
	
	@Override
	public void onExplosionReaction(IBullet bullet) {
		this.lifeBar.reduce( LifeBar.FULL_BAR );
		this.control.jump();
	}
	
	public LifeBar getLifeBar() {
		return lifeBar;
	}
	
	public void run() {
		this.currentRunningScale = RUNNING_SCALE;
		//HumanSoundManager.getInstance().changeToFightWalkerSoundState( owner );
	}
	
	public boolean isRunning() {
		return ( (this.control.getWalkDirection().lengthSquared()>0.0f) && (this.currentRunningScale==RUNNING_SCALE) );
	}
	
	public void stopWalking() {
		this.control.setWalkDirection( Vector3f.ZERO.clone() );
		this.owner.getHumanPhysicalActivity().walk( Vector3f.ZERO.clone(), 0.0f, false);
		currentRunningScale=1.0f;
		/* updating ANIMATION */
		if ( !this.control.onGround() )
			airTime+=0.0f;
		else
			airTime=0.0f;
		updateAnimation( this.control.getWalkDirection(), airTime );
	}
	
	public void stopWalking( final Vector3f viewDirection ) {
		this.control.setViewDirection( viewDirection );
		stopWalking();
	}
	
	protected void initAnimation() {
		/** init AnimationChannel and AnimationControl */
		if ( animationControl==null ) {
			animationControl = getOwnerSpatialNode().getControl(AnimControl.class);
			animationControl.addListener( this );
			
			animationChannel = animationControl.createChannel();
		}
	}
	
	protected AnimControl findAnimControl( final Spatial parent, final AnimControl previousAnimControl ) {
		AnimControl animControl = parent.getControl( AnimControl.class );
		if ( animControl!=null )
			return animControl;
		
		if ( parent instanceof Node ) {
			for( Spatial s: ( (Node) parent).getChildren() ) {
				AnimControl tmpAnimControl = findAnimControl( s, previousAnimControl );
				if ( tmpAnimControl!=null ) {
					if ( tmpAnimControl!=previousAnimControl )
						return tmpAnimControl;;
				}
			}
		}
		return null;
	}
	
	protected AnimControl findNextAnimControl( final Spatial parent, final List< AnimControl > previousAnimControls ) {
		AnimControl animControl = parent.getControl( AnimControl.class );
		if ( animControl!=null )
			return animControl;
		
		if ( parent instanceof Node ) {
			for( Spatial s: ( (Node) parent).getChildren() ) {
				AnimControl tmpAnimControl = findNextAnimControl( s, previousAnimControls );
				if ( tmpAnimControl!=null ) {
					if ( !previousAnimControls.contains( tmpAnimControl ) )
						return tmpAnimControl;;
				}
			}
		}
		return null;
	}
	
	protected void addLodControl( final Spatial parent ) {
		if ( parent instanceof Node ) {
			for( Spatial s: ( (Node) parent ).getChildren() )
				addLodControl( s );
		} else if ( parent instanceof Geometry ) {
			LodControl lodControl = new LodControl();
			lodControl.setDistTolerance( 1.0f );
			parent.addControl( lodControl );
		}
	}
	
	protected void setAnimation( final String animationName ) {
		if ( !animationName.equals( animationChannel.getAnimationName()) )
			animationChannel.setAnim( animationName );
	}
	
	protected HumanAnimation createAnimation() {
		return ( new HumanAnimation() );
	}
}
