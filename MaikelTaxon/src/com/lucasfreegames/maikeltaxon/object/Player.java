package com.lucasfreegames.maikeltaxon.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.lucasfreegames.maikeltaxon.manager.ResourcesManager;

/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public abstract class Player extends AnimatedSprite
{
	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------
	
	private Body body;
	
	private int footContacts = 0;
	
	private float normalVelocityX=5f;
	
	private boolean mooonwalking =false;
	private boolean running =false;
	private boolean jumping =false;
	private Sprite currentlyCollidingSprite=null;
	
	
	public Sprite getCurrentlyCollidingSprite() {
		return currentlyCollidingSprite;
	}

	public void setCurrentlyCollidingSprite(Sprite currentlyCollidingSprite) {
		this.currentlyCollidingSprite = currentlyCollidingSprite;
	}

	private boolean moonwalkDirection =true;
	
	// ---------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------
	
	public Player(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld)
	{
		super(pX, pY, ResourcesManager.getInstance().player_region, vbo);
		createPhysics(camera, physicsWorld);
		camera.setChaseEntity(this);
	}
	
	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld)
	{		
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));

		body.setUserData("player");
		body.setFixedRotation(true);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
		{
			@Override
	        public void onUpdate(float pSecondsElapsed)
	        {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				
				if (getY() <= 0)
				{					
					onDie();
				}
				
	        }
		});
	}
	
	public void startRunning()
	{
		if(isRunning()) return;
		
		setJumping(false);
		setRunning(true);
		setMooonwalking(false);
		
		final long[] PLAYER_ANIMATE = new long[] { 100, 100, 100,100,100,100,100 };
		animate(PLAYER_ANIMATE, 8, 14, true);
		body.setLinearVelocity(new Vector2(normalVelocityX, body.getLinearVelocity().y));
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	public boolean isRunning() {
		return running;
	}

	
	public void startMoonwalking(){
		
		if(isMooonwalking()) return;
		
		setJumping(false);
		setRunning(false);
		setMooonwalking(true);
		
		body.setType(BodyType.DynamicBody);
		final long[] PLAYER_ANIMATE = new long[] { 100,100,100,100,100,100,100,100};	
		animate(PLAYER_ANIMATE, 0, 7, true);
		body.setLinearVelocity(new Vector2(normalVelocityX, body.getLinearVelocity().y));
	}

	public void setMooonwalking(boolean mooonwalking) {
		this.mooonwalking = mooonwalking;
	}

	public boolean isMooonwalking() {
		return mooonwalking;
	}


	public void startJumping()
	{
		if(isJumping()) return;
		
		setJumping(true);
		setRunning(false);
		setMooonwalking(false);
		
		body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, 12));
		final long[] PLAYER_ANIMATE = new long[] { 150, 1000,1000 };	
		animate(PLAYER_ANIMATE, 16, 18, true);

	}
	
	public boolean isJumping() {
		return jumping;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public void increaseFootContacts()
	{
		footContacts++;
	}
	
	public void decreaseFootContacts()
	{
		footContacts--;
	}
	
	
	public void moonwalkTurn(){
		if (!isMooonwalking()) return;
		changeDirection();
		body.setLinearVelocity(0f, 0f);
		if(isDirectionRight()){
			body.setLinearVelocity(normalVelocityX, 0f);
		}else{
			body.setLinearVelocity(-1*normalVelocityX, 0f);	
		}	
	}
	
	public void changeDirection(){
		moonwalkDirection= (!(moonwalkDirection));
		setFlipped(!isDirectionRight(), false);
	}
	
	public boolean isDirectionRight(){
		return moonwalkDirection;
	}
	
	public void stop(){
		body.setLinearVelocity(0f, 0f);
		body.setType(BodyType.StaticBody);
		final long[] PLAYER_ANIMATE = new long[] { 100,100,100,100,100,100,100,100};	
		animate(PLAYER_ANIMATE, 0, 7, true);

	}
	
	public abstract void onDie();
}