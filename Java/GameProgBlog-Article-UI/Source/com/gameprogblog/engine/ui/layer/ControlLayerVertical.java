package com.gameprogblog.engine.ui.layer;

import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.ui.Control;
import com.gameprogblog.engine.ui.ControlLayer;
import com.gameprogblog.engine.ui.InterfaceRenderer;
import com.gameprogblog.engine.ui.Tile;

public class ControlLayerVertical implements ControlLayer
{
	
	public int states;
	public Tile top;
	public Tile center;
	public Tile bottom;
	
	public ControlLayerVertical(int states, Tile top, Tile center, Tile bottom)
	{
		this.states = states;
		this.top = top;
		this.center = center;
		this.bottom = bottom;
	}
	
	public boolean isVisible(int states)
	{
		return (this.states & states) != 0;
	}
	
	public void draw(InterfaceRenderer renderer, Control control, Bound2 bounds)	
	{
		float y0 = bounds.top;
		float y1 = bounds.top + top.getHeight();
		float y2 = bounds.bottom - bottom.getHeight();
		float y3 = bounds.bottom;
		float x0 = bounds.left;
		float x1 = bounds.right;
		
		renderer.draw( top, x0, y0, x1, y1 );
		renderer.draw( center, x0, y1, x1, y2 );
		renderer.draw( bottom, x0, y2, x1, y3 );
	}
	
}
