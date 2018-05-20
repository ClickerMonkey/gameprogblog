
package com.gameprogblog.engine.ui;

import com.gameprogblog.engine.core.Bound2;

public class ControlTheme
{

	public ControlLayer[] layers = {};
	public ControlEffect[] effects = {};
	public Font font;
	public float red = 1.0f;
	public float green = 1.0f;
	public float blue = 1.0f;
	public float alpha = 1.0f;

	public void start( InterfaceRenderer renderer, Control control, Bound2 bounds )
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
		
		for (int i = 0; i < effects.length; i++)
		{
			if (effects[i].isActive( control.state ))
			{
				effects[i].start( renderer, control, bounds );	
			}
		}

		for (int i = 0; i < layers.length; i++)
		{
			if (layers[i].isVisible( control.state ) )
			{
				layers[i].draw( renderer, control, bounds );	
			}
		}
	}
	
	public void end( InterfaceRenderer renderer, Control control, Bound2 bounds )
	{
		for (int i = 0; i < effects.length; i++)
		{
			if (effects[i].isActive( control.state ))
			{
				effects[i].end( renderer, control, bounds );
			}
		}
		
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

}
