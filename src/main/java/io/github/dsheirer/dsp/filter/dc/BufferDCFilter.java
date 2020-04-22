/*******************************************************************************
 * sdr-trunk
 * Copyright (C) 2014-2018 Dennis Sheirer
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by  the Free Software Foundation, either version 3 of the License, or  (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful,  but WITHOUT ANY WARRANTY; without even the implied
 * warranty of  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License  along with this program.
 * If not, see <http://www.gnu.org/licenses/>
 *
 ******************************************************************************/
package io.github.dsheirer.dsp.filter.dc;

import io.github.dsheirer.sample.buffer.ReusableBufferQueue;
import io.github.dsheirer.sample.buffer.ReusableDoubleBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BufferDCFilter
{
    private final static Logger mLog = LoggerFactory.getLogger(BufferDCFilter.class);
    private ReusableBufferQueue mReusableBufferQueue = new ReusableBufferQueue("Buffer DC Filter");
    private double mAccumulator;

    /**
     * Calculates the DC offset present in the samples and subtracts the offset from all samples.
     */
    public void filterReal(double[] samples)
    {
        double adjustment = getOffset(samples);

        for(int x = 0; x < samples.length; x++)
        {
            samples[x] = samples[x] - adjustment;
        }
    }

    private double getOffset(double[] samples)
    {
        mAccumulator = 0.0;

        for (double sample : samples) {
            mAccumulator += sample;
        }

        return mAccumulator / (double)samples.length;
    }

    /**
     * Calculates the DC offset present in the samples and subtracts the offset from all samples.
     */
    public static void filterComplex(double[] samples)
    {
        double iAccumulator = 0.0;
        double qAccumulator = 0.0;

        for(int x = 0; x < samples.length; x += 2)
        {
            iAccumulator += samples[x];
            qAccumulator += samples[x + 1];
        }

        double half = (double)samples.length / 2.0;

        double iAdjustment = iAccumulator / half;
        double qAdjustment = qAccumulator / half;

        for(int x = 0; x < samples.length; x += 2)
        {
            samples[x] = samples[x] - iAdjustment;
            samples[x + 1] = samples[x + 1] - qAdjustment;
        }
    }

    public ReusableDoubleBuffer filter(ReusableDoubleBuffer unfilteredBuffer)
    {
        double[] unfilteredSamples = unfilteredBuffer.getSamples();

        ReusableDoubleBuffer filteredBuffer = mReusableBufferQueue.getBuffer(unfilteredSamples.length);
        double[] filteredSamples = filteredBuffer.getSamples();

        double offset = getOffset(unfilteredSamples);

        for(int x = 0; x < unfilteredSamples.length; x++)
        {
            filteredSamples[x] = unfilteredSamples[x] - offset;
        }

        unfilteredBuffer.decrementUserCount();

        return filteredBuffer;
    }
}
