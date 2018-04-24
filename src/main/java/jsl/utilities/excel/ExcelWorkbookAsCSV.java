/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * @author rossetti
 */
public class ExcelWorkbookAsCSV {

    final static org.slf4j.Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private OPCPackage myPackage;
    private ReadOnlySharedStringsTable myReadOnlySharedStrings;
    private XSSFReader myXSSFReader;
    private StylesTable myStyles;
    private XSSFWorkbook myWorkBook;

    public ExcelWorkbookAsCSV(Path pathToWorkbook) {

        try {
            myPackage = OPCPackage.open(pathToWorkbook.toString(), PackageAccess.READ);
            myWorkBook = new XSSFWorkbook(myPackage);
            myReadOnlySharedStrings = new ReadOnlySharedStringsTable(myPackage);
            myXSSFReader = new XSSFReader(myPackage);
            myStyles = myXSSFReader.getStylesTable();
        } catch (InvalidFormatException | InvalidOperationException ex) {
            logger.error("Error making ExcelWorkbookAsCSV {} ", pathToWorkbook, ex);
        } catch (IOException | SAXException | OpenXML4JException ex) {
            logger.error("Error making ExcelWorkbookAsCSV {} ", pathToWorkbook, ex);
        }
    }

    public void writeXSSFSheetToCSV(String sheetName) {
        writeXSSFSheetToCSV(sheetName, null);
    }

    /**
     * @param sheetName the name of the sheet to write to the file
     * @param path      the path to the file to write to
     */
    public void writeXSSFSheetToCSV(String sheetName, Path path) {
        if (sheetName == null) {
            throw new IllegalArgumentException("The sheet name cannot be null!");
        }

        if (myWorkBook.getSheet(sheetName) == null) {
            throw new IllegalArgumentException("The sheet name does not exist in the workbook!");
        }

        if (path == null) {
            // form path to current location with same name as sheet
            path = Paths.get(".", sheetName + ".csv");
        }

        try {
            Files.createDirectories(path.getParent());
            OutputStream newOutputStream = Files.newOutputStream(path);
            PrintWriter printWriter = new PrintWriter(newOutputStream);
            XSSFSheet sheet = myWorkBook.getSheet(sheetName);
            String rId = myWorkBook.getRelationId(sheet);
            sheet.getRelationId(sheet);
            InputStream sheetInputStream = myXSSFReader.getSheet(rId);
            ExcelSheetToCSV excelSheetToCSV = new ExcelSheetToCSV(printWriter);
            ExcelUtil.processXSSFSheet(myStyles, myReadOnlySharedStrings, excelSheetToCSV, sheetInputStream);
            sheetInputStream.close();
            printWriter.flush();
            printWriter.close();
        } catch (IOException | InvalidFormatException | SAXException ex) {
            logger.error("Error writeXSSFSheetToCSV {} ", sheetName, ex);
        }
    }

    /**
     * Writes all sheets of the workbook in the directory, naming
     * each output file the name of each sheet
     *
     * @param pathToOutPutDirectory
     */
    public void writeXSSFWorkbookToCSV(Path pathToOutPutDirectory) {
        try {
            Files.createDirectories(pathToOutPutDirectory);
        } catch (IOException ex) {
            logger.error("Error writeXSSFWorkbookToCSV {} ", pathToOutPutDirectory, ex);
        }
        int numSheets = myWorkBook.getNumberOfSheets();
        for (int i = 0; i < numSheets; i++) {
            String sheetName = myWorkBook.getSheetName(i);
            Path path = Paths.get(pathToOutPutDirectory.toString(), sheetName + ".csv");
            writeXSSFSheetToCSV(sheetName, path);
        }
    }
}
