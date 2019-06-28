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

package jsl.utilities.random.distributions;

/**
 *  A promise to return estimated parameter values for the distribution implementing the interface based
 *  on the supplied data
 */
public interface EstimateParametersIfc {

    /**
     *
     * @param data the data to base the likelihood on, must not be null
     * @return an array holding the parameters of the distribution implementing the interface
     */
    double[] estimateParameters(double[] data);
}
