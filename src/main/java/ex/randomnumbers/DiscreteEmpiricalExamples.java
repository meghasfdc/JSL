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

package ex.randomnumbers;

import jsl.utilities.random.distributions.DEmpiricalCDF;
import jsl.utilities.random.rng.RNStreamIfc;
import jsl.utilities.random.rng.RNStreamProvider;
import jsl.utilities.random.rvariable.DEmpiricalRV;
import jsl.utilities.random.rvariable.RVariableIfc;

/**
 *
 * @author rossetti
 */
public class DiscreteEmpiricalExamples {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {    
        // make a factory for creating streams
        RNStreamProvider f1 = new RNStreamProvider();
        // get the first stream from the factory
        RNStreamIfc f1s1 = f1.nextRNStream();

        double[] values = {1.0, 2.0, 3.0, 4.0};
        double[] cdf = {1.0/6.0, 3.0/6.0, 5.0/6.0, 1.0};
        DEmpiricalRV n1 = new DEmpiricalRV(values, cdf, f1s1);

        System.out.println("pmf");
        System.out.println(n1);

        for (int i = 1; i <= 5; i++) {
            System.out.println("x(" + i + ")= " + n1.getValue());
        }

        DEmpiricalCDF n2 = new DEmpiricalCDF(values, cdf);

        System.out.println("pmf");
        System.out.println(n2);
        RVariableIfc n2RV = n2.getRandomVariable();
        for (int i = 1; i <= 5; i++) {
            System.out.println("x(" + i + ")= " + n2RV.getValue());
        }
    }
    
}
