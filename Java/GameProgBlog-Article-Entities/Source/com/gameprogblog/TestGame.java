
package com.gameprogblog;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.TimeUnit;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopInterpolated;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.core.EntityList;
import com.gameprogblog.engine.input.GameInput;

public class TestGame implements Game
{

	public static void main( String[] args )
	{
		Game game = new TestGame();
		GameLoop loop = new GameLoopInterpolated( 3, 20, TimeUnit.MILLISECONDS, false );
		GameScreen screen = new GameScreen( WIDTH, HEIGHT, ANTIALIASING, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "GameProgBlog - Article 2 - Entities" );
	}
	
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final boolean ANTIALIASING = true;

	public static final Color[] COLORS = {
		Color.red, Color.orange, Color.yellow, Color.blue, Color.gray, 
		Color.green, Color.pink, Color.white, Color.cyan, Color.magenta,
		Color.darkGray, Color.gray
	};

	public boolean playing;
	public EntityList<TestEntity> entities;
	public Rectangle2D.Float boundary;
	
	@Override
	public void start()
	{
		boundary = new Rectangle2D.Float( 0, 0, WIDTH, HEIGHT );
		entities = new EntityList<TestEntity>();
		playing = true;
	}

	@Override
	public void input( GameInput input )
	{
		if (input.keyDown[KeyEvent.VK_ESCAPE])
		{
			playing = false;
		}
		
		if (input.mouseUpCount > 0)
		{
			for ( int i = 0; i < COLORS.length; i++ )
			{
				entities.add( new TestEntity( COLORS[i], boundary, input.mouseX, input.mouseY ) );
			}
		}
	}

	@Override
	public void update( GameState state )
	{
		entities.update( state );
	}

	@Override
	public void draw( GameState state, Graphics2D gr )
	{
		entities.draw( state, gr );
	}

	@Override
	public void destroy()
	{
		entities.onExpire();
	}

	@Override
	public boolean isPlaying()
	{
		return playing;
	}
	
}
