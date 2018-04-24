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
