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

    /**
     *
     * @return an identifying string representing the database. This has no relation to
     * the name of the database on disk or in the dbms
     */
    String getName();

    /**
     *
     * @return the JDBC database meta data
     * @throws SQLException a checked exception
     */
    DatabaseMetaData getDatabaseMetaData() throws SQLException;

    /**
     *
     * @return the jooq SQL dialect for the database
     */
    SQLDialect getSQLDialect();

    /**
     *
     * @return a list of user defined table names within the database
     */
    List<String> getTableNames();

    /**
     *
     * @return a jooq Schema representing the schema that holds the user defined
     * tables that are in the database
     */
    Schema getUserSchema();

    /**
     *
     * @param tableName a string representation of the table name as recognized by valid SQL table name
     * @return a jooq Table holding the records for the named table
     */
    Table<? extends Record> getTable(String tableName);

    /**
     *
     * @param table a jooq table for a potential table in the database
     * @return true if the table is in this database
     */
    boolean containsTable(Table<?> table);

    /**
     *
     * @return a list of jooq Tables that in the user defined schema of the database
     */
    List<Table<?>> getTables();

    /**
     *
     * @return true if the database has user defined tables
     */
    boolean hasTables();

    /**
     *
     * @param table a string representation of the table name as recognized by valid SQL table name
     * @return true if the table is in the database
     */
    boolean tableExists(String table);

    /**
     * Writes the table as comma separated values
     *
     * @param tableName the name of the table to write
     * @param out the PrintWriter to write to
     */
    void writeTableAsCSV(String tableName, PrintWriter out);

    /**
     * Prints the table as comma separated values to the console
     *
     * @param tableName the name of the table to print
     */
    void printTableAsCSV(String tableName);

    /**
     * Writes the table as prettified text
     *
     * @param tableName the name of the table to write
     * @param out the PrintWriter to write to
     */
    void writeTableAsText(String tableName, PrintWriter out);

    /**
     * Prints the table as prettified text to the console
     *
     * @param tableName the name of the table to write
     */
    void printTableAsText(String tableName);

    /** Writes all tables as text
     *
     * @param out the PrintWriter to write to
     */
    void writeAllTablesAsText(PrintWriter out);

    /**
     * Prints all tables as text to the console
     */
    void printAllTablesAsText();

    /** Writes all tables as separate comma separated value files into the supplied
     * directory. The files are written to text files using the same name as
     * the tables in the database
     *
     * @param pathToOutPutDirectory the path to the output directory to hold the csv files
     * @throws IOException a checked exception
     */
    void writeAllTablesAsCSV(Path pathToOutPutDirectory) throws IOException;

    /**
     *
     * @param tableName
     * @return
     */
    Result<Record> selectAll(String tableName);

    /**
     *
     * @param table the name of the table
     * @return true if the table contains no records (rows)
     */
    boolean isTableEmpty(String table);

    /**
     *
     * @return true if at least one user defined table in the database has data
     */
    boolean hasData();

    /**
     *
     * @return true if all user defined tables are empty in the database
     */
    boolean areAllTablesEmpty();

    /**
     *
     * @return the jooq DSLContext for manipulating this database
     */
    DSLContext getDSLContext();

    /**
     *  Turns off jooq execution logging
     */
    void turnOffJooQDefaultExecutionLogging();

    /**
     *  Turns on jooq execution logging
     */
    void turnOnJooQDefaultExecutionLogging();

    /**
     *
     * @param tableName the name of the table
     * @return a string that represents all of the insert queries for the data that is currently in the
     * supplied table
     */
    String getInsertQueries(String tableName);

    /** Prints the insert queries associated with the supplied table to the console
     *
     * @param tableName the name of the table
     */
    void printInsertQueries(String tableName);

    /** Writes the insert queries associated with the supplied table to the PrintWriter
     *
     * @param tableName the name of the table
     * @out the PrintWriter to write to
     */
    void writeInsertQueries(String tableName, PrintWriter out);

    /**
     *  Prints all table data as insert queries to the console
     */
    void printAllTablesAsInsertQueries();

    /**
     *  Writes all table data as insert queries to the PrintWriter
     *
     * @param out the PrintWriter to write to
     */
    void writeAllTablesAsInsertQueries(PrintWriter out);

    /** Writes all that data in the user defined tables to an Excel workbook, with worksheets
     * representing each table
     *
     * @throws IOException a checked exception
     */
    void writeDbToExcelWorkbook() throws IOException;

    /** Writes all that data in the user defined tables to an Excel workbook, with worksheets
     * representing each table
     *
     * @param wbDirectory the directory that will contain the workbook
     * @throws IOException a checked exception
     */
    void writeDbToExcelWorkbook(Path wbDirectory) throws IOException;

    /** Writes all that data in the user defined tables to an Excel workbook, with worksheets
     * representing each table
     *
     * @param wbName the name of the workbook
     * @throws IOException a checked exception
     */
    void writeDbToExcelWorkbook(String wbName) throws IOException;

    /** Writes all that data in the user defined tables to an Excel workbook, with worksheets
     * representing each table
     *
     * @param wbName the name of the workbook
     * @param wbDirectory the directory of the workbook
     * @throws IOException a checked exception
     */
    void writeDbToExcelWorkbook(String wbName, Path wbDirectory) throws IOException;

    /**
     *
     * @param cmd The SQL command to execute as a string, without the semi-colon
     * @return true if the command executed
     * @throws SQLException a checked exception
     */
    boolean executeCommand(String cmd) throws SQLException;

    /**
     *
     * @param cmds a list of SQL commands as strings without the semi-colon
     * @return true if all commands executed
     */
    boolean executeCommands(List<String> cmds);

    /**
     *
     * @param path the path to a text file containing valid SQL statements to execute
     * @return true if the script executed
     * @throws IOException a checked exception
     */
    boolean executeScript(Path path) throws IOException;
}
