/*
 * Created on Aug 30, 2007
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

/** Represents a piecewise linear rate function as a sequence of segments.
 *  The rate at the beginning of the segment can be different than
 *  the rate at the end of the segment, with a linear rate over
 *  the duration
 * @author rossetti
 *
 */
public class PiecewiseLinearRateFunction extends PiecewiseRateFunction {

    /** Constructs a PiecewiseLinearRateFunction with the first rate
     *  specified for time zero as zero
     * 
     * 
     * @param duration The duration until the second rate
     * @param secondRate The rate after the supplied duration
     */
    public PiecewiseLinearRateFunction(double duration, double secondRate) {
        this(0.0, duration, secondRate);
    }

    /** Constructs a PiecewiseLinearRateFunction with the first rate
     *  specified for time zero
     * 
     * @param firstRate The rate at time zero
     * @param duration The duration until the second rate
     * @param secondRate The rate after the supplied duration
     */
    public PiecewiseLinearRateFunction(double firstRate, double duration, double secondRate) {
        super();

        addFirstSegment(firstRate, duration, secondRate);

    }

    /** Uses the segments represented by the rate, duration pairs
     *  The rate array must be larger than the duration array, not null, and have at 
     *  least 2 rates.  Any rates 
     *  rate[0] beginning rate of segment 0, duration[0] duration of segment 0
     *  i &gt;=1
     *  rate[i] ending rate of segment i-1, beginning rate of segment i, 
     *  duration[i] duration of segment i
     * 
     * @param durations the durations
     * @param rates the rates
     */
    public PiecewiseLinearRateFunction(double[] durations, double[] rates) {
        super();
        if (durations == null) {
            throw new IllegalArgumentException("durations was null");
        }
        if (rates == null) {
            throw new IllegalArgumentException("rates was null");
        }
        if (rates.length < 2) {
            throw new IllegalArgumentException("rates length was zero");
        }
        if (durations.length == 0) {
            throw new IllegalArgumentException("durations length was zero");
        }
        if (rates.length != (durations.length + 1)) {
            throw new IllegalArgumentException("rate length must be equal to duration length + 1");
        }
        addFirstSegment(rates[0], durations[0], rates[1]);
        for (int i = 1; i < durations.length; i++) {
            addRateSegment(durations[i], rates[i + 1]);
        }
    }

    protected final void addFirstSegment(double firstRate, double duration, double secondRate) {
        if (firstRate < 0.0) {
            throw new IllegalArgumentException("The rate must be >= 0");
        }

        if (firstRate >= Double.POSITIVE_INFINITY) {
            throw new IllegalArgumentException("The rate must be < infinity");
        }

        if (firstRate > myMaxRate) {
            myMaxRate = firstRate;
        }

        if (firstRate < myMinRate) {
            myMinRate = firstRate;
        }

        if (secondRate < 0.0) {
            throw new IllegalArgumentException("The rate must be >= 0");
        }

        if (secondRate >= Double.POSITIVE_INFINITY) {
            throw new IllegalArgumentException("The rate must be < infinity");
        }

        if (secondRate > myMaxRate) {
            myMaxRate = secondRate;
        }

        if (secondRate < myMinRate) {
            myMinRate = secondRate;
        }

        if (duration <= 0.0) {
            throw new IllegalArgumentException("The duration must be > 0");
        }

        if (Double.isInfinite(duration)) {
            throw new IllegalArgumentException("The duration cannot be infinite.");
        }

        LinearRateSegment first = new LinearRateSegment(0.0, 0.0, firstRate, duration, secondRate);

        myRateSegments.add(first);
    }

    /** Returns a copy of the piecewise linear rate function
     * 
     * @return a copy of the piecewise linear rate function
     */
    @Override
    public PiecewiseLinearRateFunction newInstance() {
        return new PiecewiseLinearRateFunction(getDurations(), getRates());
    }

    /** Returns a copy of the piecewise linear rate function
     *  with each rate multiplied by the addFactor
     * 
     * @param factor the addFactor to multiply
     * @return a copy of the piecewise linear rate function
     */
    @Override
    public PiecewiseLinearRateFunction newInstance(double factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("The multiplication addFactor must be > 0");
        }
        double[] rates = getRates();
        for (int i = 0; i < rates.length; i++) {
            rates[i] = rates[i] * factor;
        }
        return new PiecewiseLinearRateFunction(getDurations(), rates);
    }

    /** Allows the construction of the piecewise linear rate function 
     *  The user supplies the knot points on the piecewise linear function by supplying
     *  the rate at the end of the supplied duration via consecutive calls to addRateSegment().  
     * 
     * @param duration must be &gt; 0 and less than Double.POSITIVE_INFINITY
     * @param rate must be &gt;= 0, and less than Double.POSITIVE_INFINITY
     */
    @Override
    public final void addRateSegment(double duration, double rate) {

        if (rate < 0.0) {
            throw new IllegalArgumentException("The rate must be > 0");
        }

        if (rate >= Double.POSITIVE_INFINITY) {
            throw new IllegalArgumentException("The rate must be < infinity");
        }

        if (duration <= 0.0) {
            throw new IllegalArgumentException("The duration must be > 0");
        }

        if (Double.isInfinite(duration)) {
            throw new IllegalArgumentException("The duration cannot be infinite.");
        }

        if (rate > myMaxRate) {
            myMaxRate = rate;
        }

        if (rate < myMinRate) {
            myMinRate = rate;
        }

        int k = myRateSegments.size();
        // get the last interval
        RateSegmentIfc last = myRateSegments.get(k - 1);

        RateSegmentIfc next = new LinearRateSegment(last.getCumulativeRateUpperLimit(),
                last.getUpperTimeLimit(), last.getRateAtUpperTimeLimit(),
                last.getUpperTimeLimit() + duration, rate);

        myRateSegments.add(next);

    }

    /** Get the rates as an array
     * 
     * @return the rates as an array
     */
    @Override
    public double[] getRates() {
        double[] rates = new double[myRateSegments.size() + 1];
        rates[0] = myRateSegments.get(0).getRateAtLowerTimeLimit();
        int i = 1;
        for (RateSegmentIfc s : myRateSegments) {
            LinearRateSegment c = (LinearRateSegment) s;
            rates[i] = c.getRateAtUpperTimeLimit();
            i++;
        }
        return rates;
    }

    /** Get the durations as an array
     * 
     * @return the durations as an array
     */
    @Override
    public double[] getDurations() {
        double[] durations = new double[myRateSegments.size()];
        int i = 0;
        for (RateSegmentIfc s : myRateSegments) {
            durations[i] = s.getTimeWidth();
            i++;
        }
        return durations;
    }

    @Override
    public final boolean contains(double time) {
        return (getTimeRangeLowerLimit() <= time) && (time <= getTimeRangeUpperLimit());
    }

    /** Searches for the interval that the supplied time
     *  falls within.  Returns -1 if no interval is found
     *  
     *  Interval indexing starts at index 0 (i.e. 0 is the first interval, 
     *  silly Java zero based indexing)
     * 
     * @param time the time to look up
     * @return an int representing the interval
     */
    @Override
    public final int findTimeInterval(double time) {

        int k = -1;
        for (RateSegmentIfc i : myRateSegments) {
            k = k + 1;
            if (time <= i.getUpperTimeLimit()) {
                return k;
            }
        }
        return (-1);
    }
}
