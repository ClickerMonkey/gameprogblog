
package com.gameprogblog;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopInterpolated;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.core.Vector2;
import com.gameprogblog.engine.input.GameInput;
import com.gameprogblog.engine.ui.Font;
import com.gameprogblog.engine.ui.Image;
import com.gameprogblog.engine.ui.InterfaceRenderer;
import com.gameprogblog.engine.ui.Tile;
import com.gameprogblog.engine.ui.java2d.Java2dFont;
import com.gameprogblog.engine.ui.java2d.Java2dImage;
import com.gameprogblog.engine.ui.java2d.Java2dInterfaceRenderer;


public class UiGame implements Game
{

	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final boolean ANTIALIASING = true;

	public static void main( String[] args )
	{
		Game game = new UiGame();
		GameLoop loop = new GameLoopInterpolated( 3, 50, TimeUnit.MILLISECONDS, false );
		GameScreen screen = new GameScreen( WIDTH, HEIGHT, ANTIALIASING, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "GameProgBlog - Article 5 - UI" );
	}

	public boolean playing;
	public InterfaceRenderer ir;
	public Font font;
	public BufferedImage bi;
	public Image image;
	public Tile tile;
	
	
	@Override
	public void start()
	{
		font = new Java2dFont( new java.awt.Font( "Serif", java.awt.Font.PLAIN, 32 ) );
		
		bi = new BufferedImage( 128, 128, BufferedImage.TYPE_INT_ARGB_PRE );
		Graphics g = bi.getGraphics();
		g.setColor( Color.blue );
		g.fillRect( 0, 0, 64, 64 );
		g.setColor( Color.red );
		g.fillRect( 64, 0, 64, 64 );
		g.setColor( Color.green );
		g.fillRect( 0, 64, 64, 64 );
		g.setColor( Color.white );
		g.fillRect( 64, 64, 64, 64 );
		g.dispose();
		
		image = new Java2dImage( "quad", bi );
		tile = new Tile( image, 32, 32, 128, 128 );
		
		playing = true;
	}

	@Override
	public void input( GameInput input )
	{
		if (input.keyDown[KeyEvent.VK_ESCAPE])
		{
			playing = false;
		}
	}

	@Override
	public void update( GameState state )
	{
	}

	@Override
	public void draw( GameState state, final Graphics2D gr )
	{
		if (ir == null)
		{
			ir = new Java2dInterfaceRenderer( gr, Color.white, font );
		}
		
		ir.start( new Bound2( 0, 0, WIDTH, HEIGHT ) );
		
		ir.addAlpha( 0.5f );
		ir.draw( tile, 25, 25, 75, 75 );
		ir.popAlpha();

		ir.addShade( 0.7f, 0.9f, 1.0f );
		ir.draw( "Hello World\nMeow", 32, ir.getClip(), 24, new Vector2(0.5f, 0.5f), new Vector2(0.5f, 0.5f), true, false );
		ir.popShade();

		ir.end();
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
