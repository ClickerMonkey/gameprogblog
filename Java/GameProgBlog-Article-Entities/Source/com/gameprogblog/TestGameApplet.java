package com.gameprogblog;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameApplet;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopInterpolated;
import com.gameprogblog.engine.GameScreen;


public class TestGameApplet extends GameApplet
{
	
	private static final long serialVersionUID = 1L;
	
	public TestGameApplet()
	{
		Game game = new TestGame();
		
		GameLoop loop = new GameLoopInterpolated( 3, 20, TimeUnit.MILLISECONDS, false );
		
		GameScreen screen = new GameScreen( TestGame.WIDTH, TestGame.HEIGHT, TestGame.ANTIALIASING, loop, game );
		screen.setBackground( Color.black );
		
		setGameScreen( screen );
	}

}
