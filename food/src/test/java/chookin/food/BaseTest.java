package chookin.food;

import cmri.utils.web.NetworkHelper;
import org.junit.Before;

/**
 * Created by chookin on 16/3/8.
 */
public class BaseTest {
    @Before
    protected void setUp() {
        NetworkHelper.setDefaultProxy();
    }
}
