/*
 * Copyright (c) 2007, Manuel D. Rossetti (rossetti@uark.edu)
 *
 * Contact:
 *	Manuel D. Rossetti, Ph.D., P.E.
 *	Department of Industrial Engineering
 *	University of Arkansas
 *	4207 Bell Engineering Center
 *	Fayetteville, AR 72701
 *	Phone: (479) 575-6756
 *	Email: rossetti@uark.edu
 *	Web: www.uark.edu/~rossetti
 *
 * This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 * of Java classes that permit the easy development and execution of discrete event
 * simulation programs.
 *
 * The JSL is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The JSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the JSL (see file COPYING in the distribution);
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA, or see www.fsf.org
 *
 */
package ex.models;

import jsl.modeling.*;
import jsl.modeling.elements.variable.*;
import jsl.utilities.random.distributions.DistributionIfc;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.SimulationReporter;

/**
 */
public class UpDownComponent extends SchedulingElement {

    public static final int UP = 1;
    public static final int DOWN = 0;
    private RandomVariable myUpTime;
    private RandomVariable myDownTime;
    private TimeWeighted myState;
    private ResponseVariable myCycleLength;
    private Counter myCountFailures;
    private final UpChangeAction myUpChangeAction = new UpChangeAction();
    private final DownChangeAction myDownChangeAction = new DownChangeAction();
    private double myTimeLastUp;

    public UpDownComponent(ModelElement parent) {
        this(parent, null);
    }

    public UpDownComponent(ModelElement parent, String name) {
        super(parent, name);
        DistributionIfc utd = new Exponential(1.0);
        DistributionIfc dtd = new Exponential(2.0);
        myUpTime = new RandomVariable(this, utd, "up time");
        myDownTime = new RandomVariable(this, dtd, "down time");
        myState = new TimeWeighted(this, "state");
        myCycleLength = new ResponseVariable(this, "cycle length");
        myCountFailures = new Counter(this, "count failures");
    }

    @Override
    public void initialize() {
        // assume that the component starts in the UP state at time 0.0
        myTimeLastUp = 0.0;
        myState.setValue(UP);
        // schedule the time that it goes down
        scheduleEvent(myDownChangeAction, myUpTime.getValue(), "Down");
    }

    private class UpChangeAction implements EventActionIfc {

        @Override
        public void action(JSLEvent event) {
            // this event action represents what happens when the component goes up
            // record the cycle length, the time btw up states
            myCycleLength.setValue(getTime() - myTimeLastUp);
            // component has just gone up, change its state value
            myState.setValue(UP);
            // record the time it went up
            myTimeLastUp = getTime();
            // schedule the down state change after the uptime
            scheduleEvent(myDownChangeAction, myUpTime.getValue(), "Down");
        }
    }

    class DownChangeAction implements EventActionIfc {

        @Override
        public void action(JSLEvent event) {
            // component has just gone down, change its state value
            myCountFailures.increment();
            myState.setValue(DOWN);
            // schedule when it goes up afer the down time
            scheduleEvent(myUpChangeAction, myDownTime.getValue(), "Up");
        }
    }

    public static void main(String[] args) {
        // create the simulation
        Simulation s = new Simulation("UpDownComponent");
        s.turnOnDefaultEventTraceReport();
        s.turnOnLogReport();
        // get the model associated with the simulation
        Model m = s.getModel();
        // create the model element and attach it to the model
        UpDownComponent tv = new UpDownComponent(m);
        // make the simulation reporter
        SimulationReporter r = s.makeSimulationReporter();
        // set the running parameters of the simulation
        s.setNumberOfReplications(5);
        s.setLengthOfReplication(5000.0);
        // tell the simulation to run
        s.run();
        r.printAcrossReplicationSummaryStatistics();
    }
}
