
package com.gameprogblog.engine.map;

import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.core.Index2;
import com.gameprogblog.engine.core.Vector2;
import com.gameprogblog.engine.util.MathUtil;

/**
 * A map entity with physics is expected to use the methods in this class in
 * the given order, italicized items are optional.
 * 
 * <ol>
 * <li><b>save last position if necessary</b></li>
 * <li>physics.reset()</li>
 * <li><b>calculate acceleration</b></li>
 * <li>physics.updateVelocity(...)</li>
 * <li><b>modify velocity</b></li>
 * <li><i>physics.applyTerminalVelocity(...)</i></li>
 * <li><i>physics.applyZeroVelocityThreshold(...)</i></li>errrrrrrrrrrrrrrrrr
 * <li>physics.handleBlocks(...)</li>
 * <li>physics.updatePositionAndNode(...)</li>
 * <li><i>physics.applyFriction(...)</i></li>
 * <li><i>physics.applyRestitution(...)</i></li>
 * </ol>
 * 
 * @author Philip Diffenderfer
 *
 */
public class MapEntityPhysics
{

	public final MapEntity entity;
	public final MapTile[] tiles = new MapTile[MapConstants.SIDES];
	public final Index2[] index = { new Index2(), new Index2(), new Index2(), new Index2() };
	public final float[] overlap = new float[MapConstants.SIDES];
	public final boolean[] touching = new boolean[MapConstants.SIDES];
	public final Bound2 union = new Bound2();
	public final Bound2 futureBounds = new Bound2();
	public final Vector2 futurePosition = new Vector2();

	public MapEntityPhysics( MapEntity entity )
	{
		this.entity = entity;
	}

	/**
	 * Resets the previous frames tile intersections.
	 */
	public void reset()
	{
		for (int i = 0; i < MapConstants.SIDES; i++)
		{
			tiles[i] = null;
			index[i].set( -1, -1 );
			overlap[i] = 0;
			touching[i] = false;
		}
	}

	/**
	 * Updates the velocity of the entity by adding acceleration to it.
	 * 
	 * @param dt
	 * 	The amount of time in seconds that has elapsed since last update.
	 */
	public void updateVelocity( float dt )
	{
		final Vector2 vel = entity.getVelocity();
		final Vector2 acc = entity.getAcceleration();

		vel.add( acc, dt );
	}

	/**
	 * Ensures the velocity along the x and y axis do not exceed the given 
	 * terminal velocity.
	 * 
	 * @param terminal
	 * 	The largest possible value along any axis the velocity may be.
	 */
	public void applyTerminalVelocitySquare( float terminal )
	{
		final Vector2 vel = entity.getVelocity();

		vel.x = MathUtil.clamp( vel.x, -terminal, terminal );
		vel.y = MathUtil.clamp( vel.y, -terminal, terminal );
	}

	/**
	 * Ensures the velocity along the x and y axis do not exceed the given
	 * terminal velocity.
	 * 
	 * @param terminal
	 * 	The largest possible value along each axis the velocity may be.
	 */
	public void applyTerminalVelocitySquare( Vector2 terminal )
	{
		final Vector2 vel = entity.getVelocity();

		vel.x = MathUtil.clamp( vel.x, -terminal.x, terminal.x );
		vel.y = MathUtil.clamp( vel.y, -terminal.y, terminal.y );
	}

	/**
	 * Ensures the magnitude of the velocity does not exceed the given amount.
	 * 
	 * @param terminal
	 * 	The largest possible magnitude the velocity may be.
	 */
	public void applyTerminalVelocityRadial( float terminal )
	{
		final Vector2 vel = entity.getVelocity();

		vel.clamp( 0, terminal );
	}

	/**
	 * Ensures the velocity along the x and y axis does not exceed the given 
	 * terminal velocity.
	 * 
	 * @param terminal
	 * 	The largest possible value along each axis in both directions the
	 * 	velocity may be.
	 */
	public void applyTerminalVelocity( Bound2 terminal )
	{
		final Vector2 vel = entity.getVelocity();

		vel.x = MathUtil.clamp( vel.x, terminal.left, terminal.right );
		vel.y = MathUtil.clamp( vel.y, terminal.bottom, terminal.top );
	}

