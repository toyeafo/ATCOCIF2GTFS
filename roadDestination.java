/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

/**
 *
 * @author AFROGENIUS
 */
public class roadDestination extends roadTimePoint {
    private int arrivalTime;
    private String arrivalString;

    public roadDestination() {
        super(roadElement.Type.JOURNEY_DESTINATION);
    }

    @Override
    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalString(String arrivalString) {
        this.arrivalString = arrivalString;
    }

    public String getArrivalString() {
        return arrivalString;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Override
    public int getDepartureTime() {
        return getArrivalTime();
    }

    @Override
    public void setDepartureTime(int departureTime) {
        // IGNORE
    }
}
