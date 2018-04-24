/*
 * Created on Mar 25, 2007
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
package ex.montecarlo;

import jsl.utilities.math.FunctionIfc;
import jsl.utilities.random.distributions.Uniform;
import jsl.utilities.statistic.Statistic;

/**
 * @author rossetti
 *
 */
public class CrudeMCIntegral {

    protected Uniform myUniform;

    protected Statistic myStatistic;

    protected FunctionIfc myFunction;

    public CrudeMCIntegral(double lowerLimit, double upperLimit, FunctionIfc function) {
        setFunction(function);
        myUniform = new Uniform(lowerLimit, upperLimit);
        myStatistic = new Statistic("Monte-Carlo Integration");
    }

    public void setLimits(double lowerLimit, double upperLimit) {
        myUniform.setRange(lowerLimit, upperLimit);
    }

    public void setFunction(FunctionIfc function) {
        if (function == null) {
            throw new IllegalArgumentException("The function was null");
        }
        myFunction = function;
    }

    public void runAll(int sampleSize) {
        runAll(sampleSize, true);
    }

    public void runAll(int sampleSize, boolean resetStartStream) {
        if (sampleSize < 1) {
            throw new IllegalArgumentException("The sample size must be >= 1");
        }

        myStatistic.reset();
        if (resetStartStream) {
            myUniform.resetStartStream();
        }

        double r = myUniform.getRange();
        for (int i = 1; i <= sampleSize; i++) {
            double x = myUniform.getValue();
            double y = r * myFunction.fx(x);
            myStatistic.collect(y);
        }
    }

    public void runUntil(double desiredHW) {
        runUntil(desiredHW, 0.95, true);
    }

    public void runUntil(double desiredHW, boolean resetStartStream) {
        runUntil(desiredHW, 0.95, resetStartStream);
    }

    public void runUntil(double desiredHW, double confLevel, boolean resetStartStream) {
        if (desiredHW <= 0) {
            throw new IllegalArgumentException("The desired half-width must be >= 0");
        }

        myStatistic.reset();
        if (resetStartStream) {
            myUniform.resetStartStream();
        }

        double r = myUniform.getRange();
        boolean flag = false;
        while (flag != true) {
            double x = myUniform.getValue();
            double y = r * myFunction.fx(x);
            myStatistic.collect(y);
            if (myStatistic.getCount() > 2) {
                flag = (myStatistic.getHalfWidth(confLevel) < desiredHW);
            }
        }

    }

    public double getEstimate() {
        return (myStatistic.getAverage());
    }

    public Statistic getStatistic() {
        return myStatistic;
    }

    @Override
    public String toString() {
        return (myStatistic.toString());
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        double a = 0.0;
        double b = Math.PI;

        class SinFunc implements FunctionIfc {

            public double fx(double x) {
                return (Math.sin(x));
            }
        }

        SinFunc f = new SinFunc();
        CrudeMCIntegral mc = new CrudeMCIntegral(a, b, f);
        mc.runAll(100);
        System.out.println(mc);

        mc.runAll(100, false);
        System.out.println(mc);

        class F1 implements FunctionIfc {

            public double fx(double x) {
                return (Math.exp(-x * Math.cos(Math.PI * x)));
            }
        }

        mc.setFunction(new F1());
        mc.setLimits(0.0, 1.0);
        mc.runAll(1280);
        System.out.println(mc);
    }
}
