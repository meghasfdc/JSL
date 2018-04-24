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
