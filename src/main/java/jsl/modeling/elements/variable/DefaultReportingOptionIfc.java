package jsl.modeling.elements.variable;

public interface DefaultReportingOptionIfc {

    /** Sets the default reporting option. True means the
     *  response will appear on default reports
     *
     * @param flag
     */
    public void setDefaultReportingOption(boolean flag);

    /** Returns the default reporting option.  True means that
     *  the response should appear on the default reports
     *
     * @return
     */
    public boolean getDefaultReportingOption();
}
