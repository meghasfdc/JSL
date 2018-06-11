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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author rossetti
 */
public class ExcelUtil {

    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Writes the supplied database to an Excel workbook with one sheet for
     * every table, squelching all exceptions. The workbook will have the same
     * name as the database
     *
     * @param db the database to read data from
     */
    public static void runWriteDBAsExcelWorkbook(DatabaseIfc db) {
        runWriteDBAsExcelWorkbook(db, null);
    }

    /**
     * Writes the supplied database to an Excel workbook with one sheet for
     * every table, squelching all exceptions
     *
     * @param db             the database to read data from
     * @param pathToWorkbook the name of the workbook that is to be made
     */
    public static void runWriteDBAsExcelWorkbook(DatabaseIfc db, Path pathToWorkbook) {
        try {
            writeDBAsExcelWorkbook(db, pathToWorkbook);
        } catch (FileNotFoundException ex) {
            logger.error("FileNotFoundException {} ", pathToWorkbook, ex);
            ex.printStackTrace();
        } catch (IOException ex) {
            logger.error("Error in {} runWriteDBAsExcelWorkbook()", pathToWorkbook, ex);
            ex.printStackTrace();
        }

    }

    /**
     * Runs writeWorkbookToDatabase() squelching all exceptions. The first row of each sheet is skipped.
     *
     * @param pathToWorkbook the path to the workbook. Must be valid workbook with .xlsx extension
     * @param db             the database to write to
     * @param tableNames     the names of the sheets and tables in the order that needs to be written
     */
    public static void runWriteWorkbookToDatabase(Path pathToWorkbook, DatabaseIfc db,
                                                  List<String> tableNames) {
        runWriteWorkbookToDatabase(pathToWorkbook, true, db, tableNames);
    }

    /**
     * Runs writeWorkbookToDatabase() squelching all exceptions
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
            logger.error("IOException {} ", pathToWorkbook, e);
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            logger.error("InvalidFormatException {} ", pathToWorkbook, e);
            e.printStackTrace();
        }
    }

    /**
     * Writes the supplied database to an Excel workbook with one sheet for
     * every table. This will produce an Excel file with the same name as the
     * database in the current working directory.
     *
     * @param db the database to read data from
     * @throws IOException           io exception
     */
    public static void writeDBAsExcelWorkbook(DatabaseIfc db)
            throws IOException {
        writeDBAsExcelWorkbook(db, null);
    }

