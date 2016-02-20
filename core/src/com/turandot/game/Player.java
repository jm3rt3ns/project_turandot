package com.turandot.game;

import javax.swing.plaf.synth.SynthSeparatorUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public class Player extends Sprite {
	
	private float oldx = 0;
	private float oldy = 0;
	private String player_color = "black";
	
	private TiledMapTileLayer collisionLayer;
	
	public Player(Sprite sprite, TiledMapTileLayer collisionLayer)
	{
		super(sprite);
		this.collisionLayer = collisionLayer;
	}
	public void update()
	{
	    oldx = getX();
	    oldy = getY();
	    String name = collisionLayer.getName();
	    Cell cell = collisionLayer.getCell(4, 0);
	    boolean test = cell.getTile().getProperties().containsKey("blocked");
	    
	    //****************
	    // Handle L/R Movement
	    //****************
	    if(Gdx.input.isKeyJustPressed(Keys.LEFT) && directionClear("left"))
    	{
	    	//setX(oldx -= 500 * Gdx.graphics.getDeltaTime());
	    	setX(oldx-64);
	    	//collisionLayer.getCell((int) getX(), (int) getY()).getTile().getProperties().containsKey("blocked");
    	}
	    if(Gdx.input.isKeyJustPressed(Keys.RIGHT) && directionClear("right"))
    	{
	    	//setX(oldx += 500 * Gdx.graphics.getDeltaTime());
	    	setX((oldx+64));
    	}
		
		//******************
		// Handle toggling manta between black and white
		//******************
		
		if(Gdx.input.isKeyJustPressed(Keys.SPACE))
		{
			if(player_color.equals("white"))
				player_color = "black";
			else
				player_color = "white";
		}
	}
	
	private boolean directionClear(String direction)
	{
		
		Cell cell = new Cell();
	    //boolean test = cell.getTile().getProperties().containsKey("blocked");
		
		boolean clear = false;
		System.out.println("X: " + (int) getX()/64 + " Y: " + (int) getY()/64);
		
		switch (direction)
		{
        case "left":
        	cell = collisionLayer.getCell((int) (getX()-32)/64, (int) (getY()+32)/64);
    		if(cell!=null)
        	{
        	    boolean test = cell.getTile().getProperties().containsKey("blocked");
        	    System.out.println(test);
        	    if(test==true)
        	    {
        	    	clear = false;
        	    } else
        	    {
        	    	clear = true;
        	    }
        	} else
        	{
        		clear = true;
        	}
        	break;
        case "right":
        	cell = collisionLayer.getCell((int) (getX()+64)/64, (int) (getY()+32)/64);
    		if(cell!=null)
        	{
        	    boolean test = cell.getTile().getProperties().containsKey("blocked");
        	    System.out.println(test);
        	    if(test==true)
        	    {
        	    	clear = false;
        	    } else
        	    {
        	    	clear = true;
        	    }
        	} else
        	{
        		clear = true;
        	}
        	break;
        default:
        	clear = true;
        	break;
		}
		return clear;
	}
	
	public String getPlayerColor()
	{
		return player_color;
	}
	
	public void setPlayerColor(String player_color)
	{
		this.player_color = player_color;
	}
	
	private void resetPlayer()
	{
		//signal game to restart
		setX(64*5);
		setY(300 /2 - 64 / 2);
	}
}
