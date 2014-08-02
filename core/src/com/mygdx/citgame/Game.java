package com.mygdx.citgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.citgame.entity.*;


public class Game extends ApplicationAdapter {
	
	public static final RandomXS128 RANDOM = new RandomXS128();
	
	SpriteBatch batch;
	Texture img;
	
	BitmapFont font;
	
	OrthographicCamera camera;
	TiledMap map;
	TiledMapRenderer mapRenderer;
	
	private final int SHEET_ROWS = 1;
	private final int SHEET_COLS = 7;
	
	Texture playerSheet;
	TextureRegion[] walkFrames;
	TextureRegion currentFrame;
	Animation walkAnimation;
	
	MapLayer mapLayers;
	
	MapLayer collisionLayer;
	MapObjects collisionsObjects;
	
	MapLayer enemyEntitiesLayer;
	MapObjects enemiesObjects;
	
	MapLayer playerSpawnLayer;
	Vector2 playerSpawn;
	
	MapLayer doorEntitiesLayer;
	MapObjects doorObjects;
	
	MapLayer containerEntitiesLayer;
	MapObjects containerObjects;
	
	MapLayer transitionLayer;
	MapObjects transitionObjects;
	Array<Rectangle> transitionBounds;
	
	static Array<Entity> entities;
	
	Player player;
	boolean right = false, left = false, up = false, down = false;
	
	float delta;
	
	public static float TIME = 0;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture(Gdx.files.internal("badlogic.jpg"));
		
