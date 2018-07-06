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

import jsl.utilities.excel.ExcelUtil;
import org.jooq.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A concrete implementation of the DatabaseIfc interface.
 * <p>
 * Many databases define the terms database, user, schema in a variety of ways. This abstraction
 * defines this concept as the userSchema.  It is the name of the organizational construct for
 * which the user defined database objects are contained. These are not the system abstractions.
 * The database label provided to the construct is for labeling and may or may not have any relationship
 * to the actual file name or database name of the database. The supplied DataSource has all
 * the information that it needs to access the database.
 */
public class Database implements DatabaseIfc {

    static {
        System.setProperty("org.jooq.no-logo", "true");
    }

    private final String myLabel;
    private final DataSource myDataSource;
    private final SQLDialect mySQLDialect;
    private String myDefaultSchemaName;
    private DSLContext myDSLContext;

    /**
     * @param dbLabel    a string representing a label for the database must not be null. This label
     *                   may or may not have any relation to the actual name of the database. This is
     *                   used for labeling purposes.
     * @param dataSource the DataSource backing the database, must not be null
     * @param dialect    the SLQ dialect for this type of database, must not null, it obviously must
     *                   be consistent with the database referenced by the connection
     */
    public Database(String dbLabel, DataSource dataSource, SQLDialect dialect) {
        Objects.requireNonNull(dbLabel, "The database name was null");
        Objects.requireNonNull(dataSource, "The database source was null");
        Objects.requireNonNull(dialect, "The database dialect was null");
        myLabel = dbLabel;
        myDataSource = dataSource;
        mySQLDialect = dialect;
        myDSLContext = DSL.using(dataSource, dialect);
        setJooQDefaultExecutionLoggingOption(false);
    }

    @Override
    public final DataSource getDataSource() {
        return myDataSource;
    }

    @Override
    public final String getLabel() {
        return myLabel;
    }

    @Override
    public final SQLDialect getSQLDialect() {
        return mySQLDialect;
    }

    @Override
    public DSLContext getDSLContext() {
        return myDSLContext;
    }

    @Override
    public String getDefaultSchemaName() {
        return myDefaultSchemaName;
    }

    @Override
    public void setDefaultSchemaName(String defaultSchemaName) {
        myDefaultSchemaName = defaultSchemaName;
        if (defaultSchemaName != null) {
            if (!containsSchema(defaultSchemaName)) {
                DbLogger.warn("The supplied default schema name {} was not in the database {}.",
                        defaultSchemaName, myLabel);
            }
        } else {
            DbLogger.warn("The default schema name was set to null for database {}.", myLabel);
        }
    }

    public DbCreateTask.DbCreateTaskFirstStepIfc create(){
        return new DbCreateTask.DbCreateTaskBuilder(this);
    }

}
