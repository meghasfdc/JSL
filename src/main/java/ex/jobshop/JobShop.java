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
package ex.jobshop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.Simulation;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.random.distributions.Gamma;
import jsl.modeling.SimulationReporter;

public class JobShop extends ModelElement {

    private List<WorkStation> myWorkStations;

    private List<Sequence> mySequences;

    private List<JobGenerator> myJobGenerators;

    public JobShop(ModelElement parent) {
        this(parent, null);
    }

    public JobShop(ModelElement parent, String name) {
        super(parent, name);
        myWorkStations = new ArrayList<WorkStation>();
        mySequences = new ArrayList<Sequence>();
        myJobGenerators = new ArrayList<JobGenerator>();
    }

    public WorkStation addWorkStation() {
        return (addWorkStation(1, null));
    }

    public WorkStation addWorkStation(int numMachines) {
        return (addWorkStation(numMachines, null));
    }

    public WorkStation addWorkStation(int numMachines, String name) {
        WorkStation station = new WorkStation(this, numMachines, name);
        myWorkStations.add(station);
        return (station);
    }

    public Sequence addSequence() {
        return (addSequence(null));
    }

    public Sequence addSequence(String name) {
        Sequence s = new Sequence(this, name);
        mySequences.add(s);
        return (s);
    }

    public JobGenerator addJobGenerator(RandomIfc timeBtwArrivals) {
        return (addJobGenerator(timeBtwArrivals, null));
    }

    public JobGenerator addJobGenerator(RandomIfc timeBtwArrivals, String name) {
        JobGenerator jg = new JobGenerator(this, timeBtwArrivals, timeBtwArrivals, name);
        myJobGenerators.add(jg);
        return (jg);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Jobshop Test");

        // Create the simulation
        Simulation sim = new Simulation("Jobshop");

        // get the containing model
        Model m = sim.getModel();

        // create the jobshop
        JobShop shop = new JobShop(m, "JobShop");

        // create the workstations
        WorkStation w1 = shop.addWorkStation(3, "w1");
        WorkStation w2 = shop.addWorkStation(2, "w2");
        WorkStation w3 = shop.addWorkStation(4, "w3");
        WorkStation w4 = shop.addWorkStation(3, "w4");
        WorkStation w5 = shop.addWorkStation(1, "w5");

        // create the sequences
        Sequence s1 = shop.addSequence();
        s1.addJobStep(w3, new Gamma(2.0, 0.5 / 2.0));
        s1.addJobStep(w1, new Gamma(2.0, 0.6 / 2.0));
        s1.addJobStep(w2, new Gamma(2.0, 0.85 / 2.0));
        s1.addJobStep(w5, new Gamma(2.0, 0.5 / 2.0));

        Sequence s2 = shop.addSequence();
        s2.addJobStep(w4, new Gamma(2.0, 1.1 / 2.0));
        s2.addJobStep(w1, new Gamma(2.0, 0.8 / 2.0));
        s2.addJobStep(w3, new Gamma(2.0, 0.75 / 2.0));

        Sequence s3 = shop.addSequence();
        s3.addJobStep(w2, new Gamma(2.0, 1.2 / 2.0));
        s3.addJobStep(w5, new Gamma(2.0, 0.25 / 2.0));
        s3.addJobStep(w1, new Gamma(2.0, 0.7 / 2.0));
        s3.addJobStep(w4, new Gamma(2.0, 0.9 / 2.0));
        s3.addJobStep(w3, new Gamma(2.0, 1.0 / 2.0));

        JobGenerator jg = shop.addJobGenerator(new Exponential(0.25));
        jg.addJobType("A", s1, 0.3);
        jg.addJobType("B", s2, 0.5);
        jg.addLastJobType("C", s3);

        // set the parameters of the experiment
        sim.setNumberOfReplications(30);

        sim.setLengthOfReplication(10000.0);
        sim.setLengthOfWarmUp(5000.0);

        // tell the experiment to run
        sim.run();

        SimulationReporter r = sim.makeSimulationReporter();
        //r.printAcrossReplicationStatistics();
        r.writeAcrossReplicationStatistics("JobShop");
        r.writeAcrossReplicationSummaryStatistics("JobShop Summary");
        r.printAcrossReplicationSummaryStatistics();

        //r.showAcrossReplicationSummaryStatisticsAsPDF();

    }
}
