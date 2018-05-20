
package com.gameprogblog.core;

import java.awt.Graphics2D;
import java.util.concurrent.TimeUnit;

import com.gameprogblog.core.input.GameInput;


public class GameLoopFixed implements GameLoop
{

	public long updateRate;
	public long drawRate;
	public int maxUpdates;
	public boolean sleep = false;
	private long updateTime;
	private long drawTime;

	public GameLoopFixed( int maxUpdates, double updateRate, double drawRate )
	{
		this.maxUpdates = maxUpdates;
		this.updateRate = (long)(updateRate * 1000000000L);
		this.drawRate = (long)(drawRate * 1000000000L);
	}

	public GameLoopFixed( int maxUpdates, long updateRate, long drawRate, TimeUnit timeUnit )
	{
		this( maxUpdates, updateRate, drawRate, timeUnit, false );
	}

	public GameLoopFixed( int maxUpdates, long updateRate, long drawRate, TimeUnit timeUnit, boolean sleep )
	{
		this.maxUpdates = maxUpdates;
		this.updateRate = timeUnit.toNanos( updateRate );
		this.drawRate = timeUnit.toNanos( drawRate );
		this.sleep = sleep;
	}

	@Override
	public void onStart( Game game, GameState state )
	{
		state.reset();
		state.setElapsed( updateRate );
	}

	@Override
	public boolean onLoop( Game game, GameState state, GameInput input, Graphics2D gr )
	{
		long nanosElapsed = state.tick();

		updateTime += nanosElapsed;

		int updateCount = 0;

		while (updateTime >= updateRate && updateCount < maxUpdates)
		{
			game.input( input );
			input.clear();

			if (!game.isPlaying())
			{
				return false;
			}

			state.update();
			game.update( state );

			if (!game.isPlaying())
			{
				return false;
			}

			updateCount++;

			updateTime -= updateRate;
		}

		drawTime += nanosElapsed;

		int drawCount = 0;

		if (drawTime >= drawRate || updateCount > 0)
		{
			state.interpolate = getStateInterpolation();
			state.forward = state.interpolate * state.seconds;
			state.backward = state.forward - state.seconds;

			state.draw();
			game.draw( state, gr );
			drawCount++;

			drawTime -= (drawRate == 0 ? drawTime : drawRate);
		}

		if (sleep && drawCount == 0 && updateCount == 0)
		{
			long actualTime = updateTime + state.getElapsedSinceTick();

			long sleep = (updateRate - actualTime) / 1000000L;

			if (sleep > 1)
			{
				try
				{
					Thread.sleep( sleep - 1 );
				}
				catch (Exception e)
				{
				}
			}
		}

		return (drawCount > 0);
	}

	public float getStateInterpolation()
	{
		return (float)((double)updateTime / (double)updateRate);
	}

}
