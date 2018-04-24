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
package ex.montecarlo;

import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.distributions.Uniform;
import jsl.utilities.statistic.*;

/**
 *
 */
public class HitOrMiss {

    /**
     *
     */
    public HitOrMiss() {
        super();
    }

    public static void main(String[] args) {

        Uniform u1RN = new Uniform();
//		Uniform u2RN = new Uniform();
        Statistic s = new Statistic();
        u1RN.advanceToNextSubstream();

        for (int i = 1; i <= 1000; i++) {
            double hit = 0.0;
            double u1 = u1RN.getValue();
            double u2 = u1RN.getValue();

            double y = Math.sqrt(1.0 - u1 * u1);
            if (u2 <= y) {
                hit = 1.0;
            }
            s.collect(hit);
        }

        System.out.println(s);
        System.out.println("pi estimate = " + 4.0 * s.getAverage());

        System.out.println("p = " + Normal.stdNormalCDF(1.965));

    }
}
