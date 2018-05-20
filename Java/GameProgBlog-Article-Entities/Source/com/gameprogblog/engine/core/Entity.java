package com.gameprogblog.engine.core;

import java.awt.Graphics2D;

import com.gameprogblog.engine.GameState;


/**
 * Anything in a game that could be drawn, is updated, and can expire.
 * 
 * @author Philip Diffenderfer
 *
 */
public interface Entity
{
	/**
	 * Draws the Entity to the graphics object.
	 * 
	 * @param state
	 * 		The state of the game which contains interpolation information.
	 * @param gr
	 * 		The graphics object to draw to.
	 */
	public void draw( GameState state, Graphics2D gr );
	
	/**
	 * Updates the entity.
	 * 
	 * @param state
	 * 		The state of the game and the amount of elapsed time since the last
	 * 		update.
	 */
	public void update( GameState state );
	
	/**
	 * Determines whether this entity has expired. An expired entity is removed
	 * from it's container.
	 * 
	 * @return
	 * 	True if the entity is expired and is ready to be removed, otherwise 
	 * 	false.
	 */
	public boolean isExpired();
	
	/**
	 * Forcefully expires the entity. This may be called several times before the
	 * container of the Entity finally removes it and calls {@link #onExpire()}.
	 */
	public void expire();
	
	/**
	 * The method called when an Entity is removed from it's container. This is
	 * essentially the "deconstructor" for the Entity and will only be called 
	 * once.
	 */
	public void onExpire();
	
}
