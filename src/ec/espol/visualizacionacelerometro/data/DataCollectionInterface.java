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
 * Encapsulates a generic connection to a data source.
 * @author Federico Domínguez
 */
public interface DataCollectionInterface {
    
    /**
     * Enumeration of possible connection attempts results.
     */
    static public enum CONNECTION_RESULT {
        CONNECTION_OK, CONNECTION_ERROR, CONNECTION_BUSY, CONNECTION_IO_ERROR};
    
    /**
     * Scans communication ports in the PC.
     * @return ArrayList containing names of all found ports. Returns empty list if no ports available.
     */
    public ArrayList<String> getComLinks();
    
    /**
     * Connects to an specific communication port.
     * @param comLink String containing name of serial port.
     * @return CONNECTION_RESULT enumeration.
     */
    public CONNECTION_RESULT connect(String comLink);
    
    /**
     * Disconnects from currently connected port
     */
    public void disconnect();
    
    /**
     * Connected/disconnected state.
     * @return true if connected.
     */
    public boolean isConnected();
    
}
