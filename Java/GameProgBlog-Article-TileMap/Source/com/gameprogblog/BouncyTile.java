package com.gameprogblog;

import java.awt.Color;


public class BouncyTile extends ColorTile
{

	public BouncyTile( long groups, Color color )
	{
		super( groups, color.brighter() );
		
		this.restitution = 1.0f;
	}
	
}
