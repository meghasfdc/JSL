/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.variables;

import ex.queueing.DriverLicenseBureauWithQ;
import jsl.modeling.Model;
import jsl.modeling.Simulation;
import jsl.modeling.SimulationReporter;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;

/**
 *
 * @author rossetti
 */
public class TestTimedIntervalResponse {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulation sim = new Simulation("DLB_with_Q");
        Model m = sim.getModel();
        // create the model element and attach it to the main model
        new DriverLicenseBureauWithQ(m);
        ResponseVariable rs = m.getResponseVariable("System Time");
        TimeWeighted tw = m.getTimeWeighted("NS");
        tw.turnOnTimeIntervalCollection(10);
        rs.turnOnTimeIntervalCollection(10);
        tw.turnOnTimeIntervalTrace();
        rs.turnOnTimeIntervalTrace();
//        TimedIntervalResponse tir1 = new TimedIntervalResponse(m, 10);
//        tir1.setResponse(rs);
//        TimedIntervalResponse tir2 = new TimedIntervalResponse(m, 10);
//        tir2.setResponse(tw);
        tw.turnOnTrace(true);;
        rs.turnOnTrace(true);
//        tir1.turnOnTrace(true);
//        tir2.turnOnTrace(true);
        // set the parameters of the experiment
        sim.setNumberOfReplications(2);
        sim.setLengthOfReplication(20.0);
        //       sim.setLengthOfWarmUp(5000.0);

        SimulationReporter r = sim.makeSimulationReporter();

        //r.turnOnReplicationCSVStatisticReporting();
        System.out.println(sim);

        // tell the simulation to run
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");

        r.printAcrossReplicationSummaryStatistics();
    }

}
