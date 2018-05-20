package com.gameprogblog.engine.map;

import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.core.Bound2;



public abstract class AbstractMapTile implements MapTile
{
	public static final float DEFAULT_FRICTION = 1.0f;
	public static final float DEFAULT_RESTITUTION = 0.0f;

	protected long groups;
	protected float friction;
	protected float restitution;
	
	public AbstractMapTile(long groups)
	{
		this( groups, DEFAULT_FRICTION, DEFAULT_RESTITUTION );
	}
	
	public AbstractMapTile(long groups, float friction, float restitution )
	{
		this.groups = groups;
		this.friction = friction;
		this.restitution = restitution;
	}
	
	@Override
	public void update( GameState state, int x, int y )
	{
		
	}

	@Override
	public void onMapAdd( Map map, int x, int y )
	{
		
	}
	
	@Override
	public long getCollisionGroup()
	{
		return groups;
	}
	
	@Override
	public float getFriction()
	{
		return friction;
	}
	
	@Override
	public float getRestitution()
	{
		return restitution;
	}

	@Override
	public void onEntityCollide( Map map, int x, int y, MapEntity entity, int sides )
	{
		
	}

	@Override
	public Bound2 getBounds(Map map, int x, int y, Bound2 out)
	{
		out.left = x * map.tileSize.x + map.tileOffset.x;
		out.right = out.left + map.tileSize.x;
		out.top = y * map.tileSize.y + map.tileOffset.y;
		out.bottom = out.top + map.tileSize.y;
		
		return out;
	}
	
}
