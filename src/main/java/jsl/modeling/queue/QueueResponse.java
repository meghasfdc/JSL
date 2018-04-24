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
package jsl.modeling.queue;

import jsl.modeling.ModelElement;
import jsl.modeling.elements.variable.Aggregate;
import jsl.modeling.elements.variable.AggregateTimeWeightedVariable;
import jsl.modeling.elements.variable.AveragePerTimeWeightedVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.ResponseVariableAverageObserver;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.observers.ObserverIfc;
import jsl.utilities.statistic.StatisticAccessorIfc;
import jsl.utilities.statistic.WeightedStatisticIfc;

/**
 *
 * @author rossetti
 */
public class QueueResponse extends ModelElement implements QueueListenerIfc {

    private final Queue myQueue;

    /**
     * Tracks the number in queue.
     */
    private final TimeWeighted myNumInQ;

    /**
     * Tracks the time in queue.
     */
    private final ResponseVariable myTimeInQ;

    QueueResponse(Queue parent) {
        this(parent, null);
    }

    QueueResponse(Queue parent, String name) {
        super(parent, name);
        myQueue = parent;
        myNumInQ = new TimeWeighted(this, 0.0, myQueue.getName() + ":NumInQ");
        myTimeInQ = new ResponseVariable(this, myQueue.getName() + ":TimeInQ");
    }

    @Override
    public void update(QObject qObject) {
        switch (myQueue.getStatus()) {
            case ENQUEUED:
                myNumInQ.setValue(myQueue.size());
                break;
            case DEQUEUED:
                myNumInQ.setValue(myQueue.size());
                double wTime = qObject.getTimeExitedQueue() - qObject.getTimeEnteredQueue();
                myTimeInQ.setValue(wTime);
//                myTimeInQ.setValue(qObject.getTimeInQueue());
                break;
            case IGNORE:
                myNumInQ.setValue(myQueue.size());
                break;
            default:
                throw new IllegalStateException("Queue Status was not ENQUEUED, IGNORE, or DEQUEUED");
        }
    }

    /**
     * Turns on the tracing to a text file of the times in queue.
     */
    public final void turnOnTimeInQTrace() {
        myTimeInQ.turnOnTrace();
    }

    /**
     * Turns on the tracing to a text file of the times in queue.
     *
     * @param header the header
     */
    public final void turnOnTimeInQTrace(boolean header) {
        myTimeInQ.turnOnTrace(header);
    }

    /**
     * Turns on the tracing to a text file of the times in queue.
     *
     * @param fileName the file name
     */
    public final void turnOnTimeInQTrace(String fileName) {
        myTimeInQ.turnOnTrace(fileName);
    }

    /**
     * Turns on the tracing to a text file of the times in queue.
     *
     * @param fileName the file name
     * @param header the header
     */
    public final void turnOnTimeInQTrace(String fileName, boolean header) {
        myTimeInQ.turnOnTrace(fileName, header);
    }

    /**
     * Turns on the tracing to a text file the number in queue for each state
     * change.
     */
    public final void turnOnNumberInQTrace() {
        myNumInQ.turnOnTrace();
    }

    /**
     * Turns on the tracing to a text file the number in queue for each state
     * change.
     *
     * @param header the header
     */
    public final void turnOnNumberInQTrace(boolean header) {
        myNumInQ.turnOnTrace(header);
    }

    /**
     * Turns on the tracing to a text file the number in queue for each state
     * change.
     *
     * @param fileName the file name
     */
    public final void turnOnNumberInQTrace(String fileName) {
        myNumInQ.turnOnTrace(fileName);
    }

    /**
     * Turns on the tracing to a text file the number in queue for each state
     * change.
     *
     * @param fileName the file name
     * @param header the header
     */
    public final void turnOnNumberInQTrace(String fileName, boolean header) {
        myNumInQ.turnOnTrace(fileName, header);
    }

    /**
     * Turns off the tracing of the times in queue.
     */
    public final void turnOffTimeInQTrace() {
        myTimeInQ.turnOffTrace();
    }

    /**
     * Turns off the tracing of the number in queue.
     */
    public final void turnOffNumberInQTrace() {
        myNumInQ.turnOffTrace();
    }

    /**
     * Get the number in queue across replication statistics
     *
     * @return the statistic
     */
    public final StatisticAccessorIfc getNumInQAcrossReplicationStatistic() {
        return myNumInQ.getAcrossReplicationStatistic();
    }

    /**
     * Get the time in queue across replication statistics
     *
     * @return the statistic
     */
    public final StatisticAccessorIfc getTimeInQAcrossReplicationStatistic() {
        return myTimeInQ.getAcrossReplicationStatistic();
    }

    /**
     * Within replication statistics for time in queue
     *
     * @return Within replication statistics for time in queue
     */
    public final WeightedStatisticIfc getTimeInQWithinReplicationStatistic() {
        return myTimeInQ.getWithinReplicationStatistic();
    }

    /**
     * Within replication statistics for number in queue
     *
     * @return the within replication statistics for number in queue
     */
    public final WeightedStatisticIfc getNumInQWithinReplicationStatistic() {
        return myNumInQ.getWithinReplicationStatistic();
    }

