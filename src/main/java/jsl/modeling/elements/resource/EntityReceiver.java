/*
 *
 * Copyright (c) Manuel D. Rossetti (rossetti@uark.edu)
 *
 * Contact:
 *	Manuel D. Rossetti, Ph.D., P.E.
 *	Department of Industrial Engineering
 *	University of Arkansas
 *	4207 Bell Engineering Center
 *	Fayetteville, AR 72701
 *	Phone: (479) 575-6756
 *	Email: rossetti@uark.edu
 *	Web: www.uark.edu/~rossetti
 *
 * This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 * of Java classes that permit the easy development and execution of discrete event
 * simulation programs.
 *
 * The JSL is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The JSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the JSL (see file COPYING in the distribution);
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA, or see www.fsf.org
 *
 */
package jsl.modeling.elements.resource;

import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.elements.resource.EntityType.SendOption;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;

/** Represents a base class for developing model elements that can
 *  receive entities.
 *
 * @author rossetti
 */
public abstract class EntityReceiver extends SchedulingElement
        implements GetEntityReceiverIfc {

    private Receiver myReceiver = new Receiver();

    /** The option used by the receiver for
     *  sending entities.
     * 
     */
    protected SendOption mySendOption = SendOption.NONE;

    /** Used to collect time spent at receiver
     *
     */
    protected ResponseVariable myTimeInReceiver;

    /** Used to collect number of entities at the receiver
     *
     */
    protected TimeWeighted myNumInReceiver;

    /** This can be used to directly specify the next receiver
     *
     */
    protected EntityReceiverAbstract myDirectEntityReceiver;

    /** If the EntityReceiver is part of a CompositeReceiver
     *  this attribute contains the reference to the composite
     *
     */
    protected EntityReceiver myComposite;
    
    /** Used if the sending option is
     * 
     */
    protected EntitySenderIfc mySender;

    public EntityReceiver(ModelElement parent) {
        this(parent, null);
    }

    public EntityReceiver(ModelElement parent, String name) {
        super(parent, name);
    }

    /** Causes the receiver to collect the time spent
     *  at the receiver.  This must be called
     *  prior to any replications
     *
     */
    public final void turnOnTimeInReceiverCollection() {
        if (myTimeInReceiver == null) {
            myTimeInReceiver = new ResponseVariable(this, getName() + " Receiver Time");
        }
    }

    /** Causes the receiver to collect the number of entity's
     *  at the receiver.  This must be called
     *  prior to any replications
     *
     */
    public final void turnOnNumberInReceiverCollection() {
        if (myNumInReceiver == null) {
            myNumInReceiver = new TimeWeighted(this, getName() + " Receiver WIP");
        }
    }

    protected void setComposite(EntityReceiver composite){
        myComposite = composite;
    }

    protected EntityReceiver getComposite(){
        return myComposite;
    }

    public boolean isPartOfComposite(){
        return myComposite != null;
    }

    /** Represents logic to correctly receive the entity and
     *  process it accordingly
     *
     * @param entity
     */
    abstract protected void receive(Entity entity);

    private class Receiver extends EntityReceiverAbstract {

        @Override
        protected void receive(Entity entity) {
            if (entity == null) {
                throw new IllegalArgumentException("The supplied entity was null");
            }
            if (myNumInReceiver != null) {
                myNumInReceiver.increment();
            }
            if (isPartOfComposite()){
                entity.setCurrentReceiver(myComposite);
            } else {
                entity.setCurrentReceiver(this);
                entity.setTimeEnteredReceiver(getTime());
            }
            EntityReceiver.this.receive(entity);
        }
    }

    /** Returns a reference to the underlying EntityReceiverAbstract
     *
     * @return
     */
    @Override
    public final EntityReceiverAbstract getEntityReceiver() {
        return myReceiver;
    }

    /** Can be used by sub-classes to send the entity
     *  to its next receiver according to one of the specified
     *  options. 
     *
     * @param e
     */
    protected void sendEntity(Entity e) {
        if (myNumInReceiver != null) {
            myNumInReceiver.decrement();
        }
        if (myTimeInReceiver != null){
            if (!isPartOfComposite()){
                myTimeInReceiver.setValue(getTime() - e.getTimeEnteredReceiver());
            }
        }
        
        // tell the entity to goto next receiver
        if (mySendOption == SendOption.DIRECT) {
            e.sendViaReceiver(myDirectEntityReceiver);
        } else if (mySendOption == SendOption.SEQ) {
            e.sendViaSequence();
        } else if (mySendOption == SendOption.BY_TYPE) {
            e.sendViaEntityType();
        } else if (mySendOption == SendOption.BY_SENDER) {
            mySender.sendEntity(e);
        } else if (mySendOption == SendOption.NONE){
            throw new NoEntityReceiverException("No sending option was specified");
        }
    }

    /** Supply a sender to be used to send the entity
     *  If null is supplied the option is set to SendOption.NONE
     * 
     * @param sender 
     */
    public final void setEntitySender(EntitySenderIfc sender){
        if (sender == null){
            mySendOption = SendOption.NONE;
        } else {
            mySendOption = SendOption.BY_SENDER; 
        }
        mySender = sender;
    }
    
    /** Sets the sending option
     *  SendOption {DIRECT, SEQ, BY_TYPE}
     *  DIRECT, client must use setDirectEntityReceiver() to set receiver
     *  SEQ, entity uses predefined sequence in its EntityType
     *  BY_TYPE, entity uses its EntityType to determine next receiver
     *
     * @param option
     */
    public final void setSendingOption(SendOption option) {
        if (option == SendOption.BY_SENDER){
            throw new IllegalArgumentException("Use setEntitySender() for By_SENDER optioin");
        }
        mySendOption = option;
    }

    /** An object that will directly receive the entity
     *
     * @return
     */
    public final EntityReceiverAbstract getDirectEntityReceiver() {
        return myDirectEntityReceiver;
    }

    /** Can be used to supply a direct receiver. If used
     *  the sending option is automatically changed to direct
     *
     * @param receiver
     */
    public final void setDirectEntityReceiver(EntityReceiverAbstract receiver) {
        myDirectEntityReceiver = receiver;
        setSendingOption(SendOption.DIRECT);
    }

    /** See setDirectEntityReceiver()
     *
     * @param g
     */
    public final void setDirectEntityReceiver(GetEntityReceiverIfc g) {
        setDirectEntityReceiver(g.getEntityReceiver());
    }
}
