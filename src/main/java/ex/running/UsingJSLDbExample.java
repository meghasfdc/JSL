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

package ex.running;

import ex.queueing.DriverLicenseBureauWithQ;
import jsl.modeling.Simulation;
import jsl.utilities.dbutil.DatabaseFactory;
import jsl.utilities.dbutil.DatabaseIfc;
import jsl.utilities.random.rvariable.ExponentialRV;
import jsl.utilities.reporting.JSLDatabase;
import jsl.utilities.statistic.MultipleComparisonAnalyzer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UsingJSLDbExample {

    private static JSLDatabase mcb_db;

    private static String response;

    public static void main(String[] args) {

        // the first one has to create the database
        runFirstSimulation();

        // run each of the next experiments, changing the name of the experiment and
        // using the existing database
        run2ndSimulation();

        run3rdSimulation();

        // access the database to do the mcb analysis on a particular response for each of the experiments

        Set<String> experimentNames = new HashSet<>(Arrays.asList("FirstName", "2ndName", "3rdName"));

        MultipleComparisonAnalyzer analyzer = mcb_db.getMultipleComparisonAnalyzerFor(experimentNames, "System Time");

        System.out.println(analyzer);

    }

    private static void runFirstSimulation() {

        // creates a Simulation w/o a default database
        Simulation sim = new Simulation("The Simulation");
        // create a database with the name you want, that will not be cleared between experiments
        // this also attaches it to the simulation
        mcb_db = JSLDatabase.createEmbeddedDerbyJSLDatabase(sim, false, "MCB_Db");

        // set the parameters of the experiment
        sim.setNumberOfReplications(30);
        sim.setLengthOfReplication(20000.0);
        sim.setLengthOfWarmUp(5000.0);
        sim.setExperimentName("FirstName");

        DriverLicenseBureauWithQ dlb = new DriverLicenseBureauWithQ(sim.getModel());

        // here is where you set up the model with the correct configuration
        // by setting appropriate input parameters


        // now run the model
        sim.run();
    }


    public static void run2ndSimulation(){
        // creates a Simulation w/o a default database
        Simulation sim = new Simulation();
        // use the existing database for the new simulation instance
        JSLDatabase.useExistingEmbeddedDerbyJSLDatabase(sim, "MCB_Db");
        // set the parameters of the experiment
        sim.setNumberOfReplications(30);
        sim.setLengthOfReplication(20000.0);
        sim.setLengthOfWarmUp(5000.0);
        sim.setExperimentName("2ndName");

        DriverLicenseBureauWithQ dlb = new DriverLicenseBureauWithQ(sim.getModel());
        // here is where you set up the model with the correct configuration
        // by setting appropriate input parameters, here I am just increasing the mean service time

        dlb.setServiceDistributionInitialRandomSource(new ExponentialRV(.7));

        sim.run();
    }

    public static void run3rdSimulation(){
        // creates a Simulation w/o a default database
        Simulation sim = new Simulation();
        // use the existing database for the new simulation instance
        JSLDatabase.useExistingEmbeddedDerbyJSLDatabase(sim, "MCB_Db");
        // set the parameters of the experiment
        sim.setNumberOfReplications(30);
        sim.setLengthOfReplication(20000.0);
        sim.setLengthOfWarmUp(5000.0);
        sim.setExperimentName("3rdName");

        DriverLicenseBureauWithQ dlb = new DriverLicenseBureauWithQ(sim.getModel());
        // here is where you set up the model with the correct configuration
        // by setting appropriate input parameters, here I am just increasing the mean service time
        dlb.setServiceDistributionInitialRandomSource(new ExponentialRV(.8));

        sim.run();
    }
}
