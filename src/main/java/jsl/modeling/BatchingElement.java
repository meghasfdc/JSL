/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * Copyright (c) Manuel D. Rossetti (rossetti@uark.edu)
 *
 * Contact:
 *	Manuel D. Rossetti, Ph.D., P.E.
 *	Department of Industrial Engineering
 *	University of Arkansas
 *	4207 Bell Engineering Center
 *	Fayetteville, AR 72701
 *	Phone: (479) 575-6756
 *	Email: rossetti@uark.edu
 *	Web: www.uark.edu/~rossetti
 *
 * This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 * of Java classes that permit the easy development and execution of discrete event
 * simulation programs.
 *
 * The JSL is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The JSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the JSL (see file COPYING in the distribution);
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA, or see www.fsf.org
 *
 */
package jsl.modeling;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.observers.ModelElementObserver;
import jsl.observers.ObserverIfc;
import jsl.utilities.reporting.JSL;
import jsl.utilities.statistic.BatchStatistic;
import jsl.utilities.statistic.WeightedStatistic;

/**
 * This class controls the batching of time weighted variables in the model and
 * ensures batch collection for response variables.
 *
 * The batch interval is used to schedule events during a replication and must
 * be the same throughout the replication. If the supplied interval is 0.0, then
 * the method getApproximateBatchInterval() will be used to determine the
 * interval for the replication.
 *
 * Observation based (tally) variables (ResponseVariables) are directly observed
 * and batched using a BatchStatistic.
 *
 * Time-based variables (TimeWeighted) are first discretized based on a batching
 * interval. The default batching interval is based on the value of the initial
 * number of batches. This is by default set to DEFAULT_NUM_TW_BATCHES = 512.
 * These initial batches are then rebatched according to the procedures within
 * BatchStatistic.
 *
 * Usage:
 *
 * // Create a simulation Simulation s = new Simulation();
 *
 * // Create a BatchingElement
 *
 * BatchingElement be = s.getBatchingElement();
 *
 * // Set the characteristics of the batching or accept the defaults //
 * setBatchInterval(), setBatchingParameters()
 *
 * // Set the running parameters of the replication
 *
 * s.setLengthOfReplication(50.0); s.setLengthOfWarmUp(10.0);
 *
 * // Tell the simulation to run
 *
 * s.run();
 *
 * // Output some results, using toString()
 *
 *
 * System.out.println(be);
 *
 * // Or asks for csv output file using //
 * be.writeCSVBatchStatistics(PrintWriter out)
 *
 * @author rossetti
 * @deprecated This class has been deprecated by the new StatisticalBatchingElement class
 */
public class BatchingElement extends SchedulingElement {

    /**
     * A constant for the default batch interval for a replication If there is
     * no run length specified and the user turns on default batching, then the
     * time interval between batches will be equal to this value. The default
     * value is 10.0
     */
    public static final double DEFAULT_BATCH_INTERVAL = 10.0;

    /**
     * A constant for the default number of batches for TimeWeighted variables.
     * This value is used in the calculation of the approximate batching
     * interval if batching is turned on and there is a finite run length.
     *
     * If the run length is finite, then the batch interval is approximated as
     * follows:
     *
     * t = length of replication - length of warm up n =
     * getTimeWeightedStartingNumberOfBatches()
     *
     * batching interval = t/n
     *
     * DEFAULT_NUM_TW_BATCHES = 512.0
     *
     */
    public static final double DEFAULT_NUM_TW_BATCHES = 512.0;

    /**
     * A reference to the Batching event.
     */
    private JSLEvent myBatchEvent;

    /**
     * The priority for the batching events.
     */
    private int myBatchEventPriority = JSLEvent.DEFAULT_BATCH_PRIORITY;

    /**
     * The time interval between batching events.
     */
    private double myTimeBtwBatches = 0.0;

    /**
     * A time interval (in simulated time) that represents the default time
     * between batches The default is zero for no batching
     */
    private double myBatchInterval = 0;

