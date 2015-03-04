package chookin.stock.action;

import chookin.utils.OptionParser;
import org.apache.log4j.Logger;

/**
 * Created by zhuyin on 1/25/15.
 */
public class BaseAction {
    private final static Logger LOG = Logger.getLogger(BaseAction.class);
    private OptionParser optionParser;
    public BaseAction setArgs(String[] args){
        optionParser = new OptionParser(args);
        return this;
    }

    String getOption(String option){
        return optionParser.getOption(option);
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
