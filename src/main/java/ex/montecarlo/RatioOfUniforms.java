/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.montecarlo;

import jsl.utilities.math.FunctionIfc;
import jsl.utilities.random.distributions.Uniform;

/**
 *
 * @author rossetti
 */
public class RatioOfUniforms {

    protected Uniform uCDF;

    protected Uniform vCDF;

    protected FunctionIfc r;

    public RatioOfUniforms(double umax, double vmin, double vmax, FunctionIfc f) {
        uCDF = new Uniform(0.0, umax);
        vCDF = new Uniform(vmin, vmax);
        r = f;
    }

    public double getValue() {
        while (true) {
            double u = uCDF.getValue();
            double v = vCDF.getValue();
            double z = v / u;
            if (u * u < r.fx(z)) {
                return z;
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        RatioOfUniforms ru = new RatioOfUniforms(1.0, 0.0, 1.0, new Example88());
        int n = 10000;
        for (int i = 1; i <= n; i++) {
            System.out.println(ru.getValue());
        }

    }

    public static class Example88 implements FunctionIfc {

        public double fx(double x) {
            if ((0.0 <= x) && (x <= 1.0)) {
                return x * x;
            } else {
                return 0.0;
            }
        }
    }
}
