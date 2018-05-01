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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.dbutil;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsl.utilities.excel.ExcelUtil;
import org.apache.commons.io.FileUtils;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jooq.*;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.util.GenerationTool;
import org.jooq.util.jaxb.Database;
import org.jooq.util.jaxb.Generate;
import org.jooq.util.jaxb.Generator;
import org.jooq.util.jaxb.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstraction for using Derby embedded databases.  The static method createDb() provides a builder
 * for constructing instances.
 * <p>
 * The assumption is that the creation script only creates the tables with no
 * constraints on keys. The insertion process can be either through Excel
 * or a script having SQL insert statements. The alter script then places the key and
 * other contraints on the database.  We assume that valid data and scripts are in place.
 * <p>
 * The Excel workbook must a worksheet for each table of the database for which
 * you want data inserted. The worksheets should be named exactly the same as the table
 * names in the database. The first row of each sheet should contain the exact field
 * names for the table that the sheet represents. Valid data must be entered into each
 * sheet. No validation is provided.
 *
 * @author rossetti
 */
public class EmbeddedDerbyDatabase implements DatabaseIfc {

    public enum LineOption {
        COMMENT, CONTINUED, END
    }

    public static final String DEFAULT_DELIMITER = ";";
    public static final Pattern NEW_DELIMITER_PATTERN = Pattern.compile("(?:--|\\/\\/|\\#)?!DELIMITER=(.+)");
    public static final Pattern COMMENT_PATTERN = Pattern.compile("^(?:--|\\/\\/|\\#).+");

    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static {
        System.setProperty("org.jooq.no-logo", "true");
    }

    /**
     * The connection to the database
     */
    private final EmbeddedDataSource myEmbeddedDS;

    /**
     * The path to the database
     */
    private final Path myDbPath;
    private final Path myDbDirPath;

    private final String myDbName;
    private String myDbSchemaName;

    private Connection myConnection;

    /**
     * The connection URL
     */
    private String myConnURL;

    private final SQLDialect mySQLDialect = SQLDialect.DERBY;
    private Settings myExecuteLoggingSettings;

    private Path myCreationScriptPath;
    private Path myTableScriptPath;
    private Path myInsertionScriptPath;
    private Path myExcelInsertPath;
    private Path myAlterScriptPath;

    private final List<String> myTruncateTableOrder;
    private final List<String> myInsertTableOrder;
    private final List<String> myCreationScriptCommands;
    private final List<String> myTableCommands;
    private final List<String> myInsertCommands;
    private final List<String> myAlterCommands;

    private EmbeddedDerbyDatabase(DbBuilder builder) throws IOException, SQLException, InvalidFormatException {
        turnOffJooQDefaultExecutionLogging();
        // set up the arrays to hold possble commands from scripts
        myCreationScriptCommands = new ArrayList<>();
        myTableCommands = new ArrayList<>();
        myInsertCommands = new ArrayList<>();
        myAlterCommands = new ArrayList<>();
        // set up arrays for insert order or truncate order if supplied
        myTruncateTableOrder = new ArrayList<>();
        myInsertTableOrder = new ArrayList<>();
        // copy over information from the builder
        myDbName = builder.dbName;
        myDbDirPath = builder.pathToDirectory;
        myDbPath = myDbDirPath.resolve(myDbName);
        myEmbeddedDS = new EmbeddedDataSource();
        myEmbeddedDS.setDatabaseName(myDbPath.toString());
        // copy over the possible script specifications
        myCreationScriptPath = builder.pathToCreationScript;
        myTableScriptPath = builder.pathToTablesOnlyScript;
        myInsertionScriptPath = builder.pathToInsertScript;
        myAlterScriptPath = builder.pathToAlterScript;
        myExcelInsertPath = builder.pathToExcelWorkbook;
        // start the build process
        if (builder.createFlag == true) {
            myEmbeddedDS.setCreateDatabase("create");
            openConnection();
            myEmbeddedDS.setCreateDatabase(null);
            if (myCreationScriptPath != null) {
                // full creation script provided
                executeCreationScript(myCreationScriptPath);
            } else {
                if (myTableScriptPath != null) {
                    // use script to create database structure
                    executeCreateTablesOnlyScript(myTableScriptPath);
                    // now check for insert
                    if (myInsertionScriptPath != null) {
                        // prefer insert via SQL script if it exists
                        executeInsertionScript(myInsertionScriptPath);
                    } else {
                        // could be Excel insert
                        if (myExcelInsertPath != null) {
                            setInsertTableOrder(builder.tableNames);
                            ExcelUtil.writeWorkbookToDatabase(myExcelInsertPath, true, this, myInsertTableOrder);
                        }
                    }
                    // now check for alter
                    if (myAlterScriptPath != null) {
                        executeAlterScript(myAlterScriptPath);
                    }
                }
            }
        } else {
            openConnection();
        }
    }

    private void openConnection() throws SQLException {
        myConnection = myEmbeddedDS.getConnection();
        DatabaseMetaData metaData = myConnection.getMetaData();
        myConnURL = metaData.getURL();
        myDbSchemaName = metaData.getUserName();
        logger.trace("Connection made to {}", myEmbeddedDS.getDatabaseName());
        logWarnings(myConnection);
    }

    /**
     * Creates a Derby database with the given name in the current working directory
     *
     * @param name the name of the database
     * @return the new database after creation
     */
    public static final FirstDbBuilderStepIfc createDb(String name) {
        return new DbBuilder(name, Paths.get("."), true);
    }

    /**
     * Creates a Derby database with the given name in the specified directory
     *
     * @param name        the name of the database
     * @param dbDirectory a path to the directory that holds the database
     * @return the new database after creation
     */
    public static final FirstDbBuilderStepIfc createDb(String name, Path dbDirectory) {
        return new DbBuilder(name, dbDirectory, true);
    }

