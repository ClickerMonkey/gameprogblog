package com.gameprogblog.engine.noise;

public class NoiseGenerator
{

	public Noise noise;
	public long seed;
	public double scaleX;
	public double scaleY;
	public double scaleZ;
	public int octaves;
	public double frequency;
	public double amplitude;
	public boolean normalize;

	public NoiseGenerator()
	{
	}
	
	public NoiseGenerator( double scaleX, double scaleY, double scaleZ, int octaves, double frequency, double amplitude, boolean normalize, Noise noise )
	{
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
		this.octaves = octaves;
		this.frequency = frequency;
		this.amplitude = amplitude;
		this.normalize = normalize;
		this.noise = noise;
	}
	
	private double scale( double min, double max, double n )
	{
		return (n + 1) * 0.5f * (max - min) + min;
	}
	
	public double noise(double min, double max, double x)
	{
		return scale( min, max, noise.noise( x * scaleX, octaves, frequency, amplitude, normalize ) );
	}

	public double noise(double min, double max, double x, double y)
	{
		return scale( min, max, noise.noise( x * scaleX, y * scaleY, octaves, frequency, amplitude, normalize ) );
	}
	
	public double noise(double min, double max, double x, double y, double z)
	{
		return scale( min, max, noise.noise( x * scaleX, y * scaleY, z * scaleZ, octaves, frequency, amplitude, normalize ) );
	}

	public float noisef(float min, float max, float x)
	{
		return (float)noise( min, max, x );
	}
	
	public float noisef(float min, float max, float x, float y)
	{
		return (float)noise( min, max, x, y );
	}
	
	public float noisef(float min, float max, float x, float y, float z)
	{
		return (float)noise( min, max, x, y, z );
	}
	
}
