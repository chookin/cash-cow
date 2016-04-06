package chookin.food.dianping;

import chookin.food.BaseTest;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.spider.SpiderAdapter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by chookin on 16/3/8.
 */
public class CategoriesPageProcessorTest extends BaseTest{
    @Test
    public void testProcess() throws Exception {
        new SpiderAdapter().addPipeline(new FilePipeline())
                .addPipeline(resultItems -> System.out.println(resultItems.getItems()))
                .addPipeline(resultItems -> Assert.assertFalse(resultItems.getItems().isEmpty()))
                .test(DianpingFoodCollection.CategoriesPageProcessor.getSeedRequests());
    }
}