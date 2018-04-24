package jsl.utilities.random.rvariable;

import java.util.ArrayList;
import java.util.List;

/**
 *  An interface for getting multi-variable generate, each generate has many values
 *  held in an array
 */
public interface MVSampleIfc {

    /**
     *
     * @return generates a generate of random values
     */
    double[] sample();

    /**
     * Generates a random generate of the give size
     *
     * @param sampleSize the amount to fill
     * @return A list holding the generate
     */
    default List<double[]> sample(int sampleSize) {
        List<double[]> list = new ArrayList<>();
        for (int i = 0; i < sampleSize; i++) {
            list.add(sample());
        }
        return (list);
    }

    /**
     * Fills the supplied array with a random generate
     *
     * @param values the list to fill
     */
    default void sample(List<double[]> values) {
        if (values == null) {
            throw new IllegalArgumentException("The supplied list was null");
        }
        for (int i = 0; i < values.size(); i++) {
            values.add(sample());
        }
    }
}
