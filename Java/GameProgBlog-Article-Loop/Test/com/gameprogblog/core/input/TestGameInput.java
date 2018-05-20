package com.gameprogblog.core.input;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

import com.gameprogblog.core.Game;
import com.gameprogblog.core.GameLoopFixed;
import com.gameprogblog.core.GameScreen;
import com.gameprogblog.core.GameState;
import com.gameprogblog.core.input.GameInput;
import com.gameprogblog.core.input.GameKeyEvent;
import com.gameprogblog.core.input.GameMouseEvent;


public class TestGameInput implements Game
{
	
	public static void main( String[] args )
	{
		Game game = new TestGameInput();
		
		GameLoopFixed loop = new GameLoopFixed( 3, 20, 10, TimeUnit.MILLISECONDS );
		
		GameScreen screen = new GameScreen( 640, 480, true, loop, game );
		screen.setBackground( Color.black );

		GameScreen.showWindow( screen, "GameProgBlog - Test GameInput" );
	}

	public boolean playing;
	public boolean moving;
	public boolean dragging;
	public boolean inside;
	
	@Override
	public void start()
	{
		playing = true;
	}

	@Override
	public void input( GameInput input )
	{
		GameKeyEvent key = null;
		
		while ( ( key = input.keyEvents.poll() ) != null )
		{
			System.out.format( "Key %s (%d) type %s\n", key.e.getKeyChar(), key.e.getKeyCode(), key.type );
			
			if ( key.e.getKeyCode() == KeyEvent.VK_ESCAPE )
			{
				playing = false;
			}
		}
		
		GameMouseEvent mouse = null;
		
		while ( ( mouse = input.mouseEvents.poll() ) != null )
		{
			System.out.format( "Mouse %d at {%d, %d} type %s\n", mouse.e.getButton(), mouse.e.getX(), mouse.e.getY(), mouse.type );
		}
		
		moving = input.mouseMoving;
		dragging = input.mouseDragging;
		inside = input.mouseInside;
	}

	@Override
	public void update( GameState state )
	{

	}

	@Override
	public void draw( GameState state, Graphics2D gr )
	{
		gr.setColor( Color.white );
		gr.drawString( "Hello World! Testing GameInput", 10, 20 );
		
		gr.drawString( "Moving: " + moving, 10, 36 );
		gr.drawString( "Dragging: " + dragging, 10, 52 );
		gr.drawString( "Inside: " + inside, 10, 68 );
	}

	@Override
	public void destroy()
	{

	}

	@Override
	public boolean isPlaying()
	{
		return playing;
	}

}
