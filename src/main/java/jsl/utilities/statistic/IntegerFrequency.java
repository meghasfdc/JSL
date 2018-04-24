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
import jsl.utilities.random.distributions.DEmpiricalCDF;
import jsl.utilities.random.distributions.DEmpiricalPMF;
import jsl.utilities.random.distributions.DUniform;

/** This class tabulates the frequency associated with
 *  the integers presented to it via the collect() method
 *  Every value presented is interpreted as an integer
 *  For every value presented a count is maintained.
 *  There could be space/time performance issues if
 *  the number of different values presented is large.
 *
 *  This class can be useful for tabulating a
 *  discrete histogram over the values (integers) presented.
 *
 * @author rossetti
 */
public class IntegerFrequency extends AbstractStatistic {

    /**
     * A Cell represents a value, count pairing
     */
    protected Map<Cell, Cell> myCells;

    /** Collects statistical information
     */
    protected Statistic myStatistic;

    /**
     * Used as a temporary cell during tabulation
     */
    protected Cell myTemp;

    /** The smallest value allowed.  Any
     *  values &lt; to this value will be counted
     *  in the underflow count
     *
     */
    protected int myLowerLimit;

    /** The largest value allowed.  Any
     *  values &gt; to this value will be counted
     *  in the overflow count
     *
     */
    protected int myUpperLimit;

    /** Counts of values located below first bin.
     */
    protected int myUnderFlowCount;

    /** Counts of values located above last bin.
     */
    protected int myOverFlowCount;

