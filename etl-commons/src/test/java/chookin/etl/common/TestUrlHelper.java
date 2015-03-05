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
        Assert.assertEquals(dst, "www.126.com");
    }

    public void testEraseProtocolAndStart3W(){
        String dst = UrlHelper.eraseProtocolAndStart3W(simpleUrl);
        Assert.assertEquals(dst, "126.com");
    }

    public void testGetProtocol(){
        Assert.assertEquals(UrlHelper.getProtocol(simpleUrl), "http");
    }
}
