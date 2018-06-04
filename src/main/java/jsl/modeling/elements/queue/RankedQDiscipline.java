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
package jsl.modeling.elements.queue;

import java.util.List;
import java.util.ListIterator;
import java.util.Collections;
import jsl.modeling.ModelElement;

/** The RankedQDiscipline provides a mechanism to the Queue object
 * for ordering the elements according to a priority
 * rule.
 */
@Deprecated
public class RankedQDiscipline extends QueueDiscipline {

    public RankedQDiscipline(ModelElement parent) {
        this(parent, null);
    }

    public RankedQDiscipline(ModelElement parent, String name) {
        super(parent, name);
    }

    /** Adds the specified element to the proper location in the
     * supplied list.
     *
     * RankedQ discipline ensures that each new element is added such that the
     * priority is maintained from smallest first to largest priority last using the compareTo
     * method of the QObject.  Ties in priority give preference to time of creation, then to
     * order of creation.
     *
     * @param list The list to which the object is being added
     * @param qObject The element to be added to the supplied list
     */
    protected void add(List<QObject> list, QObject qObject) {

        // nothing in queue, just add it, and return
        if (list.isEmpty()) {
            list.add(qObject);
            return;
        }

        // might as well check for worse case, if larger than the largest then put it at the end and return
        if (qObject.compareTo(list.get(list.size() - 1)) >= 0) {
            list.add(qObject);
            return;
        }

        // now iterate through the list
        for (ListIterator<QObject> i = list.listIterator(); i.hasNext();) {
            if (qObject.compareTo(i.next()) < 0) {
                // next() move the iterator forward, if it is < what was returned by next(), then it
                // must be inserted at the previous index
                list.add(i.previousIndex(), qObject);
                return;
            }
        }

    }

    /** Returns a reference to the next QObjectIfc to be removed
     * from the queue according to the RankedQ discipline.  The item is
     * not removed from the list.
     * @param list The list to be peeked into
     * @return The QObjectIfc that is next, or null if the list is empty
     */
    protected QObject peekNext(List<QObject> list) {

        if (list.isEmpty()) {
            return (null);
        }

        return ((QObject) list.get(0));// in RankedQ the first element has the lowest priority
    }

    /** Removes the next item from the supplied list according to
     * the RankedQ discipline
     * @param list The list for which the next item will be removed
     * @return A reference to the QObjectIfc item that was removed or null if the list is empty
     */
    protected QObject removeNext(List<QObject> list) {

        if (list.isEmpty()) {
            return (null);
        }

        return ((QObject) list.remove(0)); // in RankedQ the first element has the lowest priority
    }

    /** Provides a "hook" method to be called when switching from one discpline to another
     *  The implementor should use this method to ensure that the underlying queue is in a state
     *  that allows it to be managed by this queue discipline.
     *
     *  Since regardless of the former queue discipline, the ranked queue discipline
     *  must ensure that the underlying queue is in a ranked state after the change over.
     *
     * @param list The list for which the next item will be removed
     * @param currentDiscipline The queueing discpline that is currently managing the queue
     */
    protected void switchFrom(List<QObject> list, QueueDiscipline currentDiscipline) {
        Collections.sort(list);
    }

    /* (non-Javadoc)
     * @see jsl.modeling.elements.queue.QueueDiscipline#changePriority(java.util.List, jsl.modeling.elements.queue.QObject, int)
     */
    protected void changePriority(List<QObject> list, QObject qObject, int priority) {
        super.changePriority(list, qObject, priority);
        Collections.sort(list);
    }

}
