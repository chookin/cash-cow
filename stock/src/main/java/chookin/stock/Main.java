package chookin.stock;

import chookin.stock.orm.service.ZStock;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Created by chookin on 7/27/14.
 */
@Configuration("main")
public class Main {
    private final static Logger LOG = Logger.getLogger(Main.class);

    @Autowired
    private ZStock zStock;
    public static void main(String[] args) {
        LOG.info("ZStock started.");
        try {
            ApplicationContext context =
                    new ClassPathXmlApplicationContext("applicationContext.xml");

            Main service = (Main) context.getBean("main");
//            service.extractStocks();
            service.saveCompanyInfo();
//            service.setProxy();
//            service.saveHistoryData();
        } catch (Throwable t) {
            LOG.error(null, t);
        }
        LOG.info("ZStock stopped.");
    }
    public void extractStocks(){
        try{
            this.zStock.saveStocks();
        } catch (Throwable e) {
            LOG.error(null, e);
        }
    }
    public void saveCompanyInfo(){
        try {
            this.zStock.saveCompanyInfo();
        } catch (Throwable e) {
            LOG.error(null, e);
        }
    }
    public void setProxy(){
        String host = "proxy.cmcc";
        int port = 8080;
        String authUser = "";
        String authPassword = "";
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", Integer.toString(port));
        System.setProperty("http.proxyUser", authUser);
        System.setProperty("http.proxyPassword", authPassword);

    }
    public void saveHistoryData(){
        try {
            for(int year = 2009;year <= 2013;++year){
                for(int quarter = 1;quarter<5;++quarter){
                    this.zStock.saveHistoryData(year, quarter);
                }
            }
        } catch (Throwable e) {
            LOG.error(null, e);
        }
    }
}