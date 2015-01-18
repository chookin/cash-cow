package chookin.stock;

import chookin.stock.handler.CollectHandler;
import chookin.utils.OptionParser;
import chookin.utils.configuration.ConfigManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;


/**
 * Created by chookin on 7/27/14.
 */
@Configuration("main")
public class StockCLI {
    private final static Logger LOG = Logger.getLogger(StockCLI.class);

    @Autowired
    private CollectHandler collectHandler;
    private OptionParser optionParser;
    public StockCLI setArgs(String[] args){
        if(args.length == 0){
            args = ConfigManager.getProperty("cli.paras").split(" ");
        }
        optionParser = new OptionParser(args);
        return this;
    }
    boolean action() throws IOException {
        return new CollectOper().action()
                ;
    }
    class CollectOper{
        public boolean action() throws IOException {
            String option = "collect";
            if(!processOption(option)){
                return false;
            }
            return collectStocks()
                    || collectCompany()
                    || collectHist()
                    || collectHistDetail()
                    ;
        }
        boolean collectStocks() throws IOException {
            String option = "--stock";
            if(!processOption(option)){
                return false;
            }
            collectHandler.collectStocks();
            return true;
        }
        public boolean collectCompany() throws IOException {
            String option = "--company";
            if(!processOption(option)){
                return false;
            }
            collectHandler.collectCompanyInfo();
            return true;
        }
        public boolean collectHist() throws IOException {
            String option = "--hist";
            if(!processOption(option)){
                return false;
            }
            String para = optionParser.getOption(option);
            String[] items = para.split(OptionParser.item_separator);
            if (items.length == 2) {
                collectHandler.collectHistoryData(Integer.parseInt(items[0]), Integer.parseInt(items[1]));
            } else if (items.length == 4) {
                collectHandler.collectHistoryData(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]));
            } else {
                collectHandler.collectCurrentQuarterHistoryData();
            }
            return true;
        }
        public boolean collectHistDetail() throws IOException {
            String option = "--histdetail";
            if(!processOption(option)){
                return false;
            }
            String para = optionParser.getOption(option);
            String[] items = para.split(OptionParser.item_separator);
            if (items.length == 2) {
                collectHandler.collectHistoryDetail(items[0], items[1]);
            } else {
                collectHandler.collectPrevDayHistoryDetail();
            }
            return true;
        }
    }

    public static void main(String[] args) {
        ConfigManager.setFile("stock.xml");
        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
            StockCLI service = (StockCLI) context.getBean("main");
            service.setArgs(args)
                    .action();
        } catch (Throwable t) {
            LOG.error(null, t);
        }
        LOG.info("ZStock stopped.");
    }
    boolean processOption(String option){
        if(optionParser.getOption(option) == null) {
            return false;
        }
        if(option.startsWith("--")){
            String paras = optionParser.getOption(option);
            LOG.info(String.format("process option '%s=%s'", option, paras));
        }else {
            LOG.info(String.format("process option '%s'", option));
        }
        return true;
    }
}