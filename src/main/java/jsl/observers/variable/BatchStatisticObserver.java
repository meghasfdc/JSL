/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.observers.variable;

import jsl.modeling.ModelElement;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.observers.ModelElementObserver;
import jsl.utilities.statistic.BatchStatistic;
import jsl.utilities.statistic.Statistic;

/** A observer for batching of statistics on ResponseVariables
 *  The user can control the collection rule and the batching criteria
 *  of the underlying BatchStatistic
 *
 * @author rossetti
 */
public class BatchStatisticObserver extends ModelElementObserver {

    /**
     * The default rule is no rule. Collect all data presented.
     *
     */
    protected Statistic.CollectionRule myCollectionRule = Statistic.CollectionRule.NONE;

    /**
     * The underlying BatchStatistic
     */
    protected BatchStatistic myBatchStats;
    
    /**
     *  false means no warm up event in parent hierarchy
     */
    protected boolean myWarmUpEventCheckFlag = false;

    public BatchStatisticObserver(String name) {
        this(BatchStatistic.MIN_NUM_BATCHES, BatchStatistic.MIN_NUM_OBS_PER_BATCH,
                BatchStatistic.MAX_BATCH_MULTIPLE, name);
    }

    public BatchStatisticObserver(int minNumBatches, int minBatchSize,
            int maxNBMultiple) {
        this(minNumBatches, minBatchSize, maxNBMultiple, null);
    }

    public BatchStatisticObserver(int minNumBatches, int minBatchSize,
            int maxNBMultiple, String name) {
        super(name);
        myBatchStats = new BatchStatistic(minNumBatches, minBatchSize,
                maxNBMultiple, name);
    }

    /**
     * Sets the collection rule
     *
     * @param rule must be Statistic.CollectionRule
     */
    public final void setCollectionRule(Statistic.CollectionRule rule) {
        myCollectionRule = rule;
    }

    /**
     * Returns the current collection rule
     *
     * @return the current collection rule
     */
    public final Statistic.CollectionRule getCollectionRule() {
        return myCollectionRule;
    }

    /** Sets confidence level on underlying BatchStatistic
     *
     * @param alpha the confidence level between 0 and 1
     */
    public final void setConfidenceLevel(double alpha) {
        myBatchStats.setConfidenceLevel(alpha);
    }

    /** The desired half-width if a collection rule is set
     *
     * @param desiredHalfWidth must be greater than 0
     */
    public final void setDesiredHalfWidth(double desiredHalfWidth) {
        myBatchStats.setDesiredHalfWidth(desiredHalfWidth);
    }

    /** The desired relative precision if a collection rule is set
     * 
     * @param desiredRelativePrecision must be non-negative
     */
    public final void setRelativePrecision(double desiredRelativePrecision) {
        myBatchStats.setRelativePrecision(desiredRelativePrecision);
    }
    
    /**
     * The collected BatchStatistic
     *
     * @return the collected BatchStatistic
     */
    public final BatchStatistic getBatchStatistics() {
        return myBatchStats;
    }

    @Override
    protected void beforeExperiment(ModelElement m, Object arg){
        myBatchStats.reset();
        myBatchStats.setCollectionRule(Statistic.CollectionRule.NONE);
        myWarmUpEventCheckFlag = false;
    }
    
    @Override
    protected void beforeReplication(ModelElement m, Object arg) {
        myBatchStats.reset();
        ResponseVariable r = (ResponseVariable) m;
        ModelElement mElement = r.findModelElementWithWarmUpEvent();
        if (mElement == null){
            myWarmUpEventCheckFlag = false;
            // no warm up event, set BatchStatistic to desired checking
            myBatchStats.setCollectionRule(getCollectionRule());
        } else {
            myWarmUpEventCheckFlag = true;
        }
    }

    @Override
    protected void update(ModelElement m, Object arg) {
        ResponseVariable r = (ResponseVariable) m;
        boolean collect = myBatchStats.collect(r);
        if (collect == false){
            // stop the simulation
            StringBuilder msg = new StringBuilder();
            msg.append(r.getName()).append(" stopped replication. ");
            msg.append(myBatchStats.getCollectionRule());
            msg.append(" criteria met.\n");
            r.stopExecutive(msg.toString());
        }
    }

    @Override
    protected void warmUp(ModelElement m, Object arg) {
        myBatchStats.reset();
        if (myWarmUpEventCheckFlag == true) {
          //there was a warm up event and this element was warmed up
          // now turn on any collector rule
          myBatchStats.setCollectionRule(getCollectionRule());  
        }
    }

    @Override
    public String toString() {
        return myBatchStats.toString();
    }

}