		font = new BitmapFont();
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w, h);
		camera.update();
	
		
		map = new TmxMapLoader().load("CIT.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map);
		
		collisionLayer = map.getLayers().get("collision");
		collisionsObjects = collisionLayer.getObjects();  
		
		enemyEntitiesLayer = map.getLayers().get("enemies");
		enemiesObjects = enemyEntitiesLayer.getObjects();  
		
		playerSpawnLayer = map.getLayers().get("player");
		Rectangle r = ((RectangleMapObject) (playerSpawnLayer.getObjects().get(0))).getRectangle();
		playerSpawn = new Vector2(r.x + r.width/2, r.y + r.height/2);
		
		doorEntitiesLayer = map.getLayers().get("doors");
		doorObjects = doorEntitiesLayer.getObjects();
		
		containerEntitiesLayer = map.getLayers().get("containers");
		containerObjects = containerEntitiesLayer.getObjects();
		
		transitionLayer = map.getLayers().get("transitions");
		transitionObjects = transitionLayer.getObjects();  
		transitionBounds = new Array<Rectangle>();
		
		for (MapObject o: transitionObjects) {
    		transitionBounds.add(((RectangleMapObject) o).getRectangle());
    	}
		
		
		entities = new Array<Entity>();
		
		for (MapObject o: enemiesObjects) {
			RectangleMapObject e = (RectangleMapObject) o;
			Enemy en = new Enemy(collisionsObjects, e.getRectangle().x, e.getRectangle().y);
			entities.add(en);
		}
		
		for (MapObject o: doorObjects) {
			RectangleMapObject e = (RectangleMapObject) o;
			Door d = new Door(collisionsObjects, e.getRectangle().x, e.getRectangle().y);
			entities.add(d);
		}
		
		for (MapObject o: containerObjects) {
			RectangleMapObject e = (RectangleMapObject) o;
			DestructableContainer c = new DestructableContainer(collisionsObjects, e.getRectangle().x, e.getRectangle().y);
			String containedItem = (String) o.getProperties().get("contains");
			if (containedItem != null) {
				if (containedItem.equals("key")) c.type = DestructableContainer.Type.KEY;
			}
			entities.add(c);
		}
		
		player = new Player(collisionsObjects, (int) playerSpawn.x, (int) playerSpawn.y);
		
		entities.add(player);
		
		//Animations
		playerSheet = new Texture(Gdx.files.internal("spritesheet.png"));
		walkFrames = new TextureRegion[SHEET_ROWS * SHEET_COLS];
		TextureRegion[][] tmp = TextureRegion.split(playerSheet, playerSheet.getWidth()/SHEET_COLS, playerSheet.getHeight()/SHEET_ROWS);
		int index = 0;
		for (int i = 0; i < SHEET_ROWS; i++) {
			for (int j = 0; j < SHEET_COLS; j++) {
				walkFrames[index++] = tmp[i][j];
			}
		}
		
		walkAnimation = new Animation(0.125f, walkFrames);
		
	}
	
	public static void addEntity(Entity e) {
		entities.add(e);
	}
	
	private void loadMap() {
		
	}
	
	private boolean cameraCanMove(float x, float y) {
		MapProperties prop = map.getProperties();
		int mapWidth = prop.get("width", Integer.class);
		int mapHeight = prop.get("height", Integer.class);
		int tilePixelWidth = prop.get("tilewidth", Integer.class);
		int tilePixelHeight = prop.get("tileheight", Integer.class);

		int mapPixelWidth = mapWidth * tilePixelWidth;
		int mapPixelHeight = mapHeight * tilePixelHeight;
		
		int viewportWidth = (int) camera.viewportWidth/2;
		int viewportHeight = (int) camera.viewportHeight/2;
		
		if (x + viewportWidth > mapPixelWidth) return false;
		if (x - viewportWidth < 0) return false;
		
		if (y + viewportHeight > mapPixelHeight) return false;
		if (y - viewportHeight < 0) return false;
		
		
		return true;
		
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(.37f, .6f, .09f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		delta = Gdx.graphics.getDeltaTime();
		
		TIME += delta;
		
		camera.update();
		
		int i = 0;
		while(i < entities.size) {
			Entity e = entities.get(i);
			e.update(delta, entities);
			if (!(e instanceof Player)) if (e.removed) {
				e.remove();
				entities.removeValue(e, false);
			}
			e = null;
			i++;
		}
		
		if (!player.hasTask) {
			Rectangle r = transitionBounds.get(0);
			this.addEntity(new Liftable(collisionsObjects, r.x + r.width/2, r.y + r.height/2));
			player.hasTask = true;
		}
		
		if (Entity.checkRectangleVsRectangleList(player.bounds, transitionBounds) != null) {
			System.out.println("transition");
		}
		//System.out.println(Gdx.graphics.getFramesPerSecond());
		
		//camera.position.set( (int) player.position.x, (int) player.position.y, 0);
		if (cameraCanMove(player.position.x, camera.position.y)) camera.position.x = (int) player.position.x;
		if (cameraCanMove(camera.position.x, player.position.y)) camera.position.y = (int) player.position.y;
		//camera.position.x = (int) player.position.x;
		//camera.position.y = (int) player.position.y;
		
		if (player.attackTime == 0) {
			float speed = 600;
			speed = 2500;
			if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) player.addForce(speed, 0);
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) player.addForce(-speed, 0);
			if (Gdx.input.isKeyPressed(Input.Keys.UP)) player.addForce(0, speed);
			if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) player.addForce(0, -speed);
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) player.meleetAttack(entities);
		
		mapRenderer.setView(camera);
		mapRenderer.render();
		
		batch.setProjectionMatrix(camera.projection);
		batch.setTransformMatrix(camera.view);
		
		currentFrame = walkAnimation.getKeyFrame(TIME, true);
		
		batch.begin();
		
		for (Entity e: entities) {
			
			batch.setColor(Color.WHITE);
			
			if (e instanceof HealthEntity) {
				float hurtTime = ((HealthEntity) e).hurtTime;
				if (hurtTime > 0) batch.setColor(Color.RED);
			}
			
			batch.draw(currentFrame, e.position.x - currentFrame.getRegionHeight()/2, e.position.y - currentFrame.getRegionWidth()/2, e.size * 2, e.size * 2);
			
			if (e instanceof Door) font.draw(batch, "Door",  e.position.x, e.position.y);
			if (e instanceof Enemy) font.draw(batch, "Enemy",  e.position.x, e.position.y);
			if (e instanceof Liftable) font.draw(batch, "Liftable",  e.position.x, e.position.y);
			if (e instanceof Item) {
				if (((Item) e).type == Item.Type.KEY) font.draw(batch, "Key",  e.position.x, e.position.y);
				if (((Item) e).type == Item.Type.HEART) font.draw(batch, "Heart",  e.position.x, e.position.y);
			}
			
			if (player.removed) font.draw(batch, "DEAD", player.position.x, player.position.y);
		}
		
		batch.end();
		
	}
	
}
