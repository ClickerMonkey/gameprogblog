
package com.gameprogblog.engine;

import java.awt.Graphics2D;
import java.util.concurrent.TimeUnit;

import com.gameprogblog.engine.input.GameInput;


public class GameLoopVariable implements GameLoop
{

	public long maximumElapsed;

	public GameLoopVariable( double maximumElapsedSeconds )
	{
		this.maximumElapsed = (long)(maximumElapsedSeconds * 1000000000L);
	}

	public GameLoopVariable( long maximumElapsed, TimeUnit timeUnit )
	{
		this.maximumElapsed = timeUnit.toNanos( maximumElapsed );
	}

	@Override
	public void onStart( Game game, GameState state )
	{
		state.reset();
	}

	@Override
	public boolean onLoop( Game game, GameState state, GameInput input, Graphics2D gr )
	{
		state.setElapsed( Math.min( maximumElapsed, state.tick() ) );

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
		
		state.draw();
		game.draw( state, gr );

		return true;
	}

}
