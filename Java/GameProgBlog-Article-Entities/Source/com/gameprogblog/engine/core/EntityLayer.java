
package com.gameprogblog.engine.core;

import java.awt.Graphics2D;

import com.gameprogblog.engine.GameState;

/**
 * A layer of entities. Whether the layer is drawn or updates can be set by the
 * {@link #visible} and {@link #enabled} properties of the layer.
 * 
 * @author Philip Diffenderfer
 *
 */
public class EntityLayer extends EntityList<Entity>
{

	protected final int index;
	protected boolean visible;
	protected boolean enabled;

	public EntityLayer( int index )
	{
		this.index = index;
	}

	@Override
	public void draw( GameState state, Graphics2D gr )
	{
		if ( visible )
		{
			super.draw( state, gr );
		}
	}
	
	@Override
	public void update( GameState state )
	{
		if ( enabled )
		{
			super.update( state );
		}
	}
	
	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible( boolean visible )
	{
		this.visible = visible;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled( boolean enabled )
	{
		this.enabled = enabled;
	}

	public int getIndex()
	{
		return index;
	}

}
