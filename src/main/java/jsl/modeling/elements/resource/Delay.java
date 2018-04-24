/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling.elements.resource;

import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.RandomIfc;

/**
 *
 * @author rossetti
 */
public class Delay extends EntityReceiver {

    /** NONE = no duration specified, will result in an exception
     *  DIRECT = uses the activity time specified directly for the activity
     *  BY_TYPE = asks the EntityType to provide the time for this activity
     *  ENTITY = uses the entity's getDurationTime() method
     *
     */
    public enum DelayOption {

        NONE, DIRECT, BY_TYPE, ENTITY
    };

    private RandomVariable myDelayTimeRV;

    protected DelayOption myDelayOption = DelayOption.NONE;

    public Delay(ModelElement parent) {
        this(parent, null);
    }

    public Delay(ModelElement parent, String name) {
        super(parent, name);
    }

    public final void setDelayTime(RandomIfc distribution) {
        if (distribution == null) {
            throw new IllegalArgumentException("Attempted to set the activity time distribution to null!");
        }

        if (myDelayTimeRV == null) {
            myDelayTimeRV = new RandomVariable(this, distribution);
        } else {
            myDelayTimeRV.setInitialRandomSource(distribution);
        }

        myDelayOption = DelayOption.DIRECT;

    }

    @Override
    protected void receive(Entity entity) {
//        System.out.println(getTime() + " > " + entity + " started activity");
        scheduleDelayCompletion(entity);
    }

    protected void scheduleDelayCompletion(Entity entity) {

        JSLEvent e = scheduleEvent(getDelayTime(entity), entity);

        // entity.setTimedEvent(e);
        // entity.setStatus(Entity.Status.TIME_DELAYED);

    }

    protected double getDelayTime(Entity e) {
        double time = 0.0;
        if (myDelayOption == DelayOption.DIRECT) {
            if (myDelayTimeRV == null) {
                throw new NoActivityTimeSpecifiedException();
            } else {
                time = myDelayTimeRV.getValue();
            }
        } else if (myDelayOption == DelayOption.ENTITY) {
            time = e.getDurationTime();
        } else if (myDelayOption == DelayOption.BY_TYPE) {
            EntityType et = e.getType();
            time = et.getActivityTime(this);
        } else if (myDelayOption == DelayOption.NONE) {
            throw new NoActivityTimeSpecifiedException();
        }
        return time;
    }

    protected void endOfDelay(Entity entity) {
 //        System.out.println(getTime() + " > " + entity + " ended activity");
        sendEntity(entity);
    }

    @Override
    protected void handleEvent(JSLEvent e) {
        Entity entity = (Entity) e.getMessage();
        endOfDelay(entity);
    }

    public final void setDelayOption(DelayOption option) {
        myDelayOption = option;
    }
}