    /**
     * Allows access to across interval response for number in queue if turned
     * on
     *
     * @return the across interval response
     */
    public final ResponseVariable getNumInQAcrossIntervalResponse() {
        return myNumInQ.getAcrossIntervalResponse();
    }

    /**
     * Allows access to across interval response for time in queue if turned on
     *
     * @return the across interval response
     */
    public final ResponseVariable getTimeInQAcrossIntervalResponse() {
        return myTimeInQ.getAcrossIntervalResponse();
    }

    /**
     * Allows for the collection of across replication statistics on the average
     * maximum time spent in queue
     * <p>
     */
    public final void turnOnAcrossReplicationMaxTimeInQueueCollection() {
        myTimeInQ.turnOnAcrossReplicationMaxCollection();
    }

    /**
     * Allows for the collection of across replication statistics on the average
     * maximum number in queue
     * <p>
     */
    public final void turnOnAcrossReplicationMaxNumInQueueCollection() {
        myNumInQ.turnOnAcrossReplicationMaxCollection();
    }

    /**
     * A convenience method to turn on collection of both the maximum time in
     * queue and the maximum number in queue
     * <p>
     */
    public final void turnOnAcrossReplicationMaxCollection() {
        turnOnAcrossReplicationMaxTimeInQueueCollection();
        turnOnAcrossReplicationMaxNumInQueueCollection();
    }

    /**
     * Allows an observer to be attached to the time in queue response variable
     *
     * @param observer the observer
     */
    public final void addTimeInQueueObserver(ObserverIfc observer) {
        myTimeInQ.addObserver(observer);
    }

    /**
     * Allows an observer to be removed from the time in queue response variable
     *
     * @param observer the observer
     */
    public final void removeTimeInQueueObserver(ObserverIfc observer) {
        myTimeInQ.deleteObserver(observer);
    }

    /**
     * Allows an observer to be attached to the number in queue time weighted
     * variable
     *
     * @param observer the observer
     */
    public final void addNumberInQueueObserver(ObserverIfc observer) {
        myNumInQ.addObserver(observer);
    }

    /**
     * Allows an observer to be removed from the number in queue time weighted
     * variable
     *
     * @param observer the observer
     */
    public final void removeNumberInQueueObserver(ObserverIfc observer) {
        myNumInQ.deleteObserver(observer);
    }

    /**
     * Causes the supplied AggregateTimeWeightedVariable to
     * be subscribed to the number in queue variable
     *
     * @param aggregate the aggregate
     */
    public void subscribe(AggregateTimeWeightedVariable aggregate) {
        aggregate.subscribeTo(myNumInQ);
    }

    /**
     * Causes the supplied AggregateTimeWeightedVariable to
     * be unsubscribed from the number in queue variable
     *
     * @param aggregate the aggregate
     */
    public void unsubscribe(AggregateTimeWeightedVariable aggregate) {
        aggregate.unsubscribeFrom(myNumInQ);
    }

    /**
     * Causes the supplied AveragePerTimeWeightedVariable to
     * be subscribed to the number in queue variable
     *
     * @param aggregate the aggregate
     */
    public void subscribe(AveragePerTimeWeightedVariable aggregate) {
        aggregate.subscribeTo(myNumInQ);
    }

    /**
     * Causes the supplied AveragePerTimeWeightedVariable to
     * be unsubscribed from the number in queue variable
     *
     * @param aggregate the aggregate
     */
    public void unsubscribe(AveragePerTimeWeightedVariable aggregate) {
        aggregate.unsubscribeFrom(myNumInQ);
    }

    /**
     * Causes the supplied ResponseVariableAverageObserver to
     * be subscribed to the time in queue variable
     *
     * @param aggregate the aggregate
     */
    public void subscribe(ResponseVariableAverageObserver aggregate) {
        aggregate.subscribeTo(myTimeInQ);
    }

    /**
     * Causes the supplied ResponseVariableAverageObserver to
     * be unsubscribed from the time in queue variable
     *
     * @param aggregate the aggregate
     */
    public void unsubscribe(ResponseVariableAverageObserver aggregate) {
        aggregate.unsubscribeFrom(myTimeInQ);
    }

    /**
     * Allows an Aggregate to subscribe to the time in queue variable
     *
     * @param aggregate the aggregate
     */
    public final void subscribeToTimeInQueue(Aggregate aggregate) {
        aggregate.subscribeTo(myTimeInQ);
    }

    /**
     * Allows an Aggregate to unsubscribe from the time in queue variable
     *
     * @param aggregate the aggregate
     */
    public final void unsubscribeFromTimeInQueue(Aggregate aggregate) {
        aggregate.unsubscribeFrom(myTimeInQ);
    }

    /**
     * Allows an Aggregate to subscribe to the number in queue variable
     *
     * @param aggregate the aggregate
     */
    public final void subscribeToNumberInQueue(Aggregate aggregate) {
        aggregate.subscribeTo(myNumInQ);
    }

    /**
     * Allows an Aggregate to unsubscribe from the number in queue variable
     *
     * @param aggregate the aggregate
     */
    public final void unsubscribeFromNumberInQueue(Aggregate aggregate) {
        aggregate.unsubscribeFrom(myNumInQ);
    }

}
