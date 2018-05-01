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


import jsl.utilities.excel.ExcelUtil;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestEmbeddedDerbyDatabase {

    public static Path dbDir = Paths.get(".", "db");
    public static Path dbScriptsDir = Paths.get(".", "dbScripts");

    public static void main(String[] args) throws IOException,
            SQLException, InvalidFormatException {
//        createEmptyDb("CreateOnlyDB");
//        createFullSPDb("FullSPDB");
//        createDbWithStructure("CreateWithStructureDB");
//        createDbWithStructureAndInsertData("CreateWithStructureAndDataViaInsertsDB");
//        createDbWithStructureAndDataAndConstraints("CreateWithStructureAndDataViaInsertsWithConstraintsDB");
//        EmbeddedDerbyDatabase db = createDBFromExcelWorkbook("SPFromExcel");
//        connectToExisting("CreateWithStructureAndDataViaInsertsWithConstraintsDB");
//        createExcelWorkbookFromDB("CreateWithStructureAndDataViaInsertsWithConstraintsDB");
        //duplicateDatabaseTest("FullSPDB");
        Path directory = dbDir.resolve("Clones");
        EmbeddedDerbyDatabase.connectDb("FullSPDBDUPLICATE",directory).connect().printAllTablesAsText();
    }

//    public static void testDuplication() throws IOException,
//            SQLException, InvalidFormatException {
//        System.out.println("Printing the original:");
//        EmbeddedDerbyDatabase db = connectToExisting("SPFromExcel");
//
//        EmbeddedDerbyDatabase sp_duplicate = db.duplicate("SP_Duplicate");
//        System.out.println("Printing the duplicate:");
//        sp_duplicate.printAllTablesAsText();
//    }

    public static EmbeddedDerbyDatabase createEmptyDb(String name) throws IOException,
            SQLException, InvalidFormatException {
        FileUtils.deleteDirectory(dbDir.resolve(name).toFile());
        EmbeddedDerbyDatabase db = EmbeddedDerbyDatabase.createDb(name, dbDir).connect();
        System.out.println("Printing empty database:");
        db.printAllTablesAsText();
        return db;
    }

    public static EmbeddedDerbyDatabase createFullSPDb(String name) throws IOException,
            SQLException, InvalidFormatException {
        FileUtils.deleteDirectory(dbDir.resolve(name).toFile());
        Path createScript = dbScriptsDir.resolve("SPDatabase_FullCreate.sql");
        EmbeddedDerbyDatabase db = EmbeddedDerbyDatabase.createDb(name, dbDir)
                .withCreationScript(createScript).connect();
        System.out.println("Printing full database:");
        db.printAllTablesAsText();
        return db;
    }

    public static EmbeddedDerbyDatabase createDbWithStructure(String name) throws IOException,
            SQLException, InvalidFormatException {
        FileUtils.deleteDirectory(dbDir.resolve(name).toFile());
        Path tablesScript = dbScriptsDir.resolve("SPDatabase_Tables.sql");
        EmbeddedDerbyDatabase db = EmbeddedDerbyDatabase.createDb(name, dbDir).withTables(tablesScript).connect();
        System.out.println("Printing empty database:");
        db.printAllTablesAsText();
        return db;
    }

    public static EmbeddedDerbyDatabase createDbWithStructureAndInsertData(String name) throws IOException,
            SQLException, InvalidFormatException {
        FileUtils.deleteDirectory(dbDir.resolve(name).toFile());
        Path tablesScript = dbScriptsDir.resolve("SPDatabase_Tables.sql");
        Path insertScript = dbScriptsDir.resolve("SPDatabase_Insert.sql");
        EmbeddedDerbyDatabase db = EmbeddedDerbyDatabase.createDb(name, dbDir)
                .withTables(tablesScript)
                .withInsertData(insertScript)
                .connect();
        System.out.println("Printing inserted database:");
        db.printAllTablesAsText();
        return db;
    }

    public static EmbeddedDerbyDatabase createDbWithStructureAndDataAndConstraints(String name) throws IOException,
            SQLException, InvalidFormatException {
        FileUtils.deleteDirectory(dbDir.resolve(name).toFile());
        Path tablesScript = dbScriptsDir.resolve("SPDatabase_Tables.sql");
        Path insertScript = dbScriptsDir.resolve("SPDatabase_Insert.sql");
        Path alterScript = dbScriptsDir.resolve("SPDatabase_Alter.sql");
        EmbeddedDerbyDatabase db = EmbeddedDerbyDatabase.createDb(name, dbDir)
                .withTables(tablesScript)
                .withInsertData(insertScript)
                .withConstraints(alterScript)
                .connect();
        System.out.println("Printing inserted database:");
        db.printAllTablesAsText();

        System.out.println();
        System.out.println("Printing alter command");
        db.getAlterCommands().forEach(System.out::println);
        System.out.println();

        System.out.println("Printing jooq DDL queries");
        db.printJOOQDDLQueries();
        System.out.println("As of Jan 26, 2018: JOOQ is missing the unique index constraints.");
        return db;
    }

    public static EmbeddedDerbyDatabase connectToExisting(String name) throws IOException,
            SQLException, InvalidFormatException {
        EmbeddedDerbyDatabase db = EmbeddedDerbyDatabase.connectDb(name, dbDir).connect();
        System.out.println("Printing the connected database:");
        db.printAllTablesAsText();
        return db;
    }

    public static EmbeddedDerbyDatabase createDBFromExcelWorkbook(String name) throws IOException,
            SQLException, InvalidFormatException {
        FileUtils.deleteDirectory(dbDir.resolve(name).toFile());
        Path tablesScript = dbScriptsDir.resolve("SPDatabase_Tables.sql");
        Path excelDir = Paths.get(".", "jsl/utilities/excel");
        Path wbPath = excelDir.resolve("SP.xlsx");
        Path alterScript = dbScriptsDir.resolve("SPDatabase_Alter.sql");
        List<String> tableNames = new ArrayList<>(Arrays.asList("P", "S", "SP"));
        EmbeddedDerbyDatabase db = EmbeddedDerbyDatabase.createDb(name, dbDir)
                .withTables(tablesScript)
                .withExcelData(wbPath, tableNames)
                .withConstraints(alterScript)
                .connect();
        System.out.println("Printing inserted database from Excel:");
        db.printAllTablesAsText();
        return db;
    }

    public static void createExcelWorkbookFromDB(String name) throws IOException,
            SQLException, InvalidFormatException {
        EmbeddedDerbyDatabase db = EmbeddedDerbyDatabase.connectDb(name, dbDir).connect();
        ExcelUtil.runWriteDBAsExcelWorkbook(db);
    }

    public static void duplicateDatabaseTest(String name) throws InvalidFormatException, SQLException, IOException {
        // get the database
        EmbeddedDerbyDatabase db = EmbeddedDerbyDatabase.connectDb(name, dbDir).connect();
        String dupName = db.getName() + "DUPLICATE";

        Path directory = Files.createDirectory(dbDir.resolve("Clones"));
        db.copyDb(dupName, directory);
    }

}
