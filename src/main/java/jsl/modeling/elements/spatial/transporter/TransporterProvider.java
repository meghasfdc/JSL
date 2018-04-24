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
package jsl.modeling.elements.spatial.transporter;

import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.queue.QObject;
import jsl.modeling.queue.QObjectSelectionRuleIfc;
import jsl.modeling.queue.Queue;
import jsl.modeling.queue.Queue.Discipline;

public class TransporterProvider extends SchedulingElement {

    public final static int DEFAULT_PRIORITY = 1;

    protected TransporterSet myTransporterSet;

    protected Queue myDispatchQ;

    /** Can be used to supply a rule for how the requests
     *  are selected for allocation
     */
    protected QObjectSelectionRuleIfc myInitialRequestSelectionRule;

    /** Can be used to supply a rule for how the requests
     *  are selected for allocation
     */
    protected QObjectSelectionRuleIfc myRequestSelectionRule;

    /** Creates a TransporterProvider that uses a FIFO queue discipline. An
     *  empty TransporterSet is created and must be filled
     *
     * @param parent
     */
    public TransporterProvider(ModelElement parent) {
        this(parent, null, null, null);
    }

    /** Creates a TransporterProvider that uses a FIFO queue discipline. An
     *  empty TransporterSet is created and must be filled
     *
     * @param parent
     * @param name
     */
    public TransporterProvider(ModelElement parent, String name) {
        this(parent, name, null, null);
    }

    /** Creates a TransporterProvider that uses the supplied set and FIFO queue discipline
     *
     * @param parent
     * @param name
     * @param set
     */
    public TransporterProvider(ModelElement parent, String name, TransporterSet set) {
        this(parent, name, set, null);
    }

    /** Creates a TransporterProvider that uses the supplied set and FIFO queue discipline
     *
     * @param parent
     * @param set
     */
    public TransporterProvider(ModelElement parent, TransporterSet set) {
        this(parent, null, set, null);
    }

    /** Creates a TransporterProvider that uses the supplied set and queue discipline
     *
     * @param parent
     * @param set
     * @param discipline
     */
    public TransporterProvider(ModelElement parent, TransporterSet set, Discipline discipline) {
        this(parent, null, set, discipline);
    }

    /** Creates a TransporterProvider that uses the supplied set and queue discipline
     *
     * @param parent
     * @param name
     * @param set
     * @param discipline
     */
    public TransporterProvider(ModelElement parent, String name, TransporterSet set, Discipline discipline) {
        super(parent, name);

        setTransporterSet(set);

        if (discipline == null) {
            discipline = Queue.Discipline.FIFO;
        }

        myDispatchQ = new Queue(this, getName() + " DispatchQ", discipline);

    }

    public final TransporterSet getTransporterSet() {
        return myTransporterSet;
    }

    /** This will change the queue discipline of the underlying Queue
     *
     * @param discipline
     */
    public final void changeDispatchQueueDiscipline(Discipline discipline) {
        myDispatchQ.changeDiscipline(discipline);
    }

    /** Returns the initial discipline for the queue
     *
     * @return
     */
    public final Discipline getDispatchQueueInitialDiscipline() {
        return myDispatchQ.getInitialDiscipline();
    }

    /** Sets the initial queue discipline
     *
     * @param discipline
     */
    public final void setDispatchQueueInitialDiscipline(Discipline discipline) {
        myDispatchQ.setInitialDiscipline(discipline);
    }

    /** Returns the current number of requests in the dispatch queue
     *
     * @return
     */
    public final int getNumberInDispatchQueue() {
        return (myDispatchQ.size());
    }

    /** If the request is in the dispatch queue, this removes it.  It does this
     *  without collecting statistics on the time in queue for the request.
     *
     * @param request Should not be null
     */
    public final void cancelRequest(QObject request) {
        myDispatchQ.remove(request);
    }

    /** This method provides a transporter to the requester.  A request
     *  is created with the default priority. The request is
     *  placed in the request queue for this TransporterProvider.  If the
     *  request is given an idle transporter, the request is removed from
     *  the dispatch queue and the requester is notified through the use of its
     *  idleTransporterProvided() method.  If there are no idle transporters
     *  the request waits in the dispatch queue.  When an idle transporter becomes
     *  available for the request, the requester is automatically notified through
     *  it idleTransporterProvided() method.  The request can be by the client
     *  to see if it has been queued.
     *
     * @param requester The requester for a transporter
     * @return A reference to the request.
     */
    public final QObject requestIdleTransporter(TransporterRequesterIfc requester) {
        return (requestIdleTransporter(requester, DEFAULT_PRIORITY));
    }

