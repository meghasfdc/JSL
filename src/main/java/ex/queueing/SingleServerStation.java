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

import java.util.Optional;
import jsl.modeling.EventActionIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.queue.*;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.observers.ObserverIfc;
import jsl.utilities.random.*;
import jsl.utilities.random.distributions.Constant;

/**
 */
public class SingleServerStation extends SchedulingElement {

    public final static int BUSY = 1;

    public final static int IDLE = 0;

    protected Queue myQueue;

    protected TimeWeighted myServerStatus;

    protected ResponseVariable mySystemTime;

    protected EndServiceListener myEndServiceListener;

    protected RandomIfc myServiceDistribution;

    protected RandomVariable myServiceRV;

    protected FastFoodRestaurant myFastFoodRestaurant;

    public SingleServerStation(ModelElement parent, FastFoodRestaurant restaurant) {
        this(parent, restaurant, null);
    }

    public SingleServerStation(ModelElement parent, FastFoodRestaurant restaurant, String name) {
        super(parent, name);
        setServiceDistributionInitialRandomSource(Constant.ONE);
        myFastFoodRestaurant = restaurant;
        myQueue = new Queue(this, getName() + " Queue");
        myServerStatus = new TimeWeighted(this, getName() + " Server");
        mySystemTime = new ResponseVariable(this, getName() + " System Time");
        myEndServiceListener = new EndServiceListener();
    }

    public final int getNumInQueue() {
        return (myQueue.size());
    }

    public final boolean isIdle() {
        return (myServerStatus.getValue() == IDLE);
    }

    protected final QObject removeLastCustomer() {
        QObject c = (QObject) myQueue.peekLast();
        myQueue.remove(c, false);
        return (c);
    }

    public final Optional<QueueResponse> getQueueResponses() {
        return myQueue.getQueueResponses();
    }

    public final void setServiceDistributionInitialRandomSource(RandomIfc d) {

        if (d == null) {
            throw new IllegalArgumentException("Service Time Distribution was null!");
        }

        myServiceDistribution = d;

        if (myServiceRV == null) { // not made yet
            myServiceRV = new RandomVariable(this, myServiceDistribution);
        } else { // already had been made, and added to model
            // just change the distribution
            myServiceRV.setInitialRandomSource(myServiceDistribution);
        }

    }

    protected void receive(QObject customer) {
        if (customer == null) {
            throw new IllegalArgumentException("The custoemr must be non-null!");
        }

        // enqueue arriving customer
        myQueue.enqueue(customer);
        if (isIdle()) {
            if (myQueue.peekNext() == customer) {
                myQueue.removeNext();
                myServerStatus.setValue(BUSY);
                scheduleEvent(myEndServiceListener, myServiceRV, customer);
            }
        }

    }

    protected class EndServiceListener implements EventActionIfc {

        public void action(JSLEvent event) {
            QObject e = (QObject) event.getMessage();
            // get the time
            double ws = getTime() - e.getTimeEnteredQueue();
            mySystemTime.setValue(ws);
            // tell the server to be released
            myServerStatus.setValue(IDLE);
            if (myQueue.isNotEmpty()) {
                QObject next = (QObject) myQueue.removeNext();
                myServerStatus.setValue(BUSY);
                scheduleEvent(myEndServiceListener, myServiceRV, next);
            }

            // customer is departing a station, check for jockey opportunity
            myFastFoodRestaurant.checkForJockey(SingleServerStation.this);
        }
    }
}
