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
public class Boss extends AnimatedSprite
{
	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------
	
	private Body body;
	
	private float normalVelocityX=3.5f;
	
	private boolean flying =false;
	
	private boolean flyingDirection =true;
	
	
	// ---------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------
	

	public Boss(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld)
	{
		super(pX, pY, ResourcesManager.getInstance().boss_region, vbo);
		createPhysics(camera, physicsWorld);
		
	}
	
	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld)
	{		
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		body.setUserData("boss");
		body.setFixedRotation(true);
		this.setVisible(false);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
		{
			@Override
	        public void onUpdate(float pSecondsElapsed)
	        {
				super.onUpdate(pSecondsElapsed);
				//camera.onUpdate(0.1f);
				if (getX() >= 3000 && isDirectionRight())
				{					
					flyingTurn();
				}
				if (getX() <= 2600 && !isDirectionRight())
				{					
					flyingTurn();
				}
		
	        }
		});
	}
		
	

	public void startFlying(){
		if(isFlying()) return;
		setVisible(true);
		setFlying(true);
		body.setType(BodyType.KinematicBody);
		final long[] BOSS_ANIMATE = new long[] { 100,100,100,100};	
		animate(BOSS_ANIMATE, 0, 3, true);
		body.setLinearVelocity(new Vector2(normalVelocityX, 0));
	}

	public void setFlying(boolean flying) {
		this.flying = flying;
	}

	public boolean isFlying() {
		return flying;
	}



	public void flyingTurn(){
		if (!isFlying()) return;
		changeDirection();
		body.setLinearVelocity(0f, 0f);
		if(isDirectionRight()){
			body.setLinearVelocity(normalVelocityX, 0f);
		}else{
			body.setLinearVelocity(-1*normalVelocityX, 0f);	
		}	
	}
	
	public void changeDirection(){
		flyingDirection= (!(flyingDirection));
		setFlipped(!isDirectionRight(), false);
	}
	
	public boolean isDirectionRight(){
		return flyingDirection;
	}
	
	public void stop(){
		body.setLinearVelocity(0f, 0f);
		body.setType(BodyType.StaticBody);
		setVisible(false);
	}
	
	public void onDie(){
		
	}
}