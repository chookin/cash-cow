package chookin.utils.web;

import chookin.utils.configuration.ConfigManager;
import org.apache.log4j.Logger;

/**
 * Created by work on 12/12/14.
 */
public class NetworkHelper {
    private final static Logger LOG = Logger.getLogger(NetworkHelper.class);
    public static void setProxy(boolean enable){
        if(!enable){
            System.setProperty("http.proxyHost", "");
            System.setProperty("http.proxyPort", Integer.toString(0));
            return;
        }
        String host = ConfigManager.getProperty("proxy.host");
        int port = ConfigManager.getPropertyAsInteger("proxy.port");
        String authUser = ConfigManager.getProperty("proxy.user");
        String authPassword = ConfigManager.getProperty("proxy.password");

        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", Integer.toString(port));
        System.setProperty("http.proxyUser", authUser);
        System.setProperty("http.proxyPassword", authPassword);

        LOG.info("enable proxy: "+host +":"+port);
    }
}