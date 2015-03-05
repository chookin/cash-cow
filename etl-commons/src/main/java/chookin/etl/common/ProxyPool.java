package chookin.etl.common;

import chookin.utils.configuration.ConfigManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuyin on 3/2/15.
 */
class ProxyPool{
    private static List<Proxy> proxies =null;
    private int index = 0;

    synchronized void init(){
        proxies = new ArrayList<>();
        NetworkHelper.checkNetwork();
        proxies.addAll(NetworkHelper.checkProxy(getConfiguredProxies()));
        proxies.add(NetworkHelper.getInitialProxy());
    }
    List<Proxy> proxies(){
        if(proxies == null){
            init();
        }
        return proxies;
    }
    Proxy nextProxy(){
        List<Proxy> myProxies = proxies();
        if(myProxies.isEmpty()){
            return null;
        }
        if(index == myProxies.size()){
            init(); // reload configuration.
            index = 0;
        }
        return myProxies.get(index++);
    }

    /**
     * Load proxies information from configuration file.
     * @return Proxies information.
     */
    List<Proxy> getConfiguredProxies(){
        List<Proxy> myProxies = new ArrayList<>();
        String str = ConfigManager.getProperty("proxy.pool");
        if(str == null) {
            return myProxies;
        }
        String[] arr = str.split(",|\n");
        for (String item : arr) {
            String myItem = item.trim();
            if(myItem.isEmpty()){
                continue;
            }
            if(myItem.startsWith("#")){
                continue;
            }

            Proxy proxy = new Proxy();
            int indexSemicolon = myItem.indexOf(":");
            proxy.setHost(myItem.substring(0, indexSemicolon).trim());
            int indexPound = myItem.indexOf("#");// pound sign
            if (indexPound != -1) {
                String port = myItem.substring(indexSemicolon + 1, indexPound).trim();
                proxy.setPort(Integer.parseInt(port));
                proxy.setDesc(myItem.substring(indexPound + 1).trim());
            } else {
                String port = myItem.substring(indexSemicolon + 1).trim();
                proxy.setPort(Integer.parseInt(port));
            }
            myProxies.add(proxy);
        }
        return myProxies;
    }
}
