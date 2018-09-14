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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.excel;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import jsl.utilities.dbutil.DatabaseIfc;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author rossetti
 */
public class ExcelUtil {

    final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public final static int DEFAULT_MAX_CHAR_IN_CELL = 512;

    final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    /**
     * Runs writeDBAsExcelWorkbook() to write the supplied database to an Excel workbook with one sheet for
     * every table, squelching all exceptions. The workbook will have the same
     * name as the database
     *
     * @param db         the database to read data from
     * @param tableNames the list of names of tables in the database to write to Excel, must not be null
     */
    public static void runWriteDBAsExcelWorkbook(DatabaseIfc db, List<String> tableNames) {
        Objects.requireNonNull(db, "The supplied DatabaseIfc reference was null");
        Objects.requireNonNull(tableNames, "The supplied list of table names was null");
        runWriteDBAsExcelWorkbook(db, tableNames, Paths.get(db.getLabel()));
    }

    /**
     * Runs writeDBAsExcelWorkbook() to write the supplied database to an Excel workbook with one sheet for
     * every table, squelching all exceptions
     *
     * @param db             the database to read data from
     * @param tableNames     the list of names of tables in the database to write to Excel, must not be null
     * @param pathToWorkbook the name of the workbook that is to be made
     */
    public static void runWriteDBAsExcelWorkbook(DatabaseIfc db, List<String> tableNames, Path pathToWorkbook) {
        try {
            writeDBAsExcelWorkbook(db, tableNames, pathToWorkbook);
        } catch (FileNotFoundException ex) {
            LOG.error("FileNotFoundException {} ", pathToWorkbook, ex);
            ex.printStackTrace();
        } catch (IOException ex) {
            LOG.error("Error in {} runWriteDBAsExcelWorkbook()", pathToWorkbook, ex);
            ex.printStackTrace();
        }

    }

    /**
     * Runs writeWorkbookToDatabase() squelching all exceptions. Read the workbook and writes it
     * into the database.  The first row of each sheet is skipped.
     *
     * @param pathToWorkbook the path to the workbook. Must be valid workbook with .xlsx extension
     * @param db             the database to write to
     * @param tableNames     the names of the sheets and tables in the order that needs to be written
     */
    public static void runWriteWorkbookToDatabase(Path pathToWorkbook, DatabaseIfc db,
                                                  List<String> tableNames) {
        Objects.requireNonNull(db, "The supplied DatabaseIfc reference was null");
        Objects.requireNonNull(tableNames, "The supplied list of table names was null");
        runWriteWorkbookToDatabase(pathToWorkbook, true, db, tableNames);
    }

    /**
     * Runs writeWorkbookToDatabase() squelching all exceptions. Read the workbook and writes it
     * into the database.  The first row of each sheet is skipped.
     *
     * @param pathToWorkbook the path to the workbook. Must be valid workbook with .xlsx extension
     * @param skipFirstRow   if true the first row of each sheet is skipped
     * @param db             the database to write to
     * @param tableNames     the names of the sheets and tables in the order that needs to be written
     */
    public static void runWriteWorkbookToDatabase(Path pathToWorkbook, boolean skipFirstRow, DatabaseIfc db,
                                                  List<String> tableNames) {
        try {
            writeWorkbookToDatabase(pathToWorkbook, skipFirstRow, db, tableNames);
        } catch (IOException e) {
            LOG.error("IOException {} ", pathToWorkbook, e);
            e.printStackTrace();
        }
    }

    /**
     * Writes the supplied database to an Excel workbook with one sheet for
     * every table. This will produce an Excel file with the same name as the
     * database in the current working directory.
     *
     * @param db         the database to read data from
     * @param tableNames the list of names of tables in the database to write to Excel, must not be null
     * @throws IOException io exception
     */
    public static void writeDBAsExcelWorkbook(DatabaseIfc db, List<String> tableNames)
            throws IOException {
        writeDBAsExcelWorkbook(db, tableNames, Paths.get(db.getLabel()));
    }

