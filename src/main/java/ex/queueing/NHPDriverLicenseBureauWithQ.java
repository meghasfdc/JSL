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

import jsl.modeling.*;

import jsl.modeling.queue.QObject;
import jsl.modeling.queue.Queue;
import jsl.modeling.elements.variable.*;

import jsl.modeling.elements.variable.nhpp.NHPPTimeBtwEventRV;
import jsl.modeling.elements.variable.nhpp.PiecewiseConstantRateFunction;

import jsl.utilities.random.distributions.DistributionIfc;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.SimulationReporter;

public class NHPDriverLicenseBureauWithQ extends SchedulingElement {

    private int myNumServers;

    private Queue myWaitingQ;

    private RandomVariable myServiceRV;

    private NHPPTimeBtwEventRV myArrivalRV;

    private TimeWeighted myNumBusy;

    private TimeWeighted myNS;

    private ArrivalListener myArrivalListener;

    private EndServiceListener myEndServiceListener;

    public NHPDriverLicenseBureauWithQ(ModelElement parent) {
        this(parent, 1, null);
    }

    public NHPDriverLicenseBureauWithQ(ModelElement parent, int numServers, String name) {
        super(parent, name);

        setNumberOfServers(numServers);

        myServiceRV = new RandomVariable(this, new Exponential(3));

        PiecewiseConstantRateFunction f = new PiecewiseConstantRateFunction(360.0, 0.006);
        f.addRateSegment(180.0, 0.033);
        f.addRateSegment(180.0, 0.041);
        f.addRateSegment(120.0, 0.194);
        f.addRateSegment(180.0, 0.079);
        f.addRateSegment(180.0, 0.167);
        f.addRateSegment(240.0, 0.05);

        myArrivalRV = new NHPPTimeBtwEventRV(this, f);

        myWaitingQ = new Queue(this, "DriverLicenseQ");

        myNumBusy = new TimeWeighted(this, 0.0, "NumBusy");

        myNS = new TimeWeighted(this, 0.0, "NS");

        myArrivalListener = new ArrivalListener();

        myEndServiceListener = new EndServiceListener();
    }

    public int getNumberOfServers() {
        return (myNumServers);
    }

    protected void setNumberOfServers(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }

        myNumServers = n;
    }

    public void setServiceDistributionInitialRandomSource(DistributionIfc d) {
        myServiceRV.setInitialRandomSource(d);
    }

    @Override
    protected void initialize() {
        super.initialize();

        // start the arrivals
        scheduleEvent(myArrivalListener, myArrivalRV);
    }

    class ArrivalListener implements EventActionIfc {

        @Override
        public void action(JSLEvent event) {
            myNS.increment(); // new customer arrived
            QObject arrival = createQObject();
            myWaitingQ.enqueue(arrival); // enqueue the newly arriving customer
            if (myNumBusy.getValue() < myNumServers) { // server available
                myNumBusy.increment(); // make server busy
                QObject customer = myWaitingQ.removeNext(); //remove the next customer
                //	schedule end of service, include the customer as the event's message
                scheduleEvent(myEndServiceListener, myServiceRV, customer);
            }
            //	always schedule the next arrival
            scheduleEvent(myArrivalListener, myArrivalRV);
        }
    }

    class EndServiceListener implements EventActionIfc {

        @Override
        public void action(JSLEvent event) {
            QObject leavingCustomer = (QObject) event.getMessage();

            myNS.decrement(); // customer departed
            myNumBusy.decrement(); // customer is leaving server is freed

            if (!myWaitingQ.isEmpty()) { // queue is not empty
                QObject customer = myWaitingQ.removeNext(); //remove the next customer
                myNumBusy.increment(); // make server busy
                //	schedule end of service
                scheduleEvent(myEndServiceListener, myServiceRV, customer);
            }
        }
    }

    public static void main(String[] args) {
         test1();
         test2();

    }

    /** The service time is exponential with a mean of 3 minutes
     *  The arrival process is non-homogeneous Poisson for the following
     *
     *  Interval        duration (min)  rate per min
     *  12 am - 6 am    360             0.006
     *  6 am - 9 am     180             0.033
     *  9 am - 12 pm    180             0.041
     *  12 pm - 2 pm    120             0.194
     *  2 pm - 5 pm     180             0.079
     *  5 pm - 8 pm     180             0.167
     *  8 pm - 12 am    240             0.05
     *
     *
     *  Run for 10 days, no warm up
     */
    public static void test1() {
        System.out.println("NHP Driver License Bureau Test 1");
        // create the simulation to run the model
        Simulation s = new Simulation("NHP DLB Test 1");

        // create the model element and attach it to the main model
        NHPDriverLicenseBureauWithQ dlb = new NHPDriverLicenseBureauWithQ(s.getModel());

        // set the parameters of the experiment
        s.setNumberOfReplications(10);
        s.setLengthOfReplication(1440.0);

        // tell the simulation to run
        s.run();
        System.out.println("Done!");
        
        SimulationReporter r = new SimulationReporter(s);

        r.printAcrossReplicationSummaryStatistics();
    }

    /** The service time is exponential with a mean of 3 minutes
     *  The arrival process is non-homogeneous Poisson for the following
     *
     *  Interval        duration (min)  rate per min
     *  12 am - 6 am    360             0.006
     *  6 am - 9 am     180             0.033
     *  9 am - 12 pm    180             0.041
     *  12 pm - 2 pm    120             0.194
     *  2 pm - 5 pm     180             0.079
     *  5 pm - 8 pm     180             0.167
     *  8 pm - 12 am    240             0.05
     *
     *
     *  Run for 10 days, 1 day warm up, 1 day of observation
     */
    public static void test2() {
        System.out.println("NHP Driver License Bureau Test2");

        // create the simulation to run the model
        Simulation s = new Simulation("NHP DLB Test 2");

        // create the model element and attach it to the main model
        NHPDriverLicenseBureauWithQ dlb = new NHPDriverLicenseBureauWithQ(s.getModel());

        // set the parameters of the experiment
        s.setNumberOfReplications(10);
        s.setLengthOfReplication(2880.0);
        s.setLengthOfWarmUp(1440.0);

        // tell the experiment to run
        s.run();
        
        SimulationReporter r = new SimulationReporter(s);

        r.printAcrossReplicationSummaryStatistics();
        
        System.out.println("Done!");
    }
}
