package com.gameprogblog.engine.map;

import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.core.Vector2;


public abstract class AbstractMapEntity implements MapEntity
{
	
	public final Vector2 position = new Vector2();
	public final Vector2 velocity = new Vector2();
	public final Vector2 acceleration = new Vector2();
	public final Bound2 extents = new Bound2();
	public final Bound2 bounds = new Bound2();
	public final MapEntityPhysics physics = new MapEntityPhysics( this );
	public final MapTileNode node = new MapTileNode( this, -1, -1 );
	public float terminal = MapConstants.DEFAULT_TERMINAL;
	public float friction = MapConstants.DEFAULT_FRICTION;
	public float restitution = MapConstants.DEFAULT_RESTITUTION;
	public float mass = 1.0f;
	public Map map;

	@Override
	public void onExpire()
	{
		node.remove();
	}
	
	@Override
	public void onEntityCollisionStart()
	{
		
	}

	@Override
	public void onEntityCollisionEnd()
	{
		
	}
	
	/**
	 * startPhysics( dt )
	 * "calculate acceleration"
	 * updateVelocity( dt )
	 * "modify velocity"
	 * endPhysics( dt )
	 * "check for results (ie touching)"
	 */
	
	protected void runPhysics( float dt )
	{
		startPhysics();
		updatePhysicsVelocity( dt );
		endPhysics( dt );
	}
	
	protected void startPhysics()
	{
		physics.reset();
	}
	
	protected void updatePhysicsVelocity( float dt )
	{
		physics.updateVelocity( dt );
	}

	protected void endPhysics( float dt, float zeroVelocityThreshold, int frictionSides, float airFriction, int restitutionSides )
	{
		physics.applyTerminalVelocityRadial( terminal );
		physics.applyZeroVelocityThreshold( zeroVelocityThreshold );
		physics.handleBlocks( map, dt );
		physics.updatePositionAndNode( map );
		physics.applyFriction( frictionSides, airFriction, friction );
		physics.applyRestitution( restitutionSides, restitution );
	}
	
	protected void endPhysics( float dt )
	{
		endPhysics( dt, MapConstants.DEFAULT_THRESHOLD, MapConstants.BIT_ALL, MapConstants.DEFAULT_AIR_FRICTION, MapConstants.BIT_ALL );
	}
	
	@Override
	public void setMap( Map map )
	{
		this.map = map;
	}
	
	@Override
	public Vector2 getPosition()
	{
		return position;
	}

	@Override
	public Vector2 getVelocity()
	{
		return velocity;
	}

	@Override
	public Vector2 getAcceleration()
	{
		return acceleration;
	}
	
	@Override
	public Bound2 getExtents()
	{
		return extents;
	}

	@Override
	public Bound2 getBounds()
	{
		return bounds;
	}

	@Override
	public float getMass()
	{
		return mass;
	}
	
	@Override
	public MapTileNode getNode()
	{
		return node;
	}

}
