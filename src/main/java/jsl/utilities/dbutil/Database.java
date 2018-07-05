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

package jsl.utilities.dbutil;

import jsl.utilities.excel.ExcelUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jooq.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A concrete implementation of the DatabaseIfc interface.
 * <p>
 * Many databases define the terms database, user, schema in a variety of ways. This abstraction
 * defines this concept as the userSchema.  It is the name of the organizational construct for
 * which the user defined database objects are contained. These are not the system abstractions.
 * The database label provided to the construct is for labeling and may or may not have any relationship
 * to the actual file name or database name of the database. The supplied DataSource has all
 * the information that it needs to access the database.
 */
public class Database implements DatabaseIfc {

    static {
        System.setProperty("org.jooq.no-logo", "true");
    }

    private final String myLabel;
    private final DataSource myDataSource;
    private final SQLDialect mySQLDialect;
    private String myDefaultSchemaName;
    private DSLContext myDSLContext;

    /**
     * @param dbLabel    a string representing a label for the database must not be null. This label
     *                   may or may not have any relation to the actual name of the database. This is
     *                   used for labeling purposes.
     * @param dataSource the DataSource backing the database, must not be null
     * @param dialect    the SLQ dialect for this type of database, must not null, it obviously must
     *                   be consistent with the database referenced by the connection
     */
    public Database(String dbLabel, DataSource dataSource, SQLDialect dialect) {
        Objects.requireNonNull(dbLabel, "The database name was null");
        Objects.requireNonNull(dataSource, "The database source was null");
        Objects.requireNonNull(dialect, "The database dialect was null");
        myLabel = dbLabel;
        myDataSource = dataSource;
        mySQLDialect = dialect;
        myDSLContext = DSL.using(dataSource, dialect);
        setJooQDefaultExecutionLoggingOption(false);
    }

    @Override
    public final DataSource getDataSource() {
        return myDataSource;
    }

    @Override
    public final String getLabel() {
        return myLabel;
    }

    @Override
    public final SQLDialect getSQLDialect() {
        return mySQLDialect;
    }

    @Override
    public DSLContext getDSLContext() {
        return myDSLContext;
    }

    @Override
    public String getDefaultSchemaName() {
        return myDefaultSchemaName;
    }

    @Override
    public void setDefaultSchemaName(String defaultSchemaName) {
        myDefaultSchemaName = defaultSchemaName;
        if (defaultSchemaName != null) {
            if (!containsSchema(defaultSchemaName)) {
                DbLogger.warn("The supplied default schema name {} was not in the database {}.",
                        defaultSchemaName, myLabel);
            }
        } else {
            DbLogger.warn("The default schema name was set to null for database {}.", myLabel);
        }
    }

    /**
     * Use to build a DbCreateTask that can then be executed on the database.
     *
     * @return a builder that can be used to build a properly configured DbCreateTask
     */
    public static DbCreateTaskFirstStepIfc creationTaskBuilder() {
        return new DbCreateTaskBuilder();
    }

