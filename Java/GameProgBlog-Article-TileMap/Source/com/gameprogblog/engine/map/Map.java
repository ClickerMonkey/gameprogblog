package com.gameprogblog.engine.map;

import java.awt.Graphics2D;

import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.core.EntityList;
import com.gameprogblog.engine.core.Index2;
import com.gameprogblog.engine.core.Vector2;
import com.gameprogblog.engine.util.MathUtil;

/**
 * A grid of tiles and a list of entities. The grid acts as a spatial database
 * to quickly determine intersecting entities, and also dictates what tiles
 * are drawn where and how the entities can interact with the map. 
 * 
 * @author Philip Diffenderfer
 *
 */
public class Map extends EntityList<MapEntity> implements MapQuery
{
	
	public int width, height;
	public int[][] data;
	public MapTileNode[][] nodes;
	public MapTileNode outsiders;
	public MapTile[] tiles;
	public Vector2 tileSize;
	public Vector2 tileOffset;

	/**
	 * Sets the map data.
	 * 
	 * @param data
	 * 	The new map data.
	 */
	public void setData( int[][] data )
	{
		this.data = data;
		this.height = data.length;
		this.width = data[0].length;
		
		this.initializeNodes();
		this.registerTiles();
	}

	/**
	 * Sets the map data where the rows need to flip vertically.
	 * 
	 * @param data
	 * 	The new map data.
	 */
	public void setDataReversed( int[][] data )
	{
		this.height = data.length;
		this.width = data[0].length;
		this.data = new int[height][];

		for (int y = 0; y < height; y++)
		{
			this.data[y] = data[height - y - 1];
		}
		
		this.initializeNodes();
		this.registerTiles();
	}
	
