package com.gameprogblog.engine.map;

import java.util.Collection;

import com.gameprogblog.engine.core.Bound2;


public class MapQueryList implements MapQuery
{
	
	public final Collection<MapEntity> results;

	public MapQueryList(Collection<MapEntity> results)
	{
		this.results = results;
	}
	
	@Override
	public void onStart( Object data, Bound2 query, long groups, int max )
	{
		results.clear();
	}

	@Override
	public void onEntity( Object data, MapEntity found, Bound2 query, long groups, int foundIndex )
	{
		results.add( found );
	}

	@Override
	public void onEnd( Object data, int totalFound )
	{
		
	}

}
