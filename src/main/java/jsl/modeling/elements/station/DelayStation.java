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
package jsl.modeling.elements.station;

import jsl.modeling.EventActionIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.queue.QObject;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.GetValueIfc;
import jsl.utilities.random.distributions.Constant;
import jsl.utilities.statistic.StatisticAccessorIfc;
import jsl.utilities.statistic.WeightedStatisticIfc;

public class DelayStation extends Station {

    private boolean myUseQObjectSTFlag;

    private GetValueIfc myDelayTime;

    protected TimeWeighted myNS;

    private EndDelayAction myEndDelayAction;

    public DelayStation(ModelElement parent) {
        this(parent, Constant.ZERO, null, null);
    }

    public DelayStation(ModelElement parent, GetValueIfc sd) {
        this(parent, sd, null, null);
    }

    public DelayStation(ModelElement parent, String name) {
        this(parent, Constant.ZERO, null, name);
    }

    public DelayStation(ModelElement parent, GetValueIfc sd, String name) {
        this(parent, sd, null, name);
    }

    public DelayStation(ModelElement parent,
            GetValueIfc sd, SendQObjectIfc sender, String name) {
        super(parent, sender, name);
        setDelayTime(sd);
        myNS = new TimeWeighted(this, getName() + ":NumInStation");
        myEndDelayAction = new EndDelayAction();
    }

    public final void setDelayTime(GetValueIfc st) {
        myDelayTime = st;
    }

    public GetValueIfc getDelayTime() {
        return myDelayTime;
    }

    public final double getNumInStation() {
        return myNS.getValue();
    }

    /**
     * Tells the station to use the QObject to determine the service time
     *
     * @param option
     */
    public final void setUseQObjectDelayTimeOption(boolean option) {
        myUseQObjectSTFlag = option;
    }

    /**
     * Whether or not the station uses the QObject to determine the service time
     *
     * @return
     */
    public final boolean getUseQObjectDelayTimeOption() {
        return myUseQObjectSTFlag;
    }

    protected double getDelayTime(QObject customer) {
        double t;
        if (getUseQObjectDelayTimeOption()) {
            GetValueIfc v = customer.getValueObject();
            t = v.getValue();
        } else {
            t = getDelayTime().getValue();
        }
        return t;
    }

    public final StatisticAccessorIfc getNSAcrossReplicationStatistic() {
        return myNS.getAcrossReplicationStatistic();
    }

    public final WeightedStatisticIfc getNSWithinReplicationStatistic() {
        return myNS.getWithinReplicationStatistic();
    }

    @Override
    public void receive(QObject customer) {
        myNS.increment(); // new customer arrived
        scheduleEvent(myEndDelayAction, getDelayTime(customer), customer);
    }

    class EndDelayAction implements EventActionIfc {

        @Override
        public void action(JSLEvent event) {
            QObject leavingCustomer = (QObject) event.getMessage();
            myNS.decrement(); // customer departed
            send(leavingCustomer);
        }
    }
}
