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
public class roadOperator extends roadElement {
    private String operatorId;

  private String shortFormName;

  private String legalName;

  private String enquiryPhone;

  private String contactPhone;

  public roadOperator() {
    super(Type.OPERATOR);
  }

  public String getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(String operatorId) {
    this.operatorId = operatorId;
  }

  public String getShortFormName() {
    return shortFormName;
  }

  public void setShortFormName(String shortFormName) {
    this.shortFormName = shortFormName;
  }

  public String getLegalName() {
    return legalName;
  }

  public void setLegalName(String legalName) {
    this.legalName = legalName;
  }

  public String getEnquiryPhone() {
    return enquiryPhone;
  }

  public void setEnquiryPhone(String enquiryPhone) {
    this.enquiryPhone = enquiryPhone;
  }

  public String getContactPhone() {
    return contactPhone;
  }

  public void setContactPhone(String contactPhone) {
    this.contactPhone = contactPhone;
  }
}
