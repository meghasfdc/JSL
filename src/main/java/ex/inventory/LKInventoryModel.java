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
package ex.inventory;

import java.io.IOException;
import java.lang.Math;
import jsl.modeling.elements.variable.*;
import jsl.modeling.elements.*;
import jsl.modeling.*;
import jsl.observers.textfile.CSVReplicationReport;
import jsl.observers.textfile.CSVReport;
import jsl.utilities.random.*;
import jsl.utilities.random.distributions.Constant;
import jsl.utilities.random.distributions.DEmpiricalPMF;
import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.random.distributions.Uniform;
import jsl.modeling.SimulationReporter;

/**
 *
 */
public class LKInventoryModel extends SchedulingElement {

    private int myOrderUpToLevel = 40;

    private int myReorderPoint = 20;

    private double myHoldingCost = 1.0;

    private double myCostPerItem = 3.0;

    private double myBackLogCost = 5.0;

    private double mySetupCost = 32;

    private double myInitialInventoryLevel = 60;

    private Variable myLeadTime;

    private Variable myDemandAmount;

    private TimeWeighted myInvLevel;

    private TimeWeighted myPosInv;

    private TimeWeighted myNegInv;

    private TimeWeighted myAvgTotalCost;

    private TimeWeighted myAvgHoldingCost;

    private TimeWeighted myAvgSetupCost;

    private TimeWeighted myAvgShortageCost;

    private EventGenerator myDemandGenerator;

    private EventGenerator myInventoryCheckGenerator;

    private OrderArrival myOrderArrivalListener;

    /** Creates a new instance of LKInventoryModel */
    public LKInventoryModel(ModelElement parent) {
        super(parent);
        myDemandGenerator =
                new EventGenerator(this, new DemandArrival(), new Exponential(0.1), new Exponential(0.1), "Demand Generator");
        myInventoryCheckGenerator =
                new EventGenerator(this, new InventoryCheck(), new Constant(0.0), new Constant(1.0), "Inventory Check");
        myLeadTime = new RandomVariable(this, new Uniform(0.5, 1.0));
        DEmpiricalPMF d = new DEmpiricalPMF();
        d.addProbabilityPoint(1.0, 0.167);
        d.addProbabilityPoint(2.0, 0.333);
        d.addProbabilityPoint(3.0, 0.333);
        d.addLastProbabilityPoint(4.0);
        myDemandAmount = new RandomVariable(this, d);
        myOrderArrivalListener = new OrderArrival();
        myInvLevel = new TimeWeighted(this, 0.0, "Inventory Level");
        myNegInv = new TimeWeighted(this, 0.0, "BackOrder Level");
        myPosInv = new TimeWeighted(this, 0.0, "On Hand Level");
        myAvgTotalCost = new TimeWeighted(this, "Avg Total Cost");
        myAvgSetupCost = new TimeWeighted(this, "Avg Setup Cost");
        myAvgHoldingCost = new TimeWeighted(this, "Avg Holding Cost");
        myAvgShortageCost = new TimeWeighted(this, "Avg Shortage Cost");
    }

    public void setInitialInventoryLevel(double level) {
        myInitialInventoryLevel = level;
        myInvLevel.setInitialValue(myInitialInventoryLevel);
        myPosInv.setInitialValue(Math.max(0, myInvLevel.getInitialValue()));
        myNegInv.setInitialValue(-Math.min(0, myInvLevel.getInitialValue()));
        myAvgHoldingCost.setInitialValue(myHoldingCost * myPosInv.getInitialValue());
        myAvgShortageCost.setInitialValue(myBackLogCost * myNegInv.getInitialValue());
        myAvgSetupCost.setInitialValue(0.0);
        double cost =
                myAvgSetupCost.getInitialValue() + myAvgHoldingCost.getInitialValue() + myAvgShortageCost.getInitialValue();
        myAvgTotalCost.setInitialValue(cost);

    }

    public void setReorderPoint(int level) {
        myReorderPoint = level;
    }

    public void setOrderUpToLevel(int level) {
        myOrderUpToLevel = level;
    }

    protected void initialize() {
        super.initialize();
        setInitialInventoryLevel(myInitialInventoryLevel);
    }

    private void scheduleReplenishment(double orderSize) {
        double t = myLeadTime.getValue();
        scheduleEvent(myOrderArrivalListener, t, "Order Arrival", new Double(orderSize));
    }

    private class DemandArrival implements EventGeneratorActionIfc {

        public void generate(EventGenerator generator, JSLEvent event) {
            myInvLevel.decrement(myDemandAmount.getValue());
            myPosInv.setValue(Math.max(0, myInvLevel.getValue()));
            myNegInv.setValue(-Math.min(0, myInvLevel.getValue()));
            myAvgHoldingCost.setValue(myHoldingCost * myPosInv.getValue());
            myAvgShortageCost.setValue(myBackLogCost * myNegInv.getValue());
            double cost = myAvgSetupCost.getValue() + myAvgHoldingCost.getValue() + myAvgShortageCost.getValue();
            myAvgTotalCost.setValue(cost);
        }
    }

    private class InventoryCheck implements EventGeneratorActionIfc {

        public void generate(EventGenerator generator, JSLEvent event) {
            if (myInvLevel.getValue() < myReorderPoint) {
                double orderSize = myOrderUpToLevel - myInvLevel.getValue();
                scheduleReplenishment(orderSize);
                myAvgSetupCost.setValue(mySetupCost + myCostPerItem * orderSize);
            } else {
                myAvgSetupCost.setValue(0.0);
            }
            double cost = myAvgSetupCost.getValue() + myAvgHoldingCost.getValue() + myAvgShortageCost.getValue();
            myAvgTotalCost.setValue(cost);
        }
    }

    private class OrderArrival implements EventActionIfc {

        public void action(JSLEvent event) {
            Double ordersize = (Double) event.getMessage();
            myInvLevel.increment(ordersize.doubleValue());
            myPosInv.setValue(Math.max(0, myInvLevel.getValue()));
            myNegInv.setValue(-Math.min(0, myInvLevel.getValue()));
            myAvgHoldingCost.setValue(myHoldingCost * myPosInv.getValue());
            myAvgShortageCost.setValue(myBackLogCost * myNegInv.getValue());
            double cost = myAvgSetupCost.getValue() + myAvgHoldingCost.getValue() + myAvgShortageCost.getValue();
            myAvgTotalCost.setValue(cost);
        }
    }

    public static void main(String[] args) {
        System.out.println("LKInventory Test");

        Simulation s = new Simulation("LK Inventory Test");
        SimulationReporter r = s.makeSimulationReporter();
        r.turnOnReplicationCSVStatisticReporting();

        // create the containing model
        Model m = s.getModel();

        // create the model element and attach it to the main model
        LKInventoryModel im = new LKInventoryModel(m);
        im.setReorderPoint(20);
        im.setOrderUpToLevel(40);

        // set the parameters of the experiment
        s.setNumberOfReplications(10);
        s.setLengthOfReplication(120.0);
        s.setLengthOfWarmUp(20.0);
        s.run();
        
        r.printAcrossReplicationStatistics();
        
        System.out.println("Done!");
    }
}
