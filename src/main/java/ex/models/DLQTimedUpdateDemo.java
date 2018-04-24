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

import jsl.modeling.EventActionIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.queue.QObject;
import jsl.modeling.queue.Queue;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.SimulationReporter;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.statistic.WeightedStatisticIfc;

public class DLQTimedUpdateDemo extends SchedulingElement {

    private int myNumPharmacists;
    private Queue myWaitingQ;
    private RandomIfc myServiceRS;
    private RandomIfc myArrivalRS;
    private RandomVariable myServiceRV;
    private RandomVariable myArrivalRV;
    private TimeWeighted myNumBusy;
    private TimeWeighted myNS;
    private ResponseVariable mySysTime;
    private ArrivalEventAction myArrivalEventAction;
    private EndServiceEventAction myEndServiceEventAction;
    private ResponseVariable myDemoResponse;

    public DLQTimedUpdateDemo(ModelElement parent) {
        this(parent, 1, new Exponential(1.0), new Exponential(0.5));
    }

    public DLQTimedUpdateDemo(ModelElement parent, int numServers, RandomIfc ad, RandomIfc sd) {
        super(parent);
        setNumberOfPharmacists(numServers);
        setServiceRS(sd);
        setArrivalRS(ad);
        myWaitingQ = new Queue(this, "PharmacyQ");
        myNumBusy = new TimeWeighted(this, 0.0, "NumBusy");
        myNS = new TimeWeighted(this, 0.0, "# in System");
        myNS.turnOnTimeIntervalCollection(100);
        mySysTime = new ResponseVariable(this, "System Time");
        mySysTime.turnOnTimeIntervalCollection(100);
        myDemoResponse = new ResponseVariable(this, "Cost of Something");
        myArrivalEventAction = new ArrivalEventAction();
        myEndServiceEventAction = new EndServiceEventAction();
    }

    @Override
    protected void replicationEnded() {
        super.replicationEnded(); 
        
        ResponseVariable ns = myNS.getAcrossIntervalResponse();
        ResponseVariable st = mySysTime.getAcrossIntervalResponse();
        
        WeightedStatisticIfc nsIntervalStat = ns.getWithinReplicationStatistic();
         WeightedStatisticIfc stIntervalStat = ns.getWithinReplicationStatistic();
         System.out.println(nsIntervalStat);
         System.out.println(stIntervalStat);
         myDemoResponse.setValue(nsIntervalStat.getAverage()*100);
         
    }
    
    
    public int getNumberOfServers() {
        return (myNumPharmacists);
    }

    public final void setNumberOfPharmacists(int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }

        myNumPharmacists = n;
    }

    public final void setServiceRS(RandomIfc d) {

        if (d == null) {
            throw new IllegalArgumentException("Service Time RV was null!");
        }

        myServiceRS = d;

        if (myServiceRV == null) { // not made yet
            myServiceRV = new RandomVariable(this, myServiceRS, "Service RV");
        } else { // already had been made, and added to model
            // just change the distribution
            myServiceRV.setInitialRandomSource(myServiceRS);
        }

    }

    public final void setArrivalRS(RandomIfc d) {

        if (d == null) {
            throw new IllegalArgumentException("Arrival Time Distribution was null!");
        }

        myArrivalRS = d;

        if (myArrivalRV == null) { // not made yet
            myArrivalRV = new RandomVariable(this, myArrivalRS, "Arrival RV");
        } else { // already had been made, and added to model
            // just change the distribution
            myArrivalRV.setInitialRandomSource(myArrivalRS);
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        // start the arrivals
        scheduleEvent(myArrivalEventAction, myArrivalRV);
    }

    private class ArrivalEventAction implements EventActionIfc {

        @Override
        public void action(JSLEvent event) {
            //	 schedule the next arrival
            scheduleEvent(myArrivalEventAction, myArrivalRV);
            enterSystem();
        }
    }

    private void enterSystem() {
        myNS.increment(); // new customer arrived
        QObject arrivingCustomer = new QObject(getTime());
        
        myWaitingQ.enqueue(arrivingCustomer); // enqueue the newly arriving customer
        if (myNumBusy.getValue() < myNumPharmacists) { // server available
            myNumBusy.increment(); // make server busy
            QObject customer = myWaitingQ.removeNext(); //remove the next customer
            // schedule end of service, include the customer as the event's message
            scheduleEvent(myEndServiceEventAction, myServiceRV, customer);
        }
    }

    private class EndServiceEventAction implements EventActionIfc {

        @Override
        public void action(JSLEvent event) {
            myNumBusy.decrement(); // customer is leaving server is freed
            if (!myWaitingQ.isEmpty()) { // queue is not empty
                QObject customer = myWaitingQ.removeNext(); //remove the next customer
                myNumBusy.increment(); // make server busy
                // schedule end of service
                scheduleEvent(myEndServiceEventAction, myServiceRV, customer);
            }
            departSystem((QObject) event.getMessage());
        }
    }

    private void departSystem(QObject departingCustomer) {
        mySysTime.setValue(getTime() - departingCustomer.getCreateTime());
        myNS.decrement(); // customer left system      
    }

    public static void main(String[] args) {
        Simulation sim = new Simulation("Drive Through Pharmacy");
        // get the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DLQTimedUpdateDemo driveThroughPharmacy = new DLQTimedUpdateDemo(m);
        driveThroughPharmacy.setArrivalRS(new Exponential(6.0));
        driveThroughPharmacy.setServiceRS(new Exponential(3.0));
        //m.turnOnTimeIntervalCollection(100);
        // set the parameters of the experiment
        sim.setNumberOfReplications(30);
        sim.setLengthOfReplication(20000.0);
        sim.setLengthOfWarmUp(5000.0);
        SimulationReporter r = sim.makeSimulationReporter();

        System.out.println("Simulation started.");
        sim.run();
        r.printAcrossReplicationSummaryStatistics();

    }

}
