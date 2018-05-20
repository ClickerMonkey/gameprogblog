
package com.gameprogblog.engine.ui;

import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.core.Vector2;


public abstract class BaseInterfaceRenderer implements InterfaceRenderer
{

	protected Bound2[] clips;
	protected int clipDepth;
	protected float[] reds;
	protected float[] greens;
	protected float[] blues;
	protected int shadeDepth;
	protected float[] alphas;
	protected int alphaDepth;
	protected Font[] fonts;
	protected int fontDepth;
	protected float[] x;
	protected float[] y;
	protected int originDepth;

	protected final float baseRed;
	protected final float baseGreen;
	protected final float baseBlue;
	protected final float baseAlpha;
	protected final Font baseFont;

	public BaseInterfaceRenderer( int maxDepth, float initialRed, float initialGreen, float initialBlue, float initialAlpha, Font initalFont )
	{
		clips = new Bound2[maxDepth];
		reds = new float[maxDepth];
		greens = new float[maxDepth];
		blues = new float[maxDepth];
		alphas = new float[maxDepth];
		fonts = new Font[maxDepth];
		x = new float[maxDepth];
		y = new float[maxDepth];

		for (int i = 0; i < maxDepth; i++)
		{
			clips[i] = new Bound2();
		}

		baseRed = initialRed;
		baseGreen = initialGreen;
		baseBlue = initialBlue;
		baseAlpha = initialAlpha;
		baseFont = initalFont;
	}

	@Override
	public void start( Bound2 initialArea )
	{
		clipDepth = -1;
		shadeDepth = -1;
		alphaDepth = -1;
		fontDepth = -1;
		originDepth = -1;

		setClip( initialArea, false );
		setOrigin( initialArea.left, initialArea.top );
		setShade( baseRed, baseGreen, baseBlue );
		setAlpha( baseAlpha );
		setFont( baseFont );
	}

	@Override
	public void end()
	{
	}

	@Override
	public void setClip( Bound2 area, boolean relative )
	{
		setClip( area.left, area.top, area.right, area.bottom, relative );
	}

	@Override
	public void setClipRect( float x, float y, float w, float h, boolean relative )
	{
		setClip( x, y, x + w, y + h, relative );
	}

	@Override
	public void setClip( float left, float top, float right, float bottom, boolean relative )
	{
		if (relative)
		{
			float x = getX();
			float y = getY();

			left += x;
			right += x;
			top += y;
			bottom += y;
		}

		Bound2 clip = clips[++clipDepth];

		clip.set( left, top, right, bottom );

		applyClip( clip );
	}

	protected abstract void applyClip( Bound2 clip );

	@Override
	public void popClip()
	{
		applyClip( clips[--clipDepth] );
	}

	@Override
	public Bound2 getClip()
	{
		return clips[clipDepth];
	}

	@Override
	public void addShade( float r, float g, float b )
	{
		float pr = reds[shadeDepth];
		float pg = greens[shadeDepth];
		float pb = blues[shadeDepth];
		
		++shadeDepth;
		
		reds[shadeDepth] = pr * r;
		greens[shadeDepth] = pg * g;
		blues[shadeDepth] = pb * b;
	}

	@Override
	public void setShade( float r, float g, float b )
	{
		++shadeDepth;
		
		reds[shadeDepth] = r;
		greens[shadeDepth] = g;
		blues[shadeDepth] = b;
	}

	@Override
	public void popShade()
	{
		shadeDepth--;
	}

	@Override
	public float getRed()
	{
		return reds[shadeDepth];
	}

	@Override
	public float getGreen()
	{
		return greens[shadeDepth];
	}

	@Override
	public float getBlue()
	{
		return blues[shadeDepth];
	}

	@Override
	public void addAlpha( float alpha )
	{
		float p = alphas[alphaDepth];

		alphas[++alphaDepth] = alpha * p;
	}

	@Override
	public void setAlpha( float alpha )
	{
		alphas[++alphaDepth] = alpha;
	}

	@Override
	public void popAlpha()
	{
		alphaDepth--;
	}

	@Override
	public float getAlpha()
	{
		return alphas[alphaDepth];
	}

	@Override
	public void setFont( Font font )
	{
		fonts[++fontDepth] = font;
	}

	@Override
	public void popFont()
	{
		fontDepth--;
	}

	@Override
	public Font getFont()
	{
		return fonts[fontDepth];
	}

	@Override
	public void setOrigin( float ox, float oy )
	{
		++originDepth;
		
		x[originDepth] = ox;
		y[originDepth] = oy;
	}

	@Override
	public void addOrigin( float ox, float oy )
	{
		float px = x[originDepth];
		float py = y[originDepth];
		
		++originDepth;
		
		x[originDepth] = ox + px;
		y[originDepth] = oy + py;
	}

	@Override
	public void popOrigin()
	{
		originDepth--;
	}

	@Override
	public float getX()
	{
		return x[originDepth];
	}

	@Override
	public float getY()
	{
		return y[originDepth];
	}

