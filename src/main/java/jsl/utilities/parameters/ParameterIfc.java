/*
 * Copyright (c) 2019. Manuel D. Rossetti, rossetti@uark.edu
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

package jsl.utilities.parameters;

import com.google.common.collect.Range;

public interface ParameterIfc<C extends Comparable>  {

    /**
     *
     * @return the name of the parameter
     */
    String getName();

    /**
     *
     * @return the set of valid values as a Range
     */
    Range<C> getRange();

    /**
     *
     * @return the value of the parameter
     */
    C getValue();

    /**
     *
     * @param value the value to set, must not be null
     */
    void setValue(C value);

    /**
     *
     * @param value the initial value to set, must not be null
     */
    void setInitialValue(C value);

    /**
     *
     * @return the initial value of the parameter
     */
    C getInitialValue();

    /**
     *  Causes the value to be set to the initial value
     */
    default void initialize(){
        setValue(getInitialValue());
    }

    /**
     *
     * @param value the value to check
     * @return true if the value is valid for the parameter's range
     */
    default boolean isValid(C value){
        return getRange().contains(value);
    }

}
