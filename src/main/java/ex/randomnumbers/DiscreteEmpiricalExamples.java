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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ex.randomnumbers;

import jsl.utilities.random.distributions.DEmpiricalCDF;
import jsl.utilities.random.distributions.DEmpiricalPMF;
import jsl.utilities.random.rng.RNStreamFactory;

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
        RNStreamFactory f1 = new RNStreamFactory();
        // get the first stream from the factory
        RNStreamFactory.RNStream f1s1 = f1.getStream();
        // make another factory, the factories are identical
        RNStreamFactory f2 = new RNStreamFactory();
        // thus the first streams returned are identical
        RNStreamFactory.RNStream f2s1 = f2.getStream();
        
        DEmpiricalPMF n1 = new DEmpiricalPMF(f1s1);
        n1.addProbabilityPoint(1.0, 1.0/6.0);
        n1.addProbabilityPoint(2.0, 2.0/6.0);
        n1.addProbabilityPoint(3.0, 2.0/6.0);
        n1.addLastProbabilityPoint(4.0);

        System.out.println("pmf");
        System.out.println(n1);

        for (int i = 1; i <= 5; i++) {
            System.out.println("x(" + i + ")= " + n1.getValue());
        }
        
        double[] pm = {1.0, 1.0/6.0, 2.0, 3.0/6.0, 3.0, 5.0/6.0, 4.0, 1.0};
        DEmpiricalCDF n2 = new DEmpiricalCDF(pm, f2s1);

        System.out.println("pmf");
        System.out.println(n2);

        for (int i = 1; i <= 5; i++) {
            System.out.println("x(" + i + ")= " + n2.getValue());
        }
    }
    
}
