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

package jsl.utilities.dbutil;

import org.jooq.*;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.util.Objects;

/**
 *  A concrete implementation of the DatabaseIfc interface.
 *
 * Many databases define the terms database, user, schema in a variety of ways. This abstraction
 * defines this concept as the userSchema.  It is the name of the organizational construct for
 * which the user defined database objects are contained. These are not the system abstractions.
 * The database name provided to the construct is for labeling and may or may not have any relationship
 * to the actual file name or database name of the database. The supplied connection has all
 * the information that it needs to access the database.
 */
public class Database implements DatabaseIfc {

    static {
        System.setProperty("org.jooq.no-logo", "true");
    }

    private final String myName;
    private final Schema myUserSchema;
    private final DataSource myDataSource;
    private final SQLDialect mySQLDialect;
    private DSLContext myDSLContext;

    /**
     * @param dbName         a string representing the name of the database must not be null. This name
     *                       may or may not have any relation to the actual name of the database. This is
     *                       used for labeling purposes.
     * @param userSchemaName a string representing the name of the user or schema that holds
     *                       the user defined tables with the database. Must not be null
     * @param dataSource     the DataSource backing the database, must not be null
     * @param dialect        the SLQ dialect for this type of database, must not null, it obviously must
     *                       be consistent with the database referenced by the connection
     */
    public Database(String dbName, String userSchemaName, DataSource dataSource, SQLDialect dialect) {
        Objects.requireNonNull(dbName, "The database name was null");
        Objects.requireNonNull(userSchemaName, "The database user/schema was null");
        Objects.requireNonNull(dataSource, "The database source was null");
        Objects.requireNonNull(dialect, "The database dialect was null");
        myName = dbName;
        myDataSource = dataSource;
        mySQLDialect = dialect;
        myUserSchema = getSchema(userSchemaName);
        if (myUserSchema == null) {
            DbLogger.error("The supplied userSchema name {} was not in the database.", userSchemaName);
            throw new DataAccessException("The supplied userSchema name was not in the database: " + userSchemaName);
        }
        myDSLContext = DSL.using(getDataSource(), getSQLDialect());
        setJooQDefaultExecutionLoggingOption(false);
    }

    @Override
    public final DataSource getDataSource(){
        return myDataSource;
    }

    @Override
    public final String getName() {
        return myName;
    }


    @Override
    public final SQLDialect getSQLDialect() {
        return mySQLDialect;
    }

    @Override
    public Schema getUserSchema() {
        return myUserSchema;
    }

    @Override
    public DSLContext getDSLContext() {
        return myDSLContext;
    }

}
