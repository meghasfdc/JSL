package jsl.modeling.elements.variable;

public interface CounterActionIfc {

    /**
     * @return Returns the counter's limit.
     */
    public abstract double getCounterActionLimit();

    /** Returns true if the counter's last observer state is equal
     *  to COUNTER_LIMIT_REACHED
     *
     * @return
     */
    public abstract boolean checkForCounterLimitReachedState();

    /** Tells the Counter to add an CounterActionIfc that will
     *  automatically stop the replication when the counter limit is
     *  reached.
     *
     */
    public abstract void addStoppingAction();

    /** Adds a counter action listener.  It will be called if the counter's limit is
     *  set and it is reached.
     *
     * @param action
     * @return
     */
    public abstract boolean addCounterActionListener(
            CounterActionListenerIfc action);

    /** Removes the counter action listener
     * @param action
     * @return
     */
    public abstract boolean removeCounterActionListener(
            CounterActionListenerIfc action);
}
