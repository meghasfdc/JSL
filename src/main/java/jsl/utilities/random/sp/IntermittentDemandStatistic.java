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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.random.sp;

import jsl.utilities.DataSource;
import java.util.Observable;
import java.util.Observer;

import jsl.utilities.IdentityIfc;
import jsl.utilities.Interval;
import jsl.utilities.statistic.Statistic;
import jsl.utilities.statistic.StatisticAccessorIfc;

/**
 * Implements the statistics estimator for an intermittent demand scenario
 * @author vvarghe
 *
 */
public class IntermittentDemandStatistic implements StatisticAccessorIfc, IdentityIfc, Observer {

    protected long myId;

    protected String myName;

    private static long myIdCounter_;

    private double myDemand;

    private Statistic myNZDemandStat;

    private Statistic myDemandStat;

    private Statistic myIntervalBetweenNonZeroDemandsStat;

    private Statistic myIntervalBetweenZeroDemandsStat;

    /*
     * This can be true only after the first the demand state is observed: zero or non-zero
     *
     */
    private boolean TRANSITION_PROB_ESTIMATION_CAN_BE_INITIALIZED;

    private int myPreviousEvent;

    private int numSuccess;

    private int numFailure;

    private int numSuccessAfterSuccess;

    private int numSuccessAfterFailure;

    private int myTimeSpanSinceLastDemand;

    private int myTimeSpanSinceLastZeroDemand;

    private int myTimeIndexofLastDemand;

    private int myTimeIndexofLastZeroDemand;

    private boolean INTERVAL_BTWN_NONZERODEMANDS_INITIALISED;

    private boolean INTERVAL_BTWN_ZERODEMANDS_INITIALISED;

    private int numTransBefore_IntvlBtwnNonZeroDemandsStatInitialized;

    private int numTransBefore_IntvlBtwnZeroDemandsStatInitialized;

    private int myCurrentTimeIndex = 0;

    public IntermittentDemandStatistic() {
        this(null);
    }

    public IntermittentDemandStatistic(String name) {
        setId();
        setName(name);

        myNZDemandStat = new Statistic("Non-zero demand series");
        myDemandStat = new Statistic("Demand series");
        myIntervalBetweenNonZeroDemandsStat = new Statistic("Interval between non zero demands");
        myIntervalBetweenZeroDemandsStat = new Statistic("Interval between zero demands");
        //myNZDemandStat.setSaveDataOption(true);
        //myDemandStat.setSaveDataOption(true);
        //myIntervalBetweenNonZeroDemandsStat.setSaveDataOption(true);
        //myIntervalBetweenZeroDemandsStat.setSaveDataOption(true);

        TRANSITION_PROB_ESTIMATION_CAN_BE_INITIALIZED = false;
        numSuccess = 0;
        numFailure = 0;
        numSuccessAfterSuccess = 0;
        numSuccessAfterFailure = 0;

        INTERVAL_BTWN_NONZERODEMANDS_INITIALISED = false;
        INTERVAL_BTWN_ZERODEMANDS_INITIALISED = false;
    }

    public void reset() {
        myNZDemandStat.reset();
        myDemandStat.reset();
        myIntervalBetweenNonZeroDemandsStat.reset();
        myIntervalBetweenZeroDemandsStat.reset();

        myCurrentTimeIndex = 0;

        TRANSITION_PROB_ESTIMATION_CAN_BE_INITIALIZED = false;
        numSuccess = 0;
        numFailure = 0;
        numSuccessAfterSuccess = 0;
        numSuccessAfterFailure = 0;

        INTERVAL_BTWN_NONZERODEMANDS_INITIALISED = false;
        INTERVAL_BTWN_ZERODEMANDS_INITIALISED = false;

        myPreviousEvent = 0;

        myTimeSpanSinceLastDemand = 0;
        myTimeSpanSinceLastZeroDemand = 0;
        myTimeIndexofLastDemand = 0;
        myTimeIndexofLastZeroDemand = 0;

        numTransBefore_IntvlBtwnNonZeroDemandsStatInitialized = 0;
        numTransBefore_IntvlBtwnZeroDemandsStatInitialized = 0;

    }

