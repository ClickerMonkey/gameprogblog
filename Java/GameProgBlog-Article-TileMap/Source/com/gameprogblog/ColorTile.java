package com.gameprogblog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.core.Index2;
import com.gameprogblog.engine.map.AbstractMapTile;
import com.gameprogblog.engine.map.Map;
import com.gameprogblog.engine.map.MapConstants;


public class ColorTile extends AbstractMapTile
{
	
	public static float OUTLINE = 4f;
	public static Color OUTLINE_COLOR = new Color( 0, 0, 0, 50 );
	public static Stroke OUTLINE_STROKE = new BasicStroke( OUTLINE, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER );

	public Color color;
	public Rectangle2D.Float rect = new Rectangle.Float();
	public Line2D.Float line = new Line2D.Float();
	
	public ColorTile( long groups, Color color )
	{
		super( groups );
		
		this.color = color;
	}
	
	@Override
	public void draw( GameState state, Graphics2D gr, Bound2 bounds, int x, int y, Map map )
	{
		if ( color != null )
		{
			rect.x = bounds.left;
			rect.y = bounds.top;
			rect.width = bounds.getWidth();
			rect.height = bounds.getHeight();
			
			gr.setColor( color );
			gr.fill( rect );
			
			gr.setColor( OUTLINE_COLOR );
			gr.setStroke( OUTLINE_STROKE );

			float oh = OUTLINE * 0.5f;
			
			float bl = bounds.left + oh;
			float br = bounds.right - oh;
			float bt = bounds.top + oh;
			float bb = bounds.bottom - oh;
			
			if ( isDifferent( map, x, y, MapConstants.LEFT ) )
			{
				line.setLine( bl, bt, bl, bb );
				gr.draw( line );
			}
			
			if ( isDifferent( map, x, y, MapConstants.RIGHT ) )
			{
				line.setLine( br, bt, br, bb );
				gr.draw( line );
			}
			
			if ( isDifferent( map, x, y, MapConstants.TOP ) )
			{
				line.setLine( bl, bt, br, bt );
				gr.draw( line );
			}
			
			if ( isDifferent( map, x, y, MapConstants.BOTTOM ) )
			{
				line.setLine( bl, bb, br, bb );
				gr.draw( line );
			}
		}
	}
	
	public boolean isDifferent( Map map, int x, int y, int side )
	{
		final Index2 n = MapConstants.NORMAL[side];
		final int nx = x + n.x;
		final int ny = y + n.y;
		
		return map.isOutside( nx, ny ) ? false : map.getTile( nx, ny ).getCollisionGroup() != getCollisionGroup();
	}

}
