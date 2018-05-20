
package com.gameprogblog.engine.util;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class GraphicsUtil
{

	public static final AffineTransform IDENTITY = new AffineTransform();

	public static void drawSprite( Graphics2D gr, BufferedImage tilesheet, Rectangle source, float x, float y, float width, float height )
	{
		int halfWidth = (int)(width * 0.5);
		int halfHeight = (int)(height * 0.5);

		gr.translate( x, y );
		gr.drawImage( tilesheet, -halfWidth, -halfHeight, halfWidth, halfHeight,
				source.x, source.y, source.x + source.width, source.y + source.height, null );
		gr.setTransform( IDENTITY );
	}

	public static void drawSprite( Graphics2D gr, BufferedImage tilesheet, Rectangle source, float x, float y, float width, float height, float radians )
	{
		int halfWidth = (int)(width * 0.5);
		int halfHeight = (int)(height * 0.5);

		gr.translate( x, y );
		gr.rotate( radians );
		gr.drawImage( tilesheet, -halfWidth, -halfHeight, halfWidth, halfHeight,
				source.x, source.y, source.x + source.width, source.y + source.height, null );
		gr.setTransform( IDENTITY );
	}

	/**
	 * This will draw a paragraph centered in a box where drawing each line
	 * either starts at the bottom or top.
	 * 
	 * @param gr
	 *        The graphics object to draw on.
	 * @param s
	 *        The paragraph to draw.
	 * @param left
	 *        The left side of the box.
	 * @param top
	 *        The top side of the box.
	 * @param right
	 *        The right side of the box.
	 * @param bottom
	 *        The bottom side of the box.
	 * @param startAtTop
	 *        Whether to start drawing each line at the top or bottom of the box.
	 * @return The bottom coordinate of the paragraph drawn.
	 */
	public static float drawString( Graphics2D gr, String s, float left, float top, float right, float bottom, boolean startAtTop )
	{
		String[] lines = s.split( "\n" );
		FontMetrics metrics = gr.getFontMetrics();

		final float cushion = 4f;
		float fontHeight = metrics.getHeight();
		float totalHeight = (fontHeight + cushion);
		float paraBottom = top + (lines.length * totalHeight);
		float y = (startAtTop ? top + fontHeight : bottom);
		float cx = (left + right) * 0.5f;
		float width, x;
		int line;

		for (int i = 0; i < lines.length; i++)
		{
			// The current line to draw
			line = (startAtTop ? i : lines.length - i - 1);
			// The width of the line in pixels
			width = metrics.stringWidth( lines[line] );
			// The starting x centered in the bounds.
			x = cx - (width * 0.5f);
			// Draw the line
			gr.drawString( lines[line], x, y );
			// Increase/Decrease y based on whether it started at the top.
			y += (startAtTop ? totalHeight : -totalHeight);
		}

		// Return the bottom of the paragraph
		// based on whether it started at the top.
		return (startAtTop ? paraBottom : bottom);
	}

}
