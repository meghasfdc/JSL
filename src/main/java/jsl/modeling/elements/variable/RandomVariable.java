/*
 * Copyright (c) 2007, Manuel D. Rossetti (rossetti@uark.edu)
 *
 * Contact:
 *	Manuel D. Rossetti, Ph.D., P.E.
 *	Department of Industrial Engineering
 *	University of Arkansas
 *	4207 Bell Engineering Center
 *	Fayetteville, AR 72701
 *	Phone: (479) 575-6756
 *	Email: rossetti@uark.edu
 *	Web: www.uark.edu/~rossetti
 *
 * This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 * of Java classes that permit the easy development and execution of discrete event
 * simulation programs.
 *
 * The JSL is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The JSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the JSL (see file COPYING in the distribution);
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA, or see www.fsf.org
 *
 */
package jsl.modeling.elements.variable;

import java.util.logging.Level;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.RandomElementIfc;
import jsl.utilities.random.*;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;
import jsl.utilities.reporting.*;

/** A random variable (RandomVariable) is a function that
 *  maps a probability space to a real number.    A random variable uses
 *  a RandomIfc to provide the underlying mapping to a real number
 *  via the getValue() method.
 *  
 *  To construct a RandomVariable the user must provide an instance of
 *  a class that implements the RandomIfc interface as the initial random source.
 *  This source is used to initialize the source of randomness for each
 *  replication.  
 *  
 *  WARNING:  For efficiency, this class uses the direct
 *  reference to the supplied random source.  Thus, a change to that instance
 *  will be reflected in the use of that instance in this class.  Thus, if
 *  the client changes, for example the parameters associated with the initial source instance,
 *  those parameter changes will be reflected in the use of the source by this class.
 *  This may result in replications not starting in exactly the same state.  Since
 *  the source of randomness during a replication will be the same as the initial source, because
 *  it is reset at the start of each replication, any changes to the initial source 
 *  during a replication will also be reflected in the replication. This may not be
 *  what the client intended; however, since it is unusual for a client to change
 *  the parameters of the initial source during a replication, the direct
 *  reference is utilized in order to save in the creation of extra object references.  
 *  To have this class use a separate instance of the supplied
 *  source with no dependence, call the constructor with initialSource.newInstance(), which will supply
 *  a duplicate of the source as a different object. Any changes to the initial source, will thus
 *  not be reflected in the use of this class.
 *  
 *  The initial source is used to set up the source used during the replication.  If the
 *  client changes the reference to the initial source, this change does not become effective
 *  until the beginning of the next replication.  In other words, the random source used
 *  during the replication is unaffected. However, as previously noted, if the client only changes
 *  the parameters of the initial source, these will be reflected immediately.  
 *  
 *  The client may change the parameters of the source being used during a replication.
 *  If this occurs, those changes are immediately reflected within the current replication; however,
 *  those changes are discarded for the next replication since the initial source will be
 *  used to ensure that each replication starts with the same characteristics. However, since during 
 *  a replication the random source is the same as the initial random source (unless changed by
 *  setRandomSource()), changes to the parameters with this method will also
 *  change the *current* parameters of the initial random source. It will not change the parameters
 *  available when the initial random source was set, those original parameters will be
 *  used to reset the sources appropriately so that each replication starts with the same parameter
 *  settings.
 *  
 */
public class RandomVariable extends Variable implements RandomIfc, RandomElementIfc {

    /** indicates whether or not the random variable's
     *  distribution has it stream reset to the default
     *  stream, or not prior to each experiment.  Resetting
     *  allows each experiment to use the same underlying random numbers
     *  i.e. common random numbers, this is the default
     * 
     *  Setting it to true indicates that it does reset
     */
    protected boolean myResetStartStreamOption;

    /** indicates whether or not the random variable's
     *  distribution has it stream reset to the next sub-stream
     *  stream, or not, prior to each replication.  Resetting
     *  allows each replication to better ensure that each
     *  replication will be start at the same place in the
     *  sub-streams, thereby, improving synchronization when using
     *  common random numbers.
     *
     *  Setting it to true indicates that it does jump to
     *  the next sub-stream, true is the default
     */
    protected boolean myAdvanceToNextSubStreamOption;

    /** RandomIfc provides a reference to the underlying source of randomness
     *  during the replication
     */
    protected RandomIfc myRandomSource;

    /** RandomIfc provides a reference to the underlying source of randomness
     *  to initialize each replication.
     */
    protected RandomIfc myInitialRandomSource;

