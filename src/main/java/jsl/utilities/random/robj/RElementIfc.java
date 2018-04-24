package jsl.utilities.random.robj;

import jsl.utilities.random.rng.RandomStreamIfc;


public interface RElementIfc<T> extends RandomStreamIfc {

	/** Returns an element randomly selected from the list
	 * 
	 * @return
	 */
	public T getRandomElement();

}