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

import java.util.List;

import jsl.utilities.math.JSLMath;

import static jsl.utilities.random.distributions.Gamma.invChiSquareDistribution;
import static jsl.utilities.random.distributions.Gamma.logGammaFunction;
import static jsl.utilities.random.distributions.Normal.stdNormalInvCDF;

import jsl.utilities.random.distributions.*;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 * The purpose of this class is to facilitate random variate generation from
 * various distributions through a set of static class methods.
 * <p>
 * Each method marked rXXXX will generate random variates from the named
 * distribution. The user has the option of supplying a RngIfc as the source of
 * the randomness. Methods that do not have a RngIfc parameter use,
 * RNStreamFactory.getDefaultStream() as the source of randomness.
 *
 * Also provides a number of methods for sampling with and without replacement
 * from arrays and lists as well as creating permutations of arrays and lists.
 *
 * @author rossetti
 */
public class JSLRandom {

    private static Beta myBeta;

    private JSLRandom(){}

    /**
     * @param pSuccess the probability of success
     * @return the random value
     */
    public static double rBernoulli(double pSuccess) {
        return rBernoulli(pSuccess, RNStreamFactory.getDefaultStream());
    }

    /**
     * @param pSuccess the probability of success
     * @param rng      the RngIfc
     * @return the random value
     */
    public static double rBernoulli(double pSuccess, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if ((pSuccess < 0.0) || (pSuccess > 1.0)) {
            throw new IllegalArgumentException("Success Probability must be [0,1]");
        }

        if (rng.randU01() <= pSuccess) {
            return (1.0);
        } else {
            return (0.0);
        }
    }

    /**
     * @param pSuccess the probability of success
     * @param nTrials  the number of trials
     * @return the random value
     */
    public static int rBinomial(double pSuccess, int nTrials) {
        return rBinomial(pSuccess, nTrials, RNStreamFactory.getDefaultStream());
    }

    /**
     * @param pSuccess the probability of success
     * @param nTrials  the number of trials
     * @param rng      the RngIfc
     * @return the random value
     */
    public static int rBinomial(double pSuccess, int nTrials, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (nTrials <= 0) {
            throw new IllegalArgumentException("Number of trials must be >= 1");
        }
        if ((pSuccess < 0.0) || (pSuccess > 1.0)) {
            throw new IllegalArgumentException("Success Probability must be [0,1]");
        }

        return Binomial.binomialInvCDF(rng.randU01(), nTrials, pSuccess);
    }

    /**
     * @param mean the mean of the Poisson
     * @return the random value
     */
    public static int rPoisson(double mean) {
        return rPoisson(mean, RNStreamFactory.getDefaultStream());
    }

    /**
     * @param mean the mean of the Poisson
     * @param rng the RngIfc
     * @return the random value
     */
    public static int rPoisson(double mean, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        return Poisson.poissonInvCDF(rng.randU01(), mean);
    }

    /**
     * Generates a discrete uniform over the range
     *
     * @param minimum the minimum of the range
     * @param maximum the maximum of the range
     * @return the random value
     */
    public static int rDUniform(int minimum, int maximum) {
        return rDUniform(minimum, maximum, RNStreamFactory.getDefaultStream());
    }

    /**
     * Generates a discrete uniform over the range
     *
     * @param minimum the minimum of the range
     * @param maximum the maximum of the range
     * @param rng     the RngIfc
     * @return the random value
     */
    public static int rDUniform(int minimum, int maximum, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        return rng.randInt(minimum, maximum);
    }

    /**
     * @param pSuccess the probability of success
     * @return the random value
     */
    public static int rGeometric(double pSuccess) {
        return rGeometric(pSuccess, RNStreamFactory.getDefaultStream());

    }

    /**
     * @param pSuccess the probability of success
     * @param rng      the RngIfc
     * @return the random value
     */
    public static int rGeometric(double pSuccess, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if ((pSuccess < 0.0) || (pSuccess > 1.0)) {
            throw new IllegalArgumentException("Success Probability must be [0,1]");
        }
        double u = rng.randU01();
        return ((int) Math.ceil((Math.log(1.0 - u) / (Math.log(1.0 - pSuccess))) - 1.0));
    }

    /**
     * @param pSuccess   the probability of success
     * @param rSuccesses number of trials until rth success
     * @return the random value
     */
    public static int rNegBinomial(double pSuccess, double rSuccesses) {
        return rNegBinomial(pSuccess, rSuccesses, RNStreamFactory.getDefaultStream());
    }

