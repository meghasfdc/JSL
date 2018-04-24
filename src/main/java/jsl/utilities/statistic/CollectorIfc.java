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
