
package com.gameprogblog.engine.map;

import java.awt.Graphics2D;

import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.core.Bound2;

public interface MapTile 
{
	
	// draws this tile at the given location
	public void draw( GameState state, Graphics2D gr, Bound2 bounds, int x, int y, Map map );
	
	// updates the tile at the given location
	public void update( GameState state, int x, int y );
	
	// the groups of entities that can collide with this tile
	public long getCollisionGroup();
	
	// when an entity touches this tile
	public void onEntityCollide( Map map, int x, int y, MapEntity entity, int sides );
	
	// method called for every instance of this tile on the given map
	public void onMapAdd( Map map, int x, int y );
	
	// Gets the bounds of the tile based on the map and index of the tile
	public Bound2 getBounds( Map map, int x, int y, Bound2 out );
	
	// 0 = no friction, 1 = 100% friction
	public float getFriction();
	
	// 0 = no bounce, 1 = full bounce
	public float getRestitution();
}
