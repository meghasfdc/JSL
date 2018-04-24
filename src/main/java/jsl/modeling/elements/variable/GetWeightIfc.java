package jsl.modeling.elements.variable;

public interface GetWeightIfc {

	/** Gets the weight associated with the last value observed.
	 * @return The weight for the value.
	 */
	public double getWeight();

}