    /**
     * Connects to a Derby database with the given name in the current working directory
     *
     * @param name the name of the database
     * @return the new database after creation
     */
    public static final DbConnector connectDb(String name) {
        return new DbConnector(name, Paths.get("."));
    }

    /**
     * Connects to a Derby database with the given name in the specified directory
     *
     * @param name        the name of the database
     * @param dbDirectory a path to the directory that holds the database
     * @return the new database after creation
     */
    public static final DbConnector connectDb(String name, Path dbDirectory) {
        return new DbConnector(name, dbDirectory);
    }

    public static final class DbConnector implements DbConnectStepIfc {
        private String dbName;
        private Path pathToDirectory;

        public DbConnector(String name, Path dbDirectory) {
            if (name == null) {
                throw new IllegalArgumentException("The provided name was null");
            }
            if (dbDirectory == null) {
                throw new IllegalArgumentException("The provided directory was null");
            }
            dbName = name;
            pathToDirectory = dbDirectory;
        }

        @Override
        public EmbeddedDerbyDatabase connect() throws IOException, SQLException, InvalidFormatException {
            DbBuilder dbBuilder = new DbBuilder(dbName, pathToDirectory, false);
            EmbeddedDerbyDatabase db = null;
            return dbBuilder.connect();
        }
    }

    /**
     * Provides the build process for creating instances of EmbeddedDerbyDatabase.
     * The assumption is that the creation script only creates the tables with no
     * constraints on keys. The insertion process can be either through Excel
     * or a script having SQL insert statements. The alter script then places the key and
     * other contraints on the database.  We assume that valid data and scripts are in place.
     */
    public static final class DbBuilder implements DbConnectStepIfc, WithCreateScriptStepIfc,
            WithTablesOnlyScriptStepIfc, FirstDbBuilderStepIfc, AfterTablesOnlyStepIfc,
            DbInsertStepIfc, DBAfterInsertStepIfc, DBAddConstraintsStepIfc {

        private String dbName;
        private Path pathToDirectory;
        private Path pathToCreationScript;
        private Path pathToTablesOnlyScript;
        private Path pathToInsertScript;
        private Path pathToExcelWorkbook;
        private Path pathToAlterScript;
        private boolean createFlag;
        private List<String> tableNames;

        public DbBuilder(String name, Path dbDirectory, boolean createFlag) {
            this.createFlag = createFlag;
            if (name == null) {
                throw new IllegalArgumentException("The provided name was null");
            }
            if (dbDirectory == null) {
                throw new IllegalArgumentException("The provided directory was null");
            }
            dbName = name;
            pathToDirectory = dbDirectory;
        }

        @Override
        public DbConnectStepIfc withCreationScript(Path pathToScript) {
            if (pathToScript == null) {
                throw new IllegalArgumentException("The provided creation script path was null");
            }
            pathToCreationScript = pathToScript;
            return this;
        }

        @Override
        public AfterTablesOnlyStepIfc withTables(Path pathToScript) {
            if (pathToScript == null) {
                throw new IllegalArgumentException("The provided table script path was null");
            }
            pathToTablesOnlyScript = pathToScript;
            return this;
        }

        @Override
        public DBAfterInsertStepIfc withExcelData(Path toExcelWorkbook, List<String> tableNames) {
            if (toExcelWorkbook == null) {
                throw new IllegalArgumentException("The provided workbook script path was null");
            }
            if (tableNames == null) {
                throw new IllegalArgumentException("The provided list of table names was null");
            }
            pathToExcelWorkbook = toExcelWorkbook;
            this.tableNames = new ArrayList<>(tableNames);
            return this;
        }

        @Override
        public DBAfterInsertStepIfc withInsertData(Path toInsertScript) {
            if (toInsertScript == null) {
                throw new IllegalArgumentException("The provided inset script path was null");
            }
            pathToInsertScript = toInsertScript;
            return this;
        }

        @Override
        public DbConnectStepIfc withConstraints(Path toAlterScript) {
            if (toAlterScript == null) {
                throw new IllegalArgumentException("The provided alter script path was null");
            }
            pathToAlterScript = toAlterScript;
            return this;
        }

        @Override
        public EmbeddedDerbyDatabase connect() throws IOException, SQLException, InvalidFormatException {
            return new EmbeddedDerbyDatabase(this);
        }

    }

    public interface FirstDbBuilderStepIfc extends DbConnectStepIfc, WithCreateScriptStepIfc, WithTablesOnlyScriptStepIfc {

    }

    /**
     * Allows the user to specify a full creation script that puts the database into
     * the state desired by the user.
     */
    public interface WithCreateScriptStepIfc {
        /**
         * @param pathToCreationScript a path to a full creation script that specifies the database, must not be null
         * @return A build step to permit connecting
         */
        DbConnectStepIfc withCreationScript(Path pathToCreationScript);
    }

    /**
     * Allows the user to specify a script that creates the tables of the database
     */
    public interface WithTablesOnlyScriptStepIfc {
        /**
         * @param pathToScript a path to a script that specifies the database tables, must not be null
         * @return A build step to permit connecting
         */
        AfterTablesOnlyStepIfc withTables(Path pathToScript);
    }

    public interface AfterTablesOnlyStepIfc extends DbConnectStepIfc, DbInsertStepIfc {

    }

    public interface DbCreateStepIfc extends DbConnectStepIfc {
        /**
         * @param toCreateScript the path to a script that will create the database, must not be null
         * @return a reference to the insert step in the build process
         */
        DbInsertStepIfc using(Path toCreateScript);

    }

    public interface DbInsertStepIfc extends DbConnectStepIfc {

