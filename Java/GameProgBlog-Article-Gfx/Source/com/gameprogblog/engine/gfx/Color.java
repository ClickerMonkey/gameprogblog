package com.gameprogblog.engine.gfx;
public class Color
{
	
	public static final int A_SHIFT = 24;
	public static final int A_MASK = 0xFF000000;
	public static final int R_SHIFT = 16;
	public static final int R_MASK = 0x00FF0000;
	public static final int G_SHIFT = 8;
	public static final int G_MASK = 0x0000FF00;
	public static final int B_SHIFT = 0;
	public static final int B_MASK = 0x000000FF;
	public static final int COMPONENT_MASK = 0xFF;
	public static final int COMPONENT_MIN = 0;
	public static final int COMPONENT_MAX = 255;
	public static final int COMPONENT_POWER = 8;

	public static int clamp( int c )
	{
		return ( c < COMPONENT_MIN ? COMPONENT_MIN : ( c > COMPONENT_MAX ? COMPONENT_MAX : c ) );
	}

	public static int create( int r, int g, int b )
	{
		return create( r, g, b, COMPONENT_MAX );
	}

	public static int create( int r, int g, int b, int a )
	{
		return ( a << A_SHIFT ) | ( r << R_SHIFT ) | ( g << G_SHIFT ) | ( b << B_SHIFT );
	}
	
	public static int mulComponents(int c0, int c1)
	{
		return (c0 * c1 + COMPONENT_MAX) >> COMPONENT_POWER;
	}
	
	public static int mixComponents(int c0, int c1, int delta)
	{
		return mulComponents( c0, COMPONENT_MAX ^ delta ) + mulComponents( c1, delta );
	}
	
	public static int mulAlpha(int c, int alpha)
	{
		return (c & ~A_MASK) | (mulComponents( alpha(c), alpha ) << A_SHIFT);
	}
	
	public static int mulRed(int c, int red)
	{
		return (c & ~R_MASK) | (mulComponents( red(c), red ) << R_SHIFT);
	}
	
	public static int mulGreen(int c, int green)
	{
		return (c & ~G_MASK) | (mulComponents( green(c), green ) << G_SHIFT);
	}
	
	public static int mulBlue(int c, int blue)
	{
		return (c & ~B_MASK) | (mulComponents( blue(c), blue ) << B_SHIFT);
	}
	
	public static int withAlpha(int c, int alpha)
	{
		return (c & ~A_MASK) | (alpha << A_SHIFT);
	}
	
	public static int withRed(int c, int red)
	{
		return (c & ~R_MASK) | (red << R_SHIFT);
	}
	
	public static int withGreen(int c, int green)
	{
		return (c & ~G_MASK) | (green << G_SHIFT);
	}
	
	public static int withBlue(int c, int blue)
	{
		return (c & ~B_MASK) | (blue << B_SHIFT);
	}

	public static int createAndClamp( int r, int g, int b )
	{
		return create( clamp( r ), clamp( g ), clamp( b ) );
	}

	public static int createAndClamp( int r, int g, int b, int a )
	{
		return create( clamp( r ), clamp( g ), clamp( b ), clamp( a ) );
	}

	public static int alpha( int color )
	{
		return ( color >> A_SHIFT ) & COMPONENT_MASK;
	}

	public static int red( int color )
	{
		return ( color >> R_SHIFT ) & COMPONENT_MASK;
	}

	public static int green( int color )
	{
		return ( color >> G_SHIFT ) & COMPONENT_MASK;
	}

	public static int blue( int color )
	{
		return ( color >> B_SHIFT ) & COMPONENT_MASK;
	}

	public static int add( int c0, int c1 )
	{
		return createAndClamp( 
			red( c0 ) + red( c1 ), 
			green( c0 ) + green( c1 ), 
			blue( c0 ) + blue( c1 ), 
			alpha( c0 ) + alpha( c1 ) 
		);
	}
	
