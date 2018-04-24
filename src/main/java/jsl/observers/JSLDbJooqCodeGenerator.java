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

package jsl.observers;

import jsl.utilities.dbutil.EmbeddedDerbyDatabase;
import jsl.utilities.reporting.JSL;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class JSLDbJooqCodeGenerator {

    public static void main(String[] args) throws InvalidFormatException, SQLException, IOException {

//        Path path = Paths.get("src").resolve("main").resolve("resources");

 //       System.out.println(path);
        //makeEmptyDb("JSLDbCodeGen", "JSLDb.sql");

        runCodeGeneration();

    }

    public static EmbeddedDerbyDatabase makeEmptyDb(String dbName, String scriptName) throws IOException,
            SQLException, InvalidFormatException {
        System.out.println("Making database: " + dbName);

        FileUtils.deleteDirectory(JSLDb.dbDir.resolve(dbName).toFile());
        Path createScript = JSLDb.dbScriptsDir.resolve(scriptName);
        EmbeddedDerbyDatabase db = EmbeddedDerbyDatabase.createDb(dbName, JSLDb.dbDir)
                .withCreationScript(createScript)
                .connect();
        System.out.println("Created database: " + dbName);
        return db;
    }

    public static void runCodeGeneration() throws IOException,
            SQLException, InvalidFormatException {

        // make the database
        String dbName = "tmpJSLDb";
        System.out.println("Making database: " + dbName);
        Path dbDir = Paths.get(".", "db");
        FileUtils.deleteDirectory(dbDir.resolve(dbName).toFile());
        Path createScript = Paths.get("src").resolve("main").resolve("resources").resolve("JSLDb.sql");
        EmbeddedDerbyDatabase db = EmbeddedDerbyDatabase.createDb(dbName, dbDir)
                .withCreationScript(createScript)
                .connect();
        System.out.println("Created database: " + dbName);
        Path dbPath = dbDir.resolve(dbName);
        System.out.println("Running code generation.");
        EmbeddedDerbyDatabase.runJooQCodeGeneration(dbPath,"src/main/java", "jsl.utilities.jsldbsrc");
        System.out.println("Completed code generation.");
        System.out.println("Deleting the database");
        FileUtils.deleteDirectory(dbDir.resolve(dbName).toFile());
    }
}