    /**
     * Writes the supplied database to an Excel workbook with one sheet for
     * every table. This will produce an Excel file with the supplied name in
     * the current working directory. Each sheet of the workbook will have
     * the field names as the first row in the sheet.
     *
     * @param db             the database to read data from, must not be null
     * @param tableNames     the list of names of tables in the database to write to Excel, must not be null
     * @param pathToWorkbook the name of the workbook that was made
     * @throws IOException io exception
     */
    public static void writeDBAsExcelWorkbook(DatabaseIfc db, List<String> tableNames, Path pathToWorkbook)
            throws IOException {
        Objects.requireNonNull(db, "The supplied DatabaseIfc reference was null");
        Objects.requireNonNull(tableNames, "The supplied list of table names was null");
        if (tableNames.isEmpty()) {
            LOG.warn("The supplied list of table names was empty");
            return;
        }
        List<String> tables = new ArrayList<>();
        for (String tableName : tableNames) {
            if (db.containsTable(tableName)) {
                tables.add(tableName);
            } else {
                LOG.warn("The supplied table name {} to write to Excel is not in database {}", tableName, db.getLabel());
            }
        }
        if (tables.isEmpty()) {
            LOG.warn("The supplied list of table names had no corresponding tables in database {}", db.getLabel());
            return;
        }
        //  if null make the name of the workbook the same as the database name
        if (pathToWorkbook == null) {
            Path currentDir = Paths.get(".");
            pathToWorkbook = currentDir.resolve(db.getLabel() + ".xlsx");
        }
        LOG.info("Writing database {} to Excel workbook {}.", db.getLabel(), pathToWorkbook);
       // XSSFWorkbook workbook = new XSSFWorkbook();
        // using SXSSFWorkbook to speed up processing
        // https://poi.apache.org/components/spreadsheet/how-to.html#sxssf
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        for (String tableName : tables) {
            Sheet sheet = workbook.createSheet(tableName);
            // stopped auto sizing to speed up processing
            //sheet.trackAllColumnsForAutoSizing();
            LOG.info("Writing table {} to Excel sheet.", tableName);
            writeTableAsExcelSheet(db, tableName, sheet);
        }

        FileOutputStream out = new FileOutputStream(pathToWorkbook.toFile());
        workbook.write(out);
        workbook.close();
        out.close();
        workbook.dispose();
    }

    /**
     * Writes the sheets of the workbook into database tables. The list of names is the names of the
     * sheets in the workbook and the names of the tables that need to be written. They are in the
     * order that is required for entering data so that no integrity constraints are violated.
     * <p>
     * The first row of every sheet is skipped.
     *
     * @param pathToWorkbook the path to the workbook. Must be valid workbook with .xlsx extension
     * @param db             the database to write to
     * @param tableNames     the names of the sheets and tables in the order that needs to be written
     * @throws IOException an io exception
     */
    public static void writeWorkbookToDatabase(Path pathToWorkbook, DatabaseIfc db,
                                               List<String> tableNames) throws IOException {
        writeWorkbookToDatabase(pathToWorkbook, true, db, tableNames);
    }

    /**
     * Writes the sheets of the workbook into database tables. The list of names is the names of the
     * sheets in the workbook and the names of the tables that need to be written. They are in the
     * order that is required for entering data so that no integrity constraints are violated.
     *
     * @param pathToWorkbook the path to the workbook. Must be valid workbook with .xlsx extension
     * @param skipFirstRow   if true the first row of each sheet is skipped
     * @param db             the database to write to
     * @param tableNames     the names of the sheets and tables in the order that needs to be written
     * @throws IOException an io exception
     */
    public static void writeWorkbookToDatabase(Path pathToWorkbook, boolean skipFirstRow, DatabaseIfc db,
                                               List<String> tableNames) throws IOException {
        if (pathToWorkbook == null) {
            throw new IllegalArgumentException("The path to the workbook was null");
        }
        File file = pathToWorkbook.toFile();
        OPCPackage pkg = null;
        try {
            pkg = OPCPackage.open(file);
        } catch (InvalidFormatException e) {
            LOG.error("The workbook has an invalid format");
            throw new IOException("The workbook has an invalid format. See Apache POI InvalidFormatException");
        }
        //TODO consider using SXSSFWorkbook
        XSSFWorkbook wb = new XSSFWorkbook(pkg);
        LOG.info("Writing workbook {} to database {}", pathToWorkbook, db.getLabel());
        writeWorkbookToDatabase(wb, skipFirstRow, db, tableNames);
        //wb.close();
        pkg.close();
        LOG.info("Completed writing workbook {} to database {}", pathToWorkbook, db.getLabel());
    }

