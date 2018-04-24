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
package jsl.modeling.elements.spatial;

/** The Vector3D class implements a 3D vector with the double
 *  values x1, x2, x3.  Vectors can be thought of as either a (x1, x2, x3) point
 *  or at a vector from (0.0, 0.0, 0.0) to (x1, x2, x3)
 *
 */
public class Vector3D implements CoordinateIfc {

    private double x1;

    private double x2;

    private double x3;

    /** Create a new Vector3D at (0.0, 0.0, 0.0)
     */
    public Vector3D() {
        this(0.0, 0.0, 0.0);
    }

    /** Create a new Vector3D with the same
     *  values as the specfied vector, v
     *  @param v the vector to replicate
     */
    public Vector3D(CoordinateIfc v) {
        this(v.getX1(), v.getX2(), v.getX3());
    }

    /** Create a new Vector3D at (x, y, 0.0)
     * @param x1 the 1st coordinate
     * @param x2 the 2nd coordinate
     */
    public Vector3D(double x1, double x2) {
        this(x1, x2, 0.0);
    }

    /** Create a new Vector3D at (x, y, z)
     * @param x1 the 1st coordinate
     * @param x2 the 2nd coordinate
     * @param x3 the 3rd coordinate
     */
    public Vector3D(double x1, double x2, double x3) {
        this.x1 = x1;
        this.x2 = x2;
        this.x3 = x3;
    }

    /** Returns a new instance of a Coordinate at the same
     *  underlying coordinates as the given coordinate
     *
     * @param c
     * @return
     */
    public final static Vector3D newInstance(CoordinateIfc c) {
        return (new Vector3D(c.getX1(), c.getX2(), c.getX3()));
    }

    /** Checks if this vector is equal to the specified Object
     *  They are equal if the specified Object is a Vector3D
     *  and the two vectors x, y, z coordinates are equal
     */
    @Override
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Vector3D)) {
            return false;
        }
        Vector3D v = (Vector3D) obj;
        return ((v.x1 == x1) && (v.x2 == x2) && (v.x3 == x3));
    }

    public final int hashcode() {
        int result = 17;
        long xlong = java.lang.Double.doubleToLongBits(x1);
        int xint = (int) (xlong ^ (xlong >>> 32));

        result = 37 * result + xint;

        long ylong = java.lang.Double.doubleToLongBits(x2);
        int yint = (int) (ylong ^ (ylong >>> 32));

        result = 37 * result + yint;

        long zlong = java.lang.Double.doubleToLongBits(x3);
        int zint = (int) (zlong ^ (zlong >>> 32));

        result = 37 * result + zint;

        return (result);
    }

    /** Checks if this vector is equal to the specified coordinates
     * @param x1 the 1st coordinate
     * @param x2 the 2nd coordinate
     * @param x3 the 3rd coordinate
     */
    public final boolean equals(double x1, double x2, double x3) {
        return ((this.x1 == x1) && (this.x2 == x2) && (this.x3 == x3));
    }

    /** Sets the coordinates of the vector to the values given
     *
     * @param x1 the 1st coordinate
     * @param x2 the 2nd coordinate
     * @param x3 the 3rd coordinate
     */
    public final void setCoordinates(double x1, double x2, double x3) {
        this.x1 = x1;
        this.x2 = x2;
        this.x3 = x3;
    }

    /** Sets the coordinates of the vector to the values given, x3 is assumed 0.0
     *
     * @param x1 the 1st coordinate
     * @param x2 the 2nd coordinate
     */
    public final void setCoordinates(double x1, double x2) {
        this.x1 = x1;
        this.x2 = x2;
        this.x3 = 0.0;
    }

    /** Sets the coordinates of this vector to those
     *  given by the CoordinateIfc
     *
     * @param c
     */
    public final void setCoordinates(CoordinateIfc c) {
        setCoordinates(c.getX1(), c.getX2(), c.getX3());
    }

    /** Adds the given values from the vector
     *
     * @param x1 the 1st coordinate
     * @param x2 the 2nd coordinate
     * @param x3 the 3rd coordinate
     */
    public final void add(double x1, double x2, double x3) {
        this.x1 = this.x1 + x1;
        this.x2 = this.x2 + x2;
        this.x3 = this.x3 + x3;
    }

    /** Subtracts the given values from the vector
     *
     * @param x1 the 1st coordinate
     * @param x2 the 2nd coordinate
     * @param x3 the 3rd coordinate
     */
    public final void subtract(double x1, double x2, double x3) {
        add(-x1, -x2, -x3);
    }

    /** Adds the given vector to this vector
     *
     * @param v
     */
    public final void add(CoordinateIfc v) {
        add(v.getX1(), v.getX2(), v.getX3());
    }

    /** Subtracts the given vector from this vector
     *
     * @param v
     */
    public final void subtract(CoordinateIfc v) {
        add(-v.getX1(), -v.getX2(), -v.getX3());
    }

    /** Multiplies each element by s
     *
     * @param s
     */
    public final void multiply(double s) {
        x1 = x1 * s;
        x2 = x2 * s;
        x3 = x3 * s;
    }

    /** Divides each element by s
     *
     * @param s Must not be zero
     */
    public final void divide(double s) {
        x1 = x1 / s;
        x2 = x2 / s;
        x3 = x3 / s;
    }

    /** The length of the vector
     *
     * @return the length
     */
    public final double length() {
        return (Math.sqrt(x1 * x1 + x2 * x2 + x3 * x3));
    }

    /** Converts the vector to a unit vector
     *
     */
    public final void normalize() {
        divide(length());
    }

    /** Returns the (x,y,z) coordinates as a string
     * "(x1,x2,x3)=(" + x1 + "," + x2 + "," + x3 +")"
     *
     */
    @Override
    public String toString() {
        String s = "(x1,x2,x3)=(" + x1 + "," + x2 + "," + x3 + ")";
        return (s);
    }

    /**
     * @return Returns the x1.
     */
    @Override
    public final double getX1() {
        return x1;
    }

    /**
     * @return Returns the x2.
     */
    @Override
    public final double getX2() {
        return x2;
    }

    /**
     * @return Returns the x3.
     */
    @Override
    public final double getX3() {
        return x3;
    }
}
