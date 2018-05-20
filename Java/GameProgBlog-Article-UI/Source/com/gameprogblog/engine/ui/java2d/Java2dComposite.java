
package com.gameprogblog.engine.ui.java2d;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;


public class Java2dComposite implements Composite, CompositeContext
{

	public int[] srcPixels = {};
	public int[] dstInPixels = {};
	public int[] dstOutPixels = {};
	public int r, g, b, a;

	public void set( float a, float r, float g, float b )
	{
		this.a = componentFromFloat( a );
		this.r = componentFromFloat( r );
		this.g = componentFromFloat( g );
		this.b = componentFromFloat( b );
	}
	
	public void clear()
	{
		a = r = g = b = 0xFF;
	}

	@Override
	public CompositeContext createContext( ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints )
	{
		return this;
	}

	@Override
	public void dispose()
	{

	}

	@Override
	public void compose( Raster src, Raster dstIn, WritableRaster dstOut )
	{
		int w = Math.min( src.getWidth(), dstIn.getWidth() );
		int h = Math.min( src.getHeight(), dstIn.getHeight() );
		int size = (w * h) << 2;

		if (srcPixels.length < size)
		{
			srcPixels = new int[size];
			dstInPixels = new int[size];
			dstOutPixels = new int[size];
		}

		src.getPixels( 0, 0, w, h, srcPixels );
		dstIn.getPixels( 0, 0, w, h, dstInPixels );

		for (int i = 0; i < size; i += 4)
		{
			int sr = mulComponents( r, srcPixels[i + 0] );
			int sg = mulComponents( g, srcPixels[i + 1] );
			int sb = mulComponents( b, srcPixels[i + 2] );
			int sa = mulComponents( a, srcPixels[i + 3] );

			int dr = dstInPixels[i + 0];
			int dg = dstInPixels[i + 1];
			int db = dstInPixels[i + 2];
			int da = dstInPixels[i + 3];

			dstOutPixels[i + 0] = mixComponents( dr, sr, sa );
			dstOutPixels[i + 1] = mixComponents( dg, sg, sa );
			dstOutPixels[i + 2] = mixComponents( db, sb, sa );
			dstOutPixels[i + 3] = da;
		}

		dstOut.setPixels( 0, 0, w, h, dstOutPixels );
	}

	public static int mulComponents( int c0, int c1 )
	{
		return (c0 * c1 + 0xFF) >> 8;
	}

	public static int mixComponents( int c0, int c1, int delta )
	{
		return mulComponents( c0, 0xFF ^ delta ) + mulComponents( c1, delta );
	}

	public static int componentFromFloat( float x )
	{
		return (x < 0.0f ? 0 : (x > 1.0f ? 0xFF : (int)(x * 0xFF)));
	}

}
