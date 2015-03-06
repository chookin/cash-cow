package chookin.etl.common;

import chookin.etl.downloader.JsoupDownloader;
import chookin.utils.configuration.ConfigManager;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by work on 12/12/14.
 */
public class NetworkHelper {
    private static final Logger LOG = Logger.getLogger(NetworkHelper.class);
    private static final ProxyPool proxyPool = new ProxyPool();
    private static long lastSwitchTime = 0;

    /**
     * Set proxy to access internet.
     */
    public static void setProxy(){
        getInitialProxy().set();
    }

    /**
     * Get the initial proxy to connect internet. If property "proxy.enable" is true, then set proxy that configured by "proxy.host", "proxy.port"
     */
    public static Proxy getInitialProxy() {
        boolean enable = ConfigManager.getPropertyAsBool("proxy.enable");
        if(!enable){
            return new Proxy();
        }
        String host = ConfigManager.getProperty("proxy.host");
        String port = ConfigManager.getProperty("proxy.port");
        String authUser = ConfigManager.getProperty("proxy.user");
        String authPassword = ConfigManager.getProperty("proxy.password");
        return new Proxy()
                .setHost(host)
                .setPort(Integer.parseInt(port))
                .setUser(authUser)
                .setPasswd(authPassword);
    }

    /**
     * Switch to use another proxy.
     */
    public static synchronized void switchProxy(){
        long switchInterval = 5000; // interval between two proxy setting up.
        if(System.currentTimeMillis() - lastSwitchTime < switchInterval){
            return;
        }
        Proxy proxy = proxyPool.nextProxy();
        if(proxy != null) {
            proxy.set();
            lastSwitchTime = System.currentTimeMillis();
        }
    }
    /**
     * @return whether or not can connect internet.
     */
    public static boolean checkNetwork(){
        return checkProxy(getInitialProxy());
    }

    public static boolean checkProxy(Proxy proxy){
        String url = ConfigManager.getProperty("test.url");
        if(UrlHelper.getProtocol(url) == null){
            url = "http://"+url;
        }
        if(!proxy.equals(getInitialProxy())) {
            proxy.set();
        }
        try{
            Document doc = new JsoupDownloader().getDocument(url);
            if(doc != null){
                LOG.info("success to access " + url + " on proxy "+proxy);
                return true;
            }
        } catch (IOException e) {
            LOG.trace(e.getMessage());
        }
        LOG.warn("fail to access " + url + " on proxy "+proxy);
        if(!proxy.equals(getInitialProxy())) {
            setProxy();
        }
        return false;
    }

    public static List<Proxy> checkProxy(Collection<Proxy> proxies){
        List<Proxy> success = new ArrayList<>();
        for (Proxy proxy :proxies){
            if(NetworkHelper.checkProxy(proxy)){
                success.add(proxy);
            }
        }
        LOG.info("valid proxies are " + success);
        return success;
    }
}