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

import java.util.ArrayList;
import java.util.List;
import jsl.utilities.random.robj.DPopulation;
import jsl.utilities.random.rvariable.JSLRandom;

/**
 *
 * @author rossetti
 */
public class DemoPermutations {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        double[] y = new double[10];
        for (int i = 0; i < 10; i++) {
            y[i] = i + 1;
        }

        DPopulation p = new DPopulation(y);
        System.out.println(p);

        p.permute();
        System.out.println(p);

        System.out.println("Permuting y");
        JSLRandom.permutation(y);
        System.out.println(DPopulation.toString(y));

        double[] x = p.sample(5);
        System.out.println("Sampling x from population");
        System.out.println(DPopulation.toString(x));
        
        List<String> strList = new ArrayList<>();
        strList.add("a");
        strList.add("b");
        strList.add("c");
        strList.add("d");
        System.out.println(strList);
        JSLRandom.permutation(strList);
        System.out.println(strList);
    }
    
}
