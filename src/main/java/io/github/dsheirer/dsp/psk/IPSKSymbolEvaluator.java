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
package io.github.dsheirer.dsp.psk;

public interface IPSKSymbolEvaluator<T>
{
    /**
     * Phase error of the symbol relative to the closest reference symbol
     * @return angular phase error in radians
     */
    double getPhaseError();

    /**
     * Timing error of the symbol relative to the closest reference symbol
     * @return angular timing error in radians where a positive value indicates early sampling and sample timing needs
     * to be increased and a negative value indicates late sampling where sample timing needs to be decreased.
     */
    double getTimingError();

    /**
     * Symbol decision indicating the reference symbol location closest to the symbol.
     * @return symbol decision
     */
    T getSymbolDecision();
}
