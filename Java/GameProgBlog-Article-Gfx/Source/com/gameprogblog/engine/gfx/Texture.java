
package com.gameprogblog.engine.gfx;

public class Texture
{

	public int[] pixels;
	public int width;
	public int height;

	public Texture( int w, int h )
	{
		resize( w, h );
	}
	
	public Texture (int w, int h, int[] pixels)
	{
		this.width = w;
		this.height = h;
		this.pixels = pixels;
	}

	public void resize( int w, int h )
	{
		width = w;
		height = h;
		pixels = new int[w * h];
	}

	public void clear( int color )
	{
		for (int i = 0; i < pixels.length; i++)
		{
			pixels[i] = color;
		}
	}

	public int get( int x, int y )
	{
		return pixels[getOffset( x, y )];
	}

	public void set( int x, int y, int color )
	{
		pixels[getOffset( x, y )] = color;
	}

	public Tile tile( int x, int y, int w, int h )
	{
		return new Tile( this, x, y, w, h );
	}

	public Tile tile()
	{
		return new Tile( this );
	}

	public int getOffset( int x, int y )
	{
		return y * width + x;
	}

}