    /**
     * @param pSuccess   the probability of success
     * @param rSuccesses number of trials until rth success
     * @param rng        the RngIfc
     * @return the random value
     */
    public static int rNegBinomial(double pSuccess, double rSuccesses, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        return NegativeBinomial.negBinomialInvCDF(rng.randU01(), pSuccess, rSuccesses);
    }

    /**
     * Generates a continuous uniform over the range
     *
     * @param minimum the minimum of the range
     * @param maximum the maximum of the range
     * @return the random value
     */
    public static double rUniform(double minimum, double maximum) {
        return rUniform(minimum, maximum, RNStreamFactory.getDefaultStream());
    }

    /**
     * Generates a continuous uniform over the range
     *
     * @param minimum the minimum of the range
     * @param maximum the maximum of the range
     * @param rng     the RngIfc
     * @return the random value
     */
    public static double rUniform(double minimum, double maximum, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (minimum >= maximum) {
            throw new IllegalArgumentException("Lower limit must be < upper "
                    + "limit. lower limit = " + minimum + " upper limit = " + maximum);
        }

        return minimum + (maximum - minimum) * rng.randU01();
    }

    /**
     * @param mean     the mean of the normal
     * @param variance the variance of the normal
     * @return the random value
     */
    public static double rNormal(double mean, double variance) {
        return rNormal(mean, variance, RNStreamFactory.getDefaultStream());
    }

    /**
     * @param mean     the mean of the normal
     * @param variance the variance of the normal
     * @param rng      the RngIfc
     * @return the random value
     */
    public static double rNormal(double mean, double variance, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (variance <= 0) {
            throw new IllegalArgumentException("Variance must be positive");
        }

        double z = stdNormalInvCDF(rng.randU01());
        double stdDev = Math.sqrt(variance);
        return (z * stdDev + mean);
    }

    /**
     * @param mean     the mean of the lognormal
     * @param variance the variance of the lognormal
     * @return the random value
     */
    public static double rLogNormal(double mean, double variance) {
        return rLogNormal(mean, variance, RNStreamFactory.getDefaultStream());
    }

    /**
     * @param mean     the mean of the lognormal
     * @param variance the variance of the lognormal
     * @param rng      the RngIfc
     * @return the random value
     */
    public static double rLogNormal(double mean, double variance, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (mean <= 0) {
            throw new IllegalArgumentException("Mean must be positive");
        }
        if (variance <= 0) {
            throw new IllegalArgumentException("Variance must be positive");
        }

        double z = Normal.stdNormalInvCDF(rng.randU01());
        double d = variance + mean * mean;
        double t = mean * mean;
        double normalMu = Math.log((t) / Math.sqrt(d));
        double normalSigma = Math.sqrt(Math.log(d / t));
        double x = z * normalSigma + normalMu;
        return (Math.exp(x));
    }

    /**
     * @param shape the shape
     * @param scale the scale
     * @return the random value
     */
    public static double rWeibull(double shape, double scale) {
        return rWeibull(shape, scale, RNStreamFactory.getDefaultStream());
    }

    /**
     * @param shape the shape
     * @param scale the scale
     * @param rng   the RngIfc
     * @return the random value
     */
    public static double rWeibull(double shape, double scale, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (shape <= 0) {
            throw new IllegalArgumentException("Shape parameter must be positive");
        }
        if (scale <= 0) {
            throw new IllegalArgumentException("Scale parameter must be positive");
        }
        double u = rng.randU01();
        return scale * Math.pow(-Math.log(1.0 - u), 1.0 / shape);
    }

    /**
     * @param mean the mean
     * @return the random value
     */
    public static double rExponential(double mean) {
        return rExponential(mean, RNStreamFactory.getDefaultStream());
    }

    /**
     * @param mean the mean
     * @param rng  the RngIfc
     * @return the random value
     */
    public static double rExponential(double mean, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (mean <= 0.0) {
            throw new IllegalArgumentException("Exponential mean must be > 0.0");
        }
        double u = rng.randU01();
        return (-mean * Math.log(1.0 - u));
    }

