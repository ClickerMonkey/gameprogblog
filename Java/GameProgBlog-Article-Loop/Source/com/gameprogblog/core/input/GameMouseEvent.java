
package com.gameprogblog.core.input;

import java.awt.event.MouseEvent;

public class GameMouseEvent
{

	public GameMouseType type;
	public MouseEvent e;

	public GameMouseEvent( GameMouseType type, MouseEvent e )
	{
		this.type = type;
		this.e = e;
	}
}
