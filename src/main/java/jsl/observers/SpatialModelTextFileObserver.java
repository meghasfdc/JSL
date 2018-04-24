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

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.spatial.SpatialElementIfc;
import jsl.modeling.elements.spatial.SpatialModel;
import jsl.utilities.reporting.TextReport;

public class SpatialModelTextFileObserver extends TextReport implements Observer {

    protected Model myModel;

    protected SpatialModel mySpatialModel2D;

    protected boolean myFirstUpdateFlag = true;

    public SpatialModelTextFileObserver(String name) throws IOException {
        this(null, name);
    }

    public SpatialModelTextFileObserver(String directory, String name) throws IOException {
        super(directory, name);
    }

    public void update(Observable arg0, Object arg1) {

        mySpatialModel2D = (SpatialModel) arg0;
        int state = mySpatialModel2D.getObserverState();

        if (myFirstUpdateFlag) {
            myFirstUpdateFlag = false;
            recordFirstUpdate();
        }

        if (state == SpatialModel.UPDATED_POSITION) {
            recordPositionUpdate();
        } else if (state == SpatialModel.ADDED_ELEMENT) {
            recordSpatialModelElementAdded();
        } else if (state == SpatialModel.REMOVED_ELEMENT) {
            recordSpatialModelElementRemoved();
        } else {
            throw new IllegalStateException("Not a valid state in SpatialModelTextFileObserver state = " + state);
        }

    }

    protected void recordFirstUpdate() {
        myPrintWriter.println("Starting observations on : " + mySpatialModel2D.getName());
    }

    protected void recordSpatialModelElementRemoved() {
        SpatialElementIfc se = mySpatialModel2D.getUpdatingSpatialElement();
        ModelElement me = se.getModelElement();
        double t = me.getTime();
        myPrintWriter.println("t> " + t + " The following spatial element was removed from the spatial model: " + se.getName() + " ModelElement: " + me.getName());
    }

    protected void recordSpatialModelElementAdded() {
        SpatialElementIfc se = mySpatialModel2D.getUpdatingSpatialElement();
        ModelElement me = se.getModelElement();
        double t = me.getTime();
        myPrintWriter.println("t> " + t + " The following spatial element was added to the spatial model: " + se.getName() + " ModelElement: " + me.getName());

    }

    protected void recordPositionUpdate() {
        SpatialElementIfc se = mySpatialModel2D.getUpdatingSpatialElement();
        ModelElement me = se.getModelElement();
        double t = me.getTime();
        String s = "t> " + t + " Position Update( " + se.getName() + ", " + me.getName() + " ) moved from (";
        s = s + se.getPreviousPosition().getX1() + ", " + se.getPreviousPosition().getX2() + ") to (" + se.getPosition().getX1() + ", " + se.getPosition().getX2() + ")";
        myPrintWriter.println(s);

    }

}
