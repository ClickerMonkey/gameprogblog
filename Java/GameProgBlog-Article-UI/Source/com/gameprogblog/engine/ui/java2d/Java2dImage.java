
package com.gameprogblog.engine.ui.java2d;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.gameprogblog.engine.ui.Image;


public class Java2dImage implements Image
{

	private static int currentId = 0;

	public final String name;
	public final BufferedImage image;
	public final int id;

	public Java2dImage( String filepath ) throws IOException
	{
		this( filepath, ImageIO.read( new File( filepath ) ) );
	}

	public Java2dImage( String name, InputStream in ) throws IOException
	{
		this( name, ImageIO.read( in ) );
	}

	public Java2dImage( String name, BufferedImage image )
	{
		this.name = name;
		this.image = image;
		this.id = ++currentId;
	}

	@Override
	public int getId()
	{
		return id;
	}

	@Override
	public int getWidth()
	{
		return image.getWidth();
	}

	@Override
	public int getHeight()
	{
		return image.getHeight();
	}

	@Override
	public String getName()
	{
		return name;
	}

}
