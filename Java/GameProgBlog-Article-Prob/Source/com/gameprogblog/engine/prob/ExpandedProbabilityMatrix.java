
package com.gameprogblog.engine.prob;

import java.util.Random;


public class ExpandedProbabilityMatrix<F extends Enum<F>, T extends Enum<T>> implements ProbabilityMatrix<F, T>
{

	public static final Random DEFAULT_RANDOM = new Random();

	private Random random = DEFAULT_RANDOM;
	private final T[][] expanded;
	private final int[] totalWeight;
	private final int[][] values;
	private final int[] sums;
	private final F[] fromConstants;
	private final T[] toConstants;

	public ExpandedProbabilityMatrix( Class<F> fromClass, Class<T> toClass )
	{
		this.fromConstants = fromClass.getEnumConstants();
		this.toConstants = toClass.getEnumConstants();
		
		final int constantCount = fromConstants.length;

		this.totalWeight = new int[constantCount];
		this.sums = new int[constantCount];
		this.values = new int[constantCount][constantCount];
		this.expanded = (T[][])new Enum[constantCount][];
	}

	public ExpandedProbabilityMatrix( Class<F> fromClass, Class<T> toClass, int[][] values )
	{
		this.fromConstants = fromClass.getEnumConstants();
		this.toConstants = toClass.getEnumConstants();
		
		final int constantCount = fromConstants.length;

		this.totalWeight = new int[constantCount];
		this.sums = new int[constantCount];
		this.values = values;
		this.expanded = (T[][])new Enum[constantCount][];
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
	public T random( F from )
	{
		final int index = from.ordinal();
		final T[] row = expanded[index];

		return row[random.nextInt( row.length )];
	}

	@Override
	public T random( F... froms )
	{
		final int[][] vals = values;
		final int[] sum = sums;
		final int constantCount = toConstants.length;
		int sumWeight = 0;

		for (int i = 0; i < froms.length; i++)
		{
			for (int k = 0; k < constantCount; k++)
			{
				sum[k] += vals[froms[i].ordinal()][k];
			}
		}

		for (int i = 0; i < constantCount; i++)
		{
			sumWeight += sum[i];
		}

		int target = random.nextInt( sumWeight );

		return toConstants[ProbabilityUtility.pick( target, sum )];
	}

	@Override
	public int getWeight( F from, T to )
	{
		return values[from.ordinal()][to.ordinal()];
	}

	@Override
	public void setWeight( F from, T to, int weight )
	{
		values[from.ordinal()][to.ordinal()] = weight;
	}

	@Override
	public float getProbablility( F from, T to )
	{
		return (float)values[from.ordinal()][to.ordinal()] / (float)totalWeight[from.ordinal()];
	}

	@Override
	public int getTotalWeight( F from )
	{
		return totalWeight[from.ordinal()];
	}

	@Override
	public void build()
	{
		final int constantCount = toConstants.length;

		for (int i = 0; i < constantCount; i++)
		{
			totalWeight[i] = ProbabilityUtility.sum( values[i] );
			expanded[i] = ProbabilityUtility.expand( values[i], totalWeight[i], toConstants );
		}
	}

}
