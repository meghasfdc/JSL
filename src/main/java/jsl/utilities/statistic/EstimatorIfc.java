package jsl.utilities.statistic;

public interface EstimatorIfc {

    double getEstimate(double[] data);

    /**
     * A predefined EstimatorIfc that estimates the mean of the data
     */
    public static class Average implements EstimatorIfc {
        private Statistic s = new Statistic();
        @Override
        public double getEstimate(double[] data) {
            s.reset();
            s.collect(data);
            return s.getAverage();
        }
    }

    /**
     * A predefined EstimatorIfc that estimates the variance of the data
     */
    public static class Variance implements EstimatorIfc {
        private Statistic s = new Statistic();
        @Override
        public double getEstimate(double[] data) {
            s.reset();
            s.collect(data);
            return s.getVariance();
        }
    }

    /**
     * A predefined EstimatorIfc that estimates the median of the data
     */
    public static class Median implements EstimatorIfc {
        public double getEstimate(double[] data) {
            return Statistic.getMedian(data);
        }
    }

    /**
     * A predefined EstimatorIfc that estimates the minimum of the data
     */
    public static class Minimum implements EstimatorIfc {
        public double getEstimate(double[] data) {
            return Statistic.getMin(data);
        }
    }

    /**
     * A predefined EstimatorIfc that estimates the maximum of the data
     */
    public static class Maximum implements EstimatorIfc {
        public double getEstimate(double[] data) {
            return Statistic.getMax(data);
        }
    }
}
