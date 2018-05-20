package com.gameprogblog.engine.gfx;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.gameprogblog.engine.TimeTracker;

public class Test
{

	public static void main(String[] args) throws Exception
	{
		final Gfx gfx = new Gfx( 480, 320 );
		final LineDraw line = new LineDraw( gfx );
		final RectDraw rect = new RectDraw( gfx );
		final TileDraw tile = new TileDraw( gfx );
		final CircleDraw circle = new CircleDraw( gfx );
		final Texture texture = TextureLoader.fromUrl( "http://icons.iconarchive.com/icons/deleket/scrap/256/Aqua-Ball-Green-icon.png" );
		final TimeTracker tracker = new TimeTracker( "Gfx FPS: %.1f", 500, TimeUnit.MILLISECONDS );
		
		final JPanel panel = new JPanel();
		panel.setPreferredSize( new Dimension( gfx.width(), gfx.height() ) );
		
		final JFrame window = new JFrame();
		window.setResizable( false );
		window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		window.setVisible( true );
		window.add( panel );
		window.pack();
		
		final Thread thread = new Thread() {
			public void run() {
				Graphics gr = null;
				tracker.reset();
				for(;;) 
				{
					if (gr == null)
					{
						gr = panel.getGraphics();
					}
					
					if (gr != null )
					{
						gfx.clear( Colors.Black );

						gfx.blend = Blend.Alpha;
						tile.draw( texture.tile(), 20, 20, Color.lighten( Colors.Red, 128 ) ); // halfway between red and white
						
						gfx.blend = Blend.Additive;
						rect.fill( 50, 50, 100, 100, Color.withAlpha( Colors.Blue, 128 ) );
						rect.fill( 125, 100, 100, 100, Color.withAlpha( Colors.Green, 128 ) );
						rect.fill( 100, 50, 100, 100, Color.withAlpha( Colors.Red, 128 ) );
						
						gfx.blend = Blend.Alpha;
						line.fast( 5, 5, 50, 100, Colors.White );
						line.smooth( 10, 5, 55, 100, Colors.White );
						rect.outline( 75, 75, 100, 100, Color.withAlpha( Colors.White, 128 ) );
						
						gfx.blend = Blend.Alpha;
						circle.fillSmooth( 400, 100, 50, Color.withAlpha( Colors.White, 128 ) );
						
						gfx.flush( gr );
					}
					
					if (tracker.update())
					{
						window.setTitle( tracker.rateString );
					}
				}
				
			}
		};
		
		thread.start();
	}
	
}
