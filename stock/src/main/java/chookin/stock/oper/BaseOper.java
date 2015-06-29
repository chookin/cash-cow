package chookin.stock.oper;

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
 * Created by zhuyin on 1/25/15.
 */
public abstract class BaseOper {
    private static Logger LOG;
    private OptionParser optionParser = new OptionParser();

    static {
        // configure log4j to log to custom file at runtime. In the java program directly by setting a system property (BEFORE you
        LOG = Logger.getLogger(BaseOper.class);
        ConfigManager.setFile("stock.xml");
        NetworkHelper.setDefaultProxy();
    }

    public OptionParser getOptionParser() {
        return this.optionParser;
    }

    public Logger getLogger(){
        return LOG;
    }

    public BaseOper setArgs(String[] args) {
        optionParser.setArgs(args);
        LOG.info("args: " + Arrays.toString(optionParser.getArgs()));
        return this;
    }

    boolean processOption(String option) {
        if (optionParser.getOption(option) == null) {
            return false;
        }
        if (option.startsWith("--")) {
            String paras = optionParser.getOption(option);
            LOG.info(String.format("process option '%s=%s'", option, paras));
        } else {
            LOG.info(String.format("process option '%s'", option));
        }
        return true;
    }

    /**
     * check oper name, only match, then do action.
     */
    abstract boolean action();
}
