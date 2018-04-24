/*
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
package jsl.utilities.rootfinding;

import jsl.utilities.Interval;
import jsl.utilities.math.FunctionIfc;
import jsl.utilities.math.JSLMath;


/**
 * @author rossetti
 *
 */
public class IPBisectionRootFinder extends IPRootFinder {

    /**
     * Value at which the function's value is negative.
     */
    protected double xNeg;
    
    /**
     * Value at which the function's value is positive.
     */
    protected double xPos;
    
    /** The value of the function at xNeg
     */
    protected double fNeg;
    
    /** The value of the function at xPos
     */
    protected double fPos;

	/**
     * Desired precision.
     */
    protected double myDesiredPrecision = JSLMath.getDefaultNumericalPrecision();

	/**
	 * @param func
	 * @param interval
	 */
	public IPBisectionRootFinder(FunctionIfc func, Interval interval) {
    	this(func, ((interval.getUpperLimit() + interval.getLowerLimit())/2.0), interval.getLowerLimit(), interval.getUpperLimit());
    }

	/**
	 * @param func
	 * @param lower
	 * @param upper
	 */
	public IPBisectionRootFinder(FunctionIfc func, double lower, double upper) {
		super(func, lower, upper);
		setInitialPoint((lower + upper)/2.0);
	}
	
	/**
	 * @param func
	 * @param lower
	 * @param upper
	 */
	public IPBisectionRootFinder(FunctionIfc func, double initialPt, double lower, double upper) {
		super(func, lower, upper);
		setInitialPoint(initialPt);
	}

	/**
	 * @return the xNeg
	 */
	public double getXNeg() {
		return xNeg;
	}

	/**
	 * @return the xPos
	 */
	public double getXPos() {
		return xPos;
	}

	/**
	 * @return the fNeg
	 */
	public double getFNeg() {
		return fNeg;
	}

	/**
	 * @return the fPos
	 */
	public double getFPos() {
		return fPos;
	}

	public double getPrecision(){
		return (Math.abs( xPos - xNeg));
	}

    /**
     * Returns the desired precision.
     */
    public double getDesiredPrecision( ) {
        return myDesiredPrecision;
    }
    
    /**
     * Defines the desired precision.
     */
    public void setDesiredPrecision( double prec ){
        if ( prec <= 0 )
            throw new IllegalArgumentException("Non-positive precision: "+prec);
        myDesiredPrecision = prec;
    }
    
	/* (non-Javadoc)
	 * @see jsl.modeling.IterativeProcess#hasNext()
	 */
	@Override
	protected boolean hasNext() {
		if (hasConverged() || (myStepCounter >= myMaxIterations))
			return false;
		else
			return true;
	}
	
    /**
     * Check to see if the result has been attained.
     * @return boolean
     */
    public boolean hasConverged() {
        return getPrecision() < myDesiredPrecision;
    }
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("Desired precision: " + getDesiredPrecision() + "\n");
		sb.append("Actual precision: " + getPrecision() + "\n");
		sb.append("Converged? " + hasConverged() + "\n");
		sb.append("xNeg: " + xNeg + "\n");
		sb.append("fNeg: " + fNeg + "\n");
		sb.append("xPos: " + xPos + "\n");
		sb.append("fPos: " + fPos + "\n");
		sb.append(myCurrentStep);
		return(sb.toString());
	}
	
    protected void initializeIterations(){
    	super.initializeIterations();
		double xLower = getLowerLimit();
		double xUpper = getUpperLimit();
	    
		double fL = f.fx(xLower);
		double fU = f.fx(xUpper);
		
		if (fL*fU > 0.0)
			throw new IllegalArgumentException("There is no root in the provided interval");
		
		if (fL < 0.0){
			fNeg = fL;
			fPos = fU;
			xNeg = xLower;
			xPos = xUpper;
		} else {
			fNeg = fU;
			fPos = fL;
			xPos = xLower;
			xNeg = xUpper;	
		}
    }
 
	/* (non-Javadoc)
	 * @see jsl.modeling.IterativeProcess#next()
	 */
	@Override
	protected RootFinderStep next() {
		
		if (!hasNext())
			return null;

		setRoot(( xPos + xNeg) * 0.5);
		
		return myCurrentStep;
	}
	
	/* (non-Javadoc)
	 * @see jsl.modeling.IterativeProcess#runStep()
	 */
	@Override
	protected void runStep() {
		
		myCurrentStep = next();

        if ( getFunctionAtRoot() > 0 )
            xPos = getRoot();
        else
            xNeg = getRoot();
        		
		if (mySaveStepOption == true){
			RootFinderStep s = new RootFinderStep();
			s.myX = getRoot();
			s.myFofX = getFunctionAtRoot();
			mySteps.add(s);
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		FunctionIfc f = new FunctionIfc() { public double fx(double x){return x*x*x + 4.0*x*x -10.0;}};
		
		IPBisectionRootFinder b = new IPBisectionRootFinder(f, 1.0, 2.0);
		
		b.run();
		
		System.out.println(b);
		
	}

}
