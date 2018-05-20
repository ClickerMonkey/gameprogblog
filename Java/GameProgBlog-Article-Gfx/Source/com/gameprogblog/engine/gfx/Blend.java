package com.gameprogblog.engine.gfx;

public abstract class Blend
{

	public static final Blend Alpha = new Blend()
	{
		public int blend( int o, int n )
		{
			return Color.mixRGB( o, n, Color.alpha( n ), Color.alpha( o ) );
		}
	};

	public static final Blend Additive = new Blend()
	{
		public int blend( int o, int n )
		{
			return Color.add( o, Color.scaleRGB( n, Color.alpha( n ), 0 ) );
		}
	};
	
	public static final Blend Invert = new Blend()
	{
		public int blend( int o, int n )
		{
			return Color.subRGB( Color.scaleRGB( n, Color.alpha( n ), Color.COMPONENT_MAX ), o, Color.alpha( o ) );
		}
	};

	public static final Blend Replace = new Blend()
	{
		public int blend( int o, int n )
		{
			return n;
		}
	};
	
	public static final Blend Ignore = new Blend()
	{
		public int blend( int o, int n )
		{
			return o;
		}
	};

	public abstract int blend( int o, int n );

}