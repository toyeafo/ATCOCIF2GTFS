/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import java.io.File;

/**
 *
 * @author AFROGENIUS
 */
public class roadElement {

    roadElement() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public enum Type {

        JOURNEY_DATE_RUNNING,
        JOURNEY_HEADER,
        JOURNEY_ORIGIN,
        JOURNEY_INTERMEDIATE,
        JOURNEY_DESTINATION,
        LOCATION,
        ADDITIONAL_LOCATION,
        VEHICLE_TYPE,
        ROUTE_DESCRIPTION,
        OPERATOR,
        UNKNOWN
    }

    private final Type type;

    private File path;

    private int lineNumber;

    public roadElement(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
