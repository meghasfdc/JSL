/*
 * Copyright (c) 2018. Manuel D. Rossetti, manuelrossetti@gmail.com
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

package jsl.utilities.random.arta;

import java.util.TimerTask;

import jsl.utilities.Interval;
import jsl.utilities.math.FunctionIfc;
import jsl.utilities.rootfinding.StochasticApproximationRootFinder;
import jsl.utilities.random.distributions.Distribution;
import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.random.distributions.Poisson;

/**
 * @author rossetti
 *
 */
public class ARTACorrelationFinder extends ARTACorrelationEvaluator {

	public final static double DEFAULT_HWBOUND = 0.001;

	public static final int DEFAULT_INITIAL_PTS = 10;

	public final double DEFAULT_DELTA = 0.1;

	protected double myDesiredCorrelation;

	protected CorrFunction myCorrFunction = new CorrFunction();

	protected StochasticApproximationRootFinder mySARootFinder;

	protected double myMatchingCorrelation = Double.NaN;

	protected double myHWBound = DEFAULT_HWBOUND;
	
	protected double myDelta = DEFAULT_DELTA;

	protected int myInitialPoints = DEFAULT_INITIAL_PTS;

	protected Interval myInterval;

	protected double myLRho;
	
	protected double myURho;
	
	/**
	 * @param distribution
	 */
	public ARTACorrelationFinder(Distribution distribution) {
		this(distribution, 0.0, 1000, 1, false);
	}

	/**
	 * @param distribution
	 * @param lag1
	 */
	public ARTACorrelationFinder(Distribution distribution, double lag1) {
		this(distribution, lag1, 1000, 1, false);
	}

	/**
	 * @param distribution
	 * @param lag1
	 * @param sampleSize
	 */
	public ARTACorrelationFinder(Distribution distribution, double lag1, int sampleSize) {
		this(distribution, lag1, sampleSize, 1, false);
	}

	/**
	 * @param distribution
	 * @param lag1
	 * @param sampleSize
	 * @param numReps
	 */
	public ARTACorrelationFinder(Distribution distribution, double lag1, int sampleSize, int numReps) {
		this(distribution, lag1, sampleSize, numReps, false);
	}

	/**
	 * @param distribution
	 * @param lag1
	 * @param sampleSize
	 * @param numReps
	 * @param antitheticFlag
	 */
	public ARTACorrelationFinder(Distribution distribution, double lag1, int sampleSize, int numReps, boolean antitheticFlag) {
		super(distribution, lag1, sampleSize, numReps, antitheticFlag);
		
	}

	public boolean checkMatchingCorrelation(double desiredCorrelation){
		if ( (desiredCorrelation <= -1) || (desiredCorrelation >= 1))
			throw new IllegalArgumentException("Correlation must be (-1,1)");
		
		double ll = -0.999999;
		double ul =  0.999999;
		
	//	if (desiredCorrelation <= 0.0)
			//ul = 0.0;
	//		ul = 0.1;
	//	else
			//ll = 0.0;
	//		ll = -0.1;
		
		// test if a match is possible
		setCorrelation(ll);
		double rl = estimateCorrelation(myHWBound);
		setCorrelation(ul);
		double ru = estimateCorrelation(myHWBound);

		myLRho = rl;
		myURho = ru;

//		System.out.println("Lower limit = "+ll + " Upper limit = " + ul);
//		System.out.println("LRho = " + myLRho + " URho = " + myURho);
		
		if ( (desiredCorrelation < rl) || (desiredCorrelation > ru))
			return false;

		myDesiredCorrelation = desiredCorrelation;

		if (myInterval == null)
			myInterval = new Interval(ll, ul);
		else
			myInterval.setInterval(ll, ul);

		if (mySARootFinder == null)
			mySARootFinder = new StochasticApproximationRootFinder(myCorrFunction, myInterval);
		else
			mySARootFinder.setInterval(myInterval);	

		return true;
	}
		
	/** Returns the interval of possible correlation values
	 * 
	 * @return
	 */
	public Interval getCorrelationInterval(){
		return new Interval(myLRho, myURho);
	}
	
	public double recommendInitialPoint(int n){
		return mySARootFinder.recommendInitialPoint(n);
	}
	
	public void setNumberOfPointsInInitialSearch(int n){
		if (n <= 1)
			throw new IllegalArgumentException("The number of points in initial search must be > 1");
		myInitialPoints = n;
	}
	
