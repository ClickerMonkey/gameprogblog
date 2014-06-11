
package com.gameprogblog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.util.concurrent.TimeUnit;

import com.gameprogblog.core.Game;
import com.gameprogblog.core.GameLoop;
import com.gameprogblog.core.GameLoopFixed;
import com.gameprogblog.core.GameLoopInterpolated;
import com.gameprogblog.core.GameLoopVariable;
import com.gameprogblog.core.GameScreen;
import com.gameprogblog.core.GameState;
import com.gameprogblog.core.input.GameInput;


public class TestGame implements Game
{

	public static void main( String[] args )
	{
		TestGame game = new TestGame();

		GameScreen screen = new GameScreen( WIDTH, HEIGHT, ANTIALIASING, LOOPS[0], game );
		screen.setBackground( Color.black );

		game.setScreen( screen );

		GameScreen.showWindow( screen, "GameProgBlog - Article 1 - Game Loop" );
	}
	
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final boolean ANTIALIASING = true;

	public static final Color COLOR_SELECTED = Color.white;
	public static final Color COLOR_UNSELECTED = Color.darkGray;
	
	public static final Font FONT = new Font( "Courier New", Font.PLAIN, 11 );
	
	public static final int METHOD_NORMAL = 0;
	public static final int METHOD_FORWARD_EXTRAPOLATION = 1;
	public static final int METHOD_BACKWARD_EXTRAPOLATION = 2;
	public static final int METHOD_INTERPOLATION = 3;
	public static final int METHOD_NEXT = KeyEvent.VK_Q;
	public static final int METHOD_PREV = KeyEvent.VK_A;
	public static final String[] METHOD_NAME = {
		"Non-interpolated",
		"Forward extrapolation",
		"Backward extrapolation",
		"Interpolated"
	};


	public static final int LOOP_NEXT = KeyEvent.VK_W;
	public static final int LOOP_PREV = KeyEvent.VK_S;
	public static final String[] LOOP_NAME = {
		"Variable", 
		"Fixed at 40 updates-per-second and 100 draws-per-second",
		"Fixed at 40 updates-per-second and 100 draws-per-second with sleep",
		"Interpolated at 20 updates-per-second",
		"Interpolated at 20 updates-per-second with yield",
	};
	
	public static final GameLoop[] LOOPS = {
		new GameLoopVariable( 100, TimeUnit.MILLISECONDS ),
		new GameLoopFixed( 3, 25, 10, TimeUnit.MILLISECONDS, false ),
		new GameLoopFixed( 3, 25, 10, TimeUnit.MILLISECONDS, true ),
		new GameLoopInterpolated( 3, 50, TimeUnit.MILLISECONDS, false ),
		new GameLoopInterpolated( 3, 50, TimeUnit.MILLISECONDS, true )
	};

	public int method;
	public int loop;
	
	public float x, y, velocityX, velocityY, radius, lastX, lastY;
	public boolean playing;
	public GameScreen screen;

	public void setScreen( GameScreen screen )
	{
		this.screen = screen;
	}
	
	@Override
	public void start()
	{
		float angle = random( 0.0f, (float)Math.PI * 2.0f );
		float speed = random( 100, 200 );
		
		radius = random( 10, 20 );
		x = random( radius, WIDTH - radius );
		y = random( radius, HEIGHT - radius );
		velocityX = (float)Math.cos( angle ) * speed;
		velocityY = (float)Math.sin( angle ) * speed;
		playing = true;
	}

	@Override
	public void input( GameInput input )
	{
		if (input.keyUp[METHOD_NEXT]) 
		{
			method = inc( method, METHOD_NAME.length, -1 );
		}
		if (input.keyUp[METHOD_PREV])
		{
			method = inc( method, METHOD_NAME.length, +1 );
		}
		if (input.keyUp[LOOP_NEXT])
		{
			loop = inc( loop, LOOPS.length, -1 );
			
			screen.setLoop( LOOPS[loop] );
		}
		if (input.keyUp[LOOP_PREV])
		{
			loop = inc( loop, LOOPS.length, +1 );
			
			screen.setLoop( LOOPS[loop] );
		}
		if (input.keyDown[KeyEvent.VK_ESCAPE])
		{
			playing = false;
		}
	}

	@Override
	public void update( GameState state )
	{
		lastX = x;
		lastY = y;
		x += velocityX * state.seconds;
		y += velocityY * state.seconds;

		handleCollision();
	}

	@Override
	public void draw( GameState state, Graphics2D gr )
	{
		gr.setFont( FONT );
		gr.setColor( Color.white );
		gr.drawString( state.updateTracker.rateString, 10, 20 );
		gr.drawString( state.drawTracker.rateString, 10, 36 );

		int line = 68;
		
		for ( int i = 0; i < METHOD_NAME.length; i++ )
		{
			gr.setColor( i == method ? COLOR_SELECTED : COLOR_UNSELECTED );
			gr.drawString( getSuffix( i, method, METHOD_NAME.length, "[Q] ", "[A] ", "    ") + METHOD_NAME[i], 10, line );
			
			line += 16;
		}
		
		line += 16;
		
		for ( int i = 0; i < LOOPS.length; i++ )
		{
			gr.setColor( i == loop ? COLOR_SELECTED : COLOR_UNSELECTED );
			gr.drawString( getSuffix( i, loop, LOOP_NAME.length, "[W] ", "[S] ", "    ") + LOOP_NAME[i], 10, line );
			
			line += 16;
		}
		
		float ax = 0;
		float ay = 0;

		switch (method)
		{
		case METHOD_NORMAL:
			ax = x;
			ay = y;
			break;
		
		case METHOD_FORWARD_EXTRAPOLATION:
			ax = x + state.forward * velocityX;
			ay = y + state.forward * velocityY;
			break;

		case METHOD_BACKWARD_EXTRAPOLATION:
			ax = x + state.backward * velocityX;
			ay = y + state.backward * velocityY;
			break;
			
		case METHOD_INTERPOLATION:
			ax = (x - lastX) * state.interpolate + lastX;
			ay = (y - lastY) * state.interpolate + lastY;
			break;
		}
		
		gr.setColor( Color.red );
		gr.draw( new Ellipse2D.Float( ax - radius, ay - radius, radius * 2, radius * 2 ) );
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
	
	private float random( float min, float max )
	{
		return (float)((max - min) * Math.random() + min);
	}
	
	private void handleCollision()
	{
		if (x < radius)
		{
			velocityX = -velocityX;
			x = radius;
		}
		else if (x > WIDTH - radius)
		{
			velocityX = -velocityX;
			x = WIDTH - radius;
		}

		if (y < radius)
		{
			velocityY = -velocityY;
			y = radius;
		}
		else if (y > HEIGHT - radius)
		{
			velocityY = -velocityY;
			y = HEIGHT - radius;
		}
	}

	private int inc(int i, int max, int dir)
	{
		return (i + dir + max) % max;
	}
	
	private String getSuffix(int i, int selected, int max, String prev, String next, String other)
	{
		if ( inc(i, max, 1) == selected )
		{
			return prev;
		}
		
		if ( inc(i, max, -1) == selected )
		{
			return next;
		}
		
		return other;
	}
	
}
