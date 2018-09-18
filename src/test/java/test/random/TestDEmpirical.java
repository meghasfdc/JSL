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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.random;

import java.util.Arrays;
import jsl.utilities.math.JSLMath;
import jsl.utilities.random.distributions.DEmpiricalCDF;
import jsl.utilities.random.rng.RNStreamIfc;
import jsl.utilities.random.rvariable.JSLRandom;
import jsl.utilities.random.rng.RNStreamFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author rossetti
 */
public class TestDEmpirical {

    public TestDEmpirical() {
    }


    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void test() {
        double[] p = {0.7, 0.8, 0.9, 1.0};
        double[] x = {1.0, 2.0, 3.0, 4.0};

        assertTrue(JSLRandom.isValidCDF(p) == true);
        double[] makePairs = DEmpiricalCDF.makePairs(1, p);

        System.out.println(Arrays.toString(makePairs));
        double[] pp = {1.0, 0.7, 2.0, 0.8, 3.0, 0.9, 4.0, 1.0};

        assertTrue(JSLMath.compareArrays(makePairs, pp) == true);

        RNStreamIfc defaultStream = RNStreamFactory.getDefaultStream();

        DEmpiricalCDF d = new DEmpiricalCDF(pp, defaultStream);

        int n = 100;
        double[] x1 = new double[n];

        for (int i = 0; i < n; i++) {
            x1[i] = JSLRandom.randomlySelect(x, p, defaultStream);
        }
        double[] x2 = new double[n];
        defaultStream.resetStartStream();
        for (int i = 0; i < n; i++) {
            x2[i] = JSLRandom.randomlySelect(x, p, defaultStream);
        }
        System.out.println(Arrays.toString(x1));
        System.out.println(Arrays.toString(x2));
        assertTrue(JSLMath.compareArrays(x1, x2) == true);
    }

}
