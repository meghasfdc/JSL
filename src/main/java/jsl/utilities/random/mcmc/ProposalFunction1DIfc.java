package jsl.utilities.random.mcmc;

/**
 *  For use with MetropolisHastings1D. Represents the proposal function
 *
 */
public interface ProposalFunction1DIfc {

    /** The ratio of g(y,x)/g(x,y).  The ratio of the proposal function
     *  evaluated at x = current and y = proposed, where g() is some
     *  proposal function of x and y. The implementor should ensure
     *  that the returned ratio is a valid double
     *
     * @param current the x to evaluate
     * @param proposed the y to evaluate
     * @return the ratio of the proposal function
     */
    double getProposalRatio(double current, double proposed);

    /**
     *
     * @param current the current state value of the chain (i.e. x)
     * @return the generated possible state (i.e. y) which may or may not be accepted
     */
    double generateProposedGivenCurrent(double current);
}
