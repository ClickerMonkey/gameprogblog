package com.gameprogblog.engine.core;



public class Vector2
{

	public float x, y;
	
	public Vector2()
	{
	}
	
	public Vector2(float x, float y)
	{
		set( x, y );
	}
	
	public Vector2(Vector2 v )
	{
		set( v );
	}
	
	public void set(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void set(Vector2 v)
	{
		this.x = v.x;
		this.y = v.y;
	}

	public void add(Vector2 v, float scale)
	{
		x += v.x * scale;
		y += v.y * scale;
	}

	public void sub(Vector2 v)
	{
		x -= v.x;
		y -= v.y;
	}
	
	public float dot()
	{
		return ( x * x + y * y );
	}
	
	public void scale(float s)
	{
		x *= s;
		y *= s;
	}
	
	public float norm()
	{
		float d = dot();
		
		if ( d != 0.0f && d != 1.0f )
		{
			d = (float)Math.sqrt( d );
			x /= d;
			y /= d;
		}
		
		return d;
	}
	
	public void clamp( float min, float max )
	{
		float d = dot();
		
		if ( d < min * min )
		{
			d = 1.0f / (float)Math.sqrt( d );
			
			x = x * d * min;
			y = y * d * min;
		}
		else if ( d > max * max )
		{
			d = 1.0f / (float)Math.sqrt( d );
			
			x = x * d * max;
			y = y * d * max;
		}
	}
	
	public float distance( Vector2 v )
	{
		float dx = v.x - x;
		float dy = v.y - y;
		return (float)Math.sqrt( dx * dx + dy * dy );
	}

	@Override
	public String toString()
	{
		return "Vector2 [x=" + x + ", y=" + y + "]";
	}
	
}
