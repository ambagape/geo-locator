/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.Waypoint;
import javax.swing.JOptionPane;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;

/**
 *
 * @author ambagape
 */
public class MainApplet extends javax.swing.JApplet {

    static final Logger logger = Logger.getLogger(MainApplet.class.getName());
    private JLabel label = new JLabel();
    final int maxZoom = 18;
    /**
     * Initializes the applet MainApplet
     */
    @Override
    public void init() {
        
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainApplet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the applet */
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    
                    initComponents();
                    label.setForeground(Color.red);
                    jmk.getMainMap().add(label);
                    label.setVisible(true);
                    jmk.getMainMap().addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent evt){
                            logger.info("Mouse was clicked");
                            if(evt.getClickCount()>1){
                                Point p = evt.getPoint();
                                Rectangle rect = jmk.getMainMap().getViewportBounds();
                                p.translate(rect.x, rect.y);
                                GeoPosition gp = jmk.getMainMap().getTileFactory().pixelToGeo(p, jmk.getMainMap().getZoom());
                                logger.info("This was clicked: "+gp.toString());  
                                String[] li = DAO.find(gp); 
                                if(li==null){
                                    JOptionPane.showMessageDialog(rootPane, "No hotel near here");
                                    return;
                                }
                                logger.info("This was fected: "+new GeoPosition(DAO.coorConverter(li[5]),DAO.coorConverter(li[6])).toString());
                                JOptionPane.showMessageDialog(rootPane, "Name: "+li[1]+"\n Address: "+li[2]);
                            }
                        }
                    });
                    jmk.getMainMap().addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                        @Override
                        public void mouseMoved(MouseEvent evt){
                            
                            Point p = evt.getPoint();
                            label.setLocation(p);
                            
                            Rectangle rect = jmk.getMainMap().getViewportBounds();
                            p.translate(rect.x, rect.y);
                            GeoPosition gp = jmk.getMainMap().getTileFactory().pixelToGeo(p, jmk.getMainMap().getZoom());
                            String[] li = DAO.find(gp);
                            if(li != null){
                                label.setText(li[1]);   
                                label.setVisible(true);
                            }else{
                                label.setText("");
                                label.setVisible(false);
                            }
                            
                        }
                    });
                    
                    
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }
    
    private Set<Waypoint> loadPoints(){
        
        Set<Waypoint> wayPoints = new HashSet<>();
        List<String[]> l = DAO.getAll();
        try{
             
            String[] line;            
                      
            for(int i=1;i<l.size();i++){
                line = l.get(i);
                logger.log(Level.INFO, "Converting: {0}, {1} of line {2} to decimal.", new Object[]{line[5], line[6], i});                
                double coordOne = DAO.coorConverter(line[5]);
                double coordTwo = DAO.coorConverter(line[6]);
                Waypoint p = new Waypoint(new GeoPosition(coordOne,coordTwo));
               
                wayPoints.add(p);
                if(i==2)
                    centerPos= new GeoPosition(coordOne,coordTwo);
                i++;
            }
           
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        return wayPoints;
    }
    private static GeoPosition centerPos = new GeoPosition(6.33115,5.596817);
    
    /**
     * This method is called from within the init() method to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jmk = new org.jdesktop.swingx.JXMapKit();

        TileFactoryInfo info = new TileFactoryInfo(0, 15, 18, 256, true, true, "http://a.tile.openstreetmap.org", "x", "y", "z"){
            public String getTileUrl(int x, int y, int zoom){
                int realZoom = maxZoom - zoom;
                String url = this.baseURL + "/" + realZoom + "/" + x + "/" + y + ".png";
                return url;
            }
        };
        DefaultTileFactory dtf = new DefaultTileFactory(info);
        jmk.setTileFactory(dtf);
        //jmk.setAddressLocation(new org.jdesktop.swingx.mapviewer.GeoPosition(6.33115,5.596817));
        org.jdesktop.swingx.mapviewer.WaypointPainter painter = new org.jdesktop.swingx.mapviewer.WaypointPainter<>();
        painter.setWaypoints(loadPoints());
        painter.setRenderer(new WaypointRenderer() {
            public boolean paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint wp) {
                g.setColor(Color.RED);
                g.drawLine(-5,-5,+5,+5);
                g.drawLine(-5,+5,+5,-5);
                return true;
            }
        });
        jmk.getMainMap().setCenterPosition(centerPos);
        jmk.getMainMap().setOverlayPainter(painter);
        jmk.getMainMap().setZoom(3);
        jmk.setDataProviderCreditShown(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jmk, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jmk, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXMapKit jmk;
    // End of variables declaration//GEN-END:variables
}