    /**
     * Writes the sheets of the workbook into database tables. The list of names is the names of the
     * sheets in the workbook and the names of the tables that need to be written. They are in the
     * order that is required for entering data so that no integrity constraints are violated.
     * <p>
     * The first row of every sheet is skipped.
     *
     * @param wb         the workbook to copy from
     * @param db         the database to write to
     * @param tableNames the names of the sheets and tables in the order that needs to be written
     * @throws IOException an io exception
     */
    public static void writeWorkbookToDatabase(Workbook wb, DatabaseIfc db,
                                               List<String> tableNames) throws IOException {
        writeWorkbookToDatabase(wb, true, db, tableNames);
    }

    /**
     * Writes the sheets of the workbook into database tables. The list of names is the names of the
     * sheets in the workbook and the names of the tables that need to be written. They are in the
     * order that is required for entering data so that no integrity constraints are violated.
     *
     * @param wb           the workbook to copy from
     * @param skipFirstRow if true the first row of each sheet is skipped
     * @param db           the database to write to
     * @param tableNames   the names of the sheets and tables in the order that needs to be written
     * @throws IOException an io exception
     */
    public static void writeWorkbookToDatabase(Workbook wb, boolean skipFirstRow, DatabaseIfc db,
                                               List<String> tableNames) throws IOException {
        if (wb == null) {
            throw new IllegalArgumentException("The workbook was null");
        }
        if (db == null) {
            throw new IllegalArgumentException("The database was null");
        }
        if (tableNames == null) {
            throw new IllegalArgumentException("The list of table names was null");
        }

        for (String tableName : tableNames) {
            Sheet sheet = wb.getSheet(tableName);
            if (sheet == null) {
                LOG.info("Skipping table {} no corresponding sheet in workbook", tableName);
                continue;
            }
            writeSheetToTable(sheet, skipFirstRow, tableName, db);
        }
    }

    /**
     * Writes the sheet to the named table.  Automatically skips the first row of the sheet. Uses
     * the name of the sheet as the name of the table. The table must exist in the database with that name.
     *
     * @param sheet the sheet to get the data from
     * @param db    the database containing the table
     * @throws IOException an io exception
     */
    public static void writeSheetToTable(Sheet sheet, DatabaseIfc db) throws IOException {
        writeSheetToTable(sheet, true, null, db);
    }

    /**
     * Wrties the sheet to the named table.  Automatically skips the first row of the sheet
     *
     * @param sheet     the sheet to get the data from
     * @param tableName the name of the table to write to
     * @param db        the database containing the table
     * @throws IOException an io exception
     */
    public static void writeSheetToTable(Sheet sheet, String tableName, DatabaseIfc db) throws IOException {
        writeSheetToTable(sheet, true, tableName, db);
    }

    /**
     * This method assumes that the tableName exists in the database or that a table with the same
     * name as the sheet exists within the database and that the sheet has the appropriate structure
     * to be placed within the table in the database. If the table does not exist in the database
     * the method returns and logs a warning.
     *
     * @param sheet        the sheet to get the data from, must null be null
     * @param skipFirstRow true means skip the first row of the Excel sheet
     * @param tableName    the name of the table to write to, can be null, if so the sheet name is used
     * @param db           the database containing the table, must not be null
     * @throws IOException an io exception
     */
    public static void writeSheetToTable(Sheet sheet, boolean skipFirstRow, String tableName, DatabaseIfc db) throws IOException {
        if (sheet == null) {
            throw new IllegalArgumentException("The Sheet was null");
        }
        if (db == null) {
            throw new IllegalArgumentException("The database was null");
        }
        if (tableName == null) {
            tableName = sheet.getSheetName();
        }
        if (!db.containsTable(tableName)) {
            LOG.warn("Attempting to write sheet {} to database {}, the table {} does not exist",
                    sheet.getSheetName(), db.getLabel(), tableName);
            return;
        }
        final Table<? extends Record> table = db.getTable(tableName);
        final Field<?>[] fields = table.fields();
        LOG.info("Reading sheet {} for table {} in database {}", sheet.getSheetName(), tableName, db.getLabel());
        //TODO could this be a performance bottleneck? all in memory??
        final List<Object[]> lists = readSheetAsListOfObjects(sheet, fields, skipFirstRow);
        db.getDSLContext().loadInto(table).batchAll().loadArrays(lists.iterator()).fields(fields).execute();
        LOG.info("Wrote sheet {} for table {} into database {}", sheet.getSheetName(), tableName, db.getLabel());
    }

