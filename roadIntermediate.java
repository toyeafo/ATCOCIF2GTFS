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
public class roadIntermediate extends roadTimePoint {
    private int arrivalTime;
  private String arrivalString;

  private int departureTime;
  private String departureString;

  public roadIntermediate() {
    super(roadElement.Type.JOURNEY_INTERMEDIATE);
  }

  @Override
  public int getArrivalTime() {
    return arrivalTime;
  }

  public void setArrivalTime(int arrivalTime) {
    this.arrivalTime = arrivalTime;
  }

  @Override
  public int getDepartureTime() {
    return departureTime;
  }

  public void setDepartureTime(int departureTime) {
    this.departureTime = departureTime;
  }

    public String getArrivalString() {
        return arrivalString;
    }

    public void setArrivalString(String arrivalString) {
        this.arrivalString = arrivalString;
    }

    public String getDepartureString() {
        return departureString;
    }

    public void setDepartureString(String departureString) {
        this.departureString = departureString;
    }
}
