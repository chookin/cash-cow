package chookin.food.dianping;

import cmri.etl.common.MapEntity;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.spider.SpiderAdapter;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by chookin on 16/3/12.
 */
public class ReviewsPageProcessorTest {

    @Test
    public void testProcess() throws Exception {
        MapEntity item = new MapEntity();
        item.put("code", 512899)
                .put("name", "郭林家常菜(丰台店)");
        new SpiderAdapter().addPipeline(new FilePipeline())
                .addPipeline(resultItems -> System.out.println(resultItems.getItems()))
                .addPipeline(resultItems -> Assert.assertFalse(resultItems.getItems().isEmpty()))
                .test(DianpingFoodCollection.ReviewsPageProcessor.getRequest(item));
    }

    @Test
    public void testGetNextPageUrl() throws Exception {

    }
}