    /**
     * Assumes that the first row is a header for a CSV like file and
     * returns the number of columns (1 for each header)
     *
     * @param sheet the sheet to write, must not be null
     * @return the number of header columns
     */
    public static int getNumberColumnsForCSVHeader(Sheet sheet) {
        Objects.requireNonNull(sheet, "The supplied sheet was null");
        Row row = sheet.getRow(0);
        if (row != null) {
            return row.getLastCellNum();
        } else {
            return 0;
        }
    }

    /**
     * Treats the columns as fields in a csv file, writes each row as a separate csv row
     * in the resulting csv file. Uses default maximum cell size. Does not skip the first row.
     * Writes to a CSV file with the same name as the sheet in the current working directory.
     * The number of columns is determined by assuming that the first row contains
     * the CSV header. If the sheet has no columns, then an exception is thrown.
     *
     * @param sheet the sheet to write, must not be null
     * @throws IOException an IO exception
     */
    public static void writeSheetToCSV(Sheet sheet) throws IOException {
        int numCols = getNumberColumnsForCSVHeader(sheet);
        if (numCols <= 0) {
            throw new IllegalStateException("There were no columns in the sheet to write out.");
        }
        writeSheetToCSV(sheet, numCols);
    }

    /**
     * Treats the columns as fields in a csv file, writes each row as a separate csv row
     * in the resulting csv file. Uses default maximum cell size. Does not skip the first row.
     * Writes to a CSV file with the same name as the sheet in the current working directory.
     * The number of columns is determined by assuming that the first row contains
     * the CSV header. If the sheet has no columns, then an exception is thrown.
     *
     * @param sheet     the sheet to write, must not be null
     * @param pathToCSVFile a Path to the file to write as csv, must not be null
     * @throws IOException an IO exception
     */
    public static void writeSheetToCSV(Sheet sheet, Path pathToCSVFile) throws IOException {
        int numCols = getNumberColumnsForCSVHeader(sheet);
        if (numCols <= 0) {
            throw new IllegalStateException("There were no columns in the sheet to write out.");
        }
        writeSheetToCSV(sheet, pathToCSVFile, numCols);
    }

    /**
     * Treats the columns as fields in a csv file, writes each row as a separate csv row
     * in the resulting csv file. Uses default maximum cell size. Does not skip the first row.
     * Writes to a CSV file with the same name as the sheet in the current working directory.
     *
     * @param sheet  the sheet to write, must not be null
     * @param numCol the number of columns to write from each row, must be at least 1
     * @throws IOException an IO exception
     */
    public static void writeSheetToCSV(Sheet sheet, int numCol) throws IOException {
        Objects.requireNonNull(sheet, "The supplied sheet was null");
        String sheetName = sheet.getSheetName();
        Path path = Paths.get(".").resolve(sheetName + ".csv");
        writeSheetToCSV(sheet, false, path, numCol, DEFAULT_MAX_CHAR_IN_CELL);
    }

    /**
     * Treats the columns as fields in a csv file, writes each row as a separate csv row
     * in the resulting csv file. Uses default maximum cell size. Does not skip the first row
     *
     * @param sheet     the sheet to write, must not be null
     * @param pathToCSV a Path to the file to write as csv, must not be null
     * @param numCol    the number of columns to write from each row, must be at least 1
     * @throws IOException an IO exception
     */
    public static void writeSheetToCSV(Sheet sheet, Path pathToCSV, int numCol) throws IOException {
        writeSheetToCSV(sheet, false, pathToCSV, numCol, DEFAULT_MAX_CHAR_IN_CELL);
    }

    /**
     * Treats the columns as fields in a csv file, writes each row as a separate csv row
     * in the resulting csv file. Uses default maximum cell size.
     *
     * @param sheet        the sheet to write, must not be null
     * @param skipFirstRow if true, the first row is skipped in the sheet
     * @param pathToCSV    a Path to the file to write as csv, must not be null
     * @param numCol       the number of columns to write from each row, must be at least 1
     * @throws IOException an IO exception
     */
    public static void writeSheetToCSV(Sheet sheet, boolean skipFirstRow, Path pathToCSV,
                                       int numCol) throws IOException {
        writeSheetToCSV(sheet, skipFirstRow, pathToCSV, numCol, DEFAULT_MAX_CHAR_IN_CELL);
    }

