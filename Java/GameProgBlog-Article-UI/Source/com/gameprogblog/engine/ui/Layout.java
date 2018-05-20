
package com.gameprogblog.engine.ui;

import com.gameprogblog.engine.core.Vector2;

public interface Layout
{

	public void setParent( Container container );

	public void onParentResize();
	
	public void onLayout();

	public void onUpdate( float elapsed );

	public void onChildMoved( Control child, int oldIndex, int newIndex );

	public boolean onChildAdd( Control child, int childIndex );

	public void onChildRemove( Control child, int childIndex );
	
	public Vector2 getMinimumSize(Vector2 out);

}