	public static int addRGB( int c0, int c1, int alpha )
	{
		return createAndClamp( 
			red( c0 ) + red( c1 ), 
			green( c0 ) + green( c1 ), 
			blue( c0 ) + blue( c1 ), 
			alpha
		);
	}

	public static int sub( int c0, int c1 )
	{
		return createAndClamp( 
			red( c0 ) - red( c1 ), 
			green( c0 ) - green( c1 ), 
			blue( c0 ) - blue( c1 ), 
			alpha( c0 ) - alpha( c1 )
		);
	}
	
	public static int subRGB( int c0, int c1, int alpha )
	{
		return createAndClamp( 
			red( c0 ) - red( c1 ), 
			green( c0 ) - green( c1 ), 
			blue( c0 ) - blue( c1 ), 
			alpha 
		);
	}

	public static int mul( int c0, int c1 )
	{
		return create( 
			mulComponents(red( c0 ), red( c1 )),
			mulComponents(green( c0 ), green( c1 )),
			mulComponents(blue( c0 ), blue( c1 )),
			mulComponents(alpha( c0 ), alpha( c1 ))
		);
	}
	
	public static int mulRGB( int c0, int c1, int alpha )
	{
		return create( 
			mulComponents(red( c0 ), red( c1 )),
			mulComponents(green( c0 ), green( c1 )),
			mulComponents(blue( c0 ), blue( c1 )),
			alpha
		);
	}
	
	public static int lighten( int c, int delta )
	{
		return create( 
			mixComponents( red( c ), COMPONENT_MAX, delta ), 
			mixComponents( green( c ), COMPONENT_MAX, delta ), 
			mixComponents( blue( c ), COMPONENT_MAX, delta ), 
			mixComponents( alpha( c ), COMPONENT_MAX, delta )
		);
	}
	
	public static int lightenRGB( int c, int delta, int alpha )
	{
		return create( 
			mixComponents( red( c ), COMPONENT_MAX, delta ), 
			mixComponents( green( c ), COMPONENT_MAX, delta ), 
			mixComponents( blue( c ), COMPONENT_MAX, delta ), 
			alpha 
		);
	}
	
	public static int darken( int c, int delta )
	{
		return create( 
			mixComponents( red( c ), COMPONENT_MIN, delta ),
			mixComponents( green( c ), COMPONENT_MIN, delta ),
			mixComponents( blue( c ), COMPONENT_MIN, delta ),
			mixComponents( alpha( c ), COMPONENT_MIN, delta ) 
		);
	}
	
	public static int darkenRGB( int c, int delta, int alpha )
	{
		return create( 
			mixComponents( red( c ), COMPONENT_MIN, delta ),
			mixComponents( green( c ), COMPONENT_MIN, delta ),
			mixComponents( blue( c ), COMPONENT_MIN, delta ),
			alpha
		);
	}
	
	public static int mix(int c0, int c1, int delta)
	{
		return create(
			mixComponents( red(c0), red(c1), delta ),
			mixComponents( green(c0), green(c1), delta ),
			mixComponents( blue(c0), blue(c1), delta ),
			mixComponents( alpha(c0), alpha(c1), delta )
		);
	}
	
	public static int mixRGB(int c0, int c1, int delta, int alpha)
	{
		return create(
			mixComponents( red(c0), red(c1), delta ),
			mixComponents( green(c0), green(c1), delta ),
			mixComponents( blue(c0), blue(c1), delta ),
			alpha
		);
	}
	
	public static int scale(int c, int delta)
	{
		return create(
			mixComponents( COMPONENT_MIN, red(c), delta ),
			mixComponents( COMPONENT_MIN, green(c), delta ),
			mixComponents( COMPONENT_MIN, blue(c), delta ),
			mixComponents( COMPONENT_MIN, alpha(c), delta )
		);
	}
	
	public static int scaleRGB(int c, int delta, int alpha)
	{
		return create(
			mixComponents( COMPONENT_MIN, red(c), delta ),
			mixComponents( COMPONENT_MIN, green(c), delta ),
			mixComponents( COMPONENT_MIN, blue(c), delta ),
			alpha
		);
	}

}
