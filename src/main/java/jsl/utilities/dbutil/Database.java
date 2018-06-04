package jsl.utilities.dbutil;

import jsl.utilities.excel.ExcelUtil;
import org.jooq.*;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Many databases define the terms database, user, schema in a variety of ways. This abstraction
 * defines this concept as the userSchema.  It is the name of the organizational construct for
 * which the user defined database object are contained. These are not the system abstractions.
 * The name provided to the construct is simply for labeling and may or may not have any relationship
 * to the actual file name or database name of the database. The supplied connection has all
 * the information that it needs to access the database.
 */
public class Database implements DatabaseIfc {
    public enum LineOption {
        COMMENT, CONTINUED, END
    }

    public static final String DEFAULT_DELIMITER = ";";
    public static final Pattern NEW_DELIMITER_PATTERN = Pattern.compile("(?:--|\\/\\/|\\#)?!DELIMITER=(.+)");
    public static final Pattern COMMENT_PATTERN = Pattern.compile("^(?:--|\\/\\/|\\#).+");

    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final String myName;
    private final Schema myUserSchema;
    private final Connection myConnection;
    private final SQLDialect mySQLDialect;
    private Settings myExecuteLoggingSettings;

    /**
     * @param dbName         a string representing the name of the database must not be null. This name
     *                       may or may not have any relation to the actual name of the database. This is
     *                       used for labeling purposes.
     * @param userSchemaName a string representing the name of the user or schema that holds
     *                       the user defined tables with the database. Must not be null
     * @param connection     an active connection to the database, must not be null
     * @param dialect        the SLQ dialect for this type of database, must not null
     */
    public Database(String dbName, String userSchemaName, Connection connection, SQLDialect dialect) {
        Objects.requireNonNull(dbName, "The database name was null");
        Objects.requireNonNull(userSchemaName, "The database user/schema was null");
        Objects.requireNonNull(connection, "The database connection was null");
        Objects.requireNonNull(dialect, "The database dialect was null");
        myName = dbName;
        myConnection = connection;
        mySQLDialect = dialect;
        myUserSchema = getSchema(userSchemaName);
        if (myUserSchema == null) {
            logger.error("The supplied userSchema name {} was not in the database.", userSchemaName);
            throw new DataAccessException("The supplied userSchema name was not in the database: " + userSchemaName);
        }
    }

    /**
     * @return a label or name for the database
     */
    public final String getName() {
        return myName;
    }

    /**
     * @return the meta data about the database if available, or null
     */
    public final DatabaseMetaData getDatabaseMetaData() {
        DatabaseMetaData metaData = null;
        try {
            metaData = myConnection.getMetaData();
        } catch (SQLException e) {
            logger.warn("The meta data was not available {}", e);
        }
        return metaData;
    }

    /**
     * @return the sql dialect for the database.  Here should be derby
     */
    public final SQLDialect getSQLDialect() {
        return mySQLDialect;
    }

