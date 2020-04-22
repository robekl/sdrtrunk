package io.github.dsheirer.spectrum.converter;

import org.apache.commons.math3.util.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RealDecibelConverter extends DFTResultsConverter
{
	private static final Logger mLog = LoggerFactory.getLogger( RealDecibelConverter.class );

	public RealDecibelConverter()
	{
	}
	
	/**
	 * Converts the output of the JTransforms DoubleFFT_1D.realForward()
	 * calculation into a normalized power spectrum in decibels, per description
	 * in Lyons, Understanding Digital Signal Processing, 3e, page 141.
	 * 
	 * Note: this is only calculating the lower half of the spectrum
	 */
	@Override
    public void receive( double[] results )
    {
		double dftBinSizeScalor = 1.0d / (double)results.length;

		double[] processed = new double[ results.length / 4 ];

		int index = 0;
		
		for( int x = 0; x < processed.length; x ++ )
		{
			index = x * 2;
			
			processed[ x ] = 20.0d * FastMath.log10(
				( ( results[ index ] * results[ index ] ) + 
				  ( results[ index + 1 ] * results[ index + 1 ] ) ) * dftBinSizeScalor );
		}

		dispatch( processed );
    }
}
