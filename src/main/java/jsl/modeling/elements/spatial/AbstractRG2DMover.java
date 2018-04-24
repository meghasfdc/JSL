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

/** A base class for implementing movement within a 2D rectangular grid
 *
 * @author rossetti
 */
public abstract class AbstractRG2DMover extends AbstractMover {

    protected RectangularGridSpatialModel2D myGrid;

    protected RectangularGridModel myGridModel;

    protected RectangularCell2DSelectorIfc myCellSelector;

    protected EuclideanStepBasedMovementController myMoveController;

    protected boolean myStartRandomlyFlag;

    public AbstractRG2DMover(RectangularGridModel parent) {
        this(parent, new Vector3D(), null);
    }

    public AbstractRG2DMover(RectangularGridModel parent, String name) {
        this(parent, new Vector3D(), name);
    }

    public AbstractRG2DMover(RectangularGridModel parent, CoordinateIfc coordinate, String name) {
        super(parent, name, parent.getGrid(), coordinate);
        myStartRandomlyFlag = false;
        myGrid = parent.getGrid();
        myGridModel = parent;
        myCellSelector = new UniformCellSelector(this);
        myMoveController = new EuclideanStepBasedMovementController(this);
        setMovementController(myMoveController);
    }

    /**
     *
     * @return true if initialized with random trip
     */
    public final boolean isStartRandomlyOption() {
        return myStartRandomlyFlag;
    }

    /**
     *
     * @param startRandomlyFlag true means initialize with random trip
     */
    public final void setStartRandomlyOption(boolean startRandomlyFlag) {
        this.myStartRandomlyFlag = startRandomlyFlag;
    }

    /**
     * Move to the center of the cell
     *
     * @param cell the cell
     */
    public void moveToCenterOfCell(RectangularCell2D cell) {
        moveTo(cell.getCenterCoordinate());
    }

    /**
     * Moves to a random location within the specified cell
     *
     * @param cell the cell to move in
     */
    public void moveToInsideCellRandomly(RectangularCell2D cell) {
        moveTo(getGridModel().getRandomCoordinateInCell(cell));
    }

    /**
     * Randomly set the initial position using the getRandomCoordinate()
     */
    public void setInitialPositionRandomly() {
        setInitialPosition(getRandomCoordinate());
    }

    /**
     * Randomly generates a coordinate using the RectangularGridModel
     *
     * @return the coordinate
     */
    public CoordinateIfc getRandomCoordinate() {
        return getGridModel().getRandomCoordinate();
    }

    /**
     *
     * @return the RectangularGridSpatialModel2D
     */
    public final RectangularGridModel getGridModel() {
        return myGridModel;
    }

    /**
     *
     * @return the current cell of the mover
     */
    public final RectangularCell2D getCurrentCell() {
        return getRectagularGrid().getCell(getPosition());
    }

    /**
     *
     * @return a thing that knows how to select a cell
     */
    public RectangularCell2DSelectorIfc getCellSelector() {
        return myCellSelector;
    }

    /**
     *
     * @param cellSelector the selector
     */
    public void setCellSelector(RectangularCell2DSelectorIfc cellSelector) {
        if (cellSelector == null) {
            throw new IllegalArgumentException("The supplied cell selector was null");
        }
        myCellSelector = cellSelector;
    }

    /**
     * Does not include the core cell in the neighborhood
     *
     * @return a randomly selected cell using the cell selector
     */
    public RectangularCell2D selectNeighborRandomly() {
        return myCellSelector.selectCell(getNeighborhoodList());
    }

    /**
     *
     * @return the RectanularGridSpatialModel2D that contains the mover
     */
    public final RectangularGridSpatialModel2D getRectagularGrid() {
        return myGrid;
    }

    /**
     * A list of the Moore neighborhood for this mover. The core cell is not
     * included in the list
     *
     * @return
     */
    protected List<RectangularCell2D> getNeighborhoodList() {
        return getRectagularGrid().getMooreNeighborhoodAsList(this);
    }


}
