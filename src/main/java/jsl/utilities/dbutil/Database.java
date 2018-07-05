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
     * Use to builder a DbCreateTask that can then be executed on the database.
     *
     * @return a builder that can be used to builder a properly configured DbCreateTask
     */
    public static DbCreateTask.DbCreateTaskFirstStepIfc createTaskBuilder() {
        return new DbCreateTask.DbCreateTaskBuilder();
    }

    /**
     * Attempts to execute a configured set of tasks that will create, possibly fill, and
     * alter the database.
     *
     * @param task a configured database creation task
     * @return true if the task was executed correctly, false otherwise
     */
    public final boolean executeCreateTask(DbCreateTask task) {
        switch (task.getState()) {
            case UN_EXECUTED:
                // execute the task
                return dbCreateTaskExecution(task);
            case EXECUTED:
                DbLogger.error("Tried to execute an already executed create task.\n {}", task);
                return false;
            case EXECUTION_ERROR:
                DbLogger.error("Tried to execute a previously executed task that had errors.\n {}", task);
                return false;
            case NO_TABLES_ERROR:
                DbLogger.error("Tried to execute a create task with no tables created.\n {}", task);
                return false;
        }
        return false;
    }

    private boolean dbCreateTaskExecution(DbCreateTask task) {
        boolean execFlag = false; // assume it does not execute
        switch (task.getType()) {
            case NONE:
                DbLogger.warn("Attempted to execute a create task with no commands.\n {}", task);
                execFlag = true;
                task.setState(DbCreateTask.State.EXECUTED);
                break;
            case FULL_SCRIPT:
                DbLogger.info("Attempting to execute full script create task...\n {}", task);
                execFlag = executeCommands(task.getCreationScriptCommands());
                break;
            case TABLES:
                DbLogger.info("Attempting to execute tables only create task. \n{}", task);
                execFlag = executeCommands(task.getTableCommands());
                break;
            case TABLES_INSERT:
                DbLogger.info("Attempting to execute tables plus insert create task.\n{}", task);
                execFlag = executeCommands(task.getTableCommands());
                if (execFlag){
                    execFlag = executeCommands(task.getInsertCommands());
                }
                break;
            case TABLES_ALTER:
                DbLogger.info("Attempting to execute tables plus alter create task.\n{}", task);
                execFlag = executeCommands(task.getTableCommands());
                if (execFlag){
                    execFlag = executeCommands(task.getAlterCommands());
                }
                break;
            case TABLES_INSERT_ALTER:
                DbLogger.info("Attempting to execute create/insert/alter tables create task.\n {}", task);
                execFlag = executeCommands(task.getTableCommands());
                if (execFlag){
                    execFlag = executeCommands(task.getInsertCommands());
                }
                if (execFlag){
                    execFlag = executeCommands(task.getAlterCommands());
                }
                break;
            case TABLES_EXCEL:
                DbLogger.info("Attempting to execute tables create plus Excel import task.\n {}", task);
                execFlag = executeCommands(task.getTableCommands());
                if (execFlag){
                    try {
                        ExcelUtil.writeWorkbookToDatabase(task.getExcelWorkbookPathForDataInsert(),
                                true, this,
                                task.getInsertTableOrder());
                    } catch (IOException e) {
                        execFlag = false;
                    }
                }
                break;
            case TABLES_EXCEL_ALTER:
                DbLogger.info("Attempting to execute tables create plus Excel plus alter import task.\n {}", task);
                execFlag = executeCommands(task.getTableCommands());
                if (execFlag){
                    try {
                        ExcelUtil.writeWorkbookToDatabase(task.getExcelWorkbookPathForDataInsert(),
                                true, this,
                                task.getInsertTableOrder());
                        execFlag = executeCommands(task.getAlterCommands());
                    } catch (IOException e) {
                        execFlag = false;
                    }
                }
                break;
        }
        if (execFlag) {
            task.setState(DbCreateTask.State.EXECUTED);
            DbLogger.info("The task was successfully executed.");
        } else {
            task.setState(DbCreateTask.State.EXECUTION_ERROR);
            DbLogger.info("The task had execution errors.");
            //TODO decide whether to throw this error or not
            throw new DataAccessException("There was an execution error for task \n" + task +
                    "\n see jslDbLog.log for details ");
        }
        return execFlag; // note can only get here if execFlag is true because of the execution exception
    }
}
