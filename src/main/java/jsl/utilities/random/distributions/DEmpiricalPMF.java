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
package jsl.utilities.random.distributions;

import java.util.*;
import jsl.utilities.math.*;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RNStreamIfc;
import jsl.utilities.reporting.JSL;

/**
 * Provides a representation for a discrete distribution with
 * arbitrary values and assigned probabilities to each value.
 * Basically, a user-defined probability mass function.
 */
public class DEmpiricalPMF extends Distribution implements DiscreteDistributionIfc {

    /**
     * Holds the list of probability points
     */
    private LinkedList<ProbPoint> myProbabilityPoints;

    /**
     * Keeps a running tabulation of the total amount of probability
     */
    private double myTotalProb;

    /**
     * Used to indicate that all probability points have been added
     * and that the total sums to 1.0, and that the distribution
     * is ready for use.
     * <p>
     */
    private boolean myReadyFlag = false;

    /**
     * Constructs a discrete empirical distribution. The user must
     * provide probabilities and their values.
     */
    public DEmpiricalPMF() {
        this(RNStreamFactory.getDefaultFactory().getStream());
    }

    /**
     * Constructs a discrete empirical distribution. The user must
     * provide probabilities and their values.
     *
     * @param rng RngIfc
     */
    public DEmpiricalPMF(RNStreamIfc rng) {
        super(rng);
        myProbabilityPoints = new LinkedList<ProbPoint>();
        myTotalProb = 0.0;
    }

    /**
     * This constructor takes in an Array of probability points
     * (value, probability), Eg. X[] = {4, 0.2, 2, 0.3, 7,0.5},
     * as the input parameter.
     * <p>
     * An IllegalStateException will be thrown if the user attempts
     * to use the methods and the total probability does not sum up to 1.0
     *
     * @param arrayProb An array holding the value, probability pairs.
     */
    public DEmpiricalPMF(double[] arrayProb) {
        this(RNStreamFactory.getDefaultFactory().getStream());
        setParameters(arrayProb);
        checkTotalProb();
    }

    /**
     * This constructor takes in an Array of probability points
     * (value, probability), Eg. X[] = {4, 0.2, 2, 0.3, 7,0.5},
     * as the input parameter.
     * <p>
     * An IllegalStateException will be thrown if the user attempts
     * to use the methods and the total probability does not sum up to 1.0
     *
     * @param arrayProb An array holding the value, probability pairs.
     * @param rng
     */
    public DEmpiricalPMF(double[] arrayProb, RNStreamIfc rng) {
        this(rng);
        setParameters(arrayProb);
        checkTotalProb();
    }

    /**
     * Assigns the probability associated with each pmf value
     * to the integers starting at 0.
     *
     * @param pmf the probability array. must have valid probability element
     * and must sum to 1.0
     * @return
     */
    public static double[] makePairs(double[] pmf) {
        return makePairs(0, pmf);
    }

    /**
     * Assigns the probability associated with each pmf value
     * to the integers starting at start.
     *
     * @param start place to start assignment
     * @param pmf the probability array. must have valid probability element
     * and must sum to 1.0
     * @return
     */
    public static double[] makePairs(int start, double[] pmf) {
        if (pmf == null) {
            throw new IllegalArgumentException("The probability array was null");
        }
        double sum = 0.0;
        for (double p : pmf) {
            if ((p < 0.0) || (p > 1.0)) {
                throw new IllegalArgumentException("An individual probability was not in [0.0, 1.0]");
            }
            sum = sum + p;
        }
        if (!JSLMath.equal(sum, 1.0)) {
            throw new IllegalArgumentException("The probability array sum is != 1.0");
        }
        double[] pairs = new double[pmf.length * 2];
        for (int i = 0; i < pmf.length; i++) {
            pairs[2 * i] = start;
            pairs[2 * i + 1] = pmf[i];
        }

        return pairs;
    }

    /**
     * Returns a new instance of the random source with the same parameters
     * but an independent generator
     *
     * @return
     */
    @Override
    public final DEmpiricalPMF newInstance() {
        return (new DEmpiricalPMF(getParameters()));
    }

