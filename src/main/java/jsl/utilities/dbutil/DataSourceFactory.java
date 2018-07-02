package jsl.utilities.dbutil;

import org.apache.derby.jdbc.ClientDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.util.Objects;

public class DataSourceFactory {

    /**
     *
     * @param pathToDb a path to the database, must not be null
     * @return the created DataSource
     */
    public static DataSource getEmbeddedDerbyDataSource(Path pathToDb){
        return getEmbeddedDerbyDataSource(pathToDb, null, null, false);
    }

    /**
     *
     * @param pathToDb a path to the database, must not be null
     * @param create a flag to indicate if the database should be created upon first connection
     * @return the created DataSource
     */
    public static DataSource getEmbeddedDerbyDataSource(Path pathToDb, boolean create){
        return getEmbeddedDerbyDataSource(pathToDb, null, null, create);
    }

    /**
     *
     * @param pathToDb a path to the database, must not be null
     * @param user a user name, can be null
     * @param pWord a password, can be null
     * @param create a flag to indicate if the database should be created upon first connection
     * @return the created DataSource
     */
    public static DataSource getEmbeddedDerbyDataSource(Path pathToDb, String user,
                                                        String pWord, boolean create){
        return getEmbeddedDerbyDataSource(pathToDb.toString(), user, pWord, create);
    }

    /** Assumes that the database exists
     *
     * @param dbName the path to the database, must not be null
     * @return the created DataSource
     */
    public static DataSource getEmbeddedDerbyDataSource(String dbName){
        return getEmbeddedDerbyDataSource(dbName, null, null, false);
    }

    /**
     *
     * @param dbName the path to the database, must not be null
     * @param create a flag to indicate if the database should be created upon first connection
     * @return the created DataSource
     */
    public static DataSource getEmbeddedDerbyDataSource(String dbName, boolean create){
        return getEmbeddedDerbyDataSource(dbName, null, null, create);
    }

    /**
     *
     * @param dbName the path to the database, must not be null
     * @param user a user name, can be null
     * @param pWord a password, can be null
     * @param create a flag to indicate if the database should be created upon first connection
     * @return the created DataSource
     */
    public static DataSource getEmbeddedDerbyDataSource(String dbName, String user, String pWord,
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
    public static DataSource getClientDerbyDataSourceWithLocalHost(String dbName, String user, String pWord,
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
}
