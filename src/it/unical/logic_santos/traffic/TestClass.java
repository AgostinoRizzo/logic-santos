package it.unical.logic_santos.traffic;


import com.jme3.app.SettingsDialog;

import com.jme3.app.SimpleApplication;

import com.jme3.bounding.BoundingBox;

import com.jme3.bullet.BulletAppState;

import com.jme3.bullet.PhysicsSpace;

import com.jme3.bullet.collision.shapes.CollisionShape;

import com.jme3.bullet.control.VehicleControl;

import com.jme3.bullet.objects.VehicleWheel;

import com.jme3.bullet.util.CollisionShapeFactory;

import com.jme3.input.KeyInput;

import com.jme3.input.controls.ActionListener;

import com.jme3.input.controls.KeyTrigger;

import com.jme3.light.DirectionalLight;

import com.jme3.math.FastMath;

import com.jme3.math.Matrix3f;

import com.jme3.math.Vector3f;

import com.jme3.renderer.queue.RenderQueue.ShadowMode;

import com.jme3.scene.Geometry;

import com.jme3.scene.Node;

import com.jme3.scene.Spatial;

import com.jme3.shadow.BasicShadowRenderer;

import com.jme3.system.AppSettings;

import it.unical.logic_santos.gui.application.LogicSantosApplication;
import it.unical.logic_santos.gui.roads_network.RoadsNetworkPlatformConfig;



public class TestClass extends LogicSantosApplication implements ActionListener {



    private BulletAppState bulletAppState;

    private VehicleControl player;

    private final float accelerationForce = 1000.0f;

    private final float brakeForce = 100.0f;

    private float steeringValue = 0;

    private float accelerationValue = 0;

    private Vector3f jumpForce = new Vector3f(0, 3000, 0);

    private VehicleWheel fr, fl, br, bl;

    private Node node_fr, node_fl, node_br, node_bl;

    private float wheelRadius;
	

    private Node carNode;

    public static void main(String[] args) {

    	TestClass app = new TestClass();

        app.start();

    }



    @Override

    public void simpleInitApp() {
    	super.simpleInitApp();
        bulletAppState = new BulletAppState();

        stateManager.attach(bulletAppState);

        bulletAppState.setDebugEnabled(true);

        //PhysicsTestHelper.createPhysicsTestWorld(rootNode, assetManager, bulletAppState.getPhysicsSpace());

        setupKeys();

        buildPlayer();

    }



    private PhysicsSpace getPhysicsSpace(){

        return bulletAppState.getPhysicsSpace();

    }