	/**
	 * Ensures the velocity along the given up and right directions does not
	 * exceed the given terminal velocity.
	 * 
	 * @param terminal
	 * 	The largest possible value along each axis in all directions the
	 * 	velocity may be.
	 * @param up
	 * 	The y axis, pointing to the top of the entity.
	 * @param right
	 * 	The x axis, pointing to the right of the entity.
	 */
	public void applyTerminalVelocity( Bound2 terminal, Vector2 up, Vector2 right )
	{
		// TODO clampOnAxis
	}

	/**
	 * Sets the velocity to exactly zero if the velocity on both axis are within
	 * the given threshold to zero.
	 * 
	 * @param threshold
	 * 	The number that dictates when an entity is considered stopped.
	 */
	public void applyZeroVelocityThreshold( float threshold )
	{
		final Vector2 vel = entity.getVelocity();

		float ax = Math.abs( vel.x );
		float ay = Math.abs( vel.y );

		if (ax < threshold && ay < threshold)
		{
			vel.set( 0, 0 );
		}
	}

	/**
	 * Handles tile collisions with the map.
	 * 
	 * @param map
	 * 	The map to check collisions against.
	 * @param dt
	 * 	The amount of time in seconds that has elapsed since last update.
	 */
	public void handleBlocks( Map map, float dt )
	{
		final Vector2 pos = entity.getPosition();
		final Vector2 vel = entity.getVelocity();
		final Bound2 bounds = entity.getBounds();
		final Bound2 extents = entity.getExtents();

		// Update bounds based on position
		bounds.set( pos, extents );

		// Calculate future position and bounds
		futurePosition.set( pos );
		futurePosition.add( vel, dt );
		futureBounds.set( futurePosition, extents );

		// Iterate over all tiles below the union of the past and future bounds.
		union.set( bounds );
		union.union( futureBounds );

		// Calculate the tile indices under the union.
		int startX = map.getX( union.left, true );
		int endX = map.getX( union.right, false );
		int startY = map.getY( union.top, true );
		int endY = map.getY( union.bottom, false );

		// Entity is off the map, skip update.
		if (map.isEntirelyOutside( startX, startY, endX, endY ))
		{
			return;
		}

		// Clamp values so only tiles on the map are checked.
		startX = MathUtil.clamp( startX, 0, map.width );
		endX = MathUtil.clamp( endX, 0, map.width );
		startY = MathUtil.clamp( startY, 0, map.height );
		endY = MathUtil.clamp( endY, 0, map.height );

		// Iterate over all tiles and capture all tiles colliding with entity
		for (int y = startY; y < endY; y++)
		{
			for (int x = startX; x < endX; x++)
			{
				handleEntityTileIntersection( map, x, y );
			}
		}
	}

