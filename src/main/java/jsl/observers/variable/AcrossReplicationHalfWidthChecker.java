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
public class AcrossReplicationHalfWidthChecker extends ModelElementObserver {

    /** The confidence level for within replication
     *  half-width checking. The default is Statistic.DEFAULT_CONFIDENCE_LEVEL
     *
     */
    protected double myConfidenceLevel = Statistic.DEFAULT_CONFIDENCE_LEVEL;

    /** The desired half-width for stopping
     */
    protected double myDesiredHalfWidth = 1.0;

    /** Creates a new instance of StatisticalObserver
     * @param desiredHW the desired half-width
     */
    public AcrossReplicationHalfWidthChecker(double desiredHW) {
        this(desiredHW, null);
    }

    /** Creates a new instance of StatisticalObserver
     * @param desiredHW the desired half-width
     * @param name  the name of the checker
     */
    public AcrossReplicationHalfWidthChecker(double desiredHW, String name) {
        super(name);
        setDesiredHalfWidth(desiredHW);
    }

    /** Sets the confidence level for the statistic
     * @param level must be &gt; 0.0 and less than 1
     */
    public void setConfidenceLevel(double level) {
        if ((level <= 0.0) || (level >= 1.0)) {
            throw new IllegalArgumentException("Confidence Level must be (0,1)");
        }

        myConfidenceLevel = level;
    }

    public double getConfidenceLevel() {
        return (myConfidenceLevel);
    }

    /** Sets the desired half-width
     *
     * @param desiredHalfWidth the desired half-width
     */
    public final void setDesiredHalfWidth(double desiredHalfWidth) {
        if (desiredHalfWidth <= 0) {
            throw new IllegalArgumentException("Desired half-width must be > 0.");
        }

        myDesiredHalfWidth = desiredHalfWidth;
    }

    /** Gets the current desired half-width
     *
     * @return the current desired half-width
     */
    public final double getDesiredHalfWidth() {
        return (myDesiredHalfWidth);
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
        if (hw <= myDesiredHalfWidth) {
            s.end("Half-width conditon met for " + x.getName());
        }
    }
}