    /** The values of the parameters of the initial random source
     *  These are remembered to allow the initial random source
     *  to be reset to the initial parameters at the beginning of 
     *  each replication (if they were changed during the replication)
     * 
     */
    protected double[] myInitialParameters;

    /** During a replication, the client may change the parameters of
     *  the initial source.  This flag indicates whether or not
     *  the original parameters of the initial source should be used
     *  to reset the parameters of the initial source to the originals.
     *  The default option is true (it will reset the parameters).
     *  This is only applicable if the parameters of the source changes. 
     *  If the reference to the initial source changes then the changed
     *  reference will be used. 
     * 
     */
    protected boolean myResetInitialParametersFlag = true;

    /** If the parameters of the initial source were changed during the replication
     *  and reset initial parameters flag is true, then a warning message will
     *  occur.  This flag turns off that default warning message.
     * 
     */
    protected boolean myResetInitialParametersWarningFlag = true;

    /**
     *  A flag to indicate whether or not a ResponseVariable should be created
     *  and used to capture the randomly generated values. Useful for control variate implementation
     */
    protected boolean myCaptureResponseFlag = false;

    /**
     *  Used to capture the randomly generated values if the capture response flag is true
     */
    protected ResponseVariable myResponse;

    /** Constructs a RandomVariable given the supplied reference to the underlying source of randomness
     * Throws a NullPointerException if the supplied randomness is null
     *
     * @param parent The parent ModelElement
     * @param initialSource The reference to the underlying source of randomness
     */
    public RandomVariable(ModelElement parent, RandomIfc initialSource) {
        this(parent, initialSource, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null);
    }

    /** Constructs a RandomVariable given the supplied reference to the underlying source of randomness
     * Throws a NullPointerException if the supplied randomness is null
     *
     * @param parent The parent ModelElement
     * @param initialSource The reference to the underlying source of randomness
     * @param name A string to label the RandomVariable
     */
    public RandomVariable(ModelElement parent, RandomIfc initialSource, String name) {
        this(parent, initialSource, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, name);
    }

    /** Constructs a RandomVariable given the supplied reference to the underlying source of randomness
     * Throws a NullPointerException if the supplied randomness is null
     *
     * @param parent The parent ModelElement
     * @param initialSource The reference to the underlying source of randomness
     * @param lowerLimit the lower limit on the range for the variable, must be &lt; upperLimit
     * @param upperLimit the upper limit on the range for the variable
     */
    public RandomVariable(ModelElement parent, RandomIfc initialSource, 
            double lowerLimit, double upperLimit) {
        this(parent, initialSource, Double.NaN, lowerLimit, upperLimit, null);
    }

    /** Constructs a RandomVariable given the supplied reference to the underlying source of randomness
     * Throws a NullPointerException if the supplied randomness is null
     *
     * @param parent The parent ModelElement
     * @param initialSource The reference to the underlying source of randomness
     * @param lowerLimit the lower limit on the range for the variable, must be &lt; upperLimit
     * @param upperLimit the upper limit on the range for the variable
     * @param name A string to label the RandomVariable
     */
    public RandomVariable(ModelElement parent, RandomIfc initialSource, 
            double lowerLimit, double upperLimit, String name) {
        this(parent, initialSource, Double.NaN, lowerLimit, upperLimit, name);
    }

    /** Constructs a RandomVariable given the supplied reference to the underlying source of randomness
     * Throws a NullPointerException if the supplied randomness is null
     *
     * @param parent The parent ModelElement
     * @param initialSource The reference to the underlying source of randomness
     * @param initialValue The initial value of the variable.  Must be within the range.
     * @param lowerLimit the lower limit on the range for the variable, must be &lt; upperLimit
     * @param upperLimit the upper limit on the range for the variable
     */
    public RandomVariable(ModelElement parent, RandomIfc initialSource, 
            double initialValue, double lowerLimit, double upperLimit) {
        this(parent, initialSource, Double.NaN, lowerLimit, upperLimit, null);
    }

    /** Constructs a RandomVariable given the supplied reference to the underlying source of randomness
     * Throws a NullPointerException if the supplied randomness is null
     *
     * @param parent The parent ModelElement
     * @param initialSource The reference to the underlying source of randomness
     * @param initialValue The initial value of the variable.  Must be within the range.
     * @param lowerLimit the lower limit on the range for the variable, must be &lt; upperLimit
     * @param upperLimit the upper limit on the range for the variable
     * @param name A string to label the RandomVariable
     */
    public RandomVariable(ModelElement parent, RandomIfc initialSource, 
            double initialValue, double lowerLimit, double upperLimit, String name) {
        super(parent, initialValue, lowerLimit, upperLimit, name);
        setInitialRandomSource(initialSource);
        myRandomSource = myInitialRandomSource;
        setWarmUpOption(false); // do not need to respond to warmup events
        setResetStartStreamOption(true);
        setResetNextSubStreamOption(true);
    }

