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
package ec.espol.visualizacionacelerometro.GUI;

import ec.espol.visualizacionacelerometro.control.VisualizacionAcelerometro;
import ec.espol.visualizacionacelerometro.data.*;
import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import javax.swing.ImageIcon;
import org.math.plot.Plot3DPanel;
import org.math.plot.plots.LinePlot;

/**
 * Main view frame class.
 * @author Federico Domínguez
 */
public class VisualizacionFrame extends javax.swing.JFrame {
    
    private Plot3DPanel plot;
    public static final double MAX_AXIS = 1.5; //Maximum +/- value for all three axis. 
    public static final double INIT_ALPHA = 0.5; //Initial alpha value for EMA filter
    
    private final ImageIcon iconConnOFF;
    private final ImageIcon iconConnON;
    private final ImageIcon iconConnERR;
    private final ImageIcon iconFrame20;
    private final ImageIcon iconFrame26;
    private final ImageIcon iconFrame28;
    private final ImageIcon iconFrame32;
    private final ImageIcon iconFrame48;
    private final ArrayList<Image> icons;
    private final AccDataParser accDataParser;
    private final EMAFilter digitalFilterX;
    private final EMAFilter digitalFilterY;
    private final EMAFilter digitalFilterZ;
    
    
    private LinePlot xLine;
    private LinePlot yLine;
    private LinePlot zLine;
    private LinePlot xyzLine;

