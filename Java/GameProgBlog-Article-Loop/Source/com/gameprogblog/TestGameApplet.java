package com.gameprogblog;

import java.awt.Color;

import com.gameprogblog.core.GameApplet;
import com.gameprogblog.core.GameScreen;


public class TestGameApplet extends GameApplet
{
	
	private static final long serialVersionUID = 1L;
	
	public TestGameApplet()
	{
		TestGame game = new TestGame();

		GameScreen screen = new GameScreen( TestGame.WIDTH, TestGame.HEIGHT, TestGame.ANTIALIASING, TestGame.LOOPS[0], game );
		screen.setBackground( Color.black );

		game.setScreen( screen );
		
		setGameScreen( screen );
	}

}