    /**
     * @param alpha1 alpha1 parameter
     * @param alpha2 alpha2 parameter
     * @param min    the min
     * @param max    the max
     * @return the generated value
     */
    public static double rJohnsonB(double alpha1, double alpha2,
                                         double min, double max) {
        return rJohnsonB(alpha1, alpha2, min, max, RNStreamFactory.getDefaultStream());
    }

    /**
     * @param alpha1 alpha1 parameter
     * @param alpha2 alpha2 parameter
     * @param min    the min
     * @param max    the max
     * @param rng    the RngIfc
     * @return the generated value
     */
    public static double rJohnsonB(double alpha1, double alpha2,
                                         double min, double max, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (alpha2 <= 0) {
            throw new IllegalArgumentException("alpha2 must be > 0");
        }

        if (max <= min) {
            throw new IllegalArgumentException("the min must be < than the max");
        }
        double u = rng.randU01();
        double z = Normal.stdNormalInvCDF(u);
        double y = Math.exp((z - alpha1) / alpha2);
        double x = (min + max * y) / (y + 1.0);
        return x;
    }

    /**
     * @param shape the shape
     * @param scale the scale
     * @return the generated value
     */
    public static double rLogLogistic(double shape, double scale) {
        return rLogLogistic(shape, scale, RNStreamFactory.getDefaultStream());
    }

    /**
     * @param shape the shape
     * @param scale the scale
     * @param rng   the RngIfc
     * @return the generated value
     */
    public static double rLogLogistic(double shape, double scale, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (shape <= 0) {
            throw new IllegalArgumentException("Shape parameter must be positive");
        }
        if (scale <= 0) {
            throw new IllegalArgumentException("Scale parameter must be positive");
        }
        double u = rng.randU01();
        double c = u / (1.0 - u);
        return (scale * Math.pow(c, 1.0 / shape));
    }

    /**
     * @param min  the min
     * @param mode the mode
     * @param max  the max
     * @return the random value
     */
    public static double rTriangular(double min, double mode,
                                           double max) {
        return rTriangular(min, mode, max, RNStreamFactory.getDefaultStream());
    }

    /**
     * @param min  the min
     * @param mode the mode
     * @param max  the max
     * @param rng  the RngIfc
     * @return the random value
     */
    public static double rTriangular(double min, double mode,
                                           double max, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (min > mode) {
            throw new IllegalArgumentException("min must be <= mode");
        }

        if (min >= max) {
            throw new IllegalArgumentException("min must be < max");
        }

        if (mode > max) {
            throw new IllegalArgumentException("mode must be <= max");
        }
        double range = max - min;

        double c = (mode - min) / range;

        // get the invCDF for a triang(0,c,1)
        double x;
        double p = rng.randU01();
        if (c == 0.0) { // left triangular, mode equals min
            x = 1.0 - Math.sqrt(1 - p);
        } else if (c == 1.0) { //right triangular, mode equals max
            x = Math.sqrt(p);
        } else if (p < c) {
            x = Math.sqrt(c * p);
        } else {
            x = 1.0 - Math.sqrt((1.0 - c) * (1.0 - p));
        }
        // scale it back to original scale
        return (min + range * x);
    }

    /**
     * @param shape the shape
     * @param scale the scale
     * @return the generated value
     */
    public static double rGamma(double shape, double scale) {
        return rGamma(shape, scale, RNStreamFactory.getDefaultStream());
    }

    /**
     * @param shape the shape
     * @param scale the scale
     * @param rng   the RngIfc
     * @return the generated value
     */
    public static double rGamma(double shape, double scale, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (shape <= 0) {
            throw new IllegalArgumentException("Shape parameter must be positive");
        }
        if (scale <= 0) {
            throw new IllegalArgumentException("Scale parameter must be positive");
        }
        double p = rng.randU01();
        if (p <= 0.0) {
            return 0.0;
        }

        if (p >= 1.0) {
            return Double.POSITIVE_INFINITY;
        }

        double x;
        /* ...special case: exponential distribution */
        if (shape == 1.0) {
            x = -shape * Math.log(1.0 - p);
            return (x);
        }
        /* ...compute the gamma(alpha, beta) inverse.                   *
         *    ...compute the chi-square inverse with 2*alpha degrees of *
         *       freedom, which is equivalent to gamma(alpha, 2).       */

        double v = 2.0 * shape;
        double g = logGammaFunction(shape);

        double chi2 = invChiSquareDistribution(p, v, g,
                Gamma.DEFAULT_MAX_ITERATIONS, JSLMath.getDefaultNumericalPrecision());

        /* ...transfer chi-square to gamma. */
        x = scale * chi2 / 2.0;

        return (x);
    }

