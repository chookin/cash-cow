package chookin.house.service;

import cmri.utils.configuration.ConfigManager;
import cmri.utils.lang.BaseOper;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by zhuyin on 3/29/15.
 */
public class ConfigProcess extends BaseOper {
    private static Logger LOG= Logger.getLogger(ConfigProcess.class);

    @Override
    public boolean action() {
        try {
            return dumpConfigFile();
        } catch (IOException e) {
            LOG.error(null, e);
            return true;
        }
    }

    boolean dumpConfigFile() throws IOException {
        ConfigManager.dump();
        return true;
    }
}
