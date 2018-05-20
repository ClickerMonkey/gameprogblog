package com.gameprogblog.engine.core;

import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Iterator;

import com.gameprogblog.engine.GameState;


public class EntityList<E extends Entity> implements Entity, Iterable<E>
{
	public static int DEFAULT_CAPACITY = 16;
	
	protected E[] entities;
	protected int size;
	protected boolean expired;
	protected float shrinkPercent;
	protected float shrinkReadyTime;
	protected float shrinkTime;
	protected final EntityListIterator iterator = new EntityListIterator();
	
	public EntityList()
	{
		this( DEFAULT_CAPACITY );
	}
	
	public EntityList( int initialCapacity )
	{
		this( (E[]) new Entity[ initialCapacity ] );
	}
	
	public EntityList( E[] entities )
	{
		this.entities = entities;
	}
	
	/**
	 * Called after an entity has been updated and has not expired.
	 * 
	 * @param entity
	 * 	The entity updated.
	 * @param state
	 * 	The state of the game.
	 * @param index
	 * 	The index the entity is at in the array.
	 */
	protected void onUpdated( E entity, GameState state, int index )
	{
		
	}
	
	/**
	 * Called after an entity has had it's {@link Entity#onExpire()} method 
	 * called for either expiring or this list expiring.
	 * 
	 * @param entity
	 * 	The entity that expired.
	 */
	protected void onExpired( E entity )
	{
		
	}
	
	/**
	 * Called when an entity has been added to this list.
	 * 
	 * @param entity
	 * 	The entity added.
	 * @param index
	 * 	The index the entity was added to.
	 */
	protected void onAdd( E entity, int index )
	{
		
	}
	
	@Override
	public void draw( GameState state, Graphics2D gr )
	{
		for ( int i = 0; i < size; i++ )
		{
			entities[i].draw( state, gr );
		}
	}
	
	@Override
	public void update( GameState state )
	{
		int alive = 0;
		
		for ( int i = 0; i < size; i++ )
		{
			E entity = entities[i];
			
			// Don’t want to update an expired entity! This check could be removed if you don’t care.
			if ( !entity.isExpired() )
			{
				entity.update( state );	
			}
			
			if ( entity.isExpired() )
			{
				// Call the “deconstructor” for the entity
				entity.onExpire();
				
				// Notify extending class that entity has expired
				onExpired( entity );
			}
			else
			{
				// Keep the live entity in the array. As entities expire they leave 
				// gaps, the live	entities will be copied back to take their space.
				entities[alive] = entity;
				
				// Notify extending class that an entity has been updated.
				onUpdated( entity, state, alive );
				
				alive++;
			}
		}
		
		// Clear the spaces between the last live entity and the previous size of 
		// the list so we don’t have any dangling references to objects the GC 
		// might want to collect.
		while ( size > alive )
		{
			entities[--size] = null;
		}
		
		handleShrinking( state );
	}
	
	private void handleShrinking( GameState state )
	{
		// If the number of live entities has been below "shrinkPercent" relative
		// the the length of the array, and it's been below that percent for
		// "shrinkReadyTime" in seconds: Shrink the array!
		
		int shrinkLength = (int)(shrinkPercent * entities.length);
		
		if ( size < shrinkLength )
		{
			shrinkTime += state.seconds;
			
			if ( shrinkTime >= shrinkReadyTime )
			{
				entities = Arrays.copyOf( entities, shrinkLength );
				shrinkTime = 0;
			}
		}
		else
		{
			shrinkTime = 0;
		}
	}
	
	/**
	 * Sets the auto-shrinking feature of the EntityList. If this is not called
	 * then shrinking is disabled. To disable shrinking pass in zeros for both
	 * arguments.
	 * 
	 * @param shrinkPercent
	 * 	If the list maintains less than this percent of live entities relative 
	 * 	to the size of the backing array, it may be applicable for shrinking.
	 * @param shrinkReadyTime
	 * 	If the list was applicable for shrinking based on the percent for this
	 * 	given amount of time in seconds, the array will be shrinked. 
	 */
	public void setAutoShrink( float shrinkPercent, float shrinkReadyTime )
	{
		this.shrinkPercent = shrinkPercent;
		this.shrinkReadyTime = shrinkReadyTime;
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
		for ( int i = 0; i < size; i++ )
		{
			E entity = entities[i];
			
			// Notify entity, extending class, and clear from array.
			entity.onExpire();
			
			onExpired( entity );
			
			entities[i] = null;
		}
		
		size = 0;
	}
	
