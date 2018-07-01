package jsl.utilities.dbutil;

import jsl.observers.JSLDatabase;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.SQLDialect;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class DataSourceFactoryTest {

    public static void main(String[] args) throws IOException {

       // makeDbTest();
       // testDerbyLocalHost();
       // testDataSourceConnection();
        testParsing();
    }

    public static void testDataSourceConnection(){
        Path path = JSLDatabase.dbDir.resolve("JSLDb_DLB_with_Q");
        String name = path.toAbsolutePath().toString();
//        DataSource dataSource = DataSourceFactory.getClientDerbyDataSourceWithLocalHost(name,
//                null, null, false);
        DataSource dataSource = DataSourceFactory.getEmbeddedDerbyDataSource(name,
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
        DataSource dataSource = DataSourceFactory.getClientDerbyDataSourceWithLocalHost(name,
                null, null, false);
        Database db = new Database("JSL", dataSource, SQLDialect.DERBY);
        db.setJooQDefaultExecutionLoggingOption(true);

        List<String> jsl_db = db.getTableNames("JSL_DB");

        for(String s: jsl_db){
            System.out.println(s);
        }
        db.printTableAsText("ACROSS_REP_STAT");
    }

    public static void testDerbyEmbeddedWithCreateScript() throws IOException {
        Path path = JSLDatabase.dbDir.resolve("TmpDb");
        Path pathToCreationScript = JSLDatabase.dbScriptsDir.resolve("JSLDb.sql");
        DataSource dataSource = DataSourceFactory.getEmbeddedDerbyDataSource(path, true);
        Database db = new Database("TmpDb", dataSource, SQLDialect.DERBY);
        db.executeScript(pathToCreationScript);
        List<String> jsl_db = db.getTableNames("JSL_DB");

        for(String s: jsl_db){
            System.out.println(s);
        }
    }

    public static void testDerbyEmbeddedExisting(){
        Path path = JSLDatabase.dbDir.resolve("JSLDb_DLB_with_Q");
        DataSource dataSource = DataSourceFactory.getEmbeddedDerbyDataSource(path);
        Database db = new Database("JSL", dataSource, SQLDialect.DERBY);
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
        DataSource dataSource = DataSourceFactory.getEmbeddedDerbyDataSource(path, true);
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
}
