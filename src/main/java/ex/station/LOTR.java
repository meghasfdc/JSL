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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.station;

import java.util.List;
import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.Simulation;
import jsl.modeling.SimulationReporter;
import jsl.modeling.queue.QObject;
import jsl.modeling.elements.station.ReceiveQObjectIfc;
import jsl.modeling.elements.station.SingleQueueStation;
import jsl.modeling.elements.variable.AcrossReplicationStatisticIfc;
import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.observers.variable.AcrossReplicationHalfWidthChecker;
import jsl.utilities.random.distributions.Beta;
import jsl.utilities.random.distributions.Binomial;
import jsl.utilities.random.distributions.Lognormal;
import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.distributions.ShiftedDistribution;
import jsl.utilities.random.distributions.Triangular;
import jsl.utilities.random.distributions.Uniform;
import jsl.utilities.random.distributions.Weibull;
import jsl.utilities.reporting.StatisticReporter;
import jsl.utilities.statistic.StatisticAccessorIfc;

/**
 *
 * @author rossetti
 */
public class LOTR extends ModelElement {

    private int myNumDailyCalls = 100;
    private double myRingTol = 0.02;
    private double myOTLimit = 960.0;
    private final Binomial mySuccessFullCallPMF;
    private final RandomVariable myNumSuccessFullCallsRV;
    private final RandomVariable mySalesCallProb;
    private final RandomVariable myMakeRingTimeRV;
    private final RandomVariable mySmallRingODRV;
    private final RandomVariable myBigRingIDRV;
    private final RandomVariable myInspectTimeRV;
    private final RandomVariable myPackingTimeRV;
    private final RandomVariable myReworkTimeRV;
    private final SingleQueueStation myRingMakingStation;
    private final SingleQueueStation myInspectionStation;
    private final SingleQueueStation myPackagingStation;
    private final SingleQueueStation myReworkStation;
    private final ResponseVariable mySystemTime;
    private final TimeWeighted myNumInSystem;
    private final Counter myNumCompleted;
    private final ResponseVariable myProbTooBig;
    private final ResponseVariable myProbTooSmall;
    private final ResponseVariable myProbOT;
    private final ResponseVariable myEndTime;
    private final TimeWeighted myNumInRMandInspection;
    private final ResponseVariable myTimeInRMandInspection;

//    private final SResource myRingMakers;
//    private final SResource myInspectors;
//    private final SResource myPackagers;
//    private final SResource myReworkers;
    public LOTR(ModelElement parent, String name) {
        super(parent, name);
        mySuccessFullCallPMF = new Binomial(0.5, myNumDailyCalls);
        myNumSuccessFullCallsRV = new RandomVariable(this, mySuccessFullCallPMF);
        myNumSuccessFullCallsRV.setResetInitialParametersWarningFlag(false);
        mySalesCallProb = new RandomVariable(this, new Beta(5.0, 1.5));
        myMakeRingTimeRV = new RandomVariable(this, new Uniform(5, 15));
        mySmallRingODRV = new RandomVariable(this, new Normal(1.49, 0.005 * 0.005));
        myBigRingIDRV = new RandomVariable(this, new Normal(1.5, 0.002 * 0.002));
        myInspectTimeRV = new RandomVariable(this, new Triangular(2, 4, 7));
        myPackingTimeRV = new RandomVariable(this, new Lognormal(7, 1));
        myReworkTimeRV = new RandomVariable(this,
                new ShiftedDistribution(new Weibull(3, 15), 5));
        myRingMakingStation = new SingleQueueStation(this, myMakeRingTimeRV,
                "RingMakingStation");
        myInspectionStation = new SingleQueueStation(this, myInspectTimeRV,
                "InspectStation");
        myReworkStation = new SingleQueueStation(this, myReworkTimeRV,
                "ReworkStation");
        myPackagingStation = new SingleQueueStation(this, myPackingTimeRV,
                "PackingStation");
        myRingMakingStation.setNextReceiver(myInspectionStation);
        myInspectionStation.setNextReceiver(new AfterInspection());
        myReworkStation.setNextReceiver(myPackagingStation);
        myPackagingStation.setNextReceiver(new Dispose());
        mySystemTime = new ResponseVariable(this, "System Time");
        myNumInSystem = new TimeWeighted(this, "Num in System");
        myNumCompleted = new Counter(this, "Num Completed");
        myProbTooBig = new ResponseVariable(this, "Prob too Big");
        myProbTooSmall = new ResponseVariable(this, "Prob too Small");
        myProbOT = new ResponseVariable(this, "Prob of Over Time");
        myEndTime = new ResponseVariable(this, "Time to Make Orders");
        myNumInRMandInspection = new TimeWeighted(this, "Num in RM and Inspection");
        myTimeInRMandInspection = new ResponseVariable(this, "Time in RM and Inspection");
    }

