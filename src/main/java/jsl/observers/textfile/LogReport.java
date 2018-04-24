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
