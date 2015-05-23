package chookin.stock.oper;

import chookin.stock.extractor.pipeline.CompanyPileline;
import chookin.stock.extractor.qq.CompanyPageProcessor;
import chookin.stock.orm.domain.CompanyInfoEntity;
import chookin.stock.orm.domain.StockEntity;
import chookin.stock.orm.repository.CompanyInfoRepository;
import cmri.etl.common.Request;
import cmri.etl.downloader.JsoupDownloader;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.scheduler.PriorityScheduler;
import cmri.etl.spider.Spider;
import cmri.utils.configuration.ConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * Created by zhuyin on 3/21/15.
 */
@Service
public class CompanyCollect extends Baseoper{
    @Autowired
    private CompanyPileline companyPileline;
    @Autowired
    private CompanyInfoRepository companyInfoRepository;

    public void extract() throws IOException {
        Map<String, StockEntity> stocks = this.getStocksMap();

        Spider spider = new Spider("company-info")
                .setScheduler(new PriorityScheduler())
                .setDownloader(new JsoupDownloader())
                .addPipeline(companyPileline)
                .addPipeline(new FilePipeline())
                .setSleepMillisecond(ConfigManager.getPropertyAsInteger("download.sleepMilliseconds"))
                .thread(ConfigManager.getPropertyAsInteger("download.concurrent.num"))
                .setTimeOut(ConfigManager.getPropertyAsInteger("download.timeout"))
                .setValidateSeconds(ConfigManager.getPropertyAsLong("page.validPeriod"))

        for(Map.Entry<String, StockEntity> entry : stocks.entrySet()){
            StockEntity stock = entry.getValue();
            CompanyInfoEntity company = this.companyInfoRepository.findOne(stock.getStockCode());
            if(company == null){
                company = new CompanyInfoEntity();
                company.setStockCode(stock.getStockCode());
            }
            spider.addRequest(new Request()
                            .setPageProcessor(new CompanyPageProcessor())
                            .setUrl(CompanyPageProcessor.getUrl(stock))
            ).addRequest(new Request()
                            .setPageProcessor(new chookin.stock.extractor.sina.CompanyPageProcessor())
                            .setUrl(chookin.stock.extractor.sina.CompanyPageProcessor.getUrl(stock))
            ).addRequest(new Request()
                            .setPageProcessor(new chookin.stock.extractor.eastmoney.CompanyPageProcessor())
                            .setUrl(chookin.stock.extractor.eastmoney.CompanyPageProcessor.getUrl(stock))
            );
        }
        spider.run();
    }
}
