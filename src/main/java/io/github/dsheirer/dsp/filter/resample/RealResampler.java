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
package io.github.dsheirer.dsp.filter.resample;

import com.laszlosystems.libresample4j.SampleBuffers;
import io.github.dsheirer.sample.Listener;
import io.github.dsheirer.sample.buffer.ReusableBufferQueue;
import io.github.dsheirer.sample.buffer.ReusableDoubleBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RealResampler
{
    protected static final Logger mLog = LoggerFactory.getLogger(RealResampler.class);

    private Resampler mResampler;
    private Listener<ReusableDoubleBuffer> mResampledListener;
    private Resampler.BufferManager mBufferManager;
    private double mResampleFactor;

    /**
     * Resampler for real sample buffers.
     * @param inputRate
     * @param outputRate
     */
    public RealResampler(double inputRate, double outputRate, int inputBufferSize, int outputBufferSize)
    {
        mResampleFactor = outputRate / inputRate;
        mResampler = new Resampler(true, mResampleFactor, mResampleFactor);
        mBufferManager = new Resampler.BufferManager(inputBufferSize, outputBufferSize);
    }

    /**
     * Primary input method to the resampler
     * @param reusableDoubleBuffer to resample
     */
    public void resample(ReusableDoubleBuffer reusableDoubleBuffer)
    {
        mBufferManager.load(reusableDoubleBuffer);
        mResampler.process(mResampleFactor, mBufferManager, false);
    }


    /**
     * Registers the listener to receive the resampled buffer output
     * @param resampledBufferListener to receive buffers
     */
    public void setListener(Listener<ReusableDoubleBuffer> resampledBufferListener)
    {
        mResampledListener = resampledBufferListener;
        mBufferManager.setResampledBufferListener(resampledBufferListener);
    }


}
