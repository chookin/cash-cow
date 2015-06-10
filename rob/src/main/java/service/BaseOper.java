package service;

import cmri.etl.common.NetworkHelper;
import cmri.utils.configuration.ConfigManager;
import cmri.utils.lang.DateHelper;
import cmri.utils.lang.OptionParser;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by zhuyin on 6/10/15.
 */
public abstract class BaseOper {
    private static Logger LOG;
    private OptionParser optionParser = new OptionParser();

    static {
        // configure log4j to log to custom file at runtime. In the java program directly by setting a system property (BEFORE you make any calls to log4j).
        try {
            String actionName = System.getProperty("action");
            if(actionName ==null) {
                System.setProperty("host.name.time", InetAddress.getLocalHost().getHostName() + "-" + DateHelper.toString(new Date(), "yyyyMMddHHmmss"));
            }else{
                System.setProperty("host.name.time", actionName + "-" + InetAddress.getLocalHost().getHostName() + "-" + DateHelper.toString(new Date(), "yyyyMMddHHmmss"));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        LOG = Logger.getLogger(BaseOper.class);
        ConfigManager.setFile("rob.xml");
        NetworkHelper.setDefaultProxy();
    }

    public BaseOper setArgs(String[] args){
        String[] myArgs = null;
        if (args.length == 0) {
            String defaultArgs = ConfigManager.getProperty("cli.paras");
            if (defaultArgs != null) {
                myArgs = defaultArgs.split(" ");
            }
        } else {
            myArgs = args;
        }
        optionParser.setArgs(myArgs);
        LOG.info("args: " + Arrays.toString(optionParser.getArgs()));
        return this;
    }

    public OptionParser getOptionParser(){
        return optionParser;
    }

    /**
     * @return true if execute.
     */
    public abstract boolean action();
}
