
package com.gameprogblog;

import java.awt.Graphics2D;

import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.core.Entity;
import com.gameprogblog.engine.core.Vector2;


public class Spring implements Entity
{

	public float distance;
	public float stiffness;
	public float damping;
	public Vector2 rest;
	public Vector2 velocity;
	public Vector2 position;
	public Vector2 temp;
	public boolean addVelocity;
	public boolean enabled;
	public boolean expired;

	public Spring()
	{
	}

	public Spring( Vector2 position, float distance, float damping, float stiffness )
	{
		this( position, new Vector2( position ), distance, damping, stiffness );
	}

	public Spring( Vector2 position, Vector2 rest, float distance, float damping, float stiffness )
	{
		this( position, rest, new Vector2(), distance, damping, stiffness );
	}

	public Spring( Vector2 position, Vector2 rest, Vector2 velocity, float distance, float damping, float stiffness )
	{
		this.position = position;
		this.rest = rest;
		this.velocity = velocity;
		this.temp = new Vector2();
		this.distance = distance;
		this.damping = damping;
		this.stiffness = stiffness;
	}

	@Override
	public void update( GameState state, Scene scene )
	{
		if (enabled)
		{
			final float elapsed = state.seconds;
			float d = position.distance( rest );

			temp.set( position );
			temp.add( rest, -1 );

			if (d != 0)
			{
				temp.scale( 1f / d );
				temp.scale( (d - distance) * stiffness );
			}

			temp.add( velocity, -damping );

			velocity.add( temp, elapsed );
			
			if (addVelocity)
			{
				position.add( velocity, elapsed );	
			}
		}
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{

	}

	@Override
	public boolean isExpired()
	{
		return expired;
	}

	@Override
	public void expire()
	{
		expired = true;
	}

	@Override
	public void onExpire()
	{

	}

}
