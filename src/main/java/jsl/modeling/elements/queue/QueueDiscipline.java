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

import java.util.List;
import jsl.modeling.ModelElement;


/** The QueueDiscipline provides a mechanism to the Queue object
 * for ordering the elements.
 *
 */
@Deprecated
abstract public class QueueDiscipline extends ModelElement {


    public QueueDiscipline(ModelElement parent) {
        this(parent, null);
    }

    public QueueDiscipline(ModelElement parent, String name) {
        super(parent, name);
    }

    /** Adds the specified element to the proper location in the
     * supplied list.
     *
     *
     * @param list The list to which the object is being added
     * @param qObject The element to be added to the supplied list
     */
    abstract protected void add(List<QObject> list, QObject qObject);
    
    /** Returns a reference to the next QObjectIfc to be removed
     * from the queue.  The item is
     * not removed from the list.
     * @param list The list to be peeked into
     * @return The QObjectIfc that is next, or null if the list is empty
     */
    abstract protected QObject peekNext(List<QObject> list);
    
    /** Removes the next item from the supplied list according to
     * the discipline
     * @param list The list for which the next item will be removed
     * @return A reference to the QObjectIfc item that was removed or null if the list is empty
     */
    abstract protected QObject removeNext(List<QObject> list);
    
    /** Provides a "hook" method to be called when switching from one discipline to another
     *  The implementor should use this method to ensure that the underlying queue is in a state
     *  that allows it to be managed by this queue discipline
     * @param list The list for which the next item will be removed
     * @param currentDiscipline The queuing discipline that is currently managing the queue
     */
    abstract protected void switchFrom(List<QObject> list, QueueDiscipline currentDiscipline);
    
    /** Changes the priority of the QObject.  Must also re-order the Queue as necessary
     * 
     * @param list the list holding the things
     * @param qObject the qObject
     * @param priority the new priority
     */
    protected void changePriority(List<QObject> list, QObject qObject, int priority){
    	qObject.setPriority_(priority);
    }
    
    /** can be used to initialize the discipline prior to a replication
     */
//    abstract protected void initialize();
    
    /** can be used to setup the discipline prior to an experiment
     */
//    abstract protected void beforeExperiment();
    
    /** can be used to setup the discipline after a replication
     */
//    abstract protected void afterReplication();
    
}
