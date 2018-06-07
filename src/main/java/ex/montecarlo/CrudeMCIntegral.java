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
