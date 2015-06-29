package chookin.stock.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zhuyin on 5/23/15.
 */
public class SpringHelper {
    private static ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    public static ApplicationContext getAppContext(){
        return appContext;
    }
}
