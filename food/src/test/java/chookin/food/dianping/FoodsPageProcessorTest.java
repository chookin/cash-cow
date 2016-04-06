package chookin.food.dianping;

import chookin.food.BaseTest;
import cmri.etl.downloader.JsoupDownloader;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.spider.SpiderAdapter;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by chookin on 16/3/8.
 */
public class FoodsPageProcessorTest extends BaseTest{
    String beijingCuisineUrl = "http://www.dianping.com/search/category/2/10/g311";

    @Test
    public void testProcess() throws Exception {
        new SpiderAdapter().addPipeline(new FilePipeline())
                .addPipeline(resultItems -> System.out.println(resultItems.getItems()))
                .addPipeline(resultItems -> Assert.assertFalse(resultItems.getItems().isEmpty()))
                .test(DianpingFoodCollection.FoodsPageProcessor.getRequest(beijingCuisineUrl, "北京菜"));
    }

    @Test
    public void testGetNextPageUrl() throws Exception {
        Document doc = JsoupDownloader.getInstance().getDocument(beijingCuisineUrl);
        String nextUrl = new DianpingFoodCollection.FoodsPageProcessor().getNextPageUrl(doc);
        Assert.assertFalse(nextUrl.isEmpty());
    }
}