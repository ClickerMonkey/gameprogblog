
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
import com.gameprogblog.engine.map.MapConstants;
import com.gameprogblog.engine.map.MapEntity;
import com.gameprogblog.engine.map.MapTile;


public class Player extends AbstractMapEntity
{

	public Vector2 lastPosition = new Vector2();
	public Vector2 input = new Vector2();
	public Vector2 kickOffForce = new Vector2();
	public boolean jumping;
	public float jumpTime;
	public int movingSign;
	public boolean sliding;
	public boolean canSlide;
	public boolean canKickOff;
	public boolean canClimb;
	public float jumpTimeMax;
	public float jumpVelocity;
	public float slidingFactor;
	public float sideAcceleration;
	public float climbSpeed;
	public long groups = ColorGame.GROUP_ENTITY;
	public Rectangle2D.Float rect = new Rectangle.Float();

	public Player( float x, float y )
	{
		position.set( x, y );
		extents.set( 15, 30, 15, 0 );
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
		gr.setStroke( new BasicStroke( 8, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND ) );
		gr.draw( rect );
	}

	@Override
	public void update( GameState state, Scene scene )
	{
		int frictionSides = input.x != 0 ? 0 : MapConstants.BIT_ALL;

		lastPosition.set( position );

		startPhysics();
		updatePhysicsVelocity( state.seconds );
		endPhysics( state.seconds, MapConstants.DEFAULT_THRESHOLD, frictionSides, MapConstants.DEFAULT_AIR_FRICTION, MapConstants.BIT_ALL );

		sliding = (physics.touching[MapConstants.RIGHT] && input.x > 0.0f) ||
					 (physics.touching[MapConstants.LEFT] && input.x < 0.0f);

		if (!jumping && input.y > 0.0f && physics.touching[MapConstants.BOTTOM])
		{
			jumping = true;
			jumpTime = 0;
		}

		if (jumping && input.y == 0.0f)
		{
			jumping = false;
		}

		if (physics.touching[MapConstants.TOP])
		{
			jumping = false;

			velocity.y = 0;
		}

		if (jumping)
		{
			jumpTime += state.seconds;

			velocity.y = jumpVelocity;

			if (jumpTime >= jumpTimeMax)
			{
				jumping = false;
			}
		}

		if (sliding && canKickOff && input.y > 0.0f)
		{
			if (physics.touching[MapConstants.LEFT])
			{
				velocity.x = kickOffForce.x;
				velocity.y -= kickOffForce.y;
			}
			else if (physics.touching[MapConstants.RIGHT])
			{
				velocity.x = -kickOffForce.x;
				velocity.y -= kickOffForce.y;
			}
		}
		else if (sliding && canClimb)
		{
			velocity.y = climbSpeed;
		}
		else if (sliding && canSlide)
		{
			velocity.y *= slidingFactor;
		}

		acceleration.x = input.x * sideAcceleration;
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

	}

	@Override
	public boolean isExpired()
	{
		return false;
	}

	@Override
	public void expire()
	{
	}

}
