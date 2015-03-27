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

/**
 * Implements a simple EMA (Exponential Moving Average). S(t) = alpha*Y(t) + (1-alpha)S(t-1)
 * See http://en.wikipedia.org/wiki/Moving_average
 * @author Federico Domínguez
 */
public class EMAFilter implements DataFilterInterface {
    private double alpha;
    private boolean isFirst;
    private double previousDatum;
    
    public EMAFilter(double alpha){
        this.alpha = alpha;
        isFirst = true;
    }

    @Override
    public double filter(double datum) {
        //Sets first viewed sample as previous sample
        if(isFirst){
            previousDatum = datum;
            isFirst = false;
        }
        
        //Applies EMA equation
        double s = alpha*datum + (1-alpha)*previousDatum;
        previousDatum = s;
        
        return s;
    }
    
    public void setAlpha(double alpha){
        this.alpha = alpha;
    }
    
    public double getAlpha(){
        return alpha;
    }
    
}
