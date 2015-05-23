import chookin.stock.oper.StockCollect;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by chookin on 7/30/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext.xml"})
public class TestStock {
    @Autowired
    private StockCollect stockCollect;

    @Ignore
    public void saveStocks(){

    }
}