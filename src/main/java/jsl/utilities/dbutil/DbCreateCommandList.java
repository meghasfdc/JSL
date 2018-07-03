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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DbCreateCommandList {

    private Path myExcelInsertPath;
    private List<String> myInsertTableOrder;
    private List<String> myCreationScriptCommands;
    private List<String> myTableCommands;
    private List<String> myInsertCommands;
    private List<String> myAlterCommands;


    private DbCreateCommandList(DbCreateCommandListBuilder builder){
        if (builder.pathToCreationScript != null) {
            // full creation script provided
            myCreationScriptCommands = fillCommandsFromScript(builder.pathToCreationScript);
        } else {
            if (builder.pathToTablesOnlyScript != null) {
                // use script to create database structure
                myTableCommands = fillCommandsFromScript(builder.pathToTablesOnlyScript);
                // now check for insert
                if (builder.pathToInsertScript != null) {
                    // prefer insert via SQL script if it exists
                    myInsertCommands = fillCommandsFromScript(builder.pathToInsertScript);
                } else {
                    // could be Excel insert
                    if (builder.pathToExcelWorkbook != null) {
                        myExcelInsertPath = builder.pathToExcelWorkbook;
                        myInsertTableOrder = new ArrayList<>(builder.tableNamesInInsertOrder);
                    }
                }
                // now check for alter
                if (builder.pathToAlterScript != null) {
                    myAlterCommands = fillCommandsFromScript(builder.pathToAlterScript);
                }
            }
        }
    }

    private void setUp(){

    }

    public static FirstDbFillerStepIfc builder() {
        return new DbCreateCommandListBuilder();
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
            DatabaseIfc.DbLogger.warn("The create table script failed to parse.");
        }
        return commands;
    }

    public static final class DbCreateCommandListBuilder implements DbCommandListBuildStepIfc, WithCreateScriptStepIfc,
            WithTablesOnlyScriptStepIfc, FirstDbFillerStepIfc, AfterTablesOnlyStepIfc,
            DbInsertStepIfc, DBAfterInsertStepIfc, DBAddConstraintsStepIfc {

        private Path pathToCreationScript;
        private Path pathToTablesOnlyScript;
        private Path pathToInsertScript;
        private Path pathToExcelWorkbook;
        private Path pathToAlterScript;
        private List<String> tableNamesInInsertOrder;

        @Override
        public DbCommandListBuildStepIfc withCreationScript(Path pathToScript) {
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
        public DBAfterInsertStepIfc withExcelData(Path toExcelWorkbook, List<String> tableNamesInInsertOrder) {
            if (toExcelWorkbook == null) {
                throw new IllegalArgumentException("The provided workbook script path was null");
            }
            if (tableNamesInInsertOrder == null) {
                throw new IllegalArgumentException("The provided list of table names was null");
            }
            pathToExcelWorkbook = toExcelWorkbook;
            this.tableNamesInInsertOrder = new ArrayList<>(tableNamesInInsertOrder);
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
        public DbCommandListBuildStepIfc withConstraints(Path toAlterScript) {
            if (toAlterScript == null) {
                throw new IllegalArgumentException("The provided alter script path was null");
            }
            pathToAlterScript = toAlterScript;
            return this;
        }

        @Override
        public DbCreateCommandList build() {
            return new DbCreateCommandList(this);
        }
    }


    public interface FirstDbFillerStepIfc extends DbCommandListBuildStepIfc, WithCreateScriptStepIfc,
            WithTablesOnlyScriptStepIfc {

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
        DbCommandListBuildStepIfc withCreationScript(Path pathToCreationScript);
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

    public interface AfterTablesOnlyStepIfc extends DbCommandListBuildStepIfc, DbInsertStepIfc {

    }
    public interface DbCreateStepIfc extends DbCommandListBuildStepIfc {
        /**
         * @param toCreateScript the path to a script that will create the database, must not be null
         * @return a reference to the insert step in the build process
         */
        DbInsertStepIfc using(Path toCreateScript);

    }

    public interface DbInsertStepIfc extends DbCommandListBuildStepIfc {

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

    public interface DBAddConstraintsStepIfc extends DbCommandListBuildStepIfc {
        /**
         * @param toConstraintScript a path to an SQL script that can be read to alter the
         *                           table structure of the database and add constraints, must not be null
         * @return a reference to the alter step in the build process
         */
        DbCommandListBuildStepIfc withConstraints(Path toConstraintScript);
    }

    public interface DBAfterInsertStepIfc extends DBAddConstraintsStepIfc, DbCommandListBuildStepIfc {

    }

    public interface DbCommandListBuildStepIfc {
        /**
         * Finishes the build process of building the creation commands
         *
         * @return an instance of DbCreateCommandList
         */
        DbCreateCommandList build();
    }
}
