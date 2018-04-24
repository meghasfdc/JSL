/*
 *  Copyright (C) 2017 rossetti
 * 
 *  Contact:
 * 	Manuel D. Rossetti, Ph.D., P.E.
 * 	Department of Industrial Engineering
 * 	University of Arkansas
 * 	4207 Bell Engineering Center
 * 	Fayetteville, AR 72701
 * 	Phone: (479) 575-6756
 * 	Email: rossetti@uark.edu
 * 	Web: www.uark.edu/~rossetti
 * 
 *  This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 *  of Java classes that permit the easy development and execution of discrete event
 *  simulation programs.
 * 
 *  The JSL is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  The JSL is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jsl.modeling.elements.spatial;

import java.util.List;

/** An interface to define a general pattern of selecting a cell 
 *  from a list of cells. The list of cells should not contain null as
 *  an element.
 *
 * @author rossetti
 */
public interface RectangularCell2DSelectorIfc {
   
    /** A method for selecting cells from a list of cells.
     * 
     *  If cells is null, an IllegalArgumentException is thrown
     *  If cells is empty an IllegalStateException is thrown
     *  If the returned cell is to be null, an IllegalStateException is thrown
     * 
     * @param cells the cells to select from
     * @return the selected cell
     */
    public RectangularCell2D selectCell(List<RectangularCell2D> cells);
 
    
}
