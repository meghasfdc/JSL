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
package jsl.observers;

import java.io.PrintWriter;
import java.util.Date;
import jsl.modeling.ModelElement;
import jsl.utilities.reporting.JSL;

/**
 *
 */
public class ModelActionObserver extends ModelElementObserver {

    private PrintWriter out;

        /**
     *
     */
    public ModelActionObserver() {
        this(null, JSL.out);
    }


    /**
     *
     */
    public ModelActionObserver(PrintWriter out) {
        this(null, out);
    }

    /**
     * @param name
     */
    public ModelActionObserver(String name, PrintWriter out) {
        super(name);
        this.out = out;
        
        out.println(new Date());
        out.println();
    }

    protected void beforeExperiment(ModelElement m, Object arg) {
        out.println("beforeExperiment " + m);
    }

    protected void beforeReplication(ModelElement m, Object arg) {
        out.println("beforeReplication " + m);
    }

    protected void initialize(ModelElement m, Object arg) {
        out.println("initialize " + m);
    }

    protected void montecarlo(ModelElement m, Object arg) {
        out.println("montecarlo " + m);
    }

    protected void update(ModelElement m, Object arg) {
        out.println("update " + m);
    }

    protected void warmUp(ModelElement m, Object arg) {
        out.println("warmUp " + m);
    }

    protected void timedUpdate(ModelElement m, Object arg) {
        out.println("timedUpdate " + m);
    }

    protected void batch(ModelElement m, Object arg) {
        out.println("batch " + m);
    }

    protected void replicationEnded(ModelElement m, Object arg) {
        out.println("afterExperiment " + m);
    }

    protected void afterReplication(ModelElement m, Object arg) {
        out.println("afterReplication " + m);
    }

    protected void afterExperiment(ModelElement m, Object arg) {
        out.println("afterExperiment " + m);
    }
}
