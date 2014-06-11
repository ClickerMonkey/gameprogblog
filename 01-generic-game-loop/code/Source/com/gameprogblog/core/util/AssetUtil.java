
package com.gameprogblog.core.util;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;


/**
 * @author Philip Diffenderfer
 */
public class AssetUtil
{

	// The source directory to currently load
	// resources from.
	private static String resources = "";

	// The classloader used to determine the classpath
	// of the game. Used to load resources.
	private static ClassLoader loader;

	// This occurs as soon as the Util class is initialized
	static
	{
		loader = AssetUtil.class.getClassLoader();
	}

	/**
	 * Sets the directory containing all the resources to load.
	 * 
	 * @param directory
	 *        => The directory containing resources.
	 */
	public static void setResourceDirectory( String directory )
	{
		resources = directory;
	}

	/**
	 * Loads a clip from the resource directory.
	 * 
	 * @param filename
	 *        => The name of the file in the resource directory.
	 */
	public static AudioClip loadClip( String filename ) throws Exception
	{
		URL url = loader.getResource( resources + filename );
		return Applet.newAudioClip( url );
	}

	/**
	 * Loads a font from the resource directory.
	 * 
	 * @param filename
	 *        => The name of the file in the resource directory.
	 * @param size
	 *        => The size in pixels of the font to load.
	 */
	public static Font loadFont( String filename, float size ) throws Exception
	{
		InputStream stream = loader.getResource( resources + filename ).openStream();
		Font font = Font.createFont( Font.TRUETYPE_FONT, stream );
		return font.deriveFont( size );
	}

	/**
	 * Loads an image from the resource directory.
	 * 
	 * @param filename
	 *        => The name of the file in the resource directory.
	 */
	public static BufferedImage loadImage( String filename ) throws Exception
	{
		InputStream stream = loader.getResource( resources + filename ).openStream();
		return ImageIO.read( stream );
	}

	/**
	 * Loads a file from the resource directory.
	 * 
	 * @param filename
	 *        => The name of the file in the resource directory.
	 */
	public static File loadFile( String filename ) throws Exception
	{
		return new File( loader.getResource( resources + filename ).getFile() );
	}

}
