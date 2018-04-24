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
 * An iterative process is a general structure managing iterations.
 *
 * * This is based on the IterativeProcess class of Didier Besset in 
 * "Object-Oriented Implementation of Numerical Methods", Morgan-Kaufmann
 */
public abstract class DBHIterativeProcess {
    
    /**
     * Number of iterations performed.
     */
    private int iterations;
    
    /**
     * Maximum allowed number of iterations.
     */
    private int maximumIterations = 100;
    
    /**
     * Desired precision.
     */
    private double desiredPrecision = JSLMath.getDefaultNumericalPrecision();
    
    /**
     * Achieved precision.
     */
    private double precision;
        
    /**
     * Generic constructor.
     */
    public DBHIterativeProcess() {
    }
    
    /**
     * Performs the iterative process.
     * Note: this method does not return anything 
     * Subclass must implement a method to get the result
     */
    public void evaluate() {
        iterations = 0;
        initializeIterations();
        while ( iterations++ < maximumIterations ) {
            precision = evaluateIteration();
            if ( hasConverged() )
                break;
        }
        finalizeIterations();
    }
    
    /**
     * Evaluate the result of the current iteration.
     * @return the estimated precision of the result.
     */
    abstract protected double evaluateIteration();
    
    /**
     * Perform eventual clean-up operations
     * (must be implement by subclass when needed).
     */
    abstract protected void finalizeIterations();

    /**
     * Initializes internal parameters to start the iterative process.
     */
    abstract protected void initializeIterations();
    
    /**
     * Returns the desired precision.
     */
    public double getDesiredPrecision( ) {
        return desiredPrecision;
    }
    
    /**
     * Returns the number of iterations performed.
     */
    public int getIterations() {
        return iterations;
    }
    
    /**
     * Returns the maximum allowed number of iterations.
     */
    public int getMaximumIterations( ) {
        return maximumIterations;
    }
    
    /**
     * Returns the attained precision.
     */
    public double getPrecision() {
        return precision;
    }
    
    /**
     * Check to see if the result has been attained.
     * @return boolean
     */
    public boolean hasConverged() {
        return precision < desiredPrecision;
    }
        
    /**
     * @return double
     * @param epsilon double
     * @param x double
     */
    public double relativePrecision( double epsilon, double x) {
        return x > JSLMath.getDefaultNumericalPrecision()
        ? epsilon / x: epsilon;
    }
    
    /**
     * Defines the desired precision.
     */
    public void setDesiredPrecision( double prec ){
        if ( prec <= 0 )
            throw new IllegalArgumentException("Non-positive precision: "+prec);
        desiredPrecision = prec;
    }

    /**
     * Defines the maximum allowed number of iterations.
     */
    public void setMaximumIterations( int maxIter){
        if ( maxIter < 1 )
            throw new IllegalArgumentException("Non-positive maximum iteration: "+maxIter);
        maximumIterations = maxIter;
    }
}
