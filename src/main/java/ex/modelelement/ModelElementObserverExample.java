/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * Copyright (c) Manuel D. Rossetti (rossetti@uark.edu)
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
package ex.modelelement;

import jsl.modeling.ModelElement;
import jsl.observers.ModelElementObserver;

/**
 *
 * @author rossetti
 */
public class ModelElementObserverExample extends ModelElementObserver {

    @Override
    protected void afterExperiment(ModelElement m, Object arg) {
        super.afterExperiment(m, arg);
        System.out.println("*****In observer:");
        System.out.println("ModelElement: " + m.getName());
        System.out.println("after experiment ");
        System.out.println("time = " + m.getTime());
    }

    @Override
    protected void afterReplication(ModelElement m, Object arg) {
        super.afterReplication(m, arg);
        System.out.println("*****In observer:");
        System.out.println("ModelElement: " + m.getName());
        System.out.println("after replication ");
        System.out.println("time = " + m.getTime());
    }

    @Override
    protected void beforeExperiment(ModelElement m, Object arg) {
        super.beforeExperiment(m, arg);
        System.out.println("*****In observer:");
        System.out.println("ModelElement: " + m.getName());
        System.out.println("before experiment ");
        System.out.println("time = " + m.getTime());
    }

    @Override
    protected void beforeReplication(ModelElement m, Object arg) {
        super.beforeReplication(m, arg);
        System.out.println("*****In observer:");
        System.out.println("ModelElement: " + m.getName());
        System.out.println("before replication ");
        System.out.println("time = " + m.getTime());
    }

    @Override
    protected void initialize(ModelElement m, Object arg) {
        super.initialize(m, arg);
        System.out.println("*****In observer:");
        System.out.println("ModelElement: " + m.getName());
        System.out.println("initialize ");
        System.out.println("time = " + m.getTime());
    }

    @Override
    protected void replicationEnded(ModelElement m, Object arg) {
        super.replicationEnded(m, arg);
        System.out.println("*****In observer:");
        System.out.println("ModelElement: " + m.getName());
        System.out.println("replication ended ");
        System.out.println("time = " + m.getTime());
    }

    @Override
    protected void warmUp(ModelElement m, Object arg) {
        super.warmUp(m, arg);
        System.out.println("*****In observer:");
        System.out.println("ModelElement: " + m.getName());
        System.out.println("warm up ");
        System.out.println("time = " + m.getTime());
    }

    @Override
    protected void timedUpdate(ModelElement m, Object arg) {
        super.timedUpdate(m, arg);
        System.out.println("*****In observer:");
        System.out.println("ModelElement: " + m.getName());
        System.out.println("timed update ");
        System.out.println("time = " + m.getTime());
    }
}