        /**
         * @param toExcelWorkbook a path to an Excel workbook that can be read to insert
         *                        data into the database, must not be null
         * @param tableNames      a list of table names that need to be filled. Sheets in
         *                        the workbook must correspond exactly to these names
         * @return a reference to the alter step in the build process
         */
        DBAfterInsertStepIfc withExcelData(Path toExcelWorkbook, List<String> tableNames);

        /**
         * @param toInsertScript a path to an SQL script that can be read to insert
         *                       data into the database, must not be null
         * @return a reference to the alter step in the build process
         */
        DBAfterInsertStepIfc withInsertData(Path toInsertScript);

    }

    public interface DBAddConstraintsStepIfc extends DbConnectStepIfc {
        /**
         * @param toConstraintScript a path to an SQL script that can be read to alter the
         *                           table structure of the database and add constraints, must not be null
         * @return a reference to the alter step in the build process
         */
        DbConnectStepIfc withConstraints(Path toConstraintScript);
    }

    public interface DBAfterInsertStepIfc extends DBAddConstraintsStepIfc, DbConnectStepIfc {

    }

    public interface DbConnectStepIfc {
        /**
         * Finalizes the build process and connects to the database
         *
         * @return an instance of EmbeddedDerbyDatabase
         */
        EmbeddedDerbyDatabase connect() throws IOException, SQLException, InvalidFormatException;
    }

    /**
     * The name of the database
     *
     * @return the name
     */
    @Override
    public final String getName() {
        return myDbName;
    }

    public final Path getDirectory() {
        return Paths.get(myDbDirPath.toUri());
    }

    /**
     * A URL representation of the embedded database
     *
     * @return the URL
     */
    @Override
    public final String getURL() {
        return myConnURL;
    }

    /**
     * @return the path representation for the database
     */
    public final Path getDBPath() {
        return Paths.get(myDbPath.toUri());
    }

    /**
     * The connection to the embedded database
     *
     * @return the connection
     */
    protected final Connection getConnection() {
        return myConnection;
    }

    /**
     * @return an EmbeddedDataSource for working with the database
     */
    public final EmbeddedDataSource getEmbeddedDataSource() {
        return myEmbeddedDS;
    }

    /**
     * @return the meta data about the database
     * @throws SQLException an sql exception
     */
    @Override
    public final DatabaseMetaData getDatabaseMetaData() throws SQLException {
        return myConnection.getMetaData();
    }

    /**
     * @return the schema name for the database (if defined)
     */
    @Override
    public final String getDBSchemaName() {
        return myDbSchemaName;
    }

    /**
     * @return the sql dialect for the database.  Here should be derby
     */
    @Override
    public final SQLDialect getSQLDialect() {
        return mySQLDialect;
    }

    /**
     * @return the path to the tables only script
     */
    @Override
    public final Path getCreationScriptPath() {
        return myCreationScriptPath;
    }

    /**
     * Sets the path, but does not execute the script
     *
     * @param path
     */
    @Override
    public void setCreationScriptPath(Path path) {
        myCreationScriptPath = path;
    }

    /**
     * @return the path to the tables only script
     */
    @Override
    public final Path getTablesOnlyScriptPath() {
        return myTableScriptPath;
    }

    /**
     * Sets the path, but does not execute the script
     *
     * @param path
     */
    @Override
    public void setTablesOnlyScriptPath(Path path) {
        myTableScriptPath = path;
    }

    /**
     * @return the path to the insertion script
     */
    @Override
    public final Path getInsertionScriptPath() {
        return myInsertionScriptPath;
    }

    /**
     * Sets the path, but does not execute the script
     *
     * @param path
     */
    @Override
    public void setInsertionScriptPath(Path path) {
        myInsertionScriptPath = path;
    }

    /**
     * Sets the path, but does not execute the script
     *
     * @param path
     */
    @Override
    public void setAlterScriptPath(Path path) {
        myAlterScriptPath = path;
    }

    /**
     * Sets the path, but does not cause any inserts
     *
     * @param path
     */
    @Override
    public void setExcelInsertPath(Path path) {
        myExcelInsertPath = path;
    }

    /**
     * @return the path to the Excel workbook that holds data for inserts
     */
    @Override
    public Path getExcelInsertPath() {
        return myExcelInsertPath;
    }

    /**
     * @return the path to a script that can alter the database
     */
    @Override
    public Path getAlterScriptPath() {
        return myAlterScriptPath;
    }

    /**
     * @return a list of table names in the order that they must be truncated. May be empty.
     */
    @Override
    public List<String> getTruncateTableOrder() {
        return Collections.unmodifiableList(myTruncateTableOrder);
    }

    /**
     * @return a list of table names in the order that they must be inserted. May be empty
     */
    @Override
    public List<String> getInsertTableOrder() {
        return Collections.unmodifiableList(myInsertTableOrder);
    }

    /**
     * @return a list of strings representing the creation commands for the database. May be empty.
     */
    @Override
    public List<String> getCreateCommands() {
        return Collections.unmodifiableList(myTableCommands);
    }

    /**
     * @return a list of strings representing the insertion commands for the database. May be empty.
     */
    @Override
    public List<String> getInsertCommands() {
        return Collections.unmodifiableList(myInsertCommands);
    }

    /**
     * @return a list of strings representing the insertion commands for the database. May be empty.
     */
    @Override
    public List<String> getAlterCommands() {
        return Collections.unmodifiableList(myAlterCommands);
    }

    /**
     * @param tableNames the names of the tables in the order needed for truncation
     */
    @Override
    public final void setTruncateTableOrder(List<String> tableNames) {
        if (tableNames == null) {
            throw new IllegalArgumentException("The truncate table name array must not be null");
        }
        myTruncateTableOrder.clear();
        myTruncateTableOrder.addAll(tableNames);
    }