    /**
     * Returns the names of the tables in the current database
     *
     * @return a list of the names of the tables as strings
     */
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
     * @return the jooq schema for the name or null
     */
    public Schema getSchema(String schemaName) {
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
     * The schema that holds the user defined tables within the database.
     *
     * @return the user defined schema (as opposed to the system defined schema)
     */
    public Schema getUserSchema() {
        return myUserSchema;
    }

    /**
     * @param tableName the name to get the Table for
     * @return the jooq Table associated with the supplied name
     */
    public Table<? extends Record> getTable(String tableName) {
        return getUserSchema().getTable(tableName);
    }

    /**
     * @param table the table to check
     * @return true if the databased contains the supplied table
     */
    public boolean containsTable(Table<?> table) {
        return getUserSchema().getTables().contains(table);
    }

    /**
     * @return a list of jooq Tables in the user schema
     */
    public List<Table<?>> getTables() {
        return getUserSchema().getTables();
    }

    /**
     * Checks if tables exist in the database
     *
     * @return true if it exists
     */
    public boolean hasTables() {
        return (!getTables().isEmpty());
    }

    /**
     * Checks if the supplied table exists in the database
     *
     * @param table a string representing the name of the table
     * @return true if it exists
     */
    public boolean tableExists(String table) {
        return (getTable(table) != null);
    }

    /**
     * @param tableName the name of the table to write
     * @param out       the output file for the writing
     */
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
    public void printTableAsCSV(String tableName) {
        writeTableAsCSV(tableName, new PrintWriter(System.out));
    }

    /**
     * Writes the table in pretty text to the file
     *
     * @param tableName the name of the table to write
     * @param out       the output file to write to
     */
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
    public void printTableAsText(String tableName) {
        writeTableAsText(tableName, new PrintWriter(System.out));
    }

    /**
     * Writes all user defined tables to the output
     *
     * @param out the place to write to
     */
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
    public boolean hasData() {
        return areAllTablesEmpty() != true;
    }

    /**
     * @return true if all tables are empty
     */
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
     * @return the jooq DSLContext for the database
     */
    public DSLContext getDSLContext() {
        if (myExecuteLoggingSettings == null) {
            return DSL.using(myConnection, getSQLDialect());
        } else {
            return DSL.using(myConnection, getSQLDialect(), myExecuteLoggingSettings);
        }
    }

    /**
     * Turns on JooQ Default execute SQL logging
     */
    public final void turnOffJooQDefaultExecutionLogging() {
        myExecuteLoggingSettings = new Settings().withExecuteLogging(false);
    }

    /**
     * Turns off JooQ Default execute SQL logging
     */
    public final void turnOnJooQDefaultExecutionLogging() {
        myExecuteLoggingSettings = null;
    }

    /**
     * @param tableName the name of the table to generate insert statements for
     * @return the insert statements as a string
     */
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
     * Displays the insert statements for the table on the console
     *
     * @param tableName the name of the table for the insert statements
     */
    public void printInsertQueries(String tableName) {
        writeInsertQueries(tableName, new PrintWriter(System.out));
    }

    /**
     * Writes the insert statements for the table in the file
     *
     * @param tableName the name of the table for the insert statements
     * @param out       the file to write to
     */
    public void writeInsertQueries(String tableName, PrintWriter out) {
        if (!tableExists(tableName)) {
            logger.trace("Table: {} does not exist in database schema {}", tableName, getUserSchema());
            return;
        }
        writeInsertQueries(getTable(tableName), out);
    }

    /**
     * Writes the insert statements for the table in the file
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
    public void printAllTablesAsInsertQueries() {
        writeAllTablesAsInsertQueries(new PrintWriter(System.out));
    }

    /**
     * Writes all the insert queries for the entire database
     *
     * @param out the place to write the queries
     */
    public void writeAllTablesAsInsertQueries(PrintWriter out) {
        List<Table<?>> tables = getTables();
        for (Table t : tables) {
            writeInsertQueries(t, out);
        }
    }

    /**
     * Writes all the tables to an Excel workbook, uses name of database, uses the working directory
     */
    public void writeDbToExcelWorkbook() throws IOException {
        writeDbToExcelWorkbook(null, null);
    }

    /**
     * Writes all the tables to an Excel workbook, uses name of database
     *
     * @param wbDirectory directory of the workbook, if null uses the working directory
     */
    public void writeDbToExcelWorkbook(Path wbDirectory) throws IOException {
        writeDbToExcelWorkbook(null, wbDirectory);
    }

    /**
     * Writes all the tables to an Excel workbook uses the working directory
     *
     * @param wbName name of the workbook, if null uses name of database
     */
    public void writeDbToExcelWorkbook(String wbName) throws IOException {
        writeDbToExcelWorkbook(wbName, null);
    }

    /**
     * Writes all the tables to an Excel workbook
     *
     * @param wbName      name of the workbook, if null uses name of database
     * @param wbDirectory directory of the workbook, if null uses the working directory
     */
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
     * @return true if all commands are executed
     */
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

}
