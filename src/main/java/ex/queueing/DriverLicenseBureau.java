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
package ex.queueing;

import jsl.modeling.EventActionIfc;
import jsl.modeling.Experiment;
import jsl.modeling.JSLEvent;
import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.random.distributions.DistributionIfc;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.SimulationReporter;
import jsl.utilities.statistic.StatisticAccessorIfc;

public class DriverLicenseBureau extends SchedulingElement {

    private int myNumServers;

    private DistributionIfc myServiceDistribution;

    private DistributionIfc myArrivalDistribution;

    private RandomVariable myServiceRV;

    private RandomVariable myArrivalRV;

    private TimeWeighted myNumBusy;

    private TimeWeighted myNQ;

    private TimeWeighted myNS;

    private ArrivalEventAction myArrivalEventAction;

    private EndServiceEventAction myEndServiceEventAction;

    public DriverLicenseBureau(ModelElement parent) {
        this(parent, 1, new Exponential(1.0), new Exponential(0.8));
    }

    public DriverLicenseBureau(ModelElement parent, int numServers,
            DistributionIfc ad, DistributionIfc sd) {
        super(parent);

        setNumberOfServers(numServers);
        setServiceDistributionInitialRandomSource(sd);
        setArrivalDistributionInitialRandomSource(ad);

        myNumBusy = new TimeWeighted(this, 0.0, "NumBusy");

        myNQ = new TimeWeighted(this, 0.0, "NQ");

        myNS = new TimeWeighted(this, 0.0, "NS");

        myArrivalEventAction = new ArrivalEventAction();
        myEndServiceEventAction = new EndServiceEventAction();
    }

    public int getNumberOfServers() {
        return (myNumServers);
    }

    public final void setNumberOfServers(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }

        myNumServers = n;
    }

    public final void setServiceDistributionInitialRandomSource(DistributionIfc d) {

        if (d == null) {
            throw new IllegalArgumentException("Service Time Distribution was null!");
        }

        myServiceDistribution = d;

        if (myServiceRV == null) { // not made yet
            myServiceRV = new RandomVariable(this, myServiceDistribution, "Service RV");
        } else { // already had been made, and added to model
            // just change the distribution
            myServiceRV.setInitialRandomSource(myServiceDistribution);
        }

    }

    public final void setArrivalDistributionInitialRandomSource(DistributionIfc d) {

        if (d == null) {
            throw new IllegalArgumentException("Arrival Time Distribution was null!");
        }

        myArrivalDistribution = d;

        if (myArrivalRV == null) { // not made yet
            myArrivalRV = new RandomVariable(this, myArrivalDistribution, "Arrival RV");
        } else { // already had been made, and added to model
            // just change the distribution
            myArrivalRV.setInitialRandomSource(myArrivalDistribution);
        }
    }

    public final StatisticAccessorIfc getNQAcrossReplicationStatistic() {
        return myNQ.getAcrossReplicationStatistic();
    }

    public final StatisticAccessorIfc getNBAcrossReplicationStatistic() {
        return myNumBusy.getAcrossReplicationStatistic();
    }

    public final StatisticAccessorIfc getNSAcrossReplicationStatistic() {
        return myNS.getAcrossReplicationStatistic();
    }

    @Override
    protected void initialize() {
        super.initialize();
        // start the arrivals
        scheduleEvent(myArrivalEventAction, myArrivalRV.getValue(), "Arrival");
    }

    class ArrivalEventAction implements EventActionIfc {

        public void action(JSLEvent event) {

            myNS.increment(); // new customer arrived
            if (myNumBusy.getValue() < myNumServers) { // server available
                myNumBusy.increment(); // make server busy
                //	schedule end of service
                scheduleEvent(myEndServiceEventAction, myServiceRV.getValue(), "End Service");
            } else { // no server available
                myNQ.increment(); // place customer in queue
            }

            //	always schedule the next arrival
            scheduleEvent(myArrivalEventAction, myArrivalRV.getValue(), "Arrival");
        }
    }

    class EndServiceEventAction implements EventActionIfc {

        public void action(JSLEvent event) {

            myNS.decrement(); // customer departed
            myNumBusy.decrement(); // customer is leaving server is freed

            if (myNQ.getValue() > 0) { // queue is not empty
                myNQ.decrement(); // remove from queue
                myNumBusy.increment(); // make server busy
                //	schedule end of service
                scheduleEvent(myEndServiceEventAction, myServiceRV.getValue(), "End Service");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Driver License Bureau Test");

        // Create the simulation
        Simulation sim = new Simulation("DLB Sim");

        // get the containing model
        Model m = sim.getModel();

        // create the model element and attach it to the main model
        new DriverLicenseBureau(m);

        // set the parameters of the experiment
        sim.setNumberOfReplications(10);
        sim.setLengthOfReplication(200000.0);
        sim.setLengthOfWarmUp(50000.0);

        // run the simulation
        sim.run();
        
        SimulationReporter r = sim.makeSimulationReporter();

        // write out some results
        r.printAcrossReplicationStatistics();
        System.out.println("Done!");

    }
}
