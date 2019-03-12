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
import java.io.File;
import java.io.IOException;
import jsl.modeling.Model;
import jsl.modeling.Simulation;
import jsl.modeling.SimulationReporter;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.random.rvariable.ExponentialRV;
import jsl.utilities.reporting.JSL;
import jsl.utilities.statistic.BatchStatistic;
import jsl.utilities.welch.WelchDataCollectorTW;
import jsl.utilities.welch.WelchDataFileAnalyzer;
import jsl.utilities.welch.WelchDataFileCollector;
import jsl.utilities.welch.WelchDataFileCollectorTW;

/**
 * Illustrates the use of the classes in the jsl.utilities.welch package
 *
 * @author rossetti
 */
public class CaptureWarmUpData {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        //tracingResponseVariables();
        displayWelchData();
    }

    public static void tracingResponseVariables() {
        Simulation sim = new Simulation("DTP");
        // get the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DriveThroughPharmacy driveThroughPharmacy = new DriveThroughPharmacy(m);
        driveThroughPharmacy.setArrivalRS(new ExponentialRV(1.0));
        driveThroughPharmacy.setServiceRS(new ExponentialRV(0.7));
        ResponseVariable rv = m.getResponseVariable("System Time");
        rv.turnOnTrace(true);
        //rv.turnOnTrace();
        // set the parameters of the experiment
        sim.setNumberOfReplications(5);
        sim.setLengthOfReplication(20.0);
        sim.setLengthOfWarmUp(5.0);
        SimulationReporter r = sim.makeSimulationReporter();
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");
        r.printAcrossReplicationSummaryStatistics();
    }

    public static void testWelchDataFile() {
        Simulation sim = new Simulation("DTP");
        // get the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DriveThroughPharmacy driveThroughPharmacy = new DriveThroughPharmacy(m);
        driveThroughPharmacy.setArrivalRS(new ExponentialRV(1.0));
        driveThroughPharmacy.setServiceRS(new ExponentialRV(0.7));
        ResponseVariable rv = m.getResponseVariable("System Time");
        TimeWeighted tw = m.getTimeWeighted("# in System");
        tw.turnOnTrace(true);
        File d = JSL.makeOutputSubDirectory(sim.getName());
        WelchDataFileCollector wdfc = new WelchDataFileCollector(d, "welchsystime");
        rv.addObserver(wdfc);
        WelchDataFileCollectorTW wdfctw = new WelchDataFileCollectorTW(1, d, "numInSystem");
        tw.addObserver(wdfctw);
        WelchDataCollectorTW wtw = new WelchDataCollectorTW(1, 30);
        tw.addObserver(wtw);
        //rv.turnOnTrace();
        // set the parameters of the experiment
        sim.setNumberOfReplications(5);
        sim.setLengthOfReplication(20.0);
        SimulationReporter r = sim.makeSimulationReporter();
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");
        r.printAcrossReplicationSummaryStatistics();
        System.out.println(wdfc);
        WelchDataFileAnalyzer wa = wdfc.makeWelchDataFileAnalyzer();
        System.out.println(wa);

        long n = wa.getMinNumObservationsInReplications();
        int rep = wa.getNumberOfReplications();

        System.out.println("Writing welch data to csv");
        wa.makeCSVWelchPlotDataFile();
        System.out.println("Writing welch data to welch data file");

        wa.makeWelchPlotDataFile();

        WelchDataFileAnalyzer wa1 = wdfctw.makeWelchDataFileAnalyzer();
        wa1.makeCSVWelchPlotDataFile();
        wa1.makeWelchPlotDataFile();
        wa1.displayWelchChart();

    }

    public static void displayWelchData() throws IOException {
        Simulation sim = new Simulation("DTP");
        // get the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DriveThroughPharmacy driveThroughPharmacy = new DriveThroughPharmacy(m);
        driveThroughPharmacy.setArrivalRS(new ExponentialRV(1.0));
        driveThroughPharmacy.setServiceRS(new ExponentialRV(0.7));
        // get access to the response variables
        ResponseVariable stRV = m.getResponseVariable("System Time");
        TimeWeighted nisTW = m.getTimeWeighted("# in System");

        // create a directory for the results
        File d = JSL.makeOutputSubDirectory("welchDir");
        // make the data collectors and attach them as observers
        WelchDataFileCollector stWDFC = new WelchDataFileCollector(d, "welchsystime");
        // need to specify the discretizing interval for time weighted
        WelchDataFileCollectorTW nisWDFC = new WelchDataFileCollectorTW(10, d, "numInSystem");
        stRV.addObserver(stWDFC);
        nisTW.addObserver(nisWDFC);

        // set up the simulation and run it
        sim.setNumberOfReplications(10);
        sim.setLengthOfReplication(30000.0);
        System.out.println("Simulation started.");
        sim.run();
        System.out.println("Simulation completed.");
        System.out.println();

        // print out some stuff just to show it
        System.out.println(stWDFC);
        System.out.println(nisWDFC);

        // make the Welch data file analyzers
        WelchDataFileAnalyzer stWDFA = stWDFC.makeWelchDataFileAnalyzer();
        WelchDataFileAnalyzer nisWDFA = nisWDFC.makeWelchDataFileAnalyzer();

        stWDFA.makeCSVWelchPlotDataFile();
        nisWDFA.makeCSVWelchPlotDataFile();

        BatchStatistic batchWelchAverages = stWDFA.batchWelchAverages();
        System.out.println(batchWelchAverages);

        double ts1 = WelchDataFileAnalyzer.getNegativeBiasTestStatistic(batchWelchAverages);
        System.out.println("neg bias test statistic = " + ts1);

        double ts2 = WelchDataFileAnalyzer.getPositiveBiasTestStatistic(batchWelchAverages);
        System.out.println("pos bias test statistic = " + ts2);
        
        double[] psums = WelchDataFileAnalyzer.getPartialSums(batchWelchAverages);
        for(double ps: psums){
            JSL.out.println(ps);
        }
        // display the chart
        stWDFA.displayWelchChart();
        // display the other chart
        //nisWDFA.displayWelchChart();
    }
}
