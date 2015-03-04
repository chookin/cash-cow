package chookin.utils.web;

import chookin.utils.configuration.ConfigManager;

/**
 * Created by work on 12/12/14.
 */
public class NetworkHelper {
    /**
     * Set proxy to access internet.
     */
    public static void setProxy(){
        getInitialProxy().set();
    }

    /**
     * Get the initial proxy to connect internet.
     * @return If property "proxy.enable" is true, then set proxy that configured by "proxy.host", "proxy.port"
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
    private static ProxyPool proxyPool = new ProxyPool();

    /**
     * Switch to use another proxy.
     */
    public static void switchProxy(){
        proxyPool.switchProxy();
    }
}