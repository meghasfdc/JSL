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
package ex.resource;

import jsl.modeling.*;
import jsl.modeling.queue.Queue;
import jsl.modeling.elements.resource.Allocation;
import jsl.modeling.elements.resource.Entity;
import jsl.modeling.elements.resource.Request;
import jsl.modeling.elements.resource.Resource;
import jsl.modeling.elements.resource.AllocationListenerIfc;
import jsl.modeling.elements.resource.ResourceSet;
import jsl.modeling.elements.variable.*;
import jsl.utilities.random.distributions.DistributionIfc;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.SimulationReporter;

public class DriverLicenseBureauWithResourceSet extends SchedulingElement {

    private ResourceSet myResourceSet;

    private Queue myWaitingQ;

    private DistributionIfc myServiceDistribution;

    private DistributionIfc myArrivalDistribution;

    private RandomVariable myServiceRV;

    private RandomVariable myArrivalRV;

    private TimeWeighted myNS;

    private ArrivalEventAction myArrivalEventAction;

    private AllocationListener myBeginServiceListener;

    private EndServiceEventAction myEndServiceEventAction;

    public DriverLicenseBureauWithResourceSet(ModelElement parent) {
        this(parent, 1, new Exponential(1.0), new Exponential(0.5));
    }

    public DriverLicenseBureauWithResourceSet(ModelElement parent, int numServers,
            DistributionIfc ad, DistributionIfc sd) {
        super(parent);

        setServiceDistributionInitialRandomSource(sd);
        setArrivalDistributionInitialRandomSource(ad);

        myWaitingQ = new Queue(this, "DriverLicenseQ");
        myResourceSet = new ResourceSet(this, "ResourceSet");
        myResourceSet.addResource(numServers, "Clerks");
        myNS = new TimeWeighted(this, 0.0, "NS");

        myArrivalEventAction = new ArrivalEventAction();
        myEndServiceEventAction = new EndServiceEventAction();
        myBeginServiceListener = new AllocationListener();
    }

    public void setServiceDistributionInitialRandomSource(DistributionIfc d) {

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

    public void setArrivalDistributionInitialRandomSource(DistributionIfc d) {

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

    @Override
    protected void initialize() {
        super.initialize();

        // start the arrivals
        scheduleEvent(myArrivalEventAction, myArrivalRV);
    }

    class ArrivalEventAction implements EventActionIfc {

        @Override
        public void action(JSLEvent event) {
            myNS.increment(); // new customer arrived
            // create the customer
            Entity customer = createEntity();

            // enqueue arriving customer
            myWaitingQ.enqueue(customer);

            myResourceSet.seize(customer, myBeginServiceListener);

            //	always schedule the next arrival
            scheduleEvent(myArrivalEventAction, myArrivalRV);
        }
    }

    class AllocationListener implements AllocationListenerIfc {

        @Override
        public void allocated(Request request) {
            if (request.isSatisfied()) {
                Entity customer = request.getEntity();
                myWaitingQ.remove(customer);
                Resource resource = request.getSeizedResource();
                Allocation a = resource.allocate(customer, request.getAmountAllocated());
                scheduleEvent(myEndServiceEventAction, myServiceRV, a);
            }
        }
    }

    class EndServiceEventAction implements EventActionIfc {

        public void action(JSLEvent event) {
            //System.out.println(" in end service listener");
            myNS.decrement(); // customer is departing
            Allocation a = (Allocation) event.getMessage();
            Resource r = a.getAllocatedResource();
            r.release(a);
        }
    }

    public static void main(String[] args) {
        System.out.println("Driver License Bureau With Resource Set Example");

        Simulation s = new Simulation("DLB with Resource");

        // create the model element and attach it to the main model
        new DriverLicenseBureauWithResourceSet(s.getModel());

        // set the parameters of the experiment
        s.setNumberOfReplications(10);
        s.setLengthOfReplication(20000.0);
        s.setLengthOfWarmUp(5000.0);

        // tell the experiment to run
        s.run();

        SimulationReporter r = s.makeSimulationReporter();
        r.printAcrossReplicationSummaryStatistics();

        System.out.println("Done!");

    }
}
