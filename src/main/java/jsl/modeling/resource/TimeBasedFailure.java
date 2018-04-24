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
package jsl.modeling.resource;

import jsl.modeling.JSLEvent;
import jsl.modeling.elements.EventGenerator;
import jsl.utilities.random.RandomIfc;
import jsl.modeling.elements.EventGeneratorActionIfc;

/**
 * A TimeBasedFailure uses time to determine the next failure. By default the
 * failure process does not start automatically at time zero. The user can turn
 * on automatic starting of the failure process at time zero or use the
 * startFailureProcess() method. Once the failure process has been started it
 * cannot be started again. Once the failure process has been stopped, it cannot
 * be started again.
 *
 * @author rossetti
 */
public class TimeBasedFailure extends FailureElement {

    protected EventGenerator myFailureGenerator;

    public TimeBasedFailure(ResourceUnit parent, RandomIfc repairTime,
            RandomIfc timeToFailure) {
        this(parent, repairTime, timeToFailure, false, null);
    }

    public TimeBasedFailure(ResourceUnit parent, RandomIfc repairTime,
            RandomIfc timeToFailure, boolean delayOption) {
        this(parent, repairTime, timeToFailure, delayOption, null);
    }

    public TimeBasedFailure(ResourceUnit parent, RandomIfc repairTime,
            RandomIfc timeToFailure, boolean delayOption, String name) {
        super(parent, repairTime, delayOption);

        myFailureGenerator = new EventGenerator(this, new FailureAction(),
                timeToFailure, timeToFailure);
        myFailureGenerator.setStartOnInitializeFlag(false);
    }

    @Override
    protected void initialize() {
        super.initialize();
        if (getAutoFailuresFlag() == true) {
            startFailureProcess();
        }
    }

    /**
     *
     * @return true if the element is done failing
     */
    public final boolean isFailureProcessDone() {
        return myFailureGenerator.isGeneratorDone();
    }

    /**
     * Sets the time between failures source of randomness
     *
     * @param tbf the time between failure
     */
    public final void setTimeToFailureInitialRandomSource(RandomIfc tbf) {
        myFailureGenerator.setInitialTimeBetweenEvents(tbf);
    }

    @Override
    public final boolean isStarted() {
        return myFailureGenerator.isGeneratorStarted();
    }

    @Override
    public void startFailureProcess() {
        if (isDown()) {
            throw new IllegalStateException("Attempted to start the failure process when already failed.");
        }
        double time = myFailureGenerator.getTimeBetweenEvents().getValue();
        myFailureGenerator.turnOnGenerator(time);
    }

    @Override
    protected void repairAction() {
        setStateUp();
    }

    @Override
    protected void stopFailureProcess() {
        myFailureGenerator.turnOffGenerator();
    }

    @Override
    protected void resume() {
        myFailureGenerator.resume();
    }

    @Override
    protected void suspend() {
        myFailureGenerator.suspend();
    }

    private class FailureAction implements EventGeneratorActionIfc {

        @Override
        public void generate(EventGenerator generator, JSLEvent event) {
            sendFailureNoticeToResourceUnit();
        }

    }
}
