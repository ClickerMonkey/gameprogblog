package com.gameprogblog.engine.ui;


public interface Font
{
	public Glyph getGlyph(char c, int index);
	public float getSize();
	public String getName();
}
