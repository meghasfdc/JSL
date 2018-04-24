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

import jsl.modeling.*;

/**
 *  Can be attached to a Simulation to have updates to System.out
 *  during replications
 * 
 * @author rossetti
 */
public class ReplicationConsoleObserver implements ObserverIfc {
    private Simulation mySim;
    
    public ReplicationConsoleObserver(Simulation sim) {
        mySim = sim;
    }

    @Override
    public void update(Object observable, Object obj) {
        Simulation sim = mySim;

        if (sim.isStepCompleted()) {
            System.out.println();
            System.out.println("Simulation name: " + sim.getName());
            System.out.println("Completed Replication: " + sim.getCurrentReplicationNumber() + " of " + sim.getNumberOfReplications());
            Executive exec = sim.getExecutive();
            ExperimentGetIfc ep = sim.getExperiment();

            if (exec.isEnded()) {

                System.out.println("Planned Run length for replication: " + ep.getLengthOfReplication());
                System.out.println("Warm up for replication: " + ep.getLengthOfWarmUp());
                System.out.println("The actual run length for replication: " + exec.getTime());
                long et = exec.getMaximumAllowedExecutionTime();
                if (et == 0) {
                    System.out.println("Maximum allowed execution time not specified.");
                } else {
                    System.out.println("Maximum allowed execution time: " + et + " milliseconds.");
                }
                System.out.println();
                long t = exec.getEndExecutionTime() - exec.getBeginExecutionTime();
                System.out.println("The total execution time was approximately " + t / 1000.0 + " seconds");

                if (exec.isCompleted()) {
                    System.out.println("The replication ran all its events.");
                }

                if (exec.isTimedOut()) {
                    System.out.println("The replication timed out.");
                }

                if (exec.isEndConditionMet()) {
                    System.out.println("The replication ended because its end condition was met.");
                }

                if (exec.isUnfinished()) {
                    System.out.println("The replication was ended early.");
                }

                System.out.println();
            }
        }
    }
}
