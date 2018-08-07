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
package ex.models;

import jsl.modeling.*;
import jsl.modeling.queue.QObject;
import jsl.modeling.queue.Queue;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.reporting.JSLDatabase;
import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.statistic.MultipleComparisonAnalyzer;
import org.jooq.Record5;
import org.jooq.Result;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class DriveThroughPharmacy extends SchedulingElement {

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

    public DriveThroughPharmacy(ModelElement parent) {
        this(parent, 1, new Exponential(1.0), new Exponential(0.5));
    }

    public DriveThroughPharmacy(ModelElement parent, int numServers) {
        this(parent, numServers, new Exponential(1.0), new Exponential(0.5));
    }

    public DriveThroughPharmacy(ModelElement parent, int numServers, RandomIfc ad, RandomIfc sd) {
        super(parent);
        setNumberOfPharmacists(numServers);
        setServiceRS(sd);
        setArrivalRS(ad);
        myWaitingQ = new Queue(this, "PharmacyQ");
        myNumBusy = new TimeWeighted(this, 0.0, "NumBusy");
        myNS = new TimeWeighted(this, 0.0, "# in System");
        mySysTime = new ResponseVariable(this, "System Time");
        myArrivalEventAction = new ArrivalEventAction();
        myEndServiceEventAction = new EndServiceEventAction();
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
        runModel(1);
        //runModel(2);
    }

    public static void runModel(int numServers) {

        PrintWriter printWriter = new PrintWriter(System.out);
        Simulation sim = new Simulation("Drive Through Pharmacy", true);

        sim.turnOnStatisticalBatching();
        //sim.setClearDbOption(false);
        // get the model
        Model m = sim.getModel();

        // add DriveThroughPharmacy to the main model
        DriveThroughPharmacy dtp = new DriveThroughPharmacy(m, numServers);
        dtp.setArrivalRS(new Exponential(6.0));
        dtp.setServiceRS(new Exponential(3.0));
        //m.turnOnTimeIntervalCollection(100);
        // set the parameters of the experiment
        sim.setNumberOfReplications(30);
        sim.setLengthOfReplication(20000.0);
        sim.setLengthOfWarmUp(5000.0);
        SimulationReporter r = sim.makeSimulationReporter();

//        for(ModelElement me: m.getModelElements()){
//            System.out.println(me);
//        }
//
//        System.out.println(m.getModelElementsAsString());

        r.turnOnReplicationCSVStatisticReporting();
        sim.setExperimentName("1st Run");
        System.out.println("Simulation started.");
        sim.run();
        r.printAcrossReplicationSummaryStatistics();

        //sim.getJSLDatabase().getAcrossRepStatRecords().format(printWriter);
        dtp.setNumberOfPharmacists(2);
        sim.setExperimentName("2nd Run");
        sim.run();
        System.out.println("Simulation completed.");
        r.printAcrossReplicationSummaryStatistics();

        //sim.getJSLDatabase().getAcrossRepStatRecords().format(printWriter);
        String responseName = dtp.getSystemTimeResponse().getName();
        Optional<JSLDatabase> db = sim.getDefaultJSLDatabase();
        Result<Record5<Integer, String, Integer, Integer, Double>> resultSet = db.get().getWithinRepAveragesAsResultSet(responseName);

        resultSet.format(printWriter);

//        Map<String, List<Double>> map = resultSet.intoGroups(SIMULATION_RUN.EXP_NAME, WITHIN_REP_STAT.AVERAGE);
//
//        map.get("1st Run").forEach(System.out::println);

        Map<String, double[]> withRepAveragesAsMap = db.get().getWithRepAveragesAsMap(responseName);

        System.out.println();
        for(String name: withRepAveragesAsMap.keySet()){
            double[] doubles = withRepAveragesAsMap.get(name);
            System.out.println(name);
            System.out.println(Arrays.asList(doubles));
        }


//        MultipleComparisonAnalyzer multipleComparisonAnalyzer = new MultipleComparisonAnalyzer(withRepAveragesAsMap);
 //       System.out.println(multipleComparisonAnalyzer);

//        try {
//            System.out.println();
//            System.out.println("Tablesaw work");
//            Table tablesawTable = sim.getJSLDatabase().getAcrossRepStatRecordsAsTablesawTable("AcrossRepStats");
//            //System.out.println(tablesawTable.columnNames());
//            //System.out.println(tablesawTable.structure().print());
//            System.out.println(tablesawTable.print());
//            System.out.println("Tablesaw work");
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }


    }

}
