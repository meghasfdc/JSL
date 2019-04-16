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

package jsl.utilities.reporting;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.primitives.Doubles;
import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.Simulation;
import jsl.modeling.StatisticalBatchingElement;
import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.observers.ModelElementObserver;
import jsl.utilities.dbutil.Database;
import jsl.utilities.dbutil.DatabaseFactory;
import jsl.utilities.dbutil.DatabaseIfc;
import jsl.utilities.jsldbsrc.tables.records.*;
import jsl.utilities.statistic.*;
import org.jooq.DSLContext;
import org.jooq.Record12;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameStyle;
import org.jooq.exception.DataAccessException;
import tech.tablesaw.api.Table;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.*;

import static jsl.utilities.jsldbsrc.Tables.*;
import static org.jooq.impl.DSL.*;

/**
 * An embedded database that represents the statistical output from simulation runs. See the
 * file JSLDb.sql in the dbScripts directory for the structure of the database.
 * Assumes that a schema called getJSLSchemaName() exists in the database.
 * <p>
 * If the supplied database does not have a schema called getJSLSchemaName(), then
 * the schema is created and the appropriate JSL related database artifacts are installed
 * in the schema. If a schema called getJSLSchemaName() already exists in the database, then the
 * assumption is that the schema is appropriately configured to hold JSL related
 * database artifacts.  The default schema of the database is set to getJSLSchemaName().
 */
public class JSLDatabase {

//    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public final static Path dbDir;
    public final static Path dbScriptsDir;

    private static final List<String> JSLTableNames = Arrays.asList("batch_stat", "within_rep_counter_stat",
            "across_rep_stat", "within_rep_stat", "model_element", "simulation_run");

    private static final List<String> JSLViewNames = Arrays.asList("within_rep_response_view",
            "within_rep_counter_view", "across_rep_view", "batch_stat_view", "within_rep_view", "pw_diff_within_rep_view");

    private static String jslSchemaName = "JSL_DB";

