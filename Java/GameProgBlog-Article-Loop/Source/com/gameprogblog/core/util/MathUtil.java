
package com.gameprogblog.core.util;

/**
 * @author Philip Diffenderfer
 */
public class MathUtil
{

	public static int random( int min, int max )
	{
		return (int)((max - min) * Math.random()) + min;
	}

	public static float random( float min, float max )
	{
		return (float)((max - min) * Math.random()) + min;
	}

	public static float clamp( float value, float min, float max )
	{
		return (value < min ? min : (value > max ? max : value));
	}

}
