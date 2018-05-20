
package com.gameprogblog.engine.ui.java2d;

import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import com.gameprogblog.engine.ui.Font;
import com.gameprogblog.engine.ui.Glyph;
import com.gameprogblog.engine.ui.Tile;


public class Java2dFont implements Font
{

	public static final int DEFAULT_CHAR_MAX = 256;
	public static final int DEFAULT_BITMAP_WIDTH = 512;
	public static final int DEFAULT_BITMAP_HEIGHT = 512;

	public final BufferedImage bufferedImage;
	public final Java2dImage image;
	public final Glyph[] glyphs;
	public final java.awt.Font font;
	
	public Java2dFont( InputStream in ) throws FontFormatException, IOException
	{
		this( java.awt.Font.createFont( java.awt.Font.TRUETYPE_FONT, in ) );
	}

	public Java2dFont( java.awt.Font font )
	{
		this( font, DEFAULT_CHAR_MAX, DEFAULT_BITMAP_WIDTH, DEFAULT_BITMAP_HEIGHT );
	}

	public Java2dFont( java.awt.Font font, int charMax, int bitmapWidth, int bitmapHeight )
	{
		this.font = font;
		this.glyphs = createGlyphs( font, charMax );
		this.bufferedImage = createBitmap( font, glyphs, bitmapWidth, bitmapHeight );
		this.image = new Java2dImage( font.getName(), bufferedImage );

		for (int i = 0; i < charMax; i++)
		{
			if (glyphs[i] != null)
			{
				glyphs[i].tile.image = image;
			}
		}
	}

	@Override
	public Glyph getGlyph( char c, int index )
	{
		return glyphs[c];
	}

	@Override
	public float getSize()
	{
		return font.getSize();
	}

	@Override
	public String getName()
	{
		return font.getName();
	}

	private static Glyph[] createGlyphs( java.awt.Font font, int maxChar )
	{
		Glyph[] glyphs = new Glyph[maxChar];

		FontRenderContext context = new FontRenderContext( new AffineTransform(), true, true );

		for (char[] charArray = { 0 }; charArray[0] < maxChar; charArray[0]++)
		{
			GlyphVector gv = font.createGlyphVector( context, charArray );
			Rectangle2D visual = gv.getVisualBounds();
			Rectangle2D logical = gv.getLogicalBounds();

			if (logical.getWidth() > 0)
			{
				glyphs[charArray[0]] = new Glyph( new Tile(), (float)logical.getWidth(),
					(float)visual.getMinX(), (float)visual.getMinY(),
					(float)visual.getMaxX() + 1, (float)visual.getMaxY() + 1 );
			}
		}

		return glyphs;
	}

	private static BufferedImage createBitmap( java.awt.Font font, Glyph[] glyphs, int width, int height )
	{
		BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_INT_BGR );

		Graphics2D gr = img.createGraphics();

		gr.setColor( new Color( 0, 0, 0, 0 ) );
		gr.fillRect( 0, 0, width, height );
		gr.setColor( Color.white );
		gr.setFont( font );
		gr.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		gr.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );
		gr.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		gr.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

		float maxHeight = 0;
		float x = 0;
		float y = 0;

		for (char c = 0; c < glyphs.length; c++)
		{
			Glyph g = glyphs[c];

			if (g != null)
			{
				if (x + g.width() > width)
				{
					x = 0;
					y += maxHeight + 2;
					maxHeight = 0;
				}

				g.tile.l = (int)Math.floor(x - 1);
				g.tile.t = (int)Math.floor(y - 1);
				g.tile.r = (int)Math.ceil(x + g.width());
				g.tile.b = (int)Math.ceil(y + g.height());

				gr.drawString( String.valueOf( c ), x - g.left, y - g.top );

				x += g.width() + 2;

				maxHeight = Math.max( maxHeight, g.height() );
			}
		}

		gr.dispose();

		return img;
	}

}
