
package com.gameprogblog.engine.prob;

import java.util.Random;


public class IterativeProbabilityMatrix<F extends Enum<F>, T extends Enum<T>> implements ProbabilityMatrix<F, T>
{

	public static final Random DEFAULT_RANDOM = new Random();

	private Random random = DEFAULT_RANDOM;
	private final int[] totalWeight;
	private final int[][] values;
	private final int[] sums;
	private final F[] fromConstants;
	private final T[] toConstants;

	public IterativeProbabilityMatrix( Class<F> fromClass, Class<T> toClass )
	{
		this.fromConstants = fromClass.getEnumConstants();
		this.toConstants = toClass.getEnumConstants();
		
		final int fromCount = fromConstants.length;
		final int toCount = toConstants.length;
		
		this.totalWeight = new int[fromCount];
		this.sums = new int[fromCount];
		this.values = new int[fromCount][toCount];
	}

	public IterativeProbabilityMatrix( Class<F> fromClass, Class<T> toClass, int[][] values )
	{
		this.fromConstants = fromClass.getEnumConstants();
		this.toConstants = toClass.getEnumConstants();

		final int fromCount = fromConstants.length;
		final int toCount = toConstants.length;

		this.totalWeight = new int[fromCount];
		this.sums = new int[fromCount];
		this.values = values;

		for (int i = 0; i < fromCount; i++)
		{
			for (int k = 0; k < toCount; k++)
			{
				totalWeight[i] += values[i][k];
			}
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
	public T random( F from )
	{
		final int index = from.ordinal();
		final int[] row = values[index];
		final int target = random.nextInt( totalWeight[index] );

		return toConstants[ProbabilityUtility.pick( target, row )];
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
		final int findex = from.ordinal();
		final int tindex = to.ordinal();
		final int before = values[findex][tindex];

		values[findex][tindex] = weight;
		totalWeight[findex] += -before + weight;
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

	}

}
