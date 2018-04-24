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
package jsl.utilities.statistic;

import jsl.utilities.GetValueIfc;

/**  Continually gets the value of the supplied GetValueIfc in the run() until
 * the supplied sampling half-width requirement is met or the default maximum 
 * number of iterations is reached, whichever comes first.
 *
 * @author rossetti
 */
public class HalfWidthSequentialSampler {

    protected double myDefaultDesiredHW = 0.001;

    protected long myDefaultMaxIterations = 10000;

    protected Statistic myStatistic;

    public HalfWidthSequentialSampler() {
        this(null);
    }

    public HalfWidthSequentialSampler(String name) {
        myStatistic = new Statistic(name);
    }

    public final double getDefaultDesiredHalfWidth() {
        return myDefaultDesiredHW;
    }

    public final long getDefaultMaxIterations() {
        return myDefaultMaxIterations;
    }

    public void setConfidenceLevel(double alpha) {
        myStatistic.setConfidenceLevel(alpha);
    }

    public final void setDefaultDesiredHalfWidth(double hw) {
        if (hw <= 0.0) {
            throw new IllegalArgumentException("The desired half-width must be > 0");
        }
        myDefaultDesiredHW = hw;
    }

    public final void setDefaultMaxIterations(long maxIter) {
        if (maxIter <= 1) {
            throw new IllegalArgumentException("The maximum number of iterations must be > 1");
        }
        myDefaultMaxIterations = maxIter;
    }

    public StatisticAccessorIfc getStatistic() {
        return myStatistic;
    }

    public final boolean run(GetValueIfc v) {
        return run(v, getDefaultDesiredHalfWidth(), getDefaultMaxIterations());
    }

    public final boolean run(GetValueIfc v, double hw) {
        return run(v, hw, getDefaultMaxIterations());
    }

    public boolean run(GetValueIfc v, double dhw, long maxIter) {
        myStatistic.reset();
        double hw = Double.POSITIVE_INFINITY;
        boolean converged = false;
        do {
            myStatistic.collect(v);
            if (myStatistic.getCount() > 1) {
                hw = myStatistic.getHalfWidth();
                if ((hw > 0.0) && (hw <= dhw)) {
                    converged = true;
                    break;
                }
            }
        } while (myStatistic.getCount() < maxIter);
        return converged;
    }
}