    private void setupKeys() {

        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_H));

        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_K));

        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_U));

        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_J));

        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));

        inputManager.addListener(this, "Lefts");

        inputManager.addListener(this, "Rights");

        inputManager.addListener(this, "Ups");

        inputManager.addListener(this, "Downs");

        inputManager.addListener(this, "Space");

        inputManager.addListener(this, "Reset");

    }



    private void buildPlayer() {

    	 float stiffness = 120.0f;//200=f1 car

         float compValue = 0.2f; //(lower than damp!)

         float dampValue = 0.3f;

         final float mass = 400;



         //Load model and get chassis Geometry
         Spatial m = assetManager.loadModel("Models/Ferrari/Car.scene");
         carNode = (Node)m;
         //carNode.scale(5.0f);
         carNode.setShadowMode(ShadowMode.Cast);

         Geometry chasis = findGeom(carNode, "Car");
         chasis.scale(5.0f);
         BoundingBox box = (BoundingBox) chasis.getModelBound();



         //Create a hull collision shape for the chassis

         CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(chasis);



         //Create a vehicle control

         player = new VehicleControl(carHull, mass);

         carNode.addControl(player);



         //Setting default values for wheels

         player.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));

         player.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));

         player.setSuspensionStiffness(stiffness);

         player.setMaxSuspensionForce(10000);


        final float frontYOff = -1.8f;
 		final float backYOff = -2.0f;
 		final float xOff = 4.3f;
 		final float frontZOff = 7.0f;
 		final float backZOff = 8.5f;

         //Create four wheels and add them at their locations

         //note that our fancy car actually goes backwards..

         Vector3f wheelDirection = new Vector3f(0, -1, 0);

         Vector3f wheelAxle = new Vector3f(-1, 0, 0);



         /* WHEEL FRONT-RIGHT */
 		Geometry wheel_fr = findGeom(carNode, "WheelFrontRight");
 		wheel_fr.scale(5.0f); wheel_fr.center();
 		box = (BoundingBox) wheel_fr.getModelBound();
 		final float wheelRadius = box.getYExtent();
 		final float back_wheel_h = ((wheelRadius * 1.7f) - 1.0f);
 		final float front_wheel_h = ((wheelRadius * 1.9f) -1.0f);
 		this.player.addWheel(wheel_fr.getParent(), box.getCenter().add(xOff, -front_wheel_h, -frontZOff), 
 				wheelDirection, wheelAxle, 0.2f, wheelRadius, true);
 		
 		/* WHEEL FRONT-LEFT */
 		Geometry wheel_fl = findGeom(carNode, "WheelFrontLeft");
 		wheel_fl.scale(5.0f); wheel_fl.center();
 		box = (BoundingBox) wheel_fl.getModelBound();
 		this.player.addWheel(wheel_fl.getParent(), box.getCenter().add(-xOff, -front_wheel_h, -frontZOff), 
 				wheelDirection, wheelAxle, 0.2f, wheelRadius, true);
 		
 		/* WHEEL BACK-RIGHT */
 		Geometry wheel_br = findGeom(carNode, "WheelBackRight");
 		wheel_br.scale(5.0f); wheel_br.center();
 		box = (BoundingBox) wheel_br.getModelBound();
 		this.player.addWheel(wheel_br.getParent(), box.getCenter().add(xOff, -back_wheel_h, backZOff), 
 				wheelDirection, wheelAxle, 0.2f, wheelRadius, false);
 		
 		/* WHEEL BACK-LEFT */
 		Geometry wheel_bl = findGeom(carNode, "WheelBackLeft");
 		wheel_bl.scale(5.0f); wheel_bl.center();
 		box = (BoundingBox) wheel_bl.getModelBound();
 		this.player.addWheel(wheel_bl.getParent(), box.getCenter().add(-xOff, -back_wheel_h, backZOff), 
 				wheelDirection, wheelAxle, 0.2f, wheelRadius, false);



         player.getWheel(2).setFrictionSlip(4);

         player.getWheel(3).setFrictionSlip(4);



         

        //player.setPhysicsLocation(new Vector3f(0, 100.0f, 0));
        player.setPhysicsLocation(new Vector3f(RoadsNetworkPlatformConfig.POSITION_PLATFORM.getX(), 100.0f, RoadsNetworkPlatformConfig.POSITION_PLATFORM.getZ()));
        //player.accelerate(-500.0f);
        //carNode.scale(5.0f);
        it.unical.logic_santos.physics.activity.PhysicsSpace.getInstance().getSpace().add(player);
        player.accelerate(-500.0f);
        player.steer(0.5f);
        rootNode.attachChild(m);

    }



    @Override

    public void simpleUpdate(float tpf) {
        cam.lookAt(player.getPhysicsLocation(), Vector3f.UNIT_Y);

    }



    public void onAction(String binding, boolean value, float tpf) {

        if (binding.equals("Lefts")) {

            if (value) {

                steeringValue += .5f;

            } else {

                steeringValue += -.5f;

            }

            player.steer(steeringValue);

        } else if (binding.equals("Rights")) {

            if (value) {

                steeringValue += -.5f;

            } else {

                steeringValue += .5f;

            }

            player.steer(steeringValue);

        } else if (binding.equals("Ups")) {

            if (value) {

                accelerationValue -= accelerationForce;

            } else {

                accelerationValue += accelerationForce;

            }

            player.accelerate(accelerationValue);

        } else if (binding.equals("Downs")) {

            if (value) {

            	player.brake(brakeForce);

            } else {

            	player.brake(0f);

            }

        } else if (binding.equals("Space")) {

            if (value) {

            	player.applyImpulse(jumpForce, Vector3f.ZERO);

            }

        } else if (binding.equals("Reset")) {

            if (value) {

                System.out.println("Reset");

                player.setPhysicsLocation(Vector3f.ZERO);

                player.setPhysicsRotation(new Matrix3f());

                player.setLinearVelocity(Vector3f.ZERO);

                player.setAngularVelocity(Vector3f.ZERO);

                player.resetSuspension();

            } else {

            }

        }

    }
    
    private Geometry findGeom(Spatial spatial, String name) {

        if (spatial instanceof Node) {

            Node node = (Node) spatial;

            for (int i = 0; i < node.getQuantity(); i++) {

                Spatial child = node.getChild(i);

                Geometry result = findGeom(child, name);

                if (result != null) {

                    return result;

                }

            }

        } else if (spatial instanceof Geometry) {

            if (spatial.getName().startsWith(name)) {

                return (Geometry) spatial;

            }

        }

        return null;

    }

}
