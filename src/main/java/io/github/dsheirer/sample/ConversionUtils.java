package io.github.dsheirer.sample;

import io.github.dsheirer.sample.buffer.ReusableDoubleBuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * Utilities for converting to/from signed 16-bit sample byte arrays and double buffers
 */
public class ConversionUtils
{
    public static double[] convertFromSigned16BitSamples(byte[] bytes)
    {
        return convertFromSigned16BitSamples(ByteBuffer.wrap(bytes));
    }

    /**
     * Converts the byte buffer containing 16-bit samples into a float array
     */
    public static double[] convertFromSigned16BitSamples(ByteBuffer buffer)
    {
        ShortBuffer byteBuffer = buffer.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();

        double[] samples = new double[buffer.limit() / 2];

        for(int x = 0; x < samples.length; x++)
        {
            samples[x] = byteBuffer.get() / Short.MAX_VALUE;
        }

        return samples;
    }

    /**
     * Converts the float samples into a little-endian 16-bit sample byte buffer.
     *
     * @param samples - float array of sample data
     * @return - little-endian 16-bit sample byte buffer
     */
    public static ByteBuffer convertToSigned16BitSamples(double[] samples)
    {
        ByteBuffer converted = ByteBuffer.allocate(samples.length * 2);
        converted.order(ByteOrder.LITTLE_ENDIAN);

        for(double sample : samples)
        {
            converted.putShort((short)(sample * Short.MAX_VALUE));
        }

        return converted;
    }

    /**
     * Converts the float samples in a complex buffer to a little endian 16-bit
     * buffer
     */
    public static ByteBuffer convertToSigned16BitSamples(ReusableDoubleBuffer buffer)
    {
        return convertToSigned16BitSamples(buffer.getSamples());
    }
}
