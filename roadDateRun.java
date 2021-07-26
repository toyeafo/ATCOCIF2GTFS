/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import org.onebusaway.gtfs.model.calendar.ServiceDate;

/**
 *
 * @author AFROGENIUS
 */
public class roadDateRun extends roadElement implements roadChild, Comparable<roadDateRun> {

    private ServiceDate startDate;

    private ServiceDate endDate;

    private int operationCode;

    public roadDateRun() {
        super(Type.JOURNEY_DATE_RUNNING);
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

    public int getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(int operationCode) {
        this.operationCode = operationCode;
    }

    @Override
    public int compareTo(roadDateRun r) {
        int c = this.startDate.compareTo(r.startDate);
        if (c != 0) {
            return c;
        }
        c = this.endDate.compareTo(r.endDate);
        if (c != 0) {
            return c;
        }
        return r.operationCode - this.operationCode;
    }
}