    /**
     * Returns a new instance of the random source with the same parameters
     * with the supplied RngIfc
     *
     * @param rng
     * @return
     */
    @Override
    public final DEmpiricalPMF newInstance(RNStreamIfc rng) {
        return (new DEmpiricalPMF(getParameters(), rng));
    }

    /**
     * Returns a new instance that will supply values based
     * on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    @Override
    public final DEmpiricalPMF newAntitheticInstance() {
        RNStreamIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    @Override
    public final double cdf(double x) {
        checkTotalProb();

        ProbPoint lowpt = null;
        ProbPoint uppt = null;

        lowpt = (ProbPoint) myProbabilityPoints.getFirst();

        if (x < lowpt.getValue()) {
            return (0.0);
        }

        uppt = (ProbPoint) myProbabilityPoints.getLast();

        if (x >= uppt.getValue()) {
            return (1.0);
        }

        ListIterator<ProbPoint> iter = myProbabilityPoints.listIterator();
        while (iter.hasNext()) {
            lowpt = (ProbPoint) iter.next();
            uppt = (ProbPoint) iter.next();
            double lv = lowpt.getValue();
            double uv = uppt.getValue();

            if ((lv <= x) && (x < uv)) {
                break;
            }
        }

        if (lowpt == null) {
            return (Double.NaN);
        } else {
            return (lowpt.getCumProbability());
        }
    }

    @Override
    public final double getMean() {
        checkTotalProb();
        double m = 0.0;
        for (ProbPoint p : myProbabilityPoints) {
            m = m + p.getProbability() * p.getValue();
        }
        return (m);
    }

    @Override
    public final double getVariance() {
        checkTotalProb();
        double m1 = 0.0;
        double m2 = 0.0;
        double v = 0.0;
        double p = 0.0;
        for (ProbPoint pp : myProbabilityPoints) {
            v = pp.getValue();
            p = pp.getProbability();
            m1 = m1 + p * v;
            m2 = m2 + p * v * v;
        }
        return (m2 - m1 * m1);
    }

    /**
     * The probability mass function for this discrete distribution.
     * Returns the same as pdf.
     *
     * @param x The point to get the probability for
     * @return The probability associated with x
     */
    @Override
    public final double pmf(double x) {
        checkTotalProb();
        ProbPoint p = null;
        boolean ifExist = false;

        ListIterator<ProbPoint> iter = myProbabilityPoints.listIterator();
        while (iter.hasNext()) {
            p = (ProbPoint) iter.next();
            if (x == p.getValue()) {
                ifExist = true;
                break;
            }
        }

        if (ifExist == false) {
            return (Double.NaN);
        } else {
            return (p.getProbability());
        }
    }

    private void checkTotalProb() {
        if (myReadyFlag == false) {
            throw new IllegalStateException("Total probability less than 1.0");
        }

        if (myTotalProb < 1.0) {
            throw new IllegalStateException("Total probability less than 1.0");
        }
    }

    /**
     * Returns the pmf as a string.
     *
     * @return A String of probability, value pairs.
     */
    @Override
    public String toString() {
        return (myProbabilityPoints.toString());
    }

