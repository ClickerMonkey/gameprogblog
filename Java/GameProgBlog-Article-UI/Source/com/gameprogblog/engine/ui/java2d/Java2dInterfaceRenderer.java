package com.gameprogblog.engine.ui.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.ui.BaseInterfaceRenderer;
import com.gameprogblog.engine.ui.Font;
import com.gameprogblog.engine.ui.Tile;


public class Java2dInterfaceRenderer extends BaseInterfaceRenderer
{
	
	public static final int MAX_DEPTH = 64;
	public static final float DEFAULT_COMPONENT = 1.0f;
	
	public Graphics2D graphics;
	public Java2dComposite composite = new Java2dComposite();
	public Rectangle2D.Float clipping = new Rectangle2D.Float();
	public AffineTransform transform = new AffineTransform();
	
	public Java2dInterfaceRenderer( Graphics2D graphics, Color initialShade, Font font )
	{
		super( MAX_DEPTH, DEFAULT_COMPONENT, DEFAULT_COMPONENT, DEFAULT_COMPONENT, DEFAULT_COMPONENT, font );
		
		this.graphics = graphics;
		this.graphics.setComposite( composite );
	}
	
	@Override
	public void saveState()
	{
		
	}
	
	@Override
	public void restoreState()
	{
		
	}

	@Override
	public void draw( Tile tile, float l, float t, float r, float b )
	{
		Java2dImage im = (Java2dImage)tile.image;
		AffineTransform previous = graphics.getTransform();
		
		transform.setToIdentity();
		transform.translate( l + getX(), t + getY() );
		transform.scale( r - l, b - t );

		graphics.setTransform( transform );
		
		composite.set( getAlpha(), getRed(), getGreen(), getBlue() );
		
		graphics.drawImage( im.image, 0, 0, 1, 1, tile.l, tile.t, tile.r, tile.b, null );
		
		graphics.setTransform( previous );
	}

	@Override
	protected void applyClip( Bound2 clip )
	{
		clipping.x = clip.left;
		clipping.y = clip.top;
		clipping.width = clip.getWidth();
		clipping.height = clip.getHeight();
		
		graphics.setClip( clipping );
	}

}