    /**
     * Creates new form VisualizacionFrame
     */
    public VisualizacionFrame() {
        iconConnOFF = createImageIcon("/resources/off_light.png", "Conección OFF");
        iconConnON = createImageIcon("/resources/green_light.png", "Conección ON");
        iconConnERR = createImageIcon("/resources/red_light.png", "Conección Error");
        iconFrame20 = createImageIcon("/resources/coordinator20x20.png", "Logo");
        iconFrame26 = createImageIcon("/resources/coordinator26x26.png", "Logo");
        iconFrame28 = createImageIcon("/resources/coordinator28x28.png", "Logo");
        iconFrame32 = createImageIcon("/resources/coordinator32x32.png", "Logo");
        iconFrame48 = createImageIcon("/resources/coordinator48x48.png", "Logo Big");
        icons = new ArrayList();
        icons.add(iconFrame20.getImage());
        icons.add(iconFrame26.getImage());
        icons.add(iconFrame28.getImage());
        icons.add(iconFrame32.getImage());
        icons.add(iconFrame48.getImage());
        
        initComponents();
        init3DPlot();
        
        accDataParser = new AccDataParser();

        digitalFilterX = new EMAFilter(INIT_ALPHA);
        digitalFilterY = new EMAFilter(INIT_ALPHA);
        digitalFilterZ = new EMAFilter(INIT_ALPHA);
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    private ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    /**
     * Initializes the 3D plot. Creates the three line plots, colors them and sets the bounds.
     */
    private void init3DPlot() {
        double[][] X = new double[2][3];
        
        plot = new Plot3DPanel();

        plot.setAxisLabel(0, "X [g]");
        plot.setAxisLabel(1, "Y [g]");
        plot.setAxisLabel(2, "Z [g]");
        
        X[0][0] = 0;
        X[0][1] = 0;
        X[0][2] = 0;

        X[1][0] = 0;
        X[1][1] = 0;
        X[1][2] = 0;
        
        xLine = new LinePlot("X", Color.blue, X);
        yLine = new LinePlot("Y", Color.blue, X);
        zLine = new LinePlot("Z", Color.blue, X);
        xyzLine = new LinePlot("XYZ", Color.red, X);
        plot.addPlot(xLine);
        plot.addPlot(yLine);
        plot.addPlot(zLine);
        plot.addPlot(xyzLine);
        plot.setFixedBounds(0, -1*MAX_AXIS, MAX_AXIS);
        plot.setFixedBounds(1, -1*MAX_AXIS, MAX_AXIS);
        plot.setFixedBounds(2, -1*MAX_AXIS, MAX_AXIS);
        
        visualizacionInternalFrame.setContentPane(plot);
    }
    
    /**
     * Draws the three dimensional axes from the serial port data.
     * All values are in g units. 1 g = 9.81 m/s^2
     * @param x x-axis acceleration value.
     * @param y y-axis acceleration value.
     * @param z z-axis acceleration value.
     */
    public void drawXYZData(double x, double y, double z){
        double[][] X = new double[2][3];
        double[][] Y = new double[2][3];
        double[][] Z = new double[2][3];
        double[][] XYZ = new double[2][3];
        
        X[0][0] = 0;
        X[0][1] = 0;
        X[0][2] = 0;

        X[1][0] = x;
        X[1][1] = 0;
        X[1][2] = 0;
        
        Y[0][0] = 0;
        Y[0][1] = 0;
        Y[0][2] = 0;

        Y[1][0] = 0;
        Y[1][1] = y;
        Y[1][2] = 0;
        
        Z[0][0] = 0;
        Z[0][1] = 0;
        Z[0][2] = 0;

        Z[1][0] = 0;
        Z[1][1] = 0;
        Z[1][2] = z;
        
        XYZ[0][0] = 0;
        XYZ[0][1] = 0;
        XYZ[0][2] = 0;

        XYZ[1][0] = x;
        XYZ[1][1] = y;
        XYZ[1][2] = z;
        
        plot.getPlot(0).setData(X);
        plot.getPlot(1).setData(Y);
        plot.getPlot(2).setData(Z);
        plot.getPlot(3).setData(XYZ);
        plot.repaint();
    }
    
    /**
     * Sets the available serial ports to be shown in the GUI.
     * @param comLinks Array containing the name of available serial ports in the PC.
     */
    public void setComLinks(ArrayList<String> comLinks){
        
        Enumeration e = Collections.enumeration(comLinks);
        
        if(e.hasMoreElements()){
            puertosComboBox.removeAllItems();
            while(e.hasMoreElements())
                puertosComboBox.addItem(e.nextElement());
        }
    }
    
    /**
     * Call this method to update the main view with the accelerator data.
     * @param data String containing the data. It is assumed that all three axis readings are concatenated.
     */
    public void receiveNewData(String data){
        //The class AccDataParser parses the three axis from the data.
        ArrayList<Double> xyz = accDataParser.parse(data);
        
        if(xyz != null){
            double x = digitalFilterX.filter(xyz.get(0));
            double y = digitalFilterY.filter(xyz.get(1));
            double z = digitalFilterZ.filter(xyz.get(2));
            drawXYZData(x,y,z);
            coneccionLabel.setIcon(iconConnON); //If the data is ok, show green icon.
        }else{
            coneccionLabel.setIcon(iconConnERR); //If the data can´t be parsed, show red icon.
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        empezarButton = new javax.swing.JButton();
        pararButton = new javax.swing.JButton();
        puertosComboBox = new javax.swing.JComboBox();
        coneccionLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        alphaSlider = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        alphaText = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        visualizacionInternalFrame = new javax.swing.JInternalFrame();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Visualización Acelerometro");
        setIconImage(iconFrame20.getImage());
        setIconImages(icons);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Recolección de datos"));
        jPanel1.setToolTipText("Recolección de datos");

        empezarButton.setText("Empezar");
        empezarButton.setToolTipText("Empezar recolección de datos");
        empezarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                empezarButtonActionPerformed(evt);
            }
        });