    /**
     * @param tableNames the names of the tables in the order needed for insertion
     */
    @Override
    public final void setInsertTableOrder(List<String> tableNames) {
        if (tableNames == null) {
            throw new IllegalArgumentException("The truncate table name array must not be null");
        }
        myInsertTableOrder.clear();
        myInsertTableOrder.addAll(tableNames);
    }

    /**
     * @param pathToScript sets and executes the commands in the script for creating only tables in the database
     * @return true if all commands executed
     */
    @Override
    public final boolean executeCreateTablesOnlyScript(Path pathToScript) throws IOException {
        if (pathToScript == null) {
            throw new IllegalArgumentException("The creation script path must not be null");
        }
        if (Files.notExists(pathToScript)) {
            throw new IllegalArgumentException("The creation script file does not exist");
        }
        setTablesOnlyScriptPath(pathToScript);
        List<String> parsedCmds = parseQueriesInSQLScript(pathToScript);
        myTableCommands.clear();
        myTableCommands.addAll(parsedCmds);
        return executeCommands(myTableCommands);
    }

    /**
     * @param pathToScript sets and executes the commands in the script for creating the database
     * @return true if all commands executed
     */
    @Override
    public final boolean executeCreationScript(Path pathToScript) throws IOException {
        if (pathToScript == null) {
            throw new IllegalArgumentException("The creation script path must not be null");
        }
        if (Files.notExists(pathToScript)) {
            throw new IllegalArgumentException("The creation script file does not exist");
        }
        setCreationScriptPath(pathToScript);
        List<String> parsedCmds = parseQueriesInSQLScript(pathToScript);
        myCreationScriptCommands.clear();
        myCreationScriptCommands.addAll(parsedCmds);
        return executeCommands(myCreationScriptCommands);
    }

    /**
     * @param pathToScript sets and executes the commands in the script for inserting data into the database
     * @return true if all commands executed
     */
    @Override
    public final boolean executeInsertionScript(Path pathToScript) throws IOException {
        if (pathToScript == null) {
            throw new IllegalArgumentException("The insertion script path must not be null");
        }
        if (Files.notExists(pathToScript)) {
            throw new IllegalArgumentException("The insertion script file does not exist");
        }
        setInsertionScriptPath(pathToScript);
        List<String> parsedCmds = parseQueriesInSQLScript(pathToScript);
        myInsertCommands.clear();
        myInsertCommands.addAll(parsedCmds);
        return executeCommands(myInsertCommands);
    }

    /**
     * @param pathToScript sets and executes the commands in the script for altering the database
     * @return true if all commands executed
     */
    @Override
    public final boolean executeAlterScript(Path pathToScript) throws IOException {
        if (pathToScript == null) {
            throw new IllegalArgumentException("The alter script path must not be null");
        }
        if (Files.notExists(pathToScript)) {
            throw new IllegalArgumentException("The alter script file does not exist");
        }
        setAlterScriptPath(pathToScript);
        List<String> parsedCmds = parseQueriesInSQLScript(pathToScript);
        myAlterCommands.clear();
        myAlterCommands.addAll(parsedCmds);
        return executeCommands(myAlterCommands);
    }

    /**
     * Returns the names of the tables in the current database
     *
     * @return a list of the names of the tables as strings
     */
    @Override
    public List<String> getTableNames() {
        List<Table<?>> tables = getTables();
        List<String> list = new ArrayList<>();
        for (Table t : tables) {
            list.add(t.getName());
        }
        return list;
    }

    /**
     * @param schemaName the name to find the schema for
     * @return the jooq schema for the name
     */
    protected Schema getSchema(String schemaName) {
        Meta meta = getDSLContext().meta();
        List<Schema> schemas = meta.getSchemas();
        Schema found = null;
        for (Schema s : schemas) {
            if (s.getName().equals(schemaName)) {
                found = s;
                break;
            }
        }
        return found;
    }

    /**
     * @return the use defined schema (as opposed to the system defined schema)
     */
    @Override
    public Schema getUserSchema() {
        return getSchema(getDBSchemaName());
    }

    /**
     * @param tableName the name to get the Table for
     * @return the jooq Table associated with the supplied name
     */
    @Override
    public Table<? extends Record> getTable(String tableName) {
        return getUserSchema().getTable(tableName);
    }

    /**
     * @param table the table to check
     * @return true if the databased contains the supplied table
     */
    @Override
    public boolean containsTable(Table<?> table) {
        return getUserSchema().getTables().contains(table);
    }

    /**
     * @return a list of jooq Tables in the user schema
     */
    @Override
    public List<Table<?>> getTables() {
        return getUserSchema().getTables();
    }

    /**
     * Checks if tables exist in the database
     *
     * @return true if it exists
     */
    @Override
    public boolean hasTables() {
        return (!getTables().isEmpty());
    }

    /**
     * Checks if the supplied table exists in the database
     *
     * @param table a string representing the name of the table
     * @return true if it exists
     */
    @Override
    public boolean tableExists(String table) {
        return (getTable(table) != null);
    }

    /**
     * @param tableName the name of the table to write
     * @param out       the output file for the writing
     */
    @Override
    public void writeTableAsCSV(String tableName, PrintWriter out) {
        if (!tableExists(tableName)) {
            logger.trace("Table: {} does not exist in database schema {}", tableName, getUserSchema());
            return;
        }
        out.println(selectAll(tableName).formatCSV());
        out.flush();
    }

    /**
     * Displays the named table on the console
     *
     * @param tableName the name of the table to display
     */
    @Override
    public void printTableAsCSV(String tableName) {
        writeTableAsCSV(tableName, new PrintWriter(System.out));
    }

