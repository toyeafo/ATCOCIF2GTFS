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
public interface roadContentHandler {
    public void startDocument();

  public void endDocument();

  public void startElement(roadElement element);

  public void endElement(roadElement element);
}
