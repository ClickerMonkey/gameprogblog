package com.gameprogblog.engine.map;

import com.gameprogblog.engine.core.Bound2;


public class MapQueryLast implements MapQuery
{
	
	public MapEntity result;

	@Override
	public void onStart( Object data, Bound2 query, long groups, int max )
	{
		result = null;
	}

	@Override
	public void onEntity( Object data, MapEntity found, Bound2 query, long groups, int foundIndex )
	{
		result = found;
	}

	@Override
	public void onEnd( Object data, int totalFound )
	{
		
	}

}
