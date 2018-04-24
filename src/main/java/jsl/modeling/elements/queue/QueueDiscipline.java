/*
 * Copyright (c) 2018. Manuel D. Rossetti, manuelrossetti@gmail.com
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
