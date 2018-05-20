package com.gameprogblog.engine.ui;

import com.gameprogblog.engine.core.Bound2;

public interface ControlEffect
{
	public boolean isActive(int states);
	public void start(InterfaceRenderer renderer, Control control, Bound2 bounds);
	public void end(InterfaceRenderer renderer, Control control, Bound2 bounds);
}