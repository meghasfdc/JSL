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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.variables;

import jsl.modeling.ModelElement;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.observers.ModelElementObserver;
import jsl.utilities.statistic.StatisticAccessorIfc;
import jsl.utilities.statistic.WeightedStatistic;
import jsl.utilities.statistic.WeightedStatisticIfc;

/**
 *
 * @author rossetti
 */
public class TimedIntervalResponse extends ModelElement {

    /**
     * The within interval statistics for the response variable
     *
     */
    protected WeightedStatistic myWithinIntervalStats;
    
    /**
     *  the response that is being observed
     */
    protected ResponseVariable myObservedResponse;
    
    /** For collected across interval statistics
     * 
     */
    protected ResponseVariable myAcrossIntervalResponse;
    
    private ResponseObserver myResponseObserver;

    public TimedIntervalResponse(ModelElement parent, double interval) {
        this(parent, interval, null);
    }

    public TimedIntervalResponse(ModelElement parent, double interval, String name) {
        super(parent, name);
        setTimedUpdateInterval(interval);
        myWithinIntervalStats = new WeightedStatistic();
        myResponseObserver = new ResponseObserver();
    }

    public void setResponse(ResponseVariable rv){
        if (rv == null){
            throw new IllegalArgumentException("The response to observe was null");
        }
        if (myObservedResponse == null){
            myObservedResponse = rv;
            myAcrossIntervalResponse = new ResponseVariable(this, rv.getName()+":Interval");
            rv.addObserver(myResponseObserver);
        }
    }
    
        @Override
    protected void beforeExperiment() {
        super.beforeExperiment();
        myWithinIntervalStats.reset();
    }

    @Override
    protected void beforeReplication() {
        super.beforeReplication();
        myWithinIntervalStats.reset();
    }
    
    @Override
    protected void timedUpdate() {
        // collect across timed update statistics
        myAcrossIntervalResponse.setValue(myWithinIntervalStats.getAverage());
        myWithinIntervalStats.reset();
    }

    public final void turnOnAcrossReplicationMaxCollection() {
        myAcrossIntervalResponse.turnOnAcrossReplicationMaxCollection();
    }

    public final WeightedStatisticIfc getWithinReplicationStatistic() {
        return myAcrossIntervalResponse.getWithinReplicationStatistic();
    }

    public final StatisticAccessorIfc getAcrossReplicationStatistic() {
        return myAcrossIntervalResponse.getAcrossReplicationStatistic();
    }

    public double getAcrossReplicationAverage() {
        return myAcrossIntervalResponse.getAcrossReplicationAverage();
    }

    public final void turnOnTrace() {
        myAcrossIntervalResponse.turnOnTrace();
    }

    public final void turnOnTrace(boolean header) {
        myAcrossIntervalResponse.turnOnTrace(header);
    }

    public final void turnOnTrace(String fileName) {
        myAcrossIntervalResponse.turnOnTrace(fileName);
    }

    public final void turnOnTrace(String name, boolean header) {
        myAcrossIntervalResponse.turnOnTrace(name, header);
    }

    public final void turnOffTrace() {
        myAcrossIntervalResponse.turnOffTrace();
    }

    private class ResponseObserver extends ModelElementObserver {

        @Override
        protected void update(ModelElement m, Object arg) {
            // observe the actual values of the response
            double value;
            if (myObservedResponse instanceof ResponseVariable){
                value = myObservedResponse.getValue();
            } else {
                value = myObservedResponse.getPreviousValue();
            }
            double weight = myObservedResponse.getWeight();
            myWithinIntervalStats.collect(value, weight);
        }

        @Override
        protected void removedFromModel(ModelElement m, Object arg) {
            myObservedResponse.deleteObserver(myResponseObserver);
            myAcrossIntervalResponse.removeFromModel();
            myWithinIntervalStats.reset();
            myWithinIntervalStats = null;
            myObservedResponse = null;
            myAcrossIntervalResponse = null;
            myResponseObserver = null;
            TimedIntervalResponse.this.setTimedUpdateOption(false);
            TimedIntervalResponse.this.removeFromModel();
        }
    }

}
