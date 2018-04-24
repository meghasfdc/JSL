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
