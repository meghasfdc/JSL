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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.randomnumbers;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RNStreamFactory.RNStream;

/**
 *
 * @author rossetti
 */
public class FirstFiveRN {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RNStreamFactory f = RNStreamFactory.getDefault();
        RNStream g1 = f.getStream();
        double u1;
        for (int i = 1; i <= 5; i++) {
            u1 = g1.randU01();
            System.out.println("u("+i+") = " + u1);
        }

    }
}
