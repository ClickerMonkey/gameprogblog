package com.gameprogblog.engine.ui.layer;

import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.ui.Control;
import com.gameprogblog.engine.ui.ControlLayer;
import com.gameprogblog.engine.ui.InterfaceRenderer;
import com.gameprogblog.engine.ui.Tile;

public class ControlLayerStretch implements ControlLayer
{
	
	public int states;
	public Tile tile;
	
	public ControlLayerStretch(int states, Tile tile)
	{
		this.states = states;
		this.tile = tile;
	}
	
	public boolean isVisible(int states)
	{
		return (this.states & states) != 0;
	}
	
	public void draw(InterfaceRenderer renderer, Control control, Bound2 bounds)	
	{
		renderer.draw( tile, bounds );  
	}
	
}
