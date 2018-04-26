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
package jsl.utilities.random.rng;

import jsl.utilities.random.GetAntitheticValueIfc;

public interface RandU01Ifc extends GetAntitheticValueIfc {

    /** Returns a pseudo-random uniformly distributed number
     * @return the random number
     */
    double randU01();

    /** The previous U(0,1) generated (returned) by randU01()
     *
     * @return
     */
    double getPrevU01();
}