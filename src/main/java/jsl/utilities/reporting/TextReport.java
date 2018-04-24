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
package jsl.utilities.reporting;

import java.io.*;
import java.util.Date;
import java.util.Locale;

/** Wraps a PrintWriter to make a txt file for making a report. Allows the file
 *  name and date to be added to the file via addFileNameAndDate() method
 * 
 * @author rossetti
 */
public class TextReport {

    private File myFile;

    protected PrintWriter myPrintWriter;

    public TextReport(String name) {
        this(null, name, "txt");
    }

    public TextReport(String name, String extension){
        this(null, name, extension);
    }

    public TextReport(File directory, String name, String extension) {
        myFile = JSL.makeFile(directory, name, extension);
        myPrintWriter = JSL.makePrintWriter(myFile);
    }         

    public File getFile() {
        return myFile;
    }

    public void addFileNameAndDate() {
        myPrintWriter.println(myFile);
        myPrintWriter.println(new Date());
        myPrintWriter.println();
    }

    public void write(String s) {
        myPrintWriter.write(s);
    }

    public void write(String s, int off, int len) {
        myPrintWriter.write(s, off, len);
    }

    public void write(char[] buf) {
        myPrintWriter.write(buf);
    }

    public void write(char[] buf, int off, int len) {
        myPrintWriter.write(buf, off, len);
    }

    public void write(int c) {
        myPrintWriter.write(c);
    }

    public void println(Object x) {
        myPrintWriter.println(x);
    }

    public void println(String x) {
        myPrintWriter.println(x);
    }

    public void println(char[] x) {
        myPrintWriter.println(x);
    }

    public void println(double x) {
        myPrintWriter.println(x);
    }

    public void println(float x) {
        myPrintWriter.println(x);
    }

    public void println(long x) {
        myPrintWriter.println(x);
    }

    public void println(int x) {
        myPrintWriter.println(x);
    }

    public void println(char x) {
        myPrintWriter.println(x);
    }

    public void println(boolean x) {
        myPrintWriter.println(x);
    }

    public void println() {
        myPrintWriter.println();
    }

    public PrintWriter printf(Locale l, String format, Object... args) {
        return myPrintWriter.printf(l, format, args);
    }

    public PrintWriter printf(String format, Object... args) {
        return myPrintWriter.printf(format, args);
    }

    public void print(Object obj) {
        myPrintWriter.print(obj);
    }

    public void print(String s) {
        myPrintWriter.print(s);
    }

    public void print(char[] s) {
        myPrintWriter.print(s);
    }

    public void print(double d) {
        myPrintWriter.print(d);
    }

    public void print(float f) {
        myPrintWriter.print(f);
    }

    public void print(int l) {
        myPrintWriter.print(l);
    }

    public void print(long i) {
        myPrintWriter.print(i);
    }

    public void print(char c) {
        myPrintWriter.print(c);
    }

    public void print(boolean b) {
        myPrintWriter.print(b);
    }

    public PrintWriter format(Locale l, String format, Object... args) {
        return myPrintWriter.format(l, format, args);
    }

    public PrintWriter format(String format, Object... args) {
        return myPrintWriter.format(format, args);
    }

    public void flush() {
        myPrintWriter.flush();
    }

    public void close() {
        myPrintWriter.close();
    }

    public boolean checkError() {
        return myPrintWriter.checkError();
    }

    public PrintWriter append(char c) {
        return myPrintWriter.append(c);
    }

    public PrintWriter append(CharSequence csq, int start, int end) {
        return myPrintWriter.append(csq, start, end);
    }

    public PrintWriter append(CharSequence csq) {
        return myPrintWriter.append(csq);
    }
}
