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

package jsl.utilities.random.rng;

/**
 *  An interface to define the ability to provide a random number stream (RNStreamIfc)
 *  for use in generating pseudo-random numbers that can be controlled.
 */
public interface RNStreamProviderIfc {

    /**
     * Tells the provider to make and return a RNStream with the provided name
     *
     * @param name can be null
     * @return the made stream
     */
    RNStreamIfc getStream(String name);

    /**
     * Tells the factory to make and return a RNStream
     *
     * @return the made stream
     */
    default RNStreamIfc getStream() {
        return getStream(null);
    }
}
