package com.gameprogblog.engine.gfx;


public class TileDraw
{

	public Gfx g;
	
	public TileDraw(Gfx g)
	{
		this.g = g;
	}
	
	public void draw(Tile tile, int x, int y)
	{
		int bw = g.width();
		int bh = g.height();
		
		int tl = tile.l;
		int tt = tile.t;
		int tr = tile.r;
		int tb = tile.b;
		
		int rl = x;
		int rt = y;
		int rr = x + tile.width();
		int rb = y + tile.height();
		
		if (rl < 0)
		{
			tl -= rl;
			rl = 0;
		}
		
		if (rt < 0)
		{
			tt -= rt;
			rt = 0;
		}
		
		if (rr > bw)
		{
			tr -= rr - bw;
			rr = bw;
		}
		
		if (rb > bh)
		{
			tb -= rb - bh;
			rb = bh;
		}
		
		final Texture tex = tile.texture;
		final int[] src = tex.pixels;
		final int tstride = tile.width() - (tr - tl);
		final int rstride = bw - (rr - rl);
		
		int toffset = tex.getOffset( tl, tt );
		int roffset = g.getOffset( rl, rt );
		
		for (y = rt; y < rb; y++)
		{
			for (x = rl; x < rr; x++)
			{
				g.apply( roffset++, src[toffset++] );
			}
			
			toffset += tstride;
			roffset += rstride;
		}
	}
	
	public void draw(Tile tile, int x, int y, int color)
	{
		int bw = g.width();
		int bh = g.height();
		
		int tl = tile.l;
		int tt = tile.t;
		int tr = tile.r;
		int tb = tile.b;
		
		int rl = x;
		int rt = y;
		int rr = x + tile.width();
		int rb = y + tile.height();
		
		if (rl < 0)
		{
			tl -= rl;
			rl = 0;
		}
		
		if (rt < 0)
		{
			tt -= rt;
			rt = 0;
		}
		
		if (rr > bw)
		{
			tr -= rr - bw;
			rr = bw;
		}
		
		if (rb > bh)
		{
			tb -= rb - bh;
			rb = bh;
		}
		
		final Texture tex = tile.texture;
		final int[] src = tex.pixels;
		final int tstride = tile.width() - (tr - tl);
		final int rstride = bw - (rr - rl);
		
		int toffset = tex.getOffset( tl, tt );
		int roffset = g.getOffset( rl, rt );
		
		for (y = rt; y < rb; y++)
		{
			for (x = rl; x < rr; x++)
			{
				g.apply( roffset++, Color.mul( color, src[toffset++] ) );
			}
			
			toffset += tstride;
			roffset += rstride;
		}
	}
	
}
