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
package io.github.dsheirer.dsp.am;

import io.github.dsheirer.sample.buffer.ReusableBufferQueue;
import io.github.dsheirer.sample.buffer.ReusableComplexBuffer;
import io.github.dsheirer.sample.buffer.ReusableDoubleBuffer;
import org.apache.commons.math3.util.FastMath;

/**
 * Performs AM demodulation on baseband I/Q samples to produce demodulated float output.
 */
public class AMDemodulator
{
    private ReusableBufferQueue mReusableBufferQueue = new ReusableBufferQueue("AMDemodulator");
    private int mOutputBufferPointer;
    private double mGain;

    /**
     * Constructs this demodulator where the specified gain is applied to demodulated output samples.
     *
     * @param gain to apply to demodulated output samples.
     */
    public AMDemodulator(double gain)
    {
        mGain = gain;
    }

    /**
     * Sets the gain to the specified level.
     */
    public void setGain(double gain)
    {
        mGain = gain;
    }

    /**
     * Demodulates the comples I/Q sample and returns the demodulated output sample.
     *
     * @param inphase sample
     * @param quadrature sample
     * @return AM demodulated sample
     */
    public double demodulate(double inphase, double quadrature)
    {
        return FastMath.sqrt((inphase * inphase) + (quadrature * quadrature)) * mGain;
    }

    /**
     * Demodulates the reusable comples sample buffer and returns a demodulated audio buffer.
     * @param complexBuffer to demodulate
     * @return demodulated audio buffer.
     */
    public ReusableDoubleBuffer demodulate(ReusableComplexBuffer complexBuffer)
    {
        ReusableDoubleBuffer reusableDoubleBuffer = mReusableBufferQueue.getBuffer(complexBuffer.getSampleCount());
        double[] input = complexBuffer.getSamples();
        double[] output = reusableDoubleBuffer.getSamples();
        mOutputBufferPointer = 0;

        for(int x= 0; x < complexBuffer.getSamples().length; x += 2)
        {
            output[mOutputBufferPointer++] = demodulate(input[x], input[x + 1]);
        }

        complexBuffer.decrementUserCount();

        return reusableDoubleBuffer;
    }
}
