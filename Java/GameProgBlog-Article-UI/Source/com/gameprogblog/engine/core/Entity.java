package com.gameprogblog.engine.core;

import java.awt.Graphics2D;

import com.gameprogblog.engine.GameState;


public interface Entity
{
	public void update( GameState state );
	public void draw( GameState state, Graphics2D gr );
	public boolean isExpired();
	public void expire();
	public void onExpire();
}
