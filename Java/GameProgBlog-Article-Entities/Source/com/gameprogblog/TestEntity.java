package com.gameprogblog;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.core.Entity;
import com.gameprogblog.engine.core.Vector2;
import com.gameprogblog.engine.util.MathUtil;


public class TestEntity implements Entity
{
	public Color color;
	public Rectangle2D.Float boundary;
	public Ellipse2D.Float ellipse = new Ellipse2D.Float();
	public Vector2 pos = new Vector2();
	public Vector2 vel = new Vector2();
	public Vector2 lastPos = new Vector2();
	public float radius, time, life;
	
	public TestEntity( Color color, Rectangle2D.Float boundary, float x, float y )
	{
		float angle = MathUtil.random( 0.0f, 6.28f );
		float speed = MathUtil.random( 100, 200 );
		
		this.color = color;
		this.boundary = boundary;
		this.pos.set( x, y );
		this.vel.set( (float)Math.cos( angle ) * speed, (float)Math.sin( angle ) * speed );
		this.life = MathUtil.random( 0.5f, 10.0f );
		this.radius = MathUtil.random( 10, 30 );
	}
	
	@Override
	public void draw( GameState state, Graphics2D gr )
	{
		ellipse.x = (pos.x - lastPos.x) * state.interpolate + lastPos.x - radius;
		ellipse.y = (pos.y - lastPos.y) * state.interpolate + lastPos.y - radius;
		ellipse.width = radius * 2;
		ellipse.height = radius * 2;
		
		gr.setColor( new Color( color.getRed(), color.getGreen(), color.getBlue(), (int)(255 - 255 * time / life) ) );
		gr.fill( ellipse );
	}

	@Override
	public void update( GameState state )
	{
		time += state.seconds;
		
		lastPos.set( pos );
		pos.add( vel, state.seconds );

		handleBoundaryCollision();
	}
	
	private void handleBoundaryCollision()
	{
		float bl = boundary.x + radius;
		float br = boundary.x + boundary.width - radius;
		float bt = boundary.y + radius;
		float bb = boundary.y + boundary.height - radius;
		
		if ( pos.x < bl )
		{
			vel.x = -vel.x;
			pos.x = bl;
		}
		
		if ( pos.x > br )
		{
			vel.x = -vel.x;
			pos.x = br;
		}
		
		if ( pos.y < bt )
		{
			vel.y = -vel.y;
			pos.y = bt;
		}
		
		if ( pos.y > bb )
		{
			vel.y = -vel.y;
			pos.y = bb;
		}
	}

	@Override
	public boolean isExpired()
	{
		return (time >= life);
	}

	@Override
	public void expire()
	{
		time = life;
	}

	@Override
	public void onExpire()
	{
		// nothing
	}

}