	/**
	 * Handles intersection between the entity and a single tile.
	 * 
	 * @param map
	 * 	The map to check collisions against.
	 * @param x
	 * 	The x index (column) of the tile.
	 * @param y
	 * 	The y index (row) of the tile.
	 */
	protected void handleEntityTileIntersection( Map map, int x, int y )
	{
		final Bound2 bounds = entity.getBounds();
		final MapTile tile = map.tiles[map.data[y][x]];
		final Bound2 tileBounds = map.getTileBounds( x, y, new Bound2() );

		if (!union.intersects( tileBounds ))
		{
			return;
		}
		
		int tileSides = 0;
		int entitySides = 0;

		// Check for intersection on tile left, entity right.
		if (futureBounds.right > tileBounds.left && bounds.right <= tileBounds.left && isSideCollidable( x, y, map, MapConstants.LEFT ))
		{
			tileSides |= MapConstants.BIT_LEFT;
			entitySides |= MapConstants.BIT_RIGHT;
			touch( x, y, MapConstants.RIGHT, futureBounds.right - tileBounds.left, tile );
			futureBounds.moveRight( tileBounds.left );
		}
		// Check for intersection on tile right, entity left.
		else if (futureBounds.left < tileBounds.right && bounds.left >= tileBounds.right && isSideCollidable( x, y, map, MapConstants.RIGHT ))
		{
			tileSides |= MapConstants.BIT_RIGHT;
			entitySides |= MapConstants.BIT_LEFT;
			touch( x, y, MapConstants.LEFT, tileBounds.right - futureBounds.left, tile );
			futureBounds.moveLeft( tileBounds.right );
		}

		// Check for intersection on tile top, entity bottom.
		if (futureBounds.bottom > tileBounds.top && bounds.bottom <= tileBounds.top && isSideCollidable( x, y, map, MapConstants.TOP ) )
		{
			tileSides |= MapConstants.BIT_TOP;
			entitySides |= MapConstants.BIT_BOTTOM;
			touch( x, y, MapConstants.BOTTOM, futureBounds.bottom - tileBounds.top, tile );
			futureBounds.moveBottom( tileBounds.top );
		}
		// Check for intersection on tile bottom, entity top.
		else if (futureBounds.top < tileBounds.bottom && bounds.top >= tileBounds.bottom && isSideCollidable( x, y, map, MapConstants.BOTTOM ))
		{
			tileSides |= MapConstants.BIT_BOTTOM;
			entitySides |= MapConstants.BIT_TOP;
			touch( x, y, MapConstants.TOP, tileBounds.bottom - futureBounds.top, tile );
			futureBounds.moveTop( tileBounds.bottom );
		}

		// Any intersections, notify entity and tile.
		if (tileSides != 0)
		{
			tile.onEntityCollide( map, x, y, entity, tileSides );
			entity.onMapCollide( map, x, y, tile, entitySides );
		}
	}

	/**
	 * Determines whether the given side of the given tile can collide with this
	 * entity.
	 * 
	 * @param x
	 * 	The x index (column) of the tile.
	 * @param y
	 * 	The y index (row) of the tile.
	 * @param map
	 * 	The map to check collisions against.
	 * @param side
	 * 	The side on the tile.
	 * @return
	 * 	True if the entity can collide with this side on the tile.
	 */
	protected boolean isSideCollidable( int x, int y, Map map, int side )
	{
		return map.isSideCollidable( x, y, side, entity.getCollidesWith() );
	}

	/**
	 * Marks the given tile as intersecting with the entity.
	 * 
	 * @param tileX
	 * 	The x index (column) of the tile.
	 * @param tileY
	 * 	The y index (row) of the tile.
	 * @param side
	 * 	The side of collision.
	 * @param tileOverlap
	 * 	The amount of overlap.
	 * @param tile
	 * 	The tile that was collided with.
	 */
	protected void touch( int tileX, int tileY, int side, float tileOverlap, MapTile tile )
	{
		if (!touching[side] || tileOverlap > overlap[side])
		{
			touching[side] = true;
			index[side].set( tileX, tileY );
			overlap[side] = Math.max( overlap[side], tileOverlap );
			tiles[side] = tile;
		}
	}

	/**
	 * Updates the position, bounds, and node of the entity given the map.
	 * The bounds of the entity is set to the calculated and adjusted future 
	 * bounds. The position is then derived from the final bounds.
	 * 
	 * @param map
	 * 	The map that can update the entity's node.
	 */
	public void updatePositionAndNode( Map map )
	{
		final Vector2 pos = entity.getPosition();
		final Bound2 bounds = entity.getBounds();
		final Bound2 extents = entity.getExtents();

		bounds.set( futureBounds );
		pos.x = bounds.left + extents.left;
		pos.y = bounds.top + extents.top;

		map.updateNode( entity );
	}

	/**
	 * Applies friction to the given sides if they currently have intersected
	 * with tiles. If no friction is applied to an axis, airFriction is applied.
	 * 
	 * @param sides
	 * 	The sides which can have friction applied.
	 * @param airFriction
	 * 	The friction to use when the sides are not colliding with tile.
	 * @param friction
	 * 	The friction to use when the sides are colliding with tile.
	 */
	public void applyFriction( int sides, float airFriction, Bound2 friction )
	{
		if ( sides == 0 )
		{
			return;
		}
		
		final Vector2 vel = entity.getVelocity();

		vel.x -= vel.x * getFriction( sides, airFriction, friction, MapConstants.TOP, MapConstants.BOTTOM );
		vel.y -= vel.y * getFriction( sides, airFriction, friction, MapConstants.LEFT, MapConstants.RIGHT );
	}

