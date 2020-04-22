/*******************************************************************************
 *     SDR Trunk 
 *     Copyright (C) 2014 Dennis Sheirer
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 ******************************************************************************/
package io.github.dsheirer.buffer;

public class DoubleAveragingBuffer
{
	private double[] mBuffer;
	private double mAverage = 0.0d;
	private int mBufferSize;
	private int mBufferPointer;
	
	public DoubleAveragingBuffer(int size )
	{
		mBufferSize = size;
		mBuffer = new double[ size ];
	}
	
	public double get( double newValue )
	{
		double oldValue = mBuffer[ mBufferPointer ];

		if( Double.isInfinite( newValue ) || Double.isNaN( newValue ) )
		{
			mAverage = mAverage - ( oldValue / mBufferSize );

			mBuffer[ mBufferPointer++ ] = 0.0d;
		}
		else
		{
			mAverage = mAverage - ( oldValue / mBufferSize ) + ( newValue / mBufferSize );

			mBuffer[ mBufferPointer++ ] = newValue;
		}

		if( mBufferPointer >= mBufferSize )
		{
			mBufferPointer = 0;
		}

		return mAverage;
	}
}
