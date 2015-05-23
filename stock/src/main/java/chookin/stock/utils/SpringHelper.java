package chookin.stock.utils;

import cmri.etl.common.NetworkHelper;
import cmri.utils.configuration.ConfigManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zhuyin on 5/23/15.
 */
public class SpringHelper {
    private static ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    static {
        ConfigManager.setFile("stock.xml");
        NetworkHelper.setDefaultProxy();
    }
    public static ApplicationContext getAppContext(){
        return appContext;
    }
}
