/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling.elements.station;

import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author rossetti
 */
public class ReceiverListIterator implements ReceiverIteratorIfc {

    protected ListIterator<ReceiveQObjectIfc> myListIterator;

    public ReceiverListIterator(List<ReceiveQObjectIfc> list) {
        if (list == null) {
            throw new IllegalArgumentException("List list must be non-null!");
        }
        myListIterator = list.listIterator();
    }

    @Override
    public ReceiveQObjectIfc nextReceiver() {
        if (myListIterator.hasNext()) {
            return ((ReceiveQObjectIfc) myListIterator.next());
        } else {
            return (null);
        }
    }

    @Override
    public ReceiveQObjectIfc previousReceiver() {
        if (myListIterator.hasPrevious()) {
            return ((ReceiveQObjectIfc) myListIterator.previous());
        } else {
            return (null);
        }
    }

    @Override
    public boolean hasNextReceiver() {
        return myListIterator.hasNext();
    }

    @Override
    public boolean hasPreviousReceiver() {
        return myListIterator.hasPrevious();
    }

    @Override
    public int nextReceiverIndex() {
        return myListIterator.nextIndex();
    }

    @Override
    public int previousReceiverIndex() {
        return myListIterator.previousIndex();
    }
}