    /**
     * Writes the supplied database to an Excel workbook with one sheet for
     * every table. This will produce an Excel file with the supplied name in
     * the current working directory. Each sheet of the workbook will have
     * the field names as the first row in the sheet.
     *
     * @param db             the database to read data from
     * @param pathToWorkbook the name of the workbook that was made
     * @throws IOException           io exception
     */
    public static void writeDBAsExcelWorkbook(DatabaseIfc db, Path pathToWorkbook)
            throws IOException {
        //  if null make the name of the workbook the same as the database name
        if (pathToWorkbook == null) {
            Path currentDir = Paths.get(".");
            pathToWorkbook = currentDir.resolve(db.getName() + ".xlsx");
        }
        logger.debug("Writing database {} to Excel workbook {}.", db.getName(), pathToWorkbook);
        List<String> tables = db.getTableNames();
        XSSFWorkbook workbook = new XSSFWorkbook();
        for (String tableName : tables) {
            Sheet sheet = workbook.createSheet(tableName);
            logger.debug("Writing table {} to Excel sheet.", tableName);
            writeTableAsExcelSheet(db, tableName, sheet);
        }

        FileOutputStream out = new FileOutputStream(pathToWorkbook.toFile());
        workbook.write(out);
        workbook.close();
        out.close();
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
     * @throws IOException            an io exception
     * @throws InvalidFormatException if the workbook has an invalid format
     */
    public static void writeWorkbookToDatabase(Path pathToWorkbook, DatabaseIfc db,
                                               List<String> tableNames) throws IOException, InvalidFormatException {
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
     * @throws IOException            an io exception
     * @throws InvalidFormatException if the workbook has an invalid format
     */
    public static void writeWorkbookToDatabase(Path pathToWorkbook, boolean skipFirstRow, DatabaseIfc db,
                                               List<String> tableNames) throws IOException, InvalidFormatException {
        if (pathToWorkbook == null) {
            throw new IllegalArgumentException("The path to the workbook was null");
        }
        File file = pathToWorkbook.toFile();
        OPCPackage pkg = OPCPackage.open(file);
        XSSFWorkbook wb = new XSSFWorkbook(pkg);
        logger.debug("Writing workbook {} to database {}",  pathToWorkbook, db.getName());
        writeWorkbookToDatabase(wb, skipFirstRow, db, tableNames);
        //wb.close();
        pkg.close();
        logger.debug("Completed writing workbook {} to database {}",  pathToWorkbook, db.getName());
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
    public static void writeWorkbookToDatabase(XSSFWorkbook wb, DatabaseIfc db,
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
    public static void writeWorkbookToDatabase(XSSFWorkbook wb, boolean skipFirstRow, DatabaseIfc db,
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
            XSSFSheet sheet = wb.getSheet(tableName);
            if (sheet == null){
                logger.debug("Skipping table {} no corresponding sheet in workbook", tableName);
                continue;
            }
            writeSheetToTable(sheet, skipFirstRow, tableName, db);
        }
    }

    /**
     * Wrties the sheet to the named table.  Automatically skips the first row of the sheet. Uses
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
     * @param sheet        the sheet to get the data from
     * @param skipFirstRow true means skip the first row of the Excel sheet
     * @param tableName    the name of the table to write to
     * @param db           the database containing the table
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
        if (!db.tableExists(tableName)) {
            throw new IllegalArgumentException("The database does not contain a table named: " + tableName);
        }

        final Table<? extends Record> table = db.getTable(tableName);
        final Field<?>[] fields = table.fields();
        logger.debug("Reading sheet {} for table {} in database {}", sheet.getSheetName(), tableName, db.getName());
        final List<Object[]> lists = readSheetAsListOfObjects(sheet, fields, skipFirstRow);
        db.getDSLContext().loadInto(table).batchAll().loadArrays(lists.iterator()).fields(fields).execute();
        logger.debug("Wrote sheet {} for table {} into database {}", sheet.getSheetName(), tableName, db.getName());
    }

    /**
     * @param sheet the sheet to process
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
            list.add(readRowAsObjects(rowIterator.next(), fields));
        }
        return list;
    }

    /**
     * @param sheet the sheet to process
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
            list.add(readRowsAsObjectArray(row, fields));
        }
        return list;
    }

    /** Read a row assuming a fixed number of columns.  Cells that
     *  are missing/null in the row are read as null objects.
     *
     * @param row    the Excel row
     * @param fields the fields associated with each row
     * @return a list of java objects representing the contents of the cells
     */
    public static List<Object> readRowAsObjects(Row row, Field<?>[] fields) {
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
                if (obj instanceof String){
                    int fieldLength = fields[i].getDataType().length();
                    String s = (String)obj;
                    if (s.length() > fieldLength){
                        s = s.substring(0,fieldLength-1);
                        obj = s;
                        logger.warn("The cell {} was truncated to {} characters for field {}", cell.getStringCellValue(), fieldLength, fields[i].getName());
                    }
                }
            }
            list.add(obj);
        }
        return list;
    }

    /**
     * @param row the Excel row
     * @param fields the fields associated with each row
     * @return an array of java objects representing the contents of the cells within the row
     */
    public static Object[] readRowsAsObjectArray(Row row, Field<?>[] fields) {
        List<Object> objects = readRowAsObjects(row, fields);
        return objects.toArray();
    }

    /**
     * Reads the Excel cell and translates it into a Java object
     *
     * @param cell the Excel cell to read data from
     * @return the data in the form of a Java object
     */
    public static Object readCellAsObject(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("The Cell was null");
        }
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
     * @param db        the database containing the table
     * @param tableName the table to read from
     * @param sheet     the Excel sheet to write to
     */
    public static void writeTableAsExcelSheet(DatabaseIfc db, String tableName, Sheet sheet) {
        if (db == null) {
            throw new IllegalArgumentException("The database was null");
        }
        if (tableName == null) {
            throw new IllegalArgumentException("The table was null");
        }
        if (sheet == null) {
            throw new IllegalArgumentException("The sheet was null");
        }
        if (!db.tableExists(tableName)) {
            return;
        }
        Result<Record> records = db.selectAll(tableName);

        Field[] fields = records.fields();
        Row header = sheet.createRow(0);
        int i = 0;
        for (Field field : fields) {
            Cell cell = header.createCell(i);
            cell.setCellValue(field.getName());
            i++;
        }
        int rowCnt = 1;
        for (Record record : records) {
            Row row = sheet.createRow(rowCnt);
            writeRecordToSheet(record, row);
            rowCnt++;
        }
        i = 0;
        for (Field field : fields) {
            sheet.autoSizeColumn(i);
            i++;
        }

    }

    /**
     * Writes a single row from the ResultSet to a row in the Excel Sheet
     *
     * @param record the Record to get the data
     * @param row    the Excel row
     */
    protected static void writeRecordToSheet(Record record, Row row) {
        if (record == null) {
            throw new IllegalArgumentException("The record was null");
        }
        if (row == null) {
            throw new IllegalArgumentException("The row was null");
        }
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
        } else if (object instanceof Short){
            Short x = (Short)object;
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
            java.sql.Timestamp x = (java.sql.Timestamp)object;
            java.util.Date dateFromTimeStamp = Date.from(x.toInstant());
            double excelDate = DateUtil.getExcelDate(dateFromTimeStamp);
            cell.setCellValue(excelDate);
            Workbook wb = cell.getSheet().getWorkbook();
            CellStyle cellStyle = wb.createCellStyle();
            CreationHelper createHelper = wb.getCreationHelper();
            cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
            cell.setCellStyle(cellStyle);
        } else {
            logger.error("Could not cast type {} to Excel type.", object.getClass().getName());
            throw new ClassCastException("Could not cast database type to Excel type: " + object.getClass().getName() );
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
     * @throws IOException  An IO exception from the parser,
     *                      possibly from a byte stream or character stream
     *                      supplied by the application.
     * @throws SAXException if parsing the XML data fails.
     */
    public static void processXSSFSheet(StylesTable styles, ReadOnlySharedStringsTable strings,
                                        XSSFSheetXMLHandler.SheetContentsHandler sheetHandler,
                                        InputStream sheetInputStream) throws IOException, SAXException {
        DataFormatter formatter = new DataFormatter();
        InputSource sheetSource = new InputSource(sheetInputStream);
        try {
            XMLReader sheetParser = SAXHelper.newXMLReader();
            ContentHandler handler = new XSSFSheetXMLHandler(
                    styles, null, strings, sheetHandler, formatter, false);
            sheetParser.setContentHandler(handler);
            sheetParser.parse(sheetSource);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
        }
    }

    /**
     * Initiates the processing of the XLSX workbook using the supplied sheet handler
     *
     * @param xlsxPackage  the xlsx package context for the workbook
     * @param sheetHandler the handler to process each sheet
     * @throws IOException        If reading the data from the package fails.
     * @throws OpenXML4JException if parsing XML fails
     * @throws SAXException       if parsing the XML data fails.
     */
    public static void processAllXSSFSheets(OPCPackage xlsxPackage, XSSFSheetXMLHandler.SheetContentsHandler
            sheetHandler)
            throws IOException, OpenXML4JException, SAXException {
        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(xlsxPackage);
        XSSFReader xssfReader = new XSSFReader(xlsxPackage);
        StylesTable styles = xssfReader.getStylesTable();
        XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
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
