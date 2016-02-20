package com.turandot.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class TurandotGame extends ApplicationAdapter
{
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	//images of player, tiles, colors, and background
	private Texture playerImage;
	private Rectangle player;
	
	private Texture tileImage;
	private Texture greenImage;
	private Texture yellowImage;
	private Texture backgroundImage;
	
	//sound effect vars for damage and scoring
	private Sound damage;
	private Sound score;
	private Music music;
	
	//screensize and block size
	private int block_size;
	private int screen_size;
	
	//define the level
	private short level_data[][] =
	{
	        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
	        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
	        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
	};
	
	private Rectangle blocks[][] = new Rectangle[64][64];
	
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
		
		//setup player initial settings
		player = new Rectangle();
		player.x = 20;
		player.y = 300 /2 - 64 / 2;
		player.width = 64;
		player.height = 64;
		   
		//create the maze
		createMaze();
		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    camera.update();
	    
	    batch.setProjectionMatrix(camera.combined);
	    batch.begin();
	    batch.draw(playerImage, player.x, player.y);
	    drawMaze();
	    batch.end();
	    
	    if(Gdx.input.isKeyPressed(Keys.LEFT))
    	{
	    	player.x -= 200 * Gdx.graphics.getDeltaTime();
	    	// camera.position.x += 5;
	    	camera.update();
	    	batch.setProjectionMatrix(camera.combined);
    	}
	    if(Gdx.input.isKeyPressed(Keys.RIGHT)) player.x += 200 * Gdx.graphics.getDeltaTime();
	    if(Gdx.input.isKeyPressed(Keys.UP)) player.y += 200 * Gdx.graphics.getDeltaTime();
	    if(Gdx.input.isKeyPressed(Keys.DOWN)) player.y -= 200 * Gdx.graphics.getDeltaTime();
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
	
	private void createMaze()
	{
        int i = 0;
        int j = 0;

        for (i = 0; i < 8; i ++) {
            for (j = 0; j < 3; j ++)
            {
            	blocks[i][j] = new Rectangle();
            	blocks[i][j].x = i*64;
            	blocks[i][j].y = j*64;
            	blocks[i][j].width = 64;
            	blocks[i][j].height = 64;
            }
        }
    }
	
	private void drawMaze()
	{

        int i = 0;
        int j = 0;

        for (i = 0; i < 8; i ++) {
            for (j = 0; j < 3; j ++) {
            	batch.draw(tileImage, blocks[i][j].x, blocks[i][j].y);
            }
        }
    }
}
