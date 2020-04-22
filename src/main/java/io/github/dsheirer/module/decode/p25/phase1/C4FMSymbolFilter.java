/*
 * ******************************************************************************
 * sdrtrunk
 * Copyright (C) 2014-2019 Dennis Sheirer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * *****************************************************************************
 */

package io.github.dsheirer.module.decode.p25.phase1;

import io.github.dsheirer.dsp.gain.DirectGainControl;
import io.github.dsheirer.sample.Listener;
import io.github.dsheirer.sample.buffer.ReusableDoubleBuffer;
import io.github.dsheirer.sample.real.RealSampleListener;
import io.github.dsheirer.sample.real.RealSampleProvider;
import io.github.dsheirer.source.ISourceEventListener;
import io.github.dsheirer.source.SourceEvent;
import org.apache.commons.math3.util.FastMath;

public class C4FMSymbolFilter implements Listener<ReusableDoubleBuffer>, ISourceEventListener, RealSampleProvider
{
    private static final double TAPS[][] =
        {
            {0.00000e+00d, 0.00000e+00d, 0.00000e+00d, 0.00000e+00d, 1.00000e+00d, 0.00000e+00d, 0.00000e+00d, 0.00000e+00d}, //   0/128
            {-1.54700e-04d, 8.53777e-04d, -2.76968e-03d, 7.89295e-03d, 9.98534e-01d, -5.41054e-03d, 1.24642e-03d, -1.98993e-04d}, //   1/128
            {-3.09412e-04d, 1.70888e-03d, -5.55134e-03d, 1.58840e-02d, 9.96891e-01d, -1.07209e-02d, 2.47942e-03d, -3.96391e-04d}, //   2/128
            {-4.64053e-04d, 2.56486e-03d, -8.34364e-03d, 2.39714e-02d, 9.95074e-01d, -1.59305e-02d, 3.69852e-03d, -5.92100e-04d}, //   3/128
            {-6.18544e-04d, 3.42130e-03d, -1.11453e-02d, 3.21531e-02d, 9.93082e-01d, -2.10389e-02d, 4.90322e-03d, -7.86031e-04d}, //   4/128
            {-7.72802e-04d, 4.27773e-03d, -1.39548e-02d, 4.04274e-02d, 9.90917e-01d, -2.60456e-02d, 6.09305e-03d, -9.78093e-04d}, //   5/128
            {-9.26747e-04d, 5.13372e-03d, -1.67710e-02d, 4.87921e-02d, 9.88580e-01d, -3.09503e-02d, 7.26755e-03d, -1.16820e-03d}, //   6/128
            {-1.08030e-03d, 5.98883e-03d, -1.95925e-02d, 5.72454e-02d, 9.86071e-01d, -3.57525e-02d, 8.42626e-03d, -1.35627e-03d}, //   7/128
            {-1.23337e-03d, 6.84261e-03d, -2.24178e-02d, 6.57852e-02d, 9.83392e-01d, -4.04519e-02d, 9.56876e-03d, -1.54221e-03d}, //   8/128
            {-1.38589e-03d, 7.69462e-03d, -2.52457e-02d, 7.44095e-02d, 9.80543e-01d, -4.50483e-02d, 1.06946e-02d, -1.72594e-03d}, //   9/128
            {-1.53777e-03d, 8.54441e-03d, -2.80746e-02d, 8.31162e-02d, 9.77526e-01d, -4.95412e-02d, 1.18034e-02d, -1.90738e-03d}, //  10/128
            {-1.68894e-03d, 9.39154e-03d, -3.09033e-02d, 9.19033e-02d, 9.74342e-01d, -5.39305e-02d, 1.28947e-02d, -2.08645e-03d}, //  11/128
            {-1.83931e-03d, 1.02356e-02d, -3.37303e-02d, 1.00769e-01d, 9.70992e-01d, -5.82159e-02d, 1.39681e-02d, -2.26307e-03d}, //  12/128
            {-1.98880e-03d, 1.10760e-02d, -3.65541e-02d, 1.09710e-01d, 9.67477e-01d, -6.23972e-02d, 1.50233e-02d, -2.43718e-03d}, //  13/128
            {-2.13733e-03d, 1.19125e-02d, -3.93735e-02d, 1.18725e-01d, 9.63798e-01d, -6.64743e-02d, 1.60599e-02d, -2.60868e-03d}, //  14/128
            {-2.28483e-03d, 1.27445e-02d, -4.21869e-02d, 1.27812e-01d, 9.59958e-01d, -7.04471e-02d, 1.70776e-02d, -2.77751e-03d}, //  15/128
            {-2.43121e-03d, 1.35716e-02d, -4.49929e-02d, 1.36968e-01d, 9.55956e-01d, -7.43154e-02d, 1.80759e-02d, -2.94361e-03d}, //  16/128
            {-2.57640e-03d, 1.43934e-02d, -4.77900e-02d, 1.46192e-01d, 9.51795e-01d, -7.80792e-02d, 1.90545e-02d, -3.10689e-03d}, //  17/128
            {-2.72032e-03d, 1.52095e-02d, -5.05770e-02d, 1.55480e-01d, 9.47477e-01d, -8.17385e-02d, 2.00132e-02d, -3.26730e-03d}, //  18/128
            {-2.86289e-03d, 1.60193e-02d, -5.33522e-02d, 1.64831e-01d, 9.43001e-01d, -8.52933e-02d, 2.09516e-02d, -3.42477e-03d}, //  19/128
            {-3.00403e-03d, 1.68225e-02d, -5.61142e-02d, 1.74242e-01d, 9.38371e-01d, -8.87435e-02d, 2.18695e-02d, -3.57923e-03d}, //  20/128
            {-3.14367e-03d, 1.76185e-02d, -5.88617e-02d, 1.83711e-01d, 9.33586e-01d, -9.20893e-02d, 2.27664e-02d, -3.73062e-03d}, //  21/128
            {-3.28174e-03d, 1.84071e-02d, -6.15931e-02d, 1.93236e-01d, 9.28650e-01d, -9.53307e-02d, 2.36423e-02d, -3.87888e-03d}, //  22/128
            {-3.41815e-03d, 1.91877e-02d, -6.43069e-02d, 2.02814e-01d, 9.23564e-01d, -9.84679e-02d, 2.44967e-02d, -4.02397e-03d}, //  23/128
            {-3.55283e-03d, 1.99599e-02d, -6.70018e-02d, 2.12443e-01d, 9.18329e-01d, -1.01501e-01d, 2.53295e-02d, -4.16581e-03d}, //  24/128
            {-3.68570e-03d, 2.07233e-02d, -6.96762e-02d, 2.22120e-01d, 9.12947e-01d, -1.04430e-01d, 2.61404e-02d, -4.30435e-03d}, //  25/128
            {-3.81671e-03d, 2.14774e-02d, -7.23286e-02d, 2.31843e-01d, 9.07420e-01d, -1.07256e-01d, 2.69293e-02d, -4.43955e-03d}, //  26/128
            {-3.94576e-03d, 2.22218e-02d, -7.49577e-02d, 2.41609e-01d, 9.01749e-01d, -1.09978e-01d, 2.76957e-02d, -4.57135e-03d}, //  27/128
            {-4.07279e-03d, 2.29562e-02d, -7.75620e-02d, 2.51417e-01d, 8.95936e-01d, -1.12597e-01d, 2.84397e-02d, -4.69970e-03d}, //  28/128
            {-4.19774e-03d, 2.36801e-02d, -8.01399e-02d, 2.61263e-01d, 8.89984e-01d, -1.15113e-01d, 2.91609e-02d, -4.82456e-03d}, //  29/128
            {-4.32052e-03d, 2.43930e-02d, -8.26900e-02d, 2.71144e-01d, 8.83893e-01d, -1.17526e-01d, 2.98593e-02d, -4.94589e-03d}, //  30/128
            {-4.44107e-03d, 2.50946e-02d, -8.52109e-02d, 2.81060e-01d, 8.77666e-01d, -1.19837e-01d, 3.05345e-02d, -5.06363e-03d}, //  31/128
            {-4.55932e-03d, 2.57844e-02d, -8.77011e-02d, 2.91006e-01d, 8.71305e-01d, -1.22047e-01d, 3.11866e-02d, -5.17776e-03d}, //  32/128
            {-4.67520e-03d, 2.64621e-02d, -9.01591e-02d, 3.00980e-01d, 8.64812e-01d, -1.24154e-01d, 3.18153e-02d, -5.28823e-03d}, //  33/128
            {-4.78866e-03d, 2.71272e-02d, -9.25834e-02d, 3.10980e-01d, 8.58189e-01d, -1.26161e-01d, 3.24205e-02d, -5.39500e-03d}, //  34/128
            {-4.89961e-03d, 2.77794e-02d, -9.49727e-02d, 3.21004e-01d, 8.51437e-01d, -1.28068e-01d, 3.30021e-02d, -5.49804e-03d}, //  35/128
            {-5.00800e-03d, 2.84182e-02d, -9.73254e-02d, 3.31048e-01d, 8.44559e-01d, -1.29874e-01d, 3.35600e-02d, -5.59731e-03d}, //  36/128
            {-5.11376e-03d, 2.90433e-02d, -9.96402e-02d, 3.41109e-01d, 8.37557e-01d, -1.31581e-01d, 3.40940e-02d, -5.69280e-03d}, //  37/128
            {-5.21683e-03d, 2.96543e-02d, -1.01915e-01d, 3.51186e-01d, 8.30432e-01d, -1.33189e-01d, 3.46042e-02d, -5.78446e-03d}, //  38/128
            {-5.31716e-03d, 3.02507e-02d, -1.04150e-01d, 3.61276e-01d, 8.23188e-01d, -1.34699e-01d, 3.50903e-02d, -5.87227e-03d}, //  39/128
            {-5.41467e-03d, 3.08323e-02d, -1.06342e-01d, 3.71376e-01d, 8.15826e-01d, -1.36111e-01d, 3.55525e-02d, -5.95620e-03d}, //  40/128
            {-5.50931e-03d, 3.13987e-02d, -1.08490e-01d, 3.81484e-01d, 8.08348e-01d, -1.37426e-01d, 3.59905e-02d, -6.03624e-03d}, //  41/128
            {-5.60103e-03d, 3.19495e-02d, -1.10593e-01d, 3.91596e-01d, 8.00757e-01d, -1.38644e-01d, 3.64044e-02d, -6.11236e-03d}, //  42/128
            {-5.68976e-03d, 3.24843e-02d, -1.12650e-01d, 4.01710e-01d, 7.93055e-01d, -1.39767e-01d, 3.67941e-02d, -6.18454e-03d}, //  43/128
            {-5.77544e-03d, 3.30027e-02d, -1.14659e-01d, 4.11823e-01d, 7.85244e-01d, -1.40794e-01d, 3.71596e-02d, -6.25277e-03d}, //  44/128
            {-5.85804e-03d, 3.35046e-02d, -1.16618e-01d, 4.21934e-01d, 7.77327e-01d, -1.41727e-01d, 3.75010e-02d, -6.31703e-03d}, //  45/128
            {-5.93749e-03d, 3.39894e-02d, -1.18526e-01d, 4.32038e-01d, 7.69305e-01d, -1.42566e-01d, 3.78182e-02d, -6.37730e-03d}, //  46/128
            {-6.01374e-03d, 3.44568e-02d, -1.20382e-01d, 4.42134e-01d, 7.61181e-01d, -1.43313e-01d, 3.81111e-02d, -6.43358e-03d}, //  47/128
            {-6.08674e-03d, 3.49066e-02d, -1.22185e-01d, 4.52218e-01d, 7.52958e-01d, -1.43968e-01d, 3.83800e-02d, -6.48585e-03d}, //  48/128
            {-6.15644e-03d, 3.53384e-02d, -1.23933e-01d, 4.62289e-01d, 7.44637e-01d, -1.44531e-01d, 3.86247e-02d, -6.53412e-03d}, //  49/128
            {-6.22280e-03d, 3.57519e-02d, -1.25624e-01d, 4.72342e-01d, 7.36222e-01d, -1.45004e-01d, 3.88454e-02d, -6.57836e-03d}, //  50/128
            {-6.28577e-03d, 3.61468e-02d, -1.27258e-01d, 4.82377e-01d, 7.27714e-01d, -1.45387e-01d, 3.90420e-02d, -6.61859e-03d}, //  51/128
            {-6.34530e-03d, 3.65227e-02d, -1.28832e-01d, 4.92389e-01d, 7.19116e-01d, -1.45682e-01d, 3.92147e-02d, -6.65479e-03d}, //  52/128
            {-6.40135e-03d, 3.68795e-02d, -1.30347e-01d, 5.02377e-01d, 7.10431e-01d, -1.45889e-01d, 3.93636e-02d, -6.68698e-03d}, //  53/128
            {-6.45388e-03d, 3.72167e-02d, -1.31800e-01d, 5.12337e-01d, 7.01661e-01d, -1.46009e-01d, 3.94886e-02d, -6.71514e-03d}, //  54/128
            {-6.50285e-03d, 3.75341e-02d, -1.33190e-01d, 5.22267e-01d, 6.92808e-01d, -1.46043e-01d, 3.95900e-02d, -6.73929e-03d}, //  55/128
            {-6.54823e-03d, 3.78315e-02d, -1.34515e-01d, 5.32164e-01d, 6.83875e-01d, -1.45993e-01d, 3.96678e-02d, -6.75943e-03d}, //  56/128
            {-6.58996e-03d, 3.81085e-02d, -1.35775e-01d, 5.42025e-01d, 6.74865e-01d, -1.45859e-01d, 3.97222e-02d, -6.77557e-03d}, //  57/128
            {-6.62802e-03d, 3.83650e-02d, -1.36969e-01d, 5.51849e-01d, 6.65779e-01d, -1.45641e-01d, 3.97532e-02d, -6.78771e-03d}, //  58/128
            {-6.66238e-03d, 3.86006e-02d, -1.38094e-01d, 5.61631e-01d, 6.56621e-01d, -1.45343e-01d, 3.97610e-02d, -6.79588e-03d}, //  59/128
            {-6.69300e-03d, 3.88151e-02d, -1.39150e-01d, 5.71370e-01d, 6.47394e-01d, -1.44963e-01d, 3.97458e-02d, -6.80007e-03d}, //  60/128
            {-6.71985e-03d, 3.90083e-02d, -1.40136e-01d, 5.81063e-01d, 6.38099e-01d, -1.44503e-01d, 3.97077e-02d, -6.80032e-03d}, //  61/128
            {-6.74291e-03d, 3.91800e-02d, -1.41050e-01d, 5.90706e-01d, 6.28739e-01d, -1.43965e-01d, 3.96469e-02d, -6.79662e-03d}, //  62/128
            {-6.76214e-03d, 3.93299e-02d, -1.41891e-01d, 6.00298e-01d, 6.19318e-01d, -1.43350e-01d, 3.95635e-02d, -6.78902e-03d}, //  63/128
            {-6.77751e-03d, 3.94578e-02d, -1.42658e-01d, 6.09836e-01d, 6.09836e-01d, -1.42658e-01d, 3.94578e-02d, -6.77751e-03d}, //  64/128
            {-6.78902e-03d, 3.95635e-02d, -1.43350e-01d, 6.19318e-01d, 6.00298e-01d, -1.41891e-01d, 3.93299e-02d, -6.76214e-03d}, //  65/128
            {-6.79662e-03d, 3.96469e-02d, -1.43965e-01d, 6.28739e-01d, 5.90706e-01d, -1.41050e-01d, 3.91800e-02d, -6.74291e-03d}, //  66/128
            {-6.80032e-03d, 3.97077e-02d, -1.44503e-01d, 6.38099e-01d, 5.81063e-01d, -1.40136e-01d, 3.90083e-02d, -6.71985e-03d}, //  67/128
            {-6.80007e-03d, 3.97458e-02d, -1.44963e-01d, 6.47394e-01d, 5.71370e-01d, -1.39150e-01d, 3.88151e-02d, -6.69300e-03d}, //  68/128
            {-6.79588e-03d, 3.97610e-02d, -1.45343e-01d, 6.56621e-01d, 5.61631e-01d, -1.38094e-01d, 3.86006e-02d, -6.66238e-03d}, //  69/128
            {-6.78771e-03d, 3.97532e-02d, -1.45641e-01d, 6.65779e-01d, 5.51849e-01d, -1.36969e-01d, 3.83650e-02d, -6.62802e-03d}, //  70/128
            {-6.77557e-03d, 3.97222e-02d, -1.45859e-01d, 6.74865e-01d, 5.42025e-01d, -1.35775e-01d, 3.81085e-02d, -6.58996e-03d}, //  71/128
            {-6.75943e-03d, 3.96678e-02d, -1.45993e-01d, 6.83875e-01d, 5.32164e-01d, -1.34515e-01d, 3.78315e-02d, -6.54823e-03d}, //  72/128
            {-6.73929e-03d, 3.95900e-02d, -1.46043e-01d, 6.92808e-01d, 5.22267e-01d, -1.33190e-01d, 3.75341e-02d, -6.50285e-03d}, //  73/128
            {-6.71514e-03d, 3.94886e-02d, -1.46009e-01d, 7.01661e-01d, 5.12337e-01d, -1.31800e-01d, 3.72167e-02d, -6.45388e-03d}, //  74/128
            {-6.68698e-03d, 3.93636e-02d, -1.45889e-01d, 7.10431e-01d, 5.02377e-01d, -1.30347e-01d, 3.68795e-02d, -6.40135e-03d}, //  75/128
            {-6.65479e-03d, 3.92147e-02d, -1.45682e-01d, 7.19116e-01d, 4.92389e-01d, -1.28832e-01d, 3.65227e-02d, -6.34530e-03d}, //  76/128
            {-6.61859e-03d, 3.90420e-02d, -1.45387e-01d, 7.27714e-01d, 4.82377e-01d, -1.27258e-01d, 3.61468e-02d, -6.28577e-03d}, //  77/128
            {-6.57836e-03d, 3.88454e-02d, -1.45004e-01d, 7.36222e-01d, 4.72342e-01d, -1.25624e-01d, 3.57519e-02d, -6.22280e-03d}, //  78/128
            {-6.53412e-03d, 3.86247e-02d, -1.44531e-01d, 7.44637e-01d, 4.62289e-01d, -1.23933e-01d, 3.53384e-02d, -6.15644e-03d}, //  79/128
            {-6.48585e-03d, 3.83800e-02d, -1.43968e-01d, 7.52958e-01d, 4.52218e-01d, -1.22185e-01d, 3.49066e-02d, -6.08674e-03d}, //  80/128
            {-6.43358e-03d, 3.81111e-02d, -1.43313e-01d, 7.61181e-01d, 4.42134e-01d, -1.20382e-01d, 3.44568e-02d, -6.01374e-03d}, //  81/128
            {-6.37730e-03d, 3.78182e-02d, -1.42566e-01d, 7.69305e-01d, 4.32038e-01d, -1.18526e-01d, 3.39894e-02d, -5.93749e-03d}, //  82/128
            {-6.31703e-03d, 3.75010e-02d, -1.41727e-01d, 7.77327e-01d, 4.21934e-01d, -1.16618e-01d, 3.35046e-02d, -5.85804e-03d}, //  83/128
            {-6.25277e-03d, 3.71596e-02d, -1.40794e-01d, 7.85244e-01d, 4.11823e-01d, -1.14659e-01d, 3.30027e-02d, -5.77544e-03d}, //  84/128
            {-6.18454e-03d, 3.67941e-02d, -1.39767e-01d, 7.93055e-01d, 4.01710e-01d, -1.12650e-01d, 3.24843e-02d, -5.68976e-03d}, //  85/128
            {-6.11236e-03d, 3.64044e-02d, -1.38644e-01d, 8.00757e-01d, 3.91596e-01d, -1.10593e-01d, 3.19495e-02d, -5.60103e-03d}, //  86/128
            {-6.03624e-03d, 3.59905e-02d, -1.37426e-01d, 8.08348e-01d, 3.81484e-01d, -1.08490e-01d, 3.13987e-02d, -5.50931e-03d}, //  87/128
            {-5.95620e-03d, 3.55525e-02d, -1.36111e-01d, 8.15826e-01d, 3.71376e-01d, -1.06342e-01d, 3.08323e-02d, -5.41467e-03d}, //  88/128
            {-5.87227e-03d, 3.50903e-02d, -1.34699e-01d, 8.23188e-01d, 3.61276e-01d, -1.04150e-01d, 3.02507e-02d, -5.31716e-03d}, //  89/128
            {-5.78446e-03d, 3.46042e-02d, -1.33189e-01d, 8.30432e-01d, 3.51186e-01d, -1.01915e-01d, 2.96543e-02d, -5.21683e-03d}, //  90/128
            {-5.69280e-03d, 3.40940e-02d, -1.31581e-01d, 8.37557e-01d, 3.41109e-01d, -9.96402e-02d, 2.90433e-02d, -5.11376e-03d}, //  91/128
            {-5.59731e-03d, 3.35600e-02d, -1.29874e-01d, 8.44559e-01d, 3.31048e-01d, -9.73254e-02d, 2.84182e-02d, -5.00800e-03d}, //  92/128
            {-5.49804e-03d, 3.30021e-02d, -1.28068e-01d, 8.51437e-01d, 3.21004e-01d, -9.49727e-02d, 2.77794e-02d, -4.89961e-03d}, //  93/128
            {-5.39500e-03d, 3.24205e-02d, -1.26161e-01d, 8.58189e-01d, 3.10980e-01d, -9.25834e-02d, 2.71272e-02d, -4.78866e-03d}, //  94/128
            {-5.28823e-03d, 3.18153e-02d, -1.24154e-01d, 8.64812e-01d, 3.00980e-01d, -9.01591e-02d, 2.64621e-02d, -4.67520e-03d}, //  95/128
            {-5.17776e-03d, 3.11866e-02d, -1.22047e-01d, 8.71305e-01d, 2.91006e-01d, -8.77011e-02d, 2.57844e-02d, -4.55932e-03d}, //  96/128
            {-5.06363e-03d, 3.05345e-02d, -1.19837e-01d, 8.77666e-01d, 2.81060e-01d, -8.52109e-02d, 2.50946e-02d, -4.44107e-03d}, //  97/128
            {-4.94589e-03d, 2.98593e-02d, -1.17526e-01d, 8.83893e-01d, 2.71144e-01d, -8.26900e-02d, 2.43930e-02d, -4.32052e-03d}, //  98/128
            {-4.82456e-03d, 2.91609e-02d, -1.15113e-01d, 8.89984e-01d, 2.61263e-01d, -8.01399e-02d, 2.36801e-02d, -4.19774e-03d}, //  99/128
            {-4.69970e-03d, 2.84397e-02d, -1.12597e-01d, 8.95936e-01d, 2.51417e-01d, -7.75620e-02d, 2.29562e-02d, -4.07279e-03d}, // 100/128
            {-4.57135e-03d, 2.76957e-02d, -1.09978e-01d, 9.01749e-01d, 2.41609e-01d, -7.49577e-02d, 2.22218e-02d, -3.94576e-03d}, // 101/128
            {-4.43955e-03d, 2.69293e-02d, -1.07256e-01d, 9.07420e-01d, 2.31843e-01d, -7.23286e-02d, 2.14774e-02d, -3.81671e-03d}, // 102/128
            {-4.30435e-03d, 2.61404e-02d, -1.04430e-01d, 9.12947e-01d, 2.22120e-01d, -6.96762e-02d, 2.07233e-02d, -3.68570e-03d}, // 103/128
            {-4.16581e-03d, 2.53295e-02d, -1.01501e-01d, 9.18329e-01d, 2.12443e-01d, -6.70018e-02d, 1.99599e-02d, -3.55283e-03d}, // 104/128
            {-4.02397e-03d, 2.44967e-02d, -9.84679e-02d, 9.23564e-01d, 2.02814e-01d, -6.43069e-02d, 1.91877e-02d, -3.41815e-03d}, // 105/128
            {-3.87888e-03d, 2.36423e-02d, -9.53307e-02d, 9.28650e-01d, 1.93236e-01d, -6.15931e-02d, 1.84071e-02d, -3.28174e-03d}, // 106/128
            {-3.73062e-03d, 2.27664e-02d, -9.20893e-02d, 9.33586e-01d, 1.83711e-01d, -5.88617e-02d, 1.76185e-02d, -3.14367e-03d}, // 107/128
            {-3.57923e-03d, 2.18695e-02d, -8.87435e-02d, 9.38371e-01d, 1.74242e-01d, -5.61142e-02d, 1.68225e-02d, -3.00403e-03d}, // 108/128
            {-3.42477e-03d, 2.09516e-02d, -8.52933e-02d, 9.43001e-01d, 1.64831e-01d, -5.33522e-02d, 1.60193e-02d, -2.86289e-03d}, // 109/128
            {-3.26730e-03d, 2.00132e-02d, -8.17385e-02d, 9.47477e-01d, 1.55480e-01d, -5.05770e-02d, 1.52095e-02d, -2.72032e-03d}, // 110/128
            {-3.10689e-03d, 1.90545e-02d, -7.80792e-02d, 9.51795e-01d, 1.46192e-01d, -4.77900e-02d, 1.43934e-02d, -2.57640e-03d}, // 111/128
            {-2.94361e-03d, 1.80759e-02d, -7.43154e-02d, 9.55956e-01d, 1.36968e-01d, -4.49929e-02d, 1.35716e-02d, -2.43121e-03d}, // 112/128
            {-2.77751e-03d, 1.70776e-02d, -7.04471e-02d, 9.59958e-01d, 1.27812e-01d, -4.21869e-02d, 1.27445e-02d, -2.28483e-03d}, // 113/128
            {-2.60868e-03d, 1.60599e-02d, -6.64743e-02d, 9.63798e-01d, 1.18725e-01d, -3.93735e-02d, 1.19125e-02d, -2.13733e-03d}, // 114/128
            {-2.43718e-03d, 1.50233e-02d, -6.23972e-02d, 9.67477e-01d, 1.09710e-01d, -3.65541e-02d, 1.10760e-02d, -1.98880e-03d}, // 115/128
            {-2.26307e-03d, 1.39681e-02d, -5.82159e-02d, 9.70992e-01d, 1.00769e-01d, -3.37303e-02d, 1.02356e-02d, -1.83931e-03d}, // 116/128
            {-2.08645e-03d, 1.28947e-02d, -5.39305e-02d, 9.74342e-01d, 9.19033e-02d, -3.09033e-02d, 9.39154e-03d, -1.68894e-03d}, // 117/128
            {-1.90738e-03d, 1.18034e-02d, -4.95412e-02d, 9.77526e-01d, 8.31162e-02d, -2.80746e-02d, 8.54441e-03d, -1.53777e-03d}, // 118/128
            {-1.72594e-03d, 1.06946e-02d, -4.50483e-02d, 9.80543e-01d, 7.44095e-02d, -2.52457e-02d, 7.69462e-03d, -1.38589e-03d}, // 119/128
            {-1.54221e-03d, 9.56876e-03d, -4.04519e-02d, 9.83392e-01d, 6.57852e-02d, -2.24178e-02d, 6.84261e-03d, -1.23337e-03d}, // 120/128
            {-1.35627e-03d, 8.42626e-03d, -3.57525e-02d, 9.86071e-01d, 5.72454e-02d, -1.95925e-02d, 5.98883e-03d, -1.08030e-03d}, // 121/128
            {-1.16820e-03d, 7.26755e-03d, -3.09503e-02d, 9.88580e-01d, 4.87921e-02d, -1.67710e-02d, 5.13372e-03d, -9.26747e-04d}, // 122/128
            {-9.78093e-04d, 6.09305e-03d, -2.60456e-02d, 9.90917e-01d, 4.04274e-02d, -1.39548e-02d, 4.27773e-03d, -7.72802e-04d}, // 123/128
            {-7.86031e-04d, 4.90322e-03d, -2.10389e-02d, 9.93082e-01d, 3.21531e-02d, -1.11453e-02d, 3.42130e-03d, -6.18544e-04d}, // 124/128
            {-5.92100e-04d, 3.69852e-03d, -1.59305e-02d, 9.95074e-01d, 2.39714e-02d, -8.34364e-03d, 2.56486e-03d, -4.64053e-04d}, // 125/128
            {-3.96391e-04d, 2.47942e-03d, -1.07209e-02d, 9.96891e-01d, 1.58840e-02d, -5.55134e-03d, 1.70888e-03d, -3.09412e-04d}, // 126/128
            {-1.98993e-04d, 1.24642e-03d, -5.41054e-03d, 9.98534e-01d, 7.89295e-03d, -2.76968e-03d, 8.53777e-04d, -1.54700e-04d}, // 127/128
            {0.00000e+00d, 0.00000e+00d, 0.00000e+00d, 1.00000e+00d, 0.00000e+00d, 0.00000e+00d, 0.00000e+00d, 0.00000e+00d}, // 128/128
        };

