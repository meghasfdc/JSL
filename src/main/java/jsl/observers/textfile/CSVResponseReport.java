/*
 * Copyright (c) 2019. Manuel D. Rossetti, rossetti@uark.edu
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

import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.Simulation;
import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.ResponseVariable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *  Facilitates the collection of replication statistics for named responses to a comma separated value file.
 *  See SimulationDemos.demoResponseReport() for an example usage.
 */
public class CSVResponseReport extends CSVReport {

    private final List<String> myResponseNames;
    protected int myRepCount = 0;

    /**
     *
     * @param fileName the name of the file, stored in the jslOutput directory
     * @param responseNames the model element names of the ResponseVariable, TimeWeighted, or Counter to be written
     *                      to the report
     */
    public CSVResponseReport(String fileName, List<String> responseNames) {
        this(null, fileName, responseNames);
    }

    /**
     *
     * @param directory the directory to store the file
     * @param fileName the name of the file
     * @param responseNames the model element names of the ResponseVariable, TimeWeighted, or Counter to be written
     *                      to the report
     */
    public CSVResponseReport(File directory, String fileName, List<String> responseNames) {
        super(directory, fileName);
        Objects.requireNonNull(responseNames, "The list of names was null");
        myResponseNames = new ArrayList<>(responseNames);
    }

    @Override
    protected void beforeExperiment(ModelElement m, Object arg) {
        super.beforeExperiment(m, arg);
        myRepCount = 0;
    }

    @Override
    protected void writeHeader() {
        if (myHeaderFlag == true) {
            return;
        }
        myHeaderFlag = true;
        myWriter.print("SimName,");
        myWriter.print("ModelName,");
        myWriter.print("ExpName,");
        myWriter.print("RepNum,");
        int n = 1;
        for(String name: myResponseNames){
            myWriter.print(name);
            if (n < myResponseNames.size()){
                myWriter.print(",");
                n = n + 1;
            }
        }
        myWriter.println();
    }

    @Override
    protected void afterReplication(ModelElement m, Object arg) {
        Model model = m.getModel();
        Simulation sim = m.getSimulation();
        myRepCount++;
        myWriter.print(sim.getName());
        myWriter.print(",");
        myWriter.print(sim.getModel().getName());
        myWriter.print(",");
        myWriter.print(sim.getExperiment().getExperimentName());
        myWriter.print(",");
        myWriter.print(myRepCount);
        myWriter.print(",");
        writeResponses(model);
        myWriter.println();
    }

    protected void writeResponses(Model model){
        int n = 1;
        for(String name: myResponseNames){
            double x = getReplicationValue(model, name);
            myWriter.print(x);
            if (n < myResponseNames.size()){
                myWriter.print(",");
                n = n + 1;
            }
        }
    }

    private double getReplicationValue(Model m, String name) {
        ModelElement element = m.getModelElement(name);
        if (element != null){
            if (element instanceof ResponseVariable){
                return ((ResponseVariable) element).getWithinReplicationStatistic().getAverage();
            } else if (element instanceof Counter){
                return ((Counter) element).getValue();
            }
        }
        return Double.NaN;
    }
}
