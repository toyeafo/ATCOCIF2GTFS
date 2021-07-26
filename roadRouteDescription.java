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
public class roadRouteDescription extends roadElement {
    private String operatorId;
private String routeNumber;
private String routeDirection;
private String routeDescription;

public roadRouteDescription() {
super(Type.ROUTE_DESCRIPTION);
}

public String getOperatorId() {
return operatorId;
}

public void setOperatorId(String operatorId) {
this.operatorId = operatorId;
}

public String getRouteNumber() {
return routeNumber;
}

public void setRouteNumber(String routeNumber) {
this.routeNumber = routeNumber;
}

public String getRouteDirection() {
return routeDirection;
}

public void setRouteDirection(String routeDirection) {
this.routeDirection = routeDirection;
}

public String getRouteDescription() {
return routeDescription;
}

public void setRouteDescription(String routeDescription) {
this.routeDescription = routeDescription;
}
}