	public double findMatchingCorrelation(){

		myMatchingCorrelation = Double.NaN;
			
		if (mySARootFinder != null){
			double ip = mySARootFinder.recommendInitialPoint(myInitialPoints);
			mySARootFinder.setInitialPoint(ip);
			
			double factor = StochasticApproximationRootFinder.DEFAULT_SCALE_FACTOR;
			factor = mySARootFinder.recommendScalingFactor(myDesiredCorrelation, myDelta);
			mySARootFinder.setScaleFactor(factor);
								
			mySARootFinder.run();

			myMatchingCorrelation = mySARootFinder.getRoot();
		}
					
		return myMatchingCorrelation;
	}
	
	public double getDesiredCorrelation(){
		return myDesiredCorrelation;
	}
	
	public double getMatchingCorrelation(){
		return myMatchingCorrelation;
	}
	
	protected class CorrFunction implements FunctionIfc {

		public double fx(double x) {
			setCorrelation(x);
			return estimateCorrelation() - myDesiredCorrelation;
		}
		
	}

	/** Returns a String representation
	 * @return A String with basic results
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("\nCorrelation Matching");
		sb.append(super.toString());
		sb.append(mySARootFinder);
		return(sb.toString());
	}
	
	
	/**
	 * @param milliseconds
	 * @param timerTask
	 * @see jsl.modeling.IterativeProcess#turnOnTimer(long, java.util.TimerTask)
	 */
	public final void turnOnTimer(long milliseconds, TimerTask timerTask) {
		mySARootFinder.turnOnTimer(milliseconds, timerTask);
	}

	/**
	 * @param milliseconds
	 */
	public void turnOnTimer(long milliseconds) {
		mySARootFinder.turnOnTimer(milliseconds);
	}

	public double getDesiredPrecision() {
		return mySARootFinder.getDesiredPrecision();
	}

	/**
	 * @param prec
	 */
	public void setDesiredPrecision(double prec) {
		mySARootFinder.setDesiredPrecision(prec);
	}

	
	/**
	 * @return
	 */
	public double getStoppingCriteria() {
		return mySARootFinder.getStoppingCriteria();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		test1();

	}
	
	public static void test1(){
		Distribution d = new Exponential();
		double lag1 = 0.8;
		int n = 10000;
		int r = 10;
		boolean flag = false;
		
		ARTACorrelationFinder b = new ARTACorrelationFinder(d, lag1, n, r, flag);
		
		System.out.println("Working ...");
		
		boolean found = b.checkMatchingCorrelation(lag1);
		
		System.out.println("Check for match result: " + found);

//		System.out.println(b);

		if (found == true){
			System.out.println("Searching for match");
			ARTAFinderOutputTask t = new ARTAFinderOutputTask(b);
			b.turnOnTimer(10000, t);
		
			b.setDesiredPrecision(0.005);
			
			b.findMatchingCorrelation();
			System.out.println("Matching correlation: " + b.getMatchingCorrelation());
			
			System.out.println(b);
			System.out.println("Done!");			
		} else {
			System.out.println("No match is possible");
			Interval i = b.getCorrelationInterval();
			System.out.println("The possible correlation interval is: " + i);
		}	
	}
	
	public static void test2(){
		Distribution d = new Poisson(6.64);
		double lag1 = 0.027688858576956063;
		int n = 1000;
		int r = 1;
		boolean flag = false;
		
		ARTACorrelationFinder b = new ARTACorrelationFinder(d, lag1, n, r, flag);
		
		System.out.println("Working ...");
		
		boolean found = b.checkMatchingCorrelation(lag1);
		
		System.out.println("Check for match result: " + found);
		
		//System.out.println(b.recommendInitialPoint(100));
		

		//System.out.println(b);

		if (found == true){
			System.out.println("Searching for match");
			ARTAFinderOutputTask t = new ARTAFinderOutputTask(b);
			b.turnOnTimer(10000, t);
		
			b.setDesiredPrecision(0.005);
			
			b.findMatchingCorrelation();
			System.out.println("Matching correlation: " + b.getMatchingCorrelation());
			
			System.out.println(b);
			System.out.println("Done!");			
		} else {
			System.out.println("No match is possible");
			Interval i = b.getCorrelationInterval();
			System.out.println("The possible correlation interval is: " + i);
		}

	}
	
}