    @Override
    public final boolean getResetStartStreamOption() {
        return myResetStartStreamOption;
    }

    @Override
    public final void setResetStartStreamOption(boolean b) {
        myResetStartStreamOption = b;
    }

    @Override
    public final boolean getResetNextSubStreamOption() {
        return myAdvanceToNextSubStreamOption;
    }

    @Override
    public final void setResetNextSubStreamOption(boolean b) {
        myAdvanceToNextSubStreamOption = b;
    }

    @Override
    public final void setAntitheticOption(boolean flag) {
        myRandomSource.setAntitheticOption(flag);
    }

    @Override
    public boolean getAntitheticOption() {
        return myRandomSource.getAntitheticOption();
    }

    @Override
    public final void advanceToNextSubstream() {
        myRandomSource.advanceToNextSubstream();
    }

    @Override
    public final void resetStartStream() {
        myRandomSource.resetStartStream();
    }

    @Override
    public final void resetStartSubstream() {
        myRandomSource.resetStartSubstream();
    }

    @Override
    public final double[] getParameters() {
        return myRandomSource.getParameters();
    }

    /** This changes the parameters of the random source being used during
     *  the replication.  The initial random source and its original parameters are used
     *  at the beginning of each replication to ensure that the same random
     *  source is used to start each replication.  Since during a replication
     *  the random source is the same as the initial random source (unless changed by
     *  setRandomSource()), changes to the parameters with this method will also
     *  change the parameters of the initial random source (with possible side effects to
     *  any other objects that also use the initial random source).
     *
     * @see jsl.utilities.random.ParametersIfc#setParameters(double[])
     */
    @Override
    public final void setParameters(double[] parameters) {
        myRandomSource.setParameters(parameters);
    }

    /** Gets the underlying RandomIfc for the RandomVariable. This is the
     *  source to which each replication will be initialized
     * @return a RandomIfc
     */
    public final RandomIfc getInitialRandomSource() {
        return (myInitialRandomSource);
    }

    /** Sets the underlying RandomIfc source for the RandomVariable. This is the
     *  source to which each replication will be initialized.  This is only used
     *  when the replication is initialized. Changing the reference has no effect 
     *  during a replication, since the random variable will continue to use
     *  the reference returned by getRandomSource().  Please also see the 
     *  discussion in the class documentation.
     *  
     *  WARNING: If this is used during a replication to change the characteristics of
     *  the random source, then each replication will not necessarily start in the
     *  same initial state.  It is recommended
     *  that this be used only prior to executing experiments.
     *  
     * Throws NullPointerExceptions if source is null.
     * @param source the reference to the random source
     */
    public final void setInitialRandomSource(RandomIfc source) {
        if (source == null) {
            throw new NullPointerException("RandomIfc source must be non-null");
        }
        myInitialRandomSource = source;
        myInitialParameters = source.getParameters();
    }

    /**
     *   Turns on the capturing of the generated random values with a ResponseVariable
     */
    public final void turnOnResponseCapture(){
        if (myCaptureResponseFlag == false){
            myCaptureResponseFlag = true;
            myResponse = new ResponseVariable(this, getName()+":Response");
        }
    }

    /**
     *
     * @return true means that the values are being captured via a ResponseVariable
     */
    public final boolean getCaptureResponseFlag(){
        return myCaptureResponseFlag;
    }

    /** Returns whether the reset initial parameters flag is true/false
     * 
     * @return true if on
     */
    public final boolean getResetInitialParametersFlag() {
        return myResetInitialParametersFlag;
    }

    /** Sets the option to reset initial parameters to original values
     *  of the initial source
     * 
     * @param option true means on
     */
    public final void setResetInitialParametersFlag(boolean option) {
        myResetInitialParametersFlag = option;
    }

    /** Returns whether the warning message is on 
     * 
     * @return true means on
     */
    public final boolean getResetInitialParametersWarningFlag() {
        return myResetInitialParametersWarningFlag;
    }