    /**
     * Allows the construction of the probability mass function.
     * The user supplies a value and the probability associated with
     * that value.
     * <p>
     * P(X=4) = 0.2
     * P(X=2) = 0.3
     * P(X=7) = 0.5
     * <p>
     * value = 4, probability = 0.2
     * value = 2, probability = 0.3
     * <p>
     * addProbabilityPoint(4.0, 0.2);
     * addProbabilityPoint(2.0, 0.3);
     * addLastProbabiltyPoint(7.0);
     * <p>
     * If addProbabilityPoint() results in total probability &gt; 1.0 then an
     * exception
     * will be thrown.
     * <p>
     * A probability mass point with probability of 0.0 can not be added. This
     * is to
     * prevent problems with integer conversion. For example,
     * <p>
     * addProbabilityPoint(1.0, 1/6)
     * <p>
     * will attempt to add a probability point for the value 1.0 with zero
     * probability.
     * This is because 1/6 is the ratio of two integers and when converted to a
     * double
     * has the value 0.0. This is obviously not the intent of the user. Thus,
     * when
     * a 0.0 is added, this method throws an exception. The proper call would be
     * <p>
     * addProbabilityPoint(1.0, 1.0/6.0)
     *
     * @param value double represents the value associated with the probability
     * @param probability double represents the probability for the value
     */
    public final void addProbabilityPoint(double value, double probability) {

        if (probability == 0.0) {
            JSL.LOGGER.warn("Attempted to add a probability point " + value + " with zero probability");
            return;
        }

        // check if too much probability
        if (myTotalProb + probability > 1.0) {
            throw new IllegalArgumentException("Total probability exceeds 1.0");
        }

        // create the probability point
        ProbPoint np = new ProbPoint(value, probability);

        // the probabilityPoints should be ordered by value
        // from smallest to largest
        boolean insertFlag = false;

        ListIterator<ProbPoint> iter = myProbabilityPoints.listIterator();
        while (iter.hasNext()) {
            ProbPoint p = (ProbPoint) iter.next();
            if (np.getValue() < p.getValue()) {
                iter.previous();
                iter.add(np);
                insertFlag = true;  // remains false unless an insert occurs
                break; // break out of loop
            }
        }

        // was not inserted, place at the end
        if (insertFlag == false) {
            iter.add(np);
        }

        // reset the cumulative probability values
        myTotalProb = 0.0;
        iter = myProbabilityPoints.listIterator();
        while (iter.hasNext()) {
            ProbPoint p = (ProbPoint) iter.next();
            myTotalProb = myTotalProb + p.getProbability();
            // check if we are within the default precision of 1.0
            if (JSLMath.equal(myTotalProb, 1.0)) {
                myTotalProb = 1.0; // make it equal to 1.0
            }
            p.setCumProb(myTotalProb);
        }

    }

    /**
     * Supplies the last value for which the remaining
     * probability should be associated. Used to ensure that the
     * probability distribution sums to 1.0. If this method is not
     * used in conjunction with addProbabilityPoint() to add the last value
     * then an exception will be thrown if any methods are attempted.
     * <p>
     * Allows the construction of the probability mass function.
     * The user supplies a value and the probability associated with
     * that value.
     * <p>
     * P(X=4) = 0.2
     * P(X=2) = 0.3
     * P(X=7) = 0.5
     * <p>
     * addProbabilityPoint(4.0, 0.2);
     * addProbabilityPoint(2.0, 0.3);
     * addLastProbabiltyPoint(7.0);
     *
     * @param value, the value for which 1.0- sum all prob will be associated
     */
    public final void addLastProbabilityPoint(double value) {
        // get remaining probability
        double p = 1.0 - myTotalProb;
        addProbabilityPoint(value, p);
        myReadyFlag = true;
    }

    /**
     * Deletes all the probability points and
     * resets the total probability to zero. In order to
     * use the object, probability points must be readded or
     * the setParameters() method used to properly initialize
     * the object.
     * <p>
     */
    public final void deleteAllProbabilityPoints() {
        myProbabilityPoints.clear();
        myTotalProb = 0.0;
        myReadyFlag = false;
    }

    /**
     * Provides the inverse cumulative distribution function for the
     * distribution
     *
     * @param p The probability to be evaluated for the inverse, p must be [0,1]
     * or
     * an IllegalArgumentException is thrown
     * @return The inverse cdf evaluated at prob
     */
    @Override
    public double invCDF(double p) {
        if ((p < 0.0) || (p > 1.0)) {
            throw new IllegalArgumentException("Probability must be [0,1]");
        }

        checkTotalProb();

        double x = 0.0;
        ListIterator<ProbPoint> iter = myProbabilityPoints.listIterator();

        while (iter.hasNext()) {
            ProbPoint pp = (ProbPoint) iter.next();
            double cp = pp.getCumProbability();
            if (p <= cp) {
                x = pp.getValue();
                break;
            }
        }
        return (x);
    }

    /**
     * Sets the parameters for the distribution. Array of probability points
     * (value, probability), Eg. X[] = [4, 0.2, 2, 0.3, 7,0.5],
     * as the input parameters.
     *
     * @param parameters an array of doubles representing the parameters for
     * the distribution
     */
    @Override
    public void setParameters(double[] parameters) {

        if (parameters.length % 2 != 0) {
            throw new IllegalArgumentException("Input probability array error");
        }

        // add all but the last point
        int i = 0;
        for (i = 0; i < parameters.length - 2; i = i + 2) {
            addProbabilityPoint(parameters[i], parameters[i + 1]);
        }

        // now add the last point
        addLastProbabilityPoint(parameters[i]);
    }