    private static final int NUMBER_FILTER_TAPS = 8;
    private static final int NUMBER_FILTER_STEPS = 128;

    private static final int SAMPLE_RATE = 48000;
    private static final int SYMBOL_RATE = 4800;

    /* PLLGain loop gain constant */
    private static final double K_SYMBOL_SPREAD = 0.0100;

    /* Constraints on symbol spreading */
    private static final double SYMBOL_SPREAD_MAX = 2.4d; // upper range limit: +20%
    private static final double SYMBOL_SPREAD_MIN = 1.6d; // lower range limit: -20%

    /* Symbol clock tracking loop gain */
    private static final double K_SYMBOL_TIMING = 0.025;

    /* Coarse and fine frequency tracking constants */
    private static final double K_COARSE_FREQUENCY = 0.00125;
    private static final double K_FINE_FREQUENCY = 0.125;

    /* Frequency correction broadcast threshold */
//	private static final double COARSE_FREQUENCY_DEADBAND = 1.66;
    private static final double COARSE_FREQUENCY_THRESHOLD = 1.20;

    /* 2.0 symbol spread gives -3, -1, 1, 3 */
    private double mSymbolSpread = 2.0d;
    private double mSymbolClock = 0.0d;
    private double mSymbolTime = (double)SYMBOL_RATE / (double)SAMPLE_RATE;

