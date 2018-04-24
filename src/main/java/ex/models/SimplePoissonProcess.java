/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.models;

import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.SimulationReporter;

/**
 *
 * @author rossetti
 */
public class SimplePoissonProcess extends SchedulingElement {

    protected RandomVariable myTBE;
    protected Counter myCount;

    public SimplePoissonProcess(ModelElement parent) {
        this(parent, null);
    }

    public SimplePoissonProcess(ModelElement parent, String name) {
        super(parent, name);
        myTBE = new RandomVariable(this, new Exponential());
        myCount = new Counter(this, "Counts events");
    }

    @Override
    protected void initialize() {
        super.initialize();
        scheduleEvent(myTBE.getValue());
    }

    @Override
    protected void handleEvent(JSLEvent event) {
        myCount.increment();
        scheduleEvent(myTBE.getValue());
    }

    public static void main(String[] args) {
        Simulation s = new Simulation("Simple PP");
        new SimplePoissonProcess(s.getModel());
        s.setLengthOfReplication(20.0);
        s.setNumberOfReplications(50);
        s.run();
        SimulationReporter r = s.makeSimulationReporter();
        r.printAcrossReplicationSummaryStatistics();
        System.out.println("Done!");
    }
}
