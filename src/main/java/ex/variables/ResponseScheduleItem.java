/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.variables;

/**
 *
 * @author rossetti
 */
public class ResponseScheduleItem {

    private double myStartTime;
    private ResponseInterval myResponseInterval;

    public ResponseScheduleItem(double time, ResponseInterval interval) {
        setStartTime(time);
        setResponseInterval(interval);
    }

    public double getStartTime() {
        return myStartTime;
    }

    protected final void setStartTime(double time) {
        if (time < 0) {
            throw new IllegalArgumentException("The start time must be >= 0");
        }
        myStartTime = time;
    }

    public ResponseInterval getResponseInterval() {
        return myResponseInterval;
    }

    protected final void setResponseInterval(ResponseInterval interval) {
        if (interval == null) {
            throw new IllegalArgumentException("The interval must not be null");
        }
        myResponseInterval = interval;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Scheduled Start Time: ");
        sb.append(getStartTime());
        sb.append(System.lineSeparator());
        sb.append(myResponseInterval.toString());
        return sb.toString();
    }
}