    /**
     * Gets the parameters for the distribution
     *
     * @return Returns an array of the parameters for the distribution
     */
    @Override
    public double[] getParameters() {
        int n = 2 * myProbabilityPoints.size();
        double[] param = new double[n];

        int i = 0;
        ProbPoint p;
        ListIterator<ProbPoint> iter = myProbabilityPoints.listIterator();
        while (iter.hasNext()) {
            p = (ProbPoint) iter.next();
            param[i] = p.getValue();
            param[i + 1] = p.getProbability();
            i = i + 2;
        }

        return (param);
    }

    /**
     * Gets cumulative parameters based on the distribution
     * (v1, cp1, v2, cp2, etc)
     *
     * @return Returns an array of the parameters for a DEmpiricalCDF
     */
    public double[] getCumulativeParameters() {
        int n = 2 * myProbabilityPoints.size();
        double[] param = new double[n];

        int i = 0;
        ProbPoint p;
        ListIterator<ProbPoint> iter = myProbabilityPoints.listIterator();
        while (iter.hasNext()) {
            p = (ProbPoint) iter.next();
            param[i] = p.getValue();
            param[i + 1] = p.getCumProbability();
            i = i + 2;
        }

        return (param);
    }

    /**
     * Create a DEmpiricalCDF class based on cumulative probabilities
     *
     * @return
     */
    public DEmpiricalCDF createDEmpiricalCDF() {
        return (new DEmpiricalCDF(getCumulativeParameters()));
    }

    /**
     * Inner Class
     * <p>
     */
    private final class ProbPoint {

        public ProbPoint(double v, double p) {
            if ((p < 0.0) || (p > 1.0)) {
                throw new IllegalArgumentException("Probability must be in interval [0,1]");
            }

            value = v;
            probability = p;
        }

        public double getValue() {
            return (value);
        }

        public double getProbability() {
            return (probability);
        }

        public double getCumProbability() {
            return (cumProb);
        }

        public void setCumProb(double cp) {
            if ((cp < 0.0) || (cp > 1.0)) {
                throw new IllegalArgumentException("Cumulative probability must be in interval [0,1]");
            }

            cumProb = cp;
        }

        public String toString() {
            String s = "P(x=" + value + ")= " + probability + "\t";
            s = s + "P(x<=" + value + ")= " + cumProb + "\n";
            return (s);
        }

        private double value;

        private double probability;

        private double cumProb;

    }

    public static void main(String args[]) {
        DEmpiricalPMF n2 = new DEmpiricalPMF();
        n2.addProbabilityPoint(1.0, 0.167);
        n2.addProbabilityPoint(2.0, 0.333);
        n2.addProbabilityPoint(3.0, 0.333);
        n2.addLastProbabilityPoint(4.0);

        System.out.println("mean = " + n2.getMean());
        System.out.println("var = " + n2.getVariance());
        System.out.println("pmf");
        System.out.println(n2);

        for (int i = 1; i <= 10; i++) {
            System.out.println("x(" + i + ")= " + n2.getValue());
        }

//		double[] pp = {1.0, 0.7, 2.0, 0.1, 4.0, 0.1, 5.0, 0.1};
//		DEmpirical d = new DEmpirical(pp);
        DEmpiricalPMF d = new DEmpiricalPMF(new double[]{1.0, 0.7, 2.0, 0.1, 4.0, 0.1, 5.0, 0.1});

        System.out.println("mean = " + d.getMean());
        System.out.println("var = " + d.getVariance());
        System.out.println("pmf");
        System.out.println(d);

        for (int i = 1; i <= 5; i++) {
            System.out.println("x(" + i + ")= " + d.getValue());
        }

        System.out.println();
        System.out.println("invCDF(0.2) = " + d.invCDF(0.2));
        System.out.println("invCDF(0.983) = " + d.invCDF(0.983));
        System.out.println("invCDF(" + d.cdf(1.0) + ") = " + d.invCDF(d.cdf(1.0)));

        System.out.println("done");
    }
}
