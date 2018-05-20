package com.gameprogblog.core;

import java.awt.BorderLayout;

import javax.swing.JApplet;

public class GameApplet extends JApplet implements Runnable
{
	private static final long serialVersionUID = 1L;

	private GameScreen screen;
	
	public void setGameScreen( GameScreen screen )
	{
		this.screen = screen;
	}
	
	public void destroy()
	{
		screen.stop();
		
		super.destroy();
	}
	
	public void init()
	{
		setLayout( new BorderLayout() );
		
		add( screen );
		setSize( screen.getPreferredSize() );
		setPreferredSize( screen.getPreferredSize() );
		setVisible( true );
			
		screen.setFocusable( true );
		screen.requestFocus();
		
		new Thread( this ).start();
	}
	
	public void run()
	{
		screen.start();
	}
	
}