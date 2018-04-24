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
