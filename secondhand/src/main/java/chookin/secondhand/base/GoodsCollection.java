package chookin.secondhand.base;

import cmri.etl.common.Request;
import cmri.etl.job.Job;
import cmri.etl.job.SpiderJob;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.pipeline.MongoPipeline;
import cmri.etl.spider.Spider;
import cmri.etl.spider.SpiderAdapter;

import java.util.Collection;

/**
 * 二手货数据抓取基类
 * <p>
 * Created by chookin on 16/10/11.
 */
public abstract class GoodsCollection extends SpiderJob {
    public abstract String site();

    public abstract Collection<Request> getSeedRequests();

    @Override
    public Job prepare() {
        Spider spider = new SpiderAdapter(site() + " collection", options.options())
                .addRequest(getSeedRequests())
                .addPipeline(new FilePipeline(), new MongoPipeline(site()));
        addSpider(spider);
        return this;

    }
}
