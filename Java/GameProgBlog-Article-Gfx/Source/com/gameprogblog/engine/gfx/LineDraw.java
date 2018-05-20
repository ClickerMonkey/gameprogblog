
package com.gameprogblog.engine.gfx;

public class LineDraw
{

	public Gfx g;

	public LineDraw( Gfx g )
	{
		this.g = g;
	}

	public void smooth( int x0, int y0, int x1, int y1, int color )
	{
		int temp = 0;
		
		if (y0 > y1)
		{
			temp = y1; y1 = y0; y0 = temp;
			temp = x1; x1 = x0; x0 = temp;
		}

		int dx = x1 - x0;
		int dy = y1 - y0;

		if (dx == 0)
		{
			vertical( x0, y0, y1, color );
		}
		else if (dy == 0)
		{
			horizontal( y0, x0, x1, color );
		}
		else
		{
			g.applyCheck( x0, y0, color );
			g.applyCheck( x1, y1, color );

			int sx = Integer.signum( dx );
			
			dx = StrictMath.abs( dx );
			dy = StrictMath.abs( dy );

			if (dy > dx)
			{
				char errorAdj = (char)((dx << 16) / dy);
				char errorAccTemp = 0;
				char errorAcc = 0;
				int weighting = 0;
				
				while (--dy > 0)
				{
					errorAccTemp = errorAcc;
					errorAcc += errorAdj;
					
					if (errorAcc <= errorAccTemp) 
					{
		            x0 += sx;
		         }
					
		         y0++; 

		         weighting = (errorAcc >> 8);
					
		         g.applyCheck( x0, y0, Color.mulAlpha( color, weighting ^ 0xFF ) );
		         g.applyCheck( x0 + sx, y0, Color.mulAlpha( color, weighting ) );
				}
			}
			else
			{
				char errorAdj = (char)((dy << 16) / dx);
				char errorAccTemp = 0;
				char errorAcc = 0;
				int weighting = 0;
				
				while (--dx > 0)
				{
					errorAccTemp = errorAcc;
					errorAcc += errorAdj;
					
					if (errorAcc <= errorAccTemp) 
					{
		            y0++;
		         }
					
					x0 += sx;

		         weighting = (errorAcc >> 8);
					
		         g.applyCheck( x0, y0, Color.mulAlpha( color, weighting ^ 0xFF ) );
		         g.applyCheck( x0, y0 + 1, Color.mulAlpha( color, weighting ) );
				}
			}
		}
	}

	public void fast( int x0, int y0, int x1, int y1, int color )
	{
		// TODO clipping

		int dx = x1 - x0;
		int dy = y1 - y0;
		int adx = StrictMath.abs( dx );
		int ady = StrictMath.abs( dy );
		int sx = Integer.signum( dx );
		int sy = Integer.signum( dy );
		int err = adx - ady;
		int e2 = 0;

		for (;;)
		{
			g.apply( x0, y0, color );

			if (x0 == x1 && y0 == y1)
			{
				break;
			}

			e2 = 2 * err;

			if (e2 > -ady)
			{
				err -= ady;
				x0 += sx;
			}

			if (e2 < adx)
			{
				err += adx;
				y0 += sy;
			}
		}
	}

	public void horizontal( int y, int x0, int x1, int color )
	{
		final int bw = g.width();
		final int bh = g.height();

		if (y < 0 || y >= bh || (x0 < 0 && x1 < 0) || (x0 > bw && x1 > bw))
		{
			return;
		}

		x0 = (x0 < 0 ? 0 : (x0 > bw ? bw : x0));
		x1 = (x1 < 0 ? 0 : (x1 > bw ? bw : x1));

		int d = x1 - x0;
		int ad = StrictMath.abs( d );
		int s = Integer.signum( d );
		int offset = g.getOffset( x0, y );

		while (--ad > 0)
		{
			g.apply( offset, color );
			offset += s;
		}
	}

	public void vertical( int x, int y0, int y1, int color )
	{
		final int bw = g.width();
		final int bh = g.height();

		if (x < 0 || x >= bw || (y0 < 0 && y1 < 0) || (y0 > bh && y1 > bh))
		{
			return;
		}

		y0 = (y0 < 0 ? 0 : (y0 > bh ? bh : y0));
		y1 = (y1 < 0 ? 0 : (y1 > bh ? bh : y1));

		int d = y1 - y0;
		int ad = StrictMath.abs( d );
		int s = Integer.signum( d ) * g.width();
		int offset = g.getOffset( x, y0 );

		while (--ad > 0)
		{
			g.apply( offset, color );
			offset += s;
		}
	}

}
