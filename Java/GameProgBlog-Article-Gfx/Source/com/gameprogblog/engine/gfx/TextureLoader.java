
package com.gameprogblog.engine.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;


public class TextureLoader
{

	public static Texture fromStream( InputStream in, boolean close ) throws IOException
	{
		try
		{
			BufferedImage image = ImageIO.read( in );
			Texture texture = new Texture( image.getWidth(), image.getHeight() );
			int[] pixels = texture.pixels;
			int offset = 0;

			for (int y = 0; y < texture.height; y++)
			{
				for (int x = 0; x < texture.width; x++)
				{
					pixels[offset++] = image.getRGB( x, y );
				}
			}

			return texture;
		}
		finally
		{
			if (close)
			{
				in.close();
			}
		}
	}

	public static Texture fromUrl( String url ) throws MalformedURLException, IOException
	{
		return fromStream( new URL( url ).openStream(), true );
	}

	public static Texture fromClasspath(String name) throws IOException
	{
		return fromStream( TextureLoader.class.getResourceAsStream( name ), true );
	}
	
}
