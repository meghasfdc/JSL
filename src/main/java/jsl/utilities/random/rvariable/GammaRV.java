package jsl.utilities.random.rvariable;

import jsl.utilities.random.distributions.Gamma;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *  Gamma(shape, scale) random variable
 */
public final class GammaRV extends AbstractRVariable {

    private final Gamma myGamma;

    public GammaRV(double shape, double scale){
        this(shape, scale, RNStreamFactory.getDefault().getStream());
    }

    public GammaRV(double shape, double scale, RngIfc rng){
        super(rng);
        myGamma = new Gamma(shape, scale);
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final GammaRV newInstance(RngIfc rng){
        return new GammaRV(this.getShape(), this.getScale(), rng);
    }

    @Override
    public String toString() {
        return "GammaRV{" +
                "shape=" + myGamma.getShape() +
                ", scale=" + myGamma.getScale() +
                '}';
    }

    /** Gets the shape
     * @return The shape parameter as a double
     */
    public double getShape() {
        return myGamma.getShape();
    }

    /** Gets the scale parameter
     * @return The scale parameter as a double
     */
    public double getScale() {
        return myGamma.getScale();
    }

    @Override
    protected final double generate() {
        double v = myGamma.invCDF(myRNG.randU01());
        return v;
    }
}
