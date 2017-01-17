package chookin.secondhand.newsmth;

import chookin.secondhand.model.TargetObject;
import cmri.etl.pipeline.ConsolePipeline;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.spider.SpiderAdapter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by chookin on 16/12/16.
 */
public class ListPageProcessorTest {
    @Test
    public void process() throws Exception {
        TargetObject target = new TargetObject()
                .setCategory("二手电脑市场")
                .setUrl("http://newsmth.net/nForum/board/SecondComputer?ajax");
        new SpiderAdapter().addPipeline(new FilePipeline())
                .addPipeline(new ConsolePipeline())
                .addPipeline(resultItems -> Assert.assertFalse(resultItems.getTargetRequests().isEmpty()))
                .test(NewsmthGoodsCollector.ListPageProcessor.getRequest(target));
    }

}