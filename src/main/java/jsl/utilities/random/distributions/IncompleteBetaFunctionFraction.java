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

package jsl.utilities.random.distributions;

import jsl.utilities.math.ContinuedFraction;

/**
 *
 */
public class IncompleteBetaFunctionFraction extends ContinuedFraction {

	protected double alpha1;
	protected double alpha2;
	
	public double evaluateFraction(double x, double a1, double a2){
		alpha1 = a1;
		alpha2 = a2;
		setArgument(x);
		evaluate();
		return getResult();
	}
	
     /**
     * Compute the pair numerator/denominator for iteration n.
     * @param n int
     */
    protected void computeFactorsAt(int n){
    	int m = n / 2;
    	int m2 = 2 * m;
    	factors[0] = m2 == n
    					? x * m * ( alpha2 - m)
    								/ ( (alpha1 + m2) * (alpha1 + m2 - 1))
    					: -x * ( alpha1 + m) * (alpha1 + alpha2 + m)
    								/ ( (alpha1 + m2) * (alpha1 + m2 + 1));
    	return;
    }
    
    protected double initialValue(){
    	factors[1] = 1;
    	return 1;
    }

	protected void finalizeIterations() {
		
	}


}
