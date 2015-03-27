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

import ec.espol.visualizacionacelerometro.control.VisualizacionAcelerometro;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Implements the data collection using a serial port. Listens to incoming data events.
 * @author Federico Domínguez
 */
public class SerialDataCollection implements DataCollectionInterface, SerialPortEventListener {
    
    //Input stream
    private InputStream inStream;
    
    //Temporary buffere
    private final byte[] buffer;
    
    //Connected serial port
    private SerialPort serialPort;
    
    //Maintains connected/disconnected state
    private boolean isConnected;
    
    public SerialDataCollection(){
        isConnected = false;
        
        //Inicializa buffer de datos
        buffer = new byte[1024];
    }

    
    @Override
    public ArrayList<String> getComLinks() {
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        ArrayList<String> ports = new ArrayList();
        while ( portEnum.hasMoreElements() ) 
        {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            if(portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL)
                ports.add(portIdentifier.getName());
        }        
        return ports;
    }
    
    @Override
    public CONNECTION_RESULT connect(String comLink) {
        CONNECTION_RESULT result = CONNECTION_RESULT.CONNECTION_OK;
        try{
            if(!isConnected)
                serialConnect(comLink);
        }catch(PortInUseException e){
            System.out.println("Error connecting to serial port: "+e.getLocalizedMessage());
            result = CONNECTION_RESULT.CONNECTION_BUSY;
        }catch(IOException e){
            System.out.println("Error connecting to serial port: "+e.getLocalizedMessage());
            result = CONNECTION_RESULT.CONNECTION_IO_ERROR;
        }catch(NoSuchPortException e){
            System.out.println("Error connecting to serial port: "+e.getLocalizedMessage());
            result = CONNECTION_RESULT.CONNECTION_ERROR;
        }catch(Exception e){
            System.out.println("Error connecting to serial port: "+e.getLocalizedMessage());
            result = CONNECTION_RESULT.CONNECTION_ERROR;
        }
        
        return result;
    }

    
    @Override
    public void disconnect() {
        try{
            if(isConnected){
                inStream.close();
                serialPort.close();
                isConnected = false;
            }
        }catch(IOException e){
            System.out.println("Error disconnecting from serial port: "+e.getLocalizedMessage());
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }
    
    /**
     * Attempts to connect to an specific serial port.
     * @param portName String containing the serial port name. Example: "COM3"
     * @throws Exception Connection exception
     */
    private void serialConnect( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                serialPort = (SerialPort) commPort;
                
                //Default serial parameters for most devices (Arduino, XBee, etc.)
                serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                inStream = serialPort.getInputStream();
                
                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);
                isConnected = true;
            }
            else
            {
                System.out.println("Error: Only serial ports are supported.");
            }
        }
    }
    
    /**
     * The serial port generates an event, for example incoming data.
     * @param spe 
     */
    @Override
    public void serialEvent(SerialPortEvent spe) {
        int data;
        
        try
        {
            int len = 0;
            while ( ( data = inStream.read()) > -1 )
            {
                if ( data == '\n' ) {
                    break;
                }
                buffer[len++] = (byte) data;
            }
            
            //sends data to main view window
            VisualizacionAcelerometro.getGUIFrame().receiveNewData(new String(buffer,0,len));
        }
        catch ( IOException e )
        {
            System.out.println("Error reading from serial port: "+e.getLocalizedMessage());
        }  
    }
}
