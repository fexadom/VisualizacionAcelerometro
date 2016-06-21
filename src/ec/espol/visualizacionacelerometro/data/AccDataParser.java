/*
 * Copyright (C) 2015 Federico Domínguez
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ec.espol.visualizacionacelerometro.data;

import java.util.ArrayList;

/**
 * Parses data coming from the accelerometer and converts it to acceleration in g units.
 * By default, it assumes that each axis value is separated by a tab.
 * All axis values are direct readings from the ADC.
 * @author Federico Domínguez
 */
public class AccDataParser {
    
    //Tab by default
    private String separator;
    
    //Maximum G reading supported by the acceleromenter
    public final double MAX_G = 3;
    
    //Arduino ADC reference voltage
    public final double V_ARDUINO_REF = 5;
    
    //Accelerometer reference voltage
    public final double V_ACC_REF = 3.3;
    
    //Arduino ADC bit resolution (10-bits)
    public final int ADC_BITS = 1024;
    
    public AccDataParser(){
        separator = "\t";
    }
    
    public void setSaparator(String separator){
        this.separator = separator;
    }
    
    public String getSeparator(){
        return separator;
    }
    
    /**
     * Parses the x,y,z axis values using the separator value to split them.
     * @param data String with x,y,z values, concatenated with a separator.
     * @return Array list with x,y,z values in g units. Returns null if data is corrupted and can't be parsed.
     */
    public ArrayList<Double> parse(String data){
        ArrayList<Double> xyz = new ArrayList();
        
        String[] splitData = data.split(separator);

        try{           
            double x = Double.parseDouble(splitData[0].trim());
            double y = Double.parseDouble(splitData[1].trim());
            double z = Double.parseDouble(splitData[2].trim());

            double xG = V_ARDUINO_REF*(x/ADC_BITS)*(2*MAX_G/V_ACC_REF) - MAX_G;
            double yG = V_ARDUINO_REF*(y/ADC_BITS)*(2*MAX_G/V_ACC_REF) - MAX_G;
            double zG = V_ARDUINO_REF*(z/ADC_BITS)*(2*MAX_G/V_ACC_REF) - MAX_G;
            
            xyz.add(0, xG);
            xyz.add(1, yG);
            xyz.add(2, zG);
        }catch(Exception e){
            System.out.println("Error parsing: "+e.getClass().getName());
            return null;
        }
        
        return xyz;
    }
    
}
