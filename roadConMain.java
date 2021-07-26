/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author AFROGENIUS
 */
public class roadConMain {
    
    
    public static void main(String[] args) throws IOException {
        
        //
        Scanner input = new Scanner(System.in); 
        
        //Set input path for CIF file
        System.out.println("Enter input path: ");
        String inPath = input.next();
        
        //Set output path for gtfs
        System.out.println("Enter output path: ");
        String outPath = input.next();
        
        //Set vehicle type
//        System.out.println("Enter vehicle type: ");
//        String vehType = input.next();
//        String vehValue = vehType.toLowerCase();
        
        roadConMain startP = new roadConMain();
        startP.run(new File(inPath), new File(outPath));
    }
    
    //
    public void run(File inPath, File outPath) throws IOException {
        
        roadConverter converter = new roadConverter();
        
        converter.setInputPath(inPath);
        
        converter.setOutputPath(outPath);
        
//        if (vehValue.contains("tram")) {
//        converter.setVehicleType(0);
//      } else if (vehValue.contains("subway")) {
//        converter.setVehicleType(1);
//      } else if (vehValue.contains("rail")) {
//        converter.setVehicleType(2);
//      } else if (vehValue.contains("bus")) {
//        converter.setVehicleType(3);
//      } else if (vehValue.contains("ferry")) {
//        converter.setVehicleType(4);
//      } else {
//        try {
//          converter.setVehicleType(Integer.parseInt(vehValue));
//        } catch (NumberFormatException ex) {
//          throw new roadException(
//              "unknown vehicle type specified: " + vehValue);
//        }
//      }
        
        converter.run();
    }
}
