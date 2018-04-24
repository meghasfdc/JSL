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

import java.io.File;
import java.util.List;

import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.Simulation;
import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.utilities.statistic.Statistic;

/** Represents a comma separated value file for experiment data (across
 *  replication data)
 * 
 *  SimName, ModelName, ExpName, RepNum, ResponseType, ResponseID, ResponseName, ..
 *  then the header from StatisticAccessorIfc.getCSVStatisticHeader()
 * 
 *  Captures all ResponseVariables, TimeWeighted variables, and Counters
 *
 */
public class CSVExperimentReport extends CSVReport {

    /**
     * @param name
    
     */
    public CSVExperimentReport(String name) {
        this(null, name);
    }

    /**
     * @param directory
     * @param name
     */
    public CSVExperimentReport(File directory, String name) {
        super(directory, name);
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
        myWriter.print("ResponseType,");
        myWriter.print("ResponseID,");
        myWriter.print("ResponseName,");
        Statistic s = new Statistic();
        myWriter.print(s.getCSVStatisticHeader());
        myWriter.println();
    }

    @Override
    protected void afterExperiment(ModelElement m, Object arg) {

        Model model = m.getModel();
        Simulation sim = m.getSimulation();

        List<ResponseVariable> rvs = model.getResponseVariables();

        for (ResponseVariable rv : rvs) {
            if (rv.getDefaultReportingOption()) {
                myWriter.print(sim.getName());
                myWriter.print(",");
                myWriter.print(sim.getModel().getName());
                myWriter.print(",");
                myWriter.print(sim.getExperiment().getExperimentName());
                myWriter.print(",");
                myWriter.print(rv.getClass().getSimpleName() + ",");
                myWriter.print(rv.getId() + ",");
                myWriter.print(rv.getName() + ",");
                myWriter.print(rv.getAcrossReplicationStatistic().getCSVStatistic());
                myWriter.println();
            }
        }

        List<Counter> counters = model.getCounters();

        for (Counter c : counters) {
            if (c.getDefaultReportingOption()) {
                myWriter.print(sim.getName());
                myWriter.print(",");
                myWriter.print(sim.getModel().getName());
                myWriter.print(",");
                myWriter.print(sim.getExperiment().getExperimentName());
                myWriter.print(",");
                myWriter.print(c.getClass().getSimpleName() + ",");
                myWriter.print(c.getId() + ",");
                myWriter.print(c.getName() + ",");
                myWriter.print(c.getAcrossReplicationStatistic().getCSVStatistic());
                myWriter.println();
            }
        }
    }
}
