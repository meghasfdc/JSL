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
package jsl.modeling.elements.processview.description.commands;

import jsl.modeling.ModelElement;
import jsl.modeling.elements.processview.description.ProcessCommand;

import jsl.modeling.elements.processview.description.ProcessExecutor;
import jsl.modeling.elements.resource.*;
import jsl.modeling.elements.variable.*;
import jsl.modeling.queue.Queue;

/**
 *
 */
public class Seize extends ProcessCommand {

    private Variable myAmtRequested;

    private Resource myResource;

    private int myPriority;

    private Queue myQueue;

    private final AllocationListener myAllocationListener;

    public Seize(ModelElement parent, Variable amountRequested, Resource resource, Queue queue) {
        this(parent, amountRequested, resource, queue, 1, null);
    }

    public Seize(ModelElement parent, Variable amountRequested, Resource resource, Queue queue, int priority) {
        this(parent, amountRequested, resource, queue, priority, null);
    }

    public Seize(ModelElement parent, Variable amountRequested, Resource resource, Queue queue, String name) {
        this(parent, amountRequested, resource, queue, 1, name);
    }

    public Seize(ModelElement parent, Variable amountRequested, Resource resource, Queue queue, int priority, String name) {
        super(parent, name);
        myAllocationListener = new AllocationListener();
        setAmountRequested(amountRequested);
        setResource(resource);
        setQueue(queue);
        setPriority(priority);
    }

    /** Gets the queuing priority associated with this QObject
     * @return The priority as an integer
     */
    public final int getPriority() {
        return (myPriority);
    }

    /** Returns the queue that the QObject was last enqueued within
     *  
     * @return The Queue, or null if no queue
     */
    public final Queue getQueue() {
        return (myQueue);
    }

    @Override
    public void execute() {

        int amt = (int) myAmtRequested.getValue();
        Entity entity = getProcessExecutor().getCurrentEntity();
        
        // enqueue arriving entity
        myQueue.enqueue(entity);

        myResource.seize(entity, amt, myPriority, myAllocationListener);

        // check if entity was queued
        if (entity.isQueued()) { // suspend the executor at this seize command, otherwise continue
            getProcessExecutor().suspend();
        }
    }

    class AllocationListener implements AllocationListenerIfc {

        public void allocated(Request request) {
            if (request.isSatisfied()) {
                Entity entity = request.getEntity();
                myQueue.remove(entity);
                // get the entity's process executor
                ProcessExecutor pe = entity.getProcessExecutor();
                if (pe.isSuspended()) {
                    // schedule the entity's process executor to resume, now
                    scheduleResume(pe, 0.0, 1, "Resume Seize");
                }
            }
        }
    }

    /** Sets the priority for this Seize
     *  Changing the priority while the object is in a queue
     *  has no effect on the ordering of the queue.  This priority is
     *  only used to determine competition between multiple seizes
     *  of the same resource
     * 
     * @param priority lower priority implies earlier ranking in the queue
     */
    protected final void setPriority(int priority) {
        myPriority = priority;
    }

    protected final void setAmountRequested(Variable amountRequested) {

        if (amountRequested == null) {
            throw new IllegalArgumentException("Variable amountRequested was equal to null!");
        }

        if (amountRequested.getInitialValue() <= 0) {
            throw new IllegalArgumentException("Amount requested was less or equal to zero!");
        }

        myAmtRequested = amountRequested;
    }

    protected final void setResource(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource was equal to null!");
        }
        myResource = resource;
    }

    protected final void setQueue(Queue queue) {
        if (queue == null) {
            throw new IllegalArgumentException("Queue was equal to null!");
        }
        myQueue = queue;
    }
}
