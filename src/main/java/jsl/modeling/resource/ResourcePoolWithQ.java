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

import java.util.List;
import java.util.Optional;
import jsl.modeling.ModelElement;
import jsl.modeling.queue.Queue;
import jsl.modeling.queue.QueueListenerIfc;
import jsl.modeling.queue.QueueResponse;
import jsl.utilities.reporting.JSL;

/**
 *
 * @author rossetti
 */
public class ResourcePoolWithQ extends ResourcePool implements SeizeableIfc {

    private final Queue<Request> myRequestQ;

    public ResourcePoolWithQ(ModelElement parent, List<ResourceUnit> units) {
        this(parent, units, true, false, null);
    }

    public ResourcePoolWithQ(ModelElement parent, List<ResourceUnit> units,
            String name) {
        this(parent, units, true, false, name);
    }

    public ResourcePoolWithQ(ModelElement parent, List<ResourceUnit> units,
            boolean poolStatOption, boolean requestQStatsOption) {
        this(parent, units, poolStatOption, requestQStatsOption, null);
    }

    public ResourcePoolWithQ(ModelElement parent, List<ResourceUnit> units,
            boolean poolStatOption, boolean requestQStatsOption, String name) {
        super(parent, units, poolStatOption, name);
        myRequestQ = new Queue<>(this, getName() + ":RequestQ",
                Queue.Discipline.FIFO, requestQStatsOption);
    }

    public final boolean isQueue(Queue queue){
        return myRequestQ == queue;
    }
    
    public final Optional<QueueResponse> getQueueResponses() {
        return myRequestQ.getQueueResponses();
    }

    public final boolean getQueueStatsOption() {
        return myRequestQ.getQueueStatsOption();
    }

    public final List<Request> getUnmodifiableListOfRequestQ() {
        return myRequestQ.getUnmodifiableList();
    }

    public final boolean addQueueListener(QueueListenerIfc<Request> listener) {
        return myRequestQ.addQueueListener(listener);
    }

    public boolean removeQueueListener(QueueListenerIfc<Request> listener) {
        return myRequestQ.removeQueueListener(listener);
    }

    public final Queue.Status getRequestQStatus() {
        return myRequestQ.getStatus();
    }

    public final void changeQDiscipline(Queue.Discipline discipline) {
        myRequestQ.changeDiscipline(discipline);
    }

    public final Queue.Discipline getCurrentQDiscipline() {
        return myRequestQ.getCurrentDiscipline();
    }

    public final void changePriority(Request qObject, int priority) {
        myRequestQ.changePriority(qObject, priority);
    }

    public final Queue.Discipline getInitialQDiscipline() {
        return myRequestQ.getInitialDiscipline();
    }

    public final void setInitialQDiscipline(Queue.Discipline discipline) {
        myRequestQ.setInitialDiscipline(discipline);
    }

    public final int requestQSize() {
        return myRequestQ.size();
    }

    public final boolean isRequestQEmpty() {
        return myRequestQ.isEmpty();
    }

    @Override
    public Request seize(Request request) {
        if (request == null) {
            throw new IllegalArgumentException("The seizing Request was null");
        }
        if (request.getPreemptionRule() == Request.PreemptionRule.NONE) {
            if (getFailureDelayOption() == false) {
                request.reject(getTime());
                return request;
            }
        }
        // not rejected, proceed with normal processing
        request.makeReady(getTime());
        myRequestQ.enqueue(request);
        request.enterWaitingState(myRequestQ, getTime());
        if (hasIdleUnits()) {
            myRequestQ.remove(request);
            request.exitWaitingState(myRequestQ, getTime());
            selectResourceUnit().seize(request);
        }
        return request;
    }

    @Override
    protected void unitBecameIdle(ResourceUnit ru) {
        if (myRequestQ.isNotEmpty()) {
            Request request = myRequestQ.removeNext();
            request.exitWaitingState(myRequestQ, getTime());
            ru.seize(request);
        }
    }

}
