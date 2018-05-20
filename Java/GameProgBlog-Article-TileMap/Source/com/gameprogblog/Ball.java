package com.gameprogblog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.core.Vector2;
import com.gameprogblog.engine.map.AbstractMapEntity;
import com.gameprogblog.engine.map.Map;
import com.gameprogblog.engine.map.MapEntity;
import com.gameprogblog.engine.map.MapTile;


public class Ball extends AbstractMapEntity
{
	public Vector2 lastPosition = new Vector2();
	public float time;
	public long groups;
	public Rectangle2D.Float rect = new Rectangle.Float();
	
	public Ball( float x, float y, float r, float lifespan, long colorGroup )
	{
		position.set( x, y );
		extents.set( r, r, r, r );
		time = lifespan;
		groups = colorGroup;
		rect.setRect( 0, 0, extents.left + extents.right, extents.top + extents.bottom );
	}
	
	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		float dx = (position.x - lastPosition.x) * state.interpolate + lastPosition.x;
		float dy = (position.y - lastPosition.y) * state.interpolate + lastPosition.y;
		
		rect.x = dx - extents.left;
		rect.y = dy - extents.top;
		
		Color c = ColorGame.getColor( groups );
		
		gr.setColor( c );
		gr.fill( rect ); 
		
		gr.setColor( new Color( c.getRed(), c.getGreen(), c.getBlue(), 128 ) );
		gr.setStroke( new BasicStroke( 4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND ) );
		gr.draw( rect );
	}

	@Override
	public void update( GameState state, Scene scene )
	{
		lastPosition.set( position );
		
		time -= state.seconds;
		
		runPhysics( state.seconds );
	}

	@Override
	public long getGroups()
	{
		return groups;
	}
	
	@Override
	public long getCollidesWith()
	{
		return groups | ColorGame.GROUP_BUTTONS;
	}
	
	@Override
	public void onMapCollide( Map map, int x, int y, MapTile tile, int sides )
	{
		
	}

	@Override
	public void onEntityCollide( Map map, MapEntity other, int collideIndex )
	{
		if (!(other instanceof Player))
		{
			ColorGame.handleCollision( this, other );	
		}
	}
	
	@Override
	public boolean isExpired()
	{
		return (time <= 0.0f);
	}

	@Override
	public void expire()
	{
		time = 0.0f;
	}

}
