
package com.gameprogblog.engine.prob;

import java.util.Random;


public class ExpandedProbabilityArray<E extends Enum<E>> implements ProbabilityArray<E>
{

	public static final Random DEFAULT_RANDOM = new Random();

	private Random random = DEFAULT_RANDOM;
	private E[] expanded;
	private int totalWeight;
	private final int[] values;
	private final Class<E> enumClass;

	public ExpandedProbabilityArray( Class<E> enumClass )
	{
		this.enumClass = enumClass;
		this.values = new int[enumClass.getEnumConstants().length];
	}

	public ExpandedProbabilityArray( Class<E> enumClass, int... values )
	{
		this.enumClass = enumClass;
		this.values = values;
		this.build();
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
		return expanded[random.nextInt( expanded.length )];
	}

	@Override
	public int getWeight( E element )
	{
		return values[element.ordinal()];
	}

	@Override
	public void setWeight( E element, int weight )
	{
		values[element.ordinal()] = weight;
	}

	@Override
	public float getProbablility( E element )
	{
		return (float)values[element.ordinal()] / (float)totalWeight;
	}

	@Override
	public int getTotalWeight()
	{
		return totalWeight;
	}

	@Override
	public void build()
	{
		totalWeight = ProbabilityUtility.sum( values );
		expanded = ProbabilityUtility.expand( values, totalWeight, enumClass.getEnumConstants() );
	}

}