    /**
     * Attempts to execute a configured set of tasks that will create, possibly fill, and
     * alter the database.
     *
     * @param task a configured database creation task
     * @return true if the task was executed correctly, false otherwise
     */
    public final boolean executeCreateTask(DbCreateTask task) {
        switch (task.type) {
            case NONE:
                DbLogger.warn("Attempted to execute a create task with no commands.\n {}", task);
                break;
            case FULL_SCRIPT:
                if (executeCommands(task.myCreationScriptCommands)) {
                    task.type = DbCreateTask.Type.EXECUTED;
                } else {
                    task.type = DbCreateTask.Type.EXECUTION_ERROR;
                }
                DbLogger.info("Executed a full script create task.\n {}", task);
                break;
            case TABLES:
                if (executeCommands(task.myTableCommands)) {
                    task.type = DbCreateTask.Type.EXECUTED;
                } else {
                    task.type = DbCreateTask.Type.EXECUTION_ERROR;
                }
                DbLogger.info("Executed tables only create task. \n{}", task);
                break;
            case TABLES_INSERT:
                if (executeCommands(task.myTableCommands)) {
                    if (executeCommands(task.myInsertCommands)) {
                        task.type = DbCreateTask.Type.EXECUTED;
                    } else {
                        task.type = DbCreateTask.Type.EXECUTION_ERROR;
                    }
                } else {
                    task.type = DbCreateTask.Type.EXECUTION_ERROR;
                }
                DbLogger.info("Executed a tables plus insert create task.\n{}", task);
                break;
            case TABLES_ALTER:
                if (executeCommands(task.myTableCommands)) {
                    if (executeCommands(task.myAlterCommands)) {
                        task.type = DbCreateTask.Type.EXECUTED;
                    } else {
                        task.type = DbCreateTask.Type.EXECUTION_ERROR;
                    }
                } else {
                    task.type = DbCreateTask.Type.EXECUTION_ERROR;
                }
                DbLogger.info("Executed create tables plus alter tables create task.\n{}", task);
                break;
            case TABLES_INSERT_ALTER:
                if (executeCommands(task.myTableCommands)) {
                    if (executeCommands(task.myInsertCommands)) {
                        if (executeCommands(task.myAlterCommands)) {
                            task.type = DbCreateTask.Type.EXECUTED;
                        } else {
                            task.type = DbCreateTask.Type.EXECUTION_ERROR;
                        }
                    } else {
                        task.type = DbCreateTask.Type.EXECUTION_ERROR;
                    }
                } else {
                    task.type = DbCreateTask.Type.EXECUTION_ERROR;
                }
                DbLogger.info("Executed an create/insert/alter tables create task.\n {}", task);
                break;
            case TABLES_EXCEL:
                if (executeCommands(task.myTableCommands)) {
                    try {
                        ExcelUtil.writeWorkbookToDatabase(task.myExcelInsertPath, true, this,
                                task.myInsertTableOrder);
                        task.type = DbCreateTask.Type.EXECUTED;
                    } catch (IOException | InvalidFormatException e) {
                        task.type = DbCreateTask.Type.EXECUTION_ERROR;
                    }
                } else {
                    task.type = DbCreateTask.Type.EXECUTION_ERROR;
                }
                DbLogger.info("Executed a tables create plus Excel import task. \n{}", task);
                break;
            case TABLES_EXCEL_ALTER:
                if (executeCommands(task.myTableCommands)) {
                    try {
                        ExcelUtil.writeWorkbookToDatabase(task.myExcelInsertPath, true, this,
                                task.myInsertTableOrder);
                        if (executeCommands(task.myAlterCommands)) {
                            task.type = DbCreateTask.Type.EXECUTED;
                        } else {
                            task.type = DbCreateTask.Type.EXECUTION_ERROR;
                        }
                    } catch (IOException | InvalidFormatException e) {
                        task.type = DbCreateTask.Type.EXECUTION_ERROR;
                    }
                } else {
                    task.type = DbCreateTask.Type.EXECUTION_ERROR;
                }
                DbLogger.info("Executed a tables create plus Excel plus alter import task \n {}.", task);
                break;
            case EXECUTED:
                DbLogger.warn("Tried to execute an already executed create task.\n {}", task);
                break;
            case EXECUTION_ERROR:
                DbLogger.error("Tried to execute a previously executed task that had errors.\n {}", task);
                break;
            case NO_TABLES_ERROR:
                DbLogger.error("Tried to execute a create task with no tables created.\n {}", task);
                break;
        }
        if (task.type == DbCreateTask.Type.EXECUTION_ERROR){
            throw new DataAccessException("There was an execution error for task \n" + task +
                    "\n see jslDbLog.log for details ");
        }
        return task.type == DbCreateTask.Type.EXECUTED;
    }

    /**
     * A DbCreateTask represents a set of instructions that can be used to create, possibily fill,
     * and alter a database. It can be used only once. The enum Type indicates what kind of
     * tasks will be executed and the state of the task.
     */
    public static class DbCreateTask {

        public enum Type {
            NONE, FULL_SCRIPT, TABLES, TABLES_INSERT, TABLES_ALTER,
            TABLES_EXCEL, TABLES_INSERT_ALTER, TABLES_EXCEL_ALTER, EXECUTED, EXECUTION_ERROR, NO_TABLES_ERROR
        }

        private Path myExcelInsertPath;
        private List<String> myInsertTableOrder = new ArrayList<>();
        private List<String> myCreationScriptCommands = new ArrayList<>();
        private List<String> myTableCommands = new ArrayList<>();
        private List<String> myInsertCommands = new ArrayList<>();
        private List<String> myAlterCommands = new ArrayList<>();
        private Type type;

        public Path getExcelInsertPath() {
            return myExcelInsertPath;
        }

        public List<String> getInsertTableOrder() {
            return myInsertTableOrder;
        }

        public List<String> getCreationScriptCommands() {
            return myCreationScriptCommands;
        }

        public List<String> getTableCommands() {
            return myTableCommands;
        }

