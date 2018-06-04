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
package ex.handleevent;

import jsl.modeling.*;
import jsl.modeling.elements.variable.*;
import jsl.utilities.random.distributions.Bernoulli;
import jsl.utilities.random.distributions.DistributionIfc;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.SimulationReporter;

/**
 */
public class UpDownComponentHE extends SchedulingElement {

    private Variable myHeight;

    private RandomVariable myUpTime;

    private RandomVariable myDownTime;

    private RandomVariable myInitialState;

    private TimeWeighted myState;

    private ResponseVariable myCycleLength;

    private Counter myCountFailures;

    private TimeWeighted myCountF;

    private double myTimeLastUp;

    private double myCount;

    /**
     * @param parent
     */
    public UpDownComponentHE(ModelElement parent) {
        this(parent, null);
    }

    /**
     * @param parent
     * @param name
     */
    public UpDownComponentHE(ModelElement parent, String name) {
        super(parent, name);

        myHeight = new Variable(this, 1.0, "height");
        DistributionIfc utd = new Exponential(1.0);
        DistributionIfc dtd = new Exponential(2.0);
        myUpTime = new RandomVariable(this, utd, "up time");
        myDownTime = new RandomVariable(this, dtd, "down time");
        myState = new TimeWeighted(this, "state");
        myCycleLength = new ResponseVariable(this, "cycle length");

        myCountFailures = new Counter(this, "count failures");
        myCountF = new TimeWeighted(this, "count failures TW shadow");
        myCountFailures.setTimedUpdateInterval(10.0);
//		myCountFailures.setLimit(10.0);
//		myCountFailures.addStoppingAction();
        // Let's get fancy and randomly determine the initial state
        double meanUpTime = utd.getMean();
        double meanDownTime = dtd.getMean();

        // chance of being in up state
        double p = meanUpTime / (meanUpTime + meanDownTime);
        System.out.println("p = " + p);
        myInitialState = new RandomVariable(this, new Bernoulli(p), "initial state");

    }

    @Override
    public void initialize() {
        myTimeLastUp = 0.0; // assume 0.0 but test
        myCount = 0.0;
        if (myInitialState.getValue() > 0.0) { // up state
            myState.setValue(myHeight.getValue());
            // schedule the down state change after the uptime
            scheduleEvent(myUpTime, 2);
        } else { // down state
            myState.setValue(0.0);
            // schedule the up state change after the down time
            scheduleEvent(myDownTime, 1);
        }
    }

    @Override
    protected void handleEvent(JSLEvent event) {

        if (event.getType() == 1) {
            handleUpChangeEvent(event);
        } else if (event.getType() == 2) {
            handleDownChangeEvent(event);
        }
    }

    private void handleUpChangeEvent(JSLEvent event) {
        // record the cycle length, the time btw up states
        if (myTimeLastUp > 0.0) // it has been set at least once, record the cycle
        {
            myCycleLength.setValue(getTime() - myTimeLastUp);
        }

        // component has just gone up, change its state value

        myState.setValue(myHeight.getValue());

        // record the time it went up
        myTimeLastUp = getTime();

        // schedule when it goes down, reuse the event
        event.setType(2);
        // after the up time elapse, state goes to down
        rescheduleEvent(event, myUpTime);
    }

    private void handleDownChangeEvent(JSLEvent event) {
        // component has just gone down, change its state value
        myCountFailures.increment();
        myCountF.increment();
        myState.setValue(0.0);
        myCount++;
        // schedule when it goes up, reuse the event
        event.setType(1);
        // after the down time elapse, state goes to up
        rescheduleEvent(event, myDownTime);
    }

    public static void main(String[] args) {

        testReplication();
//        testExperiment();
        
        System.out.println("Done!");

    }

    public static void testReplication() {
        // create the simulation
        Simulation s = new Simulation("UpDownComponent Replication Test");
        
        // access the model
        Model m = s.getModel();

        // create the model element and attach it to the model
        UpDownComponentHE tv = new UpDownComponentHE(m);

        SimulationReporter r = s.makeSimulationReporter();
        // capture within replication statistics to a file
        r.turnOnReplicationCSVStatisticReporting("UpDownCSVWithinRepResults");
                
        // set the running parameters of the simulation
        s.setLengthOfReplication(50.0);

        // tell the simulation to run
        s.run();
        

    }

    public static void testExperiment() {
        // create the simulation
        Simulation s = new Simulation("UpDownComponent Replication Test");
        
        // access the model
        Model m = s.getModel();

        // create the model element and attach it to the model
        UpDownComponentHE tv = new UpDownComponentHE(m);

        SimulationReporter r = s.makeSimulationReporter();
        r.turnOnReplicationCSVStatisticReporting("UpDownCSVWithinRepResults");

        // set the running parameters of the simulation
        s.setNumberOfReplications(10);
        s.setLengthOfReplication(500.0);

        // tell the simulation to run
        s.run();
        
        // write across replication results to System.out
        r.printAcrossReplicationStatistics();
        
        r.writeAcrossReplicationCSVStatistics("UpDownExampleAcrossRepResults");

    }
}
