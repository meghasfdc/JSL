package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

import java.util.Arrays;

/**
 *  Discrete Empirical Random Variable. Randomly selects from the supplied
 *  values in the value array according to the supplied CDF array. The probability array
 *  must have valid probability elements and last element equal to 1.
 *  Every element must be greater than or equal to the previous element in the CDF array.
 *  That is, monotonically increasing.
 */
public final class DEmpiricalRV extends AbstractRVariable {

    private final double[] myValues;
    private final double[] myCDF;

    /**
     * Randomly selects from the array using the supplied cdf
     *
     * @param values array to select from
     * @param cdf the cumulative probability associated with each element of
     * array
     */
    public DEmpiricalRV(double[] values, double[] cdf){
        this(values, cdf, RNStreamFactory.getDefault().getStream());
    }

    /**
     * Randomly selects from the array using the supplied cdf
     *
     * @param values array to select from
     * @param cdf the cumulative probability associated with each element of
     * array
     * @param rng the source of randomness
     */
    public DEmpiricalRV(double[] values, double[] cdf, RngIfc rng){
        super(rng);
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (values == null) {
            throw new IllegalArgumentException("The supplied values array was null");
        }
        if (cdf == null) {
            throw new IllegalArgumentException("The supplied cdf was null");
        }
        if (!JSLRandom.isValidCDF(cdf)) {
            throw new IllegalArgumentException("The supplied cdf was not valid");
        }
        if (values.length != cdf.length) {
            throw new IllegalArgumentException("The arrays did not have the same length.");
        }
        myValues = Arrays.copyOf(values, values.length);
        myCDF = Arrays.copyOf(cdf, cdf.length);
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final DEmpiricalRV newInstance(RngIfc rng){
        return new DEmpiricalRV(this.myValues, this.myCDF, rng);
    }

    /**
     *
     * @return the values to select from
     */
    public final double[] getValues(){
        return Arrays.copyOf(myValues, myValues.length);
    }

    /**
     *
     * @return the cdf to select with
     */
    public final double[] getCDF(){
        return Arrays.copyOf(myCDF, myCDF.length);
    }

    @Override
    public String toString() {
        return "DEmpiricalRV{" +
                "values=" + Arrays.toString(myValues) +
                ", cdf=" + Arrays.toString(myCDF) +
                '}';
    }

    @Override
    protected final double generate() {
        if (myCDF.length == 1) {
            return myValues[0];
        }
        int i = 0;
        double value = myValues[i];
        double u = myRNG.randU01();
        while (myCDF[i] <= u) {
            i = i + 1;
            value = myValues[i];
        }
        return value;
    }
}
