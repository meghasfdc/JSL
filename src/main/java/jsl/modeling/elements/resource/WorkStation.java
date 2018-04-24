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
package jsl.modeling.elements.resource;

import jsl.modeling.EventActionIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.queue.Queue;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.distributions.Constant;

/**
 *
 * @author rossetti
 */
public class WorkStation extends EntityReceiver {

    protected Queue myQueue;

    protected Resource myResource;

    protected ResponseVariable mySystemTime;

    protected RandomVariable myServiceRV;

    protected EndServiceListener myEndServiceListener;

    public WorkStation(ModelElement parent) {
        this(parent, null, 1);
    }

    public WorkStation(ModelElement parent, String name) {
        this(parent, name, 1);
    }

    public WorkStation(ModelElement parent, String name, int numServers) {
        super(parent, name);
        setServiceTimeInitialRandomSource(Constant.ONE);
        myQueue = new Queue(this, getName() + "_Q");
        myResource = new Resource(this, numServers, getName() + "_R");
        mySystemTime = new ResponseVariable(this, getName() + "_SystemTime");
        myEndServiceListener = new EndServiceListener();
    }

    /**
     * 
     * @param d
     */
    public final void setServiceTimeInitialRandomSource(RandomIfc d) {

        if (d == null) {
            throw new IllegalArgumentException("Service Time Distribution was null!");
        }

        if (myServiceRV == null) { // not made yet
            myServiceRV = new RandomVariable(this, d);
        } else { // already had been made, and added to model
            // just change the distribution
            myServiceRV.setInitialRandomSource(d);
        }

    }

    @Override
    protected void receive(Entity entity) {
        // an entity is arriving to the workcenter
        // enqueue arriving customer

        myQueue.enqueue(entity);
        if (myResource.hasAvailableUnits()) {
            if (myQueue.peekNext() == entity) {
                myQueue.removeNext();
                myResource.allocate(entity);
                scheduleEvent(myEndServiceListener, myServiceRV, entity);
            }

        }
    }

    protected class EndServiceListener implements EventActionIfc {

        public void action(JSLEvent event) {

            // get the departing entity
            Entity departingEntity = (Entity)event.getMessage();

            // get the time in the work center
            double ws = getTime() - departingEntity.getTimeEnteredQueue();
            mySystemTime.setValue(ws);

            // tell the resource to release the request
            departingEntity.release(myResource);

            if (myQueue.isNotEmpty()) {
                Entity e = (Entity) myQueue.removeNext();
                myResource.allocate(e);
                scheduleEvent(myEndServiceListener, myServiceRV, e);
            }

            // send the entity to its next receiver
            sendEntity(departingEntity);

        }
    }
}
