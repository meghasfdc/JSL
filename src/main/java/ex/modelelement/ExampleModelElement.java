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

import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.Simulation;

/**
 *
 * @author rossetti
 */
public class ExampleModelElement extends ModelElement {

    public ExampleModelElement(ModelElement parent) {
        this(parent, null);
    }

    public ExampleModelElement(ModelElement parent, String name) {
        super(parent, name);
    }

    @Override
    protected void afterExperiment() {
        super.afterExperiment();
        System.out.println("In afterExperiment()");
        System.out.println("time = " + getTime());
    }

    @Override
    protected void afterReplication() {
        super.afterReplication();
        System.out.println("In afterReplication()");
        System.out.println("time = " + getTime());
    }

    @Override
    protected void beforeExperiment() {
        super.beforeExperiment();
        System.out.println("In beforeExperiment()");
        System.out.println("time = " + getTime());
    }

    @Override
    protected void beforeReplication() {
        super.beforeReplication();
        System.out.println("In beforeReplication()");
        System.out.println("time = " + getTime());
    }

    @Override
    protected void initialize() {
        super.initialize();
        System.out.println("In initialize()");
        System.out.println("time = " + getTime());
    }

    @Override
    protected void replicationEnded() {
        super.replicationEnded();
        System.out.println("In replicationEnded()");
        System.out.println("time = " + getTime());
    }

    @Override
    protected void warmUp() {
        super.warmUp();
        System.out.println("In warmUp()");
        System.out.println("time = " + getTime());
    }

    @Override
    protected void timedUpdate() {
        super.timedUpdate();
        System.out.println("In timedUpdate()");
        System.out.println("time = " + getTime());
    }

    public static void main(String[] args) {

        Simulation s = new Simulation("Example ModelElement");

        // create the containing model
        Model m = s.getModel();

        // create the model element and attach it to the model
        ExampleModelElement me = new ExampleModelElement(m);
        me.setTimedUpdateInterval(25.0);
        ModelElementObserverExample o = new ModelElementObserverExample();
        me.addObserver(o);

        // set the parameters of the experiment
        s.setNumberOfReplications(2);
        s.setLengthOfWarmUp(40.0);
        s.setLengthOfReplication(100.0);

        // tell the experiment to run
        s.run();

    }

}
