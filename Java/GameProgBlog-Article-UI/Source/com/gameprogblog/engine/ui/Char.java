package com.gameprogblog.engine.ui;

import com.gameprogblog.engine.core.Vector2;

public class Char
{
	public char c;
	public Vector2 offset = new Vector2();
	public Vector2 scale = new Vector2( 1.0f );
	public float red = 1.0f;
	public float green = 1.0f;
	public float blue = 1.0f;
	public float alpha = 1.0f;
	
	public void draw(float x, float y, int charIndex, InterfaceRenderer renderer)
	{
		final Glyph g = renderer.getFont().getGlyph(c, charIndex);

		float l = g.left * scale.x;
		float r = g.right * scale.x;
		float t = g.top * scale.y;
		float b = g.bottom * scale.y;
		float ox = offset.x + x;
		float oy = offset.y + y;
		
		if (alpha != 1.0f)
		{
			renderer.addAlpha( alpha );
		}
		
		if (red != 1.0f || green != 1.0f || blue != 1.0f)
		{
			renderer.addShade( red, green, blue );
		}
		
		renderer.draw( g.tile, ox - l, oy - t, ox + r, oy + b );

		if (red != 1.0f || green != 1.0f || blue != 1.0f)
		{
			renderer.popShade();
		}

		if (alpha != 1.0f)
		{
			renderer.popAlpha();
		}
	}
}
