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
package ex.running;

import ex.models.DriveThroughPharmacy;
import jsl.modeling.Model;
import jsl.modeling.Simulation;
import jsl.modeling.StatisticalBatchingElement;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TWBatchingElement;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.random.rvariable.ExponentialRV;
import jsl.utilities.reporting.StatisticReporter;
import jsl.utilities.statistic.BatchStatistic;
import jsl.utilities.statistic.Statistic;

import java.util.Arrays;
import java.util.Map;

/**
 * Illustrates performing a batch means analysis
 *
 * @author rossetti
 */
public class JSLBatchingDemos {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        runBatchingExample();
        //sequentialBatchingExample();
    }

    public static void runBatchingExample() {
        Simulation sim = new Simulation("Drive Through Pharmacy");
        // getBatchStatisticObserver the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DriveThroughPharmacy driveThroughPharmacy = new DriveThroughPharmacy(m);
        driveThroughPharmacy.setArrivalRS(new ExponentialRV(1.0));
        driveThroughPharmacy.setServiceRS(new ExponentialRV(0.7));

        // create the batching element for the simulation
        StatisticalBatchingElement be = new StatisticalBatchingElement(m);
        // set the parameters of the experiment
        sim.setNumberOfReplications(1);
        sim.setLengthOfReplication(1300000.0);
        sim.setLengthOfWarmUp(100000.0);
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");

        //System.out.println(sim);
        // getBatchStatisticObserver a statistical reporter for the batching element
        StatisticReporter statisticReporter = be.getStatisticReporter();

        // print out the report
        System.out.println(statisticReporter.getHalfWidthSummaryReport());

        System.out.println(be);

        ResponseVariable systemTime = m.getResponseVariable("System Time");

        BatchStatistic batchStatistic = be.getBatchStatistic(systemTime);

        double[] batchMeanArrayCopy = batchStatistic.getBatchMeanArrayCopy();

        System.out.println(Arrays.toString(batchMeanArrayCopy));

        //System.out.println(statisticReporter.getHalfWidthSummaryReportAsLaTeXTabular());
    }

    public static void sequentialBatchingExample() {
        Simulation sim = new Simulation("Drive Through Pharmacy");
        // getBatchStatisticObserver the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DriveThroughPharmacy driveThroughPharmacy = new DriveThroughPharmacy(m);
        driveThroughPharmacy.setArrivalRS(new ExponentialRV(1.0));
        driveThroughPharmacy.setServiceRS(new ExponentialRV(0.7));

         // create the batching element for the simulation
        StatisticalBatchingElement be = new StatisticalBatchingElement(m);
      
        // create a TWBatchingElement for controlling the half-width
        TWBatchingElement twbe = new TWBatchingElement(m);
        // getBatchStatisticObserver getBatchStatisticObserver response to control
        TimeWeighted tw = m.getTimeWeighted("# in System");
        // add the response to the TWBatchingElement
        TWBatchingElement.TWBatchStatisticObserver bo = twbe.add(tw);
        // set up the observer's stopping criter
        bo.setDesiredHalfWidth(0.02);
        bo.setCollectionRule(Statistic.CollectionRule.HALF_WIDTH);

        // set the parameters of the experiment
        sim.setNumberOfReplications(1);
        //sim.setLengthOfReplication(200000.0);
        sim.setLengthOfWarmUp(100000.0);

        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");

        System.out.println(sim);
        // getBatchStatisticObserver a statistical reporter for the batching element
        
        StatisticReporter statisticReporter = be.getStatisticReporter();

        // print out the report
        System.out.println(statisticReporter.getHalfWidthSummaryReport());

        System.out.println(be);

        System.out.println(statisticReporter.getHalfWidthSummaryReportAsLaTeXTabular());
    }
}
