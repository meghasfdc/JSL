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
package jsl.utilities.statistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import jsl.utilities.Interval;
import jsl.utilities.random.distributions.DEmpiricalCDF;
import jsl.utilities.random.distributions.DUniform;
import jsl.utilities.random.rvariable.DUniformRV;

/**
 * This class tabulates the weights associated with
 * the integers presented to it via the collect() method
 * Every value presented is interpreted as an integer
 * For every value presented a sum of the weights is maintained.
 * There could be space/time performance issues if
 * the number of different values presented is large.
 * <p>
 * This class can be useful for tabulating the weighted proportions
 * over the values (integers) presented.
 *
 * @author rossetti
 */
public class WeightedProportion extends AbstractStatistic {

    /**
     * A Cell represents a value, count pairing
     */
    protected Map<Cell, Cell> myCells;

    /**
     * Collects statistical information
     */
    protected Statistic myStatistic;

    /**
     * Used as a temporary cell during tabulation
     */
    protected Cell myTemp;

    /**
     * The smallest value allowed.  Any
     * values &lt; to this value will be counted
     * in the underflow count
     */
    protected int myLowerLimit;

    /**
     * The largest value allowed.  Any
     * values &gt; to this value will be counted
     * in the overflow count
     */
    protected int myUpperLimit;

    /**
     * Counts of values located below first bin.
     */
    protected int myUnderFlowCount;

    /**
     * Counts of values located above last bin.
     */
    protected int myOverFlowCount;

