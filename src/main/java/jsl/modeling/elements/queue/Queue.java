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
package jsl.modeling.elements.queue;

import java.util.*;
import java.util.function.Predicate;

import jsl.modeling.elements.variable.Aggregate;
import jsl.modeling.elements.variable.AggregateTimeWeightedVariable;
import jsl.modeling.elements.variable.AveragePerTimeWeightedVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.ResponseVariableAverageObserver;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.modeling.ModelElement;
import jsl.observers.ObserverIfc;
import jsl.utilities.statistic.StatisticAccessorIfc;
import jsl.utilities.statistic.WeightedStatisticIfc;

/** The Queue class provides the ability to hold entities (QObjects)
 * within the model.  Any object can be added to a Queue.  When
 * an object is added to a Queue, the object is wrapped by a
 * QObject which provides statistical collection.  In this way,
 * objects that queue do not need additional behavior
 */
@Deprecated
public class Queue extends ModelElement implements Iterable<QObject> {

    /** ENQUEUED indicates that something was just enqueued
     *  DEQUEUED indicates that something was just dequeued
     *
     */
    public static enum Status {

        ENQUEUED, DEQUEUED
    };

    /** The list of items in the queue.
     */
    protected List<QObject> myList;

    /** The current QueueDiscipline for this Queue.
     */
    protected QueueDiscipline myDiscipline;

    /** The initial QueueDiscipline for this Queue.
     */
    protected QueueDiscipline myInitialDiscipline;

    /** Tracks the number in queue.
     */
    protected TimeWeighted myNumInQ;

    /** Tracks the time in queue.
     */
    protected ResponseVariable myTimeInQ;

    /** Allows a 1-1 mapping between QObjects and the objects entered into the queue
     */
    protected Map<Object, QObject> myQObjectMap;

    /** turns on map association between QObjects and the objects entered in the queue
     */
    private boolean myMapTrackingFlag;

    /** Holds the listeners for this queue's enqueue and removeNext method use
     */
    protected List<QueueListenerIfc> myQueueListeners;

    /** Indicates whether something was just enqueued or dequeued
     */
    protected Status myStatus;

    /** Can be used to collect max time spent in queue
     *  across replications
     * 
     */
    protected ResponseVariable myMaxTimeInQ;

    /** Can be used to collect max number in queue
     *  across replications
     * 
     */
    protected ResponseVariable myMaxNumInQ;

    /** Constructs a Queue.  The default will be a FIFO queue
     * @param parent its parent
     */
    public Queue(ModelElement parent) {
        this(parent, null, null, false);
    }

    /** Constructs a Queue with the given name.  The queue
     * will be a FIFO by default.
     * @param parent its parent
     * @param name The name of the queue
     */
    public Queue(ModelElement parent, String name) {
        this(parent, name, null, false);
    }

    /** Constructs a Queue that follows the
     * given queue discipline.
     * @param parent its parent
     * @param discipline The queuing discipline to be followed
     */
    public Queue(ModelElement parent, QueueDiscipline discipline) {
        this(parent, null, discipline, false);
    }

    /** Constructs a Queue with the given name that follows the
     * given queue discipline.
     * @param parent its parent
     * @param name The name of the queue
     * @param discipline The queuing discipline to be followed
     */
    public Queue(ModelElement parent, String name, QueueDiscipline discipline) {
        this(parent, name, discipline, false);
    }

    /** Constructs a Queue with the given name that follows the
     * given queue discipline.
     * @param parent its parent
     * @param name The name of the queue
     * @param discipline The queuing discipline to be followed
     * @param mapTrackingFlag true turns on tracking
     */
    public Queue(ModelElement parent, String name, QueueDiscipline discipline, boolean mapTrackingFlag) {
        super(parent, name);
        myList = new LinkedList<QObject>();
        setEntityMapTrackingOption(mapTrackingFlag);

        if (discipline == null) {
            discipline = new FIFODiscipline(this);
        }

        changeDiscipline(discipline);
        myInitialDiscipline = myDiscipline;

        myNumInQ = new TimeWeighted(this, 0.0, getName() + ":Num In Q");
        myTimeInQ = new ResponseVariable(this, getName() + ":Time In Q");

    }

    
    /** Allows for the collection of across replication statistics
     *  on the average maximum time spent in queue
     * 
     */
    public final void turnOnAcrossReplicationMaxTimeInQueueCollection() {
        if (myMaxTimeInQ == null) {
            myMaxTimeInQ = new ResponseVariable(this, getName() + " : Max Time In Q");
        }
    }

    /** Allows for the collection of across replication statistics
     *  on the average maximum number in queue
     * 
     */
    public final void turnOnAcrossReplicationMaxNumInQueueCollection() {
        if (myMaxNumInQ == null) {
            myMaxNumInQ = new ResponseVariable(this, getName() + " : Max Num In Q");
        }
    }

