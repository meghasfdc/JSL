/*
 * Created on Sep 17, 2007
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
package ex.variables.nhpp;

import java.util.ArrayList;
import java.util.List;

import jsl.modeling.*;
import jsl.modeling.elements.EventGenerator;
import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.nhpp.*;
import jsl.modeling.SimulationReporter;
import jsl.modeling.elements.EventGeneratorActionIfc;

/**
 * @author rossetti
 *
 */
public class TestNHPPPWLinearRepeat extends ModelElement {

    protected NHPPEventGenerator myNHPPGenerator;

    protected EventListener myListener = new EventListener();

    protected List<Counter> myCountersFC;

    protected List<Counter> myCountersSC;

    protected PiecewiseRateFunction myPWRF;

    public TestNHPPPWLinearRepeat(ModelElement parent, PiecewiseRateFunction f) {
        this(parent, f, null);
    }

    public TestNHPPPWLinearRepeat(ModelElement parent, PiecewiseRateFunction f, String name) {
        super(parent, name);
        myNHPPGenerator = new NHPPEventGenerator(this, f, myListener);
        myPWRF = f;
        myCountersFC = new ArrayList<Counter>();
        int n = f.getNumberSegments();
        for (int i = 0; i < n; i++) {
            Counter c = new Counter(this, "Interval FC " + i);
            myCountersFC.add(c);
        }
        myCountersSC = new ArrayList<Counter>();
        for (int i = 0; i < n; i++) {
            Counter c = new Counter(this, "Interval SC " + i);
            myCountersSC.add(c);
        }


    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        // create the experiment to run the model
        Simulation s = new Simulation();

        PiecewiseRateFunction f = new PiecewiseLinearRateFunction(0.5, 200.0, 0.5);

        f.addRateSegment(400.0, 0.9);
        f.addRateSegment(400.0, 0.9);
        f.addRateSegment(200.0, 1.2);
        f.addRateSegment(300.0, 0.9);
        f.addRateSegment(500.0, 0.5);

        System.out.println("-----");
        System.out.println("intervals");
        System.out.println(f);

        new TestNHPPPWLinearRepeat(s.getModel(), f);

        // set the parameters of the experiment
        // set the parameters of the experiment
        s.setNumberOfReplications(1000);
        s.setLengthOfReplication(4000.0);

        // tell the simulation to run
        s.run();

        SimulationReporter r = new SimulationReporter(s);

        r.printAcrossReplicationSummaryStatistics();

    }

    protected class EventListener implements EventGeneratorActionIfc {

        public void generate(EventGenerator generator, JSLEvent event) {

            double t = getTime();

            if (t <= 2000.0) {
                //System.out.println("event at time: " + t);				
                int i = myPWRF.findTimeInterval(t);
                //System.out.println("occurs in interval: " + i);				
                myCountersFC.get(i).increment();
            } else {
                //System.out.println("event at time: " + t);				
                int i = myPWRF.findTimeInterval(t - 2000.0);
                //System.out.println("occurs in interval: " + i);	
                myCountersSC.get(i).increment();
            }

        }
    }
}