    /**
     * @param dof degrees of freedom
     * @return the random value
     */
    public static double rChiSquared(double dof) {
        return rChiSquared(dof, RNStreamFactory.getDefaultStream());
    }

    /**
     * @param dof degrees of freedom
     * @param rng the RngIfc
     * @return the random value
     */
    public static double rChiSquared(double dof, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (dof <= 0) {
            throw new IllegalArgumentException("The degrees of freedom should be > 0");
        }
        return Gamma.invChiSquareDistribution(rng.randU01(), dof);
    }

    /**
     * @param shape the shape
     * @param scale the scale
     * @return the generated value
     */
    public static double rPearsonType5(double shape, double scale) {
        return rPearsonType5(shape, scale, RNStreamFactory.getDefaultStream());
    }

    /**
     * @param shape the shape
     * @param scale the scale
     * @param rng   the RngIfc
     * @return the generated value
     */
    public static double rPearsonType5(double shape, double scale, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (shape <= 0) {
            throw new IllegalArgumentException("Alpha (shape parameter) should be > 0");
        }

        if (scale <= 0) {
            throw new IllegalArgumentException("Beta (scale parameter) should > 0");
        }

        double GShape = shape;
        double GScale = 1.0 / scale;
        double y = rGamma(GShape, GScale, rng);
        return 1.0 / y;

    }

    /**
     * This beta is restricted to the range of (0,1)
     *
     * @param alpha1 alpha1 parameter
     * @param alpha2 alpha2 parameter
     * @return the random value
     */
    public static double rBeta(double alpha1, double alpha2) {
        return rBeta(alpha1, alpha2, RNStreamFactory.getDefaultStream());
    }

    /**
     * This beta is restricted to the range of (0,1)
     *
     * @param alpha1 alpha1 parameter
     * @param alpha2 alpha2 parameter
     * @param rng    the RngIfc
     * @return the random value
     */
    public static double rBeta(double alpha1, double alpha2, RngIfc rng) {
        if (myBeta == null) {
            myBeta = new Beta(alpha1, alpha2, rng);
        }
        myBeta.setRandomNumberGenerator(rng);
        myBeta.setParameters(alpha1, alpha2);
        return myBeta.getValue();
    }

    /**
     * This beta is restricted to the range of (minimum,maximum)
     *
     * @param alpha1  alpha1 parameter
     * @param alpha2  alpha2 parameter
     * @param minimum the minimum of the range
     * @param maximum the maximum of the range
     * @return the random value
     */
    public static double rBetaG(double alpha1, double alpha2,
                                      double minimum, double maximum) {
        return rBetaG(alpha1, alpha2, minimum, maximum, RNStreamFactory.getDefaultStream());
    }

    /**
     * This beta is restricted to the range of (minimum,maximum)
     *
     * @param alpha1  alpha1 parameter
     * @param alpha2  alpha2 parameter
     * @param minimum the minimum of the range
     * @param maximum the maximum of the range
     * @param rng     the RngIfc
     * @return the random value
     */
    public static double rBetaG(double alpha1, double alpha2,
                                      double minimum, double maximum, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (minimum >= maximum) {
            throw new IllegalArgumentException("Lower limit must be < upper "
                    + "limit. lower limit = " + minimum + " upper limit = " + maximum);
        }
        double x = rBeta(alpha1, alpha2, rng);
        return minimum + (maximum - minimum) * x;
    }

    /**
     * Pearson Type 6
     *
     * @param alpha1 alpha1 parameter
     * @param alpha2 alpha2 parameter
     * @param beta   the beta parameter
     * @return the random value
     */
    public static double rPearsonType6(double alpha1, double alpha2,
                                             double beta) {
        return rPearsonType6(alpha1, alpha2, beta, RNStreamFactory.getDefaultStream());
    }

    /**
     * Pearson Type 6
     *
     * @param alpha1 alpha1 parameter
     * @param alpha2 alpha2 parameter
     * @param beta   the beta parameter
     * @param rng    the RngIfc
     * @return the random value
     */
    public static double rPearsonType6(double alpha1, double alpha2,
                                             double beta, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (beta <= 0.0) {
            throw new IllegalArgumentException("The scale parameter must be > 0.0");
        }
        double fib = rBeta(alpha1, alpha2, rng);
        return (beta * fib) / (1.0 - fib);
    }

