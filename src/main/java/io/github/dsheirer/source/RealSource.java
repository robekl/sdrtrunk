package io.github.dsheirer.source;

import io.github.dsheirer.sample.Provider;
import io.github.dsheirer.sample.SampleType;
import io.github.dsheirer.sample.buffer.ReusableDoubleBuffer;


public abstract class RealSource extends Source implements Provider<ReusableDoubleBuffer>
{
    @Override
    public SampleType getSampleType()
    {
        return SampleType.REAL;
    }
}
