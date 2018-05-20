package com.gameprogblog.engine.ui;


public class Tile
{
	public Image image;
	public int l, t, r, b;
	
	public Tile()
	{
	}
	
	public Tile(Image image)
	{
		set( image );
	}
	
	public Tile(Image image, int l, int t, int r, int b)
	{
		set( image, l, t, r, b );
	}
	
	public void set(Image image)
	{
		set( image, 0, 0, image.getWidth(), image.getHeight() );
	}
	
	public void set(Image image, int l, int t, int r, int b)
	{
		this.image = image;
		this.l = l;
		this.t = t;
		this.r = r;
		this.b = b;
	}
	
	public int getWidth()
	{
		return (r - l);
	}
	
	public int getHeight()
	{
		return (b - t);
	}
	
}
