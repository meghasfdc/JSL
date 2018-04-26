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

/*
 * This file is generated by jOOQ.
*/
package jsl.utilities.jsldbsrc.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import jsl.utilities.jsldbsrc.App;
import jsl.utilities.jsldbsrc.Keys;
import jsl.utilities.jsldbsrc.tables.records.WithinRepStatRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class WithinRepStat extends TableImpl<WithinRepStatRecord> {

    private static final long serialVersionUID = -1303329609;

    /**
     * The reference instance of <code>APP.WITHIN_REP_STAT</code>
     */
    public static final WithinRepStat WITHIN_REP_STAT = new WithinRepStat();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WithinRepStatRecord> getRecordType() {
        return WithinRepStatRecord.class;
    }

    /**
     * The column <code>APP.WITHIN_REP_STAT.ID</code>.
     */
    public final TableField<WithinRepStatRecord, Integer> ID = createField("ID", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>APP.WITHIN_REP_STAT.MODEL_ELEMENT_NAME</code>.
     */
    public final TableField<WithinRepStatRecord, String> MODEL_ELEMENT_NAME = createField("MODEL_ELEMENT_NAME", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>APP.WITHIN_REP_STAT.SIM_RUN_ID_FK</code>.
     */
    public final TableField<WithinRepStatRecord, Integer> SIM_RUN_ID_FK = createField("SIM_RUN_ID_FK", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>APP.WITHIN_REP_STAT.STAT_NAME</code>.
     */
    public final TableField<WithinRepStatRecord, String> STAT_NAME = createField("STAT_NAME", org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>APP.WITHIN_REP_STAT.REP_NUM</code>.
     */
    public final TableField<WithinRepStatRecord, Integer> REP_NUM = createField("REP_NUM", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>APP.WITHIN_REP_STAT.STAT_COUNT</code>.
     */
    public final TableField<WithinRepStatRecord, Double> STAT_COUNT = createField("STAT_COUNT", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>APP.WITHIN_REP_STAT.AVERAGE</code>.
     */
    public final TableField<WithinRepStatRecord, Double> AVERAGE = createField("AVERAGE", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>APP.WITHIN_REP_STAT.MINIMUM</code>.
     */
    public final TableField<WithinRepStatRecord, Double> MINIMUM = createField("MINIMUM", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>APP.WITHIN_REP_STAT.MAXIMUM</code>.
     */
    public final TableField<WithinRepStatRecord, Double> MAXIMUM = createField("MAXIMUM", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>APP.WITHIN_REP_STAT.WEIGHTED_SUM</code>.
     */
    public final TableField<WithinRepStatRecord, Double> WEIGHTED_SUM = createField("WEIGHTED_SUM", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>APP.WITHIN_REP_STAT.SUM_OF_WEIGHTS</code>.
     */
    public final TableField<WithinRepStatRecord, Double> SUM_OF_WEIGHTS = createField("SUM_OF_WEIGHTS", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>APP.WITHIN_REP_STAT.WEIGHTED_SSQ</code>.
     */
    public final TableField<WithinRepStatRecord, Double> WEIGHTED_SSQ = createField("WEIGHTED_SSQ", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>APP.WITHIN_REP_STAT.LAST_VALUE</code>.
     */
    public final TableField<WithinRepStatRecord, Double> LAST_VALUE = createField("LAST_VALUE", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>APP.WITHIN_REP_STAT.LAST_WEIGHT</code>.
     */
    public final TableField<WithinRepStatRecord, Double> LAST_WEIGHT = createField("LAST_WEIGHT", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * Create a <code>APP.WITHIN_REP_STAT</code> table reference
     */
    public WithinRepStat() {
        this(DSL.name("WITHIN_REP_STAT"), null);
    }

    /**
     * Create an aliased <code>APP.WITHIN_REP_STAT</code> table reference
     */
    public WithinRepStat(String alias) {
        this(DSL.name(alias), WITHIN_REP_STAT);
    }

    /**
     * Create an aliased <code>APP.WITHIN_REP_STAT</code> table reference
     */
    public WithinRepStat(Name alias) {
        this(alias, WITHIN_REP_STAT);
    }

    private WithinRepStat(Name alias, Table<WithinRepStatRecord> aliased) {
        this(alias, aliased, null);
    }

    private WithinRepStat(Name alias, Table<WithinRepStatRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return App.APP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<WithinRepStatRecord, Integer> getIdentity() {
        return Keys.IDENTITY_WITHIN_REP_STAT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<WithinRepStatRecord> getPrimaryKey() {
        return Keys.SQL180330135926250;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<WithinRepStatRecord>> getKeys() {
        return Arrays.<UniqueKey<WithinRepStatRecord>>asList(Keys.SQL180330135926250, Keys.WRS_UNIQUE_ELEMENT_SIMRUN_REPNUM);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<WithinRepStatRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<WithinRepStatRecord, ?>>asList(Keys.WRS_MODEL_ELEMENT_FK, Keys.WRS_SIMRUN_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WithinRepStat as(String alias) {
        return new WithinRepStat(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WithinRepStat as(Name alias) {
        return new WithinRepStat(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public WithinRepStat rename(String name) {
        return new WithinRepStat(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public WithinRepStat rename(Name name) {
        return new WithinRepStat(name, null);
    }
}