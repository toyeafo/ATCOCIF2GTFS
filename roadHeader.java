/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import java.util.ArrayList;
import java.util.List;
import org.onebusaway.gtfs.model.calendar.ServiceDate;

/**
 *
 * @author AFROGENIUS
 */
public class roadHeader extends roadElement {

    private String operatorId;

    private String journeyIdentifier;

    private ServiceDate startDate;

    private ServiceDate endDate;

    private int monday;

    private int tuesday;

    private int wednesday;

    private int thursday;

    private int friday;

    private int saturday;

    private int sunday;

    private String routeIdentifier;

    private String routeDirection;

    private String vehicleType;

    private List<roadDateRun> calendarModifications = new ArrayList<roadDateRun>();

    private List<roadTimePoint> timePoints = new ArrayList<roadTimePoint>();

    public roadHeader() {
        super(Type.JOURNEY_HEADER);
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getJourneyIdentifier() {
        return journeyIdentifier;
    }

    public void setJourneyIdentifier(String journeyIdentifier) {
        this.journeyIdentifier = journeyIdentifier;
    }

    public ServiceDate getStartDate() {
        return startDate;
    }

    public void setStartDate(ServiceDate startDate) {
        this.startDate = startDate;
    }

    public ServiceDate getEndDate() {
        return endDate;
    }

    public void setEndDate(ServiceDate endDate) {
        this.endDate = endDate;
    }

    public int getMonday() {
        return monday;
    }

    public void setMonday(int monday) {
        this.monday = monday;
    }

    public int getTuesday() {
        return tuesday;
    }

    public void setTuesday(int tuesday) {
        this.tuesday = tuesday;
    }

    public int getWednesday() {
        return wednesday;
    }

    public void setWednesday(int wednesday) {
        this.wednesday = wednesday;
    }

    public int getThursday() {
        return thursday;
    }

    public void setThursday(int thursday) {
        this.thursday = thursday;
    }

    public int getFriday() {
        return friday;
    }

    public void setFriday(int friday) {
        this.friday = friday;
    }

    public int getSaturday() {
        return saturday;
    }

    public void setSaturday(int saturday) {
        this.saturday = saturday;
    }

    public int getSunday() {
        return sunday;
    }

    public void setSunday(int sunday) {
        this.sunday = sunday;
    }

    public String getRouteIdentifier() {
        return routeIdentifier;
    }

    public void setRouteIdentifier(String routeIdentifier) {
        this.routeIdentifier = routeIdentifier;
    }

    public String getRouteDirection() {
        return routeDirection;
    }

    public void setRouteDirection(String routeDirection) {
        this.routeDirection = routeDirection;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public List<roadDateRun> getCalendarModifications() {
        return calendarModifications;
    }

    public void setCalendarModifications(
            List<roadDateRun> calendarModifications) {
        this.calendarModifications = calendarModifications;
    }

    public List<roadTimePoint> getTimePoints() {
        return timePoints;
    }

    public void setTimePoints(List<roadTimePoint> elements) {
        this.timePoints = elements;
    }
}
