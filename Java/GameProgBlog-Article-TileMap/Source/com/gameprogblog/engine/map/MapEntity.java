
package com.gameprogblog.engine.map;

import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.core.Entity;
import com.gameprogblog.engine.core.Vector2;


public interface MapEntity extends Entity
{

	public Vector2 getPosition();

	public Vector2 getVelocity();

	public Vector2 getAcceleration();

	public Bound2 getExtents();

	public Bound2 getBounds();

	public long getGroups();

	public long getCollidesWith();
	
	public float getMass();
	
	public MapTileNode getNode();

	public void setMap( Map map );
	
	public void onMapCollide( Map map, int x, int y, MapTile tile, int sides );
	
	public void onEntityCollisionStart();

	public void onEntityCollisionEnd();
	
	public void onEntityCollide( Map map, MapEntity other, int collideIndex );

}
