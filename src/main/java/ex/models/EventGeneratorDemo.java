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
package ex.models;

import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.SimulationReporter;
import jsl.modeling.elements.EventGenerator;
import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.distributions.Constant;
import jsl.utilities.random.distributions.DEmpiricalCDF;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.elements.EventGeneratorActionIfc;

/**
 * Arrivals are governed by a compound Poisson process. An EventGenerator is used
 *
 * @author rossetti
 */
public class EventGeneratorDemo extends SchedulingElement {

    protected EventGenerator myArrivalGenerator;
    protected Counter myEventCounter;
    protected Counter myArrivalCounter;
    protected RandomVariable myTBA;
    protected RandomVariable myNumArrivals;

    public EventGeneratorDemo(ModelElement parent) {
        this(parent, 1.0, null);
    }

    public EventGeneratorDemo(ModelElement parent, double tba, String name) {
        super(parent, name);
        double[] a = {1, 0.2, 2, 0.5, 3, 1.0};
        myNumArrivals = new RandomVariable(this, new DEmpiricalCDF(a));
        //RandomIfc rs = new Exponential(tba);
        RandomIfc rs = new Constant(tba);
        myTBA = new RandomVariable(this, rs);
        myEventCounter = new Counter(this, "Counts Events");
        myArrivalCounter = new Counter(this, "Counts Arrivals");
        myArrivalGenerator = new EventGenerator(this, new Arrivals(), myTBA, myTBA,1);
        myArrivalGenerator.setInitialTimeUntilFirstEvent(Constant.ZERO);
        //myArrivalGenerator.setInitialMaximumNumberOfEvents(1);
    }

    protected class Arrivals implements EventGeneratorActionIfc {
        @Override
        public void generate(EventGenerator generator, JSLEvent event) {
            myEventCounter.increment();
            int n = (int)myNumArrivals.getValue();
            myArrivalCounter.increment(n);
            System.out.println(getTime() + " > In generate");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulation s = new Simulation("Poisson Process Example");
        EventGeneratorDemo pp = new EventGeneratorDemo(s.getModel());
        s.setLengthOfReplication(20.0);
        s.setNumberOfReplications(2);
        SimulationReporter r = s.makeSimulationReporter();
        s.run();
        r.printAcrossReplicationSummaryStatistics();
    }

}