    /**
     * Writes the table in pretty text to the file
     *
     * @param tableName the name of the table to write
     * @param out       the output file to write to
     */
    @Override
    public void writeTableAsText(String tableName, PrintWriter out) {
        if (!tableExists(tableName)) {
            logger.trace("Table: {} does not exist in database schema {}", tableName, getUserSchema());
            return;
        }
        out.println(tableName);
        out.println(selectAll(tableName));
        out.flush();
    }

    /**
     * Displays the name table on the console
     *
     * @param tableName the name of the table to write
     */
    @Override
    public void printTableAsText(String tableName) {
        writeTableAsText(tableName, new PrintWriter(System.out));
    }

    /**
     * Writes all user defined tables to the output
     *
     * @param out the place to write to
     */
    @Override
    public void writeAllTablesAsText(PrintWriter out) {
        List<Table<?>> tables = getTables();
        for (Table table : tables) {
            out.println(table.getName());
            out.println(selectAll(table));
            out.flush();
        }
    }

    /**
     * Displays all the user defined tables as text on the console
     */
    @Override
    public void printAllTablesAsText() {
        writeAllTablesAsText(new PrintWriter(System.out));
    }

    /**
     * @param table the table to get all records from
     * @return the records as a jooq Result
     */
    protected Result<Record> selectAll(Table<? extends Record> table) {
        if (table == null) {
            return null;
        }
        if (!getUserSchema().getTables().contains(table)) {
            return null;
        }
        Result<Record> result = getDSLContext().select().from(table).fetch();
        return result;
    }

    /**
     * @param tableName the name of the table to get all records from
     * @return a jooq result holding all of the records from the table
     */
    @Override
    public Result<Record> selectAll(String tableName) {
        if (!tableExists(tableName)) {
            return null;
        }
        return selectAll(getTable(tableName));
    }

    /**
     * @param table the table to check
     * @return true if the table has no data in the result
     */
    @Override
    public boolean isTableEmpty(String table) {
        Result<Record> selectAll = selectAll(table);
        if (selectAll == null) {
            return true;
        }
        return selectAll.isEmpty();
    }

    /**
     * @param table the table to check
     * @return true if the table has no data in the result
     */
    protected boolean isTableEmpty(Table<? extends Record> table) {
        Result<Record> selectAll = selectAll(table);
        if (selectAll == null) {
            return true;
        }
        return selectAll.isEmpty();
    }

    /**
     * @return true if at least one table has data
     */
    @Override
    public boolean hasData() {
        return areAllTablesEmpty() != true;
    }

    /**
     * @return true if all tables are empty
     */
    @Override
    public boolean areAllTablesEmpty() {
        List<Table<?>> tables = getTables();
        boolean result = true;
        for (Table t : tables) {
            result = isTableEmpty(t);
            if (result == false) {
                break;
            }
        }
        return result;
    }

    /**
     * Writes all tables of the database in the directory, naming each output
     * file the name of each table
     *
     * @param pathToOutPutDirectory
     */
    @Override
    public void writeAllTablesAsCSV(Path pathToOutPutDirectory) throws IOException {

        Files.createDirectories(pathToOutPutDirectory);

        List<Table<?>> tables = getTables();
        for (Table table : tables) {
            Path path = pathToOutPutDirectory.resolve(table.getName() + ".csv");
            OutputStream newOutputStream;

            newOutputStream = Files.newOutputStream(path);
            PrintWriter printWriter = new PrintWriter(newOutputStream);
            printWriter.println(selectAll(table).formatCSV());
            printWriter.flush();
            printWriter.close();

        }
    }

    /**
     * @return a jooq Parser for parsing queries on the database
     */
    @Override
    public Parser getParser() {
        return getDSLContext().parser();
    }

    /**
     * @return the jooq DSLContext for the database
     */
    @Override
    public DSLContext getDSLContext() {
        if (myExecuteLoggingSettings == null){
            return DSL.using(getConnection(), getSQLDialect());
        } else {
            return DSL.using(getConnection(), getSQLDialect(), myExecuteLoggingSettings);
        }
    }

    /**
     *  Turns on JooQ Default execute SQL logging
     */
    @Override
    public final void turnOffJooQDefaultExecutionLogging(){
        myExecuteLoggingSettings = new Settings().withExecuteLogging(false);
    }

    /**
     *  Turns off JooQ Default execute SQL logging
     */
    @Override
    public final void turnOnJooQDefaultExecutionLogging(){
        myExecuteLoggingSettings = null;
    }

    /**
     * @return the DDL queries needed to define and create the database
     */
    private Queries getJOOQDDLQueries() {
        //TODO waiting on jooq fix
        //return create.ddl(getUserSchema(), DDLFlag.TABLE, DDLFlag.PRIMARY_KEY, DDLFlag.UNIQUE, DDLFlag.FOREIGN_KEY);
        return getDSLContext().ddl(getUserSchema());
    }

    /**
     * @return the DDL queries needed to define and create the database as a string
     */
    private String getJOOQDDLQueriesAsString() {
        return getDSLContext().ddl(getUserSchema()).toString();
    }

    /**
     * Writes the DDL queries needed to define and create the database to a file
     *
     * @param out the place to write the queries
     */
    private void writeJOOQDDLQueries(PrintWriter out) {
        //TODO waiting on jooq fix
        Queries ddlQueries = getJOOQDDLQueries();
        Query[] queries = ddlQueries.queries();
        if (queries.length == 1) {
            return;
        }
        for (int i = 1; i < queries.length; i++) {
            out.print(queries[i]);
            out.print(";");
            out.println();
            out.flush();
        }
//        for (Query q : queries) {
//            out.print(q);
//            out.print(";");
//            out.println();
//            out.flush();
//        }
    }

    /**
     * Displays the DDL queries needed to define and create the database on the console
     */
    @Override
    public void printJOOQDDLQueries() {
        writeJOOQDDLQueries(new PrintWriter(System.out));
    }

