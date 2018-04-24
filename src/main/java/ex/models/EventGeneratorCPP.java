/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * Copyright (c) Manuel D. Rossetti (rossetti@uark.edu)
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
package ex.models;

import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.SimulationReporter;
import jsl.modeling.elements.EventGenerator;
import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.distributions.DEmpiricalCDF;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.elements.EventGeneratorActionIfc;

/**
 * Arrivals are governed by a compound Poisson process. An EventGenerator is used
 *
 * @author rossetti
 */
public class EventGeneratorCPP extends SchedulingElement {

    protected EventGenerator myArrivalGenerator;
    protected Counter myEventCounter;
    protected Counter myArrivalCounter;
    protected RandomVariable myTBA;
    protected RandomVariable myNumArrivals;

    public EventGeneratorCPP(ModelElement parent) {
        this(parent, 1.0, null);
    }

    public EventGeneratorCPP(ModelElement parent, double tba, String name) {
        super(parent, name);
        double[] a = {1, 0.2, 2, 0.5, 3, 1.0};
        myNumArrivals = new RandomVariable(this, new DEmpiricalCDF(a));
        myTBA = new RandomVariable(this, new Exponential(tba));
        myEventCounter = new Counter(this, "Counts Events");
        myArrivalCounter = new Counter(this, "Counts Arrivals");
        myArrivalGenerator = new EventGenerator(this, new Arrivals(), myTBA, myTBA);
    }
    
    public void setInitialMaximumNumberOfEvents(long n){
        myArrivalGenerator.setInitialMaximumNumberOfEvents(n);
    }

    protected class Arrivals implements EventGeneratorActionIfc {
        @Override
        public void generate(EventGenerator generator, JSLEvent event) {
            myEventCounter.increment();
            int n = (int)myNumArrivals.getValue();
            myArrivalCounter.increment(n);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulation s = new Simulation("Poisson Process Example");
        EventGeneratorCPP pp = new EventGeneratorCPP(s.getModel());
        //pp.setInitialMaximumNumberOfEvents(2);
        s.setLengthOfReplication(20.0);
        s.setNumberOfReplications(50);
        SimulationReporter r = s.makeSimulationReporter();
        s.run();
        r.printAcrossReplicationSummaryStatistics();
    }

}
