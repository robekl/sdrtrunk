package io.github.dsheirer.dsp.filter.interpolator;

import io.github.dsheirer.sample.complex.Complex;
import org.apache.commons.lang3.Validate;
import org.apache.commons.math3.util.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Arrays;

public class RealInterpolator extends Interpolator
{
    private final static Logger mLog = LoggerFactory.getLogger(RealInterpolator.class);

    private double mGain;

    /**
     * Provides an interpolated sample point along an eight-sample waveform representation using 128 filters to
     * provide approximate interpolation with a resolution of 1/128th of a sample.
     *
     * @param gain to apply to the interpolated sample
     */
    public RealInterpolator(double gain)
    {
        mGain = gain;
    }

    /**
     * Calculates an interpolated value from eight samples that start at the offset into the sample array.  The
     * interpolated sample will fall within the middle of the eight sample array, between indexes offset+3 and
     * offset+4.  The mu argument is translated into an index position between 0 and 128, where a mu of 0.0 will be
     * converted to index zero and will be equal to the sample at index offset+3 and a mu of 1.0 will be equal to
     * the sample at offset+4.  All mu values between 0.0 and 1.0 will be converted to a 1 - 127 index and will
     * produce an approximated value from among 127 interpolated sample points between indexes offset+3 and offset+4.
     *
     * @param samples - sample array of length at least offset + 7
     * @param mu - interpolated sample position between 0.0 and 1.0
     * @return - interpolated sample value
     */
    public double filter(double[] samples, int offset, double mu)
    {
        /* Ensure we have enough samples in the array */
        Validate.isTrue(samples.length >= offset + 7);

        /* Identify the filter bank that corresponds to mu */
        int index = (int)(NSTEPS * mu);

        double accumulator = (TAPS[index][7] * samples[offset]);
        accumulator += (TAPS[index][6] * samples[offset + 1]);
        accumulator += (TAPS[index][5] * samples[offset + 2]);
        accumulator += (TAPS[index][4] * samples[offset + 3]);
        accumulator += (TAPS[index][3] * samples[offset + 4]);
        accumulator += (TAPS[index][2] * samples[offset + 5]);
        accumulator += (TAPS[index][1] * samples[offset + 6]);
        accumulator += (TAPS[index][0] * samples[offset + 7]);

        return accumulator * mGain;
    }

    public Complex filter(double[] iSamples, double[] qSamples, int offset, double mu)
    {
        double i = filter(iSamples, offset, mu);
        double q = filter(qSamples, offset, mu);

        return new Complex(i, q);
    }

    public static void main(String[] args)
    {
        RealInterpolator interpolator = new RealInterpolator(1.0d);
        DecimalFormat decimalFormat = new DecimalFormat("0.0000");

        double TWO_PI = FastMath.PI * 2.0;

        double[] samples = new double[16];

		for(int x = 0; x < 16; x++)
        {
            samples[x] = FastMath.sin(TWO_PI * (double)x / 8.0);
        }

        mLog.debug("Samples: " + Arrays.toString(samples));

        for(double x = 0.0d; x <= 1.01d; x += 0.1d)
        {
            mLog.debug(decimalFormat.format(x) + ": " + decimalFormat.format(interpolator.filter(samples, 1, x)));
        }

    }
}
