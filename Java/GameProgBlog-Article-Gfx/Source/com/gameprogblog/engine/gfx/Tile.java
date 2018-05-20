
package com.gameprogblog.engine.gfx;

public class Tile
{

	public Texture texture;
	public int l, t, r, b;

	public Tile()
	{
	}

	public Tile( Texture texture )
	{
		set( texture, 0, 0, texture.width, texture.height );
	}

	public Tile( Texture texture, int x, int y, int w, int h )
	{
		set( texture, x, y, x + w, y + h );
	}

	public void set( Texture texture, int l, int t, int r, int b )
	{
		this.texture = texture;
		this.l = l;
		this.t = t;
		this.r = r;
		this.b = b;
	}

	public int width()
	{
		return (r - l);
	}

	public int height()
	{
		return (b - t);
	}
}
