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
package jsl.observers.variable;

import java.io.File;
import jsl.modeling.elements.variable.Variable;
import jsl.utilities.reporting.TextReport;
import jsl.modeling.*;
import jsl.observers.ObserverIfc;

/**
 * This class creates a comma separated file that traces the value of a variable
 *
 * observation number, time of change, value, time of previous change, previous
 * value, weight, replication number, within replication count, experiment name
 *
 */
public class VariableTraceTextReport extends TextReport implements ObserverIfc {

    protected long myCount = 0;

    protected long myRepCount = 0;

    protected double myRepNum = 0;

    /**
     * Creates new ResponseVariableTrace
     *
     * @param fileName the file name
     */
    public VariableTraceTextReport(String fileName) {
        this(null, false);
    }

    /**
     * 
     * @param fileName the file name
     * @param header the header
     */
    public VariableTraceTextReport(String fileName, boolean header) {
        this(null, fileName, false);
    }

    /**
     *
     * @param directory the directory
     * @param fileName the file name
     * @param header the header
     */
    public VariableTraceTextReport(File directory, String fileName, boolean header) {
        super(directory, fileName, "csv");
        if (header) {
            writeHeader();
        }
    }

    private void writeHeader() {
        print("n");
        print(",");
        print("t");
        print(",");
        print("x(t)");
        print(",");
        print("t(n-1)");
        print(",");
        print("x(t(n-1))");
        print(",");
        print("w");
        print(",");
        print("r");
        print(",");
        print("nr");
        print(",");
        print("sim");
        print(",");
        print("model");
        print(",");
        print("exp");
        println();
    }

    @Override
    public void update(Object observable, Object obj) {
        Variable v = (Variable) observable;
        Model m = v.getModel();

        if (v.checkForUpdate()) {
            myCount++;
            print(myCount);
            print(",");
            print(v.getTimeOfChange());
            print(",");
            print(v.getValue());
            print(",");
            print(v.getPreviousTimeOfChange());
            print(",");
            print(v.getPreviousValue());
            print(",");
            print(v.getWeight());
            print(",");
            ExperimentGetIfc e = v.getExperiment();
            if (e != null) {
                if (myRepNum != e.getCurrentReplicationNumber()) {
                    myRepCount = 0;
                }
                myRepCount++;
                myRepNum = e.getCurrentReplicationNumber();
                print(myRepNum);
                print(",");
                print(myRepCount);
                print(",");
                print(m.getSimulation().getName());
                print(",");
                print(m.getName());
                print(",");
                print(e.getExperimentName());
            }
            println();

        }
    }
}
