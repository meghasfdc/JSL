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
package jsl.modeling;

/**
 *
 * @author rossetti
 */
public interface ExperimentGetIfc {

        /**
     * Gets the name.
     *
     * @return The name of object.
     */
    String getExperimentName();
    /**
     * Returns the id for this object
     *
     * @return the id
     */
    long getExperimentId();
    
    /**
     * Gets the reset next substream option. The reset next sub stream option
     * This option indicates whether or not the random variables used during the
     * replication within the experiment will be reset to their next substream
     * after running each replication. The default is TRUE. This ensures that
     * the random variables will jump to the next substream within their current
     * stream at the end of a replication. This will cause the random variables
     * in each subsequent replication to start in the same substream in the
     * underlying random number streams if the replication is repeatedly used
     * and the ResetStartStreamOption is set to false (which is the default).
     * Otherwise, this option really has no effect if there is only 1
     * replication in an experiment. and then jump to the next substream (if
     * this option is on). Having ResetNextSubStreamOption true assists in
     * synchronizing the random number draws from one replication to another
     * aiding in the implementation of common random numbers. Each replication
     * within the same experiment is still independent.
     *
     * @return true means the option is on
     */
    boolean getAdvanceNextSubStreamOption();

    /**
     * Returns the number of times that the streams should be advanced prior to
     * running the experiment
     *
     * @return number of streams to advance
     */
    int getNumberOfStreamAdvancesPriorToRunning();

    /**
     * Returns whether or not the antithetic option is turned on. True means
     * that it has been turned on.
     *
     * @return true if option is on
     */
    boolean getAntitheticOption();

    /**
     * Returns the current number of replications completed
     *
     * @return the number as a double
     */
    int getCurrentReplicationNumber();

    /**
     * Indicates whether or not System.gc() should be called after each
     * replication
     *
     * @return true if option is on 
     */
    boolean getGarbageCollectAfterReplicationFlag();

    /**
     * Returns the length of the replication as a double
     *
     * @return the length of the replication
     */
    double getLengthOfReplication();

    /**
     * Gets the length of the warm up for each replication with this experiment
     *
     * @return the the length of the warm up
     */
    double getLengthOfWarmUp();

    /**
     * Returns maximum (real) clock time allocated for the iterative process
     *
     * @return the number as long representing milliseconds
     */
    long getMaximumAllowedExecutionTimePerReplication();

    /**
     * Returns the number of replications to run
     *
     * @return the number as a double
     */
    int getNumberOfReplications();

    /**
     * Returns the setting for whether or not each replication will be
     * reinitialized prior to running.
     *
     * @return true means that each replication will be initialized
     */
    boolean getReplicationInitializationOption();

    /**
     * Gets the reset start stream option. The reset start stream option This
     * option indicates whether or not the random variables used during the
     * experiment will be reset to their starting stream prior to running the
     * first replication. The default is FALSE. This ensures that the random
     * variable's streams WILL NOT be reset prior to running the experiment.
     * This will cause different experiments or the same experiment run multiple
     * times that use the same random variables (via the same model) to continue
     * within their current stream. Therefore the experiments will be
     * independent when invoked within the same program execution. To get common
     * random number (CRN), run the experiments in different program executions
     * OR set this option to true prior to running the experiment again within
     * the same program invocation.
     *
     * @return true means the option is on
     */
    boolean getResetStartStreamOption();

    /**
     * Checks if the current number of replications that have been executed is
     * less than the number of replications specified.
     *
     * @return true if more 
     */
    boolean hasMoreReplications();

}
