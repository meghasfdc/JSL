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
package jsl.observers.textfile;

import java.util.Collection;
import java.util.List;

import jsl.modeling.Model;
import jsl.modeling.elements.variable.*;
import jsl.observers.ObserverIfc;
import jsl.utilities.statistic.StatisticAccessorIfc;
import jsl.utilities.reporting.TextReport;

public class MSummaryReport extends TextReport implements ObserverIfc {

    protected Collection<ResponseVariable> myResponseVariables;

    protected Model myModel;

    public MSummaryReport(String name) {
        this(null, name);
    }

    public MSummaryReport(String directory, String name) {
        super(directory, name);
    }

    public void update(Object observable, Object obj) {

        myModel = (Model) observable;

        if (myModel.checkForAfterExperiment()) {

            println();
            println();
            println("----------------------------------------------------------");
            println("Across Replication statistics");
            println("----------------------------------------------------------");
            println();

            List<ResponseVariable> rvs = myModel.getResponseVariables();
            for (ResponseVariable rv : rvs) {
                if (rv.getDefaultReportingOption()) {
                    StatisticAccessorIfc stat = rv.getAcrossReplicationStatistic();
                    println(stat);
                }
            }

            println();
            println();
            println("----------------------------------------------------------");
            println("Counter statistics:");
            println("----------------------------------------------------------");
            println();

            List<Counter> counters = myModel.getCounters();

            for (Counter c : counters) {
                if (c.getDefaultReportingOption()) {
                    StatisticAccessorIfc stat = c.getAcrossReplicationStatistic();
                    println(stat);
                }
            }
        }
    }
}
