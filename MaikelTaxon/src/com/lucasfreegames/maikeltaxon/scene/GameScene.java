package com.lucasfreegames.maikeltaxon.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.SAXUtils;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
import org.andengine.util.level.simple.SimpleLevelLoader;
import org.xml.sax.Attributes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.lucasfreegames.maikeltaxon.BaseScene;
import com.lucasfreegames.maikeltaxon.extras.LevelCompleteWindow;
import com.lucasfreegames.maikeltaxon.extras.LevelCompleteWindow.StarsCount;
import com.lucasfreegames.maikeltaxon.manager.SceneManager;
import com.lucasfreegames.maikeltaxon.manager.SceneManager.SceneType;
import com.lucasfreegames.maikeltaxon.object.Bomb;
import com.lucasfreegames.maikeltaxon.object.Boss;
import com.lucasfreegames.maikeltaxon.object.Boss2;
import com.lucasfreegames.maikeltaxon.object.Player;

/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public class GameScene extends BaseScene implements IOnSceneTouchListener
{
	private static final int VALOR_PESO = 8000;
	private static final int VALOR_TAX = -1500;

	private int ingresos = 0;
	
	private HUD gameHUD;
	private Text ingresosText;
	private PhysicsWorld physicsWorld;
	private LevelCompleteWindow levelCompleteWindow;
	private boolean passedAguinaldo = false;
	private boolean passedFinDeAnio = false;
	
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_MESCOMUN = "platform1";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_MESAGUINALDO = "platform2";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BILLETE_AGUINALDO = "platform3";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_MESFINDEANIO = "platform4";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BILLETE_FINDEANIO = "platform32";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BILLETE = "coin";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BOSS = "boss";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BOSS2 = "boss2";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BOMB = "bomb";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEVEL_COMPLETE = "levelComplete";
	
	private static final float ganancias = 0.35f; 
	private static final float ley3= 0.03f;
	private static final float obraSocial= 0.03f;
	private static final float jubilacion= 0.11f;
	private static final float minimoNoImponible=15000*13;
 
	private static Text brutoDelMes;
	private static Text descuentoLey3Text;
	private static Text descuentoJubilacionText;
	private static Text descuentoObraSocialText;
	private static Text descuentoGananciasText;
	private static Text netoDelMesText;
	private static int level=2; 
	private final static String[] monthNames= {"Enero", "Febrero", "Marzo", "Abril","Mayo", "Junio", "Aguinaldo", "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre", "Fin De Año"};	
	private Player player;
	private Boss boss;
	private Boss2 boss2;
	private Bomb bomb;
	
	private Text gameOverText;
	private boolean gameOverDisplayed = false;
	private static List<Body> listOfFallingPesos = new ArrayList<Body>();
	private static List<Body> listOfFallingPesos2 = new ArrayList<Body>();
	
	
	private boolean firstTouch = true;
	
	@Override
	public void createScene()
	{
		createBackground();
		createHUD();
		createPhysics();
		loadLevel(level);
		createGameOverText();
		createLevelCompleteScore();
		passedAguinaldo = false;
		passedFinDeAnio = false;
		levelCompleteWindow = new LevelCompleteWindow(vbom);
		setOnSceneTouchListener(this); 
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public void onBackKeyPressed()
	{
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene()
	{
		camera.setHUD(null);
		camera.setChaseEntity(null); //TODO
		camera.setCenter(400, 240);
		unregisterUpdateHandler(physicsWorld);
	}
	
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
	{
		if (pSceneTouchEvent.isActionDown())
		{
			if (!firstTouch)
			{
				player.startRunning();
				firstTouch = true;
			}
			else
			{
				if (player.isMooonwalking()){
					player.moonwalkTurn();
				}else if(player.isRunning()) {
					player.startJumping();
				}
			}
		}
		return false;
	}
	
	private void loadLevel(int levelID)
	{
		final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);
		
		final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.0f, 0.5f);
		//final FixtureDef FIXTURE_DEF_FALLINGPESO = PhysicsFactory.createFixtureDef(1, 0.00f, 0.0f);
		
		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL)
		{
			public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException 
			{
				final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
				final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
				
				camera.setBounds(0, 0, width, height); // here we set camera bounds
				camera.setBoundsEnabled(true);

				return GameScene.this;
			}
		});
		
		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(TAG_ENTITY)
		{
			public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException
			{
				
				final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
				final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
				final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
				
				final Sprite levelObject;
				
				if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_MESCOMUN))
				{
					levelObject = new Sprite(x, y, resourcesManager.mesComun_region, vbom);

					PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF).setUserData("platform1");
				} 
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_MESAGUINALDO))
				{
					levelObject = new Sprite(x, y, resourcesManager.aguinaldo_region, vbom);
					final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF);
					body.setUserData("platform2");
					physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
				}
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_MESFINDEANIO))
				{
					levelObject = new Sprite(x, y, resourcesManager.aguinaldo_region, vbom);
					final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF);
					body.setUserData("platform4");
					physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
				}
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BILLETE)||
						 type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BILLETE_AGUINALDO)||
						type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BILLETE_FINDEANIO))
				{
					levelObject = new Sprite(x, y, resourcesManager.coin_region, vbom)
					{
						@Override
						protected void onManagedUpdate(float pSecondsElapsed) 
						{
							super.onManagedUpdate(pSecondsElapsed);

							if (player.collidesWith(this))
							{
								addToIngresos(VALOR_PESO);
								this.setVisible(false);
								this.setIgnoreUpdate(true);
								//this.clearUpdateHandlers();
							}
						}
					};
					if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BILLETE)){
							levelObject.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(0.5f, 1, 1.3f)));
					}else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BILLETE_AGUINALDO)){
						Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.KinematicBody, FIXTURE_DEF);
						body.setUserData(levelObject);
						body.setBullet(true);
						listOfFallingPesos.add(body);
						physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
					}else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BILLETE_FINDEANIO)){
						Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.KinematicBody, FIXTURE_DEF);
						body.setUserData(levelObject);
						body.setBullet(true);
						listOfFallingPesos2.add(body);
						physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
					}
				}	
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER))
				{
					player = new Player(x, y, vbom, camera, physicsWorld)
					{
						@Override
						public void onDie()
						{
							if (!gameOverDisplayed)
							{
								displayGameOverText();
							}
						}
					};
					levelObject = player;
				}
				
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BOMB))
				{
					bomb = new Bomb(x, y, vbom, camera, physicsWorld)
					{
						@Override
						public void onDie()
						{
							bomb.setVisible(false);
							bomb.setIgnoreUpdate(true);
						}
					};
					levelObject = bomb;
				}
				
				
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BOSS))
				{
					boss = new Boss(x, y, vbom, camera, physicsWorld)
					{
						@Override
						public void onDie()
						{
							boss.setVisible(false);
							boss.setIgnoreUpdate(true);
						}
					};
					levelObject = boss;
					
				}
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BOSS2))
				{
					boss2 = new Boss2(x, y, vbom, camera, physicsWorld)
					{
						@Override
						public void onDie()
						{
							boss2.setVisible(false);
							boss2.setIgnoreUpdate(true);
						}
					};
					levelObject = boss2;
					
				}

				
				//LEVEL COMPLETE
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEVEL_COMPLETE))
				{
					levelObject = new Sprite(x, y, resourcesManager.complete_stars_region, vbom)
					{
						@Override
						protected void onManagedUpdate(float pSecondsElapsed) 
						{
							super.onManagedUpdate(pSecondsElapsed);

							if (player.collidesWith(this))
							{
								player.stop();
								levelCompleteWindow.display(StarsCount.TWO, GameScene.this, camera);
								displayLevelCompleteScore();
								this.setVisible(false);
								this.setIgnoreUpdate(true);

							}
						}
					};
					levelObject.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(1, 1, 1.3f)));
				}	
				else
				{
					throw new IllegalArgumentException();
				}

				levelObject.setCullingEnabled(true);

				return levelObject;
			}
		});
		levelLoader.loadLevelFromAsset(activity.getAssets(), "level/" + levelID + ".lvl");
		displayMonths();
	}
	
	private void createGameOverText()
	{
		gameOverText = new Text(0, 0, resourcesManager.bigFont, "Game Over!", vbom);
	}
	
	private void displayMonths(){
		for (int i=0; i< monthNames.length; i++)
		{
			if (i!= 6 && i!= 13 )
				attachChild(new Text(100+i*450,210, resourcesManager.smallFont, monthNames[i].toString(), vbom));
			else
				attachChild(new Text(100+i*450,270, resourcesManager.smallFont, monthNames[i].toString(), vbom));
		}
	}
	
	private void displayGameOverText()
	{
		camera.setChaseEntity(null);
		gameOverText.setPosition(camera.getCenterX(), camera.getCenterY());
		attachChild(gameOverText);
		gameOverDisplayed = true;
	}
	
	private void createHUD()
	{
		gameHUD = new HUD();
		
		ingresosText = new Text(20, 420, resourcesManager.bigFont, "$ 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		ingresosText.setAnchorCenter(0, 0);	
		ingresosText.setText("$ 0");
		gameHUD.attachChild(ingresosText);
		
		camera.setHUD(gameHUD);
	}
	
	private void createBackground()
	{
		setBackground(new Background(Color.BLUE));
	}
	
	private void addToIngresos(int i)
	{
		ingresos += i;
		ingresosText.setText("$ " + ingresos);
	}
	
	//Resultado final con los impuestos
	private void createLevelCompleteScore(){
		brutoDelMes= new Text(0, 50, resourcesManager.bigFont,              "Bruto:$ 0123456789", vbom);
		descuentoLey3Text= new Text(0, 100, resourcesManager.smallFont,       "- Ley 19.032:$ 0123456789", vbom);
		descuentoJubilacionText= new Text(0, 150, resourcesManager.smallFont, "- Jubilación:$ 0123456789", vbom);
		descuentoObraSocialText= new Text(0, 200, resourcesManager.smallFont, "- Obra Social:$ 0123456789", vbom); 
		descuentoGananciasText= new Text(0, 300, resourcesManager.smallFont,  "- Ganancias:$ 0123456789", vbom);
		netoDelMesText= new Text(0, 250, resourcesManager.bigFont,          "Neto:$ 0123456789", vbom);
	}
	
	private void calculateSalary(){
		int ingresoBruto = ingresos;
		brutoDelMes.setText("Bruto:$ "+ ingresoBruto);
		ingresos-=(int)ingresoBruto*ley3;
		descuentoLey3Text.setText("- Ley 19.032:$ "+ (int)ingresoBruto*ley3);
		ingresos-=(int)ingresoBruto*jubilacion;
		descuentoJubilacionText.setText("- Jubilación:$ "+ (int)ingresoBruto*jubilacion);
		ingresos-=(int)ingresoBruto*obraSocial;
		descuentoObraSocialText.setText("- Obra Social:$ "+ (int)ingresoBruto*obraSocial);
		if (ingresoBruto>minimoNoImponible){
			ingresos-=(int)((ingresoBruto-minimoNoImponible)*ganancias);
			descuentoGananciasText.setText("- Ganancias:$ "+ (int)((ingresoBruto-minimoNoImponible)*ganancias));
		}else{
			descuentoGananciasText.setText("- Ganancias:$ 0");
		}
		netoDelMesText.setText("Neto:$ "+ (int)ingresos );
	}
	
	private void displayLevelCompleteScore(){
		calculateSalary();
		camera.setChaseEntity(null);
		
		brutoDelMes.setPosition(levelCompleteWindow.getX(), levelCompleteWindow.getY()+150);
		descuentoLey3Text.setPosition(brutoDelMes.getX(), brutoDelMes.getY()-50);
		descuentoJubilacionText.setPosition(brutoDelMes.getX(), brutoDelMes.getY()-100);
		descuentoObraSocialText.setPosition(brutoDelMes.getX(), brutoDelMes.getY()-150);
		descuentoGananciasText.setPosition(brutoDelMes.getX(), brutoDelMes.getY()-200);
		netoDelMesText.setPosition(brutoDelMes.getX(), brutoDelMes.getY()-250);
		
		attachChild(brutoDelMes);
		attachChild(descuentoLey3Text);
		attachChild(descuentoJubilacionText);
		attachChild(descuentoObraSocialText);
		attachChild(descuentoGananciasText);
		attachChild(netoDelMesText);
	}
	

	
	
	private void createPhysics()
	{
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -17), false); 
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}

	private void startRainingPesos(){

		engine.registerUpdateHandler(new TimerHandler(1.0f, new ITimerCallback()
		{									
			Iterator<Body> it = listOfFallingPesos.iterator();
		    public void onTimePassed(final TimerHandler pTimerHandler)
		    {
		    	pTimerHandler.reset();
		    	try {
		    		Body myBody;
		    		if(it.hasNext()){
			    		myBody= it.next();
			    		myBody.setType(BodyType.KinematicBody);
			    		myBody.setLinearVelocity(0f, -3f);
		    		}else{
		    			engine.unregisterUpdateHandler(pTimerHandler);
		    		}
				} catch (Exception e) {
					System.out.print("Cago un next");
					engine.unregisterUpdateHandler(pTimerHandler);
					e.printStackTrace();
				}
		    }
		}));
	
	}
	
	private void startRainingPesos2(){

		engine.registerUpdateHandler(new TimerHandler(1.0f, new ITimerCallback()
		{									
			Iterator<Body> it = listOfFallingPesos2.iterator();
		    public void onTimePassed(final TimerHandler pTimerHandler)
		    {
		    	pTimerHandler.reset();
		    	try {
		    		Body myBody;
		    		if(it.hasNext()){
			    		myBody= it.next();
			    		myBody.setType(BodyType.KinematicBody);
			    		myBody.setLinearVelocity(0f, -3f);
		    		}else{
		    			engine.unregisterUpdateHandler(pTimerHandler);
		    		}
				} catch (Exception e) {
					System.out.print("Cago un next");
					engine.unregisterUpdateHandler(pTimerHandler);
					e.printStackTrace();
				}
		    }
		}));
	
	}
	
	private void startDroppingBombs(){
		engine.registerUpdateHandler(new TimerHandler(1.5f, new ITimerCallback()
		{									
		    public void onTimePassed(final TimerHandler pTimerHandler)
		    {
		    	pTimerHandler.reset();
		    	try {
		    		bomb.startFalling(boss.getX(),boss.getY());
		    		if(player.getX()>3250){
		    			engine.unregisterUpdateHandler(pTimerHandler);
		    			boss.stop();
						Debug.v("LUCAS","Desregistro el Handler porque el usuario ya se fue");
		    		}
				} catch (Exception e) {
					Debug.v("LUCAS","Desregistro el Handler porque hubo una excepcion");
					engine.unregisterUpdateHandler(pTimerHandler);
					e.printStackTrace();
				}
		    }
		}));
	}
	
	
	private void startDroppingBombs2(){
		engine.registerUpdateHandler(new TimerHandler(1.5f, new ITimerCallback()
		{									
		    public void onTimePassed(final TimerHandler pTimerHandler)
		    {
		    	pTimerHandler.reset();
		    	try {
		    		bomb.startFalling(boss2.getX(),boss2.getY());
				} catch (Exception e) {
					Debug.e("LUCAS","Desregistro el Handler de las bombas de Cris porque hubo una excepcion",e);
					engine.unregisterUpdateHandler(pTimerHandler);
					e.printStackTrace();
				}
		    }
		}));
	}

	// ---------------------------------------------
	// INTERNAL CLASSES
	// ---------------------------------------------

	private ContactListener contactListener()
	{
		ContactListener contactListener = new ContactListener()
		{
			public void beginContact(Contact contact)
			{
				Fixture x1 = contact.getFixtureA();
				Fixture x2 = contact.getFixtureB();
				
				if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
				{
					//MES CON PLAYER
					if (x1.getBody().getUserData().equals("platform1") && x2.getBody().getUserData().equals("player"))
					{
				    	player.startRunning();
					}
					if (x1.getBody().getUserData().equals("platform2") )
					{
						//PLAYER CON AGUINALDO
						if (x2.getBody().getUserData().equals("player")){
							if (!passedAguinaldo) 
							{
								passedAguinaldo=true;
						    	player.startMoonwalking();
						    	startRainingPesos();
						    	boss.startFlying();
						    	startDroppingBombs();
					    	}
				    	}
						else if (x2.getBody().getUserData().equals("bomb")){
							
							bomb.startExploding();
				    	}
					}
					if (x1.getBody().getUserData().equals("platform4") )
					{
						//PLAYER CON FIN DE AÑO
						if (x2.getBody().getUserData().equals("player")){
							if (!passedFinDeAnio) 
								{
								passedFinDeAnio=true;
								player.startMoonwalking();
					    		startRainingPesos2();
					    		boss2.startFlying();
					    		startDroppingBombs2();
								}
				    	}
						//BOMBA CON FIN DE AÑO
						else if (x2.getBody().getUserData().equals("bomb")){
							bomb.startExploding();
				    	}
					}
					
					
					//BOMBA CON PLAYER
					if (x1.getBody().getUserData().equals("bomb") && x2.getBody().getUserData().equals("player")){
						if(!bomb.isExploding()) 
						{
							try {
								addToIngresos(VALOR_TAX);
								bomb.startExploding();
								
							} catch (Exception e) {
								Debug.v("LUCAS","Se cago el moneda con player",e);
							}
						}
						
					}
					
					//BILLETE  CON PLAYER
					if (x1.getBody().getUserData() instanceof Sprite && x2.getBody().getUserData().equals("player"))
					{
						try {
							Sprite collidingPeso = (Sprite)(x1.getBody().getUserData());
							collidingPeso.setVisible(false);
							x1.getBody().setActive(false);
							addToIngresos(VALOR_PESO);		
							physicsWorld.getPhysicsConnectorManager().remove(x1.getBody());
						} catch (Exception e) {
							Debug.v("LUCAS","Se cago el moneda con player");
						}
					}
					
					
					
				}
			}

			
			public void endContact(Contact contact)
			{
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();

				if (x1.getBody().getUserData()!= null && x2.getBody().getUserData() != null)
				{
					if (x1.getBody().getUserData().equals("platform1") && x2.getBody().getUserData().equals("player"))
					{
						
					}
				

				}
			}
			
			

			public void preSolve(Contact contact, Manifold oldManifold)
			{

			}

			public void postSolve(Contact contact, ContactImpulse impulse)
			{

			}
		};
		return contactListener;
	}
}