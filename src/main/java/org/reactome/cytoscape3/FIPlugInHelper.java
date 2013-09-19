/*
 * Created on May 8, 2009
 *
 */
package org.reactome.cytoscape3;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.reactome.cancerindex.model.CancerIndexSentenceDisplayFrame;
import org.reactome.cytoscape.service.FINetworkService;
import org.reactome.cytoscape.util.PlugInObjectManager;
/**
 * A singleton to manage other singleton objects, and some utility methods.
 * 
 * @author wgm ported July 2013 by Eric T Dawson
 */
public class FIPlugInHelper {
    private static FIPlugInHelper helper;
    // Try to track CancerIndexSentenceDisplayFrame
    private CancerIndexSentenceDisplayFrame cgiFrame;
    // Currently selected FI network version
    private String fiNetworkVersion;
    private Map<Integer, Map<String, Double>> moduleToSampleToValue;

    private FIPlugInHelper() {
    }

    public static FIPlugInHelper getHelper()
    {
        if (helper == null)
        {
            helper = new FIPlugInHelper();
        }
        return helper;
    }

    public String getFiNetworkVersion()
    {
        if (this.fiNetworkVersion != null)
            return this.fiNetworkVersion;
        else
            return getDefaultFINeworkVersion();
    }

    public String getDefaultFINeworkVersion()
    {
        Properties prop = PlugInObjectManager.getManager().getProperties();
        String fiVersions = prop.getProperty("FINetworkVersions");
        String[] tokens = fiVersions.split(",");
        for (String token : tokens)
        {
            token = token.trim();
            if (token.toLowerCase().contains("default")) return token;
        }
        return null;
    }

    public void setFiNetworkVersion(String fiNetworkVersion) {
        this.fiNetworkVersion = fiNetworkVersion;
    }
    
    public CancerIndexSentenceDisplayFrame getCancerIndexFrame(JFrame jFrame) {
        if (cgiFrame == null)
        {
            cgiFrame = new CancerIndexSentenceDisplayFrame();
            cgiFrame.setTitle("Cancer Index Annotations");
            cgiFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            cgiFrame.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    cgiFrame = null; // Enable to GC.
                }
            });
            cgiFrame.setSize(800, 600);
            cgiFrame.setLocationRelativeTo(jFrame);
            cgiFrame.setVisible(true);
        }
        else
        {
            cgiFrame.setState(Frame.NORMAL);
            cgiFrame.toFront();
        }
        return cgiFrame;
    }

    public FINetworkService getNetworkService() throws Exception
    {
        Properties prop = PlugInObjectManager.getManager().getProperties();
        String clsName = prop.getProperty("networkService",
                "org.reactome.cytoscape3.LocalService");
        FINetworkService networkService = (FINetworkService) Class.forName(
                clsName).newInstance();
        return networkService;
    }

    /**
     * Get the RESTful URL
     * 
     * @param fiVersion
     * @return
     */
    public String getRestfulURL(String fiVersion)
    {
        fiVersion = fiVersion.replaceAll(" ", "_");
        String key = fiVersion + "_restfulURL";
        Properties prop = PlugInObjectManager.getManager().getProperties();
        return prop.getProperty(key);
    }

    public String getRestfulURL()
    {
        return getRestfulURL(getFiNetworkVersion());
    }

    public String getDataSourceURL(String fiVersion)
    {
        String dataSourceURL = PlugInObjectManager.getManager().getProperties().getProperty("dataSourceURL");
        fiVersion = fiVersion.replaceAll(" ", "_");
        String dbName = PlugInObjectManager.getManager().getProperties().getProperty(fiVersion + "_sourceDb");
        String rtn = dataSourceURL.replace("${DB_NAME}", dbName);
        return rtn;
    }

    public String getDataSourceURL()
    {
        return getDataSourceURL(getFiNetworkVersion());
    }
   

    public void storeMCLModuleToSampleToValue(Map<Integer, Map<String, Double>> moduleToSampleToValue)
    {
        this.moduleToSampleToValue = moduleToSampleToValue;

    }
    
    public Map<Integer, Map<String, Double>> getMCLModuleToSampleToValue()
    {
        return this.moduleToSampleToValue;
    }
    
}
