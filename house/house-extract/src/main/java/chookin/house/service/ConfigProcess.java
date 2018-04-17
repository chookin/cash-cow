package chookin.house.service;

import cmri.utils.lang.BaseOper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by zhuyin on 3/29/15.
 */
public class ConfigProcess extends BaseOper {
    private static Logger LOG= LoggerFactory.getLogger(ConfigProcess.class);

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
        return true;
    }
}