    /**
     * Generates according to a Laplace(mean, scale)
     *
     * @param mean  mean or location parameter
     * @param scale scale parameter
     * @return the random value
     */
    public static double rLaplace(double mean, double scale) {
        return rLaplace(mean, scale, RNStreamFactory.getDefaultStream());
    }

    /**
     * Generates according to a Laplace(mean, scale)
     *
     * @param mean  mean or location parameter
     * @param scale scale parameter
     * @param rng   the RngIfc
     * @return the random value
     */
    public static double rLaplace(double mean, double scale, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (scale <= 0.0) {
            throw new IllegalArgumentException("The scale parameter must be > 0.0");
        }
        double p = rng.randU01();
        double u = p - 0.5;
        return mean - scale * Math.signum(u) * Math.log(1.0 - 2.0 * Math.abs(u));
    }

    /**
     * Randomly select an element from the array
     *
     * @param array the array to select from
     * @return
     */
    public static int randomlySelect(int[] array) {
        return randomlySelect(array, RNStreamFactory.getDefaultStream());
    }

    /**
     * Randomly select an element from the array
     *
     * @param array the array to select from
     * @param rng   the source of randomness
     * @return
     */
    public static int randomlySelect(int[] array, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (array == null) {
            throw new IllegalArgumentException("The supplied array was null");
        }
        if (array.length == 1) {
            return array[0];
        }
        int randInt = rng.randInt(0, array.length - 1);
        return array[randInt];
    }

    /**
     * Randomly select an element from the array
     *
     * @param array the array to select from
     * @return the randomly selected value
     */
    public static double randomlySelect(double[] array) {
        return randomlySelect(array, RNStreamFactory.getDefaultStream());
    }

    /**
     * Randomly select an element from the array
     *
     * @param array the array to select from
     * @param rng   the source of randomness
     * @return the randomly selected value
     */
    public static double randomlySelect(double[] array, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (array == null) {
            throw new IllegalArgumentException("The supplied array was null");
        }
        if (array.length == 1) {
            return array[0];
        }
        int randInt = rng.randInt(0, array.length - 1);
        return array[randInt];
    }

    /**
     * Randomly selects from the array using the supplied cdf
     *
     * @param array array to select from
     * @param cdf   the cumulative probability associated with each element of
     *              array
     * @return the randomly selected value
     */
    public static double randomlySelect(double[] array, double[] cdf) {
        return randomlySelect(array, cdf, RNStreamFactory.getDefaultStream());
    }

    /**
     * Randomly selects from the array using the supplied cdf
     *
     * @param array array to select from
     * @param cdf   the cumulative probability associated with each element of
     *              array
     * @param rng   the source of randomness
     * @return the randomly selected value
     */
    public  static double randomlySelect(double[] array, double[] cdf, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (array == null) {
            throw new IllegalArgumentException("The supplied array was null");
        }
        if (cdf == null) {
            throw new IllegalArgumentException("The supplied cdf was null");
        }
        if (!isValidCDF(cdf)) {
            throw new IllegalArgumentException("The supplied cdf was not valid");
        }
        if (array.length != cdf.length) {
            throw new IllegalArgumentException("The arrays did not have the same length.");
        }
        if (cdf.length == 1) {
            return array[0];
        }

        int i = 0;
        double value = array[i];
        double u = rng.randU01();
        while (cdf[i] <= u) {
            i = i + 1;
            value = array[i];
        }

        return value;

    }

    /**
     * Randomly selects from the array using the supplied cdf
     *
     * @param array array to select from
     * @param cdf   the cumulative probability associated with each element of
     *              array
     * @return the randomly selected value
     */
    public static int randomlySelect(int[] array, double[] cdf) {
        return randomlySelect(array, cdf, RNStreamFactory.getDefaultStream());
    }

