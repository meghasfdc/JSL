package jsl.utilities.random.sp;

import jsl.utilities.random.AbstractRandom;
import jsl.utilities.random.distributions.Bernoulli;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;
import jsl.utilities.statistic.Statistic;

/** Represents a two state Markov chain
 *  States = {0,1}
 *  User supplies
 *  P{X(i) = 1| X(i-1) = 1} Probability of success after success
 *  P{X(i) = 1| X(i-1) = 0} Probability of success after failure
 *
 * @author rossetti
 */
public class TwoStateMarkovChain extends AbstractRandom implements TwoStateMarkovChainIfc {

    private double myP1;

    private double myP0;

    private Bernoulli myB11;

    private Bernoulli myB01;

    private int myState;

    private int myInitialState;

    public TwoStateMarkovChain() {
        this(1, 0.5, 0.5);
    }

    public TwoStateMarkovChain(double p11, double p01) {
        this(1, p11, p01);
    }

    public TwoStateMarkovChain(double[] parameters) {
        this((int) parameters[2], parameters[0], parameters[1]);
    }

    public TwoStateMarkovChain(int initialState, double p11, double p01) {
        this(initialState, p11, p01, RNStreamFactory.getDefault().getStream());
    }

    public TwoStateMarkovChain(int initialState, double p11, double p01, RngIfc rng) {
        setInitialState(initialState);
        setProbabilities(p11, p01, rng);
        reset();
    }

    public void setInitialState(int initialState) {
        if ((initialState < 0) || (initialState > 1)) {
            throw new IllegalArgumentException("The initial state must be 0 or 1");
        }
        myInitialState = initialState;
    }

    public int getInitialState() {
        return myInitialState;
    }

    public void setState(int state) {
        if ((state < 0) || (state > 1)) {
            throw new IllegalArgumentException("The state must be 0 or 1");
        }
        myInitialState = state;
    }

    public int getState() {
        return myState;
    }

    public void setProbabilities(double p11, double p01) {
        setProbabilities(p11, p01, RNStreamFactory.getDefault().getStream());
    }

    public void setProbabilities(double p11, double p01, RngIfc rng) {
        if ((p11 < 0.0) || (p11 > 1.0)) {
            throw new IllegalArgumentException("P11 must be [0,1]");
        }
        if ((p01 < 0.0) || (p01 > 1.0)) {
            throw new IllegalArgumentException("P11 must be [0,1]");
        }

        if (myB11 == null) {
            myB11 = new Bernoulli(p11, rng);
        } else {
            myB11.setProbabilityOfSuccess(p11);
        }

        if (myB01 == null) {
            myB01 = new Bernoulli(p01, rng);
        } else {
            myB01.setProbabilityOfSuccess(p01);
        }

        myP0 = 1 - (p01 / (1 - p11 + p01));
        myP1 = 1 - myP0;
    }

    /** Sets the state back to the initial state
     *
     */
    public void reset() {
        myState = myInitialState;
    }

    public double getValue() {
        if (myState == 1) {
            myState = (int) myB11.getValue();
        } else {
            myState = (int) myB01.getValue();
        }
        return myState;
    }

    public double getP0() {
        return myP0;
    }

    public double getP1() {
        return myP1;
    }

    public double getP01() {
        return myB01.getProbabilityOfSuccess();
    }

    public double getP11() {
        return myB11.getProbabilityOfSuccess();
    }

    /** The array consists of:
     *  p[0] = p11
     *  p[1] = p01
     *  p[2] = initial state
     *
     * @return
     */
    public double[] getParameters() {
        double[] p = {getP11(), getP01(), getInitialState()};
        return p;
    }

    /** Supply an array with:
     *  p[0] = p11
     *  p[1] = p01
     *  p[2] = initial state
     * 
     * @param parameters
     */
    @Override
    public void setParameters(double[] parameters) {
        setProbabilities(parameters[0], parameters[1]);
        setInitialState((int) parameters[2]);
    }

    @Override
    public void advanceToNextSubstream() {
        myB11.advanceToNextSubstream();
        myB01.advanceToNextSubstream();
    }

    @Override
    public void resetStartStream() {
        myB11.resetStartStream();
        myB01.resetStartStream();
    }

    @Override
    public void resetStartSubstream() {
        myB11.resetStartSubstream();
        myB01.resetStartSubstream();
    }

    @Override
    public void setAntitheticOption(boolean flag) {
        myB11.setAntitheticOption(flag);
        myB01.setAntitheticOption(flag);
    }

    @Override
    public boolean getAntitheticOption() {
        boolean b = myB11.getAntitheticOption();
        b = b && myB01.getAntitheticOption();
        return b;
    }

    /** The instance is initialized at getInitialState()
     *
     * @return
     */
    @Override
    public TwoStateMarkovChain newInstance() {
        return (new TwoStateMarkovChain(getInitialState(), getP11(), getP01()));
    }

    /** The instance is initialized at getInitialState()
     *
     * @return
     */
    @Override
    public TwoStateMarkovChain newInstance(RngIfc rng) {
        return (new TwoStateMarkovChain(getInitialState(), getP11(), getP01(), rng));
    }

    public static void main(String[] args) {
        // TODO code application logic here
        Statistic s = new Statistic();
        TwoStateMarkovChain d = new TwoStateMarkovChain();
        for (int i = 1; i <= 20000; i++) {
            double x = d.getValue();
            s.collect(x);
            //System.out.println(x);
        }
        System.out.println(s);
    }
}
