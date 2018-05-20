
package com.gameprogblog.engine.gfx;


public class CircleDraw
{

	public Gfx g;

	public CircleDraw( Gfx g )
	{
		this.g = g;
	}

	public void fillSmooth( int cx, int cy, int radius, int color )
	{
		// Adjust for anti-aliasing of pixels around borders
		cx++;
		cy++;
		radius -= 2;

		final int radius2 = radius << 1;
		final int radius4 = radius << 2;
		int error = -radius - 1;
		int x = radius;
		int y = 1;

		scanline( cy, cx - x, cx + x + 1, color );
		
		int alpha = (((error + radius2) << 8) / radius4) ^ 255;
		int alphad = Color.mulAlpha( color, alpha );
		
		g.apply( cx - x - 1, cy, alphad );
		g.apply( cx + x, cy, alphad );
		g.apply( cx, cy - x - 1, alphad );
		g.apply( cx, cy + x + 1, alphad );

		while (x > y)
		{
			alpha = (((error + radius2) << 8) / radius4) ^ 255;
			alphad = Color.mulAlpha( color, alpha );
			
			g.apply( cx - x - 1, cy - y, alphad );
			g.apply( cx + x, cy - y, alphad );
			g.apply( cx - x - 1, cy + y, alphad );
			g.apply( cx + x, cy + y, alphad );
			
			g.apply( cx - y, cy - x - 1, alphad );
			g.apply( cx - y, cy + x + 1, alphad );
			
			if (y != 1)
			{
				g.apply( cx + y - 1, cy - x - 1, alphad );
				g.apply( cx + y - 1, cy + x + 1, alphad );
			}
			
			scanline( cy - y, cx - x, cx + x + 1, color );
			scanline( cy + y, cx - x, cx + x + 1, color );

			error += y + ++y;

			if (error >= 0)
			{
				scanline( cy - x, cx - y + 1, cx + y, color );
				scanline( cy + x, cx - y + 1, cx + y, color );

				error -= x + --x;
			}
		}

		alpha = (((error + radius2) << 8) / radius4) ^ 255;
		alphad = Color.mulAlpha( color, alpha );
		
		g.apply( cx - x - 1, cy - y, alphad );
		g.apply( cx + x, cy - y, alphad );
		g.apply( cx - x - 1, cy + y, alphad );
		g.apply( cx + x, cy + y, alphad );
		
		if (x == y)
		{
			g.apply( cx - x, cy - y - 1, alphad );
			g.apply( cx + x - 1, cy - y - 1, alphad );
			g.apply( cx - x, cy + y + 1, alphad );
			g.apply( cx + x - 1, cy + y + 1, alphad );
			
			scanline( cy - y, cx - x, cx + x + 1, color );
			scanline( cy + y, cx - x, cx + x + 1, color );
		}
	}

	public void fillFast( int cx, int cy, int radius, int color )
	{
		int error = -radius + 1;
		int x = radius;
		int y = 1;

		scanline( cy, cx - x, cx + x, color );

		while (x > y)
		{
			scanline( cy - y, cx - x, cx + x, color );
			scanline( cy + y, cx - x, cx + x, color );

			error += y + ++y;

			if (error >= 0)
			{
				scanline( cy - x, cx - y, cx + y, color );
				scanline( cy + x, cx - y, cx + y, color );

				error -= x + --x;
			}
		}

		if (x == y)
		{
			scanline( cy - y, cx - x, cx + x, color );
			scanline( cy + y, cx - x, cx + x, color );
		}
	}

	private void scanline( int y, int x0, int x1, int color )
	{
		final int bw = g.width();
		final int bh = g.height();

		if (y < 0 || y >= bh || (x0 < 0 && x1 < 0) || (x0 > bw && x1 > bw))
		{
			return;
		}

		x0 = (x0 < 0 ? 0 : (x0 > bw ? bw : x0));
		x1 = (x1 < 0 ? 0 : (x1 > bw ? bw : x1));

		int offset = g.getOffset( x0, y );

		while (x0 < --x1)
		{
			g.apply( offset++, color );
		}
	}

	public void outlineFast( int cx, int cy, int radius, int color )
	{
		int error = -radius;
		int x = radius;
		int y = 0;

		while (x > y)
		{
			apply8( cx, cy, x, y, color );

			error += y++ + y;

			if (error >= 0)
			{
				error -= x + --x;
			}
		}

		apply4( cx, cy, x, y, color );
	}

	private void apply8( int cx, int cy, int x, int y, int color )
	{
		apply4( cx, cy, x, y, color );
		apply4( cx, cy, y, x, color );
	}

	private void apply4( int cx, int cy, int x, int y, int color )
	{
		g.applyCheck( cx + x, cy + y, color );
		g.applyCheck( cx - x, cy + y, color );
		g.applyCheck( cx + x, cy - y, color );
		g.applyCheck( cx - x, cy - y, color );
	}

	public void outlineSmooth( int cx, int cy, int r, int color )
	{
		// TODO not suck
		
		int R2 = r * r;
		int y = 0;
		int x = r;

		int B = x * x;
		int xTop = x + 1;
		int T = xTop * xTop;

		while (y < x)
		{
			int E = R2 - y * y;
			int L = E - B;
			int U = T - E;

			if (L < 0)
			{
				xTop = x;
				x--;
				T = B;
				U = -L;
				B = x * x;
				L = E - B;
			}

			octants( cx, cy, x, xTop, y, color, 255 * U / (U + L) );

			y++;
		}
	}

	public void octants( int cx, int cy, int x0, int x1, int y, int color, int u )
	{
		int ucolor = Color.mulAlpha( color, u );
		int vcolor = Color.mulAlpha( color, Color.COMPONENT_MAX - u );

		g.applyCheck( cx + x0, cy + y, ucolor );
		g.applyCheck( cx + x1, cy + y, vcolor );
		g.applyCheck( cx + x0, cy - y, ucolor );
		g.applyCheck( cx + x1, cy - y, vcolor );

		g.applyCheck( cx - x0, cy + y, ucolor );
		g.applyCheck( cx - x1, cy + y, vcolor );
		g.applyCheck( cx - x0, cy - y, ucolor );
		g.applyCheck( cx - x1, cy - y, vcolor );

		g.applyCheck( cx + y, cy + x0, ucolor );
		g.applyCheck( cx + y, cy + x1, vcolor );
		g.applyCheck( cx - y, cy + x0, ucolor );
		g.applyCheck( cx - y, cy + x1, vcolor );

		g.applyCheck( cx + y, cy - x0, ucolor );
		g.applyCheck( cx + y, cy - x1, vcolor );
		g.applyCheck( cx - y, cy - x0, ucolor );
		g.applyCheck( cx - y, cy - x1, vcolor );
	}

}
