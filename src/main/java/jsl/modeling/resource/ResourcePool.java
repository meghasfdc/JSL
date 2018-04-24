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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.RandomElementIfc;
import jsl.modeling.elements.Schedule;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.observers.ModelElementObserver;
import jsl.utilities.random.rvariable.JSLRandom;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 * A ResourcePool represents a list of ResourceUnits from which
 * a single unit can be selected.
 * <p>
 * ResourceUnits are selected according to a ResourceSelectionRule.
 * The assumption is that any of the resource
 * units within the pool may be used to fill the request.
 * <p>
 * If no selection rule is supplied the pool selects the first idle resource
 * by default.
 * <p>
 * Statistics on the number of units idle, busy, failed, and inactive within
 * the pool can be collected by specifying the pool statistics option, which
 * is true by default.
 * <p>
 * Sub-classes may override the methods unitbecameIdle(), unitbecameBusy(),
 * unitFailed(), unitBecameInactive() in order to react to state
 * changes on individual resource units.
 *
 * @author rossetti
 */
public class ResourcePool extends ModelElement implements RandomElementIfc {

    protected final List<ResourceUnit> myResources;
    private ResourceUnitSelectionRuleIfc mySelectionRule;
    protected ResourceUnitObserver myRUObserver;
    protected TimeWeighted myNumBusy;
    protected TimeWeighted myNumIdle;
    protected TimeWeighted myNumFailed;
    protected TimeWeighted myNumInactive;
    private final boolean myPoolStatOption;
    private RngIfc myRNG;
    protected boolean myResetStartStreamOption;
    protected boolean myResetNextSubStreamOption;

    /**
     * Statistics option is false by default
     *
     * @param parent the parent model element
     * @param units a list of ResourceUnits. Must not contain nulls and
     * must not contain duplicates
     */
    public ResourcePool(ModelElement parent, List<ResourceUnit> units) {
        this(parent, units, false, null);
    }

    /**
     * Statistics option is false by default
     *
     * @param parent the parent model element
     * @param units a list of ResourceUnits. Must not contain nulls and
     * must not contain duplicates
     * @param name the name of the pool
     */
    public ResourcePool(ModelElement parent, List<ResourceUnit> units, String name) {
        this(parent, units, false, name);
    }

    /**
     *
     * @param parent the parent model element
     * @param units a list of ResourceUnits. Must not contain nulls and
     * must not contain duplicates
     * @param poolStatOption true means collect the statistics
     */
    public ResourcePool(ModelElement parent, List<ResourceUnit> units,
            boolean poolStatOption) {
        this(parent, units, poolStatOption, null);
    }

    /**
     *
     * @param parent the parent model element
     * @param units a list of ResourceUnits. Must not contain nulls and
     * must not contain duplicates
     * @param poolStatOption true means collect more detailed statistics
     * @param name the name of the pool
     */
    public ResourcePool(ModelElement parent, List<ResourceUnit> units,
            boolean poolStatOption, String name) {
        super(parent, name);
        myRUObserver = new ResourceUnitObserver();
        myResources = new LinkedList<>();
        addAll(units);
        myPoolStatOption = poolStatOption;
        if (myPoolStatOption == true) {
            myNumBusy = new TimeWeighted(this, getName() + ":NumBusy");
            myNumIdle = new TimeWeighted(this, getName() + ":NumIdle");
            if (hasFailureElements()) {
                myNumFailed = new TimeWeighted(this, getName() + ":NumFailed");
            }
            if (isUsingSchedule()) {
                myNumInactive = new TimeWeighted(this, getName() + ":NumInactive");
            }
        }
    }

    /**
     *
     * @return number of units in the pool
     */
    public final int getNumUnits() {
        return myResources.size();
    }

    /**
     *
     * @return true if at least one unit is idle
     */
    public final boolean hasIdleUnits() {
        return getNumIdle() > 0;
    }

    /**
     *
     * @return true if at least one unit is busy
     */
    public final boolean hasBusyUnits() {
        return getNumBusy() > 0;
    }