	/**
	 * Expires and Removes all Entities from this list.
	 */
	public void clear()
	{
		// Simple as calling onExpire!
		onExpire();
	}
	
	/**
	 * @return
	 * 	The number of live entities in this list.
	 */
	public int size()
	{
		return size;
	}
	
	/**
	 * @return
	 * 	The number of entities this list can store before the internal 
	 * 	structure needs to be resized.
	 */
	public int capacity()
	{
		return entities.length;
	}
	
	/**
	 * @return
	 * 	The number of additions left in the list before the internal structure
	 * 	needs to be resized.
	 */
	public int available()
	{
		return (entities.length - size);
	}
	
	/**
	 * Prepares the EntityList for adding the given number of Entities to it.
	 * 
	 * @param count
	 * 	The number of entities about to be added.
	 */
	public void pad( int count )
	{
		final int capacity = entities.length;
		
		if ( size + count >= capacity )
		{
			int nextSize = capacity + (capacity >> 1);
			int minimumSize = size + count;
			
			entities = Arrays.copyOf( entities, Math.max( nextSize, minimumSize ) );
		}
	}
	
	/**
	 * Adds entities from the given array into this EntityList. If any null
	 * values exist in the extraction array of the given array, 
	 * NullPointerExceptions will be thrown by the extending class or the next 
	 * time {@link #update(GameState)} is called.
	 * 
	 * @param <T>
	 * 	The type of entity being added into this EntityList.
	 * @param entityArray
	 * 	The array of entities to add.
	 * @param offset
	 * 	The offset of entities in the given array to start adding from.
	 * @param length
	 * 	The number of entities starting from the offset to add from the array
	 * 	and into this EntityList.
	 */
	public <T extends E> void add(T[] entityArray, int offset, int length)
	{
		pad( length );
		
		System.arraycopy( entityArray, offset, entities, size, length );
		
		for ( int i = 0; i < length; i++ )
		{
			onAdd( entityArray[offset + i], size++ );
		}
	}

	/**
	 * Adds all entities from the given array into this EntityList. If any null
	 * values exist in the given array, NullPointerExceptions will be thrown
	 * by the extending class or the next time {@link #update(GameState)} is 
	 * called.
	 * 
	 * @param <T>
	 * 	The type of entity being added into this EntityList.
	 * @param entityArray
	 * 	The array of entities to add.
	 */
	public <T extends E> void add(T[] entityArray)
	{
		add( entityArray, 0, entityArray.length );
	}
	
	/**
	 * Adds all entities from the given EntityList into this EntityList. This
	 * should carefully be done, you don't want an Entity being updated and
	 * draw twice. Typically the EntityList passed into here is about to be
	 * discarded.
	 * 
	 * @param <T>
	 * 	The type of entity being added into this EntityList.
	 * @param entityList
	 * 	The list of entities to add.
	 */
	public <T extends E> void add(EntityList<T> entityList)
	{
		add( entityList.entities, 0, entityList.size );
	}

	/**
	 * Adds a single entity to this List.
	 * 
	 * @param entity
	 * 	The entity to add.
	 */
	public void add(E entity)
	{
		pad( 1 );

		entities[size] = entity;
		
		onAdd( entity, size );
		
		size++;
	}
	
	/**
	 * Returns the entity at the given index.
	 * 
	 * @param index
	 * 	The index of the entity, >= 0 and < {@link #size()}.
	 * @return
	 * 	The entity at the given index, possibly null if none exists.
	 * @throws NullPointerException
	 * 	An entity doesn't exist at the given index.
	 */
	public E get( int index )
	{
		return entities[ index ];
	}

	@Override
	public Iterator<E> iterator()
	{
		// If the cached iterator is currently being used, but another is 
		// required - create a new one. This will create garbage that needs to be
		// collected so avoid looping over this list while the current iterator
		// has not finished.
		return ( iterator.hasNext() ? new EntityListIterator() : iterator.reset() );
	}
	
	/**
	 * A simple reset-able Iterator.
	 * 
	 * @author Philip Diffenderfer
	 *
	 */
	private class EntityListIterator implements Iterator<E>
	{
		public int index;

		public EntityListIterator reset()
		{
			index = 0;
			return this;
		}
		
		@Override
		public boolean hasNext()
		{
			return (index < size);
		}

		@Override
		public E next()
		{
			return entities[index++];
		}

		@Override
		public void remove()
		{
			entities[index - 1].expire();
		}
	}
	
}
