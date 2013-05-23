/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import java.util.Properties;

/**
 *
 * @author ambagape
 */
public class DAO {
    private static final Logger logger = Logger.getLogger(DAO.class.getName());
    private static CSVReader reader = null;
    private static File f;
    private static File confFile = new File("gisapp.ini");
    private static DAO instance = new DAO();
    private Properties config = null;
    private int latInd;
    private int longInd;
    
    private DAO(){
        logger.log(Level.INFO, "Loading a file at: {0}", confFile.getAbsolutePath());
        try {
            config = new Properties();
            config.load(new FileReader(confFile.getAbsolutePath()));
            f = new File(config.getProperty("file_name"));
            latInd = Integer.parseInt(config.getProperty("latitude"));
            longInd = Integer.parseInt(config.getProperty("longitude"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public int getLatInd(){
        return latInd;
    }
    public int getLongInd(){
        return longInd;
    }
    public Properties getConfig(){
        return config;
    }
    
    public static DAO getInstance(){
        return instance;
    }
    
    public List<String[]> getAll(){
        
        List<String[]> list = null;
        logger.log(Level.INFO, "Loading a file at: {0}", f.getAbsolutePath());
        try {
            reader = new CSVReader(new FileReader(f.getAbsolutePath()));            
            list = reader.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally{
            try {
                reader.close();                
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        return list;
        
    }
    
    public String[] find(GeoPosition gp){
        String[] result = null;
        String[] finalResult = null;
        double diff = 0.0005;
        try {
            reader = new CSVReader(new FileReader(f.getAbsolutePath())); 
            //Jump the column headers
            reader.readNext();
            while((result=reader.readNext())!=null)
                if(diff > (Math.abs(coorConverter(result[latInd])-gp.getLatitude())+Math.abs(coorConverter(result[longInd])-gp.getLongitude()))){
                    diff=Math.abs(coorConverter(result[latInd])-gp.getLatitude())+Math.abs(coorConverter(result[longInd])-gp.getLongitude());
                    finalResult = result;
                }
                            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally{
            try {
                reader.close();                
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return finalResult;
    }
    
    public double coorConverter(String deg){
        deg = deg.trim();
        int a;
        double b;
        if(deg.split(" ").length<2){
            a = Integer.parseInt(Character.toString(deg.charAt(0)));
            b = Double.parseDouble(deg.substring(1));
        }else{
            a = Integer.parseInt(deg.split(" ")[0]);        
            b = Double.parseDouble(deg.split(" ")[1]);        
        }
        double d = a+b/60;
        return d;
    }
}
