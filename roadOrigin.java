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
public class roadOrigin extends roadTimePoint {

    private int departureTime;
    private String departureString;

    public roadOrigin() {
        super(roadElement.Type.JOURNEY_ORIGIN);
    }

    @Override
    public int getDepartureTime() {
        return departureTime;
    }

    public String getDepartureString() {
        return departureString;
    }

    public void setDepartureString(String departureString) {
        this.departureString = departureString;
    }

    @Override
    public void setDepartureTime(int departureTime) {
        this.departureTime = departureTime;
    }

    @Override
    public int getArrivalTime() {
        return getDepartureTime();
    }

    @Override
    public void setArrivalTime(int arrivalTime) {
        // IGNORE
    }
}
