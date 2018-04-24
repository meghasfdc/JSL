/*
 *  Copyright (C) 2017 rossetti
 * 
 *  Contact:
 * 	Manuel D. Rossetti, Ph.D., P.E.
 * 	Department of Industrial Engineering
 * 	University of Arkansas
 * 	4207 Bell Engineering Center
 * 	Fayetteville, AR 72701
 * 	Phone: (479) 575-6756
 * 	Email: rossetti@uark.edu
 * 	Web: www.uark.edu/~rossetti
 * 
 *  This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 *  of Java classes that permit the easy development and execution of discrete event
 *  simulation programs.
 * 
 *  The JSL is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  The JSL is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jsl.modeling.resource;

import jsl.modeling.queue.Queue;

/**
 * This interface essentially provides a mechanism for ResourceUnits
 * to communicate with the user of the resource.
 * Waiting = waiting for resource, can only transition to Using or Canceled
 * Rejected = rejected after creation, no further transitions
 * Canceled = canceled after allocation, no further transitions
 * Using = using the resource, may preempt, cancel, or complete
 * Preempted = using the resource but preempted, can resume or cancel
 * Completed = completed its life-cycle, no further transitions
 * Whenever a Request is sent to a ResourceUnit, as it changes state
 * these interaction methods are called. This gives objects that
 * submit requests to resources to react if needed to these changes
 * in state for the associated request. For example, if a request is
 * preempted, there may be additional actions that the sender of the
 * request might want to do that is not handled in the standard
 * logic associated with the ResourceUnit. Also, the sender of the
 * request can respond upon completion of the request and then perform
 * other activities.
 *
 * @author rossetti
 */
public interface RequestReactorIfc {

    /**
     * Called when the request is placed in the ready state. The
     * request is now prepared for usage.
     *
     * @param request the request
     */
    void prepared(Request request);

    /**
     * Called when the request is placed in the waiting state
     *
     * @param request the request
     * @param queue the queue that was entered
     */
    void enqueued(Request request, Queue<Request> queue);

    /**
     * Called when the request is dequeued from the waiting state
     *
     * @param request the request
     * @param queue the queue that was exited
     */
    void dequeued(Request request, Queue<Request> queue);

    /**
     * Called when the request is placed in the rejected state
     *
     * @param request the request
     */
    void rejected(Request request);

    /**
     * Called when the request is placed in the canceled state
     *
     * @param request the request
     */
    void canceled(Request request);

    /**
     * Called when the request is placed in the preempted state
     *
     * @param request the request
     */
    void preempted(Request request);

    /** Called when the request is resumed from the preempted state
     * 
     * @param request the request
     */
    void resumed(Request request);

    /**
     * Called when the request is placed in the using state
     *
     * @param request the request
     */
    void allocated(Request request);

    /**
     * Called when the request is placed in the completed state
     *
     * @param request the request
     */
    void completed(Request request);

}