	/**
	 * Applies friction to the given sides if they currently have intersected
	 * with tiles. If no friction is applied to an axis, airFriction is applied.
	 * 
	 * @param sides
	 * 	The bitset of sides that can have friction applied.
	 * @param airFriction
	 * 	The friction to use when the sides are not colliding with tile.
	 * @param friction
	 * 	The friction to use when the sides are colliding with tile.
	 */
	public void applyFriction( int sides, float airFriction, float friction )
	{
		if ( sides == 0 )
		{
			return;
		}
		
		final Vector2 vel = entity.getVelocity();

		vel.x -= vel.x * getFriction( sides, airFriction, friction, MapConstants.TOP, MapConstants.BOTTOM );
		vel.y -= vel.y * getFriction( sides, airFriction, friction, MapConstants.LEFT, MapConstants.RIGHT );
	}
	
	/**
	 * Calculates the friction of the tile that intersected on two possible
	 * sides (on the same axis). If no friction can be applied to either tile 
	 * than air friction is applied.
	 * 
	 * @param sides
	 * 	The bitset of sides that can have friction applied.
	 * @param airFriction
	 * 	The friction to use when the sides are not colliding with tile.
	 * @param friction
	 * 	The friction to use when the sides are colliding with tile.
	 * @param side1
	 * 	The first side to check.
	 * @param side2
	 * 	The second side to check, on the same axis.
	 * @return
	 * 	The calculated friction.
	 */
	protected float getFriction( int sides, float airFriction, Bound2 friction, int side1, int side2 )
	{
		int s = pickSide( sides, side1, side2, -1 );
		float f = airFriction;
		
		if (s != -1)
		{
			f = tiles[s].getFriction() * getSide( s, friction );
		}
		
		return f;
	}

	/**
	 * Calculates the friction of the tile that intersected on two possible
	 * sides (on the same axis). If no friction can be applied to either tile 
	 * than air friction is applied.
	 * 
	 * @param sides
	 * 	The bitset of sides that can have friction applied.
	 * @param airFriction
	 * 	The friction to use when the sides are not colliding with tile.
	 * @param friction
	 * 	The friction to use when the sides are colliding with tile.
	 * @param side1
	 * 	The first side to check.
	 * @param side2
	 * 	The second side to check, on the same axis.
	 * @return
	 * 	The calculated friction.
	 */
	protected float getFriction( int sides, float airFriction, float friction, int side1, int side2 )
	{
		int s = pickSide( sides, side1, side2, -1 );
		float f = airFriction;
		
		if (s != -1)
		{
			f = tiles[s].getFriction() * friction;
		}
		
		return f;
	}

	/**
	 * Applies restitution (bounce) to the given sides if they currently have 
	 * intersected with tiles. 
	 * 
	 * @param sides
	 * 	The bitset of sides that can have restitution applied.
	 * @param restitution
	 * 	The restitution to use when the sides are colliding with tile.
	 */
	public void applyRestitution( int sides, Bound2 restitution )
	{
		if ( sides == 0 )
		{
			return;
		}
		
		final Vector2 vel = entity.getVelocity();

		vel.x *= -getRestitution( sides, restitution, MapConstants.LEFT, MapConstants.RIGHT );
		vel.y *= -getRestitution( sides, restitution, MapConstants.TOP, MapConstants.BOTTOM );
	}

