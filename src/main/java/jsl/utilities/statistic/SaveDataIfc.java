/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
*
* Copyright (c) Manuel D. Rossetti (rossetti@uark.edu)
*
* Contact:
*	Manuel D. Rossetti, Ph.D., P.E.
*	Department of Industrial Engineering
*	University of Arkansas
*	4207 Bell Engineering Center
*	Fayetteville, AR 72701
*	Phone: (479) 575-6756
*	Email: rossetti@uark.edu
*	Web: www.uark.edu/~rossetti
*
* This file is part of the JSL (a Java Simulation Library). The JSL is a framework
* of Java classes that permit the easy development and execution of discrete event
* simulation programs.
*
* The JSL is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* The JSL is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with the JSL (see file COPYING in the distribution);
* if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
* Boston, MA  02110-1301  USA, or see www.fsf.org
*
 */

package jsl.utilities.statistic;

/**
 *
 * @author rossetti
 */
public interface SaveDataIfc {
    /**
     * The default increment for the array size when the
     * save data option is turned on
     *
     */
    int DEFAULT_DATA_ARRAY_SIZE = 1000;

    /**
     * Indicates whether or not the save data option is on
     * true = on, false = off
     *
     * @return
     */
    boolean getSaveDataOption();

    /**
     * Returns a copy of the data saved while the
     * saved data option was turned on, will return
     * null if no data were collected
     *
     * @return
     */
    double[] getSavedData();

    /**
     * Returns a copy of the weights saved while the
     * saved data option was turned on, will return null
     * if no weights were collected
     *
     * @return
     */
    double[] getSavedWeights();

    /**
     * Controls the amount that the saved data array will grow by
     * after it has been filled up.  If the potential number of
     * data points is known, then this method can be used so that
     * arrays do not have to be copied during collection
     * The array size will start at this value and then increment by
     * this value whenever full
     *
     * @param n
     */
    void setSaveDataArraySizeIncrement(int n);

    /**
     * Sets the save data option
     * true = on, false = off
     *
     * If true, the data will be saved to an array
     * If this option is toggled, then only the data
     * when the option is true will be saved.
     *
     * @param flag
     */
    void setSaveDataOption(boolean flag);

}
