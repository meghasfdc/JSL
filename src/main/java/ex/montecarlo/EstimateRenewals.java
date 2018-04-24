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
package ex.montecarlo;

import jsl.utilities.random.distributions.Distribution;
import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.statistic.Statistic;

/**
 *
 * @author rossetti
 */
public class EstimateRenewals {

    private Statistic myStat;

    private Distribution myDist;

    private int myMaxIterations = 100000;

    private int myMinIterations = 30;

    private double myDesiredErrorTol = 0.001;

    private double myIntervalLength = 1.0;

    public EstimateRenewals() {
        this(new Exponential(), 1.0, 0.001);
    }

    public EstimateRenewals(Distribution d, double interval){
        this(d, interval, 0.001);
    }
    
    public EstimateRenewals(Distribution d, double interval, double tol) {
        setDistribution(d);
        setInterval(interval);
        setTolerance(tol);
        myStat = new Statistic();
    }

    public final void setConfidenceLevel(double level) {
        myStat.setConfidenceLevel(level);
    }

    public final double getVariance() {
        return myStat.getVariance();
    }

    public final double getAverage() {
        return myStat.getAverage();
    }

    public final void setDistribution(Distribution d) {
        if (d == null) {
            throw new IllegalArgumentException("The supplied distribution was null");
        }
        myDist = d;
    }

    public final void setInterval(double interval) {
        if (interval <= 0.0) {
            throw new IllegalArgumentException("The supplied interval was <= 0");
        }
        myIntervalLength = interval;
    }

    public final double getInterval(){
        return myIntervalLength;
    }

    public final void setTolerance(double tol) {
        if (tol <= 0.0) {
            throw new IllegalArgumentException("The supplied tolerance was <= 0");
        }
        myDesiredErrorTol = tol;
    }

    public final double getTolerance(){
        return myDesiredErrorTol;
    }
    
    public int getMaxIterations() {
        return myMaxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        myMaxIterations = maxIterations;
    }

    public int getMinIterations() {
        return myMinIterations;
    }

    public void setMinIterations(int minIterations) {
        myMinIterations = minIterations;
    }

    public final Statistic getStatistic(){
        return myStat.newInstance();
    }

    @Override
    public String toString(){
        return myStat.toString();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //PoissonProcess();
        EstimateRenewals test = new EstimateRenewals();
        test.setInterval(10.0);
        test.estimate();
        System.out.println(test);

    }

    private double generate() {
        double t = 0.0;
        double n = 0;
        do {
            t = t + myDist.getValue();
            if (t <= myIntervalLength) {
                n = n + 1;
            }
        } while (t <= myIntervalLength);
        return n;
    }

    public void estimate(){
        myStat.reset();
        // always do the min number of samples
        for(int i=1;i<=myMinIterations;i++){
            myStat.collect(generate());
        }
        int k = myMaxIterations - myMinIterations;
        for (int i=1;i<=k;i++){
            double hw = myStat.getHalfWidth();
            if (hw <= myDesiredErrorTol){
                break;
            }else{
                myStat.collect(generate());
            }
        }
    }

}
