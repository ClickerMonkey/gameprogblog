package com.gameprogblog.engine.gfx;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

public final class Gfx
{

	public Image renderBuffer;
	public MemoryImageSource renderBufferSource;
	public Texture texture;
	public Blend blend = Blend.Alpha;

	public Gfx( int w, int h )
	{
		resize( w, h );
	}

	public void resize( int w, int h )
	{
		texture = new Texture( w, h );
		renderBufferSource = new MemoryImageSource( w, h, texture.pixels, 0, w );
		renderBufferSource.setAnimated( true );
		renderBuffer = Toolkit.getDefaultToolkit().createImage( renderBufferSource );
	}

	public void clear( int color )
	{
		texture.clear( color );
	}

	public void apply( int x, int y, int color )
	{
		final int[] pixels = texture.pixels;
		final int offset = texture.getOffset( x, y );

		pixels[offset] = blend.blend( pixels[offset], color );
	}
	
	public void applyCheck( int x, int y, int color )
	{
		if (x < 0 || x >= texture.width || y < 0 || y >= texture.height)
		{
			return;
		}
		
		apply( x, y, color );
	}

	public void apply( int offset, int color )
	{
		final int[] pixels = texture.pixels;
		
		pixels[offset] = blend.blend( pixels[offset], color );
	}
	
	public int get( int x, int y )
	{
		return texture.get( x, y );
	}
	
	public int width()
	{
		return texture.width;
	}
	
	public int height()
	{
		return texture.height;
	}
	
	public int getOffset(int x, int y)
	{
		return texture.getOffset( x, y );
	}
	
	public void flush( Graphics gr )
	{
		renderBufferSource.newPixels();

		gr.drawImage( renderBuffer, 0, 0, null );
	}

}