    /**
     *
     * @return true if at least one unit is failed
     */
    public final boolean hasFailedUnits() {
        return getNumFailed() > 0;
    }

    /**
     *
     * @return true if all units are idle
     */
    public final boolean hasAllUnitsIdle() {
        return getNumUnits() == getNumIdle();
    }

    /**
     *
     * @return true if all units are busy
     */
    public final boolean hasAllUnitsBusy() {
        return getNumUnits() == getNumBusy();
    }

    /**
     *
     * @return true if all units are failed
     */
    public final boolean hasAllUnitsFailed() {
        return getNumUnits() == getNumFailed();
    }

    /**
     *
     * @return true if all units are inactive
     */
    public final boolean hasAllUnitsInactive() {
        return getNumUnits() == getNumInactive();
    }

    /**
     *
     * @return true if at least one unit is inactive
     */
    public final boolean hasInactiveUnits() {
        return getNumInactive() > 0;
    }

    /**
     *
     * @return the number of currently idle units
     */
    public final int getNumIdle() {
        int n = 0;
        for (ResourceUnit ru : myResources) {
            if (ru.isIdle()) {
                n++;
            }
        }
        return n;
    }

    /**
     *
     * @return the number of currently busy units
     */
    public final int getNumBusy() {
        int n = 0;
        for (ResourceUnit ru : myResources) {
            if (ru.isBusy()) {
                n++;
            }
        }
        return n;
    }

    /**
     *
     * @return the number of currently failed units
     */
    public final int getNumFailed() {
        int n = 0;
        for (ResourceUnit ru : myResources) {
            if (ru.isFailed()) {
                n++;
            }
        }
        return n;
    }

    /**
     *
     * @return the number of currently inactive units
     */
    public final int getNumInactive() {
        int n = 0;
        for (ResourceUnit ru : myResources) {
            if (ru.isInactive()) {
                n++;
            }
        }
        return n;
    }

    /**
     *
     * @return true if ALL units in the pool have true for their failure delay
     * option. That is, all units allow failures to be delayed.
     */
    public final boolean getFailureDelayOption() {
        boolean option = true;
        for (ResourceUnit ru : myResources) {
            if (ru.getFailureDelayOption() == false) {
                return false;
            }
        }
        return option;
    }

    /**
     *
     * @return true if ALL units in the pool have true for their inactive period
     * option. That is, all units allows their inactive periods to be delayed
     */
    public final boolean getInactivePeriodDelayOption() {
        boolean option = true;
        for (ResourceUnit ru : myResources) {
            if (ru.getInactivePeriodDelayOption() == false) {
                return false;
            }
        }
        return option;
    }

    /**
     *
     * @return true if at least one unit in the pool uses a schedule
     */
    public final boolean isUsingSchedule() {
        boolean option = false;

        for (ResourceUnit ru : myResources) {
            if (ru.isUsingSchedule()) {
                return true;
            }
        }
        return option;
    }

    /**
     *
     * @return true if at least one unit in the pool has failure elements
     */
    public final boolean hasFailureElements() {
        boolean option = false;

        for (ResourceUnit ru : myResources) {
            if (ru.hasFailureElements()) {
                return true;
            }
        }
        return option;
    }