    /**
     * Treats the columns as fields in a csv file, writes each row as a separate csv row
     * in the resulting csv file
     *
     * @param sheet        the sheet to write, must not be null
     * @param skipFirstRow if true, the first row is skipped in the sheet
     * @param pathToCSV    a Path to the file to write as csv, must not be null
     * @param numCol       the number of columns to write from each row, must be at least 1
     * @param maxChar      the maximum number of characters that can be in any cell, must be at least 1
     * @throws IOException an IO exception
     */
    public static void writeSheetToCSV(Sheet sheet, boolean skipFirstRow, Path pathToCSV, int numCol,
                                       int maxChar) throws IOException {
        Objects.requireNonNull(sheet, "The supplied sheet was null");
        Objects.requireNonNull(pathToCSV, "The supplied path was null");
        if (numCol <= 0) {
            throw new IllegalArgumentException("The number of columns must be >= 1");
        }
        if (maxChar <= 0) {
            throw new IllegalArgumentException("The maximum number of characters must be >= 1");
        }
        Iterator<Row> rowIterator = sheet.rowIterator();
        if (skipFirstRow) {
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
        }
        FileWriter fileWriter = new FileWriter(pathToCSV.toFile());
        ICSVWriter writer = new CSVWriterBuilder(fileWriter).build();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String[] strings = readRowAsStringArray(row, numCol, maxChar);
            writer.writeNext(strings);
        }
        writer.close();
    }

    /**
     * @param sheet  the sheet to process
     * @param fields the fields associated with each row
     * @return a list of lists of the java objects representing each cell of each row of the sheet
     */
    public static List<List<Object>> readSheetAsObjects(Sheet sheet, Field<?>[] fields, boolean skipFirstRow) {
        if (sheet == null) {
            throw new IllegalArgumentException("The Sheet was null");
        }
        Iterator<Row> rowIterator = sheet.rowIterator();
        if (skipFirstRow) {
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
        }
        List<List<Object>> list = new ArrayList<>();
        while (rowIterator.hasNext()) {
            list.add(readRowAsObjectList(rowIterator.next(), fields));
        }
        return list;
    }

    /**
     * @param sheet  the sheet to process
     * @param fields the fields associated with each row
     * @return a list of the arrays of the java objects representing each cell of each row of the sheet
     */
    public static List<Object[]> readSheetAsListOfObjects(Sheet sheet, Field<?>[] fields, boolean skipFirstRow) {
        if (sheet == null) {
            throw new IllegalArgumentException("The Sheet was null");
        }
        Iterator<Row> rowIterator = sheet.rowIterator();
        if (skipFirstRow) {
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
        }
        List<Object[]> list = new ArrayList<>();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            list.add(readRowAsObjectArray(row, fields));
        }
        return list;
    }

    /**
     * Read a row assuming a fixed number of columns.  Cells that
     * are missing/null in the row are read as null objects.
     *
     * @param row    the Excel row
     * @param fields the fields associated with each row
     * @return a list of java objects representing the contents of the cells
     */
    public static List<Object> readRowAsObjectList(Row row, Field<?>[] fields) {
        if (row == null) {
            throw new IllegalArgumentException("The Row was null");
        }
        if (fields == null) {
            throw new IllegalArgumentException("The Fields array was null");
        }
        int numCol = fields.length;
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < numCol; i++) {
            Cell cell = row.getCell(i);
            Object obj = null;
            if (cell != null) {
                obj = readCellAsObject(cell);
                if (obj instanceof String) {
                    int fieldLength = fields[i].getDataType().length();
                    String s = (String) obj;
                    if (s.length() > fieldLength) {
                        s = s.substring(0, fieldLength - 1);
                        obj = s;
                        LOG.warn("The cell {} was truncated to {} characters for field {}", cell.getStringCellValue(), fieldLength, fields[i].getName());
                    }
                }
            }
            list.add(obj);
        }
        return list;
    }

    /**
     * @param row    the Excel row
     * @param fields the fields associated with each row
     * @return an array of java objects representing the contents of the cells within the row
     */
    public static Object[] readRowAsObjectArray(Row row, Field<?>[] fields) {
        List<Object> objects = readRowAsObjectList(row, fields);
        return objects.toArray();
    }

    /**
     * Read a row assuming a fixed number of columns.  Cells that
     * are missing/null in the row are read as null objects.
     *
     * @param row    the Excel row
     * @param numCol the number of columns in the row
     * @return a list of java objects representing the contents of the cells
     */
    public static List<Object> readRowAsObjectList(Row row, int numCol) {
        if (row == null) {
            throw new IllegalArgumentException("The Row was null");
        }
        if (numCol <= 0) {
            throw new IllegalArgumentException("The number of columns must be >= 1");
        }
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < numCol; i++) {
            Cell cell = row.getCell(i);
            Object obj = null;
            if (cell != null) {
                obj = readCellAsObject(cell);
            }
            list.add(obj);
        }
        return list;
    }

    /**
     * @param row    the Excel row
     * @param numCol the number of columns in the row
     * @return an array of java objects representing the contents of the cells within the row
     */
    public static Object[] readRowAsObjectArray(Row row, int numCol) {
        List<Object> objects = readRowAsObjectList(row, numCol);
        return objects.toArray();
    }

    /**
     * Read a row assuming a fixed number of columns.  Cells that
     * are missing/null in the row are read as null Strings.
     *
     * @param row    the Excel row
     * @param numCol the number of columns in the row
     * @return a list of java objects representing the contents of the cells
     */
    public static List<String> readRowAsStringList(Row row, int numCol) {
        return readRowAsStringList(row, numCol, DEFAULT_MAX_CHAR_IN_CELL);
    }

    /**
     * Read a row assuming a fixed number of columns.  Cells that
     * are missing/null in the row are read as null Strings.
     *
     * @param row     the Excel row
     * @param numCol  the number of columns in the row
     * @param maxChar the maximum number of characters permitted for any string
     * @return a list of java Strings representing the contents of the cells
     */
    public static List<String> readRowAsStringList(Row row, int numCol, int maxChar) {
        if (row == null) {
            throw new IllegalArgumentException("The Row was null");
        }
        if (numCol <= 0) {
            throw new IllegalArgumentException("The number of columns must be >= 1");
        }
        if (maxChar <= 0) {
            throw new IllegalArgumentException("The maximum number of characters must be >= 1");
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < numCol; i++) {
            Cell cell = row.getCell(i);
            String s = null;
            if (cell != null) {
                s = readCellAsString(cell);
                if (s.length() > maxChar) {
                    s = s.substring(0, maxChar - 1);
                    LOG.warn("The cell {} was truncated to {} characters", cell.getStringCellValue(), maxChar);
                }
            }
            list.add(s);
        }
        return list;
    }

    /**
     * Read a row assuming a fixed number of columns.  Cells that
     * are missing/null in the row are read as null Strings.
     *
     * @param row    the Excel row
     * @param numCol the number of columns in the row
     * @return an array of java Strings representing the contents of the cells
     */
    public static String[] readRowAsStringArray(Row row, int numCol) {
        return readRowAsStringArray(row, numCol, DEFAULT_MAX_CHAR_IN_CELL);
    }

    /**
     * Read a row assuming a fixed number of columns.  Cells that
     * are missing/null in the row are read as null Strings.
     *
     * @param row     the Excel row
     * @param numCol  the number of columns in the row
     * @param maxChar the maximum number of characters permitted for any string
     * @return an array of java Strings representing the contents of the cells
     */
    public static String[] readRowAsStringArray(Row row, int numCol, int maxChar) {
        List<String> list = readRowAsStringList(row, numCol, maxChar);
        String[] strings = new String[list.size()];
        return list.toArray(strings);
    }

    /**
     * Reads the Excel cell and translates it into a String
     *
     * @param cell the Excel cell to read data from
     * @return the data in the form of a Java String
     */
    public static String readCellAsString(Cell cell) {
        Objects.requireNonNull(cell, "The Cell must not be null");
        switch (cell.getCellTypeEnum()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    date.toInstant().toString();
                    DATE_TIME_FORMATTER.format(date.toInstant());
                    return DATE_TIME_FORMATTER.format(date.toInstant());
                } else {
                    double value = cell.getNumericCellValue();
                    return Double.toString(value);
                }
            case BOOLEAN:
                boolean value = cell.getBooleanCellValue();
                Boolean.toString(value);
                return Boolean.toString(value);
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * Reads the Excel cell and translates it into a Java object
     *
     * @param cell the Excel cell to read data from
     * @return the data in the form of a Java object
     */
    public static Object readCellAsObject(Cell cell) {
        Objects.requireNonNull(cell, "The Cell must not be null");
        switch (cell.getCellTypeEnum()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * Writes a table from the database to the Excel sheet. Includes the field names as the first row of
     * the sheet.
     *
     * @param db        the database containing the table, must not be null
     * @param tableName the table to read from, must not be null
     * @param sheet     the Excel sheet to write to, must not be null
     */
    public static void writeTableAsExcelSheet(DatabaseIfc db, String tableName, Sheet sheet) {
        Objects.requireNonNull(db, "The database must not be null");
        Objects.requireNonNull(sheet, "The workbook sheet must not be null");
        Objects.requireNonNull(tableName, "The table name must not be null");

        if (!db.containsTable(tableName)) {
            LOG.warn("The supplied table name {} is not in database {}", tableName, db.getLabel());
            return;
        }
        Result<Record> records = db.selectAll(tableName);
        if (records == null){
            LOG.warn("The supplied table name {} resulted in a null Result<Record> nothing was written", tableName);
            return;
        }
        writeResultRecordsAsExcelSheet(records, sheet);
    }

    /**
     * Writes the results from a query to the Excel sheet. Includes the field names as the first row of
     * the sheet.
     *
     * @param records the records from a select query, must not be null
     * @param sheet the Excel sheet to write to, must not be null
     */
    public static void writeResultRecordsAsExcelSheet(Result<Record> records, Sheet sheet){
        Objects.requireNonNull(records, "The Result records must not be null");
        Objects.requireNonNull(sheet, "The workbook sheet must not be null");
        Field[] fields = records.fields();
        Row header = sheet.createRow(0);
        int i = 0;
        for (Field field : fields) {
            Cell cell = header.createCell(i);
            cell.setCellValue(field.getName());
            sheet.setColumnWidth(i, (field.getName().length()+2)*256);
            i++;
        }
        int rowCnt = 1;
        for (Record record : records) {
            Row row = sheet.createRow(rowCnt);
            writeRecordToSheet(record, row);
            rowCnt++;
        }
    }

    /**
     * Writes a single row from the ResultSet to a row in an Excel Sheet
     *
     * @param record the Record to get the data, must not be null
     * @param row    the Excel row, must not be null
     */
    public static void writeRecordToSheet(Record record, Row row) {
        Objects.requireNonNull(record, "The supplied Record must not be null");
        Objects.requireNonNull(row, "The supplied Row must not be null");
        Field<?>[] fields = record.fields();
        int c = 0;
        for (Field field : fields) {
            Cell cell = row.createCell(c);
            writeCell(cell, record.get(field));
            c++;
        }
    }

    /**
     * Writes the Java Object to the Excel cell
     *
     * @param cell   the cell to write
     * @param object a Java object
     */
    public static void writeCell(Cell cell, Object object) {
        if (object == null) {
            // nothing to write
        } else if (object instanceof String) {
            cell.setCellValue((String) ((String) object).trim());
        } else if (object instanceof Boolean) {
            cell.setCellValue((Boolean) object);
        } else if (object instanceof Integer) {
            cell.setCellValue((Integer) object);
        } else if (object instanceof Double) {
            cell.setCellValue((Double) object);
        } else if (object instanceof Float) {
            cell.setCellValue((Float) object);
        } else if (object instanceof BigDecimal) {
            BigDecimal x = (BigDecimal) object;
            cell.setCellValue(x.doubleValue());
        } else if (object instanceof Long) {
            Long x = (Long) object;
            cell.setCellValue(x.doubleValue());
        } else if (object instanceof Short) {
            Short x = (Short) object;
            cell.setCellValue(x.doubleValue());
        } else if (object instanceof java.sql.Date) {
            java.sql.Date x = (java.sql.Date) object;
            cell.setCellValue(x);
            Workbook wb = cell.getSheet().getWorkbook();
            CellStyle cellStyle = wb.createCellStyle();
            CreationHelper createHelper = wb.getCreationHelper();
            cellStyle.setDataFormat(
                    createHelper.createDataFormat().getFormat("m/d/yy"));
            cell.setCellStyle(cellStyle);
        } else if (object instanceof java.sql.Time) {
            java.sql.Time x = (java.sql.Time) object;
            cell.setCellValue(x);
            Workbook wb = cell.getSheet().getWorkbook();
            CellStyle cellStyle = wb.createCellStyle();
            CreationHelper createHelper = wb.getCreationHelper();
            cellStyle.setDataFormat(
                    createHelper.createDataFormat().getFormat("h:mm:ss AM/PM"));
            cell.setCellStyle(cellStyle);
        } else if (object instanceof java.sql.Timestamp) {
            java.sql.Timestamp x = (java.sql.Timestamp) object;
            java.util.Date dateFromTimeStamp = Date.from(x.toInstant());
            double excelDate = DateUtil.getExcelDate(dateFromTimeStamp);
            cell.setCellValue(excelDate);
            Workbook wb = cell.getSheet().getWorkbook();
            CellStyle cellStyle = wb.createCellStyle();
            CreationHelper createHelper = wb.getCreationHelper();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
            cell.setCellStyle(cellStyle);
        } else {
            LOG.error("Could not cast type {} to Excel type.", object.getClass().getName());
            throw new ClassCastException("Could not cast database type to Excel type: " + object.getClass().getName());
        }
    }

    /**
     * Parses and shows the content of one sheet
     * using the specified styles and shared-strings tables.
     *
     * @param styles           The table of styles that may be referenced by cells in the
     *                         sheet
     * @param strings          The table of strings that may be referenced by cells in
     *                         the sheet
     * @param sheetHandler     a sheet handler that knows how to process the sheet
     * @param sheetInputStream The stream to read the sheet-data from.
     * @throws IOException An IO exception from the parser,
     *                     possibly from a byte stream or character stream
     *                     supplied by the application.
     */
    static void processXSSFSheet(StylesTable styles, ReadOnlySharedStringsTable strings,
                                 XSSFSheetXMLHandler.SheetContentsHandler sheetHandler,
                                 InputStream sheetInputStream) throws IOException {
        DataFormatter formatter = new DataFormatter();
        InputSource sheetSource = new InputSource(sheetInputStream);
        try {
            XMLReader sheetParser = SAXHelper.newXMLReader();
            ContentHandler handler = new XSSFSheetXMLHandler(
                    styles, null, strings, sheetHandler, formatter, false);
            sheetParser.setContentHandler(handler);
            sheetParser.parse(sheetSource);
        } catch (ParserConfigurationException e) {
            LOG.error("SAX parser appears to be broken - {}", e.getMessage());
            throw new IOException("SAX parser appears to be broken - " + e.getMessage());
        } catch (SAXException e) {
            LOG.error("XML reader appears to be broken - {}", e.getMessage());
            throw new IOException("XML reader appears to be broken - " + e.getMessage());
        }
    }

    /**
     * Initiates the processing of the XLSX workbook using the supplied sheet handler
     *
     * @param xlsxPackage  the xlsx package context for the workbook
     * @param sheetHandler the handler to process each sheet
     * @throws IOException If reading the data from the package fails.
     */
    static void processAllXSSFSheets(OPCPackage xlsxPackage, XSSFSheetXMLHandler.SheetContentsHandler
            sheetHandler) throws IOException {
        ReadOnlySharedStringsTable strings = null;
        try {
            strings = new ReadOnlySharedStringsTable(xlsxPackage);
        } catch (SAXException e) {
            LOG.error("SAX parser appears to be broken - {}", e.getMessage());
            throw new IOException("SAX parser appears to be broken - " + e.getMessage());
        }
        XSSFReader xssfReader = null;
        try {
            xssfReader = new XSSFReader(xlsxPackage);
        } catch (OpenXML4JException e) {
            LOG.error("XML reader appears to be broken - {}", e.getMessage());
            throw new IOException("The XML reader appears to be broken - " + e.getMessage());
        }
        StylesTable styles = null;
        try {
            styles = xssfReader.getStylesTable();
        } catch (InvalidFormatException e) {
            LOG.error("The workbook seems to have a format problem - {}", e.getMessage());
            throw new IOException("The workbook seems to have a format problem - " + e.getMessage());
        }
        XSSFReader.SheetIterator iter = null;
        try {
            iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        } catch (InvalidFormatException e) {
            LOG.error("The sheet seems to have a format problem - {}", e.getMessage());
            throw new IOException("The sheet seems to have a format problem - " + e.getMessage());
        }
        int index = 0;
        while (iter.hasNext()) {
            InputStream stream = iter.next();
            String sheetName = iter.getSheetName();
            processXSSFSheet(styles, strings, sheetHandler, stream);
            stream.close();
            ++index;
        }
    }

}
