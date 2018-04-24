/*
 * Created on Apr 18, 2007
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Locale;

/** A wrapper for a PrintWriter.  This class has all the functionality of
 *  PrintWriter but has a public field OUTPUT_ON that can be set to false
 *  to turn off any printing or set to true to turn printing on.
 * @author rossetti
 *
 */
public class LogPrintWriter extends PrintWriter {

    /**
     *  Controls whether or not any the PrintWriter functionality happens
     */
    public boolean OUTPUT_ON = true;

    /**
     * @param out the Writer
     */
    public LogPrintWriter(Writer out) {
        super(out);
    }

    /**
     * @param out the output stream
     */
    public LogPrintWriter(OutputStream out) {
        super(out);
    }

    /**
     * @param fileName the file name
     * @throws FileNotFoundException the exception
     */
    public LogPrintWriter(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    /**
     * @param file the file
     * @throws FileNotFoundException the exception
     */
    public LogPrintWriter(File file) throws FileNotFoundException {
        super(file);
    }

    /**
     * @param out the Writer
     * @param autoFlush true means auto flush
     */
    public LogPrintWriter(Writer out, boolean autoFlush) {
        super(out, autoFlush);
    }

    /**
     * @param out the Writer
     * @param autoFlush true means auto flush
     */
    public LogPrintWriter(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    /**
     * @param fileName the file name to use
     * @param csn name of the character set
     * @throws FileNotFoundException an exception
     * @throws UnsupportedEncodingException an exception
     */
    public LogPrintWriter(String fileName, String csn)
            throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    /**
     * @param file the file
     * @param csn name of the character set
     * @throws FileNotFoundException an exception
     * @throws UnsupportedEncodingException an exception
     */
    public LogPrintWriter(File file, String csn) throws FileNotFoundException,
            UnsupportedEncodingException {
        super(file, csn);
    }

    @Override
    public void println(String x) {
        if (OUTPUT_ON) {
            super.println(x);
        }
    }

    @Override
    public PrintWriter append(char c) {
        if (OUTPUT_ON) {
            return (super.append(c));
        } else {
            return (this);
        }
    }

    @Override
    public PrintWriter append(CharSequence csq, int start, int end) {
        if (OUTPUT_ON) {
            return (super.append(csq, start, end));
        } else {
            return (this);
        }
    }

    @Override
    public PrintWriter append(CharSequence csq) {
        if (OUTPUT_ON) {
            return (super.append(csq));
        } else {
            return (this);
        }
    }

    @Override
    public void print(boolean b) {
        if (OUTPUT_ON) {
            super.print(b);
        }
    }

    @Override
    public void print(char c) {
        if (OUTPUT_ON) {
            super.print(c);
        }
    }

    @Override
    public void print(char[] s) {
        if (OUTPUT_ON) {
            super.print(s);
        }
    }

    @Override
    public void print(double d) {
        if (OUTPUT_ON) {
            super.print(d);
        }
    }

    @Override
    public void print(float f) {
        if (OUTPUT_ON) {
            super.print(f);
        }
    }

    @Override
    public void print(int i) {
        if (OUTPUT_ON) {
            super.print(i);
        }
    }

    @Override
    public void print(long l) {
        if (OUTPUT_ON) {
            super.print(l);
        }
    }

    @Override
    public void print(Object obj) {
        if (OUTPUT_ON) {
            super.print(obj);
        }
    }

    @Override
    public void print(String s) {
        if (OUTPUT_ON) {
            super.print(s);
        }
    }

    @Override
    public PrintWriter printf(Locale l, String format, Object... args) {
        if (OUTPUT_ON) {
            return super.printf(l, format, args);
        } else {
            return (this);
        }
    }

    @Override
    public PrintWriter printf(String format, Object... args) {
        if (OUTPUT_ON) {
            return super.printf(format, args);
        } else {
            return this;
        }
    }

    @Override
    public void println() {
        if (OUTPUT_ON) {
            super.println();
        }
    }

    @Override
    public void println(boolean x) {
        if (OUTPUT_ON) {
            super.println(x);
        }
    }

    @Override
    public void println(char x) {
        if (OUTPUT_ON) {
            super.println(x);
        }
    }

    @Override
    public void println(char[] x) {
        if (OUTPUT_ON) {
            super.println(x);
        }
    }

    @Override
    public void println(double x) {
        if (OUTPUT_ON) {
            super.println(x);
        }
    }

    @Override
    public void println(float x) {
        if (OUTPUT_ON) {
            super.println(x);
        }
    }

    @Override
    public void println(int x) {
        if (OUTPUT_ON) {
            super.println(x);
        }
    }

    @Override
    public void println(long x) {
        if (OUTPUT_ON) {
            super.println(x);
        }
    }

    @Override
    public void println(Object x) {
        if (OUTPUT_ON) {
            super.println(x);
        }
    }

    @Override
    public void write(char[] buf, int off, int len) {
        if (OUTPUT_ON) {
            super.write(buf, off, len);
        }
    }

    @Override
    public void write(char[] buf) {
        if (OUTPUT_ON) {
            super.write(buf);
        }
    }

    @Override
    public void write(int c) {
        if (OUTPUT_ON) {
            super.write(c);
        }
    }

    @Override
    public void write(String s, int off, int len) {
        if (OUTPUT_ON) {
            super.write(s, off, len);
        }
    }

    @Override
    public void write(String s) {
        if (OUTPUT_ON) {
            super.write(s);
        }
    }
}
