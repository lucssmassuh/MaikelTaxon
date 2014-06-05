package com.lucasfreegames.maikeltaxon.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.lucasfreegames.maikeltaxon.manager.ResourcesManager;

/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public class Bomb extends AnimatedSprite
{
	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------
	
	private Body body;
	private boolean falling =false;
	private boolean exploding=false;
	final long[] BOMB_EXPLODING = new long[] { 100,100,100,100};
	
	// ---------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------
	
	public boolean isExploding() {
		return exploding;
	}

	public void setExploding(boolean exploding) {
		this.exploding = exploding;
	}

	public Bomb(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld)
	{
		super(pX, pY, ResourcesManager.getInstance().bomb_region, vbo);
		createPhysics(camera, physicsWorld);
		//registerEntityModifier(new LoopEntityModifier(new ScaleModifier(.5f, 0.1f, 0.2f)));

	}
	
	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld)
	{		
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.KinematicBody, PhysicsFactory.createFixtureDef(1f, 0, 100f));
		body.setUserData("bomb");
		this.setVisible(false);
		body.isBullet();
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
		{
			@Override
	        public void onUpdate(float pSecondsElapsed)
	        {
				super.onUpdate(pSecondsElapsed);
		
	        }
		});
	}
	
	
	public void startFalling(float x, float y){
		if(isFalling()) return;
		if(isExploding()) return;
		setExploding(false);
		body.setActive(true);
		body.setTransform(new Vector2(x/32,y/32), 0);
		setVisible(true);
		setFalling(true);
		body.setType(BodyType.DynamicBody);
		//final long[] BOSS_ANIMATE = new long[] { 100,100,100,100};	
		final long[] BOMB_FALLING = new long[] { 100,300,400};
		animate(BOMB_FALLING, 0, 2,false,  new IAnimationListener() {
	        public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
	                int pInitialLoopCount) {
	        }

	        public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
	                int pRemainingLoopCount, int pInitialLoopCount) {
	        }

	        public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
	                int pOldFrameIndex, int pNewFrameIndex) {
	        }

	        public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
	           startExploding();
	        }
	    });

		}


	public void startExploding(){
		if(isExploding()) return;
		setExploding(true);
		body.setActive(false);
		//final long[] BOMB_EXPLODING = new long[] { 100,100,100,100,100,100,100,100,100,100,100,100,100,100,100};	
		animate(BOMB_EXPLODING, 3, 6, false,  new IAnimationListener() {
	        public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
	                int pInitialLoopCount) {
	        }

	        public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
	                int pRemainingLoopCount, int pInitialLoopCount) {
	        }

	        public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
	                int pOldFrameIndex, int pNewFrameIndex) {
	        }

	        public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
	            reloadBomb();
	        }
	    });

	}

	
	public void setFalling(boolean isfalling) {
		this.falling = isfalling;
	}

	public boolean isFalling() {
		return falling;
	}

	public void reloadBomb(){
		body.setActive(false);
		body.setType(BodyType.StaticBody);
		setVisible(false);
		setExploding(false);
		setFalling(false);
	}
	
	public void onDie(){
		
	}
}