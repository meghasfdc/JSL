/*
 * Copyright (c) 2018. Manuel D. Rossetti, rossetti@uark.edu
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

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

import static jsl.utilities.statistic.SaveDataIfc.DEFAULT_DATA_ARRAY_SIZE;

/**
 *  A class to save data to an expanding array.
 */
public class ArrayBasedDataSaver {

    /**
     * The array to collect the data if the saved flag is true If the statistic
     * is reset and data was saved, this array should be cleared (as if no data
     * had been collected).
     *
     */
    protected double[] myData;

    /**
     * Used to set the array size increment when the collect data option is
     * turned on
     *
     */
    protected int myDataArraySize = DEFAULT_DATA_ARRAY_SIZE;

    /**
     * Counts the number of data points that were saved to the save array
     *
     */
    protected int mySaveCount = 0;

    /**
     * A flag to indicate whether or not the saver should save the data as
     * it is collected.  If this flag is true, the data will be saved
     * when the save() method is called.
     *
     */
    protected boolean mySaveDataFlag = false;

    /**
     *
     * @return the number of data points saved in the array
     */
    public final int getCount(){
        return mySaveCount;
    }

    /**
     * Used to clear out data saved to the array during collection
     *
     */
    public final void clearSavedData() {
        if (myData == null) {
            return;
        }
        myData = null;
        mySaveCount = 0;
    }

    /**
     * Indicates whether or not the save data option is on
     * true = on, false = off
     *
     * @return true if option is on
     */
    public final boolean getSaveOption() {
        return mySaveDataFlag;
    }

    /**
     * Returns a copy of the data saved while the
     * saved data option was turned on, will return
     * an empty array if no data were collected
     *
     * @return the data as an array
     */
    public final double[] getSavedData() {
        if (myData == null) {
            return ArrayUtils.EMPTY_DOUBLE_ARRAY;
        }
        return Arrays.copyOf(myData, mySaveCount);
    }

    /**
     * Used to save data to an array
     *
     * @param x the data to save to the array
     */
    public final void save(double x) {
        if (getSaveOption() == false){
            return;
        }
        if (myData == null) {
            myData = new double[myDataArraySize];
        }
        // need to save x into the array
        mySaveCount++;
        if (mySaveCount > myData.length) {
            // need to grow the array
            myData = Arrays.copyOf(myData, myData.length + myDataArraySize);
        }
        myData[mySaveCount - 1] = x;
    }

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
    public final void setArraySizeIncrement(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("The array size increment must be > 0");
        }
        myDataArraySize = n;
    }

    /**
     * Sets the save data option
     * true = on, false = off
     *
     * If true, the data will be saved to an array
     * If this option is toggled, then only the data
     * when the option is true will be saved.
     *
     * @param flag true means save the data, false means stop saving the data
     */
    public final void setSaveOption(boolean flag) {
        mySaveDataFlag = flag;
    }
}
