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
package io.github.dsheirer.dsp.gain;

import io.github.dsheirer.buffer.DoubleCircularBuffer;
import io.github.dsheirer.sample.buffer.ReusableComplexBuffer;
import io.github.dsheirer.sample.buffer.ReusableComplexBufferQueue;
import io.github.dsheirer.sample.complex.Complex;
import io.github.dsheirer.sample.complex.ComplexSampleListener;

public class ComplexFeedForwardGainControl implements ComplexSampleListener
{
    public static final double OBJECTIVE_ENVELOPE = 1.0d;
    public static final double MINIMUM_ENVELOPE = 0.0001d;

    private ComplexSampleListener mListener;
    private DoubleCircularBuffer mEnvelopeHistory;
    private ReusableComplexBufferQueue mReusableComplexBufferQueue = new ReusableComplexBufferQueue("ComplexFeedForwardGainControl");

    private double mMaxEnvelope = 0.0d;
    private double mGain = 1.0d;

    /**
     * Dynamic gain control for incoming sample stream to amplify or attenuate
     * all samples toward an objective unity)gain, using the maximum envelope
     * value detected in the stream history window.
     *
     * Uses the specified damping factor to limit gain swings.  Damping factor
     * is applied against the delta between current gain value and a recalculated
     * gain value to limit how quickly the gain value will increase or decrease.
     *
     * @param window - history size to use in detecting maximum envelope value
     */
    public ComplexFeedForwardGainControl(int window)
    {
        mEnvelopeHistory = new DoubleCircularBuffer(window);
    }

    public void dispose()
    {
        mListener = null;
    }

    @Override
    public void receive(double inphase, double quadrature)
    {
        process(inphase, quadrature);

        /* Apply current gain value to the sample and send to the listener */
        if(mListener != null)
        {
            mListener.receive(inphase *= mGain, quadrature *= mGain);
        }
    }

    private void process(double inphase, double quadrature)
    {
        double envelope = Complex.envelope(inphase, quadrature);

        if(envelope > mMaxEnvelope)
        {
            mMaxEnvelope = envelope;

            adjustGain();
        }

        /* Replace oldest envelope value with current envelope value */
        double oldestEnvelope = mEnvelopeHistory.get(envelope);

        /* If the oldest envelope value was the max envelope value, then we
         * have to rediscover the max value from the envelope history */
        if(mMaxEnvelope == oldestEnvelope && mMaxEnvelope != envelope)
        {
            mMaxEnvelope = MINIMUM_ENVELOPE;

            for(double value : mEnvelopeHistory.getBuffer())
            {
                if(value > mMaxEnvelope)
                {
                    mMaxEnvelope = value;
                }
            }

            adjustGain();
        }
    }

    private void adjustGain()
    {
        mGain = OBJECTIVE_ENVELOPE / mMaxEnvelope;
    }

    public void setListener(ComplexSampleListener listener)
    {
        mListener = listener;
    }

    public double[] filter(double[] complesSamples)
    {
        mMaxEnvelope = MINIMUM_ENVELOPE;
        double currentEnvelope;

        for(int x = 0; x < complesSamples.length; x += 2)
        {
            currentEnvelope = Complex.envelope(complesSamples[x], complesSamples[x + 1]);

            if(currentEnvelope > mMaxEnvelope)
            {
                mMaxEnvelope = currentEnvelope;
            }
        }

        adjustGain();

        double[] processed = new double[complesSamples.length];

        for(int x = 0; x < complesSamples.length; x += 2)
        {
            processed[x] = complesSamples[x] * mGain;
            processed[x + 1] = complesSamples[x + 1] * mGain;
        }

        return processed;
    }

    /**
     * Applies gain to the complex sample buffer
     * @param buffer to apply gain
     * @return reusable buffer with the user count incremented to 1
     */
    public ReusableComplexBuffer filter(ReusableComplexBuffer buffer)
    {
        double[] samples = buffer.getSamples();

        ReusableComplexBuffer filteredBuffer = mReusableComplexBufferQueue.getBuffer(samples.length);
        filteredBuffer.setTimestamp(buffer.getTimestamp());

        double[] filtered = filteredBuffer.getSamples();

        mMaxEnvelope = MINIMUM_ENVELOPE;

        double currentEnvelope;

        for(int x = 0; x < samples.length; x += 2)
        {
            currentEnvelope = Complex.envelope(samples[x], samples[x + 1]);

            if(currentEnvelope > mMaxEnvelope)
            {
                mMaxEnvelope = currentEnvelope;
            }
        }

        adjustGain();

        for(int x = 0; x < samples.length; x += 2)
        {
            filtered[x] = samples[x] * mGain;
            filtered[x + 1] = samples[x + 1] * mGain;
        }

        buffer.decrementUserCount();

        return filteredBuffer;
    }
}
