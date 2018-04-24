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
package ex.station;

import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.SimulationReporter;
import jsl.modeling.elements.EventGenerator;
import jsl.modeling.queue.QObject;
import jsl.modeling.elements.station.NWayByChanceStationSender;
import jsl.modeling.elements.station.ReceiveQObjectIfc;
import jsl.modeling.elements.station.SingleQueueStation;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.elements.EventGeneratorActionIfc;

/**
 * Arriving customers choose randomly between three stations. The arrivals are
 * Poisson with mean rate 1.1. Thus, the time between arrivals is exponential
 * with mean 1/1.1. The first station is chosen with probability 0.4. The second
 * station is chosen with probability 0.3. The 3rd station with probability 0.3.
 * The service times of the stations are exponential with means 0.8, 0.7, 0.6,
 * respectively. After receiving service at the chosen station, the customer
 * leaves.
 *
 * @author rossetti
 */
public class ChooseBetweenThreeStations extends SchedulingElement {

    protected EventGenerator myArrivalGenerator;

    protected RandomVariable myTBA;

    protected SingleQueueStation myStation1;

    protected SingleQueueStation myStation2;

    protected SingleQueueStation myStation3;

    protected RandomVariable myST1;

    protected RandomVariable myST2;

    protected RandomVariable myST3;

    protected NWayByChanceStationSender myTwoWay;

    public ChooseBetweenThreeStations(ModelElement parent) {
        this(parent, null);
    }

    public ChooseBetweenThreeStations(ModelElement parent, String name) {
        super(parent, name);

        myTBA = new RandomVariable(this, new Exponential(1.0 / 1.1));

        myST1 = new RandomVariable(this, new Exponential(0.8));

        myST2 = new RandomVariable(this, new Exponential(0.7));

        myST3 = new RandomVariable(this, new Exponential(0.6));

        myArrivalGenerator = new EventGenerator(this, new Arrivals(), myTBA, myTBA);

        // Stations must have a sender or a receiver
        Dispose d = new Dispose();

        myStation1 = new SingleQueueStation(this, myST1, "Station1");
        myStation1.setNextReceiver(d);
        myStation2 = new SingleQueueStation(this, myST2, "Station2");
        myStation2.setNextReceiver(d);
        myStation3 = new SingleQueueStation(this, myST3, "Station3");
        myStation3.setNextReceiver(d);
        myTwoWay = new NWayByChanceStationSender(this);
        myTwoWay.add(myStation1, 0.4);
        myTwoWay.add(myStation2, 0.3);
        myTwoWay.addLast(myStation3);

    }

    protected class Arrivals implements EventGeneratorActionIfc {

        @Override
        public void generate(EventGenerator generator, JSLEvent event) {

            myTwoWay.receive(new QObject(getTime()));
        }

    }

    protected class Dispose implements ReceiveQObjectIfc {

        @Override
        public void receive(QObject qObj) {
            // do nothing
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulation s = new Simulation("Choose btw 3 Stations Example");

        new ChooseBetweenThreeStations(s.getModel());

        s.setNumberOfReplications(10);
        s.setLengthOfReplication(20000);
        s.setLengthOfWarmUp(5000);
        SimulationReporter r = s.makeSimulationReporter();

        s.run();

        r.printAcrossReplicationSummaryStatistics();
    }

}
