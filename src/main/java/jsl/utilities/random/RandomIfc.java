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
package jsl.utilities.random;

import jsl.utilities.GetValueIfc;
import jsl.utilities.NewInstanceIfc;
import jsl.utilities.random.rng.RandomStreamIfc;
import jsl.utilities.random.rng.RNStreamIfc;

/**
 *
 */
public interface RandomIfc extends ParametersIfc, GetValueIfc, RandomStreamIfc, NewInstanceIfc {

    /** Returns a new instance of the random source with the same parameters
     *  but an independent underlying random number source
     *
     * @return a new instance
     */
    @Override
    RandomIfc newInstance();

    /** Returns a new instance of the random source with the same parameters
     *  but using the supplied random number stream
     *
     * @param rng the stream to use
     * @return the new instance
     */
    RandomIfc newInstance(RNStreamIfc rng);
}
