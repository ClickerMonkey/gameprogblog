package com.gameprogblog.engine.ui;

import com.gameprogblog.engine.core.Bound2;

public interface ControlLayer
{
	public boolean isVisible(int states);
	public void draw(InterfaceRenderer renderer, Control control, Bound2 bounds);
}