    /** Sets the option have a warning message if resetting the initial parameters to original values
     *  of the initial source
     * 
     * @param option true is on
     */
    public final void setResetInitialParametersWarningFlag(boolean option) {
        myResetInitialParametersWarningFlag = option;
    }

    /** Gets the underlying RandomIfc for the RandomVariable currently
     *  being used during the replication
     *  
     * @return a RandomIfc
     */
    public final RandomIfc getRandomSource() {
        return (myRandomSource);
    }

    /** Sets the underlying RandomIfc source for the RandomVariable.  This
     *  changes the source for the current replication only. The random
     *  variable will start to use this source immediately; however if
     *  a replication is started after this method is called, the random source
     *  will be reassigned to the initial random source before the next replication
     *  is executed.
     *  
     *  To set the random source for the entire experiment (all replications)
     *  use the setInitialRandomSource() method
     *  
     * Throws NullPointerExceptions if source is null.
     * @param source the reference to the random source
     */
    public final void setRandomSource(RandomIfc source) {
        if (source == null) {
            throw new NullPointerException("RandomIfc source must be non-null");
        }
        myRandomSource = source;
    }

    /** Each call to getValue() returns a new observation
     * 
     */
    @Override
    public double getValue() {
        setValue(myRandomSource.getValue());
        if (getCaptureResponseFlag()){
            myResponse.setValue(myValue);
        }
        return (myValue);
    }

    /** Returns the sum of n random draws of the random variable
     *  if n &lt;= 0, then the sum is 0.0
     * 
     * @param n the number to sum
     * @return the sum
     */
    public double getSumOfValues(int n) {
        double sum = 0.0;
        for (int i = 1; i <= n; i++) {
            sum = sum + getValue();
        }
        return sum;
    }

    public final double[] sample(int sampleSize) {
        double[] x = new double[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            x[i] = getValue();
        }
        return (x);
    }

    public final void sample(double[] values) {
        if (values == null) {
            throw new IllegalArgumentException("The supplied array was null");
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = getValue();
        }
    }

    @Override
    public final RandomVariable newInstance() {
        return newInstance(RNStreamFactory.getDefault().getStream());
    }

    @Override
    public final RandomVariable newInstance(RngIfc rng) {
        double iv = this.getInitialValue();
        double ll = this.getLowerLimit();
        double ul = this.getUpperLimit();
        RandomIfc r = this.getRandomSource().newInstance(rng);
        return (new RandomVariable(getParentModelElement(), r, iv, ll, ul));
    }

    @Override
    public String asString(){
        StringBuilder sb = new StringBuilder();
        sb.append(toString());
        sb.append(myRandomSource.toString());
        return sb.toString();
    }
    @Override
    protected void removedFromModel() {
        super.removedFromModel();
        myRandomSource = null;
        myInitialRandomSource = null;
        myInitialParameters = null;
    }

    /** before any replications reset the underlying random number generator to the
     *  starting stream
     *
     */
    @Override
    protected void beforeExperiment() {
        super.beforeExperiment();
        myInitialParameters = myInitialRandomSource.getParameters();
        myRandomSource = myInitialRandomSource;

        if (getResetStartStreamOption()) {
            resetStartStream();
        }

    }

    /** after each replication reset the underlying random number generator to the next
     *  sub-stream
     */
    @Override
    protected void afterReplication() {
        super.afterReplication();

        if (myRandomSource != myInitialRandomSource) {
            // the random source or the initial random source references
            // were changed during the replication
            // make sure that the random source is the same
            // as the initial random source for the next replication
            myRandomSource = myInitialRandomSource;
        } else {
            // the random source and the initial random source
            // object references are the same, however, the client
            // might have changed the parameters during the replication
            boolean changed = false;
            double[] current = myRandomSource.getParameters();
            for (int i = 0; i < current.length; i++) {
                if (current[i] != myInitialParameters[i]) {
                    changed = true;
                    break;
                }
            }

            if (changed) {
                // reset the parameters, and indicate warning
                if (myResetInitialParametersFlag) {
                    myInitialRandomSource.setParameters(myInitialParameters);
                    if (myResetInitialParametersWarningFlag) {
                        JSL.LOGGER.log(Level.WARNING, "The parameters for the random source of {0} were changed during the replication", getName());
                        JSL.LOGGER.warning("The parameters were reset to the values available from the last call to setInitialRandomSource() for the next replication.");
                    }
                }
            }
        }

        if (getResetNextSubStreamOption()) {
            advanceToNextSubstream();
        }

    }
}
