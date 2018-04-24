package ex.randomnumbers;

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


import static jsl.utilities.random.rng.RNGTEST.*;

import java.util.ArrayList;
import java.util.List;

import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.rng.MarseRobertsLCG;
import jsl.utilities.random.rng.RandU01Ifc;
import jsl.utilities.reporting.StatisticReporter;
import jsl.utilities.statistic.Statistic;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.statistic.StatisticAccessorIfc;

public class TestRNG {

    public static void main(String[] args) {

        int n = 100; // number of replications
        int m = 5000; // number tested for runs up and correlation test

        testRNG(RNStreamFactory.getDefaultStream(), n, m);

        testRNG(new MarseRobertsLCG(4), n, m);

        testRNG(new PMMLCG(), n, m);

    }

    public static void testMarseRoberts() {

        RandU01Ifc rng = new MarseRobertsLCG(4);

        int df = 4096 - 1;
        double alpha = 0.90;
        double y = approxChiSQValue(df, alpha);
        System.out.println("approx chi square (df = " + df + " alpha = " + alpha + ") = " + y);

//	    double rTestStat = 10.6;
//	    double rhoTestStat = Normal.stdNormalInvCDF(0.9);

//	    int n = 1; // number of replications
        int m = 5000; // number tested for runs up and correlation test

        runsUpTest(rng, m);
    }

    public static void testRNG(RandU01Ifc rng, int n, int m) {

        int df = 4096 - 1;
        double alpha = 0.90;
        double y = approxChiSQValue(df, alpha);
        System.out.println("approx chi square (df = " + df + " alpha = " + alpha + ") = " + y);

        double rTestStat = 10.6;
        double rhoTestStat = Normal.stdNormalInvCDF(0.9);

        List<StatisticAccessorIfc> statistics = new ArrayList<>();

        Statistic s1 = new Statistic("1D");
        statistics.add(s1);
        Statistic s2 = new Statistic("2D");
        statistics.add(s2);
        Statistic s3 = new Statistic("3D");
        statistics.add(s3);
        Statistic s4 = new Statistic("runs up");
        statistics.add(s4);
        Statistic s5 = new Statistic("lag 1");
        statistics.add(s5);
        Statistic s6 = new Statistic("lag 2");
        statistics.add(s6);
        Statistic s7 = new Statistic("lag 3");
        statistics.add(s7);
        Statistic s8 = new Statistic("lag 4");
        statistics.add(s8);
        Statistic s9 = new Statistic("lag 5");
        statistics.add(s9);
        Statistic s10 = new Statistic("lag 6");
        statistics.add(s10);

        for (int i = 1; i <= n; i++) {
            double x1 = chiSquaredTest(rng, 32768, 4096);
            double x2 = serial2DTest(rng, 32768 * 2, 64);
            double x3 = serial3DTest(rng, 32768 * 3, 16);
            double R = runsUpTest(rng, m);
            double a1 = correlationTest(rng, 1, m);
            double a2 = correlationTest(rng, 2, m);
            double a3 = correlationTest(rng, 3, m);
            double a4 = correlationTest(rng, 4, m);
            double a5 = correlationTest(rng, 5, m);
            double a6 = correlationTest(rng, 6, m);

            s1.collect(x1 <= y);
            s2.collect(x2 <= y);
            s3.collect(x3 <= y);
            s4.collect(R <= rTestStat);
            s5.collect((-rhoTestStat <= a1) && (a1 <= rhoTestStat));
            s6.collect((-rhoTestStat <= a2) && (a2 <= rhoTestStat));
            s7.collect((-rhoTestStat <= a3) && (a3 <= rhoTestStat));
            s8.collect((-rhoTestStat <= a4) && (a4 <= rhoTestStat));
            s9.collect((-rhoTestStat <= a5) && (a5 <= rhoTestStat));
            s10.collect((-rhoTestStat <= a6) && (a6 <= rhoTestStat));
        }

        StatisticReporter reporter = new StatisticReporter(statistics);
        System.out.println(reporter.getHalfWidthSummaryReport().toString());

    }
}
