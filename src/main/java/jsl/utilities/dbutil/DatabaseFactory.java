package jsl.utilities.dbutil;

import jsl.utilities.reporting.JSLDatabase;
import org.apache.commons.io.FileUtils;
import org.apache.derby.jdbc.ClientDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class DatabaseFactory {

    /** If the database already exists it is deleted. Creates the database in JSLDatabase.dbDir
     *
     * @param dbName the name of the database
     * @return the created database
     */
    public static Database createEmbeddedDerbyDatabase(String dbName){
        return createEmbeddedDerbyDatabase(dbName, JSLDatabase.dbDir);
    }

    /** If the database already exists it is deleted
     *
     * @param dbName the name of the embedded database
     * @param dbDir a path to the directory to hold the database
     * @return the created database
     */
    public static Database createEmbeddedDerbyDatabase(String dbName, Path dbDir){
        Objects.requireNonNull(dbName, "The database name was null");
        Path pathToDb = dbDir.resolve(dbName);
        deleteEmbeddedDerbyDatabase(pathToDb);
        DataSource ds = createEmbeddedDerbyDataSource(pathToDb, true);
        Database db = new Database(dbName, ds, SQLDialect.DERBY);
        return db;
    }

    /**
     *
     * @param pathToDb the path to the embedded database on disk
     */
    public static void deleteEmbeddedDerbyDatabase(Path pathToDb) {
        try {
            FileUtils.deleteDirectory(pathToDb.toFile());
            DatabaseIfc.DbLogger.info("Deleting directory to derby database {}", pathToDb);
        } catch (IOException e) {
            DatabaseIfc.DbLogger.error("Unable to delete directory to derby database {}", pathToDb);
            throw new DataAccessException("Unable to delete directory to derby database {}");
        }
    }
    /**
     *
     * @param pathToDb a path to the database, must not be null
     * @return the created DataSource
     */
    public static DataSource createEmbeddedDerbyDataSource(Path pathToDb){
        return createEmbeddedDerbyDataSource(pathToDb, null, null, false);
    }

    /**
     *
     * @param pathToDb a path to the database, must not be null
     * @param create a flag to indicate if the database should be created upon first connection
     * @return the created DataSource
     */
    public static DataSource createEmbeddedDerbyDataSource(Path pathToDb, boolean create){
        return createEmbeddedDerbyDataSource(pathToDb, null, null, create);
    }

    /**
     *
     * @param pathToDb a path to the database, must not be null
     * @param user a user name, can be null
     * @param pWord a password, can be null
     * @param create a flag to indicate if the database should be created upon first connection
     * @return the created DataSource
     */
    public static DataSource createEmbeddedDerbyDataSource(Path pathToDb, String user,
                                                           String pWord, boolean create){
        return createEmbeddedDerbyDataSource(pathToDb.toString(), user, pWord, create);
    }

    /** Assumes that the database exists
     *
     * @param dbName the path to the database, must not be null
     * @return the created DataSource
     */
    public static DataSource createEmbeddedDerbyDataSource(String dbName){
        return createEmbeddedDerbyDataSource(dbName, null, null, false);
    }

    /**
     *
     * @param dbName the path to the database, must not be null
     * @param create a flag to indicate if the database should be created upon first connection
     * @return the created DataSource
     */
    public static DataSource createEmbeddedDerbyDataSource(String dbName, boolean create){
        return createEmbeddedDerbyDataSource(dbName, null, null, create);
    }

    /**
     *
     * @param dbName the path to the database, must not be null
     * @param user a user name, can be null
     * @param pWord a password, can be null
     * @param create a flag to indicate if the database should be created upon first connection
     * @return the created DataSource
     */
    public static DataSource createEmbeddedDerbyDataSource(String dbName, String user, String pWord,
                                                           boolean create){
        Objects.requireNonNull(dbName, "The path name to the database must not be null");
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName(dbName);
        if (user != null)
            ds.setUser(user);
        if (pWord != null)
            ds.setPassword(pWord);
        if (create){
            ds.setCreateDatabase("create");
        }
        DatabaseIfc.DbLogger.info("Created an embedded Derby data source for {}", dbName);
        return ds;
    }

    /**
     *
     * @param dbName the path to the database, must not be null
     * @param user a user name, can be null
     * @param pWord a password, can be null
     * @param create a flag to indicate if the database should be created upon first connection
     * @return the created DataSource
     */
    public static DataSource createClientDerbyDataSourceWithLocalHost(String dbName, String user, String pWord,
                                                                      boolean create){
        Objects.requireNonNull(dbName, "The path name to the database must not be null");
        ClientDataSource ds = new ClientDataSource();
        ds.setDatabaseName(dbName);
        ds.setServerName("localhost");
        ds.setPortNumber(1527);
        if (user != null)
            ds.setUser(user);
        if (pWord != null)
            ds.setPassword(pWord);
        if (create){
            ds.setCreateDatabase("create");
        }
        DatabaseIfc.DbLogger.info("Created a Derby client data source for {}", dbName);
        return ds;
    }

    /** Duplicates the database into a new database with the supplied name and directory.
     *  Assumes that the source database has no active connections and performs a file system copy
     *
     * @param sourceDB the path to the database that needs duplicating
     * @param dupName the name of the duplicate database
     * @param directory the directory to place the database in
     * @throws IOException thrown if the system file copy commands fail
     */
    public static void copyEmbeddedDerbyDatabase(Path sourceDB, String dupName, Path directory) throws IOException {
        if (sourceDB == null) {
            throw new IllegalArgumentException("The path to the source must not be null!");
        }

        if (dupName == null) {
            throw new IllegalArgumentException("The duplicate's name must not be null!");
        }
        if (directory == null) {
            throw new IllegalArgumentException("The directory must not be null!");
        }
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("The directory path was not a directory!");
        }

        if (Files.exists(directory.resolve(dupName))) {
            throw new IllegalArgumentException("A database with the supplied name already exists in the directory! db name = " + dupName);
        }

        File target = directory.resolve(dupName).toFile();
        File source = sourceDB.toFile();
        FileUtils.copyDirectory(source, target);
    }

    /** Uses an active database connection and derby system commands to freeze the database,
     * uses system OS commands to copy the database, and then unfreezes the database.  The duplicate name
     * and directory path must not already exist
     * @param ds a DataSource to the embedded derby database, obviously it must point to the derby database
     * @param dupName the name of the duplicate database, obviouisly it must reference the same database that is
     *                referenced by the DataSource
     * @param directory the directory to place the database in
     * @throws SQLException thrown if the derby commands fail
     * @throws IOException thrown if the system file copy commands fail
     */
    public static final void copyEmbeddedDerbyDatabase(DataSource ds, Path sourceDB, String dupName, Path directory)
            throws SQLException, IOException {
        if (ds == null) {
            throw new IllegalArgumentException("The data source must not be null");
        }

        if (dupName == null) {
            throw new IllegalArgumentException("The duplicate's name must not be null!");
        }
        if (directory == null) {
            throw new IllegalArgumentException("The directory must not be null!");
        }
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("The directory path was not a directory!");
        }

        if (Files.exists(directory.resolve(dupName))) {
            throw new IllegalArgumentException("A database with the supplied name already exists in the directory! db name = " + dupName);
        }

        Statement s = ds.getConnection().createStatement();
        // freeze the database
        s.executeUpdate("CALL SYSCS_UTIL.SYSCS_FREEZE_DATABASE()");
        //copy the database directory during this interval
        // translate paths to files
        File target = directory.resolve(dupName).toFile();
        File source = sourceDB.toFile();
        FileUtils.copyDirectory(source, target);
        s.executeUpdate("CALL SYSCS_UTIL.SYSCS_UNFREEZE_DATABASE()");
        s.close();
    }
}
