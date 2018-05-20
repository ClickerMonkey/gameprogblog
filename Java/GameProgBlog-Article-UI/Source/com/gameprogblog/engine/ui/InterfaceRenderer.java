package com.gameprogblog.engine.ui;

import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.core.Vector2;

public interface InterfaceRenderer
{

	public void start(Bound2 initialArea);
	public void end();
	
	public void saveState();
	public void restoreState();
	
	public void setClip(Bound2 area, boolean relative);
	public void setClip(float left, float top, float right, float bottom, boolean relative);
	public void setClipRect(float x, float y, float w, float h, boolean relative);
	public void popClip();
	public Bound2 getClip();
	
	public void addShade(float r, float g, float b);
	public void setShade(float r, float g, float b);
	public void popShade();
	public float getRed();
	public float getGreen();
	public float getBlue();
	
	public void addAlpha(float alpha);
	public void setAlpha(float alpha);
	public void popAlpha();
	public float getAlpha();
	
	public void setFont(Font font);
	public void popFont();
	public Font getFont();
	
	public void setOrigin(float x, float y);
	public void addOrigin(float x, float y);
	public void popOrigin();
	public float getX();
	public float getY();
	
	public void draw(Tile tile, Bound2 dest);
	public void draw(Tile tile, float left, float top, float right, float bottom);
	public void draw(String text, float fontSize, Bound2 dest, float lineHeight, Vector2 destAnchor, Vector2 textAnchor, boolean wraps, boolean stretches);
	public void draw(char[] text, float fontSize, Bound2 dest, float lineHeight, Vector2 destAnchor, Vector2 textAnchor, boolean wraps, boolean stretches);
	public void draw(Char[] chars, Bound2 dest, float lineHeight, Vector2 destAnchor, Vector2 textAnchor, boolean wraps, boolean stretches);
	
	public Char[] createChar(String text, float fontSize, boolean useAlpha, boolean useShade);
	
}