    /**
     * @return gets the DDL queries as a list of strings
     */
    private List<String> getDDLQueryStrings() {
        //TODO waiting on jooq fix
        List<String> list = new ArrayList<>();
        Queries ddlQueries = getJOOQDDLQueries();
        Query[] queries = ddlQueries.queries();
        if (queries.length == 1) {
            return list;
        }
        for (int i = 1; i < queries.length; i++) {
            list.add(queries[i].getSQL());
        }
//        for (Query q : queries) {
//            list.add(q.getSQL());
//        }
        return list;
    }

    /**
     * @param tableName the name of the table to generate insert statements for
     * @return the insert statements as a string
     */
    @Override
    public String getInsertQueries(String tableName) {
        Table<? extends Record> table = getTable(tableName);
        if (table == null) {
            return null;
        }
        return getInsertQueries(table);
    }

    /**
     * @param table the table to generate the insert statements for
     * @return the insert statements as a string
     */
    protected String getInsertQueries(Table<? extends Record> table) {
        if (table == null) {
            throw new IllegalArgumentException("The supplied tabel was null");
        }
        if (!containsTable(table)) {
            logger.trace("Table: {} does not exist in database schema {}", table.getName(), getUserSchema());
            return null;
        }
        Result<Record> results = selectAll(table);
        return results.formatInsert(table);
    }

    /**
     * Displays the insertation statements for the table on the console
     *
     * @param tableName the name of the table for the insert statements
     */
    @Override
    public void printInsertQueries(String tableName) {
        writeInsertQueries(tableName, new PrintWriter(System.out));
    }

    /**
     * Writes the insertation statements for the table in the file
     *
     * @param tableName the name of the table for the insert statements
     * @param out       the file to write to
     */
    @Override
    public void writeInsertQueries(String tableName, PrintWriter out) {
        if (!tableExists(tableName)) {
            logger.trace("Table: {} does not exist in database schema {}", tableName, getUserSchema());
            return;
        }
        writeInsertQueries(getTable(tableName), out);
    }

    /**
     * Writes the insertation statements for the table in the file
     *
     * @param table the the table for the insert statements
     * @param out   the file to write to
     */
    protected void writeInsertQueries(Table<? extends Record> table, PrintWriter out) {
        if (table == null) {
            throw new IllegalArgumentException("The supplied tabel was null");
        }
        if (!containsTable(table)) {
            logger.trace("Table: {} does not exist in database schema {}", table.getName(), getUserSchema());
            return;
        }
        Result<Record> results = selectAll(table);
        out.print(results.formatInsert(table));
        out.flush();
    }

    /**
     * Displays all the insert statements for the database on the console
     */
    @Override
    public void printAllTablesAsInsertQueries() {
        writeAllTablesAsInsertQueries(new PrintWriter(System.out));
    }

    /**
     * Writes all the insert queries for the entire database
     *
     * @param out the place to write the queries
     */
    @Override
    public void writeAllTablesAsInsertQueries(PrintWriter out) {
        List<Table<?>> tables = getTables();
        for (Table t : tables) {
            writeInsertQueries(t, out);
        }
    }

    /**
     * Displays all the insert statements for the database on the console
     * in the order specfied by defined insertion order
     */
    @Override
    public void printAllTablesAsInsertQueriesUsingInsertOrder() {
        writeAllTablesAsInsertQueriesUsingInsertOrder(new PrintWriter(System.out));
    }

    /**
     * Writes all the insertion queries in the insertion order to the location
     *
     * @param out the place to write the queries
     */
    @Override
    public void writeAllTablesAsInsertQueriesUsingInsertOrder(PrintWriter out) {
        List<String> insertTableOrder = getInsertTableOrder();
        for (String name : insertTableOrder) {
            writeInsertQueries(name, out);
        }
    }

    /**
     * Writes all the tables to an Excel workbook, uses name of database, uses the working directory
     */
    @Override
    public void writeDbToExcelWorkbook() throws IOException {
        writeDbToExcelWorkbook(null, null);
    }

    /**
     * Writes all the tables to an Excel workbook, uses name of database
     *
     * @param wbDirectory directory of the workbook, if null uses the working directory
     */
    @Override
    public void writeDbToExcelWorkbook(Path wbDirectory) throws IOException {
        writeDbToExcelWorkbook(null, wbDirectory);
    }

    /**
     * Writes all the tables to an Excel workbook uses the working directory
     *
     * @param wbName name of the workbook, if null uses name of database
     */
    @Override
    public void writeDbToExcelWorkbook(String wbName) throws IOException {
        writeDbToExcelWorkbook(wbName, null);
    }

    /**
     * Writes all the tables to an Excel workbook
     *
     * @param wbName      name of the workbook, if null uses name of database
     * @param wbDirectory directory of the workbook, if null uses the working directory
     */
    @Override
    public void writeDbToExcelWorkbook(String wbName, Path wbDirectory) throws IOException {
        if (wbName == null) {
            wbName = getName();
        }
        if (wbDirectory == null) {
            wbDirectory = Paths.get(".");
        }
        Path path = wbDirectory.resolve(wbName);
        ExcelUtil.writeDBAsExcelWorkbook(this, path);

    }


    /**
     * Executes the SQL provided in the string. Squelches exceptions The string
     * must not have ";" semi-colon at the end.
     *
     * @param cmd the command
     * @return true if the command executed
     */
    @Override
    public final boolean executeCommand(String cmd) throws SQLException {
        Statement statement = null;
        boolean flag = false;
        try {
            statement = myConnection.createStatement();
            statement.execute(cmd);
            logger.trace("Executed SQL: {}", cmd);
            statement.close();
            flag = true;
        } catch (SQLException ex) {
            logger.error("SQLException when executing {}", cmd, ex);
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex1) {
                logger.error("SQLException when closing statement {}", cmd, ex);
            }
        }

