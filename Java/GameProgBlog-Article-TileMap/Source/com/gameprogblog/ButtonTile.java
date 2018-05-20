package com.gameprogblog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.map.AbstractMapTile;
import com.gameprogblog.engine.map.Map;
import com.gameprogblog.engine.map.MapEntity;


public class ButtonTile extends AbstractMapTile
{

	public Bound2 size;
	public long colorGroup;
	public Rectangle2D.Float rect = new Rectangle.Float();
	
	public ButtonTile( long colorGroup, Bound2 size )
	{
		super( ColorGame.GROUP_BUTTONS );
		
		this.colorGroup = colorGroup;
		this.size = size;
	}
	
	@Override
	public void draw( GameState state, Graphics2D gr, Bound2 bounds, int x, int y, Map map )
	{
		Color c = ColorGame.getColor( colorGroup );
		
		rect.x = bounds.dx( size.left );
		rect.width = bounds.dx( size.right ) - rect.x;
		rect.y = bounds.dy( size.top );
		rect.height = bounds.dy( size.bottom ) - rect.y;
		
		gr.setColor( c );
		gr.fill( rect );

		gr.setColor( new Color( c.getRed(), c.getGreen(), c.getBlue(), 128 ) );
		gr.setStroke( new BasicStroke( 4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND ) );
		gr.draw( rect );
	}

	@Override
	public void onEntityCollide( Map map, int x, int y, MapEntity entity, int sides )
	{
		ColorGame.get.player.groups = colorGroup;
	}

	@Override
	public Bound2 getBounds(Map map, int x, int y, Bound2 out)
	{
		out = super.getBounds( map, x, y, out );
		
		float l = out.dx( size.left );
		float r = out.dx( size.right );
		float t = out.dy( size.top );
		float b = out.dy( size.bottom );
		
		out.set( l, t, r, b );
		
		return out;
	}

}
