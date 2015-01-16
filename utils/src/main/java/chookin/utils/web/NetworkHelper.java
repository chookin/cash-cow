package chookin.utils.web;

import chookin.utils.configuration.ConfigManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by work on 12/12/14.
 */
public class NetworkHelper {
    private final static Logger LOG = Logger.getLogger(NetworkHelper.class);
    public static void setProxy(){
        getConfiguredProxy().set();
    }
    static ProxyConfig getConfiguredProxy() {
        boolean enable = ConfigManager.getPropertyAsBool("proxy.enable");
        if(!enable){
            return new ProxyConfig("", "");
        }
        String host = ConfigManager.getProperty("proxy.host");
        String port = ConfigManager.getProperty("proxy.port");
        String authUser = ConfigManager.getProperty("proxy.user");
        String authPassword = ConfigManager.getProperty("proxy.password");
        return new ProxyConfig(host, port, authUser, authPassword);
    }
    private static ProxyPool proxyPool = new ProxyPool();
    public static void switchProxy(){
        proxyPool.switchProxy();
    }
    public static class ProxyPool{
        private static List<ProxyConfig> proxies =null;
        private int index = 0;
        private long lastSwitchTime = 0;
        private static long switchInterval = 5000;
        public synchronized void switchProxy(){
            List<ProxyConfig> myProxies = getProxies();
            if(myProxies.isEmpty()){
                return;
            }
            if(System.currentTimeMillis() - lastSwitchTime < switchInterval){
                return;
            }
            lastSwitchTime = System.currentTimeMillis();
            if(index == myProxies.size()){
                index = 0;
            }
            myProxies.get(index++).set();
        }
        synchronized void init(){
            proxies = new ArrayList<>();
            proxies.addAll(getConfiguredProxies());
            proxies.add(getConfiguredProxy());
        }
        List<ProxyConfig> getProxies(){
            if(proxies == null){
                init();
            }
            return proxies;
        }
        List<ProxyConfig> getConfiguredProxies(){
            List<ProxyConfig> myProxies = new ArrayList<>();
            String str = ConfigManager.getProperty("proxy.pool");
            if(str == null) {
                return myProxies;
            }
            String[] arr = str.split(",");
            for (String item : arr) {
                ProxyConfig proxy = new ProxyConfig();
                int indexSemicolon = item.indexOf(":");
                proxy.setHost(item.substring(0, indexSemicolon).trim());
                int indexPound = item.indexOf("#");// pound sign
                if (indexPound != -1) {
                    proxy.setPort(item.substring(indexSemicolon + 1, indexPound).trim());
                    proxy.setDesc(item.substring(indexPound + 1).trim());
                } else {
                    proxy.setPort(item.substring(indexSemicolon + 1).trim());
                }
                myProxies.add(proxy);
            }
            return myProxies;
        }
    }
    public static class ProxyConfig{
        public ProxyConfig(){
            this("", "", "", "");
        }
        public ProxyConfig(String host, String port){
            this(host,port, "", "");
        }
        public ProxyConfig(String host, String port, String user, String passwd){
            this.host = host;
            this.port = port;
            this.user = user;
            this.passwd = passwd;
            this.desc = "";
        }

        /**
         * Use command is:
         * java -Dhttp.proxyHost=proxy.cmcc  -Dhttp.proxyPort=8080 -jar my.jar
         */
        public void set(){
            System.setProperty("http.proxyHost", host);
            System.setProperty("http.proxyPort", port);
            System.setProperty("http.proxyUser", user);
            System.setProperty("http.proxyPassword", passwd);
            LOG.info("set proxy: "+toString());
        }
        public String getHost() {
            return host;
        }

        public String getPort() {
            return port;
        }

        public String getUser() {
            return user;
        }

        public String getPasswd() {
            return passwd;
        }
        public String getDesc(){
            return desc;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        String host;
        String port;
        String user;
        String passwd;
        String desc;

        @Override
        public String toString() {
            if(host == null){
                return "None";
            }
            return "{" +
                    "host='" + host + '\'' +
                    ", port=" + port +
                    ", user='" + user + '\'' +
                    ", passwd='" + passwd + '\'' +
                    ", desc='" + desc + '\'' +
                    '}';
        }
    }
}