    private double mFineFrequencyCorrection = 0.0d;
    private double mCoarseFrequencyCorrection = 0.0d;

    private double mHistory[] = new double[NUMBER_FILTER_TAPS];
    private int mHistoryLast = 0;

    private RealSampleListener mListener;

    private DirectGainControl mGainController = new DirectGainControl(15.0d, 0.1d, 35.0d, 0.3d);

    private int mFrequencyAdjustmentRequested = 0;
    private int mFrequencyCorrection = 0;
    private int mFrequencyCorrectionMaximum = 3000;
    private boolean mResetFrequencyTracker = false;
    private FrequencyCorrectionProcessor mFrequencyCorrectionProcessor;
    private Listener<SourceEvent> mFrequencyChangeListener;

    /**
     * C4FM Symbol Filter
     *
     * Sample Gain values - the gain of incoming sample values will critically
     * impact the performance of this filter.  Gain should be adjusted to
     * optimize the value of mSymbolSpread toward a value of 2.0.  If the value
     * is less than 2.0, then increase gain.  If the value is over 2.0, then
     * decrease gain.
     *
     * When preceded by the RealAutomaticGainFilter class, an optimal gain
     * setting for this filter is 15.4 and that yields a symbol spread that
     * is centered on 2.0, ranging 1.92 to 2.08.
     */
    public C4FMSymbolFilter(int frequencyCorrectionMaximum)
    {
        mFrequencyCorrectionMaximum = frequencyCorrectionMaximum;
    }