    /**
     * The starting number of batches for time weighted batching. Used in
     * approximating a batch interval size
     */
    private double myNumTWBatches = DEFAULT_NUM_TW_BATCHES;

    /**
     * Minimum batch size used when batching
     */
    private int myMinBatchSize = BatchStatistic.MIN_NUM_OBS_PER_BATCH;

    /**
     * Minimum number of batches used when batching
     */
    private int myMinNumBatches = BatchStatistic.MIN_NUM_BATCHES;

    /**
     * Maximum number of batches used when batching
     */
    private int myMaxNumBatchesMultiple = BatchStatistic.MAX_BATCH_MULTIPLE;

    /**
     * Holds the statistics across the time scheduled batches for the time
     * weighted variables
     *
     */
    private Map<TimeWeighted, TWBatchStatisticObserver> myBatchStats;

    /**
     * Holds the statistics across the time scheduled batches for the time
     * weighted variables
     *
     */
    private Map<ResponseVariable, RBatchStatisticObserver> myRVBatchStats;

    /**
     * Used to detect the adding and removing of ResponseVariables or
     * TimeWeighted variables from the Model
     *
     */
    private AddRemoveObserver myAddRemoveObserver;

    public BatchingElement(Model model) {
        this(model, 0.0, null);
    }

    public BatchingElement(Model model, double interval) {
        this(model, interval, null);
    }

    public BatchingElement(Model model, double interval, String name) {
        super(model, name);
        setBatchInterval(interval);
        myBatchStats = new HashMap<TimeWeighted, TWBatchStatisticObserver>();
        myRVBatchStats = new HashMap<ResponseVariable, RBatchStatisticObserver>();
        myAddRemoveObserver = new AddRemoveObserver();
        model.addObserver(myAddRemoveObserver);
    }

    /**
     * Returns a statistical summary Statistic on the TimeWeighted variable
     * across the observed batches This returns a copy of the summary
     * statistics.
     *
     * @param tw the TimeWeighted variable
     * @return the BatchStatistic
     */
    public final BatchStatistic getBatchStatistic(TimeWeighted tw) {
        TWBatchStatisticObserver bo = myBatchStats.get(tw);
        if (bo == null) {
            return new BatchStatistic(tw.getName() + " Across Batch Statistics");
        } else {
            return bo.myAcrossBatchStats.newInstance();
        }
    }

    /**
     * Returns a statistical summary BatchStatistic on the ResponseVariable This
     * returns a copy of the summary statistics.
     *
     * @param r the response variable
     * @return the batch statistic
     */
    public final BatchStatistic getBatchStatistic(ResponseVariable r) {
        RBatchStatisticObserver bo = myRVBatchStats.get(r);
        if (bo == null) {
            return new BatchStatistic(r.getName() + " Across Batch Statistics");
        } else {
            return bo.myBatchStats.newInstance();
        }
    }

    /**
     * Returns a list of summary statistics on all TimeWeighted variables The
     * list is a copy of originals.
     *
     * @return the list of statistics
     */
    public final List<BatchStatistic> getBatchStatisitcsForAllTimeWeighted() {
        List<BatchStatistic> list = new ArrayList<BatchStatistic>();
        for (TimeWeighted tw : myBatchStats.keySet()) {
            list.add(getBatchStatistic(tw));
        }
        return list;
    }

    /**
     * Returns a list of summary statistics on all ResponseVariables variables
     * The list is a copy of originals.
     *
     * @return the list
     */
    public final List<BatchStatistic> getBatchStatisitcsForAllResponseVariables() {
        List<BatchStatistic> list = new ArrayList<BatchStatistic>();
        for (ResponseVariable r : myRVBatchStats.keySet()) {
            list.add(getBatchStatistic(r));
        }
        return list;
    }

    /**
     * Sets the batch event priority.
     *
     * @param priority The batch event priority, lower means earlier
     */
    protected final void setBatchEventPriority(int priority) {
        myBatchEventPriority = priority;
    }

