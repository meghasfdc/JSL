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
package jsl.utilities.random.rvariable;

import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/** Allows for the generation of bivariate normal
 *  random variables
 *
 * @author rossetti
 */
public class BivariateLogNormalRV extends AbstractMVRVariable{

    private final  BivariateNormalRV myBVN;

    private final double myMu1;

    private final double myVar1;

    private final double myMu2;

    private final double myVar2;

    private final double myRho;

    /** Constructs a standard bivariate normal with no correlation
     *
     */
    public BivariateLogNormalRV() {
        this(1.0, 1.0, 1.0, 1.0, 0.0, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a standard bivariate normal with no correlation
     *
     */
    public BivariateLogNormalRV(RngIfc rng) {
        this(1.0, 1.0, 1.0, 1.0, 0.0, rng);
    }

    /**
     *
     * @param mean1
     * @param var1
     * @param mean2
     * @param var2
     * @param rho
     */
    public BivariateLogNormalRV(double mean1, double var1, double mean2, double var2, double rho) {
        this(mean1, var1, mean2, var2, rho, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a bivariate normal with the provided parameters
     *
     * @param m1
     * @param v1
     * @param m2
     * @param v2
     * @param r
     * @param rng
     */
    public BivariateLogNormalRV(double m1, double v1, double m2, double v2, double r, RngIfc rng) {
        super(rng);

        if (m1 <= 0) {
            throw new IllegalArgumentException("Mean 1 must be positive");
        }
        if (m2 <= 0) {
            throw new IllegalArgumentException("Mean 1 must be positive");
        }
        if (v1 <= 0) {
            throw new IllegalArgumentException("Variance 1 must be positive");
        }
        if (v2 <= 0) {
            throw new IllegalArgumentException("Variance 2 must be positive");
        }
        if ((r < -1.0) || (r > 1.0)) {
            throw new IllegalArgumentException("The correlation must be within [-1,1]");
        }
        // set the parameters
        myMu1 = m1;
        myVar1 = v1;
        myMu2 = m2;
        myVar2 = v2;
        myRho = r;

        // calculate parameters of underlying bivariate normal
        // get the means
        double mean1 = Math.log((m1 * m1) / Math.sqrt(m1 * m1 + v1));
        double mean2 = Math.log((m2 * m2) / Math.sqrt(m2 * m2 + v2));
        // get the variances
        double var1 = Math.log(1.0 + (v1 / Math.abs(m1 * m1)));
        double var2 = Math.log(1.0 + (v2 / Math.abs(m2 * m2)));
        // calculate the correlation

        double cov = Math.log(1.0 + ((r * Math.sqrt(v1 * v2)) / Math.abs(m1 * m2)));
        double rho = cov / Math.sqrt(var1 * var2);
        myBVN = new BivariateNormalRV(mean1, var1, mean2, var2, rho, rng);
    }


    /** Gets the first mean
     *
     * @return
     */
    public final double getMean1() {
        return myMu1;
    }

    /** Gets the first variance
     *
     * @return
     */
    public final double getVariance1() {
        return myVar1;
    }

    /** Gets the second mean
     *
     * @return
     */
    public final double getMean2() {
        return myMu2;
    }

    /** Gets the 2nd variance
     *
     * @return
     */
    public final double getVariance2() {
        return myVar2;
    }

    /** Gets the correlation
     *
     * @return
     */
    public final double getCorrelation() {
        return myRho;
    }

    /**
     *
     * @return
     */
    public double[] sample() {
        double[] x = myBVN.sample();
        // transform them to bivariate lognormal
        x[0] = Math.exp(x[0]);
        x[1] = Math.exp(x[1]);
        return x;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BivariateLogNormalRV{");
        sb.append("mu1=").append(myMu1);
        sb.append(", var1=").append(myVar1);
        sb.append(", mu2=").append(myMu2);
        sb.append(", var2=").append(myVar2);
        sb.append(", rho=").append(myRho);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public MVRVariableIfc newInstance(RngIfc rng) {
        return new BivariateLogNormalRV(myMu1, myVar1, myMu2, myVar2, myRho, rng);
    }
}