    public void dispose()
    {
        mGainController = null;
//		mFrequencyCorrectionControl = null;
        mListener = null;
    }

    @Override
    public void receive(ReusableDoubleBuffer buffer)
    {
        for(double sample : buffer.getSamples())
        {
            receive(sample);
        }

		/* If a frequency correction was requested during the processing of this
         * buffer, we'll apply the change and it will be reflected in the next
		 * arriving buffer.  Reset the lock on frequency correction and reset
		 * the internal frequency correction tracker */
        if(mFrequencyAdjustmentRequested != 0)
        {
            int correction = mFrequencyCorrection + mFrequencyAdjustmentRequested;

            if(correction > mFrequencyCorrectionMaximum)
            {
                correction = mFrequencyCorrectionMaximum;
            }
            else if(correction < -mFrequencyCorrectionMaximum)
            {
                correction = -mFrequencyCorrectionMaximum;
            }

            broadcast(SourceEvent.channelFrequencyCorrectionRequest(correction));

            mFrequencyAdjustmentRequested = 0;
        }
    }

    public void receive(double sample)
    {
        sample = mGainController.correct(sample);

        if(mResetFrequencyTracker)
        {
            mCoarseFrequencyCorrection = 0.0d;
            mFineFrequencyCorrection = 0.0d;

            broadcast(SourceEvent.channelFrequencyCorrectionRequest(0l));

            mResetFrequencyTracker = false;
        }

        mSymbolClock += mSymbolTime;

        mHistory[mHistoryLast++] = sample;

        mHistoryLast %= NUMBER_FILTER_TAPS;

        if(mSymbolClock > 1.0d)
        {
            mSymbolClock -= 1.0d;

            int imu = (int) FastMath.floor(0.5 +
                ((double)NUMBER_FILTER_STEPS * (mSymbolClock / mSymbolTime)));

            if(imu >= NUMBER_FILTER_STEPS)
            {
                imu = NUMBER_FILTER_STEPS - 1;
            }

            int imu_p1 = imu + 1;

            int j = mHistoryLast;

            double interp = 0.0;
            double interp_p1 = 0.0;

            for(int i = 0; i < NUMBER_FILTER_TAPS; i++)
            {
                interp += TAPS[imu][i] * mHistory[j];
                interp_p1 += TAPS[imu_p1][i] * mHistory[j];

                j = (j + 1) % NUMBER_FILTER_TAPS;
            }

			/* Output symbol will be interpolated value corrected for symbol
			 * spread and frequency offset */
            interp -= mFineFrequencyCorrection;
            interp_p1 -= mFineFrequencyCorrection;

			/* Correct output for symbol deviation (spread) */
            double output = 2.0 * interp / mSymbolSpread;

			/* Detect received symbol error: basically use a hard decision and
			 * subtract off expected position nominal symbol level which will be
			 * +/- 0.5 * symbol spread and +/- 1.5 symbol spread.  Remember that
			 * nominal symbol spread will be 2.0 */
            double symbolError;

            if(interp < -mSymbolSpread)
            {
				/* symbol is -3: Expected at -1.5 * symbol spread */
                symbolError = interp + (1.5 * mSymbolSpread);
                mSymbolSpread -= (symbolError * 0.5 * K_SYMBOL_SPREAD);
            }
            else if(interp < 0.0)
            {
				/* symbol is -1: Expected at -0.5 * symbol_spread */
                symbolError = interp + (0.5 * mSymbolSpread);
                mSymbolSpread -= (symbolError * K_SYMBOL_SPREAD);
            }
            else if(interp < mSymbolSpread)
            {
				/* symbol is +1: Expected at +0.5 * symbol_spread */
                symbolError = interp - (0.5 * mSymbolSpread);
                mSymbolSpread += (symbolError * K_SYMBOL_SPREAD);
            }
            else
            {
				/* symbol is +3: Expected at +1.5 * symbol_spread */
                symbolError = interp - (1.5 * mSymbolSpread);
                mSymbolSpread += (symbolError * 0.5 * K_SYMBOL_SPREAD);
            }

			/* Symbol clock tracking loop adjustment */
            if(interp_p1 < interp)
            {
                mSymbolClock += symbolError * K_SYMBOL_TIMING;
            }
            else
            {
                mSymbolClock -= symbolError * K_SYMBOL_TIMING;
            }

            if(mSymbolSpread < SYMBOL_SPREAD_MIN)
            {
                mGainController.increase();

                mSymbolSpread = SYMBOL_SPREAD_MIN;
            }
            else if(mSymbolSpread > SYMBOL_SPREAD_MAX)
            {
                mGainController.decrease();

                mSymbolSpread = SYMBOL_SPREAD_MAX;
            }

            mCoarseFrequencyCorrection += ((mFineFrequencyCorrection -
                mCoarseFrequencyCorrection) * K_COARSE_FREQUENCY);

            mFineFrequencyCorrection += (symbolError * K_FINE_FREQUENCY);
			
			/* Queue a frequency adjustment (once per buffer) as needed */
            if(FastMath.abs(mCoarseFrequencyCorrection) > COARSE_FREQUENCY_THRESHOLD)
            {
                mFrequencyAdjustmentRequested =
                    500 * (mCoarseFrequencyCorrection > 0 ? 1 : -1);
            }

			/* dispatch the interpolated value to the listener */
            if(mListener != null)
            {
                mListener.receive(output);
            }
        }
    }

