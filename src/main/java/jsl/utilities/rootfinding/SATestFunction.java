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

import jsl.utilities.math.FunctionIfc;
import jsl.utilities.random.distributions.DistributionIfc;
import jsl.utilities.random.distributions.Normal;
import jsl.utilities.statistic.Statistic;

/**
 * @author rossetti
 *
 */
public class SATestFunction implements FunctionIfc {

	protected DistributionIfc myNoise;
	
	/**
	 * 
	 */
	public SATestFunction() {
		myNoise = new Normal(0.0, 1.0);
	}

	/* (non-Javadoc)
	 * @see jsl.utilities.optimize.FunctionIfc#fx(double)
	 */
	public double fx(double x) {
		return x*x*x + 4.0*x*x -10.0 + myNoise.getValue();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FunctionIfc f = new SATestFunction();
        
		StochasticApproximationRootFinder b = new StochasticApproximationRootFinder(f, 1.0, 2.0);
		
		b.setMaxIterations(10000000);
		
		double x = b.recommendInitialPoint();
		System.out.println("x = " + x);

		double scale = b.recommendScalingFactor(x, 0.02);
		System.out.println("scale = " + scale);

		b.setInitialPoint(x);
		b.setScaleFactor(scale);
		b.setDesiredPrecision(0.00001);
		b.run();
		
		System.out.println(b);

        System.out.println("Evalating the function at the root");
        Statistic s = new Statistic("function at root");
        for(int i=1;i<=100;i++)
            s.collect(f.fx(b.getRoot()));
        System.out.println(s);

	}

}
