/*******************************************************************************
 *     SDR Trunk 
 *     Copyright (C) 2014,2015 Dennis Sheirer
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
package io.github.dsheirer.dsp.fm;

import io.github.dsheirer.sample.buffer.ReusableBufferQueue;
import io.github.dsheirer.sample.buffer.ReusableComplexBuffer;
import io.github.dsheirer.sample.buffer.ReusableDoubleBuffer;
import io.github.dsheirer.sample.complex.Complex;
import org.apache.commons.math3.util.FastMath;

/**
 * FM Demodulator for demodulating complex samples and producing demodulated floating point samples.
 */
public class FMDemodulator
{
    private ReusableBufferQueue mReusableBufferQueue = new ReusableBufferQueue("FMDemodulator");
    private double mPreviousI = 0.0d;
    private double mPreviousQ = 0.0d;
    protected double mGain;

    /**
     * Creates an FM demodulator instance with a default gain of 1.0.
     */
    public FMDemodulator()
    {
        this(1.0d);
    }

    /**
     * Creates an FM demodulator instance and applies the gain value to each demodulated output sample.
     * @param gain to apply to demodulated samples.
     */
    public FMDemodulator(double gain)
    {
        mGain = gain;
    }

    /**
     * Demodulates the I/Q sample via complex sample multiplication by the complex conjugates of the most recently
     * demodulated complex sample (ie previous sample).  Each new sample overwrites the previously stored sample to
     * allow future invocations of the method to simply use a new sample value.
     *
     * @param currentI of the sample
     * @param currentQ of the sample
     * @return demodulated sample
     */
    public double demodulate(double currentI, double currentQ)
    {
        /**
         * Multiply the current sample against the complex conjugate of the
         * previous sample to derive the phase delta between the two samples
         *
         * Negating the previous sample quadrature produces the conjugate
         */
        double inphase = (currentI * mPreviousI) - (currentQ * -mPreviousQ);
        double quadrature = (currentQ * mPreviousI) + (currentI * -mPreviousQ);

        double angle = 0.0d;

        //Check for divide by zero
        if(inphase != 0)
        {
            /**
             * Use the arc-tangent of quadrature divided by inphase to
             * get the phase angle (+/-) which was directly manipulated by the
             * original message waveform during the modulation.  This value now
             * serves as the instantaneous amplitude of the demodulated signal
             */
            double denominator = 1.0d / inphase;
            angle = FastMath.atan(quadrature * denominator);
        }

        /**
         * Store the current sample to use during the next iteration
         */
        mPreviousI = currentI;
        mPreviousQ = currentQ;

        return (angle * mGain);
    }

    /**
     * Demodulates the complex samples and returns the demodulated value.
     * @param previous
     * @param current
     * @return
     */
    public static double demodulate(Complex previous, Complex current)
    {
        double inphase = (current.inphase() * previous.inphase()) - (current.quadrature() * -previous.quadrature());
        double quadrature = (current.quadrature() * previous.inphase()) + (current.inphase() * -previous.quadrature());

        double angle = 0.0d;

        //Check for divide by zero
        if(inphase != 0)
        {
            /**
             * Use the arc-tangent of quadrature divided by inphase to
             * get the phase angle (+/-) which was directly manipulated by the
             * original message waveform during the modulation.  This value now
             * serves as the instantaneous amplitude of the demodulated signal
             */
            double denominator = 1.0d / inphase;
            angle = FastMath.atan(quadrature * denominator);
        }

        return angle;
    }

    /**
     * Demodulates the complex baseband sample buffer and returns a demodulated reusable buffer with the user count
     * set to 1.  The complex baseband buffer's user count is decremented after demodulation.
     *
     * @param basebandSampleBuffer containing samples to demodulate
     * @return demodulated sample buffer.
     */
    public ReusableDoubleBuffer demodulate(ReusableComplexBuffer basebandSampleBuffer)
    {
        ReusableDoubleBuffer demodulatedBuffer = mReusableBufferQueue.getBuffer(basebandSampleBuffer.getSampleCount());

        double[] basebandSamples = basebandSampleBuffer.getSamples();
        double[] demodulatedSamples = demodulatedBuffer.getSamples();

        for(int x = 0; x < basebandSamples.length; x += 2)
        {
            demodulatedSamples[x / 2] = demodulate(basebandSamples[x], basebandSamples[x + 1]);
        }

        basebandSampleBuffer.decrementUserCount();

        return demodulatedBuffer;
    }

    public void dispose()
    {
        //no-op
    }

    /**
     * Resets this demodulator by zeroing the stored previous sample.
     */
    public void reset()
    {
        mPreviousI = 0.0d;
        mPreviousQ = 0.0d;
    }

    /**
     * Sets the gain to the specified level.
     */
    public void setGain(double gain)
    {
        mGain = gain;
    }
}