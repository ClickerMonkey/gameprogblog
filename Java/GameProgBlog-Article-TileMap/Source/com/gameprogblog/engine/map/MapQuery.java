package com.gameprogblog.engine.map;

import com.gameprogblog.engine.core.Bound2;


public interface MapQuery
{
	public void onStart( Object data, Bound2 query, long groups, int max );
	public void onEntity( Object data, MapEntity found, Bound2 query, long groups, int foundIndex );
	public void onEnd( Object data, int totalFound );
}
