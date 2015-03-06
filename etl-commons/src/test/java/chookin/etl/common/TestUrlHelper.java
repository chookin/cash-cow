package chookin.etl.common;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Created by zhuyin on 3/5/15.
 */
public class TestUrlHelper extends TestCase{
    private String simpleUrl;
    @Override
    protected void setUp() {
        simpleUrl = "http://www.126.com";
    }
    public void testEraseProtocol(){
        String dst = UrlHelper.eraseProtocol(simpleUrl);
        Assert.assertEquals("www.126.com", dst);
    }

    public void testEraseProtocolAndStart3W(){
        String dst = UrlHelper.eraseProtocolAndStart3W(simpleUrl);
        Assert.assertEquals("126.com", dst);
    }

    public void testGetProtocol(){
        Assert.assertEquals("http", UrlHelper.getProtocol(simpleUrl));
    }
}
