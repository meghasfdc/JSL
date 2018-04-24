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
package test.modeling;

import jsl.modeling.Model;
import jsl.modeling.Simulation;
import jsl.modeling.resource.ResourceUnit;
import jsl.utilities.random.distributions.Constant;

/**
 *
 * @author rossetti
 */
public class FailureElementTesting {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        test1();
        //test2();
    }

    public static void test1() {
        // TODO code application logic here
        Simulation sim = new Simulation("Test FailureElement");
        Model model = sim.getModel();
        ResourceUnit resource = new ResourceUnit.Builder(model).
                allowFailuresToDelay().autoStartFailures().build();
        Constant c1 = new Constant(0.5);
        Constant c2 = new Constant(0.2);
        resource.addTimeBasedFailure(Constant.TWO, c1, true);
        resource.addTimeBasedFailure(Constant.ONE, c2, true);
        
        sim.setLengthOfReplication(20.0);
        sim.setNumberOfReplications(2);
        sim.run();
    }

    public static void test2() {
        // TODO code application logic here
        Simulation sim = new Simulation("Test FailureElement");
        Model model = sim.getModel();
        ResourceUnit resource = new ResourceUnit.Builder(model).
               autoStartFailures().build();
        Constant c1 = new Constant(0.5);
        Constant c2 = new Constant(0.2);
        resource.addTimeBasedFailure(Constant.TWO, c1, false);
        resource.addTimeBasedFailure(Constant.ONE, c2, false);
        sim.setLengthOfReplication(20.0);
        sim.setNumberOfReplications(2);
        sim.run();
    }

}
