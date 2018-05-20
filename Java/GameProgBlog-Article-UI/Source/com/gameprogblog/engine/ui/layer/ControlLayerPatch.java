package com.gameprogblog.engine.ui.layer;

import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.ui.Control;
import com.gameprogblog.engine.ui.ControlLayer;
import com.gameprogblog.engine.ui.InterfaceRenderer;
import com.gameprogblog.engine.ui.Tile;

public class ControlLayerPatch implements ControlLayer
{
	
	public int states;
	public Tile tl, tc, tr, cl, cc, cr, bl, bc, br;
	
	public ControlLayerPatch(int states, Tile outer, Tile inner)
	{
		this( states, 
			new Tile( outer.image, outer.l, outer.t, inner.l, inner.t ),
			new Tile( outer.image, inner.l, outer.t, inner.r, inner.t ),
			new Tile( outer.image, inner.r, outer.t, outer.r, inner.t ),
			new Tile( outer.image, outer.l, inner.t, inner.l, inner.b ),
			new Tile( outer.image, inner.l, inner.t, inner.r, inner.b ),
			new Tile( outer.image, inner.r, inner.t, outer.r, inner.b ),
			new Tile( outer.image, outer.l, inner.b, inner.l, outer.b ),
			new Tile( outer.image, inner.l, inner.b, inner.r, outer.b ),
			new Tile( outer.image, inner.r, inner.b, outer.r, outer.b )	
		);
	}
	
	public ControlLayerPatch(int states, Tile tl, Tile tc, Tile tr, Tile cl, Tile cc, Tile cr, Tile bl, Tile bc, Tile br)
	{
		this.states = states;
		this.tl = tl;
		this.tc = tc;
		this.tr = tr;
		this.cl = cl;
		this.cc = cc;
		this.cr = cr;
		this.bl = bl;
		this.bc = bc;
		this.br = br;
	}
	
	public boolean isVisible(int states)
	{
		return (this.states & states) != 0;
	}
	
	public void draw(InterfaceRenderer renderer, Control control, Bound2 bounds)	
	{
		float x0 = bounds.left;
		float x1 = bounds.left + tl.getWidth();
		float x2 = bounds.right - tr.getWidth();
		float x3 = bounds.right;
		float y0 = bounds.top;
		float y1 = bounds.top + tl.getHeight();
		float y2 = bounds.bottom - br.getHeight();
		float y3 = bounds.bottom;

		renderer.draw( tl, x0, y0, x1, y1 );
		renderer.draw( tc, x1, y0, x2, y1 );
		renderer.draw( tr, x2, y0, x3, y1 );
		renderer.draw( cl, x0, y1, x1, y2 );
		renderer.draw( cc, x1, y1, x2, y2 );
		renderer.draw( cr, x2, y1, x3, y2 );
		renderer.draw( bl, x0, y2, x1, y3 );
		renderer.draw( bc, x1, y2, x2, y3 );
		renderer.draw( br, x2, y2, x3, y3 );
	}
	
}
