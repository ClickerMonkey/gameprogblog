package com.gameprogblog.engine.prob;

import java.util.Random;


public class IterativeProbabilityArray<E extends Enum<E>> implements ProbabilityArray<E>
{

	public static final Random DEFAULT_RANDOM = new Random();
	
	private Random random = DEFAULT_RANDOM;
	private int totalWeight;
	private final int[] values;
	private final Class<E> enumClass;

	public IterativeProbabilityArray(Class<E> enumClass)
	{
		final int constantCount = enumClass.getEnumConstants().length;
		
		this.enumClass = enumClass;
		this.values = new int[ constantCount ];
		this.totalWeight = 0;
	}
	
	public IterativeProbabilityArray(Class<E> enumClass, int ... values )
	{
		final int constantCount = enumClass.getEnumConstants().length;
		
		this.enumClass = enumClass;
		this.values = new int[ constantCount ];
		this.totalWeight = 0;
		
		for (int i = 0; i < constantCount; i++)
		{
			totalWeight += values[i];
		}
	}
	
	@Override
	public void setRandom( Random random )
	{
		this.random = random;
	}

	@Override
	public Random getRandom()
	{
		return random;
	}

	@Override
	public E random()
	{
		final int[] vals = values;
		final E[] constants = enumClass.getEnumConstants();
		final int constantCount = constants.length;
		final int target = random.nextInt( totalWeight );
		int current = 0;

		for (int i = 0; i < constantCount; i++)
		{
			current += vals[i];
			
			if (current > target && vals[i] != 0)
			{
				return constants[i];
			}
		}
		
		return null;
	}

	@Override
	public int getWeight( E element )
	{
		return values[ element.ordinal() ];
	}

	@Override
	public void setWeight( E element, int weight )
	{
		final int index = element.ordinal();
		final int before = values[ index ];

		values[ index ] = weight;
		
		totalWeight += -before + weight;
	}

	@Override
	public float getProbablility( E element )
	{
		return (float)values[ element.ordinal() ] / (float)totalWeight;
	}

	@Override
	public int getTotalWeight()
	{
		return totalWeight;
	}

	@Override
	public void build()
	{

	}

}