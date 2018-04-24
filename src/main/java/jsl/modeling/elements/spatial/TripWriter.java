/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling.elements.spatial;

import java.io.PrintWriter;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.spatial.AbstractMover;
import jsl.modeling.elements.spatial.CoordinateIfc;
import jsl.observers.Mover2DObserver;
import jsl.utilities.reporting.JSL;

/**
 *
 * @author rossetti
 */
public class TripWriter extends Mover2DObserver {

    protected PrintWriter out;

    public TripWriter() {
        this("AbstractMover");
    }

    public TripWriter(String name) {
        super(name);
        out = JSL.makePrintWriter(name, "csv");
    }

    protected void writePosition(AbstractMover mover) {
        int r = mover.getCurrentReplicationNumber();
        CoordinateIfc position = mover.getPosition();
        String s = mover.getName();
        double x = position.getX1();
        double y = position.getX2();
        double t = mover.getTime();
        out.printf("%s, %d,%f, %f,%f %n", s, r, t, x, y);
    }

    @Override
    protected void initialize(ModelElement m, Object arg) {
        writePosition((AbstractMover)m);
    }

    @Override
    protected void tripEnded(ModelElement m, Object arg) {
        writePosition((AbstractMover)m);
    }

    @Override
    protected void moveEnded(ModelElement m, Object arg) {
 //       writePosition((AbstractMover)m);
    }

    @Override
    protected void replicationEnded(ModelElement m, Object arg) {
        writePosition((AbstractMover)m);
    }
    
}
