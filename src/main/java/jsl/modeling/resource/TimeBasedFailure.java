/*
 * Copyright (c) 2018. Manuel D. Rossetti, rossetti@uark.edu
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
import jsl.utilities.reporting.JSL;

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
public class TimeBasedFailure extends FailureProcess {

    protected EventGenerator myFailureGenerator;

    protected final ResourceUnit myResourceUnit;

    public TimeBasedFailure(ResourceUnit parent, RandomIfc repairTime,
            RandomIfc timeToFailure) {
        this(parent, repairTime, timeToFailure, false, null);
    }

    public TimeBasedFailure(ResourceUnit parent, RandomIfc failureDuration,
            RandomIfc timeToFailure, boolean delayOption) {
        this(parent, failureDuration, timeToFailure, delayOption, null);
    }

    public TimeBasedFailure(ResourceUnit parent, RandomIfc failureDuration,
            RandomIfc timeToFailure, boolean delayOption, String name) {
        super(parent, failureDuration, delayOption);
        myResourceUnit = parent;
        myFailureGenerator = new EventGenerator(this, new FailureAction(),
                timeToFailure, timeToFailure);
        myFailureGenerator.setStartOnInitializeFlag(false);
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
    protected void failureNoticeActivated(FailureNotice fn) {
        suspend();
    }

    @Override
    protected void failureNoticeDelayed(FailureNotice fn) {
        suspend();
    }

    @Override
    protected void failureNoticeIgnored(FailureNotice fn) {
        StringBuilder sb = new StringBuilder();
        sb.append(getTime());
        sb.append(" > ");
        sb.append(getName());
        sb.append(" ignored FailureNotice ");
        sb.append(fn.toString());
        sb.append(System.lineSeparator());
        JSL.LOGGER.warn(sb.toString());
    }

    @Override
    protected void failureNoticeCompleted(FailureNotice fn) {
        resume();
    }

    @Override
    protected void startProcess() {
        double time = myFailureGenerator.getTimeBetweenEvents().getValue();
        myFailureGenerator.turnOnGenerator(time);
    }

    @Override
    protected void suspendProcess() {
        myFailureGenerator.suspend();
    }

    @Override
    protected void stopProcess() {
        myFailureGenerator.turnOffGenerator();
    }

    @Override
    protected void resumeProcess() {
        myFailureGenerator.resume();
    }

    @Override
    protected void signalFailure() {
        myResourceUnit.receiveFailureNotice(createFailureNotice());
    }

    private class FailureAction implements EventGeneratorActionIfc {

        @Override
        public void generate(EventGenerator generator, JSLEvent event) {
            fail();
        }

    }
}
