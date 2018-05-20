package com.gameprogblog.engine.ui.layer;

import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.ui.Control;
import com.gameprogblog.engine.ui.ControlLayer;
import com.gameprogblog.engine.ui.InterfaceRenderer;
import com.gameprogblog.engine.ui.Tile;

public class ControlLayerHorizontal implements ControlLayer
{
	
	public int states;
	public Tile left;
	public Tile center;
	public Tile right;
	
	public ControlLayerHorizontal(int states, Tile left, Tile center, Tile right)
	{
		this.states = states;
		this.left = left;
		this.center = center;
		this.right = right;
	}
	
	public boolean isVisible(int states)
	{
		return (this.states & states) != 0;
	}
	
	public void draw(InterfaceRenderer renderer, Control control, Bound2 bounds)	
	{
		float x0 = bounds.left;
		float x1 = bounds.left + left.getWidth();
		float x2 = bounds.right - right.getWidth();
		float x3 = bounds.right;
		float y0 = bounds.top;
		float y1 = bounds.bottom;
		
		renderer.draw( left, x0, y0, x1, y1 );
		renderer.draw( center, x1, y0, x2, y1 );
		renderer.draw( right, x2, y0, x3, y1 );
	}
	
}
