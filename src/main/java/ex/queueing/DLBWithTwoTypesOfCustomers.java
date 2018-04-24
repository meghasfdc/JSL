/*
 * Copyright (c) 2018. Manuel D. Rossetti, manuelrossetti@gmail.com
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
package ex.queueing;


import java.util.Optional;
import jsl.modeling.EventActionIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.queue.QObject;
import jsl.modeling.queue.Queue;
import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.random.distributions.DistributionIfc;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.SimulationReporter;
import jsl.modeling.queue.QueueResponse;
import jsl.utilities.random.distributions.Bernoulli;
import jsl.utilities.statistic.StatisticAccessorIfc;

public class DLBWithTwoTypesOfCustomers extends SchedulingElement {

    private int myNumServers;

    private Queue myWaitingQ;

    private DistributionIfc myArrivalDistribution;

    private RandomVariable myArrivalRV;

    private TimeWeighted myNumBusy;

    private TimeWeighted myNS;

    private Counter myNumServed;

    private ResponseVariable mySysTime;

    private ArrivalEventAction myArrivalEventAction;

    private EndServiceEventAction myEndServiceEventAction;

    private RandomVariable myCommercialRV;

    private RandomVariable myResidentialRV;

    private RandomVariable myTypeRV;

    public DLBWithTwoTypesOfCustomers(ModelElement parent) {
        this(parent, 1, new Exponential(1.0));
    }

    public DLBWithTwoTypesOfCustomers(ModelElement parent, int numServers, DistributionIfc ad) {
        super(parent);

        setNumberOfServers(numServers);
        setArrivalDistributionInitialRandomSource(ad);

        myWaitingQ = new Queue(this, "DriverLicenseQ");

        myNumBusy = new TimeWeighted(this, 0.0, "NumBusy");

        myNS = new TimeWeighted(this, 0.0, "NS");

        myNumServed = new Counter(this, "Num Served");

        mySysTime = new ResponseVariable(this, "System Time");

        myCommercialRV = new RandomVariable(this, new Exponential(0.5));
        myResidentialRV = new RandomVariable(this, new Exponential(0.7));
        myTypeRV = new RandomVariable(this, new Bernoulli(0.3));

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

    public final void setCustomerTypeProbability(double p) {

        if (myTypeRV == null) { // not made yet
            myTypeRV = new RandomVariable(this, new Bernoulli(p));
        } else { // already had been made, and added to model
            // just change the parameter
            double[] parameters = myTypeRV.getParameters();
            parameters[0] = p;
            myTypeRV.setParameters(parameters);
        }

    }

    public final void setResidentialCustomerServiceTimeInitialRandomSource(DistributionIfc d) {

        if (d == null) {
            throw new IllegalArgumentException("Service Time Distribution was null!");
        }

        if (myResidentialRV == null) { // not made yet
            myResidentialRV = new RandomVariable(this, d);
        } else { // already had been made, and added to model
            // just change the distribution
            myResidentialRV.setInitialRandomSource(d);
        }

    }

    public final void setCommericalCustomerServiceTimeInitialRandomSource(DistributionIfc d) {

        if (d == null) {
            throw new IllegalArgumentException("Service Time Distribution was null!");
        }

        if (myCommercialRV == null) { // not made yet
            myCommercialRV = new RandomVariable(this, d);
        } else { // already had been made, and added to model
            // just change the distribution
            myCommercialRV.setInitialRandomSource(d);
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

    public final Optional<QueueResponse> getQueueResponses(){
        return myWaitingQ.getQueueResponses();
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
        scheduleEvent(myArrivalEventAction, myArrivalRV);
    }

    class Customer extends QObject {

        public double myST;
        
        public Customer(double creationTime) {
            this(creationTime, null);
        }

        public Customer(double creationTime, String name) {
            super(creationTime, name);
            
            if (myTypeRV.getValue() <= 0.0){
                myST = myResidentialRV.getValue();
            } else {
                myST = myCommercialRV.getValue();
            }
            
        }
        
    }
    
    class ArrivalEventAction implements EventActionIfc {

        @Override
        public void action(JSLEvent event) {
            myNS.increment(); // new customer arrived
            myWaitingQ.enqueue(new Customer(getTime())); // enqueue the newly arriving customer
            if (myNumBusy.getValue() < myNumServers) { // server available
                myNumBusy.increment(); // make server busy
                Customer customer = (Customer)myWaitingQ.removeNext(); //remove the next customer
                //	schedule end of service, include the customer as the event's message
                scheduleEvent(myEndServiceEventAction, customer.myST, customer);
            }
            //	always schedule the next arrival
            scheduleEvent(myArrivalEventAction, myArrivalRV);
        }
    }

    class EndServiceEventAction implements EventActionIfc {

        @Override
        public void action(JSLEvent event) {
            QObject leavingCustomer = (QObject) event.getMessage();
            mySysTime.setValue(getTime() - leavingCustomer.getCreateTime());
            myNS.decrement(); // customer departed
            myNumBusy.decrement(); // customer is leaving server is freed
            myNumServed.increment();
            if (!myWaitingQ.isEmpty()) { // queue is not empty
                Customer customer = (Customer)myWaitingQ.removeNext(); //remove the next customer
                myNumBusy.increment(); // make server busy
                //	schedule end of service, include the customer as the event's message
                scheduleEvent(myEndServiceEventAction, customer.myST, customer);
            }
        }
    }

    public static void main(String[] args) {

        Simulation sim = new Simulation("DLB_with_Two_Types_Customers");

        // create the model element and attach it to the main model
        new DLBWithTwoTypesOfCustomers(sim.getModel());

        // set the parameters of the experiment
        sim.setNumberOfReplications(30);
        sim.setLengthOfReplication(20000.0);
        sim.setLengthOfWarmUp(5000.0);

        SimulationReporter r = sim.makeSimulationReporter();

        //r.turnOnReplicationCSVStatisticReporting();
        System.out.println(sim);

        // tell the simulation to run
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");

        r.printAcrossReplicationSummaryStatistics();
    }

}
