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
import com.google.common.primitives.Doubles;
import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.Simulation;
import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.dbutil.EmbeddedDerbyDatabase;
import jsl.utilities.jsldbsrc.tables.records.*;
import jsl.utilities.reporting.JSL;
import jsl.utilities.statistic.BatchStatistic;
import jsl.utilities.statistic.Statistic;
import jsl.utilities.statistic.StatisticAccessorIfc;
import jsl.utilities.statistic.WeightedStatisticIfc;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jooq.*;
import tech.tablesaw.api.Table;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.*;

import static jsl.utilities.jsldbsrc.Tables.*;

/**
 * An embedded database that represents the statistical output from simulation runs. See the
 * file JSLDb.sql in the dbScripts directory for the structure of the database.
 */
public class JSLDb {

//    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public final static Path dbDir;
    public final static Path dbScriptsDir;

    static {
        File db = JSL.makeOutputSubDirectory("db");
        File dbScript = JSL.makeOutputSubDirectory("dbScript");
        dbDir = db.toPath();
        dbScriptsDir = dbScript.toPath();
        File jsldb = dbScriptsDir.resolve("JSLDb.sql").toFile();
        ClassLoader classLoader = JSLDb.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("JSLDb.sql");

        try {
            Files.copy(inputStream, dbScriptsDir.resolve("JSLDb.sql"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected final EmbeddedDerbyDatabase myDb;

    protected SimulationRunRecord myCurrentSimRunRecord;
    private String tblName;

    protected JSLDb(EmbeddedDerbyDatabase database) {
        if (database == null) {
            throw new IllegalArgumentException("The database cannot be null");
        }
        myDb = database;
        myCurrentSimRunRecord = null;
    }

    /**
     * Makes an empty JSLDb, if the database already exists it is written over
     *
     * @param dbName the name of the JSLDb instance, must not be null
     * @return the JSLDb
     */
    public static JSLDb makeEmptyJSLDbSquelchExceptions(String dbName) {
        try {
            return makeEmptyJSLDb(dbName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Makes an empty JSLDb, if the database already exists it is written over
     *
     * @param dbName the name of the JSLDb instance, must not be null
     * @return the JSLDb
     * @throws InvalidFormatException an exception
     * @throws SQLException           an exception
     * @throws IOException            an exception
     */
    public static JSLDb makeEmptyJSLDb(String dbName) throws IOException,
            SQLException, InvalidFormatException {
        FileUtils.deleteDirectory(dbDir.resolve(dbName).toFile());
        Path createScript = dbScriptsDir.resolve("JSLDb.sql");
        EmbeddedDerbyDatabase db = EmbeddedDerbyDatabase.createDb(dbName, dbDir)
                .withCreationScript(createScript)
                .connect();
        return new JSLDb(db);
    }

    /**
     * Connects to an already existing JSLDb with the given name
     *
     * @param dbName the name of the JSLDb instance
     * @return the JSLDb
     * @throws InvalidFormatException an exception
     * @throws SQLException           an exception
     * @throws IOException            an exception
     */
    public static JSLDb connect(String dbName) throws InvalidFormatException, SQLException, IOException {
        EmbeddedDerbyDatabase db = EmbeddedDerbyDatabase.connectDb(dbName, dbDir).connect();
        return new JSLDb(db);
    }

    /**
     * Connects to an already existing JSLDb with the given name
     *
     * @param dbName the name of the JSLDb instance
     * @return the JSLDb
     */
    public static JSLDb connectSquelchExceptions(String dbName) {
        try {
            return connect(dbName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Inserts a new SimulationRecord into the database
     *
     * @param sim the simulation to insert a run for
     */
    protected void insertSimulationRunRecord(Simulation sim) {
        if (sim == null) {
            throw new IllegalArgumentException("The simulation was null");
        }
        SimulationRunRecord record = myDb.getDSLContext().newRecord(SIMULATION_RUN);
        // make the mapping
        record.setSimName(sim.getName());
        record.setModelName(sim.getModel().getName());
        record.setExpName(sim.getExperimentName());
        long milliseconds = ZonedDateTime.now().toInstant().toEpochMilli();
        record.setExpStartTimeStamp(new Timestamp(milliseconds));
        record.setNumReps(sim.getNumberOfReplications());
        record.setLengthOfRep(sim.getLengthOfReplication());
        record.setLengthOfWarmUp(sim.getLengthOfWarmUp());
        record.setRepAllowedExecTime(sim.getMaximumAllowedExecutionTimePerReplication());
        record.setRepInitOption(sim.getReplicationInitializationOption());
        record.setResetStartStreamOption(sim.getResetStartStreamOption());
        record.setAntitheticOption(sim.getAntitheticOption());
        record.setAdvNextSubStreamOption(sim.getAdvanceNextSubStreamOption());
        record.setNumStreamAdvances(sim.getNumberOfStreamAdvancesPriorToRunning());
        record.store();
        myCurrentSimRunRecord = record;
    }

    /**
     * @return the current simulation run record or null
     */
    protected SimulationRunRecord getCurrentSimRunRecord() {
        return myCurrentSimRunRecord;
    }

    /**
     * Finalizes the current simulation record after the run is completed
     *
     * @param sim the current simulation
     */
    protected void finalizeCurrentSimulationRunRecord(Simulation sim) {
        myCurrentSimRunRecord.setLastRep(sim.getCurrentReplicationNumber());
        myCurrentSimRunRecord.setHasMoreReps(sim.hasMoreReplications());
        long milliseconds = ZonedDateTime.now().toInstant().toEpochMilli();
        myCurrentSimRunRecord.setExpEndTimeStamp(new Timestamp(milliseconds));
        myCurrentSimRunRecord.store();
    }

    /**
     * @return the current simulation run record id, the last inserted simulation run
     */
    protected Integer getCurrentSimRunRecordId() {
        return myCurrentSimRunRecord.getId();
    }

    /**
     * The list must be ordered according to parent-child, so that parents occur before
     * their children in the list
     *
     * @param elements the list of elements to add
     */
    protected void insertModelElements(List<ModelElement> elements) {
        List<ModelElementRecord> records = new ArrayList<>();
        for (ModelElement element : elements) {
            ModelElementRecord modelElementRecord = newModelElementRecord(element, getCurrentSimRunRecordId());
            if (modelElementRecord != null) {
                records.add(modelElementRecord);
            }
        }
        myDb.getDSLContext().batchStore(records).execute();
    }

    /**
     * Creates a new ModelElementRecord for the database
     *
     * @param modelElement the model element to get data from
     * @param simId        the simulation id
     * @return the created record
     */
    protected ModelElementRecord newModelElementRecord(ModelElement modelElement, Integer simId) {
        ModelElementRecord record = myDb.getDSLContext().newRecord(MODEL_ELEMENT);
        record.setSimRunIdFk(simId);
        record.setElementName(modelElement.getName());
        record.setElementId(modelElement.getId());
        record.setClassName(modelElement.getClass().getSimpleName());
        if (modelElement.getParentModelElement() != null) {
            record.setParentNameFk(modelElement.getParentModelElement().getName());
        }
        return record;
    }

    /**
     * Inserts within replication statistics for the supplied response variables
     *
     * @param responses the list of ResponseVariables to insert for, must not be null
     */
    protected void insertWithinRepResponses(List<ResponseVariable> responses) {
        if (responses == null) {
            throw new IllegalArgumentException("The list was null");
        }
        List<WithinRepStatRecord> records = new ArrayList<>();
        for (ResponseVariable rv : responses) {
            WithinRepStatRecord withinRepStatRecord = newWithinRepStatRecord(rv, getCurrentSimRunRecordId());
            if (withinRepStatRecord != null) {
                records.add(withinRepStatRecord);
            }
        }
        myDb.getDSLContext().batchStore(records).execute();
    }

    /**
     * Creates a new WithinRepStatRecord
     *
     * @param rv    the response variable to get data from
     * @param simId the simulation id
     * @return the created record
     */
    protected WithinRepStatRecord newWithinRepStatRecord(ResponseVariable rv, Integer simId) {
        if (simId == null) {
            throw new IllegalArgumentException("There is no current simulation run record.");
        }
        WithinRepStatRecord r = myDb.getDSLContext().newRecord(WITHIN_REP_STAT);
        r.setModelElementName(rv.getName());
        r.setSimRunIdFk(simId);
        r.setRepNum(rv.getExperiment().getCurrentReplicationNumber());
        WeightedStatisticIfc s = rv.getWithinReplicationStatistic();
        r.setStatName(s.getName());
        if (!Double.isNaN(s.getCount()) && !Double.isInfinite(s.getCount())){
            r.setStatCount(s.getCount());
        }
        if (!Double.isNaN(s.getAverage()) && !Double.isInfinite(s.getAverage())){
            r.setAverage(s.getAverage());
        }
        if (!Double.isNaN(s.getMin()) && !Double.isInfinite(s.getMin())){
            r.setMinimum(s.getMin());
        }
        if (!Double.isNaN(s.getMax()) && !Double.isInfinite(s.getMax())){
            r.setMaximum(s.getMax());
        }
        if (!Double.isNaN(s.getWeightedSum()) && !Double.isInfinite(s.getWeightedSum())){
            r.setWeightedSum(s.getWeightedSum());
        }
        if (!Double.isNaN(s.getSumOfWeights()) && !Double.isInfinite(s.getSumOfWeights())){
            r.setSumOfWeights(s.getSumOfWeights());
        }
        if (!Double.isNaN(s.getWeightedSumOfSquares()) && !Double.isInfinite(s.getWeightedSumOfSquares())){
            r.setWeightedSsq(s.getWeightedSumOfSquares());
        }
        if (!Double.isNaN(s.getLastValue()) && !Double.isInfinite(s.getLastValue())){
            r.setLastValue(s.getLastValue());
        }
        if (!Double.isNaN(s.getLastWeight()) && !Double.isInfinite(s.getLastWeight())){
            r.setLastWeight(s.getLastWeight());
        }
        return r;
    }

    /**
     * Inserts within replication statistics for the supplied counters
     *
     * @param counters the list of Counter to insert for, must not be null
     */
    protected void insertWithinRepCounters(List<Counter> counters) {
        if (counters == null) {
            throw new IllegalArgumentException("The list was null");
        }
        List<WithinRepCounterStatRecord> records = new ArrayList<>();
        for (Counter c : counters) {
            WithinRepCounterStatRecord statRecord = newWithinRepCounterStatRecord(c, getCurrentSimRunRecordId());
            if (statRecord != null) {
                records.add(statRecord);
            }
        }
        myDb.getDSLContext().batchStore(records).execute();
    }

    /**
     * Creates a WithinCounterStatRecord
     *
     * @param counter the counter to create from
     * @param simId   the simulation id
     * @return the created record
     */
    protected WithinRepCounterStatRecord newWithinRepCounterStatRecord(Counter counter, Integer simId) {
        if (simId == null) {
            throw new IllegalArgumentException("There is no current simulation run record.");
        }
        WithinRepCounterStatRecord r = myDb.getDSLContext().newRecord(WITHIN_REP_COUNTER_STAT);
        r.setModelElementName(counter.getName());
        r.setSimRunIdFk(simId);
        r.setRepNum(counter.getExperiment().getCurrentReplicationNumber());
        r.setStatName(counter.getName());
        if (!Double.isNaN(counter.getValue()) && !Double.isInfinite(counter.getValue())){
            r.setLastValue(counter.getValue());
        }
        return r;
    }

    /**
     * Inserts across replication statistics for the supplied response variables
     *
     * @param responses the list of ResponseVariables to insert for, must not be null
     */
    protected void insertAcrossRepResponses(List<ResponseVariable> responses) {
        if (responses == null) {
            throw new IllegalArgumentException("The response variable list was null");
        }
        List<AcrossRepStatRecord> records = new ArrayList<>();
        for (ResponseVariable rv : responses) {
            StatisticAccessorIfc s = rv.getAcrossReplicationStatistic();
            AcrossRepStatRecord statRecord = newAcrossRepStatRecord(rv.getName(), getCurrentSimRunRecordId(), s);
            if (statRecord != null) {
                records.add(statRecord);
            }
        }
        myDb.getDSLContext().batchStore(records).execute();
    }

    /**
     * Inserts across replication statistics for the supplied response variables
     *
     * @param counters the list of ResponseVariables to insert for, must not be null
     */
    protected void insertAcrossRepResponsesForCounters(List<Counter> counters) {
        if (counters == null) {
            throw new IllegalArgumentException("The counter list was null");
        }
        List<AcrossRepStatRecord> records = new ArrayList<>();
        for (Counter counter : counters) {
            StatisticAccessorIfc s = counter.getAcrossReplicationStatistic();
            AcrossRepStatRecord statRecord = newAcrossRepStatRecord(counter.getName(), getCurrentSimRunRecordId(), s);
            if (statRecord != null) {
                records.add(statRecord);
            }
        }
        myDb.getDSLContext().batchStore(records).execute();
    }

    /**
     * Creates an AcrossRepStatRecord into the database
     *
     * @param modelElementName the model element name
     * @param simId           the id of the simulation run
     * @param s               that statistics to insert
     * @return the created record
     */
    protected AcrossRepStatRecord newAcrossRepStatRecord(String modelElementName, Integer simId,
                                                         StatisticAccessorIfc s) {
        if (simId == null) {
            throw new IllegalArgumentException("Ther simulation id was null");
        }
        if (modelElementName == null) {
            throw new IllegalArgumentException("The model element name was null.");
        }
        if (s == null) {
            throw new IllegalArgumentException("There supplied StatisticAccessorIfc was null");
        }
        AcrossRepStatRecord r = myDb.getDSLContext().newRecord(ACROSS_REP_STAT);
        r.setModelElementName(modelElementName);
        r.setSimRunIdFk(simId);
        r.setStatName(s.getName());

        if (!Double.isNaN(s.getCount()) && !Double.isInfinite(s.getCount())){
            r.setStatCount(s.getCount());
        }
        if (!Double.isNaN(s.getAverage()) && !Double.isInfinite(s.getAverage())){
            r.setAverage(s.getAverage());
        }
        if (!Double.isNaN(s.getStandardDeviation()) && !Double.isInfinite(s.getStandardDeviation())){
            r.setStdDev(s.getStandardDeviation());
        }
        if (!Double.isNaN(s.getStandardError()) && !Double.isInfinite(s.getStandardError())){
            r.setStdErr(s.getStandardError());
        }
        if (!Double.isNaN(s.getHalfWidth()) && !Double.isInfinite(s.getHalfWidth())){
            r.setHalfWidth(s.getHalfWidth());
        }
        if (!Double.isNaN(s.getConfidenceLevel()) && !Double.isInfinite(s.getConfidenceLevel())){
            r.setConfLevel(s.getConfidenceLevel());
        }
        if (!Double.isNaN(s.getMin()) && !Double.isInfinite(s.getMin())){
            r.setMinimum(s.getMin());
        }
        if (!Double.isNaN(s.getMax()) && !Double.isInfinite(s.getMax())){
            r.setMaximum(s.getMax());
        }
        if (!Double.isNaN(s.getWeightedSum()) && !Double.isInfinite(s.getWeightedSum())){
            r.setWeightedSum(s.getWeightedSum());
        }
        if (!Double.isNaN(s.getSumOfWeights()) && !Double.isInfinite(s.getSumOfWeights())){
            r.setSumOfWeights(s.getSumOfWeights());
        }
        if (!Double.isNaN(s.getWeightedSumOfSquares()) && !Double.isInfinite(s.getWeightedSumOfSquares())){
            r.setWeightedSsq(s.getWeightedSumOfSquares());
        }
        if (!Double.isNaN(s.getDeviationSumOfSquares()) && !Double.isInfinite(s.getDeviationSumOfSquares())){
            r.setDevSsq(s.getDeviationSumOfSquares());
        }
        if (!Double.isNaN(s.getLastValue()) && !Double.isInfinite(s.getLastValue())){
            r.setLastValue(s.getLastValue());
        }
        if (!Double.isNaN(s.getLastWeight()) && !Double.isInfinite(s.getLastWeight())){
            r.setLastWeight(s.getLastWeight());
        }
        if (!Double.isNaN(s.getKurtosis()) && !Double.isInfinite(s.getKurtosis())){
            r.setKurtosis(s.getKurtosis());
        }
        if (!Double.isNaN(s.getSkewness()) && !Double.isInfinite(s.getSkewness())){
            r.setSkewness(s.getSkewness());
        }
        if (!Double.isNaN(s.getLag1Covariance()) && !Double.isInfinite(s.getLag1Covariance())){
            r.setLag1Cov(s.getLag1Covariance());
        }
        if (!Double.isNaN(s.getLag1Correlation()) && !Double.isInfinite(s.getLag1Correlation())){
            r.setLag1Corr(s.getLag1Correlation());
        }
        if (!Double.isNaN(s.getVonNeumannLag1TestStatistic()) && !Double.isInfinite(s.getVonNeumannLag1TestStatistic())){
            r.setVonNeumanLag1Stat(s.getVonNeumannLag1TestStatistic());
        }
        if (!Double.isNaN(s.getNumberMissing()) && !Double.isInfinite(s.getNumberMissing())){
            r.setNumMissingObs(s.getNumberMissing());
        }
        return r;
    }

    /**
     * Inserts batch statistics for the supplied map of response variables
     *
     * @param bmap the map of ResponseVariables to insert for, must not be null
     */
    protected void insertResponseVariableBatchStatistics(Map<ResponseVariable, BatchStatistic> bmap){
        if (bmap == null){
            throw new IllegalArgumentException("The batch statistic map was null");
        }
        List<BatchStatRecord> records = new ArrayList<>();
        for(Map.Entry<ResponseVariable, BatchStatistic> entry: bmap.entrySet()){
            ResponseVariable rv = entry.getKey();
            BatchStatistic bs = entry.getValue();
            BatchStatRecord batchStatRecord = newBatchStatRecord(rv, getCurrentSimRunRecordId(), bs);
            if (batchStatRecord != null){
                records.add(batchStatRecord);
            }
        }
        myDb.getDSLContext().batchStore(records).execute();
    }

    /**
     * Inserts batch statistics for the supplied map of response variables
     *
     * @param bmap the map of ResponseVariables to insert for, must not be null
     */
    protected void insertTimeWeightedBatchStatistics(Map<TimeWeighted, BatchStatistic> bmap){
        if (bmap == null){
            throw new IllegalArgumentException("The batch statistic map was null");
        }
        List<BatchStatRecord> records = new ArrayList<>();
        for(Map.Entry<TimeWeighted, BatchStatistic> entry: bmap.entrySet()){
            TimeWeighted tw = entry.getKey();
            BatchStatistic bs = entry.getValue();
            BatchStatRecord batchStatRecord = newBatchStatRecord(tw, getCurrentSimRunRecordId(), bs);
            if (batchStatRecord != null){
                records.add(batchStatRecord);
            }
        }
        myDb.getDSLContext().batchStore(records).execute();
    }

    /**
     * Creates an BatchStatRecord into the database
     *
     * @param rv the response variable, must not be null
     * @param simId           the id of the simulation run
     * @param s               that statistics to insert
     * @return the created record
     */
    protected BatchStatRecord newBatchStatRecord(ResponseVariable rv, Integer simId, BatchStatistic s){
        if (simId == null) {
            throw new IllegalArgumentException("Ther simulation id was null");
        }
        if (rv == null) {
            throw new IllegalArgumentException("The model element name was null.");
        }
        if (s == null) {
            throw new IllegalArgumentException("There supplied StatisticAccessorIfc was null");
        }
        BatchStatRecord r = myDb.getDSLContext().newRecord(BATCH_STAT);
        r.setModelElementName(rv.getName());
        r.setSimRunIdFk(simId);
        r.setStatName(s.getName());
        r.setRepNum(rv.getExperiment().getCurrentReplicationNumber());

        if (!Double.isNaN(s.getCount()) && !Double.isInfinite(s.getCount())){
            r.setStatCount(s.getCount());
        }
        if (!Double.isNaN(s.getAverage()) && !Double.isInfinite(s.getAverage())){
            r.setAverage(s.getAverage());
        }
        if (!Double.isNaN(s.getStandardDeviation()) && !Double.isInfinite(s.getStandardDeviation())){
            r.setStdDev(s.getStandardDeviation());
        }
        if (!Double.isNaN(s.getStandardError()) && !Double.isInfinite(s.getStandardError())){
            r.setStdErr(s.getStandardError());
        }
        if (!Double.isNaN(s.getHalfWidth()) && !Double.isInfinite(s.getHalfWidth())){
            r.setHalfWidth(s.getHalfWidth());
        }
        if (!Double.isNaN(s.getConfidenceLevel()) && !Double.isInfinite(s.getConfidenceLevel())){
            r.setConfLevel(s.getConfidenceLevel());
        }
        if (!Double.isNaN(s.getMin()) && !Double.isInfinite(s.getMin())){
            r.setMinimum(s.getMin());
        }
        if (!Double.isNaN(s.getMax()) && !Double.isInfinite(s.getMax())){
            r.setMaximum(s.getMax());
        }
        if (!Double.isNaN(s.getWeightedSum()) && !Double.isInfinite(s.getWeightedSum())){
            r.setWeightedSum(s.getWeightedSum());
        }
        if (!Double.isNaN(s.getSumOfWeights()) && !Double.isInfinite(s.getSumOfWeights())){
            r.setSumOfWeights(s.getSumOfWeights());
        }
        if (!Double.isNaN(s.getWeightedSumOfSquares()) && !Double.isInfinite(s.getWeightedSumOfSquares())){
            r.setWeightedSsq(s.getWeightedSumOfSquares());
        }
        if (!Double.isNaN(s.getDeviationSumOfSquares()) && !Double.isInfinite(s.getDeviationSumOfSquares())){
            r.setDevSsq(s.getDeviationSumOfSquares());
        }
        if (!Double.isNaN(s.getLastValue()) && !Double.isInfinite(s.getLastValue())){
            r.setLastValue(s.getLastValue());
        }
        if (!Double.isNaN(s.getLastWeight()) && !Double.isInfinite(s.getLastWeight())){
            r.setLastWeight(s.getLastWeight());
        }
        if (!Double.isNaN(s.getKurtosis()) && !Double.isInfinite(s.getKurtosis())){
            r.setKurtosis(s.getKurtosis());
        }
        if (!Double.isNaN(s.getSkewness()) && !Double.isInfinite(s.getSkewness())){
            r.setSkewness(s.getSkewness());
        }
        if (!Double.isNaN(s.getLag1Covariance()) && !Double.isInfinite(s.getLag1Covariance())){
            r.setLag1Cov(s.getLag1Covariance());
        }
        if (!Double.isNaN(s.getLag1Correlation()) && !Double.isInfinite(s.getLag1Correlation())){
            r.setLag1Corr(s.getLag1Correlation());
        }
        if (!Double.isNaN(s.getVonNeumannLag1TestStatistic()) && !Double.isInfinite(s.getVonNeumannLag1TestStatistic())){
            r.setVonNeumanLag1Stat(s.getVonNeumannLag1TestStatistic());
        }
        if (!Double.isNaN(s.getNumberMissing()) && !Double.isInfinite(s.getNumberMissing())){
            r.setNumMissingObs(s.getNumberMissing());
        }
        if (!Double.isNaN(s.getMinBatchSize()) && !Double.isInfinite(s.getMinBatchSize())){
            r.setMinBatchSize((double) s.getMinBatchSize());
        }
        if (!Double.isNaN(s.getMinNumberOfBatches()) && !Double.isInfinite(s.getMinNumberOfBatches())){
            r.setMinNumBatches((double) s.getMinNumberOfBatches());
        }
        if (!Double.isNaN(s.getMaxNumberOfBatchesMultiple()) && !Double.isInfinite(s.getMaxNumberOfBatchesMultiple())){
            r.setMaxNumBatchesMultiple((double) s.getMaxNumberOfBatchesMultiple());
        }
        if (!Double.isNaN(s.getMaxNumBatches()) && !Double.isInfinite(s.getMaxNumBatches())){
            r.setMaxNumBatches((double) s.getMaxNumBatches());
        }
        if (!Double.isNaN(s.getNumRebatches()) && !Double.isInfinite(s.getNumRebatches())){
            r.setNumRebatches((double) s.getNumRebatches());
        }
        if (!Double.isNaN(s.getCurrentBatchSize()) && !Double.isInfinite(s.getCurrentBatchSize())){
            r.setCurrentBatchSize((double) s.getCurrentBatchSize());
        }
        if (!Double.isNaN(s.getAmountLeftUnbatched()) && !Double.isInfinite(s.getAmountLeftUnbatched())){
            r.setAmtUnbatched(s.getAmountLeftUnbatched());
        }
        if (!Double.isNaN(s.getTotalNumberOfObservations()) && !Double.isInfinite(s.getTotalNumberOfObservations())){
            r.setTotalNumObs(s.getTotalNumberOfObservations());
        }
        return r;

    }
    /**
     *
     * @param simId the identifier of the simulation record
     * @return the record or null
     */
    public final SimulationRunRecord getSimulationRunRecord(Integer simId) {
        SimulationRunRecord simulationRunRecord = myDb.getDSLContext()
                .selectFrom(SIMULATION_RUN).where(SIMULATION_RUN.ID.eq(simId)).fetchOne();
        return simulationRunRecord;
    }

    /**
     *
     * @return the simulation run records as a jooq Result
     */
    public final Result<SimulationRunRecord> getSimulationRunRecords() {
        Result<SimulationRunRecord> simRecords = myDb.getDSLContext()
                .selectFrom(SIMULATION_RUN).orderBy(SIMULATION_RUN.ID,
                        SIMULATION_RUN.SIM_NAME,
                        SIMULATION_RUN.MODEL_NAME,
                        SIMULATION_RUN.EXP_NAME,
                        SIMULATION_RUN.EXP_START_TIME_STAMP,
                        SIMULATION_RUN.EXP_END_TIME_STAMP).fetch();
        return simRecords;
    }

    /**
     *
     * @return the model element records as a jooq Result
     */
    public final Result<ModelElementRecord> getModelElementRecords() {
        Result<ModelElementRecord> modelElementRecords = myDb.getDSLContext()
                .selectFrom(MODEL_ELEMENT).orderBy(MODEL_ELEMENT.ELEMENT_ID).fetch();
        return modelElementRecords;
    }

    /**
     *
     * @param model
     * @return a BiMap linking model element records with corresponding ModelElements
     */
    public final BiMap<ModelElementRecord, ModelElement> getModelElementRecordBiMap(Model model){
        if (model == null) {
            throw new IllegalArgumentException("The model was null.");
        }
        BiMap<ModelElementRecord, ModelElement> biMap = HashBiMap.create();
        Result<ModelElementRecord> elementRecords = getModelElementRecords();
        for(ModelElementRecord r: elementRecords){
            ModelElement modelElement = model.getModelElement(r.getElementName());
            if (modelElement != null){
                biMap.put(r, modelElement);
            }
        }
        return biMap;
    }

    /**
     *
     * @return the within replication statistics as a jooq Result
     */
    public final Result<WithinRepStatRecord> getWithinRepStatRecords() {
        Result<WithinRepStatRecord> withinRepStatRecords = myDb.getDSLContext()
                .selectFrom(WITHIN_REP_STAT).orderBy(WITHIN_REP_STAT.SIM_RUN_ID_FK,
                        WITHIN_REP_STAT.ID,
                        WITHIN_REP_STAT.MODEL_ELEMENT_NAME,
                        WITHIN_REP_STAT.REP_NUM).fetch();
        return withinRepStatRecords;
    }

    /**
     *
     * @return the within replication statistics as a JDBC ResultSet
     */
    public final ResultSet getWithinRepStatRecordsAsResultSet(){
        ResultSet resultSet = getWithinRepStatRecords().intoResultSet();
        return resultSet;
    }

    /**
     *
     * @param simId the id of the simulation
     * @param responseName the response name
     * @return the across replication statistics as a Statistic
     */
    public Statistic getAcrossRepStatistic(Integer simId, String responseName){
        List<Double> averages = myDb.getDSLContext()
                .select(WITHIN_REP_STAT.AVERAGE)
                .from(WITHIN_REP_STAT)
                .where(WITHIN_REP_STAT.SIM_RUN_ID_FK.eq(simId)
                        .and(WITHIN_REP_STAT.MODEL_ELEMENT_NAME.eq(responseName))).fetch(WITHIN_REP_STAT.AVERAGE);
        Statistic s = new Statistic(Doubles.toArray(averages));
        s.setName(responseName);
        return s;
    }

    /**
     *
     * @param simId the id of the simulation
     * @param counterName the counter name
     * @return the across replication statistics as a Statistic
     */
    public Statistic getAcrossRepCounterStatistic(Integer simId, String counterName){
        List<Double> averages = myDb.getDSLContext()
                .select(WITHIN_REP_COUNTER_STAT.LAST_VALUE)
                .from(WITHIN_REP_COUNTER_STAT)
                .where(WITHIN_REP_COUNTER_STAT.SIM_RUN_ID_FK.eq(simId)
                        .and(WITHIN_REP_COUNTER_STAT.MODEL_ELEMENT_NAME.eq(counterName))).fetch(WITHIN_REP_COUNTER_STAT.LAST_VALUE);
        Statistic s = new Statistic(Doubles.toArray(averages));
        s.setName(counterName);
        return s;
    }

    /**
     * @param responseName the name of the response variable
     * @return the within replication averages (simRunId, exp_name, response_name, rep_num, avg)
     */
    public final Result<Record5<Integer, String, String, Integer, Double>> getWithinRepAveragesAsResultSet(String responseName){
        Result<Record5<Integer, String, String, Integer, Double>> fetch = myDb.getDSLContext()
                .select(WITHIN_REP_STAT.SIM_RUN_ID_FK, SIMULATION_RUN.EXP_NAME, WITHIN_REP_STAT.MODEL_ELEMENT_NAME,
                        WITHIN_REP_STAT.REP_NUM, WITHIN_REP_STAT.AVERAGE)
                .from(WITHIN_REP_STAT.join(SIMULATION_RUN).on(
                        WITHIN_REP_STAT.SIM_RUN_ID_FK.eq(SIMULATION_RUN.ID)))
                .where(WITHIN_REP_STAT.MODEL_ELEMENT_NAME.eq(responseName))
                .orderBy(WITHIN_REP_STAT.SIM_RUN_ID_FK, WITHIN_REP_STAT.REP_NUM).fetch();
        return fetch;
    }

    /** Builds a map to use with MultipleComparisonAnalyzer
     *
     * @param responseName the name of the response variable
     * @return a Map with key as experiment name and replication averages in the array
     */
    public final Map<String, double[]> getWithRepAveragesAsMap(String responseName){
        Result<Record5<Integer, String, String, Integer, Double>> resultSet = getWithinRepAveragesAsResultSet(responseName);
        Map<String, List<Double>> rMap = resultSet.intoGroups(SIMULATION_RUN.EXP_NAME, WITHIN_REP_STAT.AVERAGE);
        Map<String, double[]> cMap = new LinkedHashMap<>();
        for(String s: rMap.keySet()){
            List<Double> doubles = rMap.get(s);
            double[] a = Doubles.toArray(doubles);
            cMap.put(s,a );
        }
        return cMap;
    }

    /**
     * @param counterName the name of the counter variable
     * @return the ending replication count (simRunId, exp_name, counter_name, rep_num, count)
     */
    public final Result<Record5<Integer, String, String, Integer, Double>> getEndReplicationCountsAsResultSet(String counterName){
        Result<Record5<Integer, String, String, Integer, Double>> fetch = myDb.getDSLContext()
                .select(WITHIN_REP_COUNTER_STAT.SIM_RUN_ID_FK, SIMULATION_RUN.EXP_NAME, WITHIN_REP_COUNTER_STAT.MODEL_ELEMENT_NAME,
                        WITHIN_REP_COUNTER_STAT.REP_NUM, WITHIN_REP_COUNTER_STAT.LAST_VALUE)
                .from(WITHIN_REP_COUNTER_STAT.join(SIMULATION_RUN).on(
                        WITHIN_REP_COUNTER_STAT.SIM_RUN_ID_FK.eq(SIMULATION_RUN.ID)))
                .where(WITHIN_REP_COUNTER_STAT.MODEL_ELEMENT_NAME.eq(counterName))
                .orderBy(WITHIN_REP_COUNTER_STAT.SIM_RUN_ID_FK, WITHIN_REP_COUNTER_STAT.REP_NUM).fetch();
        return fetch;
    }

    /** Builds a map to use with MultipleComparisonAnalyzer
     *
     * @param counterName the name of the counter variable
     * @return a Map with key as experiment name and replication counts in the array
     */
    public final Map<String, double[]> getEndReplicationCountsAsMap(String counterName){
        Result<Record5<Integer, String, String, Integer, Double>> resultSet = getEndReplicationCountsAsResultSet(counterName);
        Map<String, List<Double>> rMap = resultSet.intoGroups(SIMULATION_RUN.EXP_NAME, WITHIN_REP_COUNTER_STAT.LAST_VALUE);
        Map<String, double[]> cMap = new LinkedHashMap<>();
        for(String s: rMap.keySet()){
            List<Double> doubles = rMap.get(s);
            double[] a = Doubles.toArray(doubles);
            cMap.put(s,a );
        }
        return cMap;
    }

    /**
     * @param tblName the name of the Tablesaw table
     * @return the within replication statistics as a Tablesaw Table
     */
    public final Table getWithinRepStatRecordsAsTablesawTable(String tblName) throws SQLException {
        return Table.read().db(getWithinRepStatRecordsAsResultSet(), tblName);
    }

    /** Squelches the SQLException
     * @param tblName the name of the Tablesaw table
     * @return the within replication statistics as a Tablesaw Table or null
     */
    public final Table getWithinRepStatRecordsAsTablesawTableNE(String tblName) {

        Table table = null;
        try {
            table = Table.read().db(getWithinRepStatRecordsAsResultSet(), tblName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return table;
    }

    /**
     * @return a jooq Result of across replication statistics for all simulation runs
     */
    public final Result<AcrossRepStatRecord> getAcrossRepStatRecords() {
        Result<AcrossRepStatRecord> acrossRepStatRecords = myDb.getDSLContext()
                .selectFrom(ACROSS_REP_STAT).orderBy(ACROSS_REP_STAT.SIM_RUN_ID_FK,
                        ACROSS_REP_STAT.ID,
                        ACROSS_REP_STAT.MODEL_ELEMENT_NAME,
                        ACROSS_REP_STAT.STAT_NAME).fetch();
        return acrossRepStatRecords;
    }

    /**
     *
     * @return the across replication statistics as a JDBC ResultSet
     */
    public final ResultSet getAcrossRepStatRecordsAsResultSet(){
        return getAcrossRepStatRecords().intoResultSet();
    }

    /**
     * @param tblName the name of the Tablesaw table
     * @return the across replication statistics as a Tablesaw Table
     */
    public final Table getAcrossRepStatRecordsAsTablesawTable(String tblName) throws SQLException {
        return Table.read().db(getAcrossRepStatRecordsAsResultSet(), tblName);
    }

    /** Squelches the SQLException
     * @param tblName the name of the Tablesaw table
     * @return the across replication statistics as a Tablesaw Table or null
     */
    public final Table getAcrossRepStatRecordsAsTablesawTableNE(String tblName) {

        Table table = null;
        try {
            table = Table.read().db(getAcrossRepStatRecordsAsResultSet(), tblName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return table;
    }

    /**
     * @return a jooq Result of batch statistics
     */
    public final Result<BatchStatRecord> getBatchStatRecords() {
        Result<BatchStatRecord> batchStatRecords = myDb.getDSLContext()
                .selectFrom(BATCH_STAT).orderBy(BATCH_STAT.SIM_RUN_ID_FK,
                        BATCH_STAT.ID,
                        BATCH_STAT.MODEL_ELEMENT_NAME,
                        BATCH_STAT.STAT_NAME, BATCH_STAT.REP_NUM).fetch();
        return batchStatRecords;
    }

    /**
     *
     * @return the batch statistics as a JDBC ResultSet
     */
    public final ResultSet getBatchStatRecordsAsResultSet(){
        return getBatchStatRecords().intoResultSet();
    }

    /**
     * @param tblName the name of the Tablesaw table
     * @return the batch statistics as a Tablesaw Table
     */
    public final Table getBatchStatRecordsAsTablesawTable(String tblName) throws SQLException {
        return Table.read().db(getBatchStatRecordsAsResultSet(), tblName);
    }

    /** Squelches the SQLException
     * @param tblName the name of the Tablesaw table
     * @return the batch statistics as a Tablesaw Table or null
     */
    public final Table getBatchStatRecordsAsTablesawTableNE(String tblName) {

        Table table = null;
        try {
            table = Table.read().db(getBatchStatRecordsAsResultSet(), tblName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return table;
    }

    /**
     * @return a jooq Result of across replication statistics for current simulation run
     */
    public final Result<AcrossRepStatRecord> getAcrossRepStatRecordsForCurrentSimulation() {
        Result<AcrossRepStatRecord> acrossRepStatRecords = myDb.getDSLContext()
                .selectFrom(ACROSS_REP_STAT)
                .where(ACROSS_REP_STAT.SIM_RUN_ID_FK.eq(getCurrentSimRunRecordId()))
                .orderBy(ACROSS_REP_STAT.SIM_RUN_ID_FK,
                        ACROSS_REP_STAT.ID,
                        ACROSS_REP_STAT.MODEL_ELEMENT_NAME,
                        ACROSS_REP_STAT.STAT_NAME).fetch();
        return acrossRepStatRecords;
    }

    /**
     *
     * @return a map keyed by model element name with the across rep stat record as the value
     */
    public final Map<String, AcrossRepStatRecord> getAcrossRepStatRecordsMapForCurrentSimulation(){
        Map<String, AcrossRepStatRecord> map = getAcrossRepStatRecordsForCurrentSimulation()
                .intoMap(ACROSS_REP_STAT.MODEL_ELEMENT_NAME);
        return map;
    }

    /**
     * The name of the database
     *
     * @return the name
     */
    public final String getName() {
        return myDb.getName();
    }

    public final Path getDirectory() {
        return myDb.getDirectory();
    }

    /**
     * A URL representation of the embedded database
     *
     * @return the URL
     */
    public final String getURL() {
        return myDb.getURL();
    }

    /**
     * @return the path representation for the database
     */
    public final Path getDBPath() {
        return myDb.getDBPath();
    }

    /**
     * @return the sql dialect for the database.  Here should be derby
     */
    public final SQLDialect getSQLDialect() {
        return myDb.getSQLDialect();
    }

    /**
     * @return the path to the tables only script
     */
    public final Path getCreationScriptPath() {
        return myDb.getCreationScriptPath();
    }

    /**
     * Returns the names of the tables in the current database
     *
     * @return a list of the names of the tables as strings
     */
    public final List<String> getTableNames() {
        return myDb.getTableNames();
    }

    /**
     * Checks if tables exist in the database
     *
     * @return true if it exists
     */
    public final boolean hasTables() {
        return myDb.hasTables();
    }

    /**
     * Checks if the supplied table exists in the database
     *
     * @param table a string representing the name of the table
     * @return true if it exists
     */
    public final boolean tableExists(String table) {
        return myDb.tableExists(table);
    }

    /**
     * @param tableName the name of the table to write
     * @param out       the output file for the writing
     */
    public final void writeTableAsCSV(String tableName, PrintWriter out) {
        myDb.writeTableAsCSV(tableName, out);
    }

    /**
     * Displays the named table on the console
     *
     * @param tableName the name of the table to display
     */
    public final void displayTableAsCSV(String tableName) {
        myDb.displayTableAsCSV(tableName);
    }

    /**
     * Writes the table in pretty text to the file
     *
     * @param tableName the name of the table to write
     * @param out       the output file to write to
     */
    public final void writeTableAsText(String tableName, PrintWriter out) {
        myDb.writeTableAsText(tableName, out);
    }

    /**
     * Displays the name table on the console
     *
     * @param tableName the name of the table to write
     */
    public final void displayTableAsText(String tableName) {
        myDb.displayTableAsText(tableName);
    }

    /**
     * Writes all user defined tables to the output
     *
     * @param out the place to write to
     */
    public final void writeAllTablesAsText(PrintWriter out) {
        myDb.writeAllTablesAsText(out);
    }

    /**
     * Displays all the user defined tables as text on the console
     */
    public final void displayAllTablesAsText() {
        myDb.displayAllTablesAsText();
    }

    /**
     * @param table the table to check
     * @return true if the table has no data in the result
     */
    public final boolean isTableEmpty(String table) {
        return myDb.isTableEmpty(table);
    }

    /**
     * @return true if at least one table has data
     */
    public final boolean hasData() {
        return myDb.hasData();
    }

    /**
     * @return true if all tables are empty
     */
    public final boolean areAllTablesEmpty() {
        return myDb.areAllTablesEmpty();
    }

    /**
     * Writes all tables of the database in the directory, naming each output
     * file the name of each table
     *
     * @param pathToOutPutDirectory
     */
    public final void writeAllTablesAsCSV(Path pathToOutPutDirectory) throws IOException {
        myDb.writeAllTablesAsCSV(pathToOutPutDirectory);
    }

    /**
     * @return the jooq DSLContext for the database
     */
    public final DSLContext getDSLContext() {
        return myDb.getDSLContext();
    }

    /**
     * Writes all the tables to an Excel workbook, uses name of database, uses the working directory
     */
    public final void writeDbToExcelWorkbook() throws IOException {
        myDb.writeDbToExcelWorkbook();
    }

    /**
     * Writes all the tables to an Excel workbook, uses name of database
     *
     * @param wbDirectory directory of the workbook, if null uses the working directory
     */
    public final void writeDbToExcelWorkbook(Path wbDirectory) throws IOException {
        myDb.writeDbToExcelWorkbook(wbDirectory);
    }

    /**
     * Writes all the tables to an Excel workbook uses the working directory
     *
     * @param wbName name of the workbook, if null uses name of database
     */
    public final void writeDbToExcelWorkbook(String wbName) throws IOException {
        myDb.writeDbToExcelWorkbook(wbName);
    }

    /**
     * Writes all the tables to an Excel workbook
     *
     * @param wbName      name of the workbook, if null uses name of database
     * @param wbDirectory directory of the workbook, if null uses the working directory
     */
    public final void writeDbToExcelWorkbook(String wbName, Path wbDirectory) throws IOException {
        myDb.writeDbToExcelWorkbook(wbName, wbDirectory);
    }

    /**
     *  Turns on JooQ Default execute SQL logging
     */
    public final void turnOnJooQDefaultExecutionLogging() {
        myDb.turnOnJooQDefaultExecutionLogging();
    }

    /**
     *  Turns off JooQ Default execute SQL logging
     */
    public final void turnOffJooQDefaultExecutionLogging() {
        myDb.turnOffJooQDefaultExecutionLogging();
    }
}
