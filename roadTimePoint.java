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
public abstract class roadTimePoint extends roadElement implements roadChild {

    private roadHeader header;

    private String locationId;

    public roadTimePoint(roadElement.Type type) {
        super(type);
    }

    public roadHeader getHeader() {
        return header;
    }

    public void setHeader(roadHeader header) {
        this.header = header;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public abstract int getArrivalTime();

    public abstract int getDepartureTime();

    public abstract void setArrivalTime(int arrivalTime);

    public abstract void setDepartureTime(int departureTime);

    @Override
    public String toString() {
        return getArrivalTime() + " " + getDepartureTime();
    }
}
