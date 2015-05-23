package chookin.stock.oper;

import cmri.utils.configuration.ConfigManager;
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
    private CollectOper collectOper;
    public StockCLI setArgs(String[] args){
        if(args.length == 0){
            args = ConfigManager.getProperty("cli.paras").split(" ");
        }
        this.collectOper.setArgs(args);
        return this;
    }
    boolean action() throws IOException {
        return this.collectOper.action()
                ;
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

}