    public WeightedProportion() {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, null);
    }

    public WeightedProportion(String name) {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, name);
    }

    public WeightedProportion(int lowerLimit, int upperLimit) {
        this(lowerLimit, upperLimit, null);
    }

    public WeightedProportion(int lowerLimit, int upperLimit, String name) {
        super(name);
        if (lowerLimit >= upperLimit) {
            throw new IllegalArgumentException("The lower limit must be < the upper limit");
        }
        myLowerLimit = lowerLimit;
        myUpperLimit = upperLimit;
        myStatistic = new Statistic(name);
        myTemp = new Cell();
        myCells = new HashMap<Cell, Cell>();

    }

    @Override
    public boolean collect(double x, double weight) {
        if (isTurnedOff()) {
            return false;
        }
        if (Double.isNaN(x)) {
            myNumMissing++;
            return true;
        }

        if (getSaveDataOption()) {
            saveData(x, weight);
        }

        int i = (int) x;

        myStatistic.collect(i, weight);

        if (i < myLowerLimit) {
            myUnderFlowCount = myUnderFlowCount + 1;
            return true;
        }

        if (i > myUpperLimit) {
            myOverFlowCount = myOverFlowCount + 1;
            return true;
        }

        // myLowerLimit <= x <= myUpperLimit

        myTemp.myValue = i;
        Cell c = myCells.get(myTemp);
        if (c == null) {
            c = new Cell(i);
            myCells.put(c, c);
        } else {
            c.myWeight = c.myWeight + weight;
        }
        return true;
    }

    @Override
    public void reset() {
        myNumMissing = 0.0;
        myOverFlowCount = 0;
        myUnderFlowCount = 0;
        myStatistic.reset();
        myCells.clear();
        clearSavedData();
    }

    /**
     * The number of observations that fell below the first bin's lower limit
     *
     * @return
     */
    public final int getUnderFlowCount() {
        return (myUnderFlowCount);
    }

    /**
     * The number of observations that fell past the last bin's upper limit
     *
     * @return
     */
    public final int getOverFlowCount() {
        return (myOverFlowCount);
    }

    /**
     * Returns an array of size getNumberOfCells() containing
     * the values increasing by value, null if no values
     *
     * @return
     */
    public final int[] getValues() {
        if (myCells.isEmpty()) {
            return null;
        }
        SortedSet<Cell> cellSet = getCells();
        int[] v = new int[myCells.size()];
        int i = 0;
        for (Cell c : cellSet) {
            v[i] = c.myValue;
            i++;
        }
        return v;
    }

    /**
     * Returns an array of size getNumberOfCells() containing
     * the frequencies by value, null if no values
     *
     * @return an array of size getNumberOfCells() containing
     *       the frequencies by value, null if no values
     */
    public final double[] getWeights() {
        if (myCells.isEmpty()) {
            return null;
        }
        SortedSet<Cell> cellSet = getCells();
        double[] v = new double[myCells.size()];
        int i = 0;
        for (Cell c : cellSet) {
            v[i] = c.myWeight;
            i++;
        }
        return v;
    }

    /**
     * Returns an array of size getNumberOfCells() containing
     * the proportions by value, null if no values
     *
     * @return Returns an array of size getNumberOfCells() containins the proportions by value, null if no values
     */
    public final double[] getProportions() {
        if (myCells.isEmpty()) {
            return null;
        }
        SortedSet<Cell> cellSet = getCells();
        double[] v = new double[myCells.size()];
        int i = 0;
        for (Cell c : cellSet) {
            v[i] = c.myProportion;
            i++;
        }
        return v;
    }

    /**
     * Returns the cumulative frequency up to an including i
     *
     * @param i the value to evaluate
     * @return the cumulative frequency up to an including i
     */
    public final double getCumulativeWeight(int i) {
        if (myCells.isEmpty()) {
            return 0;
        }
        SortedSet<Cell> cellSet = getCells();
        double sum = 0;
        for (Cell c : cellSet) {
            if (c.myValue <= i) {
                sum = sum + c.myWeight;
            } else {
                break;
            }
        }
        return sum;
    }

    /**
     * Returns the cumulative proportion up to an including i
     *
     * @param i the value to evaluate
     * @return the cumulative proportion up to an including i
     */
    public final double getCumulativeProportion(int i) {
        if (myCells.isEmpty()) {
            return 0;
        }
        double n = getTotalCount();
        return (getCumulativeWeight(i) / n);
    }

    /**
     * The 0th column is the values and the 1st column is the frequency
     *
     * @return Returns a n by 2 array of value, frequency
     * pairs where n = getNumberOfCells()
     */
    public final double[][] getValueWeights() {
        if (myCells.isEmpty()) {
            return null;
        }
        SortedSet<Cell> cellSet = getCells();
        double[][] v = new double[myCells.size()][2];
        int i = 0;
        for (Cell c : cellSet) {
            v[i][0] = c.myValue;
            v[i][1] = c.myWeight;
            i++;
        }
        return v;
    }

    /**
     * The 0th column is the values and the 1st column is the proportions
     *
     * @return returns a n by 2 array of value, proportion pairs, where
     * n = n = getNumberOfCells()
     */
    public final double[][] getValueProportions() {
        if (myCells.isEmpty()) {
            return null;
        }
        SortedSet<Cell> cellSet = getCells();
        double[][] v = new double[myCells.size()][2];
        int i = 0;
        for (Cell c : cellSet) {
            v[i][0] = c.myValue;
            v[i][1] = c.myProportion;
            i++;
        }
        return v;
    }

    /**
     * Returns the number of cells tabulated
     *
     * @return
     */
    public final int getNumberOfCells() {
        return myCells.size();
    }

    /**
     * The total count associated with the values
     *
     * @return
     */
    public final int getTotalCount() {
        return ((int) myStatistic.getSumOfWeights());
    }

    /**
     * Returns the current frequency for the provided integer
     *
     * @param x
     * @return
     */
    public final double getWeight(int x) {
        myTemp.myValue = x;
        Cell c = myCells.get(myTemp);
        if (c == null) {
            return 0;
        } else {
            return c.myWeight;
        }
    }

    /**
     * Gets the proportion of the observations that
     * are equal to the supplied integer
     *
     * @param x
     * @return
     */
    public final double getProportion(int x) {
        myTemp.myValue = x;
        Cell c = myCells.get(myTemp);
        if (c == null) {
            return 0;
        } else {
            double n = getTotalCount();
            return c.myWeight / n;
        }
    }

    /**
     * Interprets the elements of x[] as values
     * and returns an array representing the frequency
     * for each value
     *
     * @param x
     * @return
     */
    public final double[] getWeights(int[] x) {
        double[] f = new double[x.length];
        for (int j = 0; j < x.length; j++) {
            f[j] = getWeight(x[j]);
        }
        return f;
    }

    /**
     * Returns a copy of the cells in a list
     * ordered by the value of each cell, 0th element
     * is cell with smallest value, etc
     *
     * @return
     */
    public final List<Cell> getCellList() {
        SortedSet<Cell> cellSet = getCells();
        List<Cell> list = new ArrayList<Cell>();
        for (Cell c : cellSet) {
            list.add(c.newInstance());
        }
        return list;
    }

    public DEmpiricalCDF createDEmpirical() {
        double[][] valueProportions = this.getValueProportions();
        DEmpiricalCDF d = new DEmpiricalCDF(valueProportions[0],valueProportions[1]);
        return d;
    }

    /**
     * Returns a sorted set containing the cells
     *
     * @return
     */
    protected final SortedSet<Cell> getCells() {
        SortedSet<Cell> cellSet = new TreeSet<Cell>();
        for (Cell c : myCells.keySet()) {
            double n = getTotalCount();
            c.myProportion = c.myWeight / n;
            cellSet.add(c);
        }
        return (cellSet);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Weighted Proportion Tabulation ");
        sb.append(getName());
        sb.append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Number of cells = ");
        sb.append(getNumberOfCells());
        sb.append("\n");
        sb.append("Lower limit = ");
        sb.append(myLowerLimit);
        sb.append("\n");
        sb.append("Upper limit = ");
        sb.append(myUpperLimit);
        sb.append("\n");
        sb.append("Under flow count = ");
        sb.append(myUnderFlowCount);
        sb.append("\n");
        sb.append("Over flow count = ");
        sb.append(myOverFlowCount);
        sb.append("\n");
        sb.append("Total count = ");
        sb.append(getTotalCount());
        sb.append("\n");
        sb.append("#missing = ");
        sb.append(getNumberMissing());
        sb.append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Value \t Weight \t Proportion\n");
        for (Cell c : getCells()) {
            sb.append(c);
        }
        sb.append("----------------------------------------\n");
        sb.append("\n");
        sb.append(myStatistic.toString());
        return (sb.toString());
    }

    public final StatisticAccessorIfc getStatisticAccessor() {
        return myStatistic;
    }

    @Override
    public final double getWeightedSumOfSquares() {
        return myStatistic.getWeightedSumOfSquares();
    }

    @Override
    public final double getWeightedSum() {
        return myStatistic.getWeightedSum();
    }

    @Override
    public final double getWeightedAverage() {
        return myStatistic.getWeightedAverage();
    }

    @Override
    public final double getVonNeumannLag1TestStatistic() {
        return myStatistic.getVonNeumannLag1TestStatistic();
    }

    @Override
    public final double getVonNeumannLag1TestStatisticPValue() {
        return myStatistic.getVonNeumannLag1TestStatisticPValue();
    }

    @Override
    public final double getVariance() {
        return myStatistic.getVariance();
    }

    @Override
    public final double getSumOfWeights() {
        return myStatistic.getSumOfWeights();
    }

    @Override
    public final double getSum() {
        return myStatistic.getSum();
    }

    @Override
    public final double getStandardError() {
        return myStatistic.getStandardError();
    }

    @Override
    public final double getStandardDeviation() {
        return myStatistic.getStandardDeviation();
    }

    @Override
    public final double getSkewness() {
        return myStatistic.getSkewness();
    }

    public final double getObsWeightedSum() {
        return myStatistic.getObsWeightedSum();
    }

    @Override
    public final double getMin() {
        return myStatistic.getMin();
    }

    @Override
    public final double getMax() {
        return myStatistic.getMax();
    }

    @Override
    public final double getLastWeight() {
        return myStatistic.getLastWeight();
    }

    @Override
    public final double getLastValue() {
        return myStatistic.getLastValue();
    }

    @Override
    public final double getLag1Covariance() {
        return myStatistic.getLag1Covariance();
    }

    @Override
    public final double getLag1Correlation() {
        return myStatistic.getLag1Correlation();
    }

    @Override
    public final double getKurtosis() {
        return myStatistic.getKurtosis();
    }

    @Override
    public final double getDeviationSumOfSquares() {
        return myStatistic.getDeviationSumOfSquares();
    }

    @Override
    public final double getCount() {
        return myStatistic.getCount();
    }

    @Override
    public final double getAverage() {
        return myStatistic.getAverage();
    }

    @Override
    public final double getHalfWidth() {
        return myStatistic.getHalfWidth();
    }

    @Override
    public double getHalfWidth(double alpha) {
        return myStatistic.getHalfWidth(alpha);
    }

    @Override
    public final int getLeadingDigitRule(double a) {
        return myStatistic.getLeadingDigitRule(a);
    }

    @Override
    public final Interval getConfidenceInterval() {
        return myStatistic.getConfidenceInterval();
    }

    /**
     * Holds the values and their counts
     */
    public class Cell implements Comparable<Cell> {

        private int myValue;

        private double myWeight;

        private double myProportion = 0.0;

        public Cell() {
            this(0);
        }

        public Cell(int i) {
            myValue = i;
            myWeight = 1;
        }

        public final int getValue() {
            return myValue;
        }

        public final double getWeight() {
            return myWeight;
        }

        public final double getProportion() {
            return myProportion;
        }

        @Override
        public final int compareTo(Cell cell) {
            if (myValue < cell.myValue) {
                return (-1);
            }
            if (myValue > cell.myValue) {
                return (1);
            }
            return 0;
        }

        @Override
        public final boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Cell other = (Cell) obj;
            if (this.myValue != other.myValue) {
                return false;
            }
            return true;
        }

        @Override
        public final int hashCode() {
            return myValue;
        }

        @Override
        public final String toString() {
            return (myValue + " \t " + myWeight + " \t " + myProportion + "\n");
        }

        public Cell newInstance() {
            Cell c = new Cell();
            c.myValue = this.myValue;
            c.myWeight = this.myWeight;
            c.myProportion = this.myProportion;
            return c;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        DUniformRV du = new DUniformRV(1, 10);

        WeightedProportion f = new WeightedProportion();
        // IntegerFrequency f = new IntegerFrequency(1,6);

        /*
        for(int i=1;i<=10;i++){
        double x = du.getValue();
        System.out.println("x = " + x);
        f.collect(x);
        }
         */
        f.collect(du.sample(10000));
        System.out.println("Testing");
        System.out.println(f);

        //int[][] k = f.getValueFrequencies();

        System.out.println(f.createDEmpirical());

    }
}