	/**
	 * Applies restitution (bounce) to the given sides if they currently have 
	 * intersected with tiles. 
	 * 
	 * @param sides
	 * 	The bitset of sides that can have restitution applied.
	 * @param restitution
	 * 	The restitution to use when the sides are colliding with tile.
	 */
	public void applyRestitution( int sides, float restitution )
	{
		if ( sides == 0 )
		{
			return;
		}
		
		final Vector2 vel = entity.getVelocity();

		vel.x *= -getRestitution( sides, restitution, MapConstants.LEFT, MapConstants.RIGHT );
		vel.y *= -getRestitution( sides, restitution, MapConstants.TOP, MapConstants.BOTTOM );
	}

	/**
	 * Calculates the restitution of the tile that intersected on two possible
	 * sides (on the same axis).
	 * 
	 * @param sides
	 * 	The bitset of sides that can have restitution applied.
	 * @param restitution
	 * 	The restitution to use when the sides are colliding with tile.
	 * @param side1
	 * 	The first side to check.
	 * @param side2
	 * 	The second side to check, on the same axis.
	 * @return
	 * 	The calculated restitution.
	 */
	protected float getRestitution( int sides, Bound2 restitution, int side1, int side2 )
	{
		int s = pickSide( sides, side1, side2, -1 );
		float r = -1.0f;
		
		if (s != -1)
		{
			r = tiles[s].getRestitution() + getSide( s, restitution );
		}
		
		return r;
	}

	/**
	 * Calculates the restitution of the tile that intersected on two possible
	 * sides (on the same axis).
	 * 
	 * @param sides
	 * 	The bitset of sides that can have restitution applied.
	 * @param restitution
	 * 	The restitution to use when the sides are colliding with tile.
	 * @param side1
	 * 	The first side to check.
	 * @param side2
	 * 	The second side to check, on the same axis.
	 * @return
	 * 	The calculated restitution.
	 */
	protected float getRestitution( int sides, float restitution, int side1, int side2 )
	{
		int s = pickSide( sides, side1, side2, -1 );
		float r = -1.0f;
		
		if (s != -1)
		{
			r = tiles[s].getRestitution() + restitution;
		}
		
		return r;
	}
	
	/**
	 * Picks a side to use for friction or restitution. The side that is chosen
	 * must exist in the bitset of availableSides, must have been collided with,
	 * and must have the larger overlap when compared to the other side if the
	 * other side has also met those criteria.
	 * 
	 * @param availableSides
	 * 	The bitset of sides that can be picked.
	 * @param side1
	 * 	The first side to check.
	 * @param side2
	 * 	The second side to check, on the same axis.
	 * @param neither
	 * 	The value to return when neither side could be picked (not in the 
	 * 	bitset of availableSides and neither are being touched).
	 * @return
	 * 	The side picked, or neither.
	 */
	protected int pickSide( int availableSides, int side1, int side2, int neither )
	{
		boolean b1 = touching[side1] && ( MapConstants.BITS[side1] & availableSides ) != 0;
		boolean b2 = touching[side2] && ( MapConstants.BITS[side2] & availableSides ) != 0;
		
		if (b1 && b2)
		{
			return (overlap[side1] > overlap[side2] ? side1 : side2);
		}
		else if (b1)
		{
			return side1;
		}
		else if (b2)
		{
			return side2;
		}

		return neither;
	}

	/**
	 * Returns a side of a bounds object.
	 * 
	 * @param side
	 * 	The index of the side.
	 * @param b
	 * 	The bounds object.
	 * @return
	 * 	The side value.
	 */
	protected float getSide( int side, Bound2 b )
	{
		switch (side)
		{
		case MapConstants.LEFT:
			return b.left;
		case MapConstants.TOP:
			return b.top;
		case MapConstants.RIGHT:
			return b.right;
		case MapConstants.BOTTOM:
			return b.bottom;
		}

		return 0f;
	}
	
	/**
	 * Returns true if all sides in the given bitset are being touched.
	 * 
	 * @param sides
	 * 	The bitset of sides to check.
	 * @return
	 * 	True if all sides specified in the bitset are being touched.
	 */
	public boolean isTouching( int sides )
	{
		for (int i = 0; i < MapConstants.SIDES; i++)
		{
			if ((sides & MapConstants.BITS[i]) != 0 && !touching[i])
			{
				return false;
			}
		}

		return true;
	}

}
