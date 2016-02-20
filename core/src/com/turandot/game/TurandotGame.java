package com.turandot.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

public class TurandotGame extends ApplicationAdapter
{
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	//images of player, tiles, colors, and background
	private Texture playerImage;
	private Player player;
	
	private Texture tileImage;
	private Texture greenImage;
	private Texture yellowImage;
	private Texture backgroundImage;
	
	//sound effect vars for damage and scoring
	private Sound damage;
	private Sound score;
	private Music music;
	
	//tilemap
	Texture img;
    TiledMap tiledMap;
    TiledMapTileLayer collisionLayer;
    TiledMapRenderer tiledMapRenderer;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		//Gdx.app.log("AssetPath", Gdx.files.internal("dot.png").file().getAbsolutePath());
		// load the images for the droplet and the bucket, 64x64 pixels each
		playerImage = new Texture(Gdx.files.internal("dot.png"));
		tileImage = new Texture(Gdx.files.internal("block.png"));
		greenImage = new Texture(Gdx.files.internal("green.png"));
		yellowImage = new Texture(Gdx.files.internal("yellow.png"));
		
		// load the drop sound effect and the rain background "music"
		//damage = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

		// start the playback of the background music immediately
		music.setLooping(true);
		music.play();
		
		//setup the camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1024, 600);
		
		//setup spritebatch
		batch = new SpriteBatch();
		
		float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false,w,h);
        camera.update();
        tiledMap = new TmxMapLoader().load("map1.tmx");
        collisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        //Gdx.input.setInputProcessor(this);
        
		//setup player initial settings
		player = new Player(new Sprite(), collisionLayer);
		player.setX(64*5);
		player.setY(300 /2 - 64 / 2);
	}

	@Override
	public void render () {
		Update();
	}
	
	@Override
	public void dispose()
	{
	    playerImage.dispose();
	    tileImage.dispose();
	    greenImage.dispose();
	    yellowImage.dispose();
	    //backgroundImage.dispose();
	    //score.dispose();
	    //damage.dispose();
	    music.dispose();
	    batch.dispose();
    }
	
	//update method
	public void Update()
	{
		//draw the map
		Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
	    
        //draw the player
	    batch.setProjectionMatrix(camera.combined);
	    batch.begin();
	    batch.draw(playerImage, player.getX(), player.getY());
	    batch.end();
	    
	    //handle player movement
	    player.update();
	    camera.update();
	    
	    camera.position.set(player.getX(), player.getY(), 0);
	    camera.update();
    	batch.setProjectionMatrix(camera.combined);
	}
}