        public List<String> getInsertCommands() {
            return myInsertCommands;
        }

        public List<String> getAlterCommands() {
            return myAlterCommands;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("DbCreateTask{");
            sb.append(System.lineSeparator());
            sb.append("type=").append(type);
            sb.append(System.lineSeparator());
            sb.append("Full Creation Script Commands=").append(!myCreationScriptCommands.isEmpty());
            sb.append(System.lineSeparator());
            sb.append("Table Commands=").append(!myTableCommands.isEmpty());
            sb.append(System.lineSeparator());
            sb.append("Insert Commands=").append(!myInsertCommands.isEmpty());
            sb.append(System.lineSeparator());
            sb.append("Alter Commands=").append(!myAlterCommands.isEmpty());
            sb.append(System.lineSeparator());
            sb.append("Excel Workbook Path=").append(myExcelInsertPath);
            sb.append(System.lineSeparator());
            sb.append("Excel Insert Table Order= ");
            if (myInsertTableOrder.isEmpty()) {
                sb.append("None provided");
            }
            for (String s : myInsertTableOrder) {
                sb.append("\t").append(s).append(System.lineSeparator());
            }
            sb.append(System.lineSeparator());
            sb.append('}');
            return sb.toString();
        }

        private DbCreateTask(DbCreateTaskBuilder builder) {
            type = Type.NONE;
            if (builder.pathToCreationScript != null) {
                // full creation script provided
                myCreationScriptCommands = fillCommandsFromScript(builder.pathToCreationScript);
                if (myCreationScriptCommands.isEmpty()) {
                    type = Type.NO_TABLES_ERROR;
                    return;
                }
                type = Type.FULL_SCRIPT;
            } else {
                if (builder.pathToTablesScript != null) {
                    // use script to create database structure
                    myTableCommands = fillCommandsFromScript(builder.pathToTablesScript);
                    if (myTableCommands.isEmpty()) {
                        type = Type.NO_TABLES_ERROR;
                        return;
                    }
                    // now check for insert
                    type = Type.TABLES;
                    if (builder.pathToInsertScript != null) {
                        // prefer insert via SQL script if it exists
                        myInsertCommands = fillCommandsFromScript(builder.pathToInsertScript);
                        type = Type.TABLES_INSERT;
                    } else {
                        // could be Excel insert
                        if (builder.pathToExcelWorkbook != null) {
                            myExcelInsertPath = builder.pathToExcelWorkbook;
                            myInsertTableOrder = new ArrayList<>(builder.tableNamesInInsertOrder);
                            type = Type.TABLES_EXCEL;
                        }
                    }
                    // now check for alter
                    if (builder.pathToAlterScript != null) {
                        myAlterCommands = fillCommandsFromScript(builder.pathToAlterScript);
                        if (type == Type.TABLES_INSERT) {
                            type = Type.TABLES_INSERT_ALTER;
                        } else if (type == Type.TABLES_EXCEL) {
                            type = Type.TABLES_EXCEL_ALTER;
                        } else if (type == Type.TABLES) {
                            type = Type.TABLES_ALTER;
                        }
                    }
                }
            }
        }

        /**
         * @return the type of the command sequence specified during the build process
         */
        public final Type getType() {
            return type;
        }

        /**
         * @param pathToScript the script to parse
         * @return the list of commands from the script
         */
        private final List<String> fillCommandsFromScript(Path pathToScript) {
            if (pathToScript == null) {
                throw new IllegalArgumentException("The creation script path must not be null");
            }
            if (Files.notExists(pathToScript)) {
                throw new IllegalArgumentException("The creation script file does not exist");
            }
            List<String> commands = new ArrayList<>();
            try {
                commands.addAll(DatabaseIfc.parseQueriesInSQLScript(pathToScript));
            } catch (IOException e) {
                DatabaseIfc.DbLogger.warn("The script {} t failed to parse.", pathToScript);
            }
            if (commands.isEmpty()) {
                DatabaseIfc.DbLogger.warn("The script {} produced no commands to execute.", pathToScript);
            }
            return commands;
        }
    }