	/**
	 * Initializes nodes (used in hashing an entity to a specific tile to quickly
	 * query the map for entities) given the height and width have been set.
	 */
	private void initializeNodes()
	{
		nodes = new MapTileNode[ height ][ width ];
		
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				nodes[y][x] = new MapTileNode( null, x, y );
			}
		}
		
		outsiders = new MapTileNode( null, -1, -1 );
	}
	
	/**
	 * Sets the tiles for this map. These tiles are used with the map data where
	 * the map data holds an index to a tile in the given array.
	 * 
	 * @param tiles
	 * 	The array of tiles used in this map.
	 */
	public void setTiles( MapTile... tiles )
	{
		this.tiles = tiles;
		this.registerTiles();
	}

	/**
	 * Sets the tile dimensions and offset of this map.
	 * 
	 * @param tileWidth
	 * 	The width of a tile.
	 * @param tileHeight
	 * 	The height of a tile.
	 * @param offsetX
	 * 	The offset of the map on the x-axis.
	 * @param offsetY
	 * 	The offset of the map on the y-axis.
	 */
	public void set( float tileWidth, float tileHeight, float offsetX, float offsetY )
	{
		this.tileSize = new Vector2( tileWidth, tileHeight );
		this.tileOffset = new Vector2( offsetX, offsetY );
	}
	
	/**
	 * Notifies all MapTiles that they have been added to this map.
	 */
	private void registerTiles()
	{
		if ( data != null && tiles != null )
		{
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					tiles[data[y][x]].onMapAdd( this, x, y );
				}
			}
		}
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		// Draw only visible tiles
		final Bound2 bounds = scene.camera.bounds;

		int startX = getX( bounds.left, true );
		int endX = getX( bounds.right, false );
		int startY = getY( bounds.top, true );
		int endY = getY( bounds.bottom, false );
		
		// Entity is off the map, skip drawing.
		if ( isEntirelyOutside( startX, startY, endX, endY ) )
		{
			return;
		}
		
		// Clamp values so only visible tiles are drawn.
		startX = MathUtil.clamp( startX, 0, width );
		endX = MathUtil.clamp( endX, 0, width );
		startY = MathUtil.clamp( startY, 0, height );
		endY = MathUtil.clamp( endY, 0, height );
		
		// Draw every tile
		final Bound2 tileBounds = new Bound2();

		for (int y = startY; y < endY; y++)
		{
			tileBounds.top = y * tileSize.y + tileOffset.y;
			tileBounds.bottom = tileBounds.top + tileSize.y;

			for (int x = startX; x < endX; x++)
			{
				tileBounds.left = x * tileSize.x + tileOffset.x;
				tileBounds.right = tileBounds.left + tileSize.x;

				tiles[data[y][x]].draw( state, gr, tileBounds, x, y, this );
			}
		}

		// Draw every entity
		super.draw( state, gr, scene );
	}

	@Override
	public void update( GameState state, Scene scene )
	{
		// Update all tiles
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				tiles[data[y][x]].update( state, x, y );
			}
		}
		
		// Physics, tile collision detection & resolution 
		super.update( state, scene );
		
		// Notify every entity of all collisions with other entities.
		for ( int i = 0; i < size(); i++ )
		{
			MapEntity e = get(i);
			
			query( e, e.getBounds(), tileSize.x + tileSize.y, e.getCollidesWith(), Integer.MAX_VALUE, this );	
		}
	}
	
	@Override
	protected void onAdd( final MapEntity entity )
	{
		// Update entity bounds based on position and extents
		entity.getBounds().set( entity.getPosition(), entity.getExtents() );

		// Set the map of the entity to this
		entity.setMap( this );
		
		// Determine their initial node
		updateNode( entity );
	}

	/**
	 * Is invoked when an entity has started being checked for collisions with
	 * other entities.
	 */
	@Override
	public void onStart( Object data, Bound2 query, long groups, int max )
	{
		((MapEntity)data).onEntityCollisionStart();
	}

	/**
	 * Is invoked when an entity has been found that intersects with the
	 * attached entity (data).
	 */
	@Override
	public void onEntity( Object data, MapEntity found, Bound2 query, long groups, int foundIndex )
	{
		if ( data != found )
		{
			((MapEntity)data).onEntityCollide( this, found, foundIndex );
		}
	}

	/**
	 * Is invoked when an entity has finished being checked for collisions with
	 * other entities.
	 */
	@Override
	public void onEnd( Object data, int totalFound )
	{
		((MapEntity)data).onEntityCollisionEnd();
	}
	
	/**
	 * Queries the map for all entities in a region that are a part of a set
	 * of groups.
	 * 
	 * @param data
	 * 	Data that is passed around to the callback. If this data is a 
	 * 	MapEntity, it will be directly notified of entity collisions as well
	 * 	as notifying the callback an entity was found.
	 * @param query
	 * 	The region to find colliding entities in.
	 * @param buffer
	 * 	The buffer of space around that region. Since the upper-left corner
	 * 	of an entity determines which tile it lies under, the entity is absent
	 * 	in all other tiles it may overlap with. This buffer space should be the
	 * 	max dimension of any entity to ensure the query is accurate.
	 * @param groups
	 * 	If an entity belongs in any of the groups in this bitset, it may be
	 * 	sent to the callback.
	 * @param max
	 * 	The max number of entities to find. As soon as this number is reached,
	 * 	searching stops.
	 * @param callback
	 * 	The callback to notify when searching begins, when an entity is found,
	 * 	and when searching has ended.
	 * @return
	 * 	The number of entities found.
	 */
	public int query( Object data, Bound2 query, float buffer, long groups, int max, MapQuery callback )
	{
		int startX = getX( query.left - buffer, true );
		int endX = getX( query.right, false );
		int startY = getY( query.top - buffer, true );
		int endY = getY( query.bottom, false );
		int found = 0;
		
		callback.onStart( data, query, groups, max );
		
		if ( isPartlyOutside( startX, startY, endX, endY ) )
		{
			found = checkIntersections( outsiders, data, query, groups, max, found, callback );
		}
		
		if ( found < max && !isEntirelyOutside( startX, startY, endX, endY ) )
		{
			startX = MathUtil.clamp( startX, 0, width );
			endX = MathUtil.clamp( endX, 0, width );
			startY = MathUtil.clamp( startY, 0, height );
			endY = MathUtil.clamp( endY, 0, height );
			
			for (int y = startY; y < endY; y++)
			{
				for (int x = startX; x < endX; x++)
				{
					found = checkIntersections( nodes[y][x], data, query, groups, max, found, callback );
					
					if (found == max)
					{
						break;
					}
				}
			}
		}
		
		callback.onEnd( data, found );
		
		return found;
	}

	/**
	 * Checks intersections between the items in the linked-list (head) and
	 * the query region.
	 * 
	 * @param head
	 * 	The head of the linked list.
	 * @param data
	 * 	Data that is passed around to the callback. If this data is a 
	 * 	MapEntity, it will be directly notified of entity collisions as well
	 * 	as notifying the callback an entity was found.
	 * @param query
	 * 	The region to find colliding entities in.
	 * @param groups
	 * 	If an entity belongs in any of the groups in this bitset, it may be
	 * 	sent to the callback.
	 * @param max
	 * 	The max number of entities to find. As soon as this number is reached,
	 * 	searching stops.
	 * @param index
	 * 	The current number of entities found.
	 * @param callback
	 * 	The callback to notify when searching begins, when an entity is found,
	 * 	and when searching has ended.
	 * @return
	 * 	The new number of entities found.
	 */
	public int checkIntersections( MapTileNode head, Object data, Bound2 query, long groups, int max, int index, MapQuery callback )
	{
		if ( max == 0 )
		{
			return 0;
		}

		final MapEntity other = ( data instanceof MapEntity ? (MapEntity)data : null );
		
		MapTileNode curr = head.next;
		
		while ( curr != head )
		{
			final MapTileNode next = curr.next;
			final MapEntity e = curr.entity;
			
			if ( query.intersects( e.getBounds() ) )
			{
				if ( (e.getGroups() & groups) != 0 )
				{
					callback.onEntity( data, e, query, groups, index++ );
					
					if ( index == max )
					{
						break;
					}
				}
				
				if ( other != null && (e.getCollidesWith() & other.getGroups()) != 0 ) 
				{
					callback.onEntity( e, other, query, groups, index++ );
					
					if ( index == max )
					{
						break;
					}
				}
			}
			
			curr = next;
		}
		
		return index;
	}

	
	/**
	 * Updates the node of the entity based on the upper-left corner. If the
	 * upper-left corner lies on a tile, it's added to that tiles linked-list.
	 * If the upper-left corner does not lie on a tile, then it's added to the
	 * list of outsiders.
	 * 
	 * @param e
	 * 	The entity to update the node of.
	 */
	public void updateNode( final MapEntity e )
	{
		final Bound2 b = e.getBounds();
		final MapTileNode n = e.getNode();
		
		int x = getX( b.left, true );
		int y = getY( b.top, true );
		
		if ( isOutside( x, y ) )
		{
			x = y = -1;
		}
		
		if ( x != n.x || y != n.y )
		{
			n.remove();
			
			if ( x == -1 )
			{
				n.insertAfter( outsiders );
			}
			else
			{
				n.insertAfter( nodes[y][x] );
			}
		}
	}
	
	/**
	 * Determines whether an entity with the given groups can collide with
	 * the side of a tile.
	 * 
	 * @param x
	 * 	The x index (column) of the tile.
	 * @param y
	 * 	The y index (row) of the tile.
	 * @param side
	 * 	The index of the side to check.
	 * @param entityGroups
	 * 	The groups the entity belongs to.
	 * @return
	 * 	True if the entity could collide with the side of the tile, otherwise
	 * 	false. This process takes into account sides that are against tiles
	 * 	with the same groups.
	 */
	public boolean isSideCollidable( int x, int y, int side, long entityGroups )
	{
		return (entityGroups & getSideGroups( x, y, side )) != 0;
	}
	
	/**
	 * Returns whether anything can collide with the side of the tile.
	 * 
	 * @param x
	 * 	The x index (column) of the tile.
	 * @param y
	 * 	The y index (row) of the tile.
	 * @param side
	 * 	The index of the side to check.
	 * @return
	 * 	True if the side can be collided on, otherwise false.
	 */
	public boolean isSideCollidable( int x, int y, int side )
	{
		return getSideGroups( x, y, side ) != 0;
	}
	
	/**
	 * Calculates the groups that can intersect with the tile on the given side.
	 * This checks the neighboring tile as well to remove any similar groups.
	 * This magic process ensures that sides that are covered by another tile
	 * are ignored by collision detection.
	 * 
	 * @param x
	 * 	The x index (column) of the tile.
	 * @param y
	 * 	The y index (row) of the tile.
	 * @param side
	 * 	The index of the side to check.
	 * @return
	 * 	The calculated groups. Essentially all groups in the given tile that
	 * 	are not in the neighboring tile (designated by the side).
	 */
	public long getSideGroups( int x, int y, int side )
	{
		final Index2 normal = MapConstants.NORMAL[side];
		final long sideGroups = getTileSide( x, y );
		final long neighborGroups = getTileSide( x + normal.x, y + normal.y );

		return (sideGroups ^ (sideGroups & neighborGroups));
	}

	/**
	 * Returns the collision group of the tile, or zero if no tile exists with
	 * the given index.
	 * 
	 * @param x
	 * 	The x index (column) of the tile.
	 * @param y
	 * 	The y index (row) of the tile.
	 * @return
	 * 	The collision groups of the tile, or zero if no tile exists.
	 */
	public long getTileSide( int x, int y )
	{
		return isOutside( x, y ) ? 0 : getTile( x, y ).getCollisionGroup();
	}
	
	/**
	 * Calculates the column of a tile given a coordinate on the map. If the
	 * given coordinate lies off the map the returned value may be negative
	 * or greater than the dimensions of the map.
	 * 
	 * @param x
	 * 	The x-coordinate on the map.
	 * @param floor	
	 *		Whether the index returned should be floored (rounded down) or ceiling
	 *		(rounded up).
	 * @return
	 * 	The index of the tile.
	 */
	public int getX(float x, boolean floor)
	{
		final float i = (x - tileOffset.x) / tileSize.x; 
		
		return (int)(floor ? Math.floor( i ) : Math.ceil( i ));
	}
	
	/**
	 * Calculates the row of a tile given a coordinate on the map. If the given
	 * coordinate lies off the map the returned value may be negative or greater
	 * than the dimensions of the map.
	 * 
	 * @param y
	 * 	The y-coordinate on the map.
	 * @param floor	
	 *		Whether the index returned should be floored (rounded down) or ceiling
	 *		(rounded up).
	 * @return
	 * 	The index of the tile.
	 */
	public int getY(float y, boolean floor)
	{
		final float i = (y - tileOffset.y) / tileSize.y;

		return (int)(floor ? Math.floor( i ) : Math.ceil( i ));
	}
	
	/**
	 * Determines whether the tile with the given index is outside of the map.
	 * 
	 * @param x
	 * 	The x index (column) of the tile.
	 * @param y
	 * 	The y index (row) of the tile.
	 * @return
	 * 	True if the index is outside the map, otherwise false.
	 */
	public boolean isOutside( int x, int y )
	{
		return (x < 0 || x >= width || y < 0 || y >= height);
	}
	
	/**
	 * Determines whether the rectangle of tiles between the first point (x0,y0)
	 * and the second point (x1,y1) are entirely outside of the map. The second
	 * point is expected to have larger or equal values to the first point.
	 * 
	 * @param x0
	 * 	The x index (column) of the first point. 
	 * @param y0
	 * 	The y index (row) of the first point.
	 * @param x1
	 * 	The x index (column) of the second point.
	 * @param y1
	 * 	The y index (row) of the second point.
	 * @return
	 * 	True if the rectangle of tiles is entirely outside of the map, 
	 * 	otherwise false.
	 */
	public boolean isEntirelyOutside( int x0, int y0, int x1, int y1 )
	{
		if ( ( x0 < 0 && x1 < 0 ) || ( x0 >= width && x1 >= width ) )
		{
			return true;
		}

		if ( ( y0 < 0 && y1 < 0 ) || ( y0 >= height && y1 >= height ) )
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Determines whether the rectangle of tiles between the first point (x0,y0)
	 * and the second point (x1,y1) are partially outside of the map. The second
	 * point is expected to have larger or equal values to the first point.
	 * 
	 * @param x0
	 * 	The x index (column) of the first point. 
	 * @param y0
	 * 	The y index (row) of the first point.
	 * @param x1
	 * 	The x index (column) of the second point.
	 * @param y1
	 * 	The y index (row) of the second point.
	 * @return
	 * 	True if the rectangle of tiles is partially outside of the map, 
	 * 	otherwise false.
	 */
	public boolean isPartlyOutside( int x0, int y0, int x1, int y1 )
	{
		return ( x0 < 0 || y0 < 0 || x1 >= width || y1 >= height );
	}
	
	/**
	 * Returns the tile at the given index.
	 * 
	 * @param x
	 * 	The x index (column) of the tile.
	 * @param y
	 * 	The y index (row) of the tile.
	 * @return
	 * 	The reference to the tile at the given index.
	 */
	public MapTile getTile( int x, int y )
	{
		return tiles[ data[y][x] ];
	}
	
	/**
	 * Calculates the real world bounds of a tile at a given index.
	 * 
	 * @param x
	 * 	The x index (column) of the tile.
	 * @param y
	 * 	The y index (row) of the tile.
	 * @param out
	 * 	The bounds to set and return.
	 * @return
	 * 	The reference to the bounds passed in (out).
	 */
	public Bound2 getTileBounds( int x, int y, Bound2 out )
	{
		return getTile( x, y ).getBounds( this, x, y, out );
	}

}
