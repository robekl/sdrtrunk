package io.github.dsheirer.sample.adapter;

public class ShortToDoubleMap
{
	private static double[] MAP = new double[ 65536 ];
	
	static
	{
		for( int x = 0; x < 65536; x++ )
		{
			MAP[ x] = (double)( x - 32768 ) / 32768.0d;
		}
	}

	public double get( short value )
	{
		return MAP[ 32768 + value ];
	}
}