    /** A convenience method to turn on collection of both the maximum
     *  time in queue and the maximum number in queue
     * 
     */
    public final void turnOnAcrossReplicationMaxCollection(){
        turnOnAcrossReplicationMaxTimeInQueueCollection();
        turnOnAcrossReplicationMaxNumInQueueCollection();
    }
    
    @Override
    protected void replicationEnded() {
        if (myMaxTimeInQ != null) {
            double maxT = myTimeInQ.getWithinReplicationStatistic().getMax();
            myMaxTimeInQ.setValue(maxT);
        }

        if (myMaxNumInQ != null) {
            double maxN = myNumInQ.getWithinReplicationStatistic().getMax();
            myMaxNumInQ.setValue(maxN);
        }
    }

    /** can be called to initialize the queue
     * The default behavior is to have the queue cleared 
     *
     **/
    @Override
    protected void initialize() {
        super.initialize();
        if (myDiscipline != myInitialDiscipline) {
            changeDiscipline(myInitialDiscipline);
        }

    }

    @Override
    protected void afterReplication() {
        super.afterReplication();
        clear();
    }

    @Override
    protected void removedFromModel() {
        super.removedFromModel();
        myList.clear();
        myList = null;
        myDiscipline = null;
        myInitialDiscipline = null;
        myNumInQ = null;
        myTimeInQ = null;
        if (myQObjectMap != null) {
            myQObjectMap.clear();
        }
        myQObjectMap = null;
        if (myQueueListeners != null) {
            myQueueListeners.clear();
        }
        myQueueListeners = null;
        myStatus = null;
        myMaxNumInQ = null;
        myMaxTimeInQ = null;
    }

    /** Adds the supplied listener to this queue
     * @param listener Must not be null, cannot already be added
     * @return true if added
     */
    public final boolean addQueueListener(QueueListenerIfc listener) {
        if (listener == null) {
            throw new IllegalArgumentException("The listener was null.");
        }

        if (myQueueListeners == null) {
            myQueueListeners = new ArrayList<QueueListenerIfc>();
        }

        if (myQueueListeners.contains(listener)) {
            throw new IllegalArgumentException("The queue already has the supplied listener.");
        }

        return myQueueListeners.add(listener);
    }

    /** Removes the supplied listener from this queue
     * @param listener Must not be null
     * @return true if removed
     */
    public boolean removeQueueListener(QueueListenerIfc listener) {
        if (listener == null) {
            throw new IllegalArgumentException("The listener was null.");
        }
        if (myQueueListeners == null) {
            return (false);
        }

        return myQueueListeners.remove(listener);
    }

    /** Allows an observer to be attached to the time in queue response variable
     *
     * @param observer the observer
     */
    public final void addTimeInQueueObserver(ObserverIfc observer) {
        myTimeInQ.addObserver(observer);
    }

    /** Allows an observer to be removed from the time in queue response variable
     *
     * @param observer the observer
     */
    public final void removeTimeInQueueObserver(ObserverIfc observer) {
        myTimeInQ.deleteObserver(observer);
    }

    /** Allows an observer to be attached to the number in queue time weighted variable
     *
     * @param observer the observer
     */
    public final void addNumberInQueueObserver(ObserverIfc observer) {
        myNumInQ.addObserver(observer);
    }

    /** Allows an observer to be removed from the number in queue time weighted variable
     *
     * @param observer the observer
     */
    public final void removeNumberInQueueObserver(ObserverIfc observer) {
        myNumInQ.deleteObserver(observer);
    }

    /** Causes the supplied AggregateTimeWeightedVariable to
     *  be subscribed to the number in queue variable
     *
     * @param aggregate the aggregate
     */
    public void subscribe(AggregateTimeWeightedVariable aggregate) {
        aggregate.subscribeTo(myNumInQ);
    }

    /** Causes the supplied AggregateTimeWeightedVariable to
     *  be unsubscribed from the number in queue variable
     *
     * @param aggregate the aggregate
     */
    public void unsubscribe(AggregateTimeWeightedVariable aggregate) {
        aggregate.unsubscribeFrom(myNumInQ);
    }

    /** Causes the supplied AveragePerTimeWeightedVariable to
     *  be subscribed to the number in queue variable
     *
     * @param aggregate the aggregate
     */
    public void subscribe(AveragePerTimeWeightedVariable aggregate) {
        aggregate.subscribeTo(myNumInQ);
    }

    /** Causes the supplied AveragePerTimeWeightedVariable to
     *  be unsubscribed from the number in queue variable
     *
     * @param aggregate the aggregate
     */
    public void unsubscribe(AveragePerTimeWeightedVariable aggregate) {
        aggregate.unsubscribeFrom(myNumInQ);
    }

