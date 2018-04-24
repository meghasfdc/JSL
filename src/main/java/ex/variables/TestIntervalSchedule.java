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
public class TestIntervalSchedule {

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
        ResponseSchedule sched = new ResponseSchedule(m);
        //sched.addConsecutiveIntervals(2, 5, "Interval");
        sched.addConsecutiveIntervals(2, 5);
        sched.addResponseToAllIntervals(rs);
        sched.addResponseToAllIntervals(tw);
        sched.setStartTime(5.0);
        sched.setScheduleRepeatFlag(true);
        
        System.out.println(sched);
        sim.setNumberOfReplications(2);
        sim.setLengthOfReplication(20.0);
        sim.setLengthOfWarmUp(5);

        SimulationReporter r = sim.makeSimulationReporter();

        //System.out.println(sim);
        // tell the simulation to run
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");

        r.printAcrossReplicationSummaryStatistics();
    }

}
