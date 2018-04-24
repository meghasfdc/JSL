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
import jsl.modeling.ModelElement;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.distributions.Uniform;

/**
 *
 * @author rossetti
 */
public class UniformCellSelector extends ModelElement implements RectangularCell2DSelectorIfc {

    protected RandomVariable myRV;

    public UniformCellSelector(ModelElement parent) {
        this(parent, null);
    }

    public UniformCellSelector(ModelElement parent, String name) {
        super(parent, name);
        myRV = new RandomVariable(this, new Uniform());
    }

    /** Randomly generates an integer between i and j
     * 
     * @param i the lower limit
     * @param j the upper limit
     * @return the random integer
     */
    public final int randInt(int i, int j) {
        if (i > j){
            throw new IllegalArgumentException("The lower limit must be <= the upper limit");
        }
        return (i + (int) (myRV.getValue() * (j - i + 1)));
    }

    @Override
    public RectangularCell2D selectCell(List<RectangularCell2D> cells) {
        if (cells == null){
            throw new IllegalArgumentException("The list of cells was null");
        }
        if (cells.isEmpty()){
            throw new IllegalStateException("The cell list was empty");
        }
        
        int randInt = randInt(0, cells.size()-1);
        RectangularCell2D cell = cells.get(randInt);
        if (cell == null){
            throw new IllegalStateException("The selected cell was null");
        }
        return cell;
    }

}