    public IntegerFrequency() {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, null);
    }

    public IntegerFrequency(String name) {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, name);
    }

    public IntegerFrequency(int lowerLimit, int upperLimit) {
        this(lowerLimit, upperLimit, null);
    }

    public IntegerFrequency(int lowerLimit, int upperLimit, String name) {
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

    /** Tabulates the count of the number of x's
     *  presented.  Weight represents the frequency
     *  for the x
     *
     * @param x
     * @param weight
     */
    @Override
    public boolean collect(double x, double weight) {
        if (isTurnedOff()){
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
        int f = (int) weight;

        myStatistic.collect(i, f);

        if (i < myLowerLimit) {
            myUnderFlowCount = myUnderFlowCount + f;
            return true;
        }

        if (i > myUpperLimit) {
            myOverFlowCount = myOverFlowCount + f;
            return true;
        }

        // myLowerLimit <= x <= myUpperLimit

        myTemp.myValue = i;
        Cell c = myCells.get(myTemp);
        if (c == null) {
            c = new Cell(i);
            myCells.put(c, c);
        } else {
            c.myCount = c.myCount + f;
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

    /** The number of observations that fell below the first bin's lower limit
     *
     * @return
     */
    public final int getUnderFlowCount() {
        return (myUnderFlowCount);
    }

    /** The number of observations that fell past the last bin's upper limit
     *
     * @return
     */
    public final int getOverFlowCount() {
        return (myOverFlowCount);
    }

    /** Returns an array of size getNumberOfCells() containing
     *  the values increasing by value, null if no values
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

    /** Returns an array of size getNumberOfCells() containing
     *  the frequencies by value, null if no values
     *
     * @return
     */
    public final int[] getFrequencies() {
        if (myCells.isEmpty()) {
            return null;
        }
        SortedSet<Cell> cellSet = getCells();
        int[] v = new int[myCells.size()];
        int i = 0;
        for (Cell c : cellSet) {
            v[i] = c.myCount;
            i++;
        }
        return v;
    }

        /** Returns an array of size getNumberOfCells() containing
     *  the frequencies by value, null if no values
     *
     * @return
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

    /** Returns the cumulative frequency up to an including i
     *
     * @param i
     * @return
     */
    public final int getCumulativeFrequency(int i) {
        if (myCells.isEmpty()) {
            return 0;
        }
        SortedSet<Cell> cellSet = getCells();
        int sum = 0;
        for (Cell c : cellSet) {
            if (c.myValue <= i) {
                sum = sum + c.myCount;
            } else {
                break;
            }
        }
        return sum;
    }

    /** Returns the cumulative proportion up to an including i
     *
     * @param i
     * @return
     */
    public final double getCumulativeProportion(int i) {
        if (myCells.isEmpty()) {
            return 0;
        }
        double n = getTotalCount();
        return (getCumulativeFrequency(i) / n);
    }

    /** Returns a n by 2 array of value, frequency
     *  pairs where n = getNummberOfCells()
     *
     * @return
     */
    public final int[][] getValueFrequencies() {
        if (myCells.isEmpty()) {
            return null;
        }
        SortedSet<Cell> cellSet = getCells();
        int[][] v = new int[myCells.size()][2];
        int i = 0;
        for (Cell c : cellSet) {
            v[i][0] = c.myValue;
            v[i][1] = c.myCount;
            i++;
        }
        return v;
    }

    /** Returns a n by 2 array of value, proportion pairs
     *  where n = getNumberOfCells()
     * 
     * @return 
     */
    public final double[][] getValueProportions(){
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
    
    /** Returns a n by 2 array of value, cumulative proportion pairs
     *  where n = getNumberOfCells()
     * 
     * @return 
     */
    public final double[][] getValueCumulativeProportions(){
        if (myCells.isEmpty()) {
            return null;
        }
        SortedSet<Cell> cellSet = getCells();
        double[][] v = new double[myCells.size()][2];
        int i = 0;
        double sum = 0.0;
        for (Cell c : cellSet) {
            v[i][0] = c.myValue;
            sum = sum + c.myProportion;
            v[i][1] = sum;
            i++;
        }
        return v;
    }

    /** Returns the number of cells tabulated
     *
     * @return
     */
    public final int getNumberOfCells() {
        return myCells.size();
    }

    /** The total count associated with the values
     *
     * @return
     */
    public final int getTotalCount() {
        return ((int) myStatistic.getSumOfWeights());
    }

    /** Returns the current frequency for the provided integer
     * 
     * @param x
     * @return
     */
    public final int getFrequency(int x) {
        myTemp.myValue = x;
        Cell c = myCells.get(myTemp);
        if (c == null) {
            return 0;
        } else {
            return c.myCount;
        }
    }

    /** Gets the proportion of the observations that
     *  are equal to the supplied integer
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
            return c.myCount / n;
        }
    }

    /** Interprets the elements of x[] as values
     *  and returns an array representing the frequency
     *  for each value
     *
     * @param x
     * @return
     */
    public final int[] getFrequencies(int[] x) {
        int[] f = new int[x.length];
        for (int j = 0; j < x.length; j++) {
            f[j] = getFrequency(x[j]);
        }
        return f;
    }

    /** Returns a copy of the cells in a list
     *  ordered by the value of each cell, 0th element
     *  is cell with smallest value, etc
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

    public DEmpiricalPMF createDEmpirical(){
        DEmpiricalPMF d = new DEmpiricalPMF();
        double[][] x = getValueProportions();
        for(int j = 0; j<x.length-1;j++){
            d.addProbabilityPoint(x[j][0], x[j][1]);
        }
        d.addLastProbabilityPoint(x[x.length-1][0]);
        return d;
    }
    
    public DEmpiricalCDF createDEmpiricalCDF(){
        // form the array of parameters
        double[] pm = new double[2*getNumberOfCells()];
        double[][] x = getValueCumulativeProportions();
        int k = 0;
        for(int j = 0; j<x.length;j++){
            pm[k] = x[j][0];
            pm[k+1] = x[j][1];
            k = k + 2;
        }
        return(new DEmpiricalCDF(pm));
    }

    /** Returns a sorted set containing the cells
     *
     * @return
     */
    protected final SortedSet<Cell> getCells() {
        SortedSet<Cell> cellSet = new TreeSet<Cell>();
        for (Cell c : myCells.keySet()) {
            double n = getTotalCount();
            c.myProportion = c.myCount / n;
            cellSet.add(c);
        }
        return (cellSet);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Frequency Tabulation ").append(getName()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Number of cells = ").append(getNumberOfCells()).append("\n");
        sb.append("Lower limit = ").append(myLowerLimit).append("\n");
        sb.append("Upper limit = ").append(myUpperLimit).append("\n");
        sb.append("Under flow count = ").append(myUnderFlowCount).append("\n");
        sb.append("Over flow count = ").append(myOverFlowCount).append("\n");
        sb.append("Total count = ").append(getTotalCount()).append("\n");
        sb.append("#missing = ").append(getNumberMissing()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Value \t Count \t Proportion\n");
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
    public double getHalfWidth(double alpha) {
        return myStatistic.getHalfWidth(alpha);
    }

    @Override
    public final double getHalfWidth() {
        return myStatistic.getHalfWidth();
    }

    @Override
    public final int getLeadingDigitRule(double a) {
        return myStatistic.getLeadingDigitRule(a);
    }

    /** Holds the values and their counts
     *
     */
    public class Cell implements Comparable<Cell> {

        private int myValue;

        private int myCount;

        private double myProportion = 0.0;

        public Cell() {
            this(0);
        }

        public Cell(int i) {
            myValue = i;
            myCount = 1;
        }

        public final int getValue() {
            return myValue;
        }

        public final int getCount() {
            return myCount;
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
            return (myValue + " \t " + myCount + " \t " + myProportion + "\n");
        }

        public Cell newInstance() {
            Cell c = new Cell();
            c.myValue = this.myValue;
            c.myCount = this.myCount;
            c.myProportion = this.myProportion;
            return c;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        DUniform du = new DUniform(1, 10);

        IntegerFrequency f = new IntegerFrequency();
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
