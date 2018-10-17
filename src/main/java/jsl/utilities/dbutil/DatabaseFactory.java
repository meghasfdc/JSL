package jsl.utilities.dbutil;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Properties;

public class DatabaseFactory {

    /**
     * If the database already exists it is deleted. Creates the database in JSLDatabase.dbDir
     *
     * @param dbName the name of the database
     * @return the created database
     */
    public static DatabaseIfc createEmbeddedDerbyDatabase(String dbName) {
        return createEmbeddedDerbyDatabase(dbName, JSLDatabase.dbDir);
    }

    /**
     * If the database already exists it is deleted
     *
     * @param dbName the name of the embedded database. Must not be null
     * @param dbDir  a path to the directory to hold the database. Must not be null
     * @return the created database
     */
    public static DatabaseIfc createEmbeddedDerbyDatabase(String dbName, Path dbDir) {
        Objects.requireNonNull(dbName, "The database name was null");
        Objects.requireNonNull(dbDir, "The path to the database must not be null");
        Path pathToDb = dbDir.resolve(dbName);
        deleteEmbeddedDerbyDatabase(pathToDb);
        DataSource ds = createEmbeddedDerbyDataSource(pathToDb, true);
        Database db = new Database(dbName, ds, SQLDialect.DERBY);
        return db;
    }

    /**
     *
     * @param dbLabel a label for the database
     * @param dataSource the data source for connections
     * @return the created database
     */
    public static DatabaseIfc createEmbeddedDerbyDatabase(String dbLabel, DataSource dataSource){
        Objects.requireNonNull(dataSource, "The data source was null");
        Database db = new Database(dbLabel, dataSource, SQLDialect.DERBY);
        return db;
    }

    /**
     * The database must already exist. It is not created. An exception is thrown if it does not exist.
     * Assumes that the named database is  in JSLDatabase.dbDir
     *
     * @param dbName the name of the embedded database, must not be null
     * @return the created database
     */
    public static DatabaseIfc getEmbeddedDerbyDatabase(String dbName) {
        return getEmbeddedDerbyDatabase(dbName, JSLDatabase.dbDir);
    }

    /**
     * The database must already exist. It is not created. An exception is thrown if it does not exist.
     *
     * @param dbName the name of the embedded database, must not be null
     * @param dbDir  a path to the directory that holds the database, must not be null
     * @return the created database
     */
    public static DatabaseIfc getEmbeddedDerbyDatabase(String dbName, Path dbDir) {
        Objects.requireNonNull(dbName, "The database name was null");
        Objects.requireNonNull(dbDir, "The path to the database must not be null");
        Path pathToDb = dbDir.resolve(dbName);
        if (!isEmbeddedDerbyDatabaseExists(dbName, dbDir)) {
            throw new IllegalStateException("The database does not exist at location " + pathToDb);
        }
        DataSource ds = createEmbeddedDerbyDataSource(pathToDb, false);
        Database db = new Database(dbName, ds, SQLDialect.DERBY);
        return db;
    }

    /**
     * Checks if the database exists in in JSLDatabase.dbDir
     *
     * @param dbName the name of the database
     * @return true if it exists false if not
     */
    public static boolean isEmbeddedDerbyDatabaseExists(String dbName) {
        return isEmbeddedDerbyDatabaseExists(dbName, JSLDatabase.dbDir);
    }

    /**
     * @param dbName the name of the database
     * @param dbDir  the directory to the database
     * @return true if it exists false if not
     */
    public static boolean isEmbeddedDerbyDatabaseExists(String dbName, Path dbDir) {
        if ((dbDir == null) || (dbName == null)) {
            return false;
        }
        Path pathToDb = dbDir.resolve(dbName);
        return Files.exists(pathToDb);
    }

    /**
     * @param fullPath the full path to the database including its name (because derby
     *                 stores the database in a directory
     * @return true if it exists
     */
    public static boolean isEmbeddedDerbyDatabaseExists(Path fullPath) {
        if (fullPath == null) {
            return false;
        }
        return Files.exists(fullPath);
    }

    /**
     * @param pathToDb the path to the embedded database on disk
     */
    public static void deleteEmbeddedDerbyDatabase(Path pathToDb) {
        try {
            FileUtils.deleteDirectory(pathToDb.toFile());
            DatabaseIfc.LOG.info("Deleting directory to derby database {}", pathToDb);
        } catch (IOException e) {
            DatabaseIfc.LOG.error("Unable to delete directory to derby database {}", pathToDb);
            throw new DataAccessException("Unable to delete directory to derby database {}");
        }
    }

    /**
     * The database must already exist. It will not be created
     *
     * @param pathToDb a path to the database, must not be null
     * @return the created DataSource
     */
    public static DataSource createEmbeddedDerbyDataSource(Path pathToDb) {
        return createEmbeddedDerbyDataSource(pathToDb, null, null, false);
    }

    /**
     * @param pathToDb a path to the database, must not be null
     * @param create   a flag to indicate if the database should be created upon first connection
     * @return the created DataSource
     */
    public static DataSource createEmbeddedDerbyDataSource(Path pathToDb, boolean create) {
        return createEmbeddedDerbyDataSource(pathToDb, null, null, create);
    }

    /**
     * @param pathToDb a path to the database, must not be null
     * @param user     a user name, can be null
     * @param pWord    a password, can be null
     * @param create   a flag to indicate if the database should be created upon first connection
     * @return the created DataSource
     */
    public static DataSource createEmbeddedDerbyDataSource(Path pathToDb, String user,
                                                           String pWord, boolean create) {
        return createEmbeddedDerbyDataSource(pathToDb.toString(), user, pWord, create);
    }

