package com.gameprogblog.engine.ui;

import java.util.Arrays;

import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.util.ArrayUtil;


public class Container extends Control
{

	protected Layout layout;
	protected Control[] children = {};
	
	protected void onChildRemove(Control child) 
	{
	}
	
	protected void onChildFocus(Control child) 
	{ 
	}
	
	protected void onChildBlur(Control child) 
	{ 
	}
	
	protected void onDraw(InterfaceRenderer renderer, Bound2 inset)
	{
		renderer.setClip( inset, true );
		
		for(int i = 0; i < children.length; i++)
		{
			if (children[i].clipped)
			{
				children[i].draw( renderer );	
			}
		}
		
		renderer.popClip();
		
		for(int i = 0; i < children.length; i++)
		{
			if (!children[i].clipped)
			{
				children[i].draw( renderer );	
			}
		}
	}
	
	public Control getChildAt(Relative relative, float x, float y)
	{
		for (int i = 0; i < children.length; i++)
		{
			if ( children[i].isMouseOver( relative, x, y ) )
			{
				return children[i];
			}
		}
		
		return null;
	}
	
	public boolean remove(Control child)
	{
		boolean removed = child.getParent() == this;
		
		if (removed)
		{
			int index = child.getChildIndex();
			
			layout.onChildRemove( child, index );
			
			while (index + 1 < children.length)
			{
				Control next = children[index + 1];
				
				layout.onChildMoved( next, index + 1, index );
				
				next.setChildIndex( index );
				
				children[index++] = next;
			}
			
			children = Arrays.copyOf( children, children.length - 1 );
		}
		
		return removed;
	}
	
	public boolean add(Control child)
	{
		if (child.getParent() == this)
		{
			return true;
		}
		
		child.detach();
		
		boolean added = layout.onChildAdd( child, children.length ); 
		
		if ( added )
		{
			child.setParent( this );
			child.setChildIndex( children.length );
			children = ArrayUtil.add( child, children );
		}
		
		return added;
	}
	
	public void setLayout(Layout layout)
	{
		this.layout = layout;
		
		layout.setParent( this );
		
		for (int i = 0; i < children.length; i++)
		{
			layout.onChildAdd( children[i], i );
		}
	}
	
	public void layout()
	{
		layout.onLayout();
	}
	
}