    /**
     * Randomly selects from the array using the supplied cdf
     *
     * @param array array to select from
     * @param cdf   the cumulative probability associated with each element of
     *              array
     * @param rng   the source of randomness
     * @return the randomly selected value
     */
    public static int randomlySelect(int[] array, double[] cdf, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (array == null) {
            throw new IllegalArgumentException("The supplied array was null");
        }
        if (cdf == null) {
            throw new IllegalArgumentException("The supplied cdf was null");
        }
        if (!isValidCDF(cdf)) {
            throw new IllegalArgumentException("The supplied cdf was not valid");
        }
        if (array.length != cdf.length) {
            throw new IllegalArgumentException("The arrays did not have the same length.");
        }
        if (cdf.length == 1) {
            return array[0];
        }

        int i = 0;
        int value = array[i];
        double u = rng.randU01();
        while (cdf[i] <= u) {
            i = i + 1;
            value = array[i];
        }

        return value;

    }

    /**
     * Randomly selects from the list using the supplied cdf
     *
     * @param <T>  the type returned
     * @param list list to select from
     * @param cdf  the cumulative probability associated with each element of
     *             array
     * @return the randomly selected value
     */
    public static <T> T randomlySelect(List<T> list, double[] cdf) {
        return randomlySelect(list, cdf, RNStreamFactory.getDefaultStream());
    }

    /**
     * Randomly selects from the list using the supplied cdf
     *
     * @param <T>  the type returned
     * @param list list to select from
     * @param cdf  the cumulative probability associated with each element of
     *             array
     * @param rng  the source of randomness
     * @return the randomly selected value
     */
    public static <T> T randomlySelect(List<T> list, double[] cdf, RngIfc rng) {
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (list == null) {
            throw new IllegalArgumentException("The supplied list was null");
        }
        if (cdf == null) {
            throw new IllegalArgumentException("The supplied cdf was null");
        }
        if (!isValidCDF(cdf)) {
            throw new IllegalArgumentException("The supplied cdf was not valid");
        }
        if (list.size() != cdf.length) {
            throw new IllegalArgumentException("The list and cdf did not have the same length.");
        }
        if (cdf.length == 1) {
            return list.get(0);
        }

        int i = 0;
        T value = list.get(i);
        double u = rng.randU01();
        while (cdf[i] <= u) {
            i = i + 1;
            value = list.get(i);
        }

        return value;

    }

