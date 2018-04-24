/*
 * Created on Sep 9, 2007
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

import jsl.utilities.math.JSLMath;

/**
 * @author rossetti
 *
 */
public class LinearRateSegment implements RateSegmentIfc {

    /**
     * the slope for the interval
     */
    protected double slope = 0; 

    /** the rate at the lower limit of the interval
     * 
     */
    protected double rLL = 0;

    /**
     * the rate at the upper limit of the interval
     */
    protected double rUL = 0; 

    /**
     * the width of the interval on the cumulative rate scale (crWidth = crUL - crLL)
     */
    protected double crWidth = 0; 

    /**
     * the lower limit of the interval on cumulative rate scale
     */
    protected double crLL = 0; 

    /** the upper limit of the interval on the cumulative rate scale
     * 
     */
    protected double crUL = 0; 

    /**
     * the width of the interval on the time scale (tWidth = tUL - tLL)
     */
    protected double tWidth = 0; 

    /**
     * the lower limit of the interval on the time scale
     */
    protected double tLL = 0; 

    /**
     * the upper limit of the interval on the time scale
     */
    protected double tUL = 0; 

    protected LinearRateSegment(double cumRateLL, double timeLL, double rateLL, double timeUL, 
            double rateUL) {
        tLL = timeLL;
        rLL = rateLL;
        tUL = timeUL;
        rUL = rateUL;
        tWidth = tUL - tLL;
        slope = (rUL - rLL) / tWidth;
        crLL = cumRateLL;
        crUL = crLL + (0.5) * (rUL + rLL) * (tUL - tLL);
        crWidth = crUL - crLL;
    }

    @Override
    public LinearRateSegment newInstance(){
        return new LinearRateSegment(crLL, tLL, rLL, tUL, rUL);
    }
    
    @Override
    public boolean contains(double time) {
        return (tLL <= time) && (time <= tUL);
    }

    public double getSlope() {
        return slope;
    }

    @Override
    public double getRate(double time) {
        if (JSLMath.equal(slope, 0.0)) {
            return rLL;
        } else {
            return (rLL + slope * (time - tLL));
        }
    }

    @Override
    public double getRateAtLowerTimeLimit() {
        return rLL;
    }

    @Override
    public double getRateAtUpperTimeLimit() {
        return rUL;
    }

    @Override
    public double getLowerTimeLimit() {
        return (tLL);
    }

    @Override
    public double getUpperTimeLimit() {
        return (tUL);
    }

    @Override
    public double getTimeWidth() {
        return (tWidth);
    }

    @Override
    public double getCumulativeRateLowerLimit() {
        return (crLL);
    }

    @Override
    public double getCumulativeRateUpperLimit() {
        return (crUL);
    }

    @Override
    public double getCumulativeRateIntervalWidth() {
        return (crWidth);
    }

    @Override
    public double getCumulativeRate(double time) {
        if (JSLMath.equal(slope, 0.0)) {
            return (crLL + rLL * (time - tLL));
        } else {
            return (crLL + rLL * (time - tLL) + (0.5) * slope * (time - tLL) * (time - tLL));
        }
    }

    @Override
    public double getInverseCumulativeRate(double cumRate) {

        if (JSLMath.equal(slope, 0.0)) {
            if (JSLMath.equal(rLL, 0.0)) {
                return Double.NaN;
            } else {
                return (tLL + (cumRate - crLL) / rLL);
            }
        } else {
            double n = 2.0 * (cumRate - crLL);
            double d = rLL + Math.sqrt(rLL * rLL + 2.0 * slope * (cumRate - crLL));
            return (tLL + n / d);
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" [");
        sb.append(rLL);
        sb.append(",");
        sb.append(rUL);
        sb.append("] slope = ");
        sb.append(slope);
        sb.append(" [");
        sb.append(tLL);
        sb.append(",");
        sb.append(tUL);
        sb.append("] width = ");
        sb.append(tWidth);
        sb.append(" [");
        sb.append(crLL);
        sb.append(",");
        sb.append(crUL);
        sb.append("] cr width = ");
        sb.append(crWidth);
        sb.append("\n");
        return (sb.toString());
    }
}