    /**
     * A builder that can be used to configure a database creation task through as set of configuration
     * steps.
     */
    public static final class DbCreateTaskBuilder implements DbCreateTaskBuildStepIfc, WithCreateScriptStepIfc,
            WithTablesScriptStepIfc, DbCreateTaskFirstStepIfc, AfterTablesOnlyStepIfc,
            DbInsertStepIfc, DBAfterInsertStepIfc, DBAddConstraintsStepIfc {

        private Path pathToCreationScript;
        private Path pathToTablesScript;
        private Path pathToInsertScript;
        private Path pathToExcelWorkbook;
        private Path pathToAlterScript;
        private List<String> tableNamesInInsertOrder;

        @Override
        public DbCreateTaskBuildStepIfc withCreationScript(Path pathToScript) {
            if (pathToScript == null) {
                throw new IllegalArgumentException("The provided creation script path was null");
            }
            if (Files.notExists(pathToScript)) {
                throw new IllegalArgumentException("The creation script file does not exist");
            }
            pathToCreationScript = pathToScript;
            return this;
        }

        @Override
        public AfterTablesOnlyStepIfc withTables(Path pathToScript) {
            if (pathToScript == null) {
                throw new IllegalArgumentException("The provided table script path was null");
            }
            if (Files.notExists(pathToScript)) {
                throw new IllegalArgumentException("The create table script file does not exist");
            }
            pathToTablesScript = pathToScript;
            return this;
        }

        @Override
        public DBAfterInsertStepIfc withExcelData(Path toExcelWorkbook, List<String> tableNamesInInsertOrder) {
            if (toExcelWorkbook == null) {
                throw new IllegalArgumentException("The provided workbook script path was null");
            }
            if (Files.notExists(toExcelWorkbook)) {
                throw new IllegalArgumentException("The Excel workbook file does not exist");
            }
            if (tableNamesInInsertOrder == null) {
                throw new IllegalArgumentException("The provided list of table names was null");
            }
            pathToExcelWorkbook = toExcelWorkbook;
            this.tableNamesInInsertOrder = new ArrayList<>(tableNamesInInsertOrder);
            return this;
        }

        @Override
        public DBAfterInsertStepIfc withInserts(Path toInsertScript) {
            if (toInsertScript == null) {
                throw new IllegalArgumentException("The provided inset script path was null");
            }
            if (Files.notExists(toInsertScript)) {
                throw new IllegalArgumentException("The insert script file does not exist");
            }
            pathToInsertScript = toInsertScript;
            return this;
        }

        @Override
        public DbCreateTaskBuildStepIfc withConstraints(Path toAlterScript) {
            if (toAlterScript == null) {
                throw new IllegalArgumentException("The provided alter script path was null");
            }
            if (Files.notExists(toAlterScript)) {
                throw new IllegalArgumentException("The alter table script file does not exist");
            }
            pathToAlterScript = toAlterScript;
            return this;
        }

        @Override
        public DbCreateTask build() {
            return new DbCreateTask(this);
        }
    }


    /**
     * Used to limit the options on the first step
     */
    public interface DbCreateTaskFirstStepIfc extends WithCreateScriptStepIfc,
            WithTablesScriptStepIfc {

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
        DbCreateTaskBuildStepIfc withCreationScript(Path pathToCreationScript);
    }

    /**
     * Allows the user to specify a script that creates the tables of the database
     */
    public interface WithTablesScriptStepIfc {
        /**
         * @param pathToScript a path to a script that specifies the database tables, must not be null
         * @return A build step to permit connecting
         */
        AfterTablesOnlyStepIfc withTables(Path pathToScript);
    }

    public interface AfterTablesOnlyStepIfc extends DbCreateTaskBuildStepIfc, DbInsertStepIfc {

    }

    public interface DbCreateStepIfc extends DbCreateTaskBuildStepIfc {
        /**
         * @param toCreateScript the path to a script that will create the database, must not be null
         * @return a reference to the insert step in the build process
         */
        DbInsertStepIfc using(Path toCreateScript);

    }

    public interface DbInsertStepIfc extends DbCreateTaskBuildStepIfc {

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
        DBAfterInsertStepIfc withInserts(Path toInsertScript);

    }

    public interface DBAddConstraintsStepIfc extends DbCreateTaskBuildStepIfc {
        /**
         * @param toConstraintScript a path to an SQL script that can be read to alter the
         *                           table structure of the database and add constraints, must not be null
         * @return a reference to the alter step in the build process
         */
        DbCreateTaskBuildStepIfc withConstraints(Path toConstraintScript);
    }

    public interface DBAfterInsertStepIfc extends DBAddConstraintsStepIfc, DbCreateTaskBuildStepIfc {

    }

    public interface DbCreateTaskBuildStepIfc {
        /**
         * Finishes the build process of building the creation commands
         *
         * @return an instance of DbCreateCommandList
         */
        DbCreateTask build();
    }


}
