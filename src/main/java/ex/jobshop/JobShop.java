/*
 * Created on Feb 10, 2007
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
