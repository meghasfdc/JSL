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
package test.modeling;

import java.util.List;
import jsl.modeling.JSLEvent;
import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.SimulationReporter;
import jsl.modeling.queue.QObject;
import jsl.modeling.queue.Queue;
import jsl.modeling.resource.Request;
import jsl.modeling.resource.RequestReactorAdapter;
import jsl.modeling.resource.ResourceUnit;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.reporting.JSL;
import jsl.modeling.resource.RequestReactorIfc;
import jsl.modeling.resource.ResourcePool;

public class DTPQueueResourcePoolModel extends SchedulingElement {

    private int myNumPharmacists;
    private Queue myWaitingQ;
    private RandomIfc myServiceRS;
    private RandomIfc myArrivalRS;
    private RandomVariable myServiceRV;
    private RandomVariable myArrivalRV;
    private TimeWeighted myNumBusy;
    private TimeWeighted myNS;
    private ResponseVariable mySysTime;
    private ResourcePool myResourcePool;
    private final RequestReactorIfc myRequestReactor = new RequestReactor();

    public DTPQueueResourcePoolModel(ModelElement parent) {
        this(parent, 1, new Exponential(1.0), new Exponential(0.5));
    }

    public DTPQueueResourcePoolModel(ModelElement parent, int numServers) {
        this(parent, numServers, new Exponential(1.0), new Exponential(0.5));
    }

    public DTPQueueResourcePoolModel(ModelElement parent, int numServers,
            RandomIfc ad, RandomIfc sd) {
        super(parent);
        setServiceRS(sd);
        setArrivalRS(ad);
        myWaitingQ = new Queue(this, "PharmacyQ");
        List<ResourceUnit> units = new ResourceUnit.Builder(this)
                .name("Server")
                //.collectRequestQStats()
                .build(numServers);
        myResourcePool = new ResourcePool(this, units, true, "Pharmacists");
        myNumBusy = new TimeWeighted(this, 0.0, "NumBusy");
        myNS = new TimeWeighted(this, 0.0, "# in System");
        mySysTime = new ResponseVariable(this, "System Time");
    }

    public ResponseVariable getSystemTimeResponse() {
        return mySysTime;
    }

    public TimeWeighted getNumInSystemResponse() {
        return myNS;
    }

    public int getNumberOfServers() {
        return (myNumPharmacists);
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
        schedule(this::arrival).in(myArrivalRV).units();
    }

    private void arrival(JSLEvent<QObject> evt) {
        myNS.increment(); // new customer arrived
        QObject arrivingCustomer = new QObject(getTime());
        myWaitingQ.enqueue(arrivingCustomer);
        if (myResourcePool.hasIdleUnits()) {
            myWaitingQ.remove(arrivingCustomer);
            ResourceUnit ru = myResourcePool.selectResourceUnit();
            ru.seize(myRequestReactor, myServiceRS, arrivingCustomer);
        }
        schedule(this::arrival).in(myArrivalRV).units();

    }

    private class RequestReactor extends RequestReactorAdapter {

        @Override
        public void allocated(Request request) {
            myNumBusy.increment();
            JSL.out.println(getTime() + "> Request " + request + " allocated.");
        }

        @Override
        public void completed(Request request) {
              JSL.out.println(getTime() + "> Request " + request + " completed.");  
              QObject nextCustomer = null;
            if (myWaitingQ.isNotEmpty()) {
                nextCustomer = myWaitingQ.removeNext();
                ResourceUnit ru = myResourcePool.selectResourceUnit();
                ru.seize(myRequestReactor, myServiceRS, nextCustomer);
            }
           
            QObject departingCustomer = (QObject)request.getAttachedObject();
//            if (departingCustomer == nextCustomer){
//                throw new IllegalStateException("the departing customer can't be the next customer");
//            }
            myNumBusy.decrement();
            mySysTime.setValue(getTime() - departingCustomer.getCreateTime());
            myNS.decrement(); // customer left system  
        }

    }

    public static void main(String[] args) {
        JSL.out.OUTPUT_ON = false;
        Simulation sim = new Simulation("New Queue Testing");
        // get the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        int numServers = 2;
        DTPQueueResourcePoolModel dtp = new DTPQueueResourcePoolModel(m, numServers);
        dtp.setArrivalRS(new Exponential(6.0));
        dtp.setServiceRS(new Exponential(3.0));

        // set the parameters of the experiment
        sim.setNumberOfReplications(30);
        sim.setLengthOfReplication(20000.0);
        sim.setLengthOfWarmUp(5000.0);
//        sim.setNumberOfReplications(2);
//        sim.setLengthOfReplication(1000.0);
//        sim.setLengthOfWarmUp(5.0);

        SimulationReporter r = sim.makeSimulationReporter();
        System.out.println("Simulation started.");
        sim.run();
        r.printAcrossReplicationSummaryStatistics();
    }

}
