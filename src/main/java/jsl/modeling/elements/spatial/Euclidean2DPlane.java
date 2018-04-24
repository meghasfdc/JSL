/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling.elements.spatial;

import jsl.utilities.math.JSLMath;

/**
 *
 * @author rossetti
 */
public class Euclidean2DPlane extends SpatialModel {

    public Euclidean2DPlane() {
        this(null);
    }

    public Euclidean2DPlane(String name) {
        super(name);
    }

    @Override
    public CoordinateIfc getDefaultCoordinate() {
        return (new Vector3D());
    }

    @Override
    public CoordinateIfc getCoordinate(double x1, double x2, double x3) {
        return new Vector3D(x1, x2, x3);
    }

    @Override
    public boolean isValid(CoordinateIfc coordinate) {
        return true;
    }

    @Override
    public double distance(CoordinateIfc fromCoordinate, CoordinateIfc toCoordinate) {
        double x1 = fromCoordinate.getX1();
        double y1 = fromCoordinate.getX2();
        double x2 = toCoordinate.getX1();
        double y2 = toCoordinate.getX2();
        double dx = x1 - x2;
        double dy = y1 - y2;
        double d = Math.sqrt(dx * dx + dy * dy);
        return d;
    }

    @Override
    public boolean comparePositions(CoordinateIfc coordinate1, CoordinateIfc coordinate2) {
        double x1 = coordinate1.getX1();
        double y1 = coordinate1.getX2();
        double x2 = coordinate2.getX1();
        double y2 = coordinate2.getX2();
        boolean b1 = JSLMath.equal(x1, x2, getDefaultPositionPrecision());
        boolean b2 = JSLMath.equal(y1, y2, getDefaultPositionPrecision());
        return (b1 && b2);
    }

}
