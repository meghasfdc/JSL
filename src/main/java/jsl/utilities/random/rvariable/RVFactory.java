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

import jsl.utilities.controls.Controls;

import java.util.Objects;

/**
 *  Permits construction of random variables based on factory methods defined by
 *  controls.  The controls hold the (key, value) pairs that represent distributional
 *  parameters by name.  The user of the control is responsible for setting legal
 *  parameter values on the controls as required by the desired random variable type.
 *  The getXControls() methods document the named parameters of each distribution.
 *  Use getXControls() to get the desired control. Then, change the parameters using Control methods.
 *  Then call the factory methods to make the random variable. Once a random variable is
 *  made, its parameters cannot be changed.
 */
public class RVFactory {

    /**
     * The set of pre-defined distribution types
     */
    public enum RVType {
        Bernoulli, Beta, ChiSquared, Binomial, Constant, DUniform, Exponential,
        Gamma, GeneralizedBeta, Geometric, JohnsonB, Laplace, LogLogistic, Lognormal,
        NegativeBinomial, Normal, PearsonType5, PearsonType6, Poisson, ShiftedGeometric,
        Triangular, Uniform, Weibull, DEmpirical
    }

    /**
     * The keys are "values" with default an array {0.0, 1.0} and
     * key "cdf" with default array {0.5, 1.0}
     *
     * @return a control for DEmpirical random variables
     */
    public static Controls getDEmpiricalControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleArrayControl("values", new double[] {0.0, 1.0});
                addDoubleArrayControl("cdf", new double[] {0.5, 1.0});
                setName(RVType.DEmpirical.name());
            }
        };
    }

    /**
     * The key is "ProbOfSuccess", the default value is 0.5
     *
     * @return a control for Bernoulli random variables
     */
    public static Controls getBernoulliControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("ProbOfSuccess", 0.5);
                setName(RVType.Bernoulli.name());
            }
        };
    }

    /**
     * The keys are "ProbOfSuccess", the default value is 0.5 and
     * "NumTrials" with default value 2.
     *
     * @return a control for Binomial random variables
     */
    public static Controls getBinomialControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("ProbOfSuccess", 0.5);
                addIntegerControl("NumTrials", 2);
                setName(RVType.Binomial.name());
            }
        };
    }

    /**
     * The keys are "alpha1", the default value is 1.0 and
     * "alpha2" with default value 1.0.
     *
     * @return a control for Beta random variables
     */
    public static Controls getBetaControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("alpha1", 1.0);
                addDoubleControl("alpha2", 1.0);
                setName(RVType.Beta.name());
            }
        };
    }

    /**
     * The keys are "dof", the default value is 1.0
     *
     * @return a control for Chi-Squared random variables
     */
    public static Controls getChiSquareControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("dof", 1.0);
                setName(RVType.ChiSquared.name());
            }
        };
    }

    /**
     * The keys are "value", the default value is 1.0
     *
     * @return a control for Chi-Squared random variables
     */
    public static Controls getConstantControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("value", 1.0);
                setName(RVType.Constant.name());
            }
        };
    }

    /**
     * The keys are "min" with default value 0 and "max" with
     * default value 1
     *
     * @return a control for DUniform random variables
     */
    public static Controls getDUniformControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addIntegerControl("min", 0);
                addIntegerControl("max", 1);
                setName(RVType.DUniform.name());
            }
        };
    }

    /**
     * The key is "mean" with default value 1.0
     *
     * @return a control for Exponential random variables
     */
    public static Controls getExponentialControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("mean", 1.0);
                setName(RVType.Exponential.name());
            }
        };
    }

    /**
     * The keys are "shape" with default value 1.0 and "scale" with
     * default value 1.0
     *
     * @return a control for Gamma random variables
     */
    public static Controls getGammaControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("shape", 1.0);
                addDoubleControl("scale", 1.0);
                setName(RVType.Gamma.name());
            }
        };
    }

    /**
     * The key is "ProbOfSuccess", the default value is 0.5
     *
     * @return a control for Geometric random variables
     */
    public static Controls getGeometricControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("ProbOfSuccess", 0.5);
                setName(RVType.Geometric.name());
            }
        };
    }

    /**
     * The keys are "alpha1" with default value 1.0,
     * "alpha2" with default value 1.0,  "min" with default value 0.0 and "max" with
     * default value 1.0
     *
     * @return a control for GeneralizeBeta random variables
     */
    public static Controls getGeneralizedBetaControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("alpha1", 1.0);
                addDoubleControl("alpha2", 1.0);
                addDoubleControl("min", 0.0);
                addDoubleControl("max", 1.0);
                setName(RVType.GeneralizedBeta.name());
            }
        };
    }

    /**
     * The keys are "alpha1" with default value 0.0,
     * "alpha2" with default value 1.0,  "min" with default value 0.0 and "max" with
     * default value 1.0
     *
     * @return a control for JohnsonB random variables
     */
    public static Controls getJohnsonBControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("alpha1", 0.0);
                addDoubleControl("alpha2", 1.0);
                addDoubleControl("min", 0.0);
                addDoubleControl("max", 1.0);
                setName(RVType.JohnsonB.name());
            }
        };
    }

    /**
     * The keys are "mean" with default value 0.0 and "scale" with
     * default value 1.0
     *
     * @return a control for Laplace random variables
     */
    public static Controls getLaplaceControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("mean", 0.0);
                addDoubleControl("scale", 1.0);
                setName(RVType.Laplace.name());
            }
        };
    }

    /**
     * The keys are "shape" with default value 1.0 and "scale" with
     * default value 1.0
     *
     * @return a control for LogLogistic random variables
     */
    public static Controls getLogLogisticControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("shape", 1.0);
                addDoubleControl("scale", 1.0);
                setName(RVType.LogLogistic.name());
            }
        };
    }

    /**
     * The keys are "mean" with default value 1.0 and "variance" with
     * default value 1.0
     *
     * @return a control for Lognormal random variables
     */
    public static Controls getLognormalControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("mean", 1.0);
                addDoubleControl("variance", 1.0);
                setName(RVType.Lognormal.name());
            }
        };
    }

    /**
     * The keys are "ProbOfSuccess", the default value is 0.5 and
     * "NumSuccesses" with default value 1.
     *
     * @return a control for Negative Binomial random variables
     */
    public static Controls getNegativeBinomialControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("ProbOfSuccess", 0.5);
                addIntegerControl("NumSuccesses", 1);
                setName(RVType.NegativeBinomial.name());
            }
        };
    }

    /**
     * The keys are "mean" with default value 0.0 and "variance" with
     * default value 1.0
     *
     * @return a control for Normal random variables
     */
    public static Controls getNormalControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("mean", 0.0);
                addDoubleControl("variance", 1.0);
                setName(RVType.Normal.name());
            }
        };
    }

    /**
     * The keys are "shape" with default value 1.0 and "scale" with
     * default value 1.0
     *
     * @return a control for PearsonType5 random variables
     */
    public static Controls getPearsonType5Controls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("shape", 1.0);
                addDoubleControl("scale", 1.0);
                setName(RVType.PearsonType5.name());
            }
        };
    }

    /**
     * The keys are "alpha1" with default value 2.0 and "alpha2" with
     * default value 3.0, and "beta" with default value 1.0
     *
     * @return a control for PearsonType6 random variables
     */
    public static Controls getPearsonType6Controls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("alpha1", 2.0);
                addDoubleControl("alpha2", 3.0);
                addDoubleControl("beta", 1.0);
                setName(RVType.PearsonType6.name());
            }
        };
    }

    /**
     * The key is "mean" with default value 1.0
     *
     * @return a control for Poisson random variables
     */
    public static Controls getPoissonControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("mean", 1.0);
                setName(RVType.Poisson.name());
            }
        };
    }

    /**
     * The key is "ProbOfSuccess", the default value is 0.5
     *
     * @return a control for ShiftedGeometric random variables
     */
    public static Controls getShiftedGeometricControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("ProbOfSuccess", 0.5);
                setName(RVType.ShiftedGeometric.name());
            }
        };
    }

    /**
     * The keys are "min" with default value 0.0 and "mode" with
     * default value 0.5, and "max" with default value 1.0
     *
     * @return a control for Triangular random variables
     */
    public static Controls getTriangularControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("min", 0.0);
                addDoubleControl("mode", 0.5);
                addDoubleControl("max", 1.0);
                setName(RVType.Triangular.name());
            }
        };
    }

    /**
     * The keys are "min" with default value 0.0 and "max" with default value 1.0
     *
     * @return a control for Uniform random variables
     */
    public static Controls getUniformControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("min", 0.0);
                addDoubleControl("max", 1.0);
                setName(RVType.Uniform.name());
            }
        };
    }

    /**
     * The keys are "shape" with default value 1.0 and "scale" with
     * default value 1.0
     *
     * @return a control for Weibull random variables
     */
    public static Controls getWeibullControls() {
        return new Controls() {
            @Override
            protected void fillControls() {
                addDoubleControl("shape", 1.0);
                addDoubleControl("scale", 1.0);
                setName(RVType.Weibull.name());
            }
        };
    }

    /**
     * @param type the type of random variable to get
     * @return the controls for the specified type
     */
    public static Controls getControls(RVType type) {
        Objects.requireNonNull(type, "The random variable type was null");
        Controls c;
        switch (type) {
            case Bernoulli:
                c = getBernoulliControls();
                break;
            case Beta:
                c = getBetaControls();
                break;
            case Binomial:
                c = getBinomialControls();
                break;
            case ChiSquared:
                c = getChiSquareControls();
                break;
            case Constant:
                c = getConstantControls();
                break;
            case DEmpirical:
                c = getDEmpiricalControls();
                break;
            case DUniform:
                c = getDUniformControls();
                break;
            case Exponential:
                c = getExponentialControls();
                break;
            case Gamma:
                c = getGammaControls();
                break;
            case GeneralizedBeta:
                c = getGeneralizedBetaControls();
                break;
            case Geometric:
                c = getGeometricControls();
                break;
            case JohnsonB:
                c = getJohnsonBControls();
                break;
            case Laplace:
                c = getLaplaceControls();
                break;
            case LogLogistic:
                c = getLogLogisticControls();
                break;
            case Lognormal:
                c = getLognormalControls();
                break;
            case NegativeBinomial:
                c = getNegativeBinomialControls();
                break;
            case Normal:
                c = getNormalControls();
                break;
            case PearsonType5:
                c = getPearsonType5Controls();
                break;
            case PearsonType6:
                c = getPearsonType6Controls();
                break;
            case Poisson:
                c = getPoissonControls();
                break;
            case ShiftedGeometric:
                c = getShiftedGeometricControls();
                break;
            case Triangular:
                c = getTriangularControls();
                break;
            case Uniform:
                c = getUniformControls();
                break;
            case Weibull:
                c = getWeibullControls();
                break;
            default:
                throw new IllegalArgumentException("Invalid random variable type: " + type);
        }

        return c;
    }

    /**
     *
     * @param controls the controls to used to make the random variable
     * @return the random variable
     */
    public static RVariableIfc getRandomVariable(Controls controls){
        Objects.requireNonNull(controls, "The supplied controls was null");
        String name = controls.getName();
        RVType rvType = RVType.valueOf(name);
        return getRandomVariable(rvType, controls);
    }

    /**
     *
     * @param type the type of the random variable
     * @param controls the controls to used to make the random variable
     * @return the random variable
     */
    public static RVariableIfc getRandomVariable(RVType type, Controls controls) {
        Objects.requireNonNull(controls, "The supplied controls was null");
        Objects.requireNonNull(type, "The supplied type was null");
        if (!type.name().equals(controls.getName())) {
            throw new IllegalArgumentException("The random variable type does not match the control name");
        }
        RVariableIfc rv;
        switch (type) {
            case Bernoulli:
                rv = makeBernoulli(controls);
                break;
            case Beta:
                rv = makeBeta(controls);
                break;
            case Binomial:
                rv = makeBinomial(controls);
                break;
            case ChiSquared:
                rv = makeChiSquared(controls);
                break;
            case Constant:
                rv = makeConstant(controls);
                break;
            case DUniform:
                rv = makeDUniform(controls);
                break;
            case DEmpirical:
                rv = makeDEmpirical(controls);
                break;
            case Exponential:
                rv = makeExponential(controls);
                break;
            case Gamma:
                rv = makeGamma(controls);
                break;
            case GeneralizedBeta:
                rv = makeGeneralizedBeta(controls);
                break;
            case Geometric:
                rv = makeGeometric(controls);
                break;
            case JohnsonB:
                rv = makeJohnsonB(controls);
                break;
            case Laplace:
                rv = makeLaplace(controls);
                break;
            case LogLogistic:
                rv = makeLogLogistic(controls);
                break;
            case Lognormal:
                rv = makeLognormal(controls);
                break;
            case NegativeBinomial:
                rv = makeNegativeBinomial(controls);
                break;
            case Normal:
                rv = makeNormal(controls);
                break;
            case PearsonType5:
                rv = makePearsonType5(controls);
                break;
            case PearsonType6:
                rv = makePearsonType6(controls);
                break;
            case Poisson:
                rv = makePoisson(controls);
                break;
            case ShiftedGeometric:
                rv = makeShiftedGeometric(controls);
                break;
            case Triangular:
                rv = makeTriangular(controls);
                break;
            case Uniform:
                rv = makeUniform(controls);
                break;
            case Weibull:
                rv = makeWeibull(controls);
                break;
            default:
                throw new IllegalArgumentException("Invalid random variable type: " + type);
        }
        return rv;
    }

    private static RVariableIfc makeDEmpirical(Controls controls) {
        double[] values = controls.getDoubleArrayControl("values");
        double[] cdf = controls.getDoubleArrayControl("cdf");
        return new DEmpiricalRV(values, cdf);
    }

    private static RVariableIfc makeBernoulli(Controls controls) {
        double probOfSuccess = controls.getDoubleControl("ProbOfSuccess");
        return new BernoulliRV(probOfSuccess);
    }

    private static RVariableIfc makeBeta(Controls controls) {
        double alpha1 = controls.getDoubleControl("alpha1");
        double alpha2 = controls.getDoubleControl("alpha2");
        return new BetaRV(alpha1, alpha2);
    }

    private static RVariableIfc makeBinomial(Controls controls) {
        double probOfSuccess = controls.getDoubleControl("ProbOfSuccess");
        int numTrials = controls.getIntegerControl("NumTrials");
        return new BinomialRV(probOfSuccess, numTrials);
    }

    private static RVariableIfc makeChiSquared(Controls controls) {
        double dof = controls.getDoubleControl("dof");
        return new ChiSquaredRV(dof);
    }

    private static RVariableIfc makeConstant(Controls controls) {
        Double value = controls.getDoubleControl("value");
        return new ConstantRV(value);
    }

    private static RVariableIfc makeDUniform(Controls controls) {
        int min = controls.getIntegerControl("min");
        int max = controls.getIntegerControl("max");
        return new DUniformRV(min, max);
    }

    private static RVariableIfc makeExponential(Controls controls) {
        double mean = controls.getDoubleControl("mean");
        return new ExponentialRV(mean);
    }

    private static RVariableIfc makeGamma(Controls controls) {
        double scale = controls.getDoubleControl("scale");
        double shape = controls.getDoubleControl("shape");
        return new GammaRV(shape, scale);
    }

    private static RVariableIfc makeGeneralizedBeta(Controls controls) {
        double alpha1 = controls.getDoubleControl("alpha1");
        double alpha2 = controls.getDoubleControl("alpha2");
        double min = controls.getDoubleControl("min");
        double max = controls.getDoubleControl("max");
        return new GeneralizedBetaRV(alpha1, alpha2, min, max);
    }

    private static RVariableIfc makeGeometric(Controls controls) {
        double probOfSuccess = controls.getDoubleControl("ProbOfSuccess");
        return new GeometricRV(probOfSuccess);
    }

    private static RVariableIfc makeJohnsonB(Controls controls) {
        double alpha1 = controls.getDoubleControl("alpha1");
        double alpha2 = controls.getDoubleControl("alpha2");
        double min = controls.getDoubleControl("min");
        double max = controls.getDoubleControl("max");
        return new JohnsonBRV(alpha1, alpha2, min, max);
    }

    private static RVariableIfc makeLaplace(Controls controls) {
        double scale = controls.getDoubleControl("scale");
        double mean = controls.getDoubleControl("mean");
        return new LaplaceRV(mean, scale);
    }

    private static RVariableIfc makeLogLogistic(Controls controls) {
        double scale = controls.getDoubleControl("scale");
        double shape = controls.getDoubleControl("shape");
        return new LogLogisticRV(shape, scale);
    }

    private static RVariableIfc makeLognormal(Controls controls) {
        double mean = controls.getDoubleControl("mean");
        double variance = controls.getDoubleControl("variance");
        return new LognormalRV(mean, variance);
    }

    private static RVariableIfc makeNegativeBinomial(Controls controls) {
        double probOfSuccess = controls.getDoubleControl("ProbOfSuccess");
        double numSuccesses = controls.getDoubleControl("NumSuccesses");
        return new NegativeBinomialRV(probOfSuccess, numSuccesses);
    }

    private static RVariableIfc makeNormal(Controls controls) {
        double mean = controls.getDoubleControl("mean");
        double variance = controls.getDoubleControl("variance");
        return new NormalRV(mean, variance);
    }

    private static RVariableIfc makePearsonType5(Controls controls) {
        double scale = controls.getDoubleControl("scale");
        double shape = controls.getDoubleControl("shape");
        return new PearsonType5RV(shape, scale);
    }

    private static RVariableIfc makePearsonType6(Controls controls) {
        double alpha1 = controls.getDoubleControl("alpha1");
        double alpha2 = controls.getDoubleControl("alpha2");
        double beta = controls.getDoubleControl("beta");
        return new PearsonType6RV(alpha1, alpha2, beta);
    }

    private static RVariableIfc makePoisson(Controls controls) {
        double mean = controls.getDoubleControl("mean");
        return new PoissonRV(mean);
    }

    private static RVariableIfc makeShiftedGeometric(Controls controls) {
        double probOfSuccess = controls.getDoubleControl("ProbOfSuccess");
        return new ShiftedGeometricRV(probOfSuccess);
    }

    private static RVariableIfc makeTriangular(Controls controls) {
        double mode = controls.getDoubleControl("mode");
        double min = controls.getDoubleControl("min");
        double max = controls.getDoubleControl("max");
        return new TriangularRV(min, mode, max);
    }

    private static RVariableIfc makeUniform(Controls controls) {
        double min = controls.getDoubleControl("min");
        double max = controls.getDoubleControl("max");
        return new UniformRV(min, max);
    }

    private static RVariableIfc makeWeibull(Controls controls) {
        double scale = controls.getDoubleControl("scale");
        double shape = controls.getDoubleControl("shape");
        return new WeibullRV(shape, scale);
    }
}
