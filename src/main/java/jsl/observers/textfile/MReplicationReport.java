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
import java.util.ArrayList;
import java.util.List;

import jsl.modeling.*;

import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.observers.ObserverIfc;
import jsl.utilities.statistic.StatisticAccessorIfc;
import jsl.utilities.reporting.TextReport;

public class MReplicationReport extends TextReport implements ObserverIfc {

    protected Collection<ResponseVariable> myResponseVariables;

    protected Model myModel;

    public MReplicationReport(String name) {
        this(null, name);
    }

    public MReplicationReport(String directory, String name) {
        super(directory, name);
        myResponseVariables = new ArrayList<ResponseVariable>();
    }

    public void update(Object observable, Object obj) {
        String s;
        myModel = (Model) observable;

        if (myModel.checkForBeforeExperiment()) {
            addFileNameAndDate();
            println(myModel);
            myModel.getResponseVariables(myResponseVariables);
        }

        if (myModel.checkForBeforeReplication()) {
            ExperimentGetIfc e = myModel.getExperiment();
            s = "Starting replication " + e.getCurrentReplicationNumber() + "\n";
            s = s + "Planned replication length " + e.getLengthOfReplication() + "\n";
            s = s + "Warmup time " + e.getLengthOfWarmUp() + "\n";
            println(s);
        }

        if (myModel.checkForAfterReplication()) {
            ExperimentGetIfc e = myModel.getExperiment();
            s = "Ending replication " + e.getCurrentReplicationNumber() + "\n";
            s = s + "Ending time " + myModel.getTime() + "\n";

            println(s);

            List<ResponseVariable> rvs = myModel.getResponseVariables();

            if (!rvs.isEmpty()) {
                println();
                println();
                println("----------------------------------------------------------");
                println("Within Replication statistics:");
                println("----------------------------------------------------------");
                println();

                for (ResponseVariable rv : rvs) {
                    if (rv.getDefaultReportingOption()) {
                        println(rv.getWithinReplicationStatistic());
                    }
                }
            }

            List<Counter> counters = myModel.getCounters();

            if (!counters.isEmpty()) {
                println();
                println();
                println("----------------------------------------------------------");
                println("Counter statistics:");
                println("----------------------------------------------------------");
                println();

                for (Counter c : counters) {
                    if (c.getDefaultReportingOption()) {
                        s = "Name " + c.getName() + "\n";
                        s = s + "Final Counter Value " + c.getValue() + "\n";
                        println(s);
                    }
                }

                for (Counter c : counters) {
                    if (c.getDefaultReportingOption()) {
                        ResponseVariable acrossIntervalResponse = c.getAcrossIntervalResponse();
                        if (acrossIntervalResponse != null){
                            println(acrossIntervalResponse.getAcrossReplicationStatistic());
                        }
                    }
                }
            }

        }
    }
}

