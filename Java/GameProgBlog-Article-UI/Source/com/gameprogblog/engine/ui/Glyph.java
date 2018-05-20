package com.gameprogblog.engine.ui;

import com.gameprogblog.engine.core.Bound2;


public class Glyph
{
	
	public final Tile tile;
	public final float advance, left, top, right, bottom;
	
	public Glyph(Tile tile, float advance, float left, float top, float right, float bottom)
	{
		this.tile = tile;
		this.advance = advance;
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
	public float width() 
	{
		return (right - left);
	}

	public float height() 
	{
		return (bottom - top);
	}
	
	public Glyph scale(float s)
	{
		return new Glyph(tile, advance * s, left * s, top * s, right * s, bottom * s);
	}
	
	public Bound2 getBounds(float x, float y, float scale, Bound2 out)
	{
		out.left = x + left * scale;
		out.right = x + right * scale;
		out.top = y + top * scale;
		out.bottom = y + bottom * scale;
		
		return out;
	}
	
}
