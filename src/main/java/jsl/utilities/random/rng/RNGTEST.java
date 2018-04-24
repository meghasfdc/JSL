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
package jsl.utilities.random.rng;

import jsl.utilities.random.distributions.Normal;

/**
 *
 */
public class RNGTEST {

    public static final double[][] a = {
        {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
        {0.0, 4529.4, 9044.9, 13568, 18091, 22615, 27892},
        {0.0, 9044.9, 18097, 27139, 36187, 45234, 55789},
        {0.0, 13568, 27139, 40721, 54281, 67852, 83685},
        {0.0, 18091, 36187, 54281, 72414, 90470, 111580},
        {0.0, 22615, 45234, 67852, 90470, 113262, 139476},
        {0.0, 27892, 55789, 83685, 111580, 139476, 172860}
    };

    public static final double[] b = {0.0, 1. / 6., 5. / 24., 11. / 120., 19. / 720., 29. / 5040., 1. / 840.};

    public static double approxChiSQValue(int df, double alpha) {

        if (df <= 0) {
            throw new IllegalArgumentException("The degrees of freedomm must be > 0");
        }

        if ((alpha <= 0) || (alpha >= 1)) {
            throw new IllegalArgumentException("The alpha must be (0,1)");
        }

        double z = Normal.stdNormalInvCDF(1.0 - alpha);

        double t2 = 2.0 / (9 * df);

        double t1 = z * Math.sqrt(t2);

        double t3 = (1.0 - t2 - t1);

        double x = df * t3 * t3 * t3;

        return (x);
    }

    public static double chiSquaredTest(RandU01Ifc rng, long n, int k) {

        if (rng == null) {
            throw new IllegalArgumentException("The RngIfc was null");
        }

        if (n < 0) {
            throw new IllegalArgumentException("The number of random numbers was < 0");
        }

        if (k < 1) {
            throw new IllegalArgumentException("The number of intervals was < 1");
        }

        double[] f = new double[k + 1];

        // tabulate the frequencies
        for (long i = 1; i <= n; i++) {
            double u = rng.randU01();
            int j = (int) Math.ceil(k * u);
            f[j] = f[j] + 1;
        }

        double sum = 0.0;
        double e = n / k;
        for (int j = 1; j <= k; j++) {
            sum = sum + (f[j] - e) * (f[j] - e);
        }
        sum = sum / e;
        return (sum);
    }

    public static double serial2DTest(RandU01Ifc rng, long n, int k) {

        if (rng == null) {
            throw new IllegalArgumentException("The RngIfc was null");
        }

        if (n < 0) {
            throw new IllegalArgumentException("The number of random numbers was < 0");
        }

        if (k < 1) {
            throw new IllegalArgumentException("The number of intervals was < 1");
        }

        double[][] f = new double[k + 1][k + 1];

        // tabulate the frequencies
        for (long i = 1; i <= n; i++) {
            double u1 = rng.randU01();
            int j1 = (int) Math.ceil(k * u1);
            double u2 = rng.randU01();
            int j2 = (int) Math.ceil(k * u2);
            f[j1][j2] = f[j1][j2] + 1;
        }

        double sum = 0.0;
        double e = n / (k * k);
        for (int j1 = 1; j1 <= k; j1++) {
            for (int j2 = 1; j2 <= k; j2++) {
                sum = sum + (f[j1][j2] - e) * (f[j1][j2] - e);
            }
        }
        sum = sum / e;
        return (sum);
    }

    public static double serial3DTest(RandU01Ifc rng, long n, int k) {

        if (rng == null) {
            throw new IllegalArgumentException("The RngIfc was null");
        }

        if (n < 0) {
            throw new IllegalArgumentException("The number of random numbers was < 0");
        }

        if (k < 1) {
            throw new IllegalArgumentException("The number of intervals was < 1");
        }

        double[][][] f = new double[k + 1][k + 1][k + 1];

        // tabulate the frequencies
        for (long i = 1; i <= n; i++) {
            double u1 = rng.randU01();
            int j1 = (int) Math.ceil(k * u1);
            double u2 = rng.randU01();
            int j2 = (int) Math.ceil(k * u2);
            double u3 = rng.randU01();
            int j3 = (int) Math.ceil(k * u3);
            f[j1][j2][j3] = f[j1][j2][j3] + 1;
        }

        double sum = 0.0;
        double e = n / (k * k * k);

        for (int j1 = 1; j1 <= k; j1++) {
            for (int j2 = 1; j2 <= k; j2++) {
                for (int j3 = 1; j3 <= k; j3++) {
                    sum = sum + (f[j1][j2][j3] - e) * (f[j1][j2][j3] - e);
                }
            }
        }

        sum = sum / e;
        return (sum);
    }

    public static double correlationTest(RandU01Ifc rng, int lag, long n) {

        if (rng == null) {
            throw new IllegalArgumentException("The RngIfc was null");
        }

        if (lag <= 0) {
            throw new IllegalArgumentException("The lag <= 0");
        }

        int h = (int) Math.floor((n - 1) / lag) - 1;

        if (h <= 0) {
            throw new IllegalArgumentException("(int)Math.floor((n-1)/lag) - 1 <= 0");
        }

        double sum = 0.0;
        double u1 = 0.0;
        double u2 = 0.0;
        u1 = rng.randU01();
        for (int k = 0; k <= h; k++) {
            for (int j = 1; j <= lag; j++) {
                u2 = rng.randU01();
            }
            sum = sum + u1 * u2;
            u1 = u2;
        }
        double rho = (12.0 / (h + 1.0)) * sum - 3.0;
        double varrho = (13.0 * h + 7.0) / ((h + 1) * (h + 1));
        double asubj = rho / Math.sqrt(varrho);
        return (asubj);
    }

    public static double runsUpTest(RandU01Ifc rng, long n) {

        if (rng == null) {
            throw new IllegalArgumentException("The RngIfc was null");
        }

        if (n < 0) {
            throw new IllegalArgumentException("The number of random numbers was < 0");
        }

        double[] r = new double[7];
        double A = rng.randU01();

        int J = 1;
        for (long i = 2; i <= n; i++) {
            double B = rng.randU01();
            if (A >= B) {
                J = Math.min(J, 6);
                r[J] = r[J] + 1;
                J = 1;
            } else {
                J = J + 1;
            }
            //Replace A by B
            A = B;
        }
        J = Math.min(J, 6);
        r[J] = r[J] + 1;

        //Compute R
        double R = 0;
        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 6; j++) {
                R = R + a[i][j] * (r[i] - n * b[i]) * (r[j] - n * b[j]);
            }
        }

        return (R / n);
    }
}
