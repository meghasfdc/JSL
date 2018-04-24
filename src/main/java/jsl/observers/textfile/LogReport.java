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

import java.text.DecimalFormat;

import jsl.modeling.*;

import jsl.observers.ObserverIfc;
import jsl.utilities.reporting.TextReport;

public class LogReport extends TextReport implements ObserverIfc {

    DecimalFormat df = new DecimalFormat("0.###");

    private boolean myTimedUpdateLogFlag = false;

    public LogReport(String name) {
        this(null, name);
    }

    public LogReport(String directory, String name) {
        super(directory, name);
        addFileNameAndDate();
    }

    public void turnOnTimedUpdateLogging() {
        myTimedUpdateLogFlag = true;
    }

    public void turnOffTimedUpdateLogging() {
        myTimedUpdateLogFlag = false;
    }

    public void update(Object subject, Object arg) {
        ModelElement m = (ModelElement) subject;

        if (m.checkForBeforeExperiment()) {
            println("Before experiment for " + m.getClass().getName() + " " + m.getName());
        }

        if (m.checkForInitialize()) {
            println("Initialize for " + m.getClass().getName() + " " + m.getName());
        }

        if (m.checkForBeforeReplication()) {
            Simulation s = m.getSimulation();
            println("Before Replication " + s.getCurrentReplicationNumber() + " for " + m.getClass().getName() + " " + m.getName());

        }

        if (m.checkForMonteCarlo()) {
            println("Monte Carlo for " + m.getClass().getName() + " " + m.getName());
        }

        if (myTimedUpdateLogFlag == true) {
            if (m.checkForTimedUpdate()) {
                println("Timed update for " + m.getClass().getName() + " " + m.getName() + " at time " + m.getTime());
            }
        }

        if (m.checkForWarmUp()) {
            println("Warm up for " + m.getClass().getName() + " " + m.getName() + " at time " + m.getTime());
        }

        if (m.checkForReplicationEnded()) {
            Simulation s = m.getSimulation();
            println("Replication ended " + s.getCurrentReplicationNumber() + " for " + m.getClass().getName() + " " + m.getName() + " at time " + m.getTime());
        }

        if (m.checkForAfterReplication()) {
            Simulation s = m.getSimulation();
            println("After Replication " + s.getCurrentReplicationNumber() + " for " + m.getClass().getName() + " " + m.getName() + " at time " + m.getTime());
        }

        if (m.checkForAfterExperiment()) {
            println("After experiment for " + m.getClass().getName() + " " + m.getName() + " at time " + m.getTime());
        }

    }
}
