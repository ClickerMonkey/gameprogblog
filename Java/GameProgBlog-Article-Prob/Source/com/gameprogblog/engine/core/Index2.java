package com.gameprogblog.engine.core;


public class Index2
{

	public int x, y;
	
	public Index2()
	{
	}
	
	public Index2(int x, int y)
	{
		set( x, y );
	}
	
	public Index2(Index2 v )
	{
		set( v );
	}
	
	public void set(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void set(Index2 v)
	{
		this.x = v.x;
		this.y = v.y;
	}

	public void add(Index2 v, int scale)
	{
		x += v.x * scale;
		y += v.y * scale;
	}

	public void sub(Index2 v)
	{
		x -= v.x;
		y -= v.y;
	}
	
	public int dot()
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
	
	public void clamp( int min, int max )
	{
		float d = dot();
		
		if ( d < min * min )
		{
			scale( min / (float)Math.sqrt( d ) );
		}
		else if ( d > max * max )
		{
			scale( max / (float)Math.sqrt( d ) );
		}
	}

	@Override
	public String toString()
	{
		return "Index2 [x=" + x + ", y=" + y + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Index2 other = (Index2)obj;
		if (x != other.x) return false;
		if (y != other.y) return false;
		return true;
	}
	
}