    /** Causes the supplied ResponseVariableAverageObserver to
     *  be subscribed to the time in queue variable
     *
     * @param aggregate the aggregate
     */
    public void subscribe(ResponseVariableAverageObserver aggregate) {
        aggregate.subscribeTo(myTimeInQ);
    }

    /** Causes the supplied ResponseVariableAverageObserver to
     *  be unsubscribed from the time in queue variable
     *
     * @param aggregate the aggregate
     */
    public void unsubscribe(ResponseVariableAverageObserver aggregate) {
        aggregate.unsubscribeFrom(myTimeInQ);
    }

    /** Allows an Aggregate to subscribe to the time in queue variable
     *
     * @param aggregate the aggregate
     */
    public final void subscribeToTimeInQueue(Aggregate aggregate) {
        aggregate.subscribeTo(myTimeInQ);
    }

    /** Allows an Aggregate to unsubscribe from the time in queue variable
     *
     * @param aggregate the aggregate
     */
    public final void unsubscribeFromTimeInQueue(Aggregate aggregate) {
        aggregate.unsubscribeFrom(myTimeInQ);
    }

    /** Allows an Aggregate to subscribe to the number in queue variable
     *
     * @param aggregate the aggregate
     */
    public final void subscribeToNumberInQueue(Aggregate aggregate) {
        aggregate.subscribeTo(myNumInQ);
    }

    /** Allows an Aggregate to unsubscribe from the number in queue variable
     *
     * @param aggregate the aggregate
     */
    public final void unsubscribeFromNumberInQueue(Aggregate aggregate) {
        aggregate.unsubscribeFrom(myNumInQ);
    }

    /** Gets whether or not the last action was enqueue or dequeueing an object
     *
     * @return the status
     */
    public final Status getStatus() {
        return myStatus;
    }

    /** Sets the queue's discipline to the given discipline. Throws
     * an IllegalArgumentException if the discipline is null.
     * @param discipline An interface to a queue discipline
     */
    public final void changeDiscipline(QueueDiscipline discipline) {
        if (discipline == null) {
            throw new IllegalArgumentException("The discipline must be non-null");
        }

        discipline.switchFrom(myList, myDiscipline);

        myDiscipline = discipline;
    }

    /** Changes the priority of the supplied QObject. May cause the queue
     *  to reorder using its discipline
     * 
     * @param qObject the thing to change the priority on
     * @param priority the new priority
     */
    public final void changePriority(QObject qObject, int priority) {
        myDiscipline.changePriority(myList, qObject, priority);
    }

    /** Gets the initial queue discipline
     * 
     * @return the initial queue discipline
     */
    public final QueueDiscipline getInitialDiscipline() {
        return myInitialDiscipline;
    }

    /** Sets the initial queue discipline
     *
     * @param discipline the discipline
     */
    public final void setInitialDiscipline(QueueDiscipline discipline) {
        if (discipline == null) {
            throw new IllegalArgumentException("The QueueDisciplineIfct must be non-null");
        }
        myInitialDiscipline = discipline;
    }

    /** Creates and enqueue's a QObject with default priority 1
     * 
     * @return The created QObject
     */
    public final QObject enqueue() {
        return (enqueue(1));
    }

    /** Creates and enqueue's a QObject with the given priority
     * 
     * @param priority the priority of the item
     * @return The created QObject
     */
    public final QObject enqueue(int priority) {
        QObject qObj = new QObject(getTime());
        enqueue(qObj, priority, null);
        return (qObj);
    }

    /** Places the object in the queue, with the default priority of 1
     * Returns a reference to the QObject that wraps the supplied Object
     *
     *  Automatically, updates the number in queue response variable.
     * @param obj - the object to enqueue
     * @return a reference to the QObject object that wraps the item
     */
    public final QObject enqueue(Object obj) {
        return (enqueue(obj, 1));
    }

    /** Places the object in the queue, with the specified priority
     * Returns a reference to the QObject that wraps the supplied Object
     *
     *  Automatically, updates the number in queue response variable.
     *
     * @param obj - the object to enqueue
     * @param priority - the priority for ordering the object, lower has more priority
     * @return a reference to the QObject object that wraps the enqueued item
     */
    public final QObject enqueue(Object obj, int priority) {
        QObject qObj = new QObject(getTime());
        enqueue(qObj, priority, obj);
        return (qObj);
    }

    /** Places the QObject in the queue, with the default priority of 1
     *  Automatically, updates the number in queue response variable.
     *
     * @param queueingObject  the QObject to enqueue
     */
    public final void enqueue(QObject queueingObject) {
        enqueue(queueingObject, 1, queueingObject.getObject());
    }

    /** Places the QObject in the queue, with the default priority of 1
     *  Automatically, updates the number in queue response variable.
     *
     * @param queueingObject  the QObject to enqueue
     * @param priority  the priority for ordering the object, lower has more priority
     */
    public final void enqueue(QObject queueingObject, int priority) {
        enqueue(queueingObject, priority, queueingObject.getObject());
    }

