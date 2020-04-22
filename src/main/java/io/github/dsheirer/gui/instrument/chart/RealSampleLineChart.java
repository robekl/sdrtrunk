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
package io.github.dsheirer.gui.instrument.chart;

import io.github.dsheirer.buffer.RealCircularBuffer;
import io.github.dsheirer.sample.Listener;
import io.github.dsheirer.sample.buffer.ReusableDoubleBuffer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RealSampleLineChart extends LineChart implements Listener<ReusableDoubleBuffer>
{
    private final static Logger mLog = LoggerFactory.getLogger(RealSampleLineChart.class);
    private ObservableList<Data<Integer,Double>> mSamples = FXCollections.observableArrayList();
    private Series<Integer,Double> mSampleSeries = new Series<>("Samples", mSamples);
    private RealCircularBuffer mRealCircularBuffer;

    public RealSampleLineChart(int length, double tickUnit)
    {
        super(new NumberAxis("Samples", 0, length, tickUnit),
            new NumberAxis("Value", -1.0, 1.0, 0.25));

        ObservableList<Series<Integer,Double>> observableList = FXCollections.observableArrayList(mSampleSeries);

        setData(observableList);

        init(length);
    }

    private void init(int length)
    {
        mRealCircularBuffer = new RealCircularBuffer(length);

        for(int x = 0; x < length; x++)
        {
            Data<Integer,Double> sample = new Data<>(x, 0.0d);
            mSamples.add(sample);
        }

        //Turn off the data point marker symbols
        for(XYChart.Data data: mSampleSeries.getData())
        {
            StackPane stackPane = (StackPane)data.getNode();
            stackPane.setVisible(false);
        }
    }

    @Override
    public void receive(ReusableDoubleBuffer buffer)
    {
        for(double sample: buffer.getSamples())
        {
            mRealCircularBuffer.put(sample);
        }

        double[] bufferSamples = mRealCircularBuffer.get();

        for(int x = 0; x < bufferSamples.length; x++)
        {
            Data<Integer,Double> sample = mSamples.get(x);
            sample.setYValue(bufferSamples[x]);
        }

        buffer.decrementUserCount();
    }
}
