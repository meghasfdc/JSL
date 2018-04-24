package jsl.utilities.random;

public interface SampleIfc {

    /**
     *
     * @return generates a random value
     */
    double sample();

    /**
     * Generates a random generate of the give size
     *
     * @param sampleSize the amount to fill
     * @return A array holding the generate
     */
    default double[] sample(int sampleSize) {
        double[] x = new double[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            x[i] = sample();
        }
        return (x);
    }

    /**
     * Fills the supplied array with a random generate
     *
     * @param values the array to fill
     */
    default void sample(double[] values) {
        if (values == null) {
            throw new IllegalArgumentException("The supplied array was null");
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = sample();
        }
    }
}
