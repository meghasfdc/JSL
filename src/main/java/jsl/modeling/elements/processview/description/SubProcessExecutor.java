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
package jsl.modeling.elements.processview.description;

import jsl.modeling.elements.resource.Entity;

/**
 */
public class SubProcessExecutor extends ProcessExecutor {

    private ProcessExecutor myMainExecutor;

    /**
     * @param mainExecutor the executor
     * @param processDescription the description
     * @param entity the associated entity
     */
    protected SubProcessExecutor(ProcessExecutor mainExecutor, ProcessDescription processDescription, Entity entity) {
        super(processDescription, entity);
        setMainExecutor(mainExecutor);
        initialize();
    }

    @Override
    public void start(int commandIndex) {
        myMainExecutor.suspend();
        super.start(commandIndex);
    }

    public void terminate() {
        super.terminate();
        myMainExecutor.resume();
    }

    private void setMainExecutor(ProcessExecutor mainExecutor) {
        if (mainExecutor == null) {
            throw new IllegalArgumentException("ProcessExecutor must be non-null!");
        }
        myMainExecutor = mainExecutor;
    }
}