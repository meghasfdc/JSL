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
