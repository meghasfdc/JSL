/*
 * Copyright (c) 2019. Manuel D. Rossetti, rossetti@uark.edu
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

package ineg5823sp19.hw5;

import ex.hospitalward.HospitalWard;
import jsl.modeling.Model;
import jsl.modeling.Simulation;
import jsl.modeling.SimulationReporter;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.utilities.reporting.JSL;
import jsl.utilities.welch.WelchDataCollector;
import jsl.utilities.welch.WelchDataFileAnalyzer;
import jsl.utilities.welch.WelchDataFileCollector;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.File;
import java.util.Arrays;

public class Problem3 {

    public static void main(String[] args) {
        Simulation s = new Simulation("Hospital Ward Simulation");

        // create the containing model
        Model m = s.getModel();

        // create the model element and attach it to the model
        HospitalWard hw = new HospitalWard(m, "HospitalWard");
        hw.setInitialNumberOfBeds(40);

        ResponseVariable systemTime = m.getResponseVariable("System Time");

        // make the data collectors and attach them as observers
        WelchDataCollector w = new WelchDataCollector(1000, 1);
        systemTime.addObserver(w);
        // create a directory for the results
        File d = JSL.makeOutputSubDirectory("welchDir");
        WelchDataFileCollector stWDFC = new WelchDataFileCollector(d, "systime");
        systemTime.addObserver(stWDFC);

        // set the parameters of the experiment
        s.setNumberOfReplications(5);
        s.setLengthOfWarmUp(5000.0);
        s.setLengthOfReplication(15000.0);

        // tell the experiment to run
        s.run();

        System.out.println(s);
        SimulationReporter r = s.makeSimulationReporter();
        r.printAcrossReplicationSummaryStatistics();

        System.out.println(stWDFC);
        WelchDataFileAnalyzer wa = stWDFC.makeWelchDataFileAnalyzer();
        System.out.println(wa);
        wa.makeWelchPlotDataFile();
        //wa.makeCSVWelchPlotDataFile();
        // wa.displayWelchChart(); // uncomment for chart

        double[][] data = w.getData();

        RealMatrix rm = new Array2DRowRealMatrix(data);
        double[] column = rm.getColumn(0);
        for(double x: column){
            JSL.out.println(x);// write out the jslOutput.txt and then make plot
        }

    }
}
