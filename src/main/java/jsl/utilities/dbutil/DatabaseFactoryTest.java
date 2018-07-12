package jsl.utilities.dbutil;

import jsl.utilities.reporting.JSL;
import jsl.utilities.reporting.JSLDatabase;
import org.jooq.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DatabaseFactoryTest {

    public static void main(String[] args) throws IOException {

       // testDerbyLocalHost();
       // testDataSourceConnection();
       // testParsing();
   //     testDatabaseCreation();
        //testDerbyEmbeddedExisting();
 //       testExcelDbExport();
  //      testExcelDbImport();

 //       metaDataTest();

        testPostgresLocalHost();
    }

    public static void testDataSourceConnection(){
        Path path = JSLDatabase.dbDir.resolve("JSLDb_DLB_with_Q");
        String name = path.toAbsolutePath().toString();
//        DataSource dataSource = DataSourceFactory.createClientDerbyDataSourceWithLocalHost(name,
//                null, null, false);
        DataSource dataSource = DatabaseFactory.createEmbeddedDerbyDataSource(name,
                null, null, false);
        try {
            dataSource.getConnection();
            System.out.println("Connection established.");
        } catch (SQLException e) {
            System.out.println("Could not establish a connection.");
        }

    }
    public static void testDerbyLocalHost(){
        //note the server must be started for this to work
        Path path = JSLDatabase.dbDir.resolve("JSLDb_DLB_with_Q");
        String name = path.toAbsolutePath().toString();
        DataSource dataSource = DatabaseFactory.createClientDerbyDataSourceWithLocalHost(name,
                null, null, false);
        Database db = new Database("JSL", dataSource, SQLDialect.DERBY);
        db.setJooQDefaultExecutionLoggingOption(true);

        List<String> jsl_db = db.getTableNames("JSL_DB");

        for(String s: jsl_db){
            System.out.println(s);
        }
        System.out.println();
        db.printTableAsText("ACROSS_REP_STAT");
    }

    public static void testPostgresLocalHost(){
        String dbName = "temp";
        String user = "test";
        String pw = "test";
        DataSource dataSource = DatabaseFactory.getPostGressDataSourceWithLocalHost(dbName, user, pw);
        // make the database
        Database db = new Database(dbName, dataSource, SQLDialect.POSTGRES);
        // builder the creation task
        Path pathToCreationScript = JSLDatabase.dbScriptsDir.resolve("SPDatabase_Postgres.sql");

//        Path tables = JSLDatabase.dbScriptsDir.resolve("SPDatabase_Tables.sql");
//        Path inserts = JSLDatabase.dbScriptsDir.resolve("SPDatabase_Insert.sql");
//        Path alters = JSLDatabase.dbScriptsDir.resolve("SPDatabase_Alter.sql");

//        DbCreateTask task = db.create()
//                .withTables(tables).withInserts(inserts).withConstraints(alters)
//                .execute();

        DbCreateTask task = db.create().withCreationScript(pathToCreationScript).execute();
        System.out.println(task);

        task.getCreationScriptCommands().forEach(System.out::println);
//        task.getInsertCommands().forEach(System.out::println);
//        task.getAlterCommands().forEach(System.out::println);

        System.out.println(task);

        db.printTableAsText("s");

    }

    public static void testDerbyEmbeddedWithCreateScript() throws IOException {
        Path path = JSLDatabase.dbDir.resolve("TmpDb");
        Path pathToCreationScript = JSLDatabase.dbScriptsDir.resolve("JSLDb.sql");
        DataSource dataSource = DatabaseFactory.createEmbeddedDerbyDataSource(path, true);
        Database db = new Database("TmpDb", dataSource, SQLDialect.DERBY);
        db.executeScript(pathToCreationScript);
        List<String> jsl_db = db.getTableNames("JSL_DB");

        for(String s: jsl_db){
            System.out.println(s);
        }
    }

    public static void testDerbyEmbeddedExisting(){
        Path path = JSLDatabase.dbDir.resolve("JSLDb_DLB_with_Q");
        Database db = DatabaseFactory.getEmbeddedDerbyDatabase("JSLDb_DLB_with_Q");
//        DataSource dataSource = DatabaseFactory.createEmbeddedDerbyDataSource(path);
//        Database db = new Database("JSL", dataSource, SQLDialect.DERBY);
        db.setJooQDefaultExecutionLoggingOption(true);

        List<String> jsl_db = db.getTableNames("JSL_DB");

        for(String s: jsl_db){
            System.out.println(s);
        }
        db.printTableAsText("ACROSS_REP_STAT");
    }

    public static void testParsing() throws IOException {
        Path path = JSLDatabase.dbDir.resolve("TmpDb2");
        Path pathToCreationScript = JSLDatabase.dbScriptsDir.resolve("JSLDb.sql");
        DataSource dataSource = DatabaseFactory.createEmbeddedDerbyDataSource(path, true);
        Database db = new Database("TmpDb2", dataSource, SQLDialect.DERBY);
        List<String> lines = DatabaseIfc.parseQueriesInSQLScript(pathToCreationScript);
        //List<String> lines = Files.readAllLines(pathToCreationScript, StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        for(String s: lines){
            sb.append(s).append(";").append(System.lineSeparator());
        }
//        System.out.println(sb);
        Queries queries = db.getDSLContext().parser().parse(sb.toString());
        Iterator<Query> iterator = queries.iterator();

        int i = 1;
        for(String s: lines){
            System.out.println("Line " + i);
            System.out.print("Original:    ");
            System.out.println(s);
            Query query = iterator.next();
            //System.out.println();
            System.out.print("Jooq Parser: ");
            System.out.println(query.getSQL());
            System.out.println();
            i++;
        }
//        while(iterator.hasNext()){
//            Query query = iterator.next();
//            System.out.println(query.getSQL());
//            System.out.println("Executing query");
//            query.execute();
//        }

        //System.out.println("Executing batch of queries");
        // queries.executeBatch();
    }

    public static void testDatabaseCreation(){
        Path path = JSLDatabase.dbDir.resolve("TestCreationTaskDb");
        String name = path.toAbsolutePath().toString();
        // just create it
        DataSource dataSource = DatabaseFactory.createEmbeddedDerbyDataSource(name, true);
        // make the database
        Database db = new Database(name, dataSource, SQLDialect.DERBY);
        // builder the creation task
        //Path pathToCreationScript = JSLDatabase.dbScriptsDir.resolve("JSLDb.sql");

        Path tables = JSLDatabase.dbScriptsDir.resolve("SPDatabase_Tables.sql");
        Path inserts = JSLDatabase.dbScriptsDir.resolve("SPDatabase_Insert.sql");
        Path alters = JSLDatabase.dbScriptsDir.resolve("SPDatabase_Alter.sql");

        DbCreateTask task = db.create()
                .withTables(tables).withInserts(inserts).withConstraints(alters)
                .execute();
        System.out.println(task);

        task.getCreationScriptCommands().forEach(System.out::println);
        task.getInsertCommands().forEach(System.out::println);
        task.getAlterCommands().forEach(System.out::println);

        System.out.println(task);
    }

    public static void testExcelDbExport() throws IOException {
        String dbName = "SP";
        // make the database
        Database db = DatabaseFactory.createEmbeddedDerbyDatabase(dbName);
        // builder the creation task
        Path tables = JSLDatabase.dbScriptsDir.resolve("SPDatabase_Tables.sql");
        Path inserts = JSLDatabase.dbScriptsDir.resolve("SPDatabase_Insert.sql");
        Path alters = JSLDatabase.dbScriptsDir.resolve("SPDatabase_Alter.sql");
        DbCreateTask task = db.create().withTables(tables).withInserts(inserts).withConstraints(alters)
                .execute();

        System.out.println(task);
        db.writeDbToExcelWorkbook("APP");
    }

    public static void testExcelDbImport() throws IOException {
        String dbName = "SPViaExcel";
        // make the database
        Database db = DatabaseFactory.createEmbeddedDerbyDatabase(dbName);

        // builder the creation task
        Path tables = JSLDatabase.dbScriptsDir.resolve("SPDatabase_Tables.sql");
        Path inserts = JSLDatabase.dbScriptsDir.resolve("SPDatabase_Insert.sql");
        Path alters = JSLDatabase.dbScriptsDir.resolve("SPDatabase_Alter.sql");

        Path wbPath = JSL.ExcelDir.resolve("SP.xlsx");

        db.create().withTables(tables)
                .withExcelData(wbPath, Arrays.asList("S", "P", "SP"))
                .withConstraints(alters)
                .execute();

        db.printAllTablesAsText("APP");
    }

    public static void metaDataTest(){

        Database sp = DatabaseFactory.getEmbeddedDerbyDatabase("SP");
        Meta meta = sp.getDSLContext().meta();

        System.out.println("The catalogs are:");
        List<Catalog> catalogs = meta.getCatalogs();
        for(Catalog c: catalogs){
            System.out.println(c);
        }
        List<Schema> schemas = meta.getSchemas();
        System.out.println(schemas);

    }
}
