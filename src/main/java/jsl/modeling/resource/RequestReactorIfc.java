/*
 * Copyright (c) 2018. Manuel D. Rossetti, rossetti@uark.edu
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