    @Override
    protected void initialize() {
        super.initialize();
        double p = mySalesCallProb.getValue();
        mySuccessFullCallPMF.setParameters(p, myNumDailyCalls);
        int n = (int) myNumSuccessFullCallsRV.getValue();
        for (int i = 0; i < n; i++) {
            myRingMakingStation.receive(new RingOrder());
            myNumInSystem.increment();
            myNumInRMandInspection.increment();
        }
    }

    protected class AfterInspection implements ReceiveQObjectIfc {

        @Override
        public void receive(QObject qObj) {
            myNumInRMandInspection.decrement();
            myTimeInRMandInspection.setValue(getTime() - qObj.getCreateTime());
            RingOrder order = (RingOrder) qObj;
            if (order.myNeedsReworkFlag) {
                myReworkStation.receive(order);
            } else {
                myPackagingStation.receive(order);
            }
        }

    }

    protected class Dispose implements ReceiveQObjectIfc {

        @Override
        public void receive(QObject qObj) {
            // collect final statistics
            RingOrder order = (RingOrder) qObj;
            myNumInSystem.decrement();
            mySystemTime.setValue(getTime() - order.getCreateTime());
            myNumCompleted.increment();
            myProbTooBig.setValue(order.myTooBigFlag);
            myProbTooSmall.setValue(order.myTooSmallFlag);
        }

    }

    @Override
    protected void replicationEnded() {
        super.replicationEnded();
        myProbOT.setValue(getTime() > myOTLimit);
        myEndTime.setValue(getTime());
    }

    private class RingOrder extends QObject {

        private double myBigRingID;
        private double mySmallRingOuterD;
        private double myGap;
        private boolean myNeedsReworkFlag = false;
        private boolean myTooBigFlag = false;
        private boolean myTooSmallFlag = false;

        public RingOrder() {
            this(getTime(), null);
        }

        public RingOrder(double creationTime, String name) {
            super(creationTime, name);
            myBigRingID = myBigRingIDRV.getValue();
            mySmallRingOuterD = mySmallRingODRV.getValue();
            myGap = myBigRingID - mySmallRingOuterD;
            if (mySmallRingOuterD > myBigRingID) {
                myTooBigFlag = true;
                myNeedsReworkFlag = true;
            } else if (myGap > myRingTol) {
                myTooSmallFlag = true;
                myNeedsReworkFlag = true;
            }
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //test1();
        test2();

    }

    public static void test1() {
        Simulation sim = new Simulation("LOTR Example");
        // get the model
        Model m = sim.getModel();
        // add system to the main model
        LOTR system = new LOTR(m, "LOTR");
        // set the parameters of the experiment
        sim.setNumberOfReplications(61);
        SimulationReporter r = sim.makeSimulationReporter();
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");
        r.printAcrossReplicationSummaryStatistics();
        //List<StringBuilder> sb = r.getAcrossReplicatonStatisticsAsLaTeXTables();
        List<StatisticAccessorIfc> list = r.getAcrossReplicationStatisticsList();
        StatisticReporter statisticReporter = new StatisticReporter(list);
        System.out.println(statisticReporter.getHalfWidthSummaryReport());
        //System.out.println(sb);
        AcrossReplicationStatisticIfc rsv = m.getAcrossReplicationResponseVariable("Time in RM and Inspection");
        StatisticAccessorIfc stat = rsv.getAcrossReplicationStatistic();
        System.out.println("hw = " + stat.getHalfWidth());
        System.out.println(stat.getConfidenceInterval());
    }
    
    public static void test2(){
        Simulation sim = new Simulation("LOTR Example");
        // get the model
        Model m = sim.getModel();
        // add system to the main model
        LOTR system = new LOTR(m, "LOTR");
        ResponseVariable rsv = m.getResponseVariable("Time in RM and Inspection");
        AcrossReplicationHalfWidthChecker hwc = new AcrossReplicationHalfWidthChecker(20.0);
        rsv.addObserver(hwc);
        // set the parameters of the experiment
        sim.setNumberOfReplications(1000);
        SimulationReporter r = sim.makeSimulationReporter();
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");
        System.out.println(sim);
        List<StatisticAccessorIfc> list = r.getAcrossReplicationStatisticsList();
        StatisticReporter statisticReporter = new StatisticReporter(list);
        StringBuilder halfWidthSummaryReport = statisticReporter.getHalfWidthSummaryReport();
        System.out.println(halfWidthSummaryReport);
        //List<StringBuilder> sb = r.getAcrossReplicatonStatisticsAsLaTeXTables();
        //System.out.println(sb);
        List<StringBuilder> halfWidthSummaryReportAsLaTeXTables = statisticReporter.getHalfWidthSummaryReportAsLaTeXTables();
        System.out.println(halfWidthSummaryReportAsLaTeXTables);
    }

}
