
package com.gameprogblog.engine.prob;

import java.util.Random;


public interface ProbabilityArray<E extends Enum<E>>
{

	public void setRandom( Random random );

	public Random getRandom();

	public E random();

	public int getWeight( E element );

	public void setWeight( E element, int weight );

	public float getProbablility( E element );

	public int getTotalWeight();
	
	public void build();
}
