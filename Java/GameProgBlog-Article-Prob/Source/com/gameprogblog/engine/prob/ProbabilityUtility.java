
package com.gameprogblog.engine.prob;

public class ProbabilityUtility
{

	public static int pick( int target, int[] weights )
	{
		int current = 0;

		for (int i = 0; i < weights.length; i++)
		{
			current += weights[i];

			if (current >= target && weights[i] != 0)
			{
				return i;
			}
		}

		return 0;
	}
	
	public static int sum(final int[] x)
	{
		int sum = 0;
		
		for (int i = 0; i < x.length; i++)
		{
			sum += x[i];
		}
		
		return sum;
	}
	
	public static <E>	E[] expand(final int[] weights, final int sum, final E[] constants)
	{
		final E[] expanded = (E[])new Object[sum];
		
		int j = 0;
		
		for (int i = 0; i < weights.length; i++)
		{
			E currentConstant = constants[i];
			
			for (int k = weights[i]; k > 0; k--)
			{
				expanded[j++] = currentConstant;
			}
		}
		
		return expanded;
	}

}
