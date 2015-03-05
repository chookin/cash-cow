package chookin.etl.common;

import chookin.utils.configuration.ConfigManager;
import junit.framework.TestCase;

/**
 * Created by zhuyin on 3/5/15.
 */
public class TestNetworkHelper extends TestCase {
    @Override
    protected void setUp(){
        ConfigManager.setFile("etl-commons.xml");
        NetworkHelper.setProxy();
    }

    public void testSwitchProxy(){
        NetworkHelper.switchProxy();
    }
}
