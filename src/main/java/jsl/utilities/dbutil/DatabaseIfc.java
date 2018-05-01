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

package jsl.utilities.dbutil;

import org.jooq.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

public interface DatabaseIfc {
    String getName();

    String getURL();

    DatabaseMetaData getDatabaseMetaData() throws SQLException;

    String getDBSchemaName();

    SQLDialect getSQLDialect();

    Path getCreationScriptPath();

    void setCreationScriptPath(Path path);

    Path getTablesOnlyScriptPath();

    void setTablesOnlyScriptPath(Path path);

    Path getInsertionScriptPath();

    void setInsertionScriptPath(Path path);

    void setAlterScriptPath(Path path);

    void setExcelInsertPath(Path path);

    Path getExcelInsertPath();

    Path getAlterScriptPath();

    List<String> getTruncateTableOrder();

    List<String> getInsertTableOrder();

    List<String> getCreateCommands();

    List<String> getInsertCommands();

    List<String> getAlterCommands();

    void setTruncateTableOrder(List<String> tableNames);

    void setInsertTableOrder(List<String> tableNames);

    boolean executeCreateTablesOnlyScript(Path pathToScript) throws IOException;

    boolean executeCreationScript(Path pathToScript) throws IOException;

    boolean executeInsertionScript(Path pathToScript) throws IOException;

    boolean executeAlterScript(Path pathToScript) throws IOException;

    List<String> getTableNames();

    Schema getUserSchema();

    Table<? extends Record> getTable(String tableName);

    boolean containsTable(Table<?> table);

    List<Table<?>> getTables();

    boolean hasTables();

    boolean tableExists(String table);

    void writeTableAsCSV(String tableName, PrintWriter out);

    void printTableAsCSV(String tableName);

    void writeTableAsText(String tableName, PrintWriter out);

    void printTableAsText(String tableName);

    void writeAllTablesAsText(PrintWriter out);

    void printAllTablesAsText();

    Result<Record> selectAll(String tableName);

    boolean isTableEmpty(String table);

    boolean hasData();

    boolean areAllTablesEmpty();

    void writeAllTablesAsCSV(Path pathToOutPutDirectory) throws IOException;

    Parser getParser();

    DSLContext getDSLContext();

    void turnOffJooQDefaultExecutionLogging();

    void turnOnJooQDefaultExecutionLogging();

    void printJOOQDDLQueries();

    String getInsertQueries(String tableName);

    void printInsertQueries(String tableName);

    void writeInsertQueries(String tableName, PrintWriter out);

    void printAllTablesAsInsertQueries();

    void writeAllTablesAsInsertQueries(PrintWriter out);

    void printAllTablesAsInsertQueriesUsingInsertOrder();

    void writeAllTablesAsInsertQueriesUsingInsertOrder(PrintWriter out);

    void writeDbToExcelWorkbook() throws IOException;

    void writeDbToExcelWorkbook(Path wbDirectory) throws IOException;

    void writeDbToExcelWorkbook(String wbName) throws IOException;

    void writeDbToExcelWorkbook(String wbName, Path wbDirectory) throws IOException;

    boolean executeCommand(String cmd) throws SQLException;

    boolean executeCommands(List<String> cmds);

    boolean executeScript(Path path) throws IOException;

    void copyDb(String dupName, Path directory) throws SQLException, IOException;
}
