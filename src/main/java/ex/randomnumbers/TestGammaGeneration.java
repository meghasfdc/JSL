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

package ex.randomnumbers;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RNStreamIfc;
import jsl.utilities.random.rvariable.JSLRandom;
import jsl.utilities.reporting.JSL;

import java.io.PrintWriter;

public class TestGammaGeneration {

    public static void main(String[] args) {

        PrintWriter out = JSL.makePrintWriter("gammaOut", "txt");
        RNStreamIfc stream = RNStreamFactory.getDefaultStream();
        for (int i = 1; i <= 10000; i++) {
            //double x = JSLRandom.rGamma(3.0, 5.0, stream, JSLRandom.AlgoType.AcceptanceRejection);
            double x = JSLRandom.rGamma(3.0, 5.0, stream);
            out.println(x);
        }
    }
}
