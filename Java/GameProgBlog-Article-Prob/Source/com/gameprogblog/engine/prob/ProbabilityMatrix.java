
package com.gameprogblog.engine.prob;

import java.util.Random;


public interface ProbabilityMatrix<F extends Enum<F>, T extends Enum<T>>
{

	public void setRandom( Random random );

	public Random getRandom();

	public T random( F from );
	
	public T random( F ... froms );

	public int getWeight( F from, T to );

	public void setWeight( F from, T to, int weight );

	public float getProbablility( F from, T to );
 
	public int getTotalWeight( F from );
	
	public void build();
}