    /** Places the QObject in the queue, with the specified priority
     *  Automatically, updates the number in queue response variable.
     *
     * @param qObject - the QObject to enqueue
     * @param priority - the priority for ordering the object, lower has more priority
     * @param obj an Object to be "wrapped" and queued while the QObject is queued
     */
    public void enqueue(QObject qObject, int priority, Object obj) {
        if (qObject == null) {
            throw new IllegalArgumentException("The QObject must be non-null");
        }
        double t = getTime();

        qObject.enterQueue(this, t, priority, obj);

        myDiscipline.add(myList, qObject);
        if (myMapTrackingFlag == true) {
            if (obj == null) {
                throw new IllegalArgumentException("Attached object was null and map tracking was on.!");
            }
            myQObjectMap.put(obj, qObject);
        }
        myNumInQ.setValue(myList.size());
        myStatus = Status.ENQUEUED;
        notifyQueueListeners(qObject);
    }

    /** Places all the objects in the specified collection in the queue
     *  using the queue's discipline.  Throws an IllegalArgumentException if the Collection is null
     *
     *  Automatically, updates the number in queue response variable.
     *
     *  @param c the Collection c of items to check
     *
     */
    public final void enqueueAll(Collection<?> c) {
        if (c == null) {
            throw new IllegalArgumentException("The Collection c must be non-null");
        }

        for (Iterator<?> i = c.iterator(); i.hasNext();) {
            enqueue(i.next());
        }
    }

    /** Returns a reference to the QObject representing
     * the item that is next to be removed from the queue according
     * to the queue discipline that was specified.
     *
     * @return a reference to the QObject object that wraps the next item to be removed, or null if the queue is empty
     */
    public final QObject peekNext() {
        return (myDiscipline.peekNext(myList));
    }

    /** Removes the next item from the queue according to the queue discipline
     * that was specified.  Returns a reference to the QObject representing
     * the item that was removed
     *
     * Automatically, collects the time in queue for the item and includes it in
     * the time in queue response variable.
     *
     *  Automatically, updates the number in queue response variable.
     *
     * @return a reference to the QObject object that wraps the removed item, or null if the queue is empty
     */
    public final QObject removeNext() {
        QObject qObj = myDiscipline.removeNext(myList);
        if (qObj != null) {
            qObj.exitQueue(getTime());
            double timeInQ = getTime() - qObj.getTimeEnteredQueue();
            myTimeInQ.setValue(timeInQ);
            myNumInQ.setValue(myList.size());
            if (myMapTrackingFlag == true) {
                Object obj = qObj.getObject();
                myQObjectMap.remove(obj);
            }
        }
        myStatus = Status.DEQUEUED;
        notifyQueueListeners(qObj);
        return (qObj);
    }

    /** Returns true if this queue contains the specified element.
     * More formally, returns true if and only if this list contains at least
     * one element e such that (o==null ? e==null : o.equals(e)).
     *
     * Throws an IllegalArgumentException if QObject qObj is null.
     *
     * @param qObj The object to be removed
     * @return True if the queue contains the specified element.
     */
    public final boolean contains(QObject qObj) {
        if (qObj == null) {
            throw new IllegalArgumentException("The QObject qObj must be non-null");
        }
        return (myList.contains(qObj));
    }

    /** Returns true if this queue contains all of the elements in the specified collection
     * WARNING: The collection should contain references to QObject's otherwise it will
     * certainly return false.
     *
     * Throws an IllegalArguementException if the Collection is null
     *
     * @param c Collection c of items to check
     * @return True if the queue contains all of the elements.
     */
    public final boolean contains(Collection<QObject> c) {
        if (c == null) {
            throw new IllegalArgumentException("The Collection c must be non-null");
        }
        return (myList.containsAll(c));
    }

    /** Returns the index in this queue of the first occurrence of the specified element,
     * or -1 if the queue does not contain this element.
     * More formally, returns the lowest index i such that
     * (o==null ? get(i)==null : o.equals(get(i))), or -1 if there is no such index.
     *
     * Throws an IllegalArgumentException if QObject qObj is null.
     *
     * @param qObj The object to be found
     * @return The index (zero based) of the element or -1 if not found.
     */
    public final int indexOf(QObject qObj) {
        if (qObj == null) {
            throw new IllegalArgumentException("The QObject qObj must be non-null");
        }
        return (myList.indexOf(qObj));
    }

    /** Returns the index in this queue of the last occurrence of the specified element,
     * or -1 if the queue does not contain this element.
     * More formally, returns the lowest index i such that
     * (o==null ? get(i)==null : o.equals(get(i))), or -1 if there is no such index.
     *
     * Throws an IllegalArgumentException if QObject qObj is null.
     *
     * @param qObj The object to be found
     * @return The (zero based) index or -1 if not found.
     */
    public final int lastIndexOf(QObject qObj) {
        if (qObj == null) {
            throw new IllegalArgumentException("The QObject qObj must be non-null");
        }
        return (myList.lastIndexOf(qObj));
    }