    public void setSaveDataOption(boolean flag) {
        myNZDemandStat.setSaveDataOption(flag);
        myDemandStat.setSaveDataOption(flag);
        myIntervalBetweenNonZeroDemandsStat.setSaveDataOption(flag);
        myIntervalBetweenZeroDemandsStat.setSaveDataOption(flag);
    }

    protected void setId() {
        myIdCounter_ = myIdCounter_ + 1;
        myId = myIdCounter_;
    }

    public final void setName(String str) {
        if (str == null) {
            String s = this.getClass().getName();
            int k = s.lastIndexOf(".");
            if (k != -1) {
                s = s.substring(k + 1);
            }
            myName = s;
        } else {
            myName = str;
        }
    }

    @Override
    public String getName() {
        return myName;
    }

    @Override
    public long getId() {
        return myId;
    }

    /**
     * 
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        DataSource ds = (DataSource) o;
        double forecastValue = ds.getValue();
        if (!Double.isNaN(forecastValue)) {
            collect(forecastValue);
        }
    }

    public void collect(double[] data) {

        for (double d : data) {
            this.collect(d);
        }

    }

    public void collect(double data) {
        myDemand = data;
        if (myDemand > 0) {
            numSuccess++;
            if (TRANSITION_PROB_ESTIMATION_CAN_BE_INITIALIZED == true) {
                if (myPreviousEvent != 0) {
                    numSuccessAfterSuccess++;
                } else {
                    numSuccessAfterFailure++;
                }
            }
            myPreviousEvent = 1;
            //myNZDemandStat.collect(myDemand);
            //myDemandStat.collect(myDemand);
            myNZDemandStat.collect(myDemand, 1.0);
            myDemandStat.collect(myDemand, 1.0);
            TRANSITION_PROB_ESTIMATION_CAN_BE_INITIALIZED = true;
            setIntervalBetweenNonZeroDemands(myCurrentTimeIndex);
            setIntervalBetweenZeroDemands(myCurrentTimeIndex);
        } else {
            myPreviousEvent = 0;
            numFailure++;
            myDemand = 0;
            //myDemandStat.collect(myDemand);
            myDemandStat.collect(myDemand, 1.0);
            TRANSITION_PROB_ESTIMATION_CAN_BE_INITIALIZED = true;
            setIntervalBetweenNonZeroDemands(myCurrentTimeIndex);
            setIntervalBetweenZeroDemands(myCurrentTimeIndex);
        }
        myCurrentTimeIndex++;
    }

    public double[] getNonZeroDemandsData() {
        return this.myNZDemandStat.getSavedData();
    }

    public double[] getDemandsData() {
        return this.myDemandStat.getSavedData();
    }

    public double[] getIntervalBetweenNonZeroDemandsData() {
        return this.myIntervalBetweenNonZeroDemandsStat.getSavedData();
    }

    public double[] getIntervalBetweenZeroDemandsData() {
        return this.myIntervalBetweenZeroDemandsStat.getSavedData();
    }

    public int getNumTransactions() {
        return (int) myNZDemandStat.getCount();
    }

    private void setIntervalBetweenNonZeroDemands(int myCurrentTimeIndex) {
        if (INTERVAL_BTWN_NONZERODEMANDS_INITIALISED == false) {
            if (myDemand != 0) {
                numTransBefore_IntvlBtwnNonZeroDemandsStatInitialized++;
                if (numTransBefore_IntvlBtwnNonZeroDemandsStatInitialized == 2) {
                    INTERVAL_BTWN_NONZERODEMANDS_INITIALISED = true;
                    myTimeSpanSinceLastDemand = myCurrentTimeIndex - myTimeIndexofLastDemand;
                    //myIntervalBetweenNonZeroDemandsStat.collect(myTimeSpanSinceLastDemand);
                    myIntervalBetweenNonZeroDemandsStat.collect(myTimeSpanSinceLastDemand, 1.0);
                }
                myTimeIndexofLastDemand = myCurrentTimeIndex;
            }
        } else {
            if (myDemand != 0) {
                numTransBefore_IntvlBtwnNonZeroDemandsStatInitialized++;
                myTimeSpanSinceLastDemand = myCurrentTimeIndex - myTimeIndexofLastDemand;
                //myIntervalBetweenNonZeroDemandsStat.collect(myTimeSpanSinceLastDemand);
                myIntervalBetweenNonZeroDemandsStat.collect(myTimeSpanSinceLastDemand, 1.0);
                myTimeIndexofLastDemand = myCurrentTimeIndex;
            }
        }
    }

    private void setIntervalBetweenZeroDemands(int myCurrentTimeIndex) {
        if (INTERVAL_BTWN_ZERODEMANDS_INITIALISED == false) {
            if (myDemand == 0) {
                numTransBefore_IntvlBtwnZeroDemandsStatInitialized++;
                if (numTransBefore_IntvlBtwnZeroDemandsStatInitialized == 2) {
                    INTERVAL_BTWN_ZERODEMANDS_INITIALISED = true;
                    myTimeSpanSinceLastZeroDemand = myCurrentTimeIndex - myTimeIndexofLastZeroDemand;
                    //myIntervalBetweenZeroDemandsStat.collect(myTimeSpanSinceLastZeroDemand);
                    myIntervalBetweenZeroDemandsStat.collect(myTimeSpanSinceLastZeroDemand, 1.0);
                }
                myTimeIndexofLastZeroDemand = myCurrentTimeIndex;
            }
        } else {
            if (myDemand == 0) {
                numTransBefore_IntvlBtwnZeroDemandsStatInitialized++;
                myTimeSpanSinceLastZeroDemand = myCurrentTimeIndex - myTimeIndexofLastZeroDemand;
                //myIntervalBetweenZeroDemandsStat.collect(myTimeSpanSinceLastZeroDemand);
                myIntervalBetweenZeroDemandsStat.collect(myTimeSpanSinceLastZeroDemand, 1.0);
                myTimeIndexofLastZeroDemand = myCurrentTimeIndex;
            }
        }
    }

    public Statistic getIntervalBetweenNonZeroDemandStat() {
        return myIntervalBetweenNonZeroDemandsStat;
    }

    public Statistic getIntervalBetweenZeroDemandsStat() {
        return myIntervalBetweenZeroDemandsStat;
    }

    public Statistic getNonZeroDemandStat() {
        return myNZDemandStat;
    }

    public Statistic getDemandStat() {
        return myDemandStat;
    }

    public double getProbSuccessAfterSuccess() {
        return (double) (numSuccessAfterSuccess) / (double) numSuccess;
    }

    public double getProbSuccessAfterFailure() {
        return (double) numSuccessAfterFailure / (double) numFailure;
    }

    public double getProbSuccess() {
        return (double) numSuccess / (double) (numSuccess + numFailure);
    }

    public double getProbFailure() {
        return (double) numFailure / (double) (numSuccess + numFailure);
    }

    @Override
    public double getAverage() {
        return myDemandStat.getAverage();
    }

    @Override
    public double getConfidenceLevel() {
        return myDemandStat.getConfidenceLevel();
    }

    @Override
    public double getCount() {
        return myDemandStat.getCount();
    }

    @Override
    public double getDeviationSumOfSquares() {
        return myDemandStat.getDeviationSumOfSquares();
    }

    @Override
    public double getHalfWidth(double alpha) {
        return myDemandStat.getHalfWidth(alpha);
    }

    @Override
    public double getHalfWidth() {
        return myDemandStat.getHalfWidth();
    }
    
    public Interval getConfidenceInterval(double alpha) {
        return myDemandStat.getConfidenceInterval(alpha);
    }

    @Override
    public Interval getConfidenceInterval() {
        return myDemandStat.getConfidenceInterval();
    }

    @Override
    public double getKurtosis() {
        return myDemandStat.getKurtosis();
    }

    @Override
    public double getLag1Correlation() {
        return myDemandStat.getLag1Correlation();
    }

    @Override
    public double getLag1Covariance() {
        return myDemandStat.getLag1Covariance();
    }

    @Override
    public double getLastValue() {
        return myDemandStat.getLastValue();
    }

    @Override
    public double getLastWeight() {
        return myDemandStat.getLastWeight();
    }

    @Override
    public double getMax() {
        return myDemandStat.getMax();
    }

    @Override
    public double getRelativeError() {
        return myDemandStat.getRelativeError();
    }

    @Override
    public double getRelativeWidth() {
        return myDemandStat.getRelativeWidth();
    }

    @Override
    public double getRelativeWidth(double level) {
        return myDemandStat.getRelativeWidth(level);
    }

    @Override
    public double getMin() {
        return myDemandStat.getMin();
    }

    @Override
    public double getNumberMissing() {
        return myDemandStat.getNumberMissing();
    }

    @Override
    public double getSkewness() {
        return myDemandStat.getSkewness();
    }

    @Override
    public double getStandardDeviation() {
        return myDemandStat.getStandardDeviation();
    }

    @Override
    public double getStandardError() {
        return myDemandStat.getStandardError();
    }

    @Override
    public final int getLeadingDigitRule(double a) {
        return myDemandStat.getLeadingDigitRule(a);
    }

    @Override
    public void getStatistics(double[] statistics) {
        if (statistics.length != 87) {
            throw new IllegalArgumentException("The supplied array was not of size 87");
        }

        statistics[0] = getCount();
        statistics[1] = getAverage();
        statistics[2] = getVariance();
        statistics[3] = getStandardDeviation();
        statistics[4] = getStandardError();
        statistics[5] = getMin();
        statistics[6] = getMax();
        statistics[7] = getSum();
        statistics[8] = getWeightedSum();
        statistics[9] = getWeightedSumOfSquares();
        statistics[10] = getSumOfWeights();
        statistics[11] = getWeightedAverage();
        statistics[12] = getDeviationSumOfSquares();
        statistics[13] = getLastValue();
        statistics[14] = getLastWeight();
        statistics[15] = getKurtosis();
        statistics[16] = getSkewness();
        statistics[17] = getLag1Correlation();
        statistics[18] = getLag1Covariance();
        statistics[19] = getVonNeumannLag1TestStatistic();
        statistics[20] = getNumberMissing();

        statistics[21] = myNZDemandStat.getCount();
        statistics[22] = myNZDemandStat.getAverage();
        statistics[23] = myNZDemandStat.getVariance();
        statistics[24] = myNZDemandStat.getStandardDeviation();
        statistics[25] = myNZDemandStat.getStandardError();
        statistics[26] = myNZDemandStat.getMin();
        statistics[27] = myNZDemandStat.getMax();
        statistics[28] = myNZDemandStat.getSum();
        statistics[29] = myNZDemandStat.getWeightedSum();
        statistics[30] = myNZDemandStat.getWeightedSumOfSquares();
        statistics[31] = myNZDemandStat.getSumOfWeights();
        statistics[32] = myNZDemandStat.getWeightedAverage();
        statistics[33] = myNZDemandStat.getDeviationSumOfSquares();
        statistics[34] = myNZDemandStat.getLastValue();
        statistics[35] = myNZDemandStat.getLastWeight();
        statistics[36] = myNZDemandStat.getKurtosis();
        statistics[37] = myNZDemandStat.getSkewness();
        statistics[38] = myNZDemandStat.getLag1Correlation();
        statistics[39] = myNZDemandStat.getLag1Covariance();
        statistics[40] = myNZDemandStat.getVonNeumannLag1TestStatistic();
        statistics[41] = myNZDemandStat.getNumberMissing();

        statistics[42] = myIntervalBetweenNonZeroDemandsStat.getCount();
        statistics[43] = myIntervalBetweenNonZeroDemandsStat.getAverage();
        statistics[44] = myIntervalBetweenNonZeroDemandsStat.getVariance();
        statistics[45] = myIntervalBetweenNonZeroDemandsStat.getStandardDeviation();
        statistics[46] = myIntervalBetweenNonZeroDemandsStat.getStandardError();
        statistics[47] = myIntervalBetweenNonZeroDemandsStat.getMin();
        statistics[48] = myIntervalBetweenNonZeroDemandsStat.getMax();
        statistics[49] = myIntervalBetweenNonZeroDemandsStat.getSum();
        statistics[50] = myIntervalBetweenNonZeroDemandsStat.getWeightedSum();
        statistics[51] = myIntervalBetweenNonZeroDemandsStat.getWeightedSumOfSquares();
        statistics[52] = myIntervalBetweenNonZeroDemandsStat.getSumOfWeights();
        statistics[53] = myIntervalBetweenNonZeroDemandsStat.getWeightedAverage();
        statistics[54] = myIntervalBetweenNonZeroDemandsStat.getDeviationSumOfSquares();
        statistics[55] = myIntervalBetweenNonZeroDemandsStat.getLastValue();
        statistics[56] = myIntervalBetweenNonZeroDemandsStat.getLastWeight();
        statistics[57] = myIntervalBetweenNonZeroDemandsStat.getKurtosis();
        statistics[58] = myIntervalBetweenNonZeroDemandsStat.getSkewness();
        statistics[59] = myIntervalBetweenNonZeroDemandsStat.getLag1Correlation();
        statistics[60] = myIntervalBetweenNonZeroDemandsStat.getLag1Covariance();
        statistics[61] = myIntervalBetweenNonZeroDemandsStat.getVonNeumannLag1TestStatistic();
        statistics[62] = myIntervalBetweenNonZeroDemandsStat.getNumberMissing();

        statistics[63] = myIntervalBetweenZeroDemandsStat.getCount();
        statistics[64] = myIntervalBetweenZeroDemandsStat.getAverage();
        statistics[65] = myIntervalBetweenZeroDemandsStat.getVariance();
        statistics[66] = myIntervalBetweenZeroDemandsStat.getStandardDeviation();
        statistics[67] = myIntervalBetweenZeroDemandsStat.getStandardError();
        statistics[68] = myIntervalBetweenZeroDemandsStat.getMin();
        statistics[69] = myIntervalBetweenZeroDemandsStat.getMax();
        statistics[70] = myIntervalBetweenZeroDemandsStat.getSum();
        statistics[71] = myIntervalBetweenZeroDemandsStat.getWeightedSum();
        statistics[72] = myIntervalBetweenZeroDemandsStat.getWeightedSumOfSquares();
        statistics[73] = myIntervalBetweenZeroDemandsStat.getSumOfWeights();
        statistics[74] = myIntervalBetweenZeroDemandsStat.getWeightedAverage();
        statistics[75] = myIntervalBetweenZeroDemandsStat.getDeviationSumOfSquares();
        statistics[76] = myIntervalBetweenZeroDemandsStat.getLastValue();
        statistics[77] = myIntervalBetweenZeroDemandsStat.getLastWeight();
        statistics[78] = myIntervalBetweenZeroDemandsStat.getKurtosis();
        statistics[79] = myIntervalBetweenZeroDemandsStat.getSkewness();
        statistics[80] = myIntervalBetweenZeroDemandsStat.getLag1Correlation();
        statistics[81] = myIntervalBetweenZeroDemandsStat.getLag1Covariance();
        statistics[82] = myIntervalBetweenZeroDemandsStat.getVonNeumannLag1TestStatistic();
        statistics[83] = myIntervalBetweenZeroDemandsStat.getNumberMissing();

        statistics[84] = this.getProbSuccessAfterSuccess();
        statistics[85] = this.getProbSuccessAfterFailure();
        statistics[86] = this.getProbFailure();

    }

    @Override
    public double[] getStatistics() {
        double[] x = new double[87];
        getStatistics(x);
        return (x);
    }

    @Override
    public double getSum() {
        return myDemandStat.getSum();
    }

    @Override
    public double getSumOfWeights() {
        return myDemandStat.getSumOfWeights();
    }

    @Override
    public double getVariance() {
        return myDemandStat.getVariance();
    }

    @Override
    public double getVonNeumannLag1TestStatistic() {
        return myDemandStat.getVonNeumannLag1TestStatistic();
    }

    @Override
    public final double getVonNeumannLag1TestStatisticPValue() {
        return myDemandStat.getVonNeumannLag1TestStatisticPValue();
    }

    @Override
    public double getWeightedAverage() {
        return myDemandStat.getWeightedAverage();
    }

    @Override
    public double getWeightedSum() {
        return myDemandStat.getWeightedSum();
    }

    @Override
    public double getWeightedSumOfSquares() {
        return myDemandStat.getWeightedSumOfSquares();
    }

    /** NOT IMPLEMENTED YET
     *
     * @return
     */
    @Override
    public String getCSVStatistic() {
        //TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** NOT IMPLEMENTED YET
     *
     * @return
     */
    @Override
    public String getCSVStatisticHeader() {
        //TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