        return flag;
    }

    /**
     * Consecutively executes the list of SQL queries supplied as a list of
     * strings The strings must not have ";" semi-colon at the end.
     *
     * @param cmds the commands
     * @return true if all commands were executed
     */
    @Override
    public final boolean executeCommands(List<String> cmds) {
        boolean flag = true;
        try {
            myConnection.setAutoCommit(false);
            for (String cmd : cmds) {
                flag = executeCommand(cmd);
                if (flag == false) {
                    myConnection.rollback();
                    break;
                }
            }
            if (flag == true) {
                myConnection.commit();
            }
            myConnection.setAutoCommit(true);
        } catch (SQLException ex) {
            logger.error("SQLException: ", ex);
            try {
                myConnection.rollback();
            } catch (SQLException ex1) {
                logger.error("SQLException: ", ex);
            }
        }

        return flag;
    }

    /**
     * Executes the commands in the script on the database
     *
     * @param path the path
     * @return
     */
    @Override
    public final boolean executeScript(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("The script path must not be null");
        }
        if (Files.notExists(path)) {
            throw new IllegalArgumentException("The script file does not exist");
        }
        logger.trace("Executing SQL in file: {}", path);
        return executeCommands(parseQueriesInSQLScript(path));
    }

    /** Uses the active database connection and derby system commands to freeze the database,
     * uses system OS commands to copy the database, and then unfreezes the database.  The duplicate name
     * and directory path must not already exist
     *
     * @param dupName the name of the duplicate database
     * @param directory the directory to place the database in
     * @throws SQLException thrown if the derby commands fail
     * @throws IOException thrown if the system file copy commands fail
     */
    @Override
    public final void copyDb(String dupName, Path directory) throws SQLException, IOException {
        if (dupName == null) {
            throw new IllegalArgumentException("The duplicate's name must not be null!");
        }
        if (directory == null) {
            throw new IllegalArgumentException("The directory must not be null!");
        }
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("The directory path was not a directory!");
        }

        if (Files.exists(directory.resolve(dupName))) {
            throw new IllegalArgumentException("A database with the supplied name already exists in the directory! db name = " + dupName);
        }

        Statement s = getConnection().createStatement();
        // freeze the database
        s.executeUpdate("CALL SYSCS_UTIL.SYSCS_FREEZE_DATABASE()");
        //copy the database directory during this interval
        // translate paths to files
        File target = directory.resolve(dupName).toFile();
        File source = this.getDBPath().toFile();
        FileUtils.copyDirectory(source, target);
        s.executeUpdate("CALL SYSCS_UTIL.SYSCS_UNFREEZE_DATABASE()");
        s.close();
    }

    /** Duplicates the database into a new database with the supplied name and directory.
     *  Assumes that the source database has no active connections and performs a file system copy
     *
     * @param sourceDB the path to the database that needs duplicating
     * @param dupName the name of the duplicate database
     * @param directory the directory to place the database in
     * @throws IOException thrown if the system file copy commands fail
     */
    public static void copyDatabase(Path sourceDB, String dupName, Path directory) throws IOException {
        if (sourceDB == null) {
            throw new IllegalArgumentException("The path to the source must not be null!");
        }

        if (dupName == null) {
            throw new IllegalArgumentException("The duplicate's name must not be null!");
        }
        if (directory == null) {
            throw new IllegalArgumentException("The directory must not be null!");
        }
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("The directory path was not a directory!");
        }

        if (Files.exists(directory.resolve(dupName))) {
            throw new IllegalArgumentException("A database with the supplied name already exists in the directory! db name = " + dupName);
        }

        File target = directory.resolve(dupName).toFile();
        File source = sourceDB.toFile();
        FileUtils.copyDirectory(source, target);
    }

    /**
     * Writes SQLWarnings to log file
     *
     * @param conn the connection
     * @throws SQLException the exception
     */
    public static void logWarnings(Connection conn) throws SQLException {
        SQLWarning warning = conn.getWarnings();
        if (warning != null) {
            while (warning != null) {
                logger.warn("Message: {}", warning.getMessage());
                warning = warning.getNextWarning();
            }
        }
    }

    /**
     * Method to parse a SQL script for the database. The script honors SQL
     * comments and separates each SQL command into a list of strings, 1 string
     * for each command. The list of queries is returned.
     * <p>
     * The script should have each command end in a semi-colon, ; The best
     * comment to use is #. All characters on a line after # will be stripped.
     * Best to put # as the first character of a line with no further SQL on the
     * line
     * <p>
     * Based on the work described here:
     * <p>
     * https://blog.heckel.xyz/2014/06/22/run-sql-scripts-from-java-on-hsqldb-derby-mysql/
     *
     * @param filePath a path to the file for parsing
     * @return the list of strings of the commands
     */
    public static List<String> parseQueriesInSQLScript(Path filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("The supplied path was null!");
        }
        List<String> queries = new ArrayList<>();
        InputStream in = Files.newInputStream(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder cmd = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            //boolean end = parseCommandString(line, cmd);
            LineOption option = parseLine(line, cmd);
            if (option == LineOption.END) {
                String trimmedString = cmd.toString().trim();
                //System.out.println(trimmedString);
                queries.add(trimmedString);
                cmd = new StringBuilder();
            }
        }
        return queries;
    }

    /**
     * Parses the supplied string and breaks it up into a list of strings The
     * string needs to honor SQL comments and separates each SQL command into a
     * list of strings, 1 string for each command. The list of queries is
     * returned.
     * <p>
     * The script should have each command end in a semi-colon, ; The best
     * comment to use is #. All characters on a line after # will be stripped.
     * Best to put # as the first character of a line with no further SQL on the
     * line
     *
     * @param str A big string that has SQL queries
     * @return a list of strings representing each SQL command
     */
    public static List<String> parseQueriesInString(String str) throws IOException {
        List<String> queries = new ArrayList<>();
        if (str != null) {
            StringReader sr = new StringReader(str); // wrap your String
            BufferedReader reader = new BufferedReader(sr); // wrap your StringReader
            StringBuilder cmd = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                //boolean end = parseCommandString(line, cmd);
                LineOption option = parseLine(line, cmd);
                if (option == LineOption.END) {
                    queries.add(cmd.toString().trim());
                    cmd = new StringBuilder();
                }
            }
        }
        return queries;
    }

    /**
     * Takes the input string and builds a string to represent the command from
     * the string.
     *
     * @param input   the input to parse
     * @param command the parsed output
     * @return true if the parse was successful
     */
    public static boolean parseCommandString(String input, StringBuilder command) {
        String delimiter = DEFAULT_DELIMITER;
        String trimmedLine = input.trim();

        Matcher delimiterMatcher = NEW_DELIMITER_PATTERN.matcher(trimmedLine);
        Matcher commentMatcher = COMMENT_PATTERN.matcher(trimmedLine);

        if (delimiterMatcher.find()) {
            // a) Delimiter change
            delimiter = delimiterMatcher.group(1);
            //LOGGER.log(Level.INFO, "SQL (new delimiter): {0}", delimiter);
        } else if (commentMatcher.find()) {
            // b) Comment
            //LOGGER.log(Level.INFO, "SQL (comment): {0}", trimmedLine);
        } else { // c) Statement
            command.append(trimmedLine);
            command.append(" ");
            // End of statement
            if (trimmedLine.endsWith(delimiter)) {
                command.delete(command.length() - delimiter.length() - 1, command.length());
                logger.trace("Parsed SQL: {}", command);
                return true;
            }
        }
        return false;
    }

    /**
     * Takes the input string and builds a string to represent the SQL command from
     * the string. Uses EmbeddedDerbyDatabase.DEFAULT_DELIMITER as the delimiter, i.e. ";"
     * Checks for "--", "//" and "#" as start of line comments
     *
     * @param line    the input to parse
     * @param command the parsed output
     * @return the LineOption COMMENT means line was a comment, CONTINUED means that
     * command continues on next line, END means that command was ended with the delimiter
     */
    public static LineOption parseLine(String line, StringBuilder command) {
        return parseLine(line, DEFAULT_DELIMITER, command);
    }

    /**
     * Takes the input string and builds a string to represent the SQL command from
     * the string.  Checks for "--", "//" and "#" as start of line comments
     *
     * @param line      the input to parse
     * @param delimiter the end of comand indicator
     * @param command   the parsed output
     * @return the LineOption COMMENT means line was a comment, CONTINUED means that
     * command continues on next line, END means that command was ended with the delimiter
     */
    public static LineOption parseLine(String line, String delimiter, StringBuilder command) {
        String trimmedLine = line.trim();

        if (trimmedLine.startsWith("--")
                || trimmedLine.startsWith("//")
                || trimmedLine.startsWith("#")) {
            return LineOption.COMMENT;
        }
        // not a comment, could be end of command or continued on next line
        // add the line to the command
        //command.append(trimmedLine);
        if (trimmedLine.endsWith(delimiter)) {
            // remove the delimiter
            trimmedLine = trimmedLine.replaceFirst(delimiter, " ");
            trimmedLine = trimmedLine.trim();
            command.append(trimmedLine);
//            command.delete(command.length() - delimiter.length() - 1, command.length());
            command.append(" ");
            return LineOption.END;
        }
        command.append(trimmedLine);
        command.append(" ");
        // already added the line, command must be continued on next line
        return LineOption.CONTINUED;
    }

    /**
     * Runs jooq code generation on the database at the supplied path.  Assumes
     * that the database exists and has well defined structure.  Places generated
     * source files in package gensrc with the main java source
     *
     * @param pathToDb path to database, must not be null
     * @param packageName name of package to be created to hold generated code, must not be null
     */
    public static void jooqCodeGeneration(Path pathToDb, String pkgDirName, String packageName) throws Exception {
        if (pathToDb == null) {
            throw new IllegalArgumentException("The path was null!");
        }

        if (pkgDirName == null) {
            throw new IllegalArgumentException("The package directory name was null!");
        }

        if (packageName == null) {
            throw new IllegalArgumentException("The package name was null!");
        }

        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName(pathToDb.toString());

        Connection connection = ds.getConnection();
        org.jooq.util.jaxb.Configuration configuration = new org.jooq.util.jaxb.Configuration()
                .withGenerator(new Generator()
                        .withDatabase(new Database()
                                .withName("org.jooq.util.derby.DerbyDatabase")
                                .withIncludes(".*")
                                .withExcludes("")
                                .withInputSchema(connection.getSchema()))
                        .withTarget(new Target()
                                .withPackageName(packageName)
                                .withDirectory(pkgDirName))
                        .withGenerate(new Generate()
                            //    .withDaos(true)
                            //    .withPojosToString(true)
                            //    .withImmutablePojos(true)
                                .withIndexes(true)
                                .withLinks(true)));

        GenerationTool tool = new GenerationTool();
        tool.setConnection(connection);
        tool.run(configuration);
    }

    /**
     * Runs jooq code generation on the database at the supplied path, squelching all exceptions
     *
     * @param pathToDb path to database, must not be null
     * @param packageName name of package to be created to hold generated code, must not be null
     */
    public static void runJooQCodeGeneration(Path pathToDb, String pkgDirName, String packageName) {
        try {
            jooqCodeGeneration(pathToDb, pkgDirName, packageName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.trace("Error in jooq code generation for database: {}", pathToDb);
        }
    }


}