    static {
        File db = JSL.makeOutputSubDirectory("db");
        File dbScript = JSL.makeOutputSubDirectory("dbScript");
        dbDir = db.toPath();
        dbScriptsDir = dbScript.toPath();
        File jsldb = dbScriptsDir.resolve("JSLDb.sql").toFile();
        ClassLoader classLoader = JSLDatabase.class.getClassLoader();
        InputStream jslcreate = classLoader.getResourceAsStream("JSLDb.sql");
        InputStream jsldrop = classLoader.getResourceAsStream("JSLDbDropScript.sql");
        try {
            Files.copy(jslcreate, dbScriptsDir.resolve("JSLDb.sql"),
                    StandardCopyOption.REPLACE_EXISTING);
            Files.copy(jsldrop, dbScriptsDir.resolve("JSLDbDropScript.sql"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final DatabaseIfc myDb;
    private final Simulation mySimulation;
    private boolean myClearDbFlag = true;
    protected SimulationRunRecord myCurrentSimRunRecord;
    private String tblName;
    private SimulationDatabaseObserver mySimulationObserver;

    /**
     * Creates an instance of a JSLDatabase. Assumes that a schema called getJSLSchemaName() exists
     * in the database. If the supplied database does not have a schema called getJSLSchemaName(), then
     * the schema is created and the appropriate JSL related database artifacts are installed
     * in the schema. If a schema called getJSLSchemaName() already exists in the database, then the
     * assumption is that the schema is appropriately configured to hold JSL related
     * database artifacts.  The default schema of the database is set to getJSLSchemaName()
     * <p>
     * The clear database option is set to false.
     *
     * @param database   the database to use for setting up the JSLDatabase, must not be null
     * @param simulation the simulation to be associated with the JSLDatabase, must not be null
     */
    public JSLDatabase(DatabaseIfc database, Simulation simulation) {
        this(database, simulation, false);
    }

    /**
     * Creates an instance of a JSLDatabase. Assumes that a schema called getJSLSchemaName() exists
     * in the database. If the supplied database does not have a schema called getJSLSchemaName(), then
     * the schema is created and the appropriate JSL related database artifacts are installed
     * in the schema. If a schema called getJSLSchemaName() already exists in the database, then the
     * assumption is that the schema is appropriately configured to hold JSL related
     * database artifacts.  The default schema of the database is set to getJSLSchemaName().
     *
     * @param database        the database to use for setting up the JSLDatabase, must not be null
     * @param simulation      the simulation to be associated with the JSLDatabase, must not be null
     * @param clearDataOption whether or not the database will be cleared before each simulation
     *                        experiment
     */
    public JSLDatabase(DatabaseIfc database, Simulation simulation, boolean clearDataOption) {
        Objects.requireNonNull(database, "The database was null");
        Objects.requireNonNull(simulation, "The simulation was null");
        myDb = database;
        if (!myDb.containsSchema(getJSLSchemaName())) {
            DatabaseIfc.LOG.warn("The database {} does not contain schema {}", myDb.getLabel(), getJSLSchemaName());
            // assume that the schema has not been made and run the table creation script
            try {
                DatabaseIfc.LOG.warn("Assume the schema has not be made and execute the creation script JSLDb.sql");
                boolean b = myDb.executeScript(dbScriptsDir.resolve("JSLDb.sql"));
                if (b == false) {
                    throw new DataAccessException("The execution script JSLDb.sql did not fully execute");
                }
            } catch (IOException e) {
                DatabaseIfc.LOG.error("Unable to execute JSLDb.sql creation script");
                throw new DataAccessException("Unable to execute JSLDb.sql creation script");
            }
        }
        myDb.setDefaultSchemaName(getJSLSchemaName());
        mySimulation = simulation;
        myClearDbFlag = clearDataOption;
        myCurrentSimRunRecord = null;
        startObservingModel();
    }

    /**
     * The name of the database created is based on the name of the simulation via
     * "JSLDb_" + simulation.getName();
     *
     * @param simulation      the simulation to be associated with the JSLDatabase, must not be null
     * @param clearDataOption whether or not the database will be cleared before each simulation
     *                        experiment
     * @return the created JSLDatabase which is backed by an embedded derby database
     */
    public static JSLDatabase createEmbeddedDerbyJSLDatabase(Simulation simulation,
                                                             boolean clearDataOption) {
        return createEmbeddedDerbyJSLDatabase(simulation, clearDataOption, null);
    }

    /**
     * @param simulation      the simulation to be associated with the JSLDatabase, must not be null
     * @param clearDataOption whether or not the database will be cleared before each simulation
     *                        experiment
     * @param dbName          the name of the database within the JSLDatabase.dbDir
     *                        directory. If the JSLDatabase.dbDir already
     *                        contains an embedded derby database with that name then that database is
     *                        deleted and a new one is created in its place. If the supplied
     *                        name is null then the database name is "JSLDb_" + simulation.getName();
     * @return the created JSLDatabase which is backed by an embedded derby database
     */
    public static JSLDatabase createEmbeddedDerbyJSLDatabase(Simulation simulation,
                                                             boolean clearDataOption, String dbName) {
        Objects.requireNonNull(simulation, "The Simulation was null");
        if (dbName == null) {
            // use the simulation name
            String name = simulation.getName().replaceAll("\\s+", "");
            dbName = "JSLDb_" + name;
            //dbName = dbName.replaceAll("\\s+", "");
        }
        Path pathToDb = dbDir.resolve(dbName);
        DatabaseFactory.deleteEmbeddedDerbyDatabase(pathToDb);
        DataSource ds = DatabaseFactory.createEmbeddedDerbyDataSource(pathToDb, true);
        Database db = new Database(dbName, ds, SQLDialect.DERBY);
        return new JSLDatabase(db, simulation, clearDataOption);
    }

    /**
     * The database will not be cleared between simulation runs
     *
     * @param simulation the simulation to be associated with the JSLDatabase, must not be null
     * @param dbName     the name of the existing database within the JSLDatabase.dbDir directory
     * @return the JSLDatabase which is backed by an embedded derby database
     */
    public static JSLDatabase useExistingEmbeddedDerbyJSLDatabase(Simulation simulation,
                                                                  String dbName) {
        return useExistingEmbeddedDerbyJSLDatabase(simulation, false, dbName);
    }

    /**
     * If the database does not exist, then a new one is created.
     *
     * @param simulation      the simulation to be associated with the JSLDatabase, must not be null
     * @param clearDataOption whether or not the database will be cleared before each simulation
     * @param dbName          the name of the existing database within the JSLDatabase.dbDir directory
     * @return the JSLDatabase which is backed by an embedded derby database
     */
    public static JSLDatabase useExistingEmbeddedDerbyJSLDatabase(Simulation simulation,
                                                                  boolean clearDataOption,
                                                                  String dbName) {
        Objects.requireNonNull(simulation, "The Simulation was null");
        Objects.requireNonNull(dbName, "The database name was was null");
        Path pathToDb = dbDir.resolve(dbName);
        DataSource ds = null;
        if (Files.isDirectory(pathToDb)) {
            // the directory holding the database exists already
            // just make a data source for it
            ds = DatabaseFactory.createEmbeddedDerbyDataSource(pathToDb);
        } else {
            // the named database does not exist, make a new one
            ds = DatabaseFactory.createEmbeddedDerbyDataSource(pathToDb, true);
            DatabaseIfc.LOG.warn("The requested database {} does not exist, made a new one instead", pathToDb.toString());
        }
        Database db = new Database(dbName, ds, SQLDialect.DERBY);
        return new JSLDatabase(db, simulation, clearDataOption);
    }

    /**
     * Creates a getJSLSchemaName() schema within the database specified within the database.
     * If such a schema already exists in thee database, it is dropped.
     *
     * @param simulation      the simulation that will use the database
     * @param clearDataOption whether or not the data will be cleared prior to experiments
     * @param dbName          the name of the database, must not be null
     * @param user            the user
     * @param pWord           the password
     * @return a fresh new JSLDatabase
     */
    public static JSLDatabase createPostgresLocalHostJSLDatabase(Simulation simulation, boolean clearDataOption,
                                                                 String dbName, String user, String pWord) {
        return createPostgresJSLDatabase(simulation, clearDataOption, "localhost", dbName, user, pWord);
    }

    /**
     * Creates a getJSLSchemaName() schema within the database specified within the database.
     * If such a schema already exists in thee database, it is dropped.
     *
     * @param simulation      the simulation that will use the database
     * @param clearDataOption whether or not the data will be cleared prior to experiments
     * @param dbServerName    the name of the database server, must not be null
     * @param dbName          the name of the database, must not be null
     * @param user            the user
     * @param pWord           the password
     * @return a fresh new JSLDatabase
     */
    public static JSLDatabase createPostgresJSLDatabase(Simulation simulation, boolean clearDataOption,
                                                        String dbServerName, String dbName, String user, String pWord) {
        Objects.requireNonNull(simulation, "The Simulation was null");
        Objects.requireNonNull(dbServerName, "The name to the database server must not be null");
        Objects.requireNonNull(dbName, "The path name to the database must not be null");
        Properties props = new Properties();
        props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        props.setProperty("dataSource.user", user);
        props.setProperty("dataSource.password", pWord);
        props.setProperty("dataSource.databaseName", dbName);
        props.setProperty("dataSource.serverName", dbServerName);
        JSLDatabase jslDatabase = createJSLDatabase(simulation, clearDataOption, props, SQLDialect.POSTGRES);
        jslDatabase.getDatabase().getDSLContext().settings().withRenderNameStyle(RenderNameStyle.LOWER);
        DatabaseIfc.LOG.info("Created a postgres JSL database {} ", jslDatabase.getDatabase().getLabel());
        return jslDatabase;
    }

    /**
     * Creates a getJSLSchemaName() schema within the database specified within the supplied database properties.
     * If such a schema already exists in thee database, it is dropped.
     *
     * @param simulation      the simulation that will use the database
     * @param clearDataOption whether or not the data will be cleared prior to experiments
     * @param dBProperties    appropriately configured HikariCP datasource properties
     * @param sqlDialect      the jooq dialect for the database. It must match the database type in the properties
     * @return a fresh new JSLDatabase
     */
    public static JSLDatabase createJSLDatabase(Simulation simulation, boolean clearDataOption,
                                                Properties dBProperties, SQLDialect sqlDialect) {
        Objects.requireNonNull(simulation, "The Simulation was null");
        Objects.requireNonNull(dBProperties, "The database properties was was null");
        Objects.requireNonNull(sqlDialect, "The database dialect was was null");
        DataSource ds = DatabaseFactory.getDataSource(dBProperties);
        String user = dBProperties.getProperty("dataSource.user");
        String name = dBProperties.getProperty("dataSource.databaseName");
        String dbLabel = user + "_" + name;
        Database db = new Database(dbLabel, ds, sqlDialect);
        dropJSLDbSchema(db);
        return new JSLDatabase(db, simulation, clearDataOption);
    }

    /**
     * Creates a reference to a JSLDatabase. This method assumes that the data source
     * has a properly configured JSL schema. If it does not, one is created. If it has
     * one the data from previous simulations remains. If the clear data option is
     * set to true then the data WILL be deleted prior to the first experiment.
     *
     * @param simulation      the simulation that will use the database
     * @param clearDataOption whether or not the data will be cleared prior to experiments
     * @param dbName          the name of the database, must not be null
     * @param user            the user
     * @param pWord           the password
     * @return a fresh new JSLDatabase
     */
    public static JSLDatabase getPostgresLocalHostJSLDatabase(Simulation simulation, boolean clearDataOption,
                                                              String dbName, String user, String pWord) {
        return getPostgresJSLDatabase(simulation, clearDataOption, "localhost", dbName, user, pWord);
    }

    /**
     * Creates a reference to a JSLDatabase. This method assumes that the data source
     * has a properly configured JSL schema. If it does not, one is created. If it has
     * one the data from previous simulations remains. If the clear data option is
     * set to true then the data WILL be deleted prior to the first experiment.
     *
     * @param simulation      the simulation that will use the database
     * @param clearDataOption whether or not the data will be cleared prior to experiments
     * @param dbServerName    the name of the database server, must not be null
     * @param dbName          the name of the database, must not be null
     * @param user            the user
     * @param pWord           the password
     * @return a fresh new JSLDatabase
     */
    public static JSLDatabase getPostgresJSLDatabase(Simulation simulation, boolean clearDataOption,
                                                     String dbServerName, String dbName, String user, String pWord) {
        Objects.requireNonNull(simulation, "The Simulation was null");
        Objects.requireNonNull(dbServerName, "The name to the database server must not be null");
        Objects.requireNonNull(dbName, "The path name to the database must not be null");
        Properties props = new Properties();
        props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        props.setProperty("dataSource.user", user);
        props.setProperty("dataSource.password", pWord);
        props.setProperty("dataSource.databaseName", dbName);
        props.setProperty("dataSource.serverName", dbServerName);
        JSLDatabase jslDatabase = getJSLDatabase(simulation, clearDataOption, props, SQLDialect.POSTGRES);
        jslDatabase.getDatabase().getDSLContext().settings().withRenderNameStyle(RenderNameStyle.LOWER);
        DatabaseIfc.LOG.info("Connected to a postgres JSL database {} ", jslDatabase.getDatabase().getLabel());
        return jslDatabase;
    }

    /**
     * Creates a reference to a JSLDatabase. This method assumes that the data source
     * has a properly configured JSL schema. If it does not, one is created. If it has
     * one the data from previous simulations remains. If the clear data option is
     * set to true then the data WILL be deleted prior to the first experiment.
     *
     * @param simulation      the simulation that will use the database
     * @param clearDataOption whether or not the data will be cleared prior to experiments
     * @param dBProperties    appropriately configured HikariCP datasource properties
     * @param sqlDialect      the jooq dialect for the database. It must match the database type in the properties
     * @return a reference to JSLDatabase
     */
    public static JSLDatabase getJSLDatabase(Simulation simulation, boolean clearDataOption,
                                             Properties dBProperties, SQLDialect sqlDialect) {
        Objects.requireNonNull(simulation, "The Simulation was null");
        Objects.requireNonNull(dBProperties, "The database properties was was null");
        Objects.requireNonNull(sqlDialect, "The database dialect was was null");
        DataSource ds = DatabaseFactory.getDataSource(dBProperties);
        String user = dBProperties.getProperty("dataSource.user");
        String name = dBProperties.getProperty("dataSource.databaseName");
        String dbLabel = user + "_" + name;
        Database db = new Database(dbLabel, ds, sqlDialect);
        return new JSLDatabase(db, simulation, clearDataOption);
    }

    /**
     * @return the name of the schema that holds JSL database artifacts
     */
    public static String getJSLSchemaName() {
        return jslSchemaName;
    }

    /**
     * @return the names of the tables in the JSL database as strings
     */
    public static List<String> getJSLTableNames() {
        return new ArrayList<>(JSLTableNames);
    }

    /**
     * Drops the getJSLSchemaName() schema and any related tables in the supplied database if they exist.
     * If the database does not contain a schema called getJSLSchemaName(), then nothing happens
     *
     * @param db the database to do the dropping action
     */
    public static void dropJSLDbSchema(Database db) {
        db.dropSchema(getJSLSchemaName(), JSLTableNames, JSLViewNames);
    }

    /**
     * Called by the model before the simulation experiment is run
     */
    protected void beforeExperiment() {
        if (getClearDatabaseOption()) {
            clearSimulationData();
        } else {
            // no clear option, need to check if simulation record exists
            String simName = mySimulation.getName();
            String expName = mySimulation.getExperimentName();
            if (simulationRunRecordExists(simName, expName)){
                JSL.LOGGER.error("A simulation run record exists for simulation: {}, and experiment: {}",
                        simName, expName);
                JSL.LOGGER.error("You attempted to run a simulation for a run that has ");
                JSL.LOGGER.error(" the same name and experiment without allowing its data to be cleared.");
                JSL.LOGGER.error("You should consider using setClearDatabaseOption() on the database {}.", getDatabase().getLabel());
                JSL.LOGGER.error("Or, you might change the name of the experiment before calling simulation.run().");
                JSL.LOGGER.error("This error is to prevent you from accidentally losing data associated with simulation: {}, and experiment: {}",
                        simName, expName);
                throw new DataAccessException("A simulation run record already exists with the name " + simName + " and experiment name " + expName);
            }
        }
        // insert the new simulation run into the database
        insertSimulationRunRecord(mySimulation);
        // add the model elements associated with this run to the database
        List<ModelElement> currMEList = getModel().getModelElements();
        insertModelElements(currMEList);
    }

    /**
     * Called by the model after the simulation experiment replication is run
     */
    protected void afterReplication() {
        List<ResponseVariable> rvs = getModel().getResponseVariables();
        List<Counter> counters = getModel().getCounters();
        insertWithinRepResponses(rvs);
        insertWithinRepCounters(counters);
        Optional<StatisticalBatchingElement> sbe = mySimulation.getStatisticalBatchingElement();
        if (sbe.isPresent()) {
            // insert the statistics from the batching
            Map<ResponseVariable, BatchStatistic> rmap = sbe.get().getAllResponseVariableBatchStatisticsAsMap();
            Map<TimeWeighted, BatchStatistic> twmap = sbe.get().getAllTimeWeightedBatchStatisticsAsMap();
            insertResponseVariableBatchStatistics(rmap);
            insertTimeWeightedBatchStatistics(twmap);
        }
    }

    /**
     * Called by the model after the simulation experiment is run
     */
    protected void afterExperiment() {
        finalizeCurrentSimulationRunRecord(mySimulation);
        List<ResponseVariable> rvs = getModel().getResponseVariables();
        List<Counter> counters = getModel().getCounters();
        insertAcrossRepResponses(rvs);
        insertAcrossRepResponsesForCounters(counters);
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
        if (!Double.isNaN(sim.getLengthOfReplication()) && !Double.isInfinite(sim.getLengthOfReplication())) {
            record.setLengthOfRep(sim.getLengthOfReplication());
        }
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
     * The list must be ordered according to parent-child, so that parents occur before
     * their children in the list
     *
     * @param elements the list of elements to add
     */
    protected void insertModelElements(List<ModelElement> elements) {
        List<ModelElementRecord> records = new ArrayList<>();
        for (ModelElement element : elements) {
            ModelElementRecord modelElementRecord = newModelElementRecord(element, myCurrentSimRunRecord.getId());
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
            record.setParentIdFk(modelElement.getParentModelElement().getId());
            record.setParentName(modelElement.getParentModelElement().getName());
        }
        record.setLeftCount(modelElement.getLeftPreOrderTraversalCount());
        record.setRightCount(modelElement.getRightPreOrderTraversalCount());
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
            WithinRepStatRecord withinRepStatRecord = newWithinRepStatRecord(rv, myCurrentSimRunRecord.getId());
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
        r.setElementIdFk(rv.getId());
        r.setSimRunIdFk(simId);
        r.setRepNum(rv.getExperiment().getCurrentReplicationNumber());
        WeightedStatisticIfc s = rv.getWithinReplicationStatistic();
        r.setStatName(s.getName());
        if (!Double.isNaN(s.getCount()) && !Double.isInfinite(s.getCount())) {
            r.setStatCount(s.getCount());
        }
        if (!Double.isNaN(s.getAverage()) && !Double.isInfinite(s.getAverage())) {
            r.setAverage(s.getAverage());
        }
        if (!Double.isNaN(s.getMin()) && !Double.isInfinite(s.getMin())) {
            r.setMinimum(s.getMin());
        }
        if (!Double.isNaN(s.getMax()) && !Double.isInfinite(s.getMax())) {
            r.setMaximum(s.getMax());
        }
        if (!Double.isNaN(s.getWeightedSum()) && !Double.isInfinite(s.getWeightedSum())) {
            r.setWeightedSum(s.getWeightedSum());
        }
        if (!Double.isNaN(s.getSumOfWeights()) && !Double.isInfinite(s.getSumOfWeights())) {
            r.setSumOfWeights(s.getSumOfWeights());
        }
        if (!Double.isNaN(s.getWeightedSumOfSquares()) && !Double.isInfinite(s.getWeightedSumOfSquares())) {
            r.setWeightedSsq(s.getWeightedSumOfSquares());
        }
        if (!Double.isNaN(s.getLastValue()) && !Double.isInfinite(s.getLastValue())) {
            r.setLastValue(s.getLastValue());
        }
        if (!Double.isNaN(s.getLastWeight()) && !Double.isInfinite(s.getLastWeight())) {
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
            WithinRepCounterStatRecord statRecord = newWithinRepCounterStatRecord(c, myCurrentSimRunRecord.getId());
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
        r.setElementIdFk(counter.getId());
        r.setSimRunIdFk(simId);
        r.setRepNum(counter.getExperiment().getCurrentReplicationNumber());
        r.setStatName(counter.getName());
        if (!Double.isNaN(counter.getValue()) && !Double.isInfinite(counter.getValue())) {
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
            AcrossRepStatRecord statRecord = newAcrossRepStatRecord(rv, myCurrentSimRunRecord.getId(), s);
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
            AcrossRepStatRecord statRecord = newAcrossRepStatRecord(counter, myCurrentSimRunRecord.getId(), s);
            if (statRecord != null) {
                records.add(statRecord);
            }
        }
        myDb.getDSLContext().batchStore(records).execute();
    }

    /**
     * Creates an AcrossRepStatRecord into the database
     *
     * @param modelElement the model element name
     * @param simId        the id of the simulation run
     * @param s            that statistics to insert
     * @return the created record
     */
    protected AcrossRepStatRecord newAcrossRepStatRecord(ModelElement modelElement, Integer simId,
                                                         StatisticAccessorIfc s) {
        if (simId == null) {
            throw new IllegalArgumentException("Ther simulation id was null");
        }
        if (modelElement == null) {
            throw new IllegalArgumentException("The model element was null.");
        }
        if (s == null) {
            throw new IllegalArgumentException("There supplied StatisticAccessorIfc was null");
        }
        AcrossRepStatRecord r = myDb.getDSLContext().newRecord(ACROSS_REP_STAT);
        r.setElementIdFk(modelElement.getId());
        r.setSimRunIdFk(simId);
        r.setStatName(s.getName());

        if (!Double.isNaN(s.getCount()) && !Double.isInfinite(s.getCount())) {
            r.setStatCount(s.getCount());
        }
        if (!Double.isNaN(s.getAverage()) && !Double.isInfinite(s.getAverage())) {
            r.setAverage(s.getAverage());
        }
        if (!Double.isNaN(s.getStandardDeviation()) && !Double.isInfinite(s.getStandardDeviation())) {
            r.setStdDev(s.getStandardDeviation());
        }
        if (!Double.isNaN(s.getStandardError()) && !Double.isInfinite(s.getStandardError())) {
            r.setStdErr(s.getStandardError());
        }
        if (!Double.isNaN(s.getHalfWidth()) && !Double.isInfinite(s.getHalfWidth())) {
            r.setHalfWidth(s.getHalfWidth());
        }
        if (!Double.isNaN(s.getConfidenceLevel()) && !Double.isInfinite(s.getConfidenceLevel())) {
            r.setConfLevel(s.getConfidenceLevel());
        }
        if (!Double.isNaN(s.getMin()) && !Double.isInfinite(s.getMin())) {
            r.setMinimum(s.getMin());
        }
        if (!Double.isNaN(s.getMax()) && !Double.isInfinite(s.getMax())) {
            r.setMaximum(s.getMax());
        }
        if (!Double.isNaN(s.getWeightedSum()) && !Double.isInfinite(s.getWeightedSum())) {
            r.setWeightedSum(s.getWeightedSum());
        }
        if (!Double.isNaN(s.getSumOfWeights()) && !Double.isInfinite(s.getSumOfWeights())) {
            r.setSumOfWeights(s.getSumOfWeights());
        }
        if (!Double.isNaN(s.getWeightedSumOfSquares()) && !Double.isInfinite(s.getWeightedSumOfSquares())) {
            r.setWeightedSsq(s.getWeightedSumOfSquares());
        }
        if (!Double.isNaN(s.getDeviationSumOfSquares()) && !Double.isInfinite(s.getDeviationSumOfSquares())) {
            r.setDevSsq(s.getDeviationSumOfSquares());
        }
        if (!Double.isNaN(s.getLastValue()) && !Double.isInfinite(s.getLastValue())) {
            r.setLastValue(s.getLastValue());
        }
        if (!Double.isNaN(s.getLastWeight()) && !Double.isInfinite(s.getLastWeight())) {
            r.setLastWeight(s.getLastWeight());
        }
        if (!Double.isNaN(s.getKurtosis()) && !Double.isInfinite(s.getKurtosis())) {
            r.setKurtosis(s.getKurtosis());
        }
        if (!Double.isNaN(s.getSkewness()) && !Double.isInfinite(s.getSkewness())) {
            r.setSkewness(s.getSkewness());
        }
        if (!Double.isNaN(s.getLag1Covariance()) && !Double.isInfinite(s.getLag1Covariance())) {
            r.setLag1Cov(s.getLag1Covariance());
        }
        if (!Double.isNaN(s.getLag1Correlation()) && !Double.isInfinite(s.getLag1Correlation())) {
            r.setLag1Corr(s.getLag1Correlation());
        }
        if (!Double.isNaN(s.getVonNeumannLag1TestStatistic()) && !Double.isInfinite(s.getVonNeumannLag1TestStatistic())) {
            r.setVonNeumanLag1Stat(s.getVonNeumannLag1TestStatistic());
        }
        if (!Double.isNaN(s.getNumberMissing()) && !Double.isInfinite(s.getNumberMissing())) {
            r.setNumMissingObs(s.getNumberMissing());
        }
        return r;
    }

    /**
     * Inserts batch statistics for the supplied map of response variables
     *
     * @param bmap the map of ResponseVariables to insert for, must not be null
     */
    protected void insertResponseVariableBatchStatistics(Map<ResponseVariable, BatchStatistic> bmap) {
        if (bmap == null) {
            throw new IllegalArgumentException("The batch statistic map was null");
        }
        List<BatchStatRecord> records = new ArrayList<>();
        for (Map.Entry<ResponseVariable, BatchStatistic> entry : bmap.entrySet()) {
            ResponseVariable rv = entry.getKey();
            BatchStatistic bs = entry.getValue();
            BatchStatRecord batchStatRecord = newBatchStatRecord(rv, myCurrentSimRunRecord.getId(), bs);
            if (batchStatRecord != null) {
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
    protected void insertTimeWeightedBatchStatistics(Map<TimeWeighted, BatchStatistic> bmap) {
        if (bmap == null) {
            throw new IllegalArgumentException("The batch statistic map was null");
        }
        List<BatchStatRecord> records = new ArrayList<>();
        for (Map.Entry<TimeWeighted, BatchStatistic> entry : bmap.entrySet()) {
            TimeWeighted tw = entry.getKey();
            BatchStatistic bs = entry.getValue();
            BatchStatRecord batchStatRecord = newBatchStatRecord(tw, myCurrentSimRunRecord.getId(), bs);
            if (batchStatRecord != null) {
                records.add(batchStatRecord);
            }
        }
        myDb.getDSLContext().batchStore(records).execute();
    }

    /**
     * Creates an BatchStatRecord into the database
     *
     * @param rv    the response variable, must not be null
     * @param simId the id of the simulation run
     * @param s     that statistics to insert
     * @return the created record
     */
    protected BatchStatRecord newBatchStatRecord(ResponseVariable rv, Integer simId, BatchStatistic s) {
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
        r.setElementIdFk(rv.getId());
        r.setSimRunIdFk(simId);
        r.setStatName(s.getName());
        r.setRepNum(rv.getExperiment().getCurrentReplicationNumber());

        if (!Double.isNaN(s.getCount()) && !Double.isInfinite(s.getCount())) {
            r.setStatCount(s.getCount());
        }
        if (!Double.isNaN(s.getAverage()) && !Double.isInfinite(s.getAverage())) {
            r.setAverage(s.getAverage());
        }
        if (!Double.isNaN(s.getStandardDeviation()) && !Double.isInfinite(s.getStandardDeviation())) {
            r.setStdDev(s.getStandardDeviation());
        }
        if (!Double.isNaN(s.getStandardError()) && !Double.isInfinite(s.getStandardError())) {
            r.setStdErr(s.getStandardError());
        }
        if (!Double.isNaN(s.getHalfWidth()) && !Double.isInfinite(s.getHalfWidth())) {
            r.setHalfWidth(s.getHalfWidth());
        }
        if (!Double.isNaN(s.getConfidenceLevel()) && !Double.isInfinite(s.getConfidenceLevel())) {
            r.setConfLevel(s.getConfidenceLevel());
        }
        if (!Double.isNaN(s.getMin()) && !Double.isInfinite(s.getMin())) {
            r.setMinimum(s.getMin());
        }
        if (!Double.isNaN(s.getMax()) && !Double.isInfinite(s.getMax())) {
            r.setMaximum(s.getMax());
        }
        if (!Double.isNaN(s.getWeightedSum()) && !Double.isInfinite(s.getWeightedSum())) {
            r.setWeightedSum(s.getWeightedSum());
        }
        if (!Double.isNaN(s.getSumOfWeights()) && !Double.isInfinite(s.getSumOfWeights())) {
            r.setSumOfWeights(s.getSumOfWeights());
        }
        if (!Double.isNaN(s.getWeightedSumOfSquares()) && !Double.isInfinite(s.getWeightedSumOfSquares())) {
            r.setWeightedSsq(s.getWeightedSumOfSquares());
        }
        if (!Double.isNaN(s.getDeviationSumOfSquares()) && !Double.isInfinite(s.getDeviationSumOfSquares())) {
            r.setDevSsq(s.getDeviationSumOfSquares());
        }
        if (!Double.isNaN(s.getLastValue()) && !Double.isInfinite(s.getLastValue())) {
            r.setLastValue(s.getLastValue());
        }
        if (!Double.isNaN(s.getLastWeight()) && !Double.isInfinite(s.getLastWeight())) {
            r.setLastWeight(s.getLastWeight());
        }
        if (!Double.isNaN(s.getKurtosis()) && !Double.isInfinite(s.getKurtosis())) {
            r.setKurtosis(s.getKurtosis());
        }
        if (!Double.isNaN(s.getSkewness()) && !Double.isInfinite(s.getSkewness())) {
            r.setSkewness(s.getSkewness());
        }
        if (!Double.isNaN(s.getLag1Covariance()) && !Double.isInfinite(s.getLag1Covariance())) {
            r.setLag1Cov(s.getLag1Covariance());
        }
        if (!Double.isNaN(s.getLag1Correlation()) && !Double.isInfinite(s.getLag1Correlation())) {
            r.setLag1Corr(s.getLag1Correlation());
        }
        if (!Double.isNaN(s.getVonNeumannLag1TestStatistic()) && !Double.isInfinite(s.getVonNeumannLag1TestStatistic())) {
            r.setVonNeumanLag1Stat(s.getVonNeumannLag1TestStatistic());
        }
        if (!Double.isNaN(s.getNumberMissing()) && !Double.isInfinite(s.getNumberMissing())) {
            r.setNumMissingObs(s.getNumberMissing());
        }
        if (!Double.isNaN(s.getMinBatchSize()) && !Double.isInfinite(s.getMinBatchSize())) {
            r.setMinBatchSize((double) s.getMinBatchSize());
        }
        if (!Double.isNaN(s.getMinNumberOfBatches()) && !Double.isInfinite(s.getMinNumberOfBatches())) {
            r.setMinNumBatches((double) s.getMinNumberOfBatches());
        }
        if (!Double.isNaN(s.getMaxNumberOfBatchesMultiple()) && !Double.isInfinite(s.getMaxNumberOfBatchesMultiple())) {
            r.setMaxNumBatchesMultiple((double) s.getMaxNumberOfBatchesMultiple());
        }
        if (!Double.isNaN(s.getMaxNumBatches()) && !Double.isInfinite(s.getMaxNumBatches())) {
            r.setMaxNumBatches((double) s.getMaxNumBatches());
        }
        if (!Double.isNaN(s.getNumRebatches()) && !Double.isInfinite(s.getNumRebatches())) {
            r.setNumRebatches((double) s.getNumRebatches());
        }
        if (!Double.isNaN(s.getCurrentBatchSize()) && !Double.isInfinite(s.getCurrentBatchSize())) {
            r.setCurrentBatchSize((double) s.getCurrentBatchSize());
        }
        if (!Double.isNaN(s.getAmountLeftUnbatched()) && !Double.isInfinite(s.getAmountLeftUnbatched())) {
            r.setAmtUnbatched(s.getAmountLeftUnbatched());
        }
        if (!Double.isNaN(s.getTotalNumberOfObservations()) && !Double.isInfinite(s.getTotalNumberOfObservations())) {
            r.setTotalNumObs(s.getTotalNumberOfObservations());
        }
        return r;

    }

    /**
     * @return the current simulation run record id, the last inserted simulation run
     */
    public Optional<Integer> getCurrentSimRunRecordId() {
        Integer id = myCurrentSimRunRecord.getId();
        return Optional.ofNullable(id);
    }

    /**
     * @return the current simulation run record or null
     */
    public Optional<SimulationRunRecord> getCurrentSimRunRecord() {
        return Optional.ofNullable(myCurrentSimRunRecord);
    }

    /**
     * Clears all data from the JSL database. Keeps the database structure.
     */
    public final void clearAllData() {
        DSLContext create = myDb.getDSLContext();
        create.delete(BATCH_STAT).execute();
        create.delete(WITHIN_REP_COUNTER_STAT).execute();
        create.delete(ACROSS_REP_STAT).execute();
        create.delete(WITHIN_REP_STAT).execute();
        create.delete(MODEL_ELEMENT).execute();
        create.delete(SIMULATION_RUN).execute();
    }

    /**
     * Deletes all simulation data associated with the (current) simulation. In other
     * words, the simulation run data associated with a simulation with the current
     * name and the experiment with the current name.
     */
    protected final void clearSimulationData() {
        String simName = mySimulation.getName();
        String expName = mySimulation.getExperimentName();
        deleteSimulationRunRecord(simName, expName);
    }

    /**
     * The combination of simName and expName should be unique within the database. Many
     * different experiments can be run with different names for the same simulation. This method
     * deletes the simulation run record with the provided names AND all related data
     * associated with that simulation run.  If a SIMULATION_RUN record does not
     * exist with the simName and expName combination, nothing occurs.
     *
     * @param simName the name of the simulation
     * @param expName the experiment name for the simulation
     */
    public final void deleteSimulationRunRecord(String simName, String expName) {
        DSLContext create = myDb.getDSLContext();
        SimulationRunRecord simulationRunRecord = create.selectFrom(SIMULATION_RUN)
                .where(SIMULATION_RUN.SIM_NAME.eq(simName)
                        .and(SIMULATION_RUN.EXP_NAME.eq(expName)))
                .fetchOne();
        if (simulationRunRecord != null) {
            simulationRunRecord.delete();
        }
    }

    /**
     * The combination of simName and expName should be unique within the database. Many
     * different experiments can be run with different names for the same simulation. This method
     * gets the simulation run record with the provided names.  If a SIMULATION_RUN record does not
     * exist with the simName and expName combination, null is returned.
     *
     * @param simName the name of the simulation
     * @param expName the experiment name for the simulation
     * @return the record or null
     */
    public final SimulationRunRecord getSimulationRunRecord(String simName, String expName) {
        DSLContext create = myDb.getDSLContext();
        SimulationRunRecord simulationRunRecord = create.selectFrom(SIMULATION_RUN)
                .where(SIMULATION_RUN.SIM_NAME.eq(simName)
                        .and(SIMULATION_RUN.EXP_NAME.eq(expName)))
                .fetchOne();

        return simulationRunRecord;
    }

    /**
     * The combination of simName and expName should be unique within the database. Many
     * different experiments can be run with different names for the same simulation. This method
     * checks if the simulation run record with the provided names exists.
     *
     * @param simName the name of the simulation
     * @param expName the experiment name for the simulation
     * @return true if the record exits
     */
    public final boolean simulationRunRecordExists(String simName, String expName) {
        DSLContext create = myDb.getDSLContext();
        SimulationRunRecord simulationRunRecord = create.selectFrom(SIMULATION_RUN)
                .where(SIMULATION_RUN.SIM_NAME.eq(simName)
                        .and(SIMULATION_RUN.EXP_NAME.eq(expName)))
                .fetchOne();
        return simulationRunRecord != null;
    }

    /**
     * @param simId the identifier of the simulation record
     * @return the record or null
     */
    public final SimulationRunRecord getSimulationRunRecord(Integer simId) {
        SimulationRunRecord simulationRunRecord = myDb.getDSLContext()
                .selectFrom(SIMULATION_RUN).where(SIMULATION_RUN.ID.eq(simId)).fetchOne();
        return simulationRunRecord;
    }

    /**
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
     * @return the model element records as a jooq Result
     */
    public final Result<ModelElementRecord> getModelElementRecords() {
        Result<ModelElementRecord> modelElementRecords = myDb.getDSLContext()
                .selectFrom(MODEL_ELEMENT).orderBy(MODEL_ELEMENT.ELEMENT_ID).fetch();
        return modelElementRecords;
    }

    /**
     * @param model
     * @return a BiMap linking model element records with corresponding ModelElements
     */
    public final BiMap<ModelElementRecord, ModelElement> getModelElementRecordBiMap(Model model) {
        if (model == null) {
            throw new IllegalArgumentException("The model was null.");
        }
        BiMap<ModelElementRecord, ModelElement> biMap = HashBiMap.create();
        Result<ModelElementRecord> elementRecords = getModelElementRecords();
        for (ModelElementRecord r : elementRecords) {
            ModelElement modelElement = model.getModelElement(r.getElementName());
            if (modelElement != null) {
                biMap.put(r, modelElement);
            }
        }
        return biMap;
    }

    /**
     * @return the within replication statistics as a jooq Result
     */
    public final Result<WithinRepStatRecord> getWithinRepStatRecords() {
        Result<WithinRepStatRecord> withinRepStatRecords = myDb.getDSLContext()
                .selectFrom(WITHIN_REP_STAT)
                .orderBy(WITHIN_REP_STAT.SIM_RUN_ID_FK,
                        WITHIN_REP_STAT.ID,
                        WITHIN_REP_STAT.ELEMENT_ID_FK,
                        WITHIN_REP_STAT.REP_NUM).fetch();
        return withinRepStatRecords;
    }

    /**
     * @return the within replication statistics as a JDBC ResultSet
     */
    public final ResultSet getWithinRepStatRecordsAsResultSet() {
        ResultSet resultSet = getWithinRepStatRecords().intoResultSet();
        return resultSet;
    }

    /**
     * @return a jooq Result of across replication statistics for all simulation runs
     */
    public final Result<AcrossRepStatRecord> getAcrossRepStatRecords() {
        Result<AcrossRepStatRecord> acrossRepStatRecords = myDb.getDSLContext()
                .selectFrom(ACROSS_REP_STAT)
                .orderBy(ACROSS_REP_STAT.SIM_RUN_ID_FK,
                        ACROSS_REP_STAT.ID,
                        ACROSS_REP_STAT.ELEMENT_ID_FK,
                        ACROSS_REP_STAT.STAT_NAME).fetch();
        return acrossRepStatRecords;
    }

    /**
     * @return the across replication statistics as a JDBC ResultSet
     */
    public final ResultSet getAcrossRepStatRecordsAsResultSet() {
        return getAcrossRepStatRecords().intoResultSet();
    }

    /**
     * @return a jooq Result of batch statistics
     */
    public final Result<BatchStatRecord> getBatchStatRecords() {
        Result<BatchStatRecord> batchStatRecords = myDb.getDSLContext()
                .selectFrom(BATCH_STAT)
                .orderBy(BATCH_STAT.SIM_RUN_ID_FK,
                        BATCH_STAT.ID,
                        BATCH_STAT.ELEMENT_ID_FK,
                        BATCH_STAT.STAT_NAME, BATCH_STAT.REP_NUM).fetch();
        return batchStatRecords;
    }

    /**
     * @return the batch statistics as a JDBC ResultSet
     */
    public final ResultSet getBatchStatRecordsAsResultSet() {
        return getBatchStatRecords().intoResultSet();
    }

    /**
     * Within replication view of that simulation response results
     *
     * @return a jooq Result holding (simid, exp_name, element_name, stat_name, rep_num, average)
     */
    public final Result<WithinRepResponseViewRecord> getWithinRepResponseViewRecords() {
        Result<WithinRepResponseViewRecord> fetch = myDb.getDSLContext()
                .selectFrom(WITHIN_REP_RESPONSE_VIEW)
                .orderBy(WITHIN_REP_RESPONSE_VIEW.SIM_RUN_ID_FK,
                        WITHIN_REP_RESPONSE_VIEW.EXP_NAME,
                        WITHIN_REP_RESPONSE_VIEW.ELEMENT_NAME,
                        WITHIN_REP_RESPONSE_VIEW.STAT_NAME,
                        WITHIN_REP_RESPONSE_VIEW.REP_NUM).fetch();
        return fetch;
    }

    /**
     * Within replication view of that simulation counter results
     *
     * @return a jooq Result holding (simid, exp_name, element_name, stat_name, rep_num, last_value)
     */
    public final Result<WithinRepCounterViewRecord> getWithinRepCounterViewRecords() {
        Result<WithinRepCounterViewRecord> fetch = myDb.getDSLContext()
                .selectFrom(WITHIN_REP_COUNTER_VIEW)
                .orderBy(WITHIN_REP_COUNTER_VIEW.SIM_RUN_ID_FK,
                        WITHIN_REP_COUNTER_VIEW.EXP_NAME,
                        WITHIN_REP_COUNTER_VIEW.ELEMENT_NAME,
                        WITHIN_REP_COUNTER_VIEW.STAT_NAME,
                        WITHIN_REP_COUNTER_VIEW.REP_NUM).fetch();
        return fetch;
    }

    /**
     * Across replication view of that simulation response results
     *
     * @return a jooq Result holding (simid, exp_name, element_name, stat_name, stat_count, average, std_dev)
     */
    public final Result<AcrossRepViewRecord> getAcrossRepViewRecords() {
        Result<AcrossRepViewRecord> fetch = myDb.getDSLContext()
                .selectFrom(ACROSS_REP_VIEW)
                .orderBy(ACROSS_REP_VIEW.SIM_RUN_ID_FK,
                        ACROSS_REP_VIEW.EXP_NAME,
                        ACROSS_REP_VIEW.ELEMENT_NAME,
                        ACROSS_REP_VIEW.STAT_NAME).fetch();
        return fetch;
    }

    /**
     * @return A JDBC ResultSet of the across replication view records.
     */
    public final ResultSet getAcrossRepViewRecordsAsResultSet() {
        return getAcrossRepViewRecords().intoResultSet();
    }

    /**
     * Batch statistic view of that simulation batch response results
     *
     * @return a jooq Result holding (simid, exp_name, element_name, stat_name, stat_count, average, std_dev)
     */
    public final Result<BatchStatViewRecord> getBatchStatViewRecords() {
        Result<BatchStatViewRecord> fetch = myDb.getDSLContext()
                .selectFrom(BATCH_STAT_VIEW)
                .orderBy(BATCH_STAT_VIEW.SIM_RUN_ID_FK,
                        BATCH_STAT_VIEW.EXP_NAME,
                        BATCH_STAT_VIEW.ELEMENT_NAME,
                        BATCH_STAT_VIEW.STAT_NAME).fetch();
        return fetch;
    }

    /**
     * Returns observations for each replication for all ResponseVariable, TimeWeighted, and Counter
     * responses. In the case of ResponseVariable and TimeWeighted response, the value field
     * is the within replication average. In the case of a Counter, the value field is the
     * last value of the counter variable (i.e. the total count for the replication).
     *
     * @return a jooq Result holding (simid, exp_name, element_name, stat_name, rep_num, value)
     */
    public final Result<WithinRepViewRecord> getWithinRepViewRecords() {
        Result<WithinRepViewRecord> fetch = myDb.getDSLContext()
                .selectFrom(WITHIN_REP_VIEW)
                .orderBy(WITHIN_REP_VIEW.SIM_RUN_ID_FK,
                        WITHIN_REP_VIEW.EXP_NAME,
                        WITHIN_REP_VIEW.ELEMENT_NAME,
                        WITHIN_REP_VIEW.STAT_NAME,
                        WITHIN_REP_VIEW.REP_NUM).fetch();
        return fetch;
    }

    /** Returns all of the pairwise differences (A - B) for each response variable, time weighted, and
     * counters based on within replication data.
     *
     * (sim_name, A_SIM_NUM, STAT_NAME, A_EXP_NUM, REP_NUM, A_VALUE, B_SIM_NUM, B_EXP_NAME,
     * B_VALUE, DIFF_NAME, A_MINUS_B)
     *
     * @return the jooq Result holding the records
     */
    public Result<PwDiffWithinRepViewRecord> getPairWiseWithinRepViewRecords(){
        Result<PwDiffWithinRepViewRecord> fetch = myDb.getDSLContext()
                .selectFrom(PW_DIFF_WITHIN_REP_VIEW)
                .orderBy(PW_DIFF_WITHIN_REP_VIEW.SIM_NAME,
                        PW_DIFF_WITHIN_REP_VIEW.STAT_NAME,
                        PW_DIFF_WITHIN_REP_VIEW.REP_NUM).fetch();
        return fetch;
    }

    /** Across replication summary statistics for all pairwise differences
     * (sim_name, stat_name, A_EXP_NAME, B_EXP_NAME, DIFF_NAME, AVG_A, STD_DEV_A, AVG_B, STD_DEV_B
     * AVG_DIFF_A_MINUS_B, STD_DEV_DIFF_A_MINUS_B, STAT_COUNT)
     *
     * @return the jooq Result
     */
    public Result<Record12<String, String, String, String, String, BigDecimal, BigDecimal, BigDecimal, BigDecimal,
            BigDecimal, BigDecimal, Integer>> getPairWiseAcrossRepRecords(){
        Result<Record12<String, String, String, String, String, BigDecimal, BigDecimal, BigDecimal, BigDecimal,
                BigDecimal, BigDecimal, Integer>> fetch = myDb.getDSLContext()
                .select(PW_DIFF_WITHIN_REP_VIEW.SIM_NAME,
                        PW_DIFF_WITHIN_REP_VIEW.STAT_NAME,
                        PW_DIFF_WITHIN_REP_VIEW.A_EXP_NAME,
                        PW_DIFF_WITHIN_REP_VIEW.B_EXP_NAME,
                        PW_DIFF_WITHIN_REP_VIEW.DIFF_NAME,
                        avg(PW_DIFF_WITHIN_REP_VIEW.A_VALUE).as("AVG_A"),
                        stddevSamp(PW_DIFF_WITHIN_REP_VIEW.A_VALUE).as("STD_DEV_A"),
                        avg(PW_DIFF_WITHIN_REP_VIEW.B_VALUE).as("AVG_B"),
                        stddevSamp(PW_DIFF_WITHIN_REP_VIEW.B_VALUE).as("STD_DEV_B"),
                        avg(PW_DIFF_WITHIN_REP_VIEW.A_MINUS_B).as("AVG_DIFF_A_MINUS_B"),
                        stddevSamp(PW_DIFF_WITHIN_REP_VIEW.A_MINUS_B).as("STD_DEV_DIFF_A_MINUS_B"),
                        count(PW_DIFF_WITHIN_REP_VIEW.A_MINUS_B).as("STAT_COUNT"))
                .from(PW_DIFF_WITHIN_REP_VIEW)
                .groupBy(PW_DIFF_WITHIN_REP_VIEW.SIM_NAME,
                        PW_DIFF_WITHIN_REP_VIEW.STAT_NAME,
                        PW_DIFF_WITHIN_REP_VIEW.A_EXP_NAME,
                        PW_DIFF_WITHIN_REP_VIEW.B_EXP_NAME,
                        PW_DIFF_WITHIN_REP_VIEW.DIFF_NAME).fetch();
        return fetch;
    }

    /** Across replication summary statistics for all pairwise differences
     * (sim_name, stat_name, A_EXP_NAME, B_EXP_NAME, DIFF_NAME, AVG_A, STD_DEV_A, AVG_B, STD_DEV_B
     * AVG_DIFF_A_MINUS_B, STD_DEV_DIFF_A_MINUS_B, STAT_COUNT)
     *
     * @return the ResultSet
     */
    public ResultSet getPairWiseAcrossRepRecordsAsResultSet(){
        return getPairWiseAcrossRepRecords().intoResultSet();
    }

    /**
     * @return the within replication view records as a JDBC ResultSet
     */
    public final ResultSet getWithinRepViewRecordsAsResultSet() {
        return getWithinRepViewRecords().intoResultSet();
    }

    /**
     * @return a map with key sim run id holding the within replication view records by simulation run id
     */
    public final Map<Integer, WithinRepViewRecord> getWithinRepViewRecordsBySimulationRunId() {
        Map<Integer, WithinRepViewRecord> map = getWithinRepViewRecords()
                .intoMap(WITHIN_REP_VIEW.SIM_RUN_ID_FK);
        return map;
    }

    /**
     * @return a map with key sim run id holding the across replication view records
     */
    public final Map<Integer, AcrossRepViewRecord> getAcrossRepViewRecordsBySimulationRunId() {
        Map<Integer, AcrossRepViewRecord> map = getAcrossRepViewRecords().intoMap(ACROSS_REP_VIEW.SIM_RUN_ID_FK);
        return map;
    }

    /**
     * @param simId        the id of the simulation
     * @param responseName the name of the model element that has observations within each replication. It can be
     *                     a Counter, ResponseVariable, or TimeWeighted model element name
     * @return the across replication statistics as a Statistic
     */
    public final Statistic getAcrossRepStatistic(Integer simId, String responseName) {
        List<Double> list = myDb.getDSLContext()
                .select(WITHIN_REP_VIEW.VALUE)
                .from(WITHIN_REP_VIEW)
                .where(WITHIN_REP_VIEW.SIM_RUN_ID_FK.eq(simId))
                .and(WITHIN_REP_VIEW.ELEMENT_NAME.eq(responseName))
                .fetch(WITHIN_REP_VIEW.VALUE);
        Statistic s = new Statistic(Doubles.toArray(list));
        s.setName(responseName);
        return s;
    }

    /**
     * A map holding the values as an array for the within replication response values
     * by simulation run
     *
     * @param responseName the name of the response variable, time weighted variable or counter
     * @return a Map with key as simulation id and replication value in the array
     */
    public final Map<Integer, double[]> getWithRepViewValuesAsMap(String responseName) {
        Map<Integer, List<Double>> rMap = myDb.getDSLContext()
                .selectFrom(WITHIN_REP_VIEW)
                .where(WITHIN_REP_VIEW.ELEMENT_NAME.eq(responseName))
                .fetch()
                .intoGroups(WITHIN_REP_VIEW.SIM_RUN_ID_FK, WITHIN_REP_VIEW.VALUE);
        Map<Integer, double[]> cMap = new LinkedHashMap<>();
        for (Integer s : rMap.keySet()) {
            List<Double> doubles = rMap.get(s);
            double[] a = Doubles.toArray(doubles);
            cMap.put(s, a);
        }
        return cMap;
    }

    /**
     * This prepares a map that can be used with MultipleComparisonAnalyzer and
     * returns the MultipleComparisonAnalyzer. If the set of
     * simulation runs does not contain the provided experiment name, then an IllegalArgumentException
     * occurs.  If there are multiple simulation runs with the same experiment name, then
     * an IllegalArgumentException occurs. In other words, when running the experiments, the user
     * must make the experiment names unique in order for this map to be built.
     *
     * @param expNames     The set of experiment names for with the responses need extraction, must not
     *                     be null
     * @param responseName the name of the response variable, time weighted variable or counter
     * @return a configured MultipleComparisonAnalyzer
     */
    public final MultipleComparisonAnalyzer getMultipleComparisonAnalyzerFor(Set<String> expNames,
                                                                             String responseName) {
        Map<String, double[]> map = getWithinRepViewValuesAsMapForExperiments(expNames, responseName);
        MultipleComparisonAnalyzer mca = new MultipleComparisonAnalyzer(map);
        mca.setName(responseName);
        return mca;
    }

    /**
     * This prepares a map that can be used with MultipleComparisonAnalyzer. If the set of
     * simulation runs does not contain the provided experiment name, then an IllegalArgumentException
     * occurs.  If there are multiple simulation runs with the same experiment name, then
     * an IllegalArgumentException occurs. In other words, when running the experiments, the user
     * must make the experiment names unique in order for this map to be built.
     *
     * @param expNames     The set of experiment names for with the responses need extraction, must not
     *                     be null
     * @param responseName the name of the response variable, time weighted variable or counter
     * @return a map with key exp_name containing an array of values, each value from each replication
     */
    public final Map<String, double[]> getWithinRepViewValuesAsMapForExperiments(Set<String> expNames,
                                                                                 String responseName) {
        Objects.requireNonNull(expNames, "The set of experiment names was null");
        Map<String, double[]> responseMap = new LinkedHashMap<>();
        if (expNames.isEmpty()) {
            return responseMap;
        }
        if (responseName == null) {
            return responseMap;
        }

        Map<Integer, double[]> valuesBySimID = getWithRepViewValuesAsMap(responseName);
        for (String expName : expNames) {
            List<Integer> simIDs = myDb.getDSLContext()
                    .selectDistinct(WITHIN_REP_VIEW.SIM_RUN_ID_FK)
                    .from(WITHIN_REP_VIEW)
                    .where(WITHIN_REP_VIEW.EXP_NAME.eq(expName))
                    .fetch()
                    .getValues(WITHIN_REP_VIEW.SIM_RUN_ID_FK);
            if (simIDs.isEmpty()) {
                throw new IllegalArgumentException("There were no simulation runs with the experiment name: " + expName);
            }
            if (simIDs.size() > 1) {
                throw new IllegalArgumentException("There were multiple simulation runs with the same experiment name: " + expName);
            }
            // must have 1 element in list
            // there is a 1 to 1 mapping between simulation id's and experiment names
            Integer simID = simIDs.get(0); //the sim id of the experiment
            responseMap.put(expName, valuesBySimID.get(simID));
        }

        return responseMap;
    }

    /**
     * @param tblsawTableName the name of the table saw table
     * @return the within replication view records as a Tablesaw Table
     * @throws SQLException an SQLException
     */
    public final Table getWithinRepViewRecordsAsTablesawTable(String tblsawTableName) throws SQLException {
        return Table.read().db(getWithinRepViewRecordsAsResultSet(), tblsawTableName);
    }

    /**
     * Squelches the SQLException
     *
     * @param tblsawTableName the name of the table saw table
     * @return the within replication view records as a Tablesaw Table
     */
    public final Table getWithinRepViewRecordsAsTablesawTableNE(String tblsawTableName) {

        Table table = null;
        try {
            table = Table.read().db(getWithinRepViewRecordsAsResultSet(), tblsawTableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return table;
    }

    /**
     * @param tblsawTableName the name of the table saw table
     * @return the within replication view records as a Tablesaw Table
     * @throws SQLException an SQLException
     */
    public final Table getAcrossRepViewRecordsAsTablesawTable(String tblsawTableName) throws SQLException {
        return Table.read().db(getAcrossRepViewRecordsAsResultSet(), tblsawTableName);
    }

    /**
     * Squelches the SQLException
     *
     * @param tblsawTableName the name of the table saw table
     * @return the within replication view records as a Tablesaw Table
     */
    public final Table getAcrossRepViewRecordsAsTablesawTableNE(String tblsawTableName) {

        Table table = null;
        try {
            table = Table.read().db(getAcrossRepViewRecordsAsResultSet(), tblsawTableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return table;
    }

    /**
     * @param tblName the name of the Tablesaw table
     * @return the within replication statistics as a Tablesaw Table
     */
    public final Table getWithinRepStatRecordsAsTablesawTable(String tblName) throws SQLException {
        return Table.read().db(getWithinRepStatRecordsAsResultSet(), tblName);
    }

    /**
     * Squelches the SQLException
     *
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
     * @param tblName the name of the Tablesaw table
     * @return the across replication statistics as a Tablesaw Table
     */
    public final Table getAcrossRepStatRecordsAsTablesawTable(String tblName) throws SQLException {
        return Table.read().db(getAcrossRepStatRecordsAsResultSet(), tblName);
    }

    /**
     * Squelches the SQLException
     *
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
     * @param tblName the name of the Tablesaw table
     * @return the batch statistics as a Tablesaw Table
     */
    public final Table getBatchStatRecordsAsTablesawTable(String tblName) throws SQLException {
        return Table.read().db(getBatchStatRecordsAsResultSet(), tblName);
    }

    /**
     * Squelches the SQLException
     *
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
     * @param tblName the name of the Tablesaw table
     * @return the pairwise difference results a Tablesaw Table
     */
    public final Table getPairWiseAcrossRepRecordsAsTablesawTable(String tblName) throws SQLException {
        return Table.read().db(getPairWiseAcrossRepRecordsAsResultSet(), tblName);
    }

    /**
     * Squelches the SQLException
     *
     * @param tblName the name of the Tablesaw table
     * @return the pairwise difference results as a Tablesaw Table or null
     */
    public final Table getPairWiseAcrossRepRecordsAsTablesawTableNE(String tblName) {

        Table table = null;
        try {
            table = Table.read().db(getPairWiseAcrossRepRecordsAsResultSet(), tblName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return table;
    }

    /**
     * @return a reference to the underlying database via a DatabaseIfc
     */
    public final DatabaseIfc getDatabase() {
        return myDb;
    }

    /**
     * @return true means that the underlying database will be cleared before each
     * simulation run
     */
    public final boolean getClearDatabaseOption() {
        return myClearDbFlag;
    }

    /**
     * @param option true means that the database will be cleared before each simulation run
     */
    public final void setClearDatabaseOption(boolean option) {
        myClearDbFlag = option;
    }

    /**
     * @return the attached model
     */
    public final Model getModel() {
        return mySimulation.getModel();
    }

    /**
     * @return the simulation that is being tabulated
     */
    public final Simulation getSimulation() {
        return mySimulation;
    }

    /**
     * Tells the database to stop observing the model if it is observing it
     */
    public final void stopObservingModel() {
        if (mySimulationObserver != null) {
            getModel().deleteObserver(mySimulationObserver);
            mySimulationObserver = null;
        }
    }

    /**
     * Tells the database to observe the model if it is not already
     */
    public final void startObservingModel() {
        if (mySimulationObserver == null) {
            mySimulationObserver = new SimulationDatabaseObserver();
            getModel().addObserver(mySimulationObserver);
        }
    }

    /**
     * Writes all tables as text
     *
     * @param out the PrintWriter to write to
     */
    public void writeAllTablesAsText(PrintWriter out) {
        myDb.writeAllTablesAsText(getJSLSchemaName(), out);
    }

    /**
     * Writes all tables as separate comma separated value files into the jslOutput/excel directory.
     * The files are written to text files using the same name as
     * the tables in the database
     *
     * @throws IOException a checked exception
     */
    public void writeAllTablesAsCSV() throws IOException {
        myDb.writeAllTablesAsCSV(getJSLSchemaName(), JSL.ExcelDir);
    }

    /**
     * Writes all tables as separate comma separated value files into the supplied
     * directory. The files are written to text files using the same name as
     * the tables in the database
     *
     * @param pathToOutPutDirectory the path to the output directory to hold the csv files
     * @throws IOException a checked exception
     */
    public void writeAllTablesAsCSV(Path pathToOutPutDirectory) throws IOException {
        myDb.writeAllTablesAsCSV(getJSLSchemaName(), pathToOutPutDirectory);
    }

    /**
     * Writes all the tables to an Excel workbook,  uses JSL.ExcelDir for the directory
     */
    public void writeDbToExcelWorkbook() throws IOException {
        myDb.writeDbToExcelWorkbook(getJSLSchemaName(), JSL.ExcelDir);
    }

    /**
     * Writes all the tables to an Excel workbook, uses name of database
     *
     * @param wbDirectory directory of the workbook, if null uses the working directory
     */
    public void writeDbToExcelWorkbook(Path wbDirectory) throws IOException {
        myDb.writeDbToExcelWorkbook(getJSLSchemaName(), wbDirectory);
    }

    /**
     * Writes all the tables to an Excel workbook uses JSL.ExcelDir for the directory
     *
     * @param wbName name of the workbook, if null uses name of database
     */
    public void writeDbToExcelWorkbook(String wbName) throws IOException {
        myDb.writeDbToExcelWorkbook(getJSLSchemaName(), wbName, JSL.ExcelDir);
    }

    /**
     * Writes all the tables to an Excel workbook
     *
     * @param wbName      name of the workbook, if null uses name of database
     * @param wbDirectory directory of the workbook, if null uses the working directory
     */
    public void writeDbToExcelWorkbook(String wbName, Path wbDirectory) throws IOException {
        myDb.writeDbToExcelWorkbook(getJSLSchemaName(), wbName, wbDirectory);
    }

    private class SimulationDatabaseObserver extends ModelElementObserver {

        @Override
        protected void beforeExperiment(ModelElement m, Object arg) {
            super.beforeExperiment(m, arg);
            JSLDatabase.this.beforeExperiment();
        }

        @Override
        protected void afterReplication(ModelElement m, Object arg) {
            super.afterReplication(m, arg);
            JSLDatabase.this.afterReplication();
        }

        @Override
        protected void afterExperiment(ModelElement m, Object arg) {
            super.afterExperiment(m, arg);
            JSLDatabase.this.afterExperiment();
        }

    }
}
