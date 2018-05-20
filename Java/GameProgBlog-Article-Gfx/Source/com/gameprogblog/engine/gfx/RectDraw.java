
package com.gameprogblog.engine.gfx;

public class RectDraw
{

	public Gfx g;

	public RectDraw( Gfx g )
	{
		this.g = g;
	}

	public void fill( int x, int y, int w, int h, int color )
	{
		final int backWidth = g.width();
		final int backHeight = g.height();
		
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		if (x + w > backWidth) w = backWidth - x;
		if (y + h > backHeight) h = backHeight - y;
		
		final int stride = backWidth - w;
		int offset = g.getOffset( x, y );

		for (int yy = 0; yy < h; yy++)
		{
			for (int xx = 0; xx < w; xx++)
			{
				g.apply( offset++, color );
			}

			offset += stride;
		}
	}

	public void outline( int x, int y, int w, int h, int color )
	{
		// TODO clipping
		
		final int stride = g.width();
		
		int toffset = g.getOffset( x, y );
		int boffset = g.getOffset( x, y + h - 1 );
		
		for (int i = 0; i < w; i++ )
		{
			g.apply( toffset++, color );
			g.apply( boffset++, color );
		}
		
		int loffset = g.getOffset( x, y + 1 );
		int roffset = g.getOffset( x + w - 1, y + 1 );
		
		for (int i = 2; i < h; i++ )
		{
			g.apply( loffset, color );
			g.apply( roffset, color );
			
			loffset += stride;
			roffset += stride;
		}
	}

}
