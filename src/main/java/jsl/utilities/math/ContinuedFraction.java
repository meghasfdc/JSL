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
package jsl.utilities.math;

/**
 * Continued fraction
 * 
 */
public abstract class ContinuedFraction extends DBHIterativeProcess {
	
	/**
	 * Best approximation of the fraction.
	 */
	private double result;
	/**
	 * Fraction's argument.
	 */
	protected double x;
	/**
	 * Fraction's accumulated numerator.
	 */
	private double numerator;
	/**
	 * Fraction's accumulated denominator.
	 */
	private double denominator;
	/**
	 * Fraction's next factors.
	 */
	protected double[] factors = new double[2];
	
	/**
	 * Compute the pair numerator/denominator for iteration n.
	 * @param n int
	 */
	protected abstract void computeFactorsAt(int n);
	
	/**
	 * @return double
	 */
	public double evaluateIteration(){
		computeFactorsAt(getIterations());
		denominator = 1 / limitedSmallValue( factors[0] * denominator
				+ factors[1]);
		numerator = limitedSmallValue( factors[0] / numerator + factors[1]);
		double delta = numerator * denominator;
		result = result*delta;
		return Math.abs(delta - 1);
	}
	
	/**
	 * @return double
	 */
	public double getResult( ){
		return result;
	}
	
	public void initializeIterations(){
		numerator = limitedSmallValue(initialValue());
		denominator = 0;
		result = numerator;
		return;
	}
	
	/**
	 * @return double
	 */
	protected abstract double initialValue();
	
	/**
	 * Protection against small factors.
	 * @return double
	 * @param r double
	 */
	private double limitedSmallValue( double r){
		if (Math.abs( r) < JSLMath.getSmallNumber())
			return(JSLMath.getSmallNumber());
		else
			return(r);
	}
	
	/**
	 * @param r double	the value of the series argument.
	 */
	public void setArgument(double r){
		x = r;
		return;
	}
}