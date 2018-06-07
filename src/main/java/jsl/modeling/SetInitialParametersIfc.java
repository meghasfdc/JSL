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
package jsl.modeling;

import java.util.Collection;

/**
 *  Implementors of this interface can use the supplied arguments 
 *  during initialization prior to running an experiment/replication.
 *  It is up to implementors to describe how the parameters/Collection
 *  will be used.  The implementor should guarantee that the model element state
 *  is valid prior to the running of a replication if this method is called
 *
 */
public interface SetInitialParametersIfc {

	@SuppressWarnings("unchecked")
	void setInitialParameters(double[] parameters, Collection object);
	
}
