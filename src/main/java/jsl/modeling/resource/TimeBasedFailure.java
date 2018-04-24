/*
 * Copyright (c) 2018. Manuel D. Rossetti, manuelrossetti@gmail.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
