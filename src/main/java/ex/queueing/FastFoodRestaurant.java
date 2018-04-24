/*
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
package ex.queueing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.elements.EventGenerator;
import jsl.modeling.queue.QObject;
import jsl.modeling.elements.variable.AveragePerTimeWeightedVariable;
import jsl.modeling.elements.variable.ResponseVariableAverageObserver;
import jsl.modeling.elements.variable.AggregateTimeWeightedVariable;
import jsl.utilities.random.distributions.DistributionIfc;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.SimulationReporter;
import jsl.modeling.elements.EventGeneratorActionIfc;
import jsl.modeling.queue.QueueResponse;

public class FastFoodRestaurant extends SchedulingElement {

    protected int myNumStations;

    protected List<SingleServerStation> myStations;

    protected EventGenerator myCustomerGenerator;

    protected CustomerArrival myArrivalListener = new CustomerArrival();

    public FastFoodRestaurant(ModelElement parent) {
        this(parent, 5, new Exponential(1.0), new Exponential(4.5), null);
    }

    public FastFoodRestaurant(ModelElement parent, int numStations) {
        this(parent, numStations, new Exponential(1.0), new Exponential(4.5), null);
    }

    public FastFoodRestaurant(ModelElement parent, int numStations, DistributionIfc timeBtwArrivals, DistributionIfc serviceTime, String name) {
        super(parent, name);

        if (numStations < 1) {
            throw new IllegalArgumentException("Number of stations was < 1");
        }
        // declare response observers to collect aggregate statistics on the queues
        AggregateTimeWeightedVariable s1 = new AggregateTimeWeightedVariable(this, "TW Total in Q");
        AveragePerTimeWeightedVariable s3 = new AveragePerTimeWeightedVariable(this, "AvgTW Total in Q");
        ResponseVariableAverageObserver s2 = new ResponseVariableAverageObserver(this, "Avg Waiting Time");
        myStations = new ArrayList<SingleServerStation>();
        myNumStations = numStations;
        for (int i = 1; i <= numStations; i++) {
            SingleServerStation t = new SingleServerStation(this, this);
            t.setServiceDistributionInitialRandomSource(serviceTime);
            Optional<QueueResponse> oqr = t.myQueue.getQueueResponses();
            if (oqr.isPresent()) {
                QueueResponse response = oqr.get();
                response.subscribe(s1);
                response.subscribe(s2);
                response.subscribe(s3);
            }
            myStations.add(t);
        }
        myCustomerGenerator = new EventGenerator(this, myArrivalListener, timeBtwArrivals, timeBtwArrivals);

    }

    protected void checkForJockey(SingleServerStation station) {
        // a customer is departing, opportunity for jockeying
        // get number in queue at the station the customer is departing from
        int n = station.getNumInQueue();
        // get the index of current station
        int i = myStations.indexOf(station);
        int min = Integer.MAX_VALUE;
        // find the stations to jockey from
        SingleServerStation fStation = null;
        for (SingleServerStation t : myStations) {
            if (t != station) {
                if (t.getNumInQueue() > n + 1) {
                    // station t has more in queue than station
                    // get distance from current station
                    int d = Math.abs(i - myStations.indexOf(t));
                    if (d < min) {
                        fStation = t;
                        min = d;
                    }
                }
            }
        }
        if (fStation != null) { // a station to jockey from was found
            // remove the last customer, w/o collecting statistics
            QObject jockeyingCustomer = fStation.removeLastCustomer();
            // tell the station to process this customer
            station.receive(jockeyingCustomer);
        }
    }

    private SingleServerStation selectStation() {
        // check if there is an idle station, then use it
        for (SingleServerStation t : myStations) {
            if (t.isIdle()) {
                return (t);
            }
        }
        // no idle stations, then pick station with shortest queue
        SingleServerStation shortest = null;
        int min = Integer.MAX_VALUE;
        for (SingleServerStation t : myStations) {
            int n = t.getNumInQueue();
            if (n < min) {
                shortest = t;
                min = n;
            }
        }
        return (shortest);
    }

    protected class CustomerArrival implements EventGeneratorActionIfc {

        @Override
        public void generate(EventGenerator generator, JSLEvent event) {
            QObject customer = new QObject(getTime());
            SingleServerStation t = selectStation();
            t.receive(customer);
        }
    }

    public static void main(String[] args) {
        System.out.println("Fast Food Line Jockeying Example");

        // create the simulation for the model and experiment
        Simulation s = new Simulation("FastFoodJockeying");

        // create the model element and attach it to the main model
        new FastFoodRestaurant(s.getModel(), 5);

        // set the parameters of the experiment
        s.setNumberOfReplications(1000);
        s.setLengthOfReplication(240.0);

        // tell the simulation to run
        s.run();
        SimulationReporter r = new SimulationReporter(s);
        r.printAcrossReplicationSummaryStatistics();
        System.out.println("Done!");
    }
}
