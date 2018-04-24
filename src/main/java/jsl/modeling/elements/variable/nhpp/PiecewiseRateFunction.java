/*
 * Created on Sep 13, 2007
 * Copyright (c) 2007, Manuel D. Rossetti (rossetti@uark.edu)
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
package jsl.modeling.elements.variable.nhpp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rossetti
 *
 */
public abstract class PiecewiseRateFunction implements InvertibleCumulativeRateFunctionIfc {

    protected List<RateSegmentIfc> myRateSegments;

    protected double myMaxRate = Double.NEGATIVE_INFINITY;

    protected double myMinRate = Double.POSITIVE_INFINITY;

    public PiecewiseRateFunction() {

        myRateSegments = new ArrayList<RateSegmentIfc>();

    }

    /** Adds a rate segment to the function
     * 
     * @param duration the duration
     * @param rate the rate
     */
    abstract public void addRateSegment(double duration, double rate);

    /** Searches for the interval that the supplied time
     *  falls within.  Returns -1 if no interval is found
     *  
     *  Interval indexing starts at index 0 (i.e. 0 is the first interval, 
     *  silly Java zero based indexing)
     * 
     * @param time the time to look for
     * @return the index of the interval
     */
    abstract public int findTimeInterval(double time);

    /** Returns a copy of the piecewise  rate function
     * 
     * @return a copy of the piecewise  rate function
     */
    abstract public PiecewiseRateFunction newInstance();

    /** Returns a copy of the piecewise  rate function
     *  with each rate multiplied by the addFactor
     * 
     * @param factor rate multiplied by the addFactor
     * @return a copy of the piecewise
     */
    abstract public PiecewiseRateFunction newInstance(double factor);

    /** Get the rates as an array
     * 
     * @return the rates as an array
     */
    abstract public double[] getRates();

    /** Get the durations as an array
     * 
     * @return  the durations as an array
     */
    abstract public double[] getDurations();

    @Override
    public final double getTimeRangeLowerLimit() {
        return myRateSegments.get(0).getLowerTimeLimit();
    }

    @Override
    public final double getTimeRangeUpperLimit() {
        int k = myRateSegments.size();
        // get the last interval
        RateSegmentIfc last = myRateSegments.get(k - 1);
        return last.getUpperTimeLimit();
    }

    @Override
    public final double getCumulativeRateRangeLowerLimit() {
        return myRateSegments.get(0).getCumulativeRateLowerLimit();
    }

    @Override
    public final double getCumulativeRateRangeUpperLimit() {
        int k = myRateSegments.size();
        // get the last interval
        RateSegmentIfc last = myRateSegments.get(k - 1);
        return last.getCumulativeRateUpperLimit();
    }

    /** Returns the rate for the supplied time
     * 
     * @param time the time to evaluate
     * @return the rate for the supplied time
     */
    @Override
    public final double getRate(double time) {

        if (time < 0.0) {
            return (0.0);
        }

        int k = findTimeInterval(time);

        if (k == -1) {
            throw new IllegalArgumentException("The time = " + time + " exceeds range of the function");
        }

        RateSegmentIfc i = myRateSegments.get(k);
        return (i.getRate(time));
    }

    /** Returns the value of the cumulative rate function at the supplied time
     * 
     * @param time the time to evaluate
     * @return the value of the cumulative rate function
     */
    @Override
    public final double getCumulativeRate(double time) {
        if (time < 0.0) {
            return (0.0);
        }

        int k = findTimeInterval(time);

        if (k == -1) {
            throw new IllegalArgumentException("The time = " + time + " exceeds range of the function");
        }

        RateSegmentIfc i = myRateSegments.get(k);

        return (i.getCumulativeRate(time));

    }

    /** Returns the value of the inverse cumulative rate function at the supplied rate
     *  The value returned is interpreted as a time
     * 
     * @param rate the rate
     * @return the value of the inverse cumulative rate function 
     */
    @Override
    public final double getInverseCumulativeRate(double rate) {

        if (rate <= 0.0) {
            return (0.0);
        }

        int k = findCumulativeRateInterval(rate);

        if (k == -1) {
            throw new IllegalArgumentException("The rate = " + rate + " exceeds range of the inverse function");
        }

        RateSegmentIfc i = myRateSegments.get(k);

        return (i.getInverseCumulativeRate(rate));

    }

    /** Searches for the interval that the supplied cumulative rate
     *  falls within.  Returns -1 if no interval is found
     *  
     *  Interval indexing starts at index 0 (i.e. 0 is the first interval, 
     *  silly Java zero based indexing)
     * 
     * @param cumRate the rate
     * @return the interval that the supplied cumulative rate
     */
    public int findCumulativeRateInterval(double cumRate) {

        int k = -1;
        for (RateSegmentIfc i : myRateSegments) {
            k = k + 1;
            if (cumRate <= i.getCumulativeRateUpperLimit()) {
                return k;
            }
        }
        return (-1);
    }

    /** Returns the rate segment at index k
     * Interval indexing starts at index 0 (i.e. 0 is the first interval, 
     *  silly Java zero based indexing)
     * @param k the index
     * @return the rate segment at index k
     */
    public RateSegmentIfc getRateSegment(int k) {
        return myRateSegments.get(k);
    }

    /** Returns the number of segments
     * 
     * @return the number of segments
     */
    public int getNumberSegments() {
        return myRateSegments.size();
    }

    @Override
    public final double getMaximum() {
        return myMaxRate;
    }

    @Override
    public final double getMinimum() {
        return myMinRate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (RateSegmentIfc i : myRateSegments) {
            sb.append(i.toString());
        }
        return (sb.toString());

    }
}
