package chookin.stock.oper;

import chookin.stock.utils.SpringHelper;
import cmri.utils.web.NetworkHelper;
import cmri.utils.configuration.ConfigManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


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
            args = ConfigManager.get("cli.paras").split(" ");
        }
        this.collectOper.setArgs(args);
        return this;
    }

    boolean action() {
        return this.collectOper.action()
                ;
    }

    public static void main(String[] args) {
        ConfigManager.addFile("stock.xml");
        NetworkHelper.setDefaultProxy();
        StockCLI service = (StockCLI) SpringHelper.getAppContext().getBean("main");
        service.setArgs(args)
                .action();
        LOG.info("ZStock stopped.");
    }

}