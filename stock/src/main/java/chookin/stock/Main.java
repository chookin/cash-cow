package chookin.stock;

import chookin.stock.orm.service.ZStock;
import chookin.utils.OptionParser;
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
            OptionParser optionParser = new OptionParser(args);
            if(optionParser.getOption("--proxy") == "yes"){
                setProxy(true);
            }else{
                setProxy(false);
            }
            ApplicationContext context =
                    new ClassPathXmlApplicationContext("applicationContext.xml");

            Main service = (Main) context.getBean("main");
            if(optionParser.getOption("collect") != null) {
                LOG.info("process option `collect`");
                if (optionParser.getOption("--stock") != null) {
                    service.zStock.collectStocks();
                }
                if (optionParser.getOption("--cmpr") != null) {
                    service.zStock.collectCompanyInfo();
                }
                if (optionParser.getOption("--hist") != null) {
                    String para = optionParser.getOption("--hist");
                    String[] items = para.split(OptionParser.item_separator);
                    if (items.length == 2) {
                        service.zStock.collectHistoryData(Integer.parseInt(items[0]), Integer.parseInt(items[1]));
                    } else if (items.length == 4) {
                        service.zStock.collectHistoryData(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]));
                    } else {
                        service.zStock.collectCurrentQuarterHistoryData();
                    }
                }
                if (optionParser.getOption("--histdetail") != null) {
                    String para = optionParser.getOption("--histdetail");
                    String[] items = para.split(OptionParser.item_separator);
                    if (items.length == 2) {
                        service.zStock.collectHistoryDetail(items[0], items[1]);
                    } else {
                        service.zStock.collectYesterdayHistoryDetail();
                    }

                }
            }
        } catch (Throwable t) {
            LOG.error(null, t);
        }
        LOG.info("ZStock stopped.");
    }
    /**
     * Set system proxy.
     * java -Dhttp.proxyHost=proxy.cmcc  -Dhttp.proxyPort=8080 -jar stock.jar
     */
    public static void setProxy(boolean enable){
        if(!enable){
            System.setProperty("http.proxyHost", "");
            System.setProperty("http.proxyPort", Integer.toString(0));
            return;
        }
        String host = "proxy.cmcc";
        int port = 8080;
        String authUser = "";
        String authPassword = "";
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", Integer.toString(port));
        System.setProperty("http.proxyUser", authUser);
        System.setProperty("http.proxyPassword", authPassword);

    }
}