    /** This method provides a transporter to the requester.  A request
     *  is created with the given priority. The request is
     *  placed in the request queue for this TransporterProvider.  If the
     *  request is given an idle transporter, the request is removed from
     *  the dispatch queue and the requester is notified through the use of its
     *  idleTransporterProvided() method.  If there are no idle transporters
     *  the request waits in the dispatch queue.  When an idle transporter becomes
     *  available for the request, the requester is automatically notified through
     *  it idleTransporterProvided() method.  The request can be checked by the client
     *  to see if it has been queued.
     *
     * @param requester The requester for a transporter
     * @param priority, The priority for the request
     * @return A reference to the request.
     */
    public QObject requestIdleTransporter(TransporterRequesterIfc requester, int priority) {

        if (requester == null) {
            throw new IllegalArgumentException("The supplied TransporterRequesterIfc was null!");
        }

        QObject request = new QObject(getTime());
        request.setPriority(priority);
        request.setAttachedObject(requester);

        // always enqueue the request
        myDispatchQ.enqueue(request);

        // select the next request for the transporter
        QObject r = selectNextRequest();

        // if the selected request is the same as the entering request
        // we can try to provide a transporter for it
        Transporter transporter = null;
        if (r == request) {
            // check if there is an idle transporter
            transporter = myTransporterSet.selectIdleTransporter(r);

            if (transporter != null) { // an idle transporter has been selected for the request
                if (r == myDispatchQ.peekNext()) {
                    myDispatchQ.removeNext();
                } else {
                    myDispatchQ.remove(myDispatchQ.indexOf(r));
                }

                TransporterRequesterIfc requestingObj = (TransporterRequesterIfc) r.getAttachedObject();
                requestingObj.idleTransporterProvided(transporter, r);
            }

        }

        return (request);
    }

    /** Returns a reference to the request selection rule. May be null.
     *
     * @return
     */
    public final QObjectSelectionRuleIfc getRequestSelectionRule() {
        return myRequestSelectionRule;
    }

    /** A request selection rule can be supplied to provide alternative behavior
     *  within the selectNextRequest() method. A request selection rule, provides
     *  a mechanism to select the next request from the queue of waiting requests
     *
     * @param rule
     */
    public final void setRequestSelectionRule(QObjectSelectionRuleIfc rule) {
        myRequestSelectionRule = rule;
    }

    /** The rule to use when this provider is initialized
     *
     * @return
     */
    public final QObjectSelectionRuleIfc getInitialRequestSelectionRule() {
        return myInitialRequestSelectionRule;
    }

    /** The rule to use when this provider is initialized
     *
     * @param rule
     */
    public final void setInitialRequestSelectionRule(QObjectSelectionRuleIfc rule) {
        myInitialRequestSelectionRule = rule;
    }

    /** Selects a candidate request from the queue for allocation
     *  to one of the transporter units.  The selection process does not remove
     *  the request from the queue.
     *
     * @return The request that was selected to for a transporter
     */
    protected QObject selectNextRequest() {
        QObject r = null;
        if (myRequestSelectionRule != null) {
            r = myRequestSelectionRule.selectNext(myDispatchQ, this);
        } else {
            r = myDispatchQ.peekNext();
        }
        return (r);
    }

    @Override
    protected void initialize() {
        setRequestSelectionRule(getInitialRequestSelectionRule());

    }

    protected final void setTransporterSet(TransporterSet set) {

        if ((set == null) && (myTransporterSet == null)) {
            myTransporterSet = new TransporterSet(this, getName() + " TransporterSet");
        } else {
            myTransporterSet = set;
        }

        myTransporterSet.addTransporterProvider(this);

    }

    protected void transporterFreed() {
        //This is called when the transporterset has an idle transporter returned to it

        if (!myDispatchQ.isEmpty()) {
            // there are requests waiting for a transporter
            // select the next request for the transporter
            QObject r = selectNextRequest();

            // check if there is an idle transporter
            Transporter t = myTransporterSet.selectIdleTransporter(r);

            if (t != null) { // an idle transporter has been selected for the request
                if (r == myDispatchQ.peekNext()) {
                    myDispatchQ.removeNext();
                } else {
                    myDispatchQ.remove(myDispatchQ.indexOf(r));
                }
                TransporterRequesterIfc requestingObj = (TransporterRequesterIfc) r.getAttachedObject();
                requestingObj.idleTransporterProvided(t, r);
            }

        }

    }
}
