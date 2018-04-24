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
package jsl.observers.animation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class AnimationTextFileGenerator implements AnimationMessageHandlerIfc {

    private File myFile;
    protected PrintWriter myPrintWriter;
    protected StringBuilder myAnimationMessage;

	public AnimationTextFileGenerator(String name) throws IOException {
		this(null, name);
	}

	public AnimationTextFileGenerator(String directory, String name) throws IOException {
        if (directory == null)
        		directory = "jslOutput";
        
        File d = new File(directory);
        d.mkdir();
        
        myFile = new File(d, makeFileName(name));
        
        myPrintWriter = new PrintWriter(new FileWriter(myFile),true);

		myAnimationMessage = new StringBuilder();

	}

	public boolean isStarted() {
		return (myAnimationMessage.length() > 0);
	}
	
	public void beginMessage() {
		if (myAnimationMessage.length() > 0)
			myAnimationMessage.delete(0, myAnimationMessage.length()+1);
	}

	public void append(double value) {
		myAnimationMessage.append(value);
	}

	public void append(long value) {
		myAnimationMessage.append(value);		
	}

	public void append(String value) {
		myAnimationMessage.append(value);		
	}
	
	public void commitMessage() {
		if (myAnimationMessage.length() > 0)
			myPrintWriter.println(myAnimationMessage);	
	}

    private String makeFileName(String name){
        //construct filename to ensure .txt
        String s;
        int dot = name.lastIndexOf(".");
        
        if ( dot == -1 ) // no period found
            s = name + ".txt";
        else // period found
            s = name.substring(dot) + "txt";
        
        return(s);
    }

}
