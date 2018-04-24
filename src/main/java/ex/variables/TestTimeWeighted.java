/*
 * Created on Apr 19, 2007
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
package ex.variables;

import java.io.PrintWriter;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.reporting.JSL;
import jsl.modeling.SimulationReporter;
import jsl.modeling.StatisticalBatchingElement;
import jsl.utilities.reporting.StatisticReporter;

/**
 * @author rossetti
 *
 */
public class TestTimeWeighted extends SchedulingElement {

    TimeWeighted myX;

    /**
     * @param parent
     */
    public TestTimeWeighted(ModelElement parent) {
        this(parent, null);
    }

    /**
     * @param parent
     * @param name
     */
    public TestTimeWeighted(ModelElement parent, String name) {
        super(parent, name);
        myX = new TimeWeighted(this, 0.0);
    }

    @Override
    protected void initialize() {
        scheduleEvent(20.0);
    }

    @Override
    protected void handleEvent(JSLEvent e) {
        System.out.println(getTime() + ">");

        myX.setValue(2.0);

        System.out.println(myX.getWithinReplicationStatistic());
    }

    @Override
    protected void replicationEnded() {
        System.out.println("replicationEnded()");
        System.out.println(myX.getWithinReplicationStatistic());
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

         testExperiment();

        //	testReplication();

       // testBatchReplication();
    }

    public static void testExperiment() {
        Simulation sim = new Simulation();

        new TestTimeWeighted(sim.getModel());

        // set the running parameters of the experiment
        sim.setNumberOfReplications(2);
        sim.setLengthOfReplication(50.0);

        // tell the experiment to run
        sim.run();
        System.out.println(sim);

        SimulationReporter r = sim.makeSimulationReporter();
        r.printAcrossReplicationStatistics();
    }

    public static void testBatchReplication() {
        Simulation sim = new Simulation();

        new TestTimeWeighted(sim.getModel());

        sim.turnOnStatisticalBatching();
        StatisticalBatchingElement be = sim.getStatisticalBatchingElement().get();

                // set the running parameters of the replication
        sim.setLengthOfReplication(50.0);

        // tell the experiment to run
        sim.run();

        System.out.println(sim);
        System.out.println(be);

        PrintWriter w = JSL.makePrintWriter(sim.getName() + "BatchStatistics", "csv");

        StatisticReporter statisticReporter = be.getStatisticReporter();
        StringBuilder csvStatistics = statisticReporter.getCSVStatistics(true);
        w.print(csvStatistics);

        System.out.println("Done!");

    }

    public static void testReplication() {
        Simulation sim = new Simulation();

        new TestTimeWeighted(sim.getModel());

        // set the running parameters of the replication
        sim.setLengthOfReplication(50.0);

        // tell the experiment to run
        sim.run();

        SimulationReporter r = sim.makeSimulationReporter();
        r.printAcrossReplicationStatistics();
        System.out.println("Done!");
    }
}