        pararButton.setText("Parar");
        pararButton.setToolTipText("Parar recolección de datos");
        pararButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pararButtonActionPerformed(evt);
            }
        });

        puertosComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No disponible" }));
        puertosComboBox.setToolTipText("Seleccione puerto serial");

        coneccionLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/off_light.png"))); // NOI18N
        coneccionLabel.setText("Conección");
        coneccionLabel.setToolTipText("Estado conección");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(empezarButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pararButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(puertosComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(coneccionLabel)
                .addContainerGap(166, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(empezarButton)
                    .addComponent(puertosComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coneccionLabel))
                .addGap(18, 18, 18)
                .addComponent(pararButton)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtro EMA"));
        jPanel2.setToolTipText("Filtro de datos");

        alphaSlider.setMajorTickSpacing(10);
        alphaSlider.setMinorTickSpacing(2);
        alphaSlider.setToolTipText("Valor de alpha");
        alphaSlider.setValue(50);
        alphaSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                alphaSliderStateChanged(evt);
            }
        });

        jLabel1.setText("0");

        jLabel2.setText("1");

        jLabel3.setText("alpha");

        alphaText.setText("0.5");
        alphaText.setToolTipText("valor de alpha");
        alphaText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alphaTextActionPerformed(evt);
            }
        });

        jLabel4.setText("alpha = ");

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/ema.png"))); // NOI18N
        jLabel6.setToolTipText("");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(alphaSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(alphaText, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addContainerGap(84, Short.MAX_VALUE))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(197, 197, 197)
                .addComponent(jLabel3)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(alphaSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(alphaText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        visualizacionInternalFrame.setBorder(null);
        visualizacionInternalFrame.setTitle("Orientación");
        visualizacionInternalFrame.setToolTipText("Orientación del acelerometro");
        visualizacionInternalFrame.setVisible(true);

        javax.swing.GroupLayout visualizacionInternalFrameLayout = new javax.swing.GroupLayout(visualizacionInternalFrame.getContentPane());
        visualizacionInternalFrame.getContentPane().setLayout(visualizacionInternalFrameLayout);
        visualizacionInternalFrameLayout.setHorizontalGroup(
            visualizacionInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        visualizacionInternalFrameLayout.setVerticalGroup(
            visualizacionInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 413, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(visualizacionInternalFrame)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(visualizacionInternalFrame)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Empezar (start) button has been pressed.
     * @param evt 
     */
    private void empezarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_empezarButtonActionPerformed
        //connects to the serial port
        DataCollectionInterface.CONNECTION_RESULT resultado = VisualizacionAcelerometro.getDataSource().connect((String) puertosComboBox.getSelectedItem());
        
        //show visual feedback
        switch(resultado){
            case CONNECTION_OK:
                coneccionLabel.setIcon(iconConnON);
                break;
            case CONNECTION_ERROR:
                coneccionLabel.setIcon(iconConnERR);
                break;
            case CONNECTION_BUSY:
                coneccionLabel.setIcon(iconConnOFF);
                break;
            default:
                coneccionLabel.setIcon(iconConnOFF);
        }
    }//GEN-LAST:event_empezarButtonActionPerformed

    /**
     * Parar (stop) button has been pressed.
     * @param evt 
     */
    private void pararButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pararButtonActionPerformed
        //Disconnect from serial port
        VisualizacionAcelerometro.getDataSource().disconnect();
        coneccionLabel.setIcon(iconConnOFF);
    }//GEN-LAST:event_pararButtonActionPerformed

    /**
     * Main window is closing.
     * @param evt 
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //Disconnect from serial port
        VisualizacionAcelerometro.getDataSource().disconnect();
    }//GEN-LAST:event_formWindowClosing

    /**
     * EMA filter slider has been moved.
     * @param evt 
     */
    private void alphaSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_alphaSliderStateChanged

        Double alpha = ((double) alphaSlider.getValue()) / 100;
        digitalFilterX.setAlpha(alpha);
        digitalFilterY.setAlpha(alpha);
        digitalFilterZ.setAlpha(alpha);
        alphaText.setText(alpha.toString());
    }//GEN-LAST:event_alphaSliderStateChanged

    /**
     * EMA alpha text box has been edited.
     * @param evt 
     */
    private void alphaTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alphaTextActionPerformed
        String alphaStr = alphaText.getText();
        
        try{
            Double alpha = Double.parseDouble(alphaStr);
            digitalFilterX.setAlpha(alpha);
            digitalFilterY.setAlpha(alpha);
            digitalFilterZ.setAlpha(alpha);
            alpha = alpha * 100;
            alphaSlider.setValue(alpha.intValue());
        }catch(Exception e){
            Double alpha = ((double) alphaSlider.getValue()) / 100;
            alphaText.setText(alpha.toString());
        }
    }//GEN-LAST:event_alphaTextActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider alphaSlider;
    private javax.swing.JTextField alphaText;
    private javax.swing.JLabel coneccionLabel;
    private javax.swing.JButton empezarButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton pararButton;
    private javax.swing.JComboBox puertosComboBox;
    private javax.swing.JInternalFrame visualizacionInternalFrame;
    // End of variables declaration//GEN-END:variables
}
