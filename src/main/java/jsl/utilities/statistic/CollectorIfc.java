/*
 * Copyright (c) 2018. Manuel D. Rossetti, manuelrossetti@gmail.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package jsl.utilities.statistic;

import jsl.utilities.GetValueIfc;
import jsl.utilities.IdentityIfc;
import jsl.utilities.random.SampleIfc;

/**
 * This interface represents a general set of methods for data collection The
 * collect() method takes in the supplied data and collects it in some manner as
 * specified by the collector. The collect() method may return true if the
 * collector can continue collecting and false if subsequent calls to collect()
 * will have no effect. The method isTurnedOff() will indicate if collection has
 * been turned off. That is, if at some point collect() returned false.
 *
 * @author rossetti
 *
 */
public interface CollectorIfc extends IdentityIfc {

    /**
     * Indicates if collector can continue collecting
     *
     * @return true if collector has been turned off
     */
    public boolean isTurnedOff();

    /**
     * Indicates if collector can continue collecting
     *
     * @return true if collector is on
     */
    public boolean isTurnedOn();

    /**
     * Should have the effect of turning off collection. That is, calls to
     * collect() have no effect.
     *
     */
    public void turnOff();

    /**
     * Should have the effect of turning on collection. That is, calls to
     * collect() have an effect.
     *
     */
    public void turnOn();

    /**
     * Collects statistics on the values returned by the supplied GetValueIfc
     *
     * @param v
     * @return true if collection can continue, false if collector is turned off
     */
    public boolean collect(GetValueIfc v);

    /**
     * Collects statistics on the values returned by the supplied GetValueIfc
     *
     * @param v
     * @param weight
     * @return true if collection can continue, false if collector is turned off
     */
    public boolean collect(GetValueIfc v, double weight);

    /**
     * Collects statistics on the boolean value true = 1.0, false = 0.0
     *
     * @param value
     * @return true if collection can continue, false if collector is turned off
     */
    public boolean collect(boolean value);

    /**
     * Collects statistics on the boolean value true = 1.0, false = 0.0
     *
     * @param value
     * @param weight
     * @return true if collection can continue, false if collector is turned off
     */
    public boolean collect(boolean value, double weight);

    /**
     * Collect statistics on the supplied value
     *
     * @param value a double representing the observation
     * @return true if collection can continue, false if collector is turned off
     */
    public boolean collect(double value);

    /**
     * Collects statistics on values in the supplied array. If collector
     * is turned off during collection, then not all values are collected.
     *
     * @param values
     * @return true if collection can continue, false if collector was turned off
     */
    public boolean collect(double[] values);

    /**
     * Collects statistics on the values in the supplied array. The lengths of
     * the arrays must be the same. If collector
     * is turned off during collection, then not all values are collected.
     *
     * @param x the values
     * @param w the weights
     * @return true if collection can continue, false if collector was turned off
     */
    public boolean collect(double[] x, double[] w);

    /**
     * Collect weighted statistics on the supplied value using the supplied
     * weight
     *
     * @param x a double representing the observation
     * @param weight a double to be used to weight the observation
     * @return true if collection can continue, false if collector is turned off
     */
    public boolean collect(double x, double weight);

    /**
     * Resets the collection as if no data had been collected. Collector
     * is assumed to be turned on.
     */
    public void reset();
}
