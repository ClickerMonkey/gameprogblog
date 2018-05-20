
package com.gameprogblog.engine.ui;

import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.core.Vector2;


public abstract class Control
{

	public final Bound2 margins = new Bound2();
	public final Bound2 padding = new Bound2();
	public final Bound2 bounds = new Bound2();
	public final Vector2 position = new Vector2();
	public final Vector2 size = new Vector2();
	public final Vector2 minimumSize = new Vector2();
	protected final Bound2 lastBounds = new Bound2();

	public int state = States.Normal;
	public float stateTime;
	public int statePrevious = States.Disabled;

	public ControlTheme theme;
	public float red = 1.0f;
	public float green = 1.0f;
	public float blue = 1.0f;
	public float alpha = 1.0f;
	public Font font;

	public boolean visible = true;
	public boolean enabled = true;
	public boolean focusable = true;
	public boolean selectable = false;
	protected boolean mouseover = false;
	protected boolean focused = false;
	protected boolean selected = false;

	public boolean clipped;
	protected Container parent;
	protected int childIndex;
	protected Bound2 inset = new Bound2();

	protected void onFocus()
	{
	}

	protected void onBlur()
	{
	}

	protected void onSelected()
	{
	}

	protected void onUnselected()
	{
	}

	protected boolean onKey( int key, char keyChar )
	{
		return true;
	}

	protected void onMouse( int button, boolean down, float x, float y )
	{
	}

	protected void onMouseLeave( float x, float y )
	{
	}

	protected void onMouseEnter( float x, float y )
	{
	}

	protected void onMouseHover( float x, float y )
	{
	}

	protected void handleMouseMove( float x, float y )
	{
		if (state == States.Disabled)
		{
			return;
		}

		boolean inside = isMouseOver( Relative.Parent, x, y );

		x -= bounds.left;
		y -= bounds.top;

		if (inside)
		{
			if (!mouseover)
			{
				onMouseEnter( x, y );

				if (!focused)
				{
					state = States.Hovering;
				}

				mouseover = true;
			}
		}
		else
		{
			if (mouseover)
			{
				onMouseLeave( x, y );

				if (!focused)
				{
					state = States.Normal;
				}

				mouseover = false;
			}
		}
	}

	protected void handleMouse( int button, boolean down, float x, float y )
	{
		if (state == States.Disabled || !isMouseOver( Relative.Parent, x, y ))
		{
			return;
		}

		x -= bounds.left;
		y -= bounds.top;

		if (button == 0)
		{
			if (down)
			{
				state = States.Pressed;
			}
			else
			{
				state = (focused ? States.Focused : States.Hovering);
			}
		}

		onMouse( button, down, x, y );
	}

	protected void handleKey( int key, char keyChar )
	{
		if (state == States.Disabled)
		{
			return;
		}

		if (onKey( key, keyChar ) && parent != null)
		{
			parent.handleKey( key, keyChar );
		}
	}

	protected void handleFocus()
	{
		if (focusable)
		{
			state = States.Focused;
			focused = true;
			onFocus();

			if (parent != null)
			{
				parent.onChildBlur( this );
			}
		}

		if (selectable)
		{
			if (selected)
			{
				state = States.Normal;
				onUnselected();
			}
			else
			{
				state = States.Selected;
				onSelected();
			}

			selected = !selected;
		}
	}

	protected void handleBlur()
	{
		if (state == States.Focused)
		{
			state = States.Normal;
		}

		focused = false;

		onBlur();

		if (parent != null)
		{
			parent.onChildBlur( this );
		}
	}

	public void update( float dt )
	{
		if (!bounds.isEqual( lastBounds ))
		{
			// TODO

			lastBounds.set( bounds );
		}
	}

	public void draw( InterfaceRenderer renderer )
	{
		if (visible)
		{
			preDraw( renderer, bounds );

			getPadding( Relative.Control, inset );
			onDraw( renderer, inset );

			postDraw( renderer, bounds );
		}
	}

	protected void preDraw( InterfaceRenderer renderer, Bound2 bounds )
	{
		if (red != 1.0f || green != 1.0f || blue != 1.0f)
		{
			renderer.addShade( red, green, blue );
		}

		if (alpha != 1.0f)
		{
			renderer.addAlpha( alpha );
		}

		if (font != null)
		{
			renderer.setFont( font );
		}

		renderer.addOrigin( bounds.left, bounds.top );

		theme.start( renderer, this, bounds );
	}

