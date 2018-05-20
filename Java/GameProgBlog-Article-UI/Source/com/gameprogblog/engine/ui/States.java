package com.gameprogblog.engine.ui;


public class States
{
	public static final int All			= 0x7FFFFFFF;
	public static final int Normal		= (1 << 0);
	public static final int Disabled 	= (1 << 1);
	public static final int Focused 		= (1 << 2);
	public static final int Hovering 	= (1 << 3);
	public static final int Pressed		= (1 << 4);
	public static final int Selected		= (1 << 5);
	
	public static boolean hasState(int states, int specificState)
	{
		return (states & specificState) != 0;
	}
	
	public static boolean isState(int states, int specificState)
	{
		return (states == specificState);
	}
	
	public static boolean hasAllStates(int states, int specificStates)
	{
		return (states & specificStates) == specificStates;
	}
	
}
