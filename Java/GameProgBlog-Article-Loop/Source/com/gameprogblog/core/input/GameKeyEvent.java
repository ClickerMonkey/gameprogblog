
package com.gameprogblog.core.input;

import java.awt.event.KeyEvent;

public class GameKeyEvent
{

	public GameKeyType type;
	public KeyEvent e;

	public GameKeyEvent( GameKeyType type, KeyEvent e )
	{
		this.type = type;
		this.e = e;
	}
}