    @Override
    public void setListener(RealSampleListener listener)
    {
        mListener = listener;
    }

    @Override
    public void removeListener(RealSampleListener listener)
    {
        mListener = null;
    }

    /**
     * Broadcasts a frequency change event to the registered listener
     *
     * @param event
     */
    public void broadcast(SourceEvent event)
    {
        if(mFrequencyChangeListener != null)
        {
            mFrequencyChangeListener.receive(event);
        }
    }

    /**
     * Sets the listener for frequency change events.
     *
     * @param listener
     */
    public void setFrequencyChangeListener(Listener<SourceEvent> listener)
    {
        mFrequencyChangeListener = listener;
    }

    @Override
    public Listener<SourceEvent> getSourceEventListener()
    {
        if(mFrequencyCorrectionProcessor == null)
        {
            mFrequencyCorrectionProcessor = new FrequencyCorrectionProcessor();
        }

        return mFrequencyCorrectionProcessor;
    }

    /**
     * Receives notifications that the channel frequency correction has been
     * applied and updates internal tracking value.
     */
    public class FrequencyCorrectionProcessor implements Listener<SourceEvent>
    {
        @Override
        public void receive(SourceEvent event)
        {
            switch(event.getEvent())
            {
                case NOTIFICATION_CHANNEL_FREQUENCY_CORRECTION_CHANGE:
                    mFrequencyCorrection = event.getValue().intValue();

                    //Reset internal frequency tracking
                    mCoarseFrequencyCorrection = 0.0d;
                    mFineFrequencyCorrection = 0.0d;
                    break;
                case NOTIFICATION_FREQUENCY_CORRECTION_CHANGE:
                case NOTIFICATION_SAMPLE_RATE_CHANGE:
                    mResetFrequencyTracker = true;
                    break;
                default:
                    break;
            }
        }
    }
}