    /**
     * @param cdf the probability array. must have valid probability elements
     *            and last element equal to 1. Every element must be greater than or equal
     *            to the previous element. That is, monotonically increasing.
     * @return true if valid cdf
     */
    public static boolean isValidCDF(double[] cdf) {
        if (cdf == null) {
            return false;
        }
        if (cdf[cdf.length - 1] != 1.0) {
            return false;
        }
        for (int i = 0; i < cdf.length; i++) {
            if ((cdf[i] < 0.0) || (cdf[i] > 1.0)) {
                return false;
            }
            if (i < cdf.length - 1) {
                if (cdf[i + 1] < cdf[i]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Randomly select from the list using the default stream
     *
     * @param <T>  The type of element in the list
     * @param list the list
     * @return the randomly selected element
     */
    public static <T> T randomlySelect(List<T> list) {
        return randomlySelect(list, RNStreamFactory.getDefaultStream());
    }

    /**
     * Randomly select from the list
     *
     * @param <T>  The type of element in the list
     * @param list the list
     * @param rng  the source of randomness
     * @return the randomly selected element
     */
    public static <T> T randomlySelect(List<T> list, RngIfc rng) {
        if (list == null) {
            throw new IllegalArgumentException("The supplied list was null");
        }
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (list.isEmpty()) {
            return null;
        }

        if (list.size() == 1) {
            return list.get(0);
        }

        // more than 1, need to randomly pick
        return list.get(rng.randInt(0, list.size()-1));
    }

    /**
     * Randomly permutes the supplied array using the default random
     * number generator.  The array is changed
     *
     * @param x the array
     */
    public static void permutation(double[] x) {
        permutation(x, RNStreamFactory.getDefaultStream());
    }

    /**
     * Randomly permutes the supplied array using the suppled random
     * number generator, the array is changed
     *
     * @param x the array
     * @param rng the source of randomness
     */
    public static void permutation(double[] x, RngIfc rng) {
        sampleWithoutReplacement(x, x.length, rng);
    }

    /**
     * The array x is changed, such that the first sampleSize elements contain the generate.
     * That is, x[0], x[1], ... , x[sampleSize-1] is the random generate without replacement
     * using the default random number generator
     *
     * @param x the array
     * @param sampleSize the size of the generate
     */
    public static void sampleWithoutReplacement(double[] x, int sampleSize) {
        sampleWithoutReplacement(x, sampleSize, RNStreamFactory.getDefaultStream());
    }

    /**
     * The array x is changed, such that the first sampleSize elements contain the generate.
     * That is, x[0], x[1], ... , x[sampleSize-1] is the random generate without replacement
     *
     * @param x the array
     * @param sampleSize the generate size
     * @param rng the source of randomness
     */
    public static void sampleWithoutReplacement(double[] x, int sampleSize, RngIfc rng) {
        if (x == null) {
            throw new IllegalArgumentException("The supplied array was null");
        }
        if (rng == null) {
            throw new IllegalArgumentException("The supplied random number generator was null");
        }
        if (sampleSize > x.length) {
            throw new IllegalArgumentException("Can't draw without replacement more than the number of elements");
        }

        for (int j = 0; j < sampleSize; j++) {
            int i = rng.randInt(j, x.length - 1);
            double temp = x[j];
            x[j] = x[i];
            x[i] = temp;
        }
    }

    /**
     * Randomly permutes the supplied array using the default random
     * number generator.  The array is changed
     *
     * @param x the array
     */
    public static void permutation(int[] x) {
        permutation(x, RNStreamFactory.getDefaultStream());
    }

    /**
     * Randomly permutes the supplied array using the suppled random
     * number generator, the array is changed
     *
     * @param x the array
     * @param rng the source of randomness
     */
    public static void permutation(int[] x, RngIfc rng) {
        sampleWithoutReplacement(x, x.length, rng);
    }

    /**
     * The array x is changed, such that the first sampleSize elements contain the generate.
     * That is, x[0], x[1], ... , x[sampleSize-1] is the random generate without replacement
     * using the default random number generator
     *
     * @param x the array
     * @param sampleSize the generate size
     */
    public static void sampleWithoutReplacement(int[] x, int sampleSize) {
        sampleWithoutReplacement(x, sampleSize, RNStreamFactory.getDefaultStream());
    }

    /**
     * The array x is changed, such that the first sampleSize elements contain the generate.
     * That is, x[0], x[1], ... , x[sampleSize-1] is the random generate without replacement
     *
     * @param x the array
     * @param sampleSize the generate size
     * @param rng the source of randomness
     */
    public static void sampleWithoutReplacement(int[] x, int sampleSize, RngIfc rng) {
        if (x == null) {
            throw new IllegalArgumentException("The supplied array was null");
        }
        if (rng == null) {
            throw new IllegalArgumentException("The supplied random number generator was null");
        }
        if (sampleSize > x.length) {
            throw new IllegalArgumentException("Can't draw without replacement more than the number of elements");
        }

        for (int j = 0; j < sampleSize; j++) {
            int i = rng.randInt(j, x.length - 1);
            int temp = x[j];
            x[j] = x[i];
            x[i] = temp;
        }
    }

    /**
     * Randomly permutes the supplied array using the default random
     * number generator.  The array is changed
     *
     * @param x the array
     */
    public static void permutation(boolean[] x) {
        permutation(x, RNStreamFactory.getDefaultStream());
    }

    /**
     * Randomly permutes the supplied array using the suppled random
     * number generator, the array is changed
     *
     * @param x the array
     * @param rng the source of randomness
     */
    public static void permutation(boolean[] x, RngIfc rng) {
        sampleWithoutReplacement(x, x.length, rng);
    }

    /**
     * The array x is changed, such that the first sampleSize elements contain the generate.
     * That is, x[0], x[1], ... , x[sampleSize-1] is the random generate without replacement
     * using the default random number generator
     *
     * @param x the array
     * @param sampleSize the generate size
     */
    public static void sampleWithoutReplacement(boolean[] x, int sampleSize) {
        sampleWithoutReplacement(x, sampleSize, RNStreamFactory.getDefaultStream());
    }

    /**
     * The array x is changed, such that the first sampleSize elements contain the generate.
     * That is, x[0], x[1], ... , x[sampleSize-1] is the random generate without replacement
     *
     * @param x the array
     * @param sampleSize the generate size
     * @param rng the source of randomness
     */
    public static void sampleWithoutReplacement(boolean[] x, int sampleSize, RngIfc rng) {
        if (x == null) {
            throw new IllegalArgumentException("The supplied array was null");
        }
        if (rng == null) {
            throw new IllegalArgumentException("The supplied random number generator was null");
        }
        if (sampleSize > x.length) {
            throw new IllegalArgumentException("Can't draw without replacement more than the number of elements");
        }

        for (int j = 0; j < sampleSize; j++) {
            int i = rng.randInt(j, x.length - 1);
            boolean temp = x[j];
            x[j] = x[i];
            x[i] = temp;
        }
    }

    /**
     * Randomly permutes the supplied array using the default random
     * number generator.  The array is changed
     *
     * @param x the array
     */
    public static <T> void permutation(T[] x) {
        permutation(x, RNStreamFactory.getDefaultStream());
    }

    /**
     * Randomly permutes the supplied array using the suppled random
     * number generator, the array is changed
     *
     * @param x the array
     * @param rng the source of randomness
     */
    public static <T> void permutation(T[] x, RngIfc rng) {
        sampleWithoutReplacement(x, x.length, rng);
    }

    /**
     * The array x is changed, such that the first sampleSize elements contain the generate.
     * That is, x[0], x[1], ... , x[sampleSize-1] is the random generate without replacement
     * using the default random number generator
     *
     * @param x the array
     * @param sampleSize the source of randomness
     */
    public static <T> void sampleWithoutReplacement(T[] x, int sampleSize) {
        sampleWithoutReplacement(x, sampleSize, RNStreamFactory.getDefaultStream());
    }

    /**
     * The array x is changed, such that the first sampleSize elements contain the generate.
     * That is, x[0], x[1], ... , x[sampleSize-1] is the random generate without replacement
     *
     * @param x the array
     * @param sampleSize the generate size
     * @param rng the source of randomness
     */
    public static <T> void sampleWithoutReplacement(T[] x, int sampleSize, RngIfc rng) {
        if (x == null) {
            throw new IllegalArgumentException("The supplied array was null");
        }
        if (rng == null) {
            throw new IllegalArgumentException("The supplied random number generator was null");
        }
        if (sampleSize > x.length) {
            throw new IllegalArgumentException("Can't draw without replacement more than the number of elements");
        }

        for (int j = 0; j < sampleSize; j++) {
            int i = rng.randInt(j, x.length - 1);
            T temp = x[j];
            x[j] = x[i];
            x[i] = temp;
        }
    }

    /**
     * Randomly permutes the supplied List using the suppled random
     * number generator, the list is changed
     *
     * @param <T> the type of the list
     * @param x the list
     */
    public static <T> void permutation(List<T> x) {
        permutation(x, RNStreamFactory.getDefaultStream());
    }

    /**
     * Randomly permutes the supplied List using the suppled random
     * number generator, the list is changed
     *
     * @param <T> the type of the list
     * @param x the list
     * @param rng the source of randomness
     */
    public static <T> void permutation(List<T> x, RngIfc rng) {
        sampleWithoutReplacement(x, x.size(), rng);
    }

    /**
     * The List x is changed, such that the first sampleSize elements contain the generate.
     * That is, x.get(0), x.get(1), ... , x.get(sampleSize-1) is the random generate without replacement
     * using the default random number generator
     *
     * @param <T> the type of the list
     * @param x the list
     * @param sampleSize the generate size
     */
    public static <T> void sampleWithoutReplacement(List<T> x, int sampleSize) {
        sampleWithoutReplacement(x, sampleSize, RNStreamFactory.getDefaultStream());
    }

    /**
     * The List x is changed, such that the first sampleSize elements contain the generate.
     * That is, x.get(0), x.get(1), ... , x.get(sampleSize-1) is the random generate without replacement
     *
     * @param <T> the type of the list
     * @param x the list
     * @param sampleSize the generate size
     * @param rng the source of randomness
     */
    public static <T> void sampleWithoutReplacement(List<T> x, int sampleSize, RngIfc rng) {
        if (x == null) {
            throw new IllegalArgumentException("The supplied array was null");
        }
        if (rng == null) {
            throw new IllegalArgumentException("The supplied random number generator was null");
        }
        int n = x.size();
        if (sampleSize > n) {
            throw new IllegalArgumentException("Can't draw without replacement more than the number of elements");
        }

        for (int j = 0; j < sampleSize; j++) {
            int i = rng.randInt(j, n - 1);
            T temp = x.get(j);
            x.set(j, x.get(i));
            x.set(i, temp);
        }
    }
}
