package com.gameprogblog.engine.map;

import com.gameprogblog.engine.core.Index2;


public class MapConstants
{

	public static final float DEFAULT_FRICTION = 1.0f;
	public static final float DEFAULT_AIR_FRICTION = 0.0f;
	public static final float DEFAULT_RESTITUTION = 0.0f;
	public static final float DEFAULT_THRESHOLD = 0.001f;
	public static final float DEFAULT_TERMINAL = (float)Math.sqrt( Float.MAX_VALUE );
	
	public static final int LEFT = 0;
	public static final int TOP = 1;
	public static final int RIGHT = 2;
	public static final int BOTTOM = 3;
	public static final int SIDES = 4;
	
	public static final int BIT_LEFT = 1 << LEFT;
	public static final int BIT_TOP = 1 << TOP;
	public static final int BIT_RIGHT = 1 << RIGHT;
	public static final int BIT_BOTTOM = 1 << BOTTOM;
	public static final int BIT_ALL = BIT_LEFT | BIT_TOP | BIT_RIGHT | BIT_BOTTOM;
	
	public static final int[] BITS = {
		BIT_LEFT, BIT_TOP, BIT_RIGHT, BIT_BOTTOM
	};
	
	public static final int[] OPPOSITE = { 
		RIGHT, BOTTOM, LEFT, TOP 
	};

	public static final Index2[] NORMAL = {
		new Index2(-1, 0 ),
		new Index2( 0,-1 ),
		new Index2( 1, 0 ),
		new Index2( 0, 1 )
	};
	
}
