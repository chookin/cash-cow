import chookin.stock.orm.service.ZStock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * Created by chookin on 7/30/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext.xml"})
public class TestStock {
    @Autowired
    private ZStock zStock;

    @Ignore
    public void saveStocks(){
        try {
            this.zStock.saveStocks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    public void saveCompanyInfo(){
        try {
            this.zStock.saveCompanyInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    public void saveRealData(){
        try {
            this.zStock.saveRealData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Before
    public void setProxy(){
        String host = "proxy.cmcc";
        int port = 8080;
        String authUser = "";
        String authPassword = "";
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", Integer.toString(port));
        System.setProperty("http.proxyUser", authUser);
        System.setProperty("http.proxyPassword", authPassword);

    }
    @Test
    public void saveHistoryData(){
        try {
            for(int year = 2000;year <= 2003;++year){
                for(int quarter = 1;quarter<=4;++quarter){
                    this.zStock.saveHistoryData(year, quarter);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}