    /**
     * Gets the batch event priority
     *
     * @return The batch event priority
     */
    protected final int getBatchEventPriority() {
        return (myBatchEventPriority);
    }

    /**
     * Gets the current batch interval length for time weighted variables
     *
     * @return The batch interval as time
     */
    public final double getBatchInterval() {
        return (myBatchInterval);
    }

    /**
     * The starting number of batches, used to determine the batch interval when
     * it is not explicitly set.
     *
     * @return number of batches
     */
    public final double getTimeWeightedStartingNumberOfBatches() {
        return myNumTWBatches;
    }

    /**
     * Sets the initial number of batches for time-weighted variables The number
     * of initial batches is not recommended to be less than 10.
     *
     * @param numBatches must be bigger than 0
     */
    public final void setTimeWeightedStartingNumberOfBatches(int numBatches) {
        if (numBatches <= 0) {
            throw new IllegalArgumentException("The number of batches must be >0");
        }
        if (numBatches < 10) {
            StringBuilder sb = new StringBuilder();
            sb.append("The number of initial batches < 10\n");
            sb.append("is not recommended for batching time-based variables\n");
            JSL.LOGGER.warning(sb.toString());
            System.out.flush();
        }
        myNumTWBatches = numBatches;
    }

    /**
     * Sets the batch interval length Changing this during a replication has no
     * effect. The batch interval is used to schedule events during a
     * replication and must be the same throughout the replication. If the
     * supplied interval is 0.0, then the method getApproximateBatchInterval()
     * will be used to determine the interval for the replication
     *
     * @param batchInterval The batch interval size in time units must be
     * &gt;=0, if it is larger than run length it will not occur
     */
    public final void setBatchInterval(double batchInterval) {
        if (batchInterval < 0.0) {
            throw new IllegalArgumentException("The batch interval cannot be less than zero");
        }
        myBatchInterval = batchInterval;
    }

    /**
     * Checks if a batching event has been scheduled for this model element
     *
     * @return True means that it has been scheduled.
     */
    public final boolean isBatchEventScheduled() {
        if (myBatchEvent == null) {
            return (false);
        } else {
            return (myBatchEvent.isScheduled());
        }
    }

    /**
     * This method returns a suggested batching interval based on the length of
     * the run, the warm up period, and default number of batches.
     *
     * @return a double representing an approximate batch interval
     */
    protected final double getApproximateBatchInterval() {
        ExperimentGetIfc e = getModel().getExperiment();
        if (e == null) {
            return DEFAULT_BATCH_INTERVAL;
        }
        return getApproximateBatchInterval(e.getLengthOfReplication(), e.getLengthOfWarmUp());
    }

    /**
     * This method returns a suggested batching interval based on the length of
     * of the replication and warm up length for TimeWeighted variables.
     *
     * This value is used in the calculation of the approximate batching
     * interval if batching is turned on and there is a finite run length.
     *
     * If the run length is finite, then the batch interval is approximated as
     * follows:
     *
     * t = length of replication - length of warm up 
     * n = getTimeWeightedStartingNumberOfBatches()
     *
     * batching interval = t/n
     *
     * DEFAULT_NUM_TW_BATCHES = 512.0
     *
     * @param repLength the replication length
     * @param warmUp the warmup length
     * @return the batch interval
     */
    public final double getApproximateBatchInterval(double repLength, double warmUp) {

        if (repLength <= 0.0) {
            throw new IllegalArgumentException("The length of the replication must be > 0");
        }

        if (warmUp < 0) {
            throw new IllegalArgumentException("The length of the replication must be >= 0");
        }

        double deltaT = 0.0;

        if (Double.isInfinite(repLength)) {
            // runlength is infinite
            deltaT = DEFAULT_BATCH_INTERVAL;
        } else { // runlength is finite
            double t = repLength;
            t = t - warmUp; // actual observation length
            double n = getTimeWeightedStartingNumberOfBatches();
            deltaT = t / n;
        }
        return (deltaT);
    }