	@Override
	public void draw( Tile tile, Bound2 dest )
	{
		draw( tile, dest.left, dest.top, dest.right, dest.bottom );
	}

	@Override
	public void draw( String text, float fontSize, Bound2 dest, float lineHeight, Vector2 destAnchor, Vector2 textAnchor, boolean wraps, boolean stretches )
	{
		draw( text.toCharArray(), fontSize, dest, lineHeight, destAnchor, textAnchor, wraps, stretches );
	}

	protected int getLineCount( char[] text, Font font, boolean wrap, float scale, float maximumWidth )
	{
		int lineCount = 1;
		float currentWidth = 0.0f;

		for (int i = 0; i < text.length; i++)
		{
			char c = text[i];

			if (c == '\n' || c == '\r')
			{
				lineCount++;
				currentWidth = 0.0f;
			}
			else if (wrap)
			{
				Glyph g = font.getGlyph( c, i );

				if (g != null)
				{
					float add = g.advance * scale;

					if (currentWidth + add > maximumWidth)
					{
						lineCount++;
						currentWidth = 0.0f;
					}

					currentWidth += add;
				}
			}
		}

		return lineCount;
	}

	protected float[] getLineMetrics( char[] text, Font font, float scale, boolean wrap, float maximumWidth, float[] lineWidth, int[] lineStart, int[] lineEnd )
	{
		int currentLine = 0;
		float currentWidth = 0.0f;

		for (int i = 0; i < text.length; i++)
		{
			char c = text[i];

			if (c == '\n' || c == '\r')
			{
				lineEnd[currentLine] = i - 1;
				lineWidth[currentLine++] = currentWidth;
				lineStart[currentLine] = i + 1;
				currentWidth = 0.0f;
			}
			else
			{
				Glyph g = font.getGlyph( c, i );

				if (g != null)
				{
					float add = g.advance * scale;

					if (wrap && add + currentWidth > maximumWidth)
					{
						lineEnd[currentLine] = i;
						lineWidth[currentLine++] = currentWidth;
						lineStart[currentLine] = i + 1;
						currentWidth = 0.0f;
					}

					currentWidth += add;
				}
			}
		}

		lineStart[0] = 0;
		lineWidth[currentLine] = currentWidth;
		lineEnd[currentLine] = text.length - 1;

		return lineWidth;
	}

	@Override
	public void draw( char[] text, float fontSize, Bound2 dest, float lineHeight, Vector2 destAnchor, Vector2 textAnchor, boolean wraps, boolean stretches )
	{
		final Font font = getFont();

		final float scale = fontSize / font.getSize();
		final float maximumWidth = dest.getWidth();
		final float maximumHeight = dest.getHeight();

		final int lineCount = getLineCount( text, font, wraps, scale, maximumWidth );

		final float[] lineWidth = new float[lineCount];
		final int[] lineStart = new int[lineCount];
		final int[] lineEnd = new int[lineCount];

		getLineMetrics( text, font, scale, wraps, maximumWidth, lineWidth, lineStart, lineEnd );

		final float expectedHeight = lineCount * lineHeight;

		final float cornerX = dest.left;
		final float cornerY = dest.top;

		float y = (maximumHeight * destAnchor.y) - (expectedHeight * textAnchor.y) + cornerY;

		for (int i = 0; i < lineCount; i++)
		{
			float emptySpace = maximumWidth - lineWidth[i];
			int charCount = lineEnd[i] - lineStart[i] + 1;
			float kerningOffset = stretches ? (emptySpace / charCount) : 0.0f;
			float x = stretches ? 0 : (maximumWidth * destAnchor.x) - (lineWidth[i] * textAnchor.x) + cornerX;

			for (int k = lineStart[i]; k <= lineEnd[i]; k++)
			{
				Glyph g = font.getGlyph( text[k], k );

				if (g != null)
				{
					float l = x - g.left * scale;
					float r = x + g.right * scale;
					float t = y + (lineHeight + g.top * scale);
					float b = y + (lineHeight - g.bottom * scale);

					draw( g.tile, l, t, r, b );

					x += g.advance * scale + kerningOffset;
				}
			}

			y += lineHeight;
		}
	}

	@Override
	public void draw( Char[] chars, Bound2 dest, float lineHeight, Vector2 destAnchor, Vector2 textAnchor, boolean wraps, boolean stretches )
	{
		// TODO
	}

	@Override
	public Char[] createChar( String text, float fontSize, boolean useAlpha, boolean useShade )
	{
		Font f = getFont();
		float scale = fontSize / f.getSize();
		int count = text.length();

		Char[] chars = new Char[count];

		for (int i = 0; i < count; i++)
		{
			Char c = new Char();
			c.c = text.charAt( i );
			c.scale.set( scale );

			if (useAlpha)
			{
				c.alpha = getAlpha();
			}

			if (useShade)
			{
				c.red = getRed();
				c.green = getGreen();
				c.blue = getBlue();
			}

			chars[i] = c;
		}

		return chars;
	}

}
