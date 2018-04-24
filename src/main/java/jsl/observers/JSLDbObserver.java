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

package jsl.observers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.Simulation;
import jsl.modeling.StatisticalBatchingElement;
import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.jsldbsrc.tables.records.ModelElementRecord;
import jsl.utilities.math.Sets;
import jsl.utilities.reporting.JSL;
import jsl.utilities.statistic.BatchStatistic;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jooq.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;

/**
 *  Used by Simulation to attach an embedded database to collect statistics
 */
public class JSLDbObserver extends ModelElementObserver {

    protected final Simulation mySimulation;
    protected final Model myModel;
    protected final JSLDb myJSLDb;
    protected final boolean myClearDbFlag;

    /** Uses the simulation name for the database name (without whitespace). The database will be cleared
     *
     * @param simulation the simulation, must not null
     * @throws InvalidFormatException an exception
     * @throws SQLException  an exception
     * @throws IOException an exception
     */
    public JSLDbObserver(Simulation simulation)
            throws InvalidFormatException, SQLException, IOException {
        this(simulation, true, null);
    }

    /**
     *
     * @return the underlying JSLDb
     */
    public final JSLDb getJSLDb(){
        return myJSLDb;
    }

    /** Uses the simulation name for the database name (without whitespace)
     *
     * @param simulation the simulation, must not null
     * @param clearDbFlag whether or not the database should be cleared before the run, true by default
     * @throws InvalidFormatException an exception
     * @throws SQLException  an exception
     * @throws IOException an exception
     */
    public JSLDbObserver(Simulation simulation, boolean clearDbFlag)
            throws InvalidFormatException, SQLException, IOException {
        this(simulation, clearDbFlag, null);
    }

    /**
     *
     * @param simulation the simulation, must not null
     * @param clearDbFlag whether or not the database should be cleared before the run, true by default
     * @param dbName  if the name has whitespace it will be removed
     * @throws InvalidFormatException an exception
     * @throws SQLException  an exception
     * @throws IOException an exception
     */
    public JSLDbObserver(Simulation simulation, boolean clearDbFlag, String dbName)
            throws InvalidFormatException, SQLException, IOException {
        if (simulation == null) {
            throw new IllegalArgumentException("The simulation was null");
        }
        mySimulation = simulation;
        myModel = simulation.getModel();
        myClearDbFlag = clearDbFlag;
        if (dbName == null){
            dbName = "JSLDb_" + simulation.getName();
        }
        dbName = dbName.replaceAll("\\s+","");
        // make the db
        Path path = JSLDb.dbDir.resolve(dbName);
        if (Files.exists(path)) {
            // the database exists, check to clear it
            if (clearDbFlag){
                myJSLDb = JSLDb.makeEmptyJSLDb(dbName);
            } else {
                myJSLDb = JSLDb.connect(dbName);
            }
        } else {
            // does not exist at all, just make it
            myJSLDb = JSLDb.makeEmptyJSLDb(dbName);
        }
        myModel.addObserver(this);
    }

    @Override
    protected void beforeExperiment(ModelElement m, Object arg) {
        super.beforeExperiment(m, arg);
        // insert the new simulation run into the database
        myJSLDb.insertSimulationRunRecord(mySimulation);
        // add the model elements associated with this run to the database
        List<ModelElement> currMEList = myModel.getModelElements();
        myJSLDb.insertModelElements(currMEList);
    }

    @Override
    protected void beforeReplication(ModelElement m, Object arg) {
        super.beforeReplication(m, arg);
    }

    @Override
    protected void afterReplication(ModelElement m, Object arg) {
        super.afterReplication(m, arg);
        List<ResponseVariable> rvs = myModel.getResponseVariables();
        List<Counter> counters = myModel.getCounters();
        myJSLDb.insertWithinRepResponses(rvs);
        myJSLDb.insertWithinRepCounters(counters);

        Optional<StatisticalBatchingElement> sbe = mySimulation.getStatisticalBatchingElement();

        if(sbe.isPresent()){
            // insert the statistics from the batching
            Map<ResponseVariable, BatchStatistic> rmap = sbe.get().getAllResponseVariableBatchStatisticsAsMap();
            Map<TimeWeighted, BatchStatistic> twmap = sbe.get().getAllTimeWeightedBatchStatisticsAsMap();
            myJSLDb.insertResponseVariableBatchStatistics(rmap);
            myJSLDb.insertTimeWeightedBatchStatistics(twmap);
        }
    }

    @Override
    protected void afterExperiment(ModelElement m, Object arg) {
        super.afterExperiment(m, arg);
        myJSLDb.finalizeCurrentSimulationRunRecord(mySimulation);
        List<ResponseVariable> rvs = myModel.getResponseVariables();
        List<Counter> counters = myModel.getCounters();
        myJSLDb.insertAcrossRepResponses(rvs);
        myJSLDb.insertAcrossRepResponsesForCounters(counters);
    }
}
