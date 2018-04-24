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
package jsl.observers;

import java.util.*;

import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.utilities.statistic.StatisticAccessorIfc;
import jsl.modeling.*;

/**
 */
public class ExperimentConsoleObserver implements ObserverIfc {

    private Simulation mySim;
    protected boolean myRepUpdateFlag = false;

    public ExperimentConsoleObserver(Simulation sim) {
        this(false);
        mySim = sim;
    }

    /**
     * @param repUpdateFlag 
     */
    public ExperimentConsoleObserver(boolean repUpdateFlag) {
        myRepUpdateFlag = repUpdateFlag;
    }

    public final void setReplicationOutputFlag(boolean flag) {
        myRepUpdateFlag = flag;
    }

    public final boolean getReplicationOutputFlag() {
        return (myRepUpdateFlag);
    }

    @Override
    public void update(Object simulation, Object arg1) {
        Simulation sim = mySim;

        if (sim.isInitialized()) {
            System.out.println("Simulation: " + sim.getName() + " initialized.");
        }

        if (myRepUpdateFlag == true) {
            if (sim.isStepCompleted()) {
                System.out.println("Experiment: " + sim.getName() + " Replication: " + sim.getCurrentReplicationNumber() + " Completed");
            }
        }

        if (sim.isEnded()) {
            ExperimentGetIfc e = sim.getExperiment();
            System.out.println(e);
            System.out.println("Number of replications: " + e.getNumberOfReplications());
            System.out.println("Run length for each replication: " + e.getLengthOfReplication());
            System.out.println("Warm up for each replication: " + e.getLengthOfWarmUp());
            long et = sim.getMaximumAllowedExecutionTime();
            if (et == 0) {
                System.out.println("Maximum allowed experiment execution time not specified.");
            } else {
                System.out.println("Maximum allowed experiment execution time: " + et + " milliseconds.");
            }

            System.out.println();
            long t = sim.getEndExecutionTime() - sim.getBeginExecutionTime();
            System.out.println("The total time was approximately " + (t / 1000.0) + " seconds");

            if (sim.allStepsCompleted()) {
                System.out.println("The experiment ran all replications.");
            }

            if (sim.executionTimeExceeded()) {
                System.out.println("The experiment timed out.");
            }

            if (sim.stoppedByCondition()) {
                System.out.println("The experiment because it was stopped by the user.");
                System.out.println();
            }

            if (sim.isUnfinished()) {
                System.out.println("The experiment was ended early.");
            }

            
            List<ResponseVariable> rvs = sim.getModel().getResponseVariables();

            if (!rvs.isEmpty()) {
                System.out.println();
                System.out.println();
                System.out.println("----------------------------------------------------------");
                System.out.println("Across Replication statistics");
                System.out.println("----------------------------------------------------------");
                System.out.println();
                for (ResponseVariable rv : rvs) {
                    if (rv.getDefaultReportingOption()) {
                        StatisticAccessorIfc stat = rv.getAcrossReplicationStatistic();
                        System.out.println(stat);
                    }
                }
            }

            List<Counter> counters = sim.getModel().getCounters();

            if (!counters.isEmpty()) {
                System.out.println();
                System.out.println("----------------------------------------------------------");
                System.out.println("Counter statistics:");
                System.out.println("----------------------------------------------------------");
                System.out.println();

                for (Counter c : counters) {
                    if (c.getDefaultReportingOption()) {
                        StatisticAccessorIfc stat = c.getAcrossReplicationStatistic();
                        System.out.println(stat);
                    }
                }
            }

        }

    }
}
