package com.gameprogblog.engine.util;

import java.util.Arrays;
import java.util.Collection;

public class ArrayUtil 
{
	
	public static <T> T[] fromCollection( Collection<T> items ) 
	{
		return items.toArray( (T[])new Object[ items.size()] );
	}

	public static <T> T[] add(T e, T[] elements)
	{
		int size = elements.length;
		elements = Arrays.copyOf(elements, size + 1);
		elements[size] = e;
		return elements;
	}
	
	public static float[] add(float e, float[] elements)
	{
		int size = elements.length;
		elements = Arrays.copyOf(elements, size + 1);
		elements[size] = e;
		return elements;
	}
	
	public static int[] add(int e, int[] elements)
	{
		int size = elements.length;
		elements = Arrays.copyOf(elements, size + 1);
		elements[size] = e;
		return elements;
	}
	
	public static boolean[] add(boolean e, boolean[] elements)
	{
		int size = elements.length;
		elements = Arrays.copyOf(elements, size + 1);
		elements[size] = e;
		return elements;
	}
	
	public static <T> void insert( int index, T e, T[] elements )
	{
		 System.arraycopy( elements, index, elements, index + 1, elements.length - index );
		 
		 elements[ index ] = e;
	}
	
	public static <T> T remove( int index, T[] elements )
	{
		T e = elements[ index ];
		
		System.arraycopy( elements, index - 1, elements, index, elements.length - index );
		
		return e;
	}
	
	public static <T> int indexOf(T e, T[] elements)
	{
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] == e) {
				return i;
			}
		}
		return -1;
	}
	
	public static <T> T[] remove(T e, T[] elements) 
	{
		int i = indexOf(e, elements);
		if (i >= 0) {
			int size = elements.length - 1;
			elements[i] = elements[size];
			elements = Arrays.copyOf(elements, size);
		}
		return elements;
	}
	
	public static <T> T[] select( T[] elements, int ... indices )
	{
		T[] copy = Arrays.copyOf( elements, indices.length );

		for (int i = 0; i < indices.length; i++) 
		{
			copy[i] = elements[ indices[i] ];
		}
		
		return copy;
	}
	
	public static <T, E extends Enum<E>> T[] select( T[] elements, E ... enumConstants )
	{
		T[] copy = Arrays.copyOf( elements, enumConstants.length );

		for (int i = 0; i < enumConstants.length; i++) 
		{
			copy[i] = elements[ enumConstants[i].ordinal() ];
		}
		
		return copy;
	}
	
	public static <T> T[] selectRanges( T[] elements, int ... indices )
	{
		int total = 0, current = 0;
		int[] dir = new int[ indices.length >> 1 ];
		
		for ( int i = 0; i < indices.length; i+=2 )
		{
			int k = i >> 1;
			int d = indices[ i + 1 ] - indices[ i ];
			dir[ k ] = Integer.signum( d );
			total += d;
		}
		
		T[] copy = Arrays.copyOf( elements, total );
		
		for ( int i = 0; i < indices.length; i+=2 )
		{
			for (int k = indices[i]; k != indices[i + 1]; k += dir[ i >> 1 ] ) 
			{
				copy[ current++ ] = elements[ k ];
			}
			copy[ current++ ] = elements[ indices[i + 1] ];
		}
		
		return copy;
	}
	
	public static <T> T[] place(int relativeIndex, T value, T[] array)
	{
		if (relativeIndex < 0) 
		{
			array = Arrays.copyOf( array, array.length - relativeIndex );
			System.arraycopy( array, 0, array, -relativeIndex, array.length + relativeIndex );
			Arrays.fill( array, 0, -relativeIndex - 1, null );
			array[0] = value;
		}
		else if (relativeIndex >= array.length)
		{
			array = Arrays.copyOf( array, relativeIndex + 1 );
			array[relativeIndex] = value;
		}
		else
		{
			array[relativeIndex] = value;
		}
		
		return array;
	}
	
	public static <T> T[] trim(T[] array)
	{
		int tn = trailingNulls( array );
		
		if (tn > 0) {
			array = Arrays.copyOf( array, array.length - tn );
		}
		
		int ln = leadingNulls( array );
		
		if (ln > 0) {
			int z = array.length - ln;
			System.arraycopy( array, ln, array, 0, z );
			array = Arrays.copyOf( array, z );
		}
		
		return array;
	}
	
	public static <T> int leadingNulls(T[] input)
	{
		int nullCount = 0;

		while (nullCount < input.length && input[nullCount] == null) 
		{
			nullCount++;
		}
		
		return nullCount;
	}
	
	public static <T> int trailingNulls(T[] input)
	{
		int n = input.length;
		int nullCount = 0;
		
		while (nullCount < input.length && input[--n] == null) 
		{
			nullCount++;
		}
		
		return nullCount;
	}

	public static <T> T[] resize( T[] array )
	{
		return resize( array, 1 );
	}
	
	public static <T> T[] resize( T[] array, int minimumLength )
	{
		int length = Math.max( minimumLength, array.length + (array.length >> 1) );
		
		return Arrays.copyOf( array, length );
	}
	
	public static <T> T[] duplify( T[] array )
	{
		return duplify( array, 1 );
	}
	
	public static <T> T[] duplify( T[] array, int minimumLength )
	{
		int length = Math.max( minimumLength, array.length << 1 );
		
		return Arrays.copyOf( array, length );
	}
	
}
