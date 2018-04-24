/*
 * Copyright (c) 2018. Manuel D. Rossetti, manuelrossetti@gmail.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
