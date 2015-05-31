package chookin.stock.oper;

import cmri.utils.lang.OptionParser;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * Created by zhuyin on 1/25/15.
 */
public abstract class BaseOper {
    private final static Logger LOG = Logger.getLogger(BaseOper.class);
    private OptionParser optionParser = new OptionParser();

    public OptionParser getOptionParser() {
        return this.optionParser;
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