	protected abstract void onDraw( InterfaceRenderer renderer, Bound2 inset );

	protected void postDraw( InterfaceRenderer renderer, Bound2 bounds )
	{
		theme.end( renderer, this, bounds );

		renderer.popOrigin();

		if (font != null)
		{
			renderer.popFont();
		}

		if (alpha != 1.0f)
		{
			renderer.popAlpha();
		}
		
		if (red != 1.0f || green != 1.0f || blue != 1.0f)
		{
			renderer.popShade();
		}
	}

	protected void setParent( Container parent )
	{
		this.parent = parent;
	}

	protected void setChildIndex( int childIndex )
	{
		this.childIndex = childIndex;
	}

	public boolean isMouseOver( Relative relative, float x, float y )
	{
		float l = getX( relative ) + margins.left;
		float t = getY( relative ) + margins.top;
		float r = l + bounds.getWidth() - margins.right;
		float b = t + bounds.getHeight() - margins.bottom;

		return (x >= l && x < r && y >= t && y < b);
	}

	public void detach()
	{
		if (parent != null)
		{
			parent.remove( this );
		}
	}

	public Container getParent()
	{
		return parent;
	}

	public int getChildIndex()
	{
		return childIndex;
	}

	public Bound2 getMargins( Relative relative, Bound2 out )
	{
		float x = getX( relative );
		float y = getY( relative );

		out.setRect( x, y, size.x, size.y );

		return out;
	}

	public Bound2 getMouseBounds( Relative relative, Bound2 out )
	{
		float x = margins.left + getX( relative );
		float y = margins.top + getY( relative );

		out.setRect( x, y, getMouseBoundsWidth(), getMouseBoundsHeight() );

		return out;
	}

	public Bound2 getPadding( Relative relative, Bound2 out )
	{
		float x = margins.left + padding.left + getX( relative );
		float y = margins.top + padding.top + getY( relative );

		out.setRect( x, y, getPaddingWidth(), getPaddingHeight() );

		return out;
	}

	public float getScreenX()
	{
		return (parent == null ? 0 : parent.getScreenX()) + bounds.left;
	}

	public float getScreenY()
	{
		return (parent == null ? 0 : parent.getScreenY()) + bounds.top;
	}

	public float getMouseBoundsWidth()
	{
		return bounds.getWidth() - margins.left - margins.right;
	}

	public float getMouseBoundsHeight()
	{
		return bounds.getHeight() - margins.top - margins.bottom;
	}

	public float getPaddingWidth()
	{
		return getMouseBoundsWidth() - padding.left - padding.right;
	}

	public float getPaddingHeight()
	{
		return getMouseBoundsHeight() - padding.top - padding.bottom;
	}

	public float getWidth()
	{
		return bounds.getWidth();
	}

	public float getHeight()
	{
		return bounds.getHeight();
	}

	public Vector2 getSize( Vector2 out )
	{
		out.x = bounds.getWidth();
		out.y = bounds.getHeight();

		return out;
	}

	public Vector2 getPosition( Relative relative, Vector2 out )
	{
		out.x = getX( relative );
		out.y = getY( relative );

		return out;
	}

	public float getX( Relative relative )
	{
		switch (relative)
		{
		case Control:
			return 0;
		case Parent:
			return bounds.left;
		case Screen:
			return getScreenX();
		}

		return -1;
	}

	public float getY( Relative relative )
	{
		switch (relative)
		{
		case Control:
			return 0;
		case Parent:
			return bounds.top;
		case Screen:
			return getScreenY();
		}

		return -1;
	}

	public int getState()
	{
		return state;
	}

	public boolean isMouseOver()
	{
		return mouseover;
	}

	public boolean isFocused()
	{
		return focused;
	}

	public boolean hasImplicitFocus()
	{
		return focused;
	}

	public boolean isSelected()
	{
		return selected;
	}

	public boolean isFocusable()
	{
		return focusable;
	}

	public void setFocusable( boolean focusable )
	{
		this.focusable = focusable;
	}

	public boolean isSelectable()
	{
		return selectable;
	}

	public void setSelectable( boolean selectable )
	{
		this.selectable = selectable;
	}

}
