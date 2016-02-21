package com.turandot.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class TurandotGame extends ApplicationAdapter
{
	private int gameState = -2;
	private int level = 0;
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	//images of player, tiles, colors, and background
	private Texture manta_white;
	private Texture manta_black;
	private Player player;
	
	//explosion
	private static final int frame_cols = 2;
    private static final int frame_rows = 2;
    private static float frame_duration = .1f;
    private Animation explosion;
    private Texture explosionSheet;
    private TextureRegion[] explosionFrames;
    private TextureRegion currentFrame;
    float stateTime;
	
	private Texture image;
	private Texture tileImage;
	private Texture whiteImage;
	private Texture blackImage;
	private Texture backgroundImage;
	private int background_y = 0;
	
	//sound effect vars for damage and scoring
	private Sound damage;
	private Sound score;
	private Music music;
	
	//timer
	long old_time = System.currentTimeMillis();
	long new_time = 0;
	
	//tilemap
	Texture img;
    TiledMap tiledMap;
    TiledMapTileLayer collisionLayer;
    TiledMapRenderer tiledMapRenderer;
    
	@Override
	public void create () {
		batch = new SpriteBatch();
		
		//setup explosion animation
		explosionSheet = new Texture(Gdx.files.internal("explosion.png")); // #9
        TextureRegion[][] tmp = TextureRegion.split(explosionSheet, explosionSheet.getWidth()/frame_cols, explosionSheet.getHeight()/frame_rows);              // #10
        explosionFrames = new TextureRegion[frame_cols * frame_rows];
        int index = 0;
        for (int i = 0; i < frame_rows; i++) {
            for (int j = 0; j < frame_cols; j++) {
                explosionFrames[index++] = tmp[i][j];
            }
        }
        explosion = new Animation(0.24f, explosionFrames);
        stateTime = 0f;
		
		//create the background image
		backgroundImage = new Texture(Gdx.files.internal("background_1a.png"));
		backgroundImage.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		//Gdx.app.log("AssetPath", Gdx.files.internal("dot.png").file().getAbsolutePath());
		// load the images for the droplet and the bucket, 64x64 pixels each
		manta_white = new Texture(Gdx.files.internal("manta_white.png"));
		manta_black = new Texture(Gdx.files.internal("manta_black.png"));
//		tileImage = new Texture(Gdx.files.internal("block.png"));
//		whiteImage = new Texture(Gdx.files.internal("white.png"));
//		blackImage = new Texture(Gdx.files.internal("black.png"));
		
		// load the drop sound effect and the rain background "music"
		//damage = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		music = Gdx.audio.newMusic(Gdx.files.internal("faster.mp3"));

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
        
        loadMap();
        //Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render () {
		
		if(gameState==0)
		{
			splashScreen();
		}
		if(gameState==1)
		{
			Update();
		}
		else if(gameState==2)
		{
			Victory();
		}
		else if (gameState==3)
		{
			gameOver();
		}
		else if (gameState==-1)
		{
			//countdown until game begins
			preGame();
		} else if(gameState==-2)
		{
			storyScreen();
		}
	}
	
	@Override
	public void dispose()
	{
	    manta_white.dispose();
	    manta_black.dispose();
	    music.dispose();
	    batch.dispose();
    }
	
	//update method
	public void Update()
	{
		drawGame();
	    drawPlayer();
	    movePlayer();
	    camera.update();
	}
	
	public void movePlayer()
	{
		//*******************
	    // Handle up movement + collisions
	    //*******************
    	Cell cell = collisionLayer.getCell((int) (player.getX()+32)/64, (int) (player.getY()+50)/64);
	    float oldy = player.getY();
		if(cell!=null)
    	{
			System.out.println("blocked!");
    	    boolean test = cell.getTile().getProperties().containsKey("blocked");
    	    if(test==true)
    	    {
    	    	//nothing
    	    } else
    	    {
    	    	player.setY(oldy += 500 * Gdx.graphics.getDeltaTime());
    	    	background_y = background_y + 2;
    	    }
    	    
    	    //handle running into a white tile
    	    test = cell.getTile().getProperties().containsKey("white");
    	    if(test==true)
    	    {
    	    	System.out.println("found white");
    	    	//check to see the player color
    	    	if(player.getPlayerColor().equals("white")!=true)
    	    	{
    	    		gameState = 3;
    	    	}
    	    }
    	    
    	    //handle running into a black tile
    	    test = cell.getTile().getProperties().containsKey("black");
    	    if(test==true)
    	    {
    	    	System.out.println("found black");
    	    	//check to see the player color
    	    	if(player.getPlayerColor().equals("black")!=true)
    	    	{
    	    		gameState = 3;
    	    	}
    	    }
    	    test = cell.getTile().getProperties().containsKey("finish_line");
    	    if(test==true)
    	    {
    	    	gameState = 2;
    	    	level++;
    	    	System.out.println(level);
    	    }
    	} else
    	{
    		player.setY(oldy += 500 * Gdx.graphics.getDeltaTime());
    		background_y = background_y + 2;
    	}
	}
	
	public void splashScreen()
	{
		renderImage("splash_screen.png");
	    
	    if(Gdx.input.isKeyJustPressed(Keys.SPACE)==true)
	    {
	    	//restart game
	    	old_time = System.currentTimeMillis();
	    	gameState = -1;
	    }
	}
	
	public void storyScreen()
	{
		renderImage("explosion.png");
	    
	    if(Gdx.input.isKeyJustPressed(Keys.SPACE)==true)
	    {
	    	//restart game
	    	old_time = System.currentTimeMillis();
	    	gameState = 0;
	    }
	}
	
	public void gameOver()
	{
		drawGame();
		overlayImage("deathscreen.png");
		deathAnimation();

	    if(Gdx.input.isKeyJustPressed(Keys.SPACE)==true)
	    {
	    	//restart game
	    	old_time = System.currentTimeMillis();
	    	gameState = -1;
	    }
	}
	
	public void Victory()
	{
		drawGame();
		overlayImage("victory_splash.png");
		System.out.println("Chezinu is the best");		
	    
	    if(Gdx.input.isKeyJustPressed(Keys.SPACE)==true)
	    {
	    	//restart game
	    	old_time = System.currentTimeMillis();
	    	gameState = -1;
	    }
	}
	
	public void preGame()
	{
//		//draw the map
//				Gdx.gl.glClearColor(1, 0, 0, 1);
//		        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//		        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		        camera.update();
//		        //render the background
//		        
//		        tiledMapRenderer.setView(camera);
//		        tiledMapRenderer.render();
//			    
//		        //draw the player
//			    batch.setProjectionMatrix(camera.combined);
//			    batch.begin();
//			    if(player.getPlayerColor().equals("white"))
//			    {
//			    	batch.draw(manta_white, player.getX(), player.getY());
//			    }
//			    else
//			    {
//			    	batch.draw(manta_black, player.getX(), player.getY());
//			    }
//			    batch.end();
//			    
//			    //handle player movement
//			    player.update();
//			    camera.update();
//			    
//			    camera.position.set(camera.position.x, player.getY()+200, 0);
//			    camera.update();
//		    	batch.setProjectionMatrix(camera.combined);
		    	
		resetGame();
		camera.update();
		drawGame();
		drawPlayer();
    	new_time = System.currentTimeMillis();
    	if((new_time-old_time)<750)
    	{
    		overlayImage("ready.png");
    	} else if((750 <= (new_time-old_time)) && (new_time-old_time) <= 1500)
    	{
    		overlayImage("go.png");
    	}
    	
    	if((new_time-old_time)>1500)
    	{
    		gameState = 1;
    	}
	}
	
	public void resetGame()
	{
		loadMap();
		player.setX(64*5);
		background_y = 0;
		player.setY(300 /2 - 64 / 2);
		player.setPlayerColor("black");
		camera.position.set(64*7, player.getY()+300, 0);
	}
	
	public void deathAnimation()
	{
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = explosion.getKeyFrame(stateTime, false);
        batch.begin();
        batch.draw(currentFrame, player.getX()-32, player.getY()-32);
        batch.end();
        camera.update();
	}
	
	//helper functions
	public void renderImage(String imageName)
	{
		image = new Texture(Gdx.files.internal(imageName));
		Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
		batch.begin();
	    batch.draw(image, camera.position.x-image.getWidth()/2, camera.position.y-image.getHeight()/2);
	    batch.end();
	    camera.update();
	    image.dispose();
	}
	
	public void overlayImage(String imageName)
	{
		image = new Texture(Gdx.files.internal(imageName));
		batch.begin();
	    batch.draw(image, camera.position.x-image.getWidth()/2, camera.position.y-image.getHeight()/2);
	    batch.end();
	    camera.update();
	    image.dispose();
	}
	
	public void drawGame()
	{
		camera.position.set(64*7, player.getY()+300, 0);
		//draw the map
		Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        int tileCount = 20; 
        batch.draw(backgroundImage, (64*7)-250, background_y,
             backgroundImage.getWidth() * tileCount, 
             backgroundImage.getHeight() * tileCount, 
             0, tileCount, 
             tileCount, 0);
        batch.end();
        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
	    
        
	    
	  //handle player movement
	    player.update();
	    camera.update();
	}
	
	public void loadMap()
	{
		if(level==0)
		{
			tiledMap = new TmxMapLoader().load("level_one.tmx");
		}
		else if(level==1)
		{
			tiledMap = new TmxMapLoader().load("map2.tmx");
		}
        collisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        
        //setup player initial settings
		player = new Player(new Sprite(), collisionLayer);
		player.setX(64*5);
		player.setY(300 /2 - 64 / 2);
		player.setPlayerColor("black");
		//camera.position.set(64*7, player.getY()+300, 0);
	}
	
	public void drawPlayer()
	{
		//draw the player
        batch.begin();
	    batch.setProjectionMatrix(camera.combined);
	    if(player.getPlayerColor().equals("white"))
	    {
	    	batch.draw(manta_white, player.getX(), player.getY());
	    }
	    else
	    {
	    	batch.draw(manta_black, player.getX(), player.getY());
	    }
	    batch.end();
	    camera.update();
	}
}