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
package test.modeling;

import jsl.modeling.JSLEvent;
import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
//import jsl.modeling.elements.enterWaitingState.QObject;
//import jsl.modeling.elements.enterWaitingState.Queue;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.modeling.resource.*;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.SimulationReporter;
import jsl.modeling.queue.QObject;
import jsl.modeling.queue.Queue;
import jsl.modeling.resource.Request.PreemptionRule;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.distributions.Constant;

public class DTPQueueResourceModelWithFailures extends SchedulingElement {

    private int myNumPharmacists;
    private Queue myWaitingQ;
    private RandomIfc myServiceRS;
    private RandomIfc myArrivalRS;
    private RandomVariable myServiceRV;
    private RandomVariable myArrivalRV;
    private TimeWeighted myNumBusy;
    private TimeWeighted myNS;
    private ResponseVariable mySysTime;
    private ResourceUnit myResource;
    private Request myPreemptedRequest;
    private final RequestInteraction myRequestInteraction = new RequestInteraction();

    public DTPQueueResourceModelWithFailures(ModelElement parent) {
        this(parent, 1, new Exponential(1.0), new Exponential(0.5));
    }

    public DTPQueueResourceModelWithFailures(ModelElement parent, int numServers, RandomIfc ad, RandomIfc sd) {
        super(parent);
        setNumberOfPharmacists(numServers);
        setServiceRS(sd);
        setArrivalRS(ad);
        myWaitingQ = new Queue(this, "PharmacyQ");
        myResource = new ResourceUnit.Builder(this)
                .name("Server")
                .collectRequestQStats()
                .allowFailuresToDelay()
                .collectStateStatistics()
                .build();

        //Constant c1 = new Constant(0.5);
        Constant c1 = new Constant(3.0);
        TimeBasedFailure timeBasedFailure = myResource.addTimeBasedFailure(Constant.TWO, c1, c1);
        //SingleFailureEvent fe = new SingleFailureEvent(myResource, new Constant(5), new Constant(6));
        // myResource = new ResourceUnit.Builder(this).name("Server").build();
        ResourceFailureEvent mfe = new ResourceFailureEvent(this,
                new Constant(5), new Constant(6));
        mfe.addResourceUnit(myResource);
        mfe.addFailureEventListener(new FailureEventListener());

        myNumBusy = new TimeWeighted(this, 0.0, "NumBusy");
        myNS = new TimeWeighted(this, 0.0, "# in System");
        mySysTime = new ResponseVariable(this, "System Time");
    }

    protected class FailureEventListener implements FailureEventListenerIfc{

        @Override
        public void failureStarted() {
            System.out.println("The failureStarted event occurred!");
        }

        @Override
        public void failureCompleted() {
            System.out.println("The failureCompleted event occurred!");
        }
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
//        JSL.LOGGER.error("initialized");
        schedule(this::arrival).in(myArrivalRV).units();
    }

    private void arrival(JSLEvent<QObject> evt) {
        myNS.increment(); // new customer arrived
        QObject arrivingCustomer = new QObject(getTime());
        Request request = Request.builder()
                .createTime(getTime())
                .reactor(myRequestInteraction)
                .entity(arrivingCustomer)
                .duration(myServiceRS)
                .rule(PreemptionRule.RESUME)
                .build();

        Request seize = myResource.seize(request);
        schedule(this::arrival).in(myArrivalRV).units();

    }

    private void departure(JSLEvent<QObject> evt) {
        QObject departingCustomer = evt.getMessage();
        myNumBusy.decrement(); // customer is leaving server is freed
        if (!myWaitingQ.isEmpty()) { // enterWaitingState is not empty
            QObject c = myWaitingQ.removeNext(); //remove the next customer
            myNumBusy.increment(); // make server busy
            // schedule end of service
            schedule(this::departure).withMessage(c).in(myServiceRV).units();
        }
        mySysTime.setValue(getTime() - departingCustomer.getCreateTime());
        myNS.decrement(); // customer left system   
    }

    private class RequestInteraction implements RequestReactorIfc {

        @Override
        public void prepared(Request request) {
//            JSL.out.println(getTime() + "> Request " + request + " is prepared.");
        }

        @Override
        public void dequeued(Request request, Queue<Request> queue) {
            myWaitingQ.remove((QObject) request.getAttachedObject());
//            JSL.out.println(getTime() + "> Request " + request + " exited queue.");
        }

        @Override
        public void enqueued(Request request, Queue<Request> queue) {
            myWaitingQ.enqueue((QObject) request.getAttachedObject());
//            JSL.out.println(getTime() + "> Request " + request + " entered queue.");
        }

        @Override
        public void rejected(Request request) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void canceled(Request request) {
            // if canceled from allocated state then resource is no longer busy
            if (request.isPreviousStateAllocated()) {
                myNumBusy.decrement();
            }
//            JSL.out.println(getTime() + "> Request " + request + " was canceled.");
        }

        @Override
        public void preempted(Request request) {
            // if preempted the resource is no longer busy
            myNumBusy.decrement();
            myPreemptedRequest = request;
//            JSL.out.println(getTime() + "> Request " + request + " was preempted.");
        }

        @Override
        public void resumed(Request request) {
            myNumBusy.increment();
            myPreemptedRequest = null;
//            JSL.out.println(getTime() + "> Request " + request + " resumed using resource.");
        }

        @Override
        public void allocated(Request request) {
            myNumBusy.increment();
//            JSL.out.println(getTime() + "> Request " + request + " allocated resource.");
        }

        @Override
        public void completed(Request request) {
            myNumBusy.decrement();
//            JSL.out.println(getTime() + "> Request " + request + " completed.");
            mySysTime.setValue(getTime() - request.getCreateTime());
            myNS.decrement(); // customer left system  
        }

    }

    public static void main(String[] args) {
        Simulation sim = new Simulation("New Queue Testing");
        // get the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DTPQueueResourceModelWithFailures driveThroughPharmacy = new DTPQueueResourceModelWithFailures(m);
        driveThroughPharmacy.setArrivalRS(new Exponential(6.0));
        driveThroughPharmacy.setServiceRS(new Exponential(3.0));

        // set the parameters of the experiment
        sim.setNumberOfReplications(2);
        sim.setLengthOfReplication(20.0);
        sim.setLengthOfWarmUp(5.0);
//        sim.setNumberOfReplications(2);
//        sim.setLengthOfReplication(30.0);
//        sim.setLengthOfWarmUp(5000.0);

        SimulationReporter r = sim.makeSimulationReporter();
        System.out.println("Simulation started.");
        sim.run();
        r.printAcrossReplicationSummaryStatistics();
    }

}
