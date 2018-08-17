/*
 * This file is generated by jOOQ.
*/
package jsl.utilities.jsldbsrc.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import jsl.utilities.jsldbsrc.JslDb;
import jsl.utilities.jsldbsrc.Keys;
import jsl.utilities.jsldbsrc.tables.records.BatchStatRecord;

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
public class BatchStat extends TableImpl<BatchStatRecord> {

    private static final long serialVersionUID = -1692867710;

    /**
     * The reference instance of <code>JSL_DB.BATCH_STAT</code>
     */
    public static final BatchStat BATCH_STAT = new BatchStat();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BatchStatRecord> getRecordType() {
        return BatchStatRecord.class;
    }

    /**
     * The column <code>JSL_DB.BATCH_STAT.ID</code>.
     */
    public final TableField<BatchStatRecord, Integer> ID = createField("ID", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.ELEMENT_ID_FK</code>.
     */
    public final TableField<BatchStatRecord, Integer> ELEMENT_ID_FK = createField("ELEMENT_ID_FK", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.SIM_RUN_ID_FK</code>.
     */
    public final TableField<BatchStatRecord, Integer> SIM_RUN_ID_FK = createField("SIM_RUN_ID_FK", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.REP_NUM</code>.
     */
    public final TableField<BatchStatRecord, Integer> REP_NUM = createField("REP_NUM", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.STAT_NAME</code>.
     */
    public final TableField<BatchStatRecord, String> STAT_NAME = createField("STAT_NAME", org.jooq.impl.SQLDataType.VARCHAR(510), this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.STAT_COUNT</code>.
     */
    public final TableField<BatchStatRecord, Double> STAT_COUNT = createField("STAT_COUNT", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.AVERAGE</code>.
     */
    public final TableField<BatchStatRecord, Double> AVERAGE = createField("AVERAGE", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.STD_DEV</code>.
     */
    public final TableField<BatchStatRecord, Double> STD_DEV = createField("STD_DEV", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.STD_ERR</code>.
     */
    public final TableField<BatchStatRecord, Double> STD_ERR = createField("STD_ERR", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.HALF_WIDTH</code>.
     */
    public final TableField<BatchStatRecord, Double> HALF_WIDTH = createField("HALF_WIDTH", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.CONF_LEVEL</code>.
     */
    public final TableField<BatchStatRecord, Double> CONF_LEVEL = createField("CONF_LEVEL", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.MINIMUM</code>.
     */
    public final TableField<BatchStatRecord, Double> MINIMUM = createField("MINIMUM", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.MAXIMUM</code>.
     */
    public final TableField<BatchStatRecord, Double> MAXIMUM = createField("MAXIMUM", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.WEIGHTED_SUM</code>.
     */
    public final TableField<BatchStatRecord, Double> WEIGHTED_SUM = createField("WEIGHTED_SUM", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.SUM_OF_WEIGHTS</code>.
     */
    public final TableField<BatchStatRecord, Double> SUM_OF_WEIGHTS = createField("SUM_OF_WEIGHTS", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.WEIGHTED_SSQ</code>.
     */
    public final TableField<BatchStatRecord, Double> WEIGHTED_SSQ = createField("WEIGHTED_SSQ", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.DEV_SSQ</code>.
     */
    public final TableField<BatchStatRecord, Double> DEV_SSQ = createField("DEV_SSQ", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.LAST_VALUE</code>.
     */
    public final TableField<BatchStatRecord, Double> LAST_VALUE = createField("LAST_VALUE", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.LAST_WEIGHT</code>.
     */
    public final TableField<BatchStatRecord, Double> LAST_WEIGHT = createField("LAST_WEIGHT", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.KURTOSIS</code>.
     */
    public final TableField<BatchStatRecord, Double> KURTOSIS = createField("KURTOSIS", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.SKEWNESS</code>.
     */
    public final TableField<BatchStatRecord, Double> SKEWNESS = createField("SKEWNESS", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.LAG1_COV</code>.
     */
    public final TableField<BatchStatRecord, Double> LAG1_COV = createField("LAG1_COV", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.LAG1_CORR</code>.
     */
    public final TableField<BatchStatRecord, Double> LAG1_CORR = createField("LAG1_CORR", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.VON_NEUMAN_LAG1_STAT</code>.
     */
    public final TableField<BatchStatRecord, Double> VON_NEUMAN_LAG1_STAT = createField("VON_NEUMAN_LAG1_STAT", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.NUM_MISSING_OBS</code>.
     */
    public final TableField<BatchStatRecord, Double> NUM_MISSING_OBS = createField("NUM_MISSING_OBS", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.MIN_BATCH_SIZE</code>.
     */
    public final TableField<BatchStatRecord, Double> MIN_BATCH_SIZE = createField("MIN_BATCH_SIZE", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.MIN_NUM_BATCHES</code>.
     */
    public final TableField<BatchStatRecord, Double> MIN_NUM_BATCHES = createField("MIN_NUM_BATCHES", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.MAX_NUM_BATCHES_MULTIPLE</code>.
     */
    public final TableField<BatchStatRecord, Double> MAX_NUM_BATCHES_MULTIPLE = createField("MAX_NUM_BATCHES_MULTIPLE", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.MAX_NUM_BATCHES</code>.
     */
    public final TableField<BatchStatRecord, Double> MAX_NUM_BATCHES = createField("MAX_NUM_BATCHES", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.NUM_REBATCHES</code>.
     */
    public final TableField<BatchStatRecord, Double> NUM_REBATCHES = createField("NUM_REBATCHES", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.CURRENT_BATCH_SIZE</code>.
     */
    public final TableField<BatchStatRecord, Double> CURRENT_BATCH_SIZE = createField("CURRENT_BATCH_SIZE", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.AMT_UNBATCHED</code>.
     */
    public final TableField<BatchStatRecord, Double> AMT_UNBATCHED = createField("AMT_UNBATCHED", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>JSL_DB.BATCH_STAT.TOTAL_NUM_OBS</code>.
     */
    public final TableField<BatchStatRecord, Double> TOTAL_NUM_OBS = createField("TOTAL_NUM_OBS", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * Create a <code>JSL_DB.BATCH_STAT</code> table reference
     */
    public BatchStat() {
        this(DSL.name("BATCH_STAT"), null);
    }

    /**
     * Create an aliased <code>JSL_DB.BATCH_STAT</code> table reference
     */
    public BatchStat(String alias) {
        this(DSL.name(alias), BATCH_STAT);
    }

    /**
     * Create an aliased <code>JSL_DB.BATCH_STAT</code> table reference
     */
    public BatchStat(Name alias) {
        this(alias, BATCH_STAT);
    }

    private BatchStat(Name alias, Table<BatchStatRecord> aliased) {
        this(alias, aliased, null);
    }

    private BatchStat(Name alias, Table<BatchStatRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return JslDb.JSL_DB;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<BatchStatRecord, Integer> getIdentity() {
        return Keys.IDENTITY_BATCH_STAT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<BatchStatRecord> getPrimaryKey() {
        return Keys.SQL180815113710430;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<BatchStatRecord>> getKeys() {
        return Arrays.<UniqueKey<BatchStatRecord>>asList(Keys.SQL180815113710430);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<BatchStatRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BatchStatRecord, ?>>asList(Keys.BS_MODEL_ELEMENT_FK, Keys.BS_SIMRUN_FK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BatchStat as(String alias) {
        return new BatchStat(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BatchStat as(Name alias) {
        return new BatchStat(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public BatchStat rename(String name) {
        return new BatchStat(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public BatchStat rename(Name name) {
        return new BatchStat(name, null);
    }
}