    /**
     * Gets the maximum batching multiple. See BatchStatistic Used for batching
     * of ResponseVariables
     *
     * @return the maximum number of batch size multiple
     */
    public final int getMaxNumBatchesMultiple() {
        return myMaxNumBatchesMultiple;
    }

    /**
     * Gets the minimum batch size. See BatchStatistic Used for batching
     * ResponseVariables
     *
     * @return the minimum batch size
     */
    public final int getMinBatchSize() {
        return myMinBatchSize;
    }

    /**
     * Gets the minimum number of batches. See BatchStatistic Used for batching
     * ResponseVariables
     *
     * @return the min number of batches
     */
    public final int getMinNumBatches() {
        return myMinNumBatches;
    }

    /**
     * Sets the parameters for batch statistics
     *
     * @param minNumBatches The minimum number of batches, must be &gt;= 2
     * @param minBatchSize The minimum number of observations per batch, must be
     * &gt;= 2
     * @param maxNBMultiple The maximum number of batches as a multiple of the
     * minimum number of batches. For example, if minNB = 20 and maxNBMultiple =
     * 2 then the maximum number of batches allowed will be 40. maxNBMultiple
     * must be &gt;= 2.
     */
    public final void setBatchingParameters(int minNumBatches, int minBatchSize, int maxNBMultiple) {
        if (minNumBatches <= 1) {
            throw new IllegalArgumentException("Number of batches must be >= 2");
        }
        myMinNumBatches = minNumBatches;

        if (minBatchSize <= 1) {
            throw new IllegalArgumentException("Batch size must be >= 2");
        }
        myMinBatchSize = minBatchSize;

        if (maxNBMultiple <= 1) {
            throw new IllegalArgumentException("Maximum number of batches multiple must be >= 2");
        }
        myMaxNumBatchesMultiple = maxNBMultiple;
    }

    @Override
    protected void beforeExperiment() {

        myBatchStats.clear();
        myRVBatchStats.clear();
        Model m = getModel();
        List<ResponseVariable> list = m.getResponseVariables();
        for (ResponseVariable r : list) {
            if (r instanceof TimeWeighted) {
                createTimeWeightedBatchStatistic((TimeWeighted) r);
            } else {
                createResponseVariableBatchStatistic(r);
            }
        }
    }

    @Override
    protected void beforeReplication() {
        if (getBatchInterval() == 0.0) {
            setBatchInterval(getApproximateBatchInterval());
        }
        myTimeBtwBatches = getBatchInterval();
    }

    private void createTimeWeightedBatchStatistic(TimeWeighted tw) {
        TWBatchStatisticObserver bo = new TWBatchStatisticObserver(tw.getName());
        myBatchStats.put(tw, bo);
        tw.addObserver(bo);
    }

    private void createResponseVariableBatchStatistic(ResponseVariable r) {
        RBatchStatisticObserver s = new RBatchStatisticObserver(r.getName() + " Batch Statistics");
        myRVBatchStats.put(r, s);
        r.addObserver(s);
    }

    @Override
    protected void initialize() {
        myBatchEvent = scheduleEvent(myTimeBtwBatches, 1, myBatchEventPriority);
    }

    /**
     * The batch method is called during each replication when the batching
     * event occurs This method ensures that each time weighted variable gets
     * within replication batch statistics collected across batches
     */
    protected void batch() {
        for (TimeWeighted tw : myBatchStats.keySet()) {
            tw.setValue(tw.getValue());
            TWBatchStatisticObserver bo = myBatchStats.get(tw);
            bo.batch();
        }
    }

    @Override
    protected void handleEvent(JSLEvent event) {
        myBatchEvent = event;
        // System.out.println(getTime() + "> BatchingElement batch()");
        batch();
        rescheduleEvent(event, myTimeBtwBatches);
    }

