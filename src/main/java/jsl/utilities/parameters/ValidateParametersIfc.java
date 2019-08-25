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
import jsl.utilities.math.JSLMath;

import java.util.List;
import java.util.Objects;

/**
 * Assumes that distribution parameters are in an array parameters.  Provides functionality
 * to validate the parameters to ensure compliance with distributional parameter constraints as
 * denoted by a Range that indicates the parameter's set of valid values.
 */
public interface ValidateParametersIfc {

    /**
     *
     * @return a list of ranges that specify for each parameter the set of legal values
     */
    List<Range<Double>> getValidParameterRanges();

    /**
     * @param parameterIndex the index of the parameter to get the range for. May throw IndexOutOfBoundsException
     *                       if index is not valid for set of possible parameters. Index starts at 0.
     * @return a true if the supplied value is valid for the parameter at the supplied index
     */
    default boolean isValid(int parameterIndex, double value){
        return getValidParameterRange(parameterIndex).contains(value);
    }

    /**
     * @param parameterIndex the index of the parameter to get the range for. May throw IndexOutOfBoundsException
     *                       if index is not valid for set of possible parameters. Index starts at 0.
     * @return a Range representing the set of possible valid values for the parameter
     */
    default Range<Double> getValidParameterRange(int parameterIndex){
        return getValidParameterRanges().get(parameterIndex);
    }

    /**
     * @param parameters the parameters to validate, must not be null
     * @return an array of the same size as parameters, with true indicating the the parameter if valid
     */
    default boolean[] validate(double[] parameters){
        Objects.requireNonNull(parameters,"The parameters array was null");
        boolean[] v = new boolean[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            v[i] = isValid(i, parameters[i]);
        }
        return v;
    }

    /**
     * @param parameters the parameters to validate, must not be null
     * @return true if and only if all parameters are valid
     */
    default boolean validateAll(double[] parameters) {
         return JSLMath.isAllTrue(validate(parameters));
    }

}
