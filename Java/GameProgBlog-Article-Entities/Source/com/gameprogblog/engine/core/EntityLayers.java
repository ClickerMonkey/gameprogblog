package com.gameprogblog.engine.core;

import java.awt.Graphics2D;

import com.gameprogblog.engine.GameState;


public class EntityLayers implements Entity
{

	private final EntityLayer[] layers;
	private boolean expired;
	
	public <E extends Enum<E>> EntityLayers( Class<E> layerEnum )
	{
		this( layerEnum.getEnumConstants().length );
	}
	
	public EntityLayers( int layerCount )
	{
		layers = new EntityLayer[ layerCount ];
		
		for ( int i = 0; i < layerCount; i++ )
		{
			layers[i] = new EntityLayer( i );
		}
	}

	@Override
	public void draw( GameState state, Graphics2D gr )
	{
		for ( int i = 0; i < layers.length; i++ )
		{
			layers[i].draw( state, gr );
		}
	}

	@Override
	public void update( GameState state )
	{
		for ( int i = 0; i < layers.length; i++ )
		{
			layers[i].update( state );
		}
	}

	@Override
	public boolean isExpired()
	{
		return expired;
	}

	@Override
	public void expire()
	{
		for ( int i = 0; i < layers.length; i++ )
		{
			layers[i].expire();
		}
		
		expired = true;
	}

	@Override
	public void onExpire()
	{
		for ( int i = 0; i < layers.length; i++ )
		{
			layers[i].onExpire();	
		}
	}
	
	public void add( int index, Entity e )
	{
		layers[ index ].add( e );
	}
	
	public void add( Enum<?> index, Entity e )
	{
		layers[ index.ordinal() ].add( e );
	}
	
	public EntityLayer getLayer( int index )
	{
		return layers[ index ];
	}
	
	public EntityLayer getLayer( Enum<?> index )
	{
		return layers[ index.ordinal() ];
	}
	
}
