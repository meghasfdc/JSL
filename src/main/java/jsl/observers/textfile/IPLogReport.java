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

import java.util.*;

import jsl.modeling.*;
import jsl.observers.ObserverIfc;
import jsl.utilities.reporting.TextReport;
import jsl.utilities.IdentityIfc;

public class IPLogReport extends TextReport implements ObserverIfc {

    public IPLogReport(String name) {
        this(null, name);
    }

    public IPLogReport(String directory, String name) {
        super(directory, name);
    }

    public void update(Object observable, Object obj) {
        IterativeProcess ip = (IterativeProcess) observable;
        IdentityIfc id = (IdentityIfc) obj;

        if (ip.isInitialized()) {
            println(ip.getName() + " initialized at " + new Date());
        }

        if (ip.isStepCompleted()) {
            println(ip.getName() + "  completed " + id.getName());
        }

        if (ip.isEnded()) {
            println(ip.getName() + " ended at " + new Date());
            print("\t");
            if (ip.allStepsCompleted()) {
                println(ip.getName() + " completed all steps.");
            }

            if (ip.executionTimeExceeded()) {
                println(ip.getName() + " timed out.");
            }

            if (ip.stoppedByCondition()) {
                println(ip.getName() + " ended due to end condition being met.");
            }

            if (ip.isUnfinished()) {
                println(ip.getName() + " ended due to user.");
            }
        }
        print("\tCurrent state ");
        print(ip.getCurrentStateAsString());
        print("\t\tEnding State Indicator: ");
        println(ip.getEndingStateIndicatorAsString());;
        if (ip.getStoppingMessage()!= null) {
            print("\t\tStopping Message: ");
            println(ip.getStoppingMessage());
        }
    }
}
