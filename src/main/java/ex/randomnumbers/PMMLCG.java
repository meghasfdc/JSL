package ex.randomnumbers;

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
import jsl.utilities.random.rng.RandU01Ifc;

/**
 * Random is a PMMLCG generator that returns a pseudo-random real number
 * uniformly distributed between 0.0 and 1.0.  The period is (m - 1)
 * where m = 2,147,483,647 and the smallest and largest possible values
 * are (1 / m) and 1 - (1 / m) respectively.
 *
 * @author rossetti
 */
public class PMMLCG implements RandU01Ifc {

    private long myModulus = 2147483647;

    private long myMultiplier = 48271;

    /* initial seed, use 0 < DEFAULT < MODULUS   */
    private long myDefaultSeed = 123456789L;

    /* seed is the state of the generator        */
    private long mySeed = myDefaultSeed;

    private long Q;

    private long R;

    private double myPrevU;

    public PMMLCG() {
        this(123456789L);
    }

    public PMMLCG(long seed) {
        setSeed(seed);
        Q = myModulus / myMultiplier;
        R = myModulus % myMultiplier;
        myPrevU = Double.NaN;
    }

    public final long getDefaultSeed() {
        return myDefaultSeed;
    }

    public double randU01() {
        long t;

        t = myMultiplier * (mySeed % Q) - R * (mySeed / Q);
        if (t > 0) {
            mySeed = t;
        } else {
            mySeed = t + myModulus;
        }
        double u = (double) mySeed / myModulus;
        myPrevU = u;
        return (u);
    }

    public double getPrevU01() {
        return myPrevU;
    }

    public double getAntitheticValue() {
        return 1.0 - myPrevU;
    }

    public final void setSeed(long seed) {
        if (seed <= 0L) {
            throw new IllegalArgumentException("The seed must be > 0");
        }
        if (seed >= myModulus) {
            throw new IllegalArgumentException("The seed must be < " + myModulus);
        }

        mySeed = seed;

    }

    public final long getSeed() {
        return mySeed;
    }

    public static void main(String[] args) {

        PMMLCG r = new PMMLCG();

        for (int i = 1; i <= 10; i++) {
            System.out.println(r.randU01());
        }


    }
}