    /**
     * Assumes that the database exists
     *
     * @param dbName the path to the database, must not be null
     * @return the created DataSource
     */
    public static DataSource createEmbeddedDerbyDataSource(String dbName) {
        return createEmbeddedDerbyDataSource(dbName, null, null, false);
    }

    /**
     * @param dbName the path to the database, must not be null
     * @param create a flag to indicate if the database should be created upon first connection
     * @return the created DataSource
     */
    public static DataSource createEmbeddedDerbyDataSource(String dbName, boolean create) {
        return createEmbeddedDerbyDataSource(dbName, null, null, create);
    }

    /**
     * @param dbName the path to the database, must not be null
     * @param user   a user name, can be null
     * @param pWord  a password, can be null
     * @param create a flag to indicate if the database should be created upon first connection
     * @return the created DataSource
     */
    public static DataSource createEmbeddedDerbyDataSource(String dbName, String user, String pWord,
                                                           boolean create) {
        Objects.requireNonNull(dbName, "The path name to the database must not be null");
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName(dbName);
        if (user != null)
            ds.setUser(user);
        if (pWord != null)
            ds.setPassword(pWord);
        if (create) {
            Path path = Paths.get(dbName);
            DatabaseIfc.LOG.info("Create option is on for {}", dbName);
            if (isEmbeddedDerbyDatabaseExists(path)){
                DatabaseIfc.LOG.info("Database already exists at location {}", dbName);
                deleteEmbeddedDerbyDatabase(path);
            }
            ds.setCreateDatabase("create");
        }
        DatabaseIfc.LOG.info("Created an embedded Derby data source for {}", dbName);
        return ds;
    }

    /**
     * @param dbName the path to the database, must not be null
     * @param user   a user name, can be null
     * @param pWord  a password, can be null
     * @param create a flag to indicate if the database should be created upon first connection
     * @return the created DataSource
     */
    public static DataSource createClientDerbyDataSourceWithLocalHost(String dbName, String user, String pWord,
                                                                      boolean create) {
        Objects.requireNonNull(dbName, "The path name to the database must not be null");
        ClientDataSource ds = new ClientDataSource();
        ds.setDatabaseName(dbName);
        ds.setServerName("localhost");
        ds.setPortNumber(1527);
        if (user != null)
            ds.setUser(user);
        if (pWord != null)
            ds.setPassword(pWord);
        if (create) {
            ds.setCreateDatabase("create");
        }
        DatabaseIfc.LOG.info("Created a Derby client data source for {}", dbName);
        return ds;
    }

    /**
     *
     * @param dbName the name of the database, must not be null
     * @param user the user
     * @param pWord the password
     * @return the DataSource for getting connections
     */
    public static DataSource getPostGresDataSourceWithLocalHost(String dbName, String user, String pWord) {
        return getPostGresDataSource("localhost", dbName, user, pWord);
    }

    /** Assumes standard PostGres port
     *
     * @param dbServerName the name of the database server, must not be null
     * @param dbName the name of the database, must not be null
     * @param user the user
     * @param pWord the password
     * @return the DataSource for getting connections
     */
    public static DataSource getPostGresDataSource(String dbServerName, String dbName, String user,
                                                                 String pWord){
        Objects.requireNonNull(dbServerName, "The name to the database server must not be null");
        Objects.requireNonNull(dbName, "The path name to the database must not be null");
        Properties props = new Properties();
        props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        props.setProperty("dataSource.user", user);
        props.setProperty("dataSource.password", pWord);
        props.setProperty("dataSource.databaseName", dbName);
        props.setProperty("dataSource.serverName", dbServerName);
        return getDataSource(props);
    }

    /** Assumes that the properties are appropriately configured to create a DataSource
     *  via  HikariCP
     *
     * @param properties the properties
     * @return a pooled connection DataSource
     */
    public static DataSource getDataSource(Properties properties){
        Objects.requireNonNull(properties, "The properties must not be null");
        HikariConfig config = new HikariConfig(properties);
        HikariDataSource ds = new HikariDataSource(config);
        return ds;
    }

    /**
     *
     * @param pathToPropertiesFile must not be null
     * @return a DataSource for making a database
     */
    public static DataSource getDataSource(Path pathToPropertiesFile){
        Objects.requireNonNull(pathToPropertiesFile, "The properties file path must not be null");
        HikariConfig config = new HikariConfig(pathToPropertiesFile.toString());
        HikariDataSource ds = new HikariDataSource(config);
        return ds;
    }

    /**
     * Duplicates the database into a new database with the supplied name and directory.
     * Assumes that the source database has no active connections and performs a file system copy
     *
     * @param sourceDB  the path to the database that needs duplicating
     * @param dupName   the name of the duplicate database
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

    /**
     * Uses an active database connection and derby system commands to freeze the database,
     * uses system OS commands to copy the database, and then unfreezes the database.  The duplicate name
     * and directory path must not already exist
     *
     * @param ds        a DataSource to the embedded derby database, obviously it must point to the derby database
     * @param dupName   the name of the duplicate database, obviouisly it must reference the same database that is
     *                  referenced by the DataSource
     * @param directory the directory to place the database in
     * @throws SQLException thrown if the derby commands fail
     * @throws IOException  thrown if the system file copy commands fail
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