    protected void timeWeightedVariableAdded(TimeWeighted tw) {
        if (getModel().isRunning()) {
            // added after beginning of replication
            createTimeWeightedBatchStatistic(tw);
        }
    }

    protected void responseVariableAdded(ResponseVariable r) {
        if (getModel().isRunning()) {
            // added after beginning of replication
            createResponseVariableBatchStatistic(r);
        }
    }

    protected void timeWeightedVariableRemoved(TimeWeighted timeWeighted) {
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    protected void responseVariableRemoved(ResponseVariable responseVariable) {
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Writes the batching statistics to the supplied PrintWriter as comma
     * separated value output
     *
     * @param out the PrintWriter
     */
    public void writeCSVBatchStatistics(PrintWriter out) {
        if (out == null) {
            throw new IllegalArgumentException("The PrintStream was null");
        }
        boolean header = true;
        for (TWBatchStatisticObserver bo : myBatchStats.values()) {
            if (header) {
                header = false;
                out.println(bo.getCSVHeader());
            }
            out.println(bo.getCSVRow());
        }

        for (RBatchStatisticObserver bo : myRVBatchStats.values()) {
            out.println(bo.getCSVRow());
        }
    }

    /**
     * Returns the batching statistics as CSV in a StringBuilder
     *
     * @return the StringBuilder
     */
    public StringBuilder getCSVBatchStatistics() {
        StringBuilder sb = new StringBuilder();
        getCSVBatchStatistics(sb);
        return sb;
    }

    /**
     * Gets the batching statistics to the supplied StringBuilder as comma
     * separated value output
     *
     * @param sb the StringBuilder
     */
    public void getCSVBatchStatistics(StringBuilder sb) {
        if (sb == null) {
            throw new IllegalArgumentException("The StringBuilder was null");
        }
        boolean header = true;
        for (TWBatchStatisticObserver bo : myBatchStats.values()) {
            if (header) {
                header = false;
                sb.append(bo.getCSVHeader());
                sb.append("\n");
            }
            sb.append(bo.getCSVRow());
            sb.append("\n");
        }

        for (RBatchStatisticObserver bo : myRVBatchStats.values()) {
            sb.append(bo.getCSVRow());
            sb.append("\n");
        }
    }

    protected class TWBatchStatisticObserver extends ModelElementObserver {

        private WeightedStatistic myWithinBatchStats;

        //private Statistic myAcrossBatchStats;
        private BatchStatistic myAcrossBatchStats;

        public TWBatchStatisticObserver() {
            this(null);
        }

        public TWBatchStatisticObserver(String name) {
            super(name);
            myWithinBatchStats = new WeightedStatistic();
            //myAcrossBatchStats = new Statistic(name + " Across Batch Statistics");
            myAcrossBatchStats = new BatchStatistic(myMinNumBatches, myMinBatchSize,
                    myMaxNumBatchesMultiple, name + " Across Batch Statistics");
        }

        @Override
        protected void beforeReplication(ModelElement m, Object arg) {
            myAcrossBatchStats.reset();
            myWithinBatchStats.reset();
        }

        @Override
        protected void update(ModelElement m, Object arg) {
            TimeWeighted tw = (TimeWeighted) m;
            double weight = tw.getWeight();
            double prev = tw.getPreviousValue();
            //System.out.println("prev = " + prev);
            //System.out.println("weight = " + weight);
            myWithinBatchStats.collect(prev, weight);
        }

        @Override
        protected void warmUp(ModelElement m, Object arg) {
//            System.out.println("Warming up " + getName());
            myAcrossBatchStats.reset();
            myWithinBatchStats.reset();
        }

        protected void batch() {
            //System.out.println("Formed batch");
            // System.out.println("collecting = " + myWithinBatchStats.getAverage() );
            double avg = myWithinBatchStats.getAverage();
//            if (Double.isNaN(avg)){
//                System.out.println("Batching " + getName());
//                System.out.println("Time = " + getTime());
//                System.out.println("observed " + avg);
//            }
            myAcrossBatchStats.collect(avg);
            myWithinBatchStats.reset();
        }

        @Override
        public String toString() {
            return myAcrossBatchStats.toString();
        }

        public String getCSVRow() {
            StringBuilder row = new StringBuilder();
            row.append(getModel().getName());
            row.append(",");
            row.append("TimeWeighted");
            row.append(",");
            row.append(myAcrossBatchStats.getCSVStatistic());
            return row.toString();
        }

        public String getCSVHeader() {
            StringBuilder header = new StringBuilder();
            header.append("Model,");
            header.append("StatType,");
            header.append(myAcrossBatchStats.getCSVStatisticHeader());
            return header.toString();
        }
    }

    protected class RBatchStatisticObserver extends ModelElementObserver {

        private BatchStatistic myBatchStats;

        public RBatchStatisticObserver() {
            this(null);
        }

        public RBatchStatisticObserver(String name) {
            super(name);
            myBatchStats = new BatchStatistic(myMinNumBatches, myMinBatchSize,
                    myMaxNumBatchesMultiple, name + " Batch Statistics");
        }

        @Override
        protected void beforeReplication(ModelElement m, Object arg) {
            myBatchStats.reset();
        }

        @Override
        protected void update(ModelElement m, Object arg) {
            ResponseVariable r = (ResponseVariable) m;
            myBatchStats.collect(r);
        }

        @Override
        protected void warmUp(ModelElement m, Object arg) {
            myBatchStats.reset();
        }

        @Override
        public String toString() {
            return myBatchStats.toString();
        }

        public String getCSVRow() {
            StringBuilder row = new StringBuilder();
            row.append(getModel().getName());
            row.append(",");
            row.append("ResponseVariable");
            row.append(",");
            row.append(myBatchStats.getCSVStatistic());
            return row.toString();
        }

        public String getCSVHeader() {
            StringBuilder header = new StringBuilder();
            header.append("Model,");
            header.append("StatType,");
            header.append(myBatchStats.getCSVStatisticHeader());
            return header.toString();
        }
    }

    protected class AddRemoveObserver implements ObserverIfc {

        @Override
        public void update(Object theObserved, Object arg) {
            if (arg instanceof ResponseVariable) {
                Model m = (Model) theObserved;
                ModelElement me = (ModelElement) arg;
                int state = m.getObserverState();
                if (state == Model.MODEL_ELEMENT_ADDED) {
                    if (arg instanceof TimeWeighted) {
                        timeWeightedVariableAdded((TimeWeighted) me);
                    } else {
                        responseVariableAdded((ResponseVariable) me);
                    }
                } else if (state == Model.MODEL_ELEMENT_REMOVED) {
                    if (arg instanceof TimeWeighted) {
                        timeWeightedVariableRemoved((TimeWeighted) me);
                    } else {
                        responseVariableRemoved((ResponseVariable) me);
                    }
                }
            }
        }
    }

    @Override
    public String asString(){
        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------------------------------------");
        sb.append("\n");
        sb.append("Batch Statistics");
        sb.append("\n");
        sb.append("------------------------------------------------------------");
        sb.append("\n");
        sb.append("TimeWeighted Variables \n");
        sb.append("Batching based on ");
        sb.append("batch interval = ");
        sb.append(getBatchInterval());
        sb.append(" time units \n");
        sb.append("Initial number of batches = ");
        sb.append(getTimeWeightedStartingNumberOfBatches());
        sb.append("\n");
        for (TWBatchStatisticObserver bo : myBatchStats.values()) {
            sb.append(bo);
            sb.append("\n");
        }
        for (RBatchStatisticObserver bo : myRVBatchStats.values()) {
            sb.append(bo);
            sb.append("\n");
        }
        sb.append("------------------------------------------------------------");
        sb.append("\n");
        return sb.toString();
    }
}
