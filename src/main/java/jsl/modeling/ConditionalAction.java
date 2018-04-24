/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsl.modeling;

/**
 *
 * @author rossetti
 */
abstract public class ConditionalAction implements Comparable<ConditionalAction>{

    private int myPriority;

    private int myId;

    abstract public boolean testCondition();

    abstract protected void action();

    final void setPriority(int priority){
        myPriority = priority;
    }

    public final int getPriority(){
        return myPriority;
    }

    final void setId(int id){
        myId = id;
    }

    public final int getId(){
        return myId;
    }

    /** Returns a negative integer, zero, or a positive integer
     * if this object is less than, equal to, or greater than the
     * specified object.
     *
     * Natural ordering: time, then priority, then order of creation
     *
     * Lower time, lower priority, lower order of creation goes first
     *
     * Throws ClassCastException if the specified object's type
     * prevents it from begin compared to this object.
     *
     * Throws RuntimeException if the id's of the objects are the same,
     * but the references are not when compared with equals.
     *
     * Note:  This class may have a natural ordering that is inconsistent
     * with equals.
     * @param action The action to compare 
     * @return Returns a negative integer, zero, or a positive integer
     * if this object is less than, equal to, or greater than the
     * specified object.
     */
    @Override
    public final int compareTo(ConditionalAction action) {

        // check priorities

        if (myPriority < action.getPriority())
            return (-1);

        if (myPriority > action.getPriority())
            return (1);

        // time and priorities are equal, compare ids

        if (myId < action.getId()) // lower id, implies created earlier
            return (-1);

        if (myId > action.getId())
            return (1);

        // if the id's are equal then the object references must be equal
        // if this is not the case there is a problem

        if (this.equals(action))
            return(0);
        else
            throw new RuntimeException("Id's were equal, but references were not, in ConditionalAction compareTo");

    }

}