    /**
     * Checks if the preemption rule of the request is compatible
     * with the failure delay option. If the request doesn't allow
     * preemption and failures cannot be delayed, this means that the
     * request will be rejected.
     *
     * @param request the request to check
     * @return true if compatible
     */
    public boolean isPreemptionRuleCompatible(Request request) {
        if (request.getPreemptionRule() == Request.PreemptionRule.NONE) {
            if (getFailureDelayOption() == false) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return true if pooled statistics will be collected
     */
    public final boolean isPooledStatsOptionOn() {
        return myPoolStatOption;
    }

    /**
     *
     * @param unit the unit to test
     * @return true if in the pool
     */
    public final boolean contains(ResourceUnit unit) {
        return myResources.contains(unit);
    }

    /**
     *
     * @return an unmodifiable list of the resource units
     */
    public final List<ResourceUnit> getUnits() {
        return Collections.unmodifiableList(myResources);
    }

    /**  Tells all ResourceUnits in the pool that are not already using a Schedule
     *   to use the supplied schedule
     *
     * @param schedule the schedule to use
     */
    public final void useSchedule(Schedule schedule){
        if (schedule == null){
            throw new IllegalArgumentException("The supplied Schedule was null");
        }
        for(ResourceUnit unit: getUnits()){
            if(!unit.isUsingSchedule()){
                unit.useSchedule(schedule);
            }
        }
    }

    /**
     *
     * @param units the list to add. Must not contain any nulls and must not
     * have any units that are already in the pool.
     */
    protected final void addAll(List<ResourceUnit> units) {
        if (units == null) {
            throw new IllegalArgumentException("The resource unit list was null!");
        }
        for (ResourceUnit ru : units) {
            add(ru);
        }
    }

    /**
     *
     * @param unit the unit to add. Must not be null. Must not already have been
     * added
     * @return true if added
     */
    protected final boolean add(ResourceUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("The resource unit was null!");
        }
        if (myResources.contains(unit)) {
            throw new IllegalArgumentException("The resource unit was already added!");
        }
        unit.addObserver(myRUObserver);
        return myResources.add(unit);
    }

    /**
     *
     * @return an Optional with the rule
     */
    public Optional<ResourceUnitSelectionRuleIfc> getSelectionRule() {
        return Optional.ofNullable(mySelectionRule);
    }

    /**
     *
     * @param rule the supplied rule, may be null
     */
    public void setSelectionRule(ResourceUnitSelectionRuleIfc rule) {
        mySelectionRule = rule;
    }

    /**
     * Selects a resource unit according to the selection rule. If no
     * selection rule is present, selects the first idle resource
     * in the list of resources.
     *
     * @return the selected resource unit or null
     */
    public ResourceUnit selectResourceUnit() {
        if (mySelectionRule != null) {
            return mySelectionRule.selectAvailableResource(myResources);
        }
        for (ResourceUnit ru : myResources) {
            if (ru.isIdle()) {
                return ru;
            }
        }
        return null;
    }

    /**
     * Randomly selects an idle resource unit if available
     *
     * @return the selected resource unit or null
     */
    public ResourceUnit randomlySelectResourceUnit() {
        List<ResourceUnit> list = findIdleResourceUnits();
        return JSLRandom.randomlySelect(list, getRandomness());
    }

    /**
     *
     * @return the underlying source of randomness for this model element
     */
    public RngIfc getRandomness() {
        if (myRNG == null) {
            myRNG = RNStreamFactory.getDefault().getStream();
        }
        return myRNG;
    }

    /**
     *
     * @return returns a list of idle resource units. It may be empty
     */
    public List<ResourceUnit> findIdleResourceUnits() {
        List<ResourceUnit> list = new ArrayList<>();
        for (ResourceUnit ru : myResources) {
            if (ru.isIdle()) {
                list.add(ru);
            }
        }
        return list;
    }

    /**
     *
     * @return the first idle resource found or null
     */
    public ResourceUnit findFirstIdle() {
        for (ResourceUnit ru : myResources) {
            if (ru.isIdle()) {
                return ru;
            }
        }
        return null;
    }

    @Override
    public void resetStartStream() {
        if (myRNG != null) {
            myRNG.resetStartStream();
        }
    }

    @Override
    public void resetStartSubstream() {
        if (myRNG != null) {
            myRNG.resetStartSubstream();
        }
    }

    @Override
    public void advanceToNextSubstream() {
        if (myRNG != null) {
            myRNG.advanceToNextSubstream();
        }
    }

    @Override
    public void setAntitheticOption(boolean flag) {
        if (myRNG != null) {
            myRNG.setAntitheticOption(flag);
        }
    }

    @Override
    public boolean getAntitheticOption() {
        if (myRNG != null) {
            return myRNG.getAntitheticOption();
        }
        return false;
    }

    /**
     * Gets the current Reset Start Stream Option
     *
     * @return
     */
    @Override
    public final boolean getResetStartStreamOption() {
        return myResetStartStreamOption;
    }

    /**
     * Sets the reset start stream option, true
     * means that it will be reset to the starting stream
     *
     * @param b
     */
    @Override
    public final void setResetStartStreamOption(boolean b) {
        myResetStartStreamOption = b;
    }

    /**
     * Gets the current reset next substream option
     * true means, that it is set to jump to the next substream after
     * each replication
     *
     * @return
     */
    @Override
    public final boolean getResetNextSubStreamOption() {
        return myResetNextSubStreamOption;
    }

    /**
     * Sets the current reset next substream option
     * true means, that it is set to jump to the next substream after
     * each replication
     *
     * @param b
     */
    @Override
    public final void setResetNextSubStreamOption(boolean b) {
        myResetNextSubStreamOption = b;
    }

    /**
     * before any replications reset the underlying random number generator to
     * the
     * starting stream
     * <p>
     */
    @Override
    protected void beforeExperiment() {
        super.beforeExperiment();
        if (getResetStartStreamOption()) {
            resetStartStream();
        }

    }

    /**
     * after each replication reset the underlying random number generator to
     * the next
     * substream
     */
    @Override
    protected void afterReplication() {
        super.afterReplication();
        if (getResetNextSubStreamOption()) {
            advanceToNextSubstream();
        }

    }

    protected class ResourceUnitObserver extends ModelElementObserver {

        @Override
        protected void update(ModelElement m, Object arg) {
            super.update(m, arg);
            ResourceUnit ru = (ResourceUnit) m;
            if (isPooledStatsOptionOn()) {
                collectStateStatistics(ru);
            }
            resourceUnitChanged(ru);
        }

    }

    /**
     * Partials out unit changes to unitBecameIdle(), unitBecameBusy(),
     * unitFailed(), unitBecameInactive()
     *
     * @param ru the unit that changed
     */
    protected void resourceUnitChanged(ResourceUnit ru) {
        if (ru.isIdle()) {
            unitBecameIdle(ru);
        } else if (ru.isBusy()) {
            unitBecameBusy(ru);
        } else if (ru.isFailed()) {
            unitFailed(ru);
        } else if (ru.isInactive()) {
            unitBecameInactive(ru);
        } else {
            // nothing
        }
    }

    /**
     * Collects pool statistics based on change of state of contained
     * resource units
     *
     * @param ru the resource unit that changed state
     */
    protected void collectStateStatistics(ResourceUnit ru) {
        if (myNumBusy != null) {
            myNumBusy.setValue(getNumBusy());
        }
        if (myNumIdle != null) {
            myNumIdle.setValue(getNumIdle());
        }
        if (myNumFailed != null) {
            myNumFailed.setValue(getNumFailed());
        }
        if (myNumInactive != null) {
            myNumInactive.setValue(getNumInactive());
        }
    }

    /**
     * Called when one of the units becomes idle
     *
     * @param ru the unit that became idle
     */
    protected void unitBecameIdle(ResourceUnit ru) {

    }

    /**
     * Called when one of the units becomes busy
     *
     * @param ru the unit that became busy
     */
    protected void unitBecameBusy(ResourceUnit ru) {

    }

    /**
     * Called when one of the units becomes failed
     *
     * @param ru the unit that became failed
     */
    protected void unitFailed(ResourceUnit ru) {

    }

    /**
     * Called when one of the units becomes inactive
     *
     * @param ru the unit that became inactive
     */
    protected void unitBecameInactive(ResourceUnit ru) {

    }

    public static Comparator<ResourcePool> getDescendingByNumIdleComparator() {
        return new DescendingByNumIdleComparator();
    }

    public static class DescendingByNumIdleComparator implements Comparator<ResourcePool> {

        @Override
        public int compare(ResourcePool o1, ResourcePool o2) {
            return o2.getNumIdle() - o1.getNumIdle();
        }

    }
}
