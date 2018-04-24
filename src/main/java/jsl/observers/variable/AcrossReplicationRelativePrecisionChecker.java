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
package jsl.observers.variable;

import jsl.utilities.statistic.*;
import jsl.modeling.elements.variable.*;
import jsl.modeling.*;
import jsl.observers.ModelElementObserver;

/**
 *
 */
public class AcrossReplicationRelativePrecisionChecker extends ModelElementObserver {

    /** The confidence level for within replication
     *  half-width checking. The default is Statistic.DEFAULT_CONFIDENCE_LEVEL
     *
     */
    protected double myConfidenceLevel = Statistic.DEFAULT_CONFIDENCE_LEVEL;

    /** The desired relative precision for stopping
     */
    protected double myDesiredRelativePrecision = 1.0;

    /** Creates a new instance of StatisticalObserver
     * @param desiredPrecision desired precision
     */
    public AcrossReplicationRelativePrecisionChecker(double desiredPrecision) {
        this(desiredPrecision, null);
    }

    /** Creates a new instance of StatisticalObserver
     * @param desiredPrecision the desired precision
     * @param name the name
     */
    public AcrossReplicationRelativePrecisionChecker(double desiredPrecision, String name) {
        super(name);
        setDesiredRelativePrecision(desiredPrecision);
    }

        /** Sets the confidence level for the statistic
     * @param alpha must be &gt; 0.0
     */
    public void setConfidenceLevel(double alpha) {
        if ((alpha <= 0.0) || (alpha >= 1.0)) {
            throw new IllegalArgumentException("Confidence Level must be (0,1)");
        }

        myConfidenceLevel = alpha;
    }

    /**
     * 
     * @return  the confidence level
     */
    public double getConfidenceLevel() {
        return (myConfidenceLevel);
    }

    /** Sets the desired relative precision
     *
     * @param desiredPrecision the desired relative precision
     */
    public final void setDesiredRelativePrecision(double desiredPrecision) {
        if (desiredPrecision <= 0) {
            throw new IllegalArgumentException("Desired relative precision must be > 0.");
        }

        myDesiredRelativePrecision = desiredPrecision;
    }

    /** Gets the current desired half-width
     *
     * @return the current desired half-width
     */
    public final double getDesiredRelativePrecision() {
        return (myDesiredRelativePrecision);
    }

    @Override
    protected void afterReplication(ModelElement m, Object arg) {
        ResponseVariable x = (ResponseVariable) m;
        Simulation s = x.getSimulation();
        
        if (s == null) {
            return;
        }
        if (s.getCurrentReplicationNumber() <= 2.0) {
            return;
        }

        StatisticAccessorIfc stat = x.getAcrossReplicationStatistic();
        double hw = stat.getHalfWidth(getConfidenceLevel());
        double xbar = stat.getAverage();
        if (hw <= myDesiredRelativePrecision * xbar) {
            s.end("Relative precision conditon met for " + x.getName());
        }
    }
}