    /** If the QObject, object map tracking is turned on
     * return the QObject associated with this object
     * Once the QObject is found, the user may use the queue methods
     * to operate on it
     *
     * return Null if object is not in Queue or if map tracking is not on.
     *
     * Throws an IllegalArgumentException if Object obj is null.
     *
     * @param obj The object for which the QObject must be found
     * @return The index (zero based) of the element or -1 if not found.
     */
    public final QObject getQObject(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("The Object obj must be non-null");
        }

        QObject qObj = null;
        if (myMapTrackingFlag == true) {
            qObj = (QObject) myQObjectMap.get(obj);
        }

        return (qObj);
    }

    /** Finds all the QObjects in the Queue that satisfy the condition and adds them
     *  to the foundItems collection
     * 
     * @param condition the condition of the search
     * @param foundItems the items found
     * @return yields true if at least one was found, false otherwise
     */
    public final boolean find(Predicate<QObject> condition, Collection<QObject> foundItems) {
        boolean found = false;
        for (QObject qo : myList) {
            if (condition.test(qo)) {
                found = true;
                foundItems.add(qo);
            }
        }
        return (found);
    }

    /** Finds the first QObject whose getQueuedObject().equals(object)
     * 
     * @param object the object to look for
     * @return null if no QObject is found
     */
    public final QObject find(Object object) {
        for (QObject qo : myList) {
            if (qo.getObject().equals(object)) {
                return (qo);
            }
        }
        return (null);
    }

    /** Finds all QObjects whose getQueuedObject().equals(object)
     * 
     * @param foundQObjects all QObjects whose getQueuedObject().equals(object) 
     * @param object the object to search
     * @return returns true if at least one match is found
     */
    public final boolean find(Collection<QObject> foundQObjects, Object object) {
        boolean found = false;
        for (QObject qo : myList) {
            if (qo.getObject().equals(object)) {
                foundQObjects.add(qo);
                found = true;
            }
        }
        return (found);
    }

    /** Finds and removes all the QObjects in the Queue that satisfy the condition and adds them
     *  to the deletedItems collection.  Waiting time statistics are automatically collected
     * 
     * @param condition The condition to check
     * @param deletedItems Holds the items that were removed from the Queue
     * @return yields true if at least one was deleted, false otherwise
     */
    public final boolean remove(Predicate<QObject> condition, Collection<QObject> deletedItems) {
        return (remove(condition, deletedItems, true));
    }

    /** Finds and removes all the QObjects in the Queue that satisfy the condition and adds them
     *  to the deletedItems collection
     * 
     * @param condition The condition to check
     * @param deletedItems Holds the items that were removed from the Queue
     * @param waitStats indicates whether or not waiting time statistics should be collected
     * @return yields true if at least one was deleted, false otherwise
     */
    public final boolean remove(Predicate<QObject> condition, Collection<QObject> deletedItems, boolean waitStats) {
        boolean found = false;
        for (int i = 0; i < myList.size(); i++) {
            QObject qo = myList.get(i);
            if (condition.test(qo)) {
                found = true;
                deletedItems.add(qo);
                remove(qo, waitStats);
            }
        }
        return (found);
    }

    /** Removes the first occurrence in the queue of the specified element
     *  Automatically collects waiting time statistics and number in queue
     *  statistics.  If the queue does not contain the element then it
     *  is unchanged and false is returned
     *  
     * Throws an IllegalArgumentException if QObject qObj is null.
     * 
     * @param qObj the thing to remove
     * @return true if the item was removed
     */
    public final boolean remove(QObject qObj) {
        return (remove(qObj, true));
    }

    /** Removes the first occurrence in the queue of the specified element
     *  Automatically collects waiting time statistics and number in queue
     *  statistics.  If the queue does not contain the element then it
     *  is unchanged and false is returned
     *
     * Throws an IllegalArgumentException if QObject qObj is null.
     *
     * @param qObj The object to be removed
     * @param waitStats Indicates whether waiting time statistics should
     *        be collected on the removed item, true means collect statistics
     * @return True if the item was removed.
     */
    public final boolean remove(QObject qObj, boolean waitStats) {
        if (qObj == null) {
            throw new IllegalArgumentException("The QObject qObj must be non-null");
        }

        if (myList.remove(qObj)) {
            qObj.exitQueue(getTime());
            myNumInQ.setValue(myList.size());
            if (myMapTrackingFlag == true) {
                Object obj = qObj.getObject();
                myQObjectMap.remove(obj);
            }
            if (waitStats) {
                double timeInQ = getTime() - qObj.getTimeEnteredQueue();
                myTimeInQ.setValue(timeInQ);
            }
            return (true);
        } else {
            return (false);
        }
    }

    /** Removes the element at the specified position in this queue.
     * Shifts any subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     *
     * Automatically, collects the time in queue for the item and includes it in
     * the time in queue response variable.
     *
     * Automatically, updates the number in queue response variable.
     *
     * Throws an IndexOutOfBoundsException if the specified index is out of range {@literal (index < 0 || index >= size())}.
     *
     * @param index - the index of the element to be removed.
     * @return the element previously at the specified position
     */
    public final QObject remove(int index) {
        return (remove(index, true));
    }

    /** Removes the element at the specified position in this queue.
     * Shifts any subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     *
     * Automatically, collects number in queue statistics. If waitStats flag is
     * true, then automatically collects the time in queue for the item and includes it in
     * the time in queue response variable.
     *
     * Throws an IndexOutOfBoundsException if the specified index is out of range {@literal (index < 0 || index >= size())}.
     *
     * @param index - the index of the element to be removed.
     * @param waitStats - true means collect waiting time statistics, false means do not
     * @return the element previously at the specified position
     */
    public final QObject remove(int index, boolean waitStats) {
        QObject qObj = (QObject) myList.remove(index);
        if (myMapTrackingFlag == true) {
            Object obj = qObj.getObject();
            myQObjectMap.remove(obj);
        }
        qObj.exitQueue(getTime());
        if (waitStats) {
            double timeInQ = getTime() - qObj.getTimeEnteredQueue();
            myTimeInQ.setValue(timeInQ);
        }
        myNumInQ.setValue(myList.size());
        return (qObj);
    }

    /** Removes the QObject at the front of the queue
     *  Uses remove(int index) where index = 0 
     *  
     * @return The first QObject in the queue or null if the list is empty
     */
    public final QObject removeFirst() {
        if (myList.isEmpty()) {
            return (null);
        } else {
            return (remove(0));
        }
    }

    /** Removes the QObject at the last index in the queue.
     *  Uses remove(int index) where index is the size of the list - 1
     *  
     * @return The last QObject in the queue or null if the list is empty
     */
    public final QObject removeLast() {
        if (myList.isEmpty()) {
            return (null);
        } else {
            return (remove(myList.size() - 1));
        }
    }

    /** Returns the QObject at the front of the queue
     *  Depending on the queue discipline this may not be the next QObject
     *  
     * @return The first QObject in the queue or null if the list is empty
     */
    public final QObject peekFirst() {
        if (myList.isEmpty()) {
            return (null);
        } else {
            return (myList.get(0));
        }
    }

    /** Returns the QObject at the last index in the queue.
     *  
     * @return The last QObject in the queue or null if the list is empty
     */
    public final QObject peekLast() {
        if (myList.isEmpty()) {
            return (null);
        } else {
            return (myList.get(myList.size() - 1));
        }
    }

    /** Returns the QObject at the supplied index in the queue.
     *  
     * Throws an IndexOutOfBoundsException if the specified index is out of range {@literal (index < 0 || index >= size())}.
     * @param index the index to inspect
     * @return The QObject at index in the queue or null if the list is empty
     */
    public final QObject peekAt(int index) {
        if (myList.isEmpty()) {
            return (null);
        } else {
            return (myList.get(index));
        }
    }

    /** Removes from this queue all the elements that are contained in the specified collection
     * The collection should contain references to objects of type QObject
     * that had been enqueued in this queue; otherwise, nothing will be removed.
     *
     * Automatically, updates the number in queue variable
     * and time in queue statistics on removed items
     *
     * Throws an IllegalArguementException if the Collection is null
     *
     * @param c The collection containing the QObject's to remove
     * @return true if the queue changed as a result of the call
     */
    public final boolean removeAll(Collection<QObject> c) {
        return (removeAll(c, true));
    }

    /** Removes from this queue all the elements that are contained in the specified collection
     * The collection should contain references to objects of type QObject
     * that had been enqueued in this queue; otherwise, nothing will be removed.
     *
     * Automatically, updates the number in queue variable
     * If statFlag is true it automatically collects time in queue statistics on removed items
     *
     * Throws an IllegalArguementException if the Collection is null
     *
     * @param c The collection containing the QObject's to remove
     * @param statFlag true means collect statistics, false means do not
     * @return true if the queue changed as a result of the call
     */
    public final boolean removeAll(Collection<QObject> c, boolean statFlag) {
        if (c == null) {
            throw new IllegalArgumentException("The Collection c must be non-null");
        }

        boolean removedFlag = false;
        for (QObject qObj : c) {
            removedFlag = remove(qObj, statFlag);
        }
        return (removedFlag);
    }

    /** Removes from this queue all the elements that are presented by iterating through this iterator
     * The iterator should be based on a collection that contains references to objects of type QObject
     * that had been enqueued in this queue; otherwise, nothing will be removed.
     *
     * Automatically, updates the number in queue variable
     * and time in queue statistics on removed items
     *
     * Throws an IllegalArguementException if the Iterator is null
     *
     * @param c The iterator over the collection containing the QObject's to remove
     * @return true if the queue changed as a result of the call
     */
    public final boolean removeAll(Iterator<QObject> c) {
        return (removeAll(c, true));
    }

    /** Removes from this queue all the elements that are presented by iterating through this iterator
     * The iterator should be based on a collection that contains references to objects of type QObject
     * that had been enqueued in this queue; otherwise, nothing will be removed.
     *
     * Automatically, updates the number in queue variable
     * If statFlag is true it automatically collects time in queue statistics on removed items
     *
     * Throws an IllegalArguementException if the Iterator is null
     *
     * @param c The iterator over the collection containing the QObject's to remove
     * @param statFlag true means collect statistics, false means do not
     * @return true if the queue changed as a result of the call
     */
    public final boolean removeAll(Iterator<QObject> c, boolean statFlag) {
        if (c == null) {
            throw new IllegalArgumentException("The iterator must be non-null");
        }

        boolean removedFlag = false;
        while (c.hasNext()) {
            QObject qo = c.next();
            removedFlag = remove(qo, statFlag);
        }

        return (removedFlag);
    }

    /** Turns on the tracing to a text file of the times in queue.
     */
    public final void turnOnTimeInQTrace() {
        myTimeInQ.turnOnTrace();
    }

    /** Turns on the tracing to a text file of the times in queue.
     * @param header the header
     */
    public final void turnOnTimeInQTrace(boolean header) {
        myTimeInQ.turnOnTrace(header);
    }

    /** Turns on the tracing to a text file of the times in queue.
     * @param fileName the file name
     */
    public final void turnOnTimeInQTrace(String fileName) {
        myTimeInQ.turnOnTrace(fileName);
    }

    /** Turns on the tracing to a text file of the times in queue.
     * @param fileName the file name
     * @param header the header
     */
    public final void turnOnTimeInQTrace(String fileName, boolean header) {
        myTimeInQ.turnOnTrace(fileName, header);
    }

    /** Turns on the tracing to a text file the number in queue for each
     * state change.
     */
    public final void turnOnNumberInQTrace() {
        myNumInQ.turnOnTrace();
    }

    /** Turns on the tracing to a text file the number in queue for each
     * state change.
     * @param header the header
     */
    public final void turnOnNumberInQTrace(boolean header) {
        myNumInQ.turnOnTrace(header);
    }

    /** Turns on the tracing to a text file the number in queue for each
     * state change.
     * @param fileName the file name
     */
    public final void turnOnNumberInQTrace(String fileName) {
        myNumInQ.turnOnTrace(fileName);
    }

    /** Turns on the tracing to a text file the number in queue for each
     * state change.
     * @param fileName the file name
     * @param header the header
     */
    public final void turnOnNumberInQTrace(String fileName, boolean header) {
        myNumInQ.turnOnTrace(fileName, header);
    }

    /** Turns off the tracing of the times in queue.
     */
    public final void turnOffTimeInQTrace() {
        myTimeInQ.turnOffTrace();
    }

    /** Turns off the tracing of the number in queue.
     */
    public final void turnOffNumberInQTrace() {
        myNumInQ.turnOffTrace();
    }

    /** Removes all of the elements from this collection
     *
     * WARNING: This method DOES NOT record the time in queue for the cleared items
     * if the user wants this functionality, it can be accomplished using the
     * remove(int index) method, while looping through the items to remove
     * No listeners are notified of the queue change.
     * 
     * This method simply clears the underlying data structure that holds the objects
     */
    public final void clear() {
        for (QObject qObj : myList) {
            qObj.exitQueue(getTime());
        }
        myList.clear();
        myNumInQ.setValue(myList.size());
        if (myMapTrackingFlag == true) {
            myQObjectMap.clear();
        }
    }

    /** Returns an iterator (as specified by Collection ) over the elements in the queue in proper sequence.
     * The elements will be ordered according to the state of the queue given
     * the specified queue discipline.
     *
     * WARNING:  The remove() method is not supported by this iterator.  A call to remove() with this iterator
     * will result in an UnsupportedOperationException
     *
     * @return an iterator over the elements in the queue
     */
    @Override
    public final Iterator<QObject> iterator() {
        return (new QueueListIterator());
    }

    /** Returns an iterator (as specified by Collection ) over the elements in the queue in proper sequence.
     * The elements will be ordered according to the state of the queue given
     * the specified queue discipline.
     *
     * WARNING:  The add(), remove(), and set() methods are not supported by this iterator.  Calls to these
     * methods will result in an UnsupportedOperationException
     *
     * @return an iterator over the elements in the queue
     */
    public final ListIterator<QObject> listIterator() {
        return (new QueueListIterator());
    }

    /** Gets the size (number of elements) of the queue.
     * @return The number of items in the queue.
     */
    public final int size() {
        return (myList.size());
    }

    /** Returns whether or not the queue is empty.
     * @return True if the queue is empty.
     */
    public final boolean isEmpty() {
        return (myList.isEmpty());
    }

    /** Returns true if the queue is not empty
     * 
     * @return true if the queue is not empty
     */
    public final boolean isNotEmpty() {
        return (!isEmpty());
    }

    @Override
    public String asString(){
        return (myList.toString());
    }

    /** Gets the entity tracking option, true means on
     *
     * @return the entity tracking option,
     */
    public final boolean getEntityTrackingOption() {
        return (myMapTrackingFlag);
    }

    /** Sets the option for tracking entities in a map, while
     *  they are in the queue, true means tracking is on.
     *  If the user attempts to turn tracking on when the queue is not empty
     *  then an IllegalArgumentException is thrown.
     *
     * @param b  true for tracking
     */
    public final void setEntityMapTrackingOption(boolean b) {
        if (b == false) {// wants to turn it off
            myMapTrackingFlag = false;
            if (myQObjectMap != null) {
                myQObjectMap.clear();
            }
        } else { // wants to turn it on
            if (!isEmpty()) // if queue is not empty throw an exception
            {
                throw new IllegalArgumentException("Cannot turn on entity map tracking when the queue is not empty.");
            }
            // queue is empty
            myMapTrackingFlag = true;
            if (myQObjectMap == null) // if not made yet, make the map
            {
                myQObjectMap = new LinkedHashMap<Object, QObject>();
            }
        }
    }

    /** Turns off the ability to track entities in the queue
     *  and clears the map tracking if it had been on
     */
    public final void turnOffEntityMapTracking() {
        myMapTrackingFlag = false;
        if (myQObjectMap != null) {
            myQObjectMap.clear();
        }
    }

    /** Get the number in queue across replication statistics
     *
     * @return the statistic
     */
    public final StatisticAccessorIfc getNumInQAcrossReplicationStatistic() {
        return myNumInQ.getAcrossReplicationStatistic();
    }

    /** Get the time in queue across replication statistics
     *
     * @return the statistic
     */
    public final StatisticAccessorIfc getTimeInQAcrossReplicationStatistic() {
        return myTimeInQ.getAcrossReplicationStatistic();
    }

    /** Within replication statistics for time in queue
     *
     * @return Within replication statistics for time in queue
     */
    public final WeightedStatisticIfc getTimeInQWithinReplicationStatistic() {
        return myTimeInQ.getWithinReplicationStatistic();
    }

    /** Within replication statistics for number in queue
     *
     * @return the within replication statistics for number in queue
     */
    public final WeightedStatisticIfc getNumInQWithinReplicationStatistic() {
        return myNumInQ.getWithinReplicationStatistic();
    }

    /** Allows access to across interval response for number in queue
     *  if turned on
     * 
     * @return the across interval response
     */
    public final ResponseVariable getNumInQAcrossIntervalResponse() {
        return myNumInQ.getAcrossIntervalResponse();
    }

    /** Allows access to across interval response for time in queue
     *  if turned on
     * 
     * @return  the across interval response
     */
    public final ResponseVariable getTimeInQAcrossIntervalResponse() {
        return myTimeInQ.getAcrossIntervalResponse();
    }

    
    /** Notifies any listeners that the queue changed
     * @param qObject The qObject associated with the notification
     */
    protected void notifyQueueListeners(QObject qObject) {
        if (myQueueListeners == null) {
            return;
        }

        for (QueueListenerIfc ql : myQueueListeners) {
            ql.update(this, qObject);
        }
    }

    private class QueueListIterator implements ListIterator<QObject> {

        protected ListIterator<QObject> myIterator;

        protected QueueListIterator() {
            myIterator = myList.listIterator();
        }

        @Override
        public void add(QObject o) {
            throw new UnsupportedOperationException("The method add() is not supported for Queue iteration");
        }

        @Override
        public boolean hasNext() {
            return myIterator.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return myIterator.hasPrevious();
        }

        @Override
        public QObject next() {
            return myIterator.next();
        }

        @Override
        public int nextIndex() {
            return myIterator.nextIndex();
        }

        @Override
        public QObject previous() {
            return myIterator.previous();
        }

        @Override
        public int previousIndex() {
            return myIterator.previousIndex();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("The method remove() is not supported for Queue iteration");
        }

        @Override
        public void set(QObject o) {
            throw new UnsupportedOperationException("The method set() is not supported for Queue iteration");
        }
    }
}
