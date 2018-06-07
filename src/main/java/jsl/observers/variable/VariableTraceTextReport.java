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
