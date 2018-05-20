package com.gameprogblog.engine.map;

import java.util.Iterator;

import com.gameprogblog.engine.core.Bound2;


public class MapQueryArray implements MapQuery, Iterable<MapEntity>, Iterator<MapEntity>
{
	
	public MapEntity[] results;
	public int total;
	public int index;
	
	public MapQueryArray(int capacity)
	{
		results = new MapEntity[ capacity ];
	}
	
	@Override
	public void onStart( Object data, Bound2 query, long groups, int max )
	{
		while (total > 0)
		{
			results[--total] = null;
		}
	}

	@Override
	public void onEntity( Object data, MapEntity found, Bound2 query, long groups, int foundIndex )
	{
		if ( foundIndex < results.length )
		{
			results[foundIndex] = found;
		}
	}

	@Override
	public void onEnd( Object data, int totalFound )
	{
		total = Math.min( totalFound, results.length );
	}

	@Override
	public boolean hasNext()
	{
		return (index < total);
	}

	@Override
	public MapEntity next()
	{
		return results[index++];
	}

	@Override
	public void remove()
	{
		results[index - 1].expire();
	}

	@Override
	public Iterator<MapEntity> iterator()
	{
		index = 0;
		
		return this;
	}

}
