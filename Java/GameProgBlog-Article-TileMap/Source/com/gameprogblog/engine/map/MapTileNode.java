package com.gameprogblog.engine.map;


public class MapTileNode
{
	
	public final MapEntity entity;
	public MapTileNode next;
	public MapTileNode prev;
	public int x, y;
	
	public MapTileNode(MapEntity entity, int x, int y)
	{
		this.entity = entity;
		this.next = this;
		this.prev = this;
		this.x = x;
		this.y = y;
	}
	
	public void remove() 
	{
		if (prev != null && next != null) 
		{
			prev.next = next;
			next.prev = prev;
			next = prev = null;	
		}
	}
	
	public void insertAfter( MapTileNode node )
	{
		(next = node.next).prev = this;
		(prev = node).next = this;
		x = node.x;
		y = node.y;
	}
	
	public boolean isOutside()
	{
		return (x == -1 && y == -1);
